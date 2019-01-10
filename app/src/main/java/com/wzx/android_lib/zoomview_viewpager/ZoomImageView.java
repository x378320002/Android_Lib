package com.wzx.android_lib.zoomview_viewpager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import com.wzx.android_lib.R;

/**
 * 剪裁用的imageview
 */
public class ZoomImageView extends AppCompatImageView {
    private float mLastX;
    private float mLastY;
    private Matrix mMatrix = new Matrix();

    /**
     * 初始填充的缩放(当前的缩放), 和允许最小的最大的缩放系数, 和双击图片时来回切换的系数
     */
    private float mCurrentScale, mZoomMinScale, mZommMaxScale, mDoubleClickMinScale, mDoubleClickMaxScale;

    //原始bitmap的宽高，当前bitmap的宽高, 本imageview的宽高，
    private float mOriBmpWidth, mOriBmpHeight, mCurrentBmpWidth, mCurrentBmpHeight, mWidth, mHeight;
    //当前bitmap的左上角点, matrix会根据这个坐标点移动bitmap
    private float mMatrixX, mMatrixY;

    private float mOldDistant;
    private float mCurrentDistant;
    public static final int NONE = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;
    public static final int ZOOM_ANIM = 3;
    private PointF mCenter = new PointF();

    private float[] mTmpArray = new float[9];
    private static TimeInterpolator sInterpolator = new DecelerateInterpolator();
    private AnimatorUpdateListener zoomBacklistener = null;

    //x，y方向的剪裁框内的冗余量，当前图片的宽高减去剪裁框的宽高，负数说明图片小于于剪裁框
    private float mRedundantXSpace, mRedundantYSpace;
    private int mMode;
    private boolean mAlreadyLoadBigBmp = false;
    private int mPointCount;
    private Bitmap mBitmap = null;
    private RectF mClipRect; //剪裁框, 也是图片最小框, 图片边缘不能进入这个框内
    private RectF mEdgeRect; //图片边缘框/Imageview本身的边界
    private int mFirstFillMode = 2; //初始时, 图片显示填充的模式 0:以剪裁框centercrop的模式, 1:以边缘框centercrop的模式, 2以边缘框fitcenter的模式
    private boolean mHasClipRect;
    /**
     * 判断用户是否开始滑动的，手指防抖裕量
     */
    private int mTouchSlop;
    private boolean mIsFirstScale; //由静止到开始双指缩放
    private boolean mIsZoomAniming = false; //是否正在进行缩放回弹的动画中
    private boolean mIsOnLeftSide = false, mIsOnRightSide = false, mIsOnTopSide = false, mIsOnBottomSide = false;
    private int mClipLeft, mClipRight, mClipTop, mClipBottom; //剪裁框距离左右上下边的距离
    private float[] mConsumeXY; //当拖动图片移动是, 图片x,y方向消耗的当次移动距离
    private boolean mCanRequestParentDisllowIntercept = true;

    //点击相关begin
    private float mDownX;
    private float mDownY;
    private long mDownTime;
    private int mDoubleClickDuration = 200; //接受doubleClik点击事件行程的间隔, 大约这个时间不形成双击点击事件
    private boolean mCanClick;
    private boolean mDoubleClickZoom; //是否支持双击放大快速切换, 如果支持双击, 那么单击事件就肯定有延迟
    private OnClickListener mOnClickListener;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        setScaleType(ScaleType.MATRIX);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClipZoomImageView);
        try {
            mHasClipRect = a.getBoolean(R.styleable.ClipZoomImageView_hasclip, false);
            if (mHasClipRect) {
                mClipLeft = a.getDimensionPixelSize(R.styleable.ClipZoomImageView_clipleft, 0);
                mClipTop = a.getDimensionPixelSize(R.styleable.ClipZoomImageView_cliptop, 0);
                mClipRight = a.getDimensionPixelSize(R.styleable.ClipZoomImageView_clipright, 0);
                mClipBottom = a.getDimensionPixelSize(R.styleable.ClipZoomImageView_clipbottom, 0);
            } else {
                mClipLeft = mClipTop = mClipRight = mClipBottom = 0;
            }

            mFirstFillMode = a.getInt(R.styleable.ClipZoomImageView_scalemode, 2);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 初始时, 图片显示填充的模式 0:以剪裁框centercrop的模式, 1:以边缘框centercrop的模式, 2以边缘框fitcenter的模式
     *
     * @param mode
     */
    public void setFirstFillMode(int mode) {
        mFirstFillMode = mode;
        calcBitmapWH();
    }

    /**
     * 设置剪切框距离左上右下的距离
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setClipRect(int left, int top, int right, int bottom) {
        mHasClipRect = true;
        mClipLeft = left;
        mClipTop = top;
        mClipRight = right;
        mClipBottom = bottom;

        calcBitmapWH();
    }

    /**
     * 自己实现初始时图片怎么显示，这里是用matrix实现的center_crop的效果，
     * 而且可以实现以任意的框来center_crop, 系统的scaleType除了fitXy，其他
     * 的也是这样用matrix变换的，参考系统源码，imageview中的configureBounds()方法
     */
    private void calcBitmapWH() {
        if (mBitmap != null) {
            mOriBmpWidth = mBitmap.getWidth();
            mOriBmpHeight = mBitmap.getHeight();
        } else {
            return;
        }

        if (mWidth == 0 || mHeight == 0 || mOriBmpWidth == 0 || mOriBmpHeight == 0) {
            return;
        }

        mClipRect = new RectF(mClipLeft, mClipTop, mWidth - mClipRight, mHeight - mClipBottom);
        mEdgeRect = new RectF(0, 0, mWidth, mHeight);

//        Log.d("wangzixu", "calcBitmapWH mClipRect = " + mClipRect + ", mEdgeRect = " + mEdgeRect);

        //模拟系统的形式来处理图片，参考系统源码，imageview中的configureBounds()方法
        if (mFirstFillMode == 0) { //以剪裁框centercrop的模式
            float scaleX = mClipRect.width() / mOriBmpWidth;
            float scaleY = mClipRect.height() / mOriBmpHeight;
            float scale = Math.max(scaleX, scaleY);

            //初始化一些信息, 主要是初始化三个缩放系数, 后续计算都是根据这些缩放系数计算的
            mCurrentScale = scale;
            mZoomMinScale = scale;
            mZommMaxScale = mCurrentScale * 4;
            mDoubleClickMinScale = mZoomMinScale;
            mDoubleClickMaxScale = mZommMaxScale;
            if (mDoubleClickMaxScale <= mDoubleClickMinScale + 0.1) {
                mDoubleClickMaxScale = mZommMaxScale;
            }

            mCurrentBmpWidth = scale * mOriBmpWidth;
            mCurrentBmpHeight = scale * mOriBmpHeight;
            float Xoffset = mEdgeRect.width() - mCurrentBmpWidth;
            float Yoffset = mEdgeRect.height() - mCurrentBmpHeight;

            mMatrixX = mEdgeRect.left + Xoffset * 0.5f;
            mMatrixY = mEdgeRect.top + Yoffset * 0.5f;
            //Log.d("wangzixu", "clipimg calcBitmapWH called mClipRect = " + mClipRect.toString());
            //Log.d("wangzixu", "clipimg calcBitmapWH called mMatrixX = " + mMatrixX + ", mMatrixY = " + mMatrixY);

            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate(Math.round(mMatrixX), Math.round(mMatrixY));
            setImageMatrix(mMatrix);

            mRedundantXSpace = mCurrentBmpWidth - mClipRect.width();
            mRedundantYSpace = mCurrentBmpHeight - mClipRect.height();
        } else if (mFirstFillMode == 1) { //1:以边缘框centercrop的模式
            float scaleX = mEdgeRect.width() / mOriBmpWidth;
            float scaleY = mEdgeRect.height() / mOriBmpHeight;
            float scale = Math.max(scaleX, scaleY);

            //初始化一些信息, 主要是初始化三个缩放系数, 后续计算都是根据这些缩放系数计算的
            mCurrentScale = scale;
            if (mHasClipRect) { //有剪裁框
                float clipScale = Math.max(mClipRect.width() / mOriBmpWidth, mClipRect.height() / mOriBmpHeight);
                mZoomMinScale = clipScale; //最小的缩放系数不能进剪裁框
            } else {
                mZoomMinScale = Math.min(scaleX, scaleY);
            }
            mZommMaxScale = mCurrentScale * 4;
            mDoubleClickMinScale = mZoomMinScale;
            mDoubleClickMaxScale = mCurrentScale;
            if (mDoubleClickMaxScale <= mDoubleClickMinScale + 0.1) {
                mDoubleClickMaxScale = mZommMaxScale;
            }

            mCurrentBmpWidth = scale * mOriBmpWidth;
            mCurrentBmpHeight = scale * mOriBmpHeight;
            float Xoffset = mEdgeRect.width() - mCurrentBmpWidth;
            float Yoffset = mEdgeRect.height() - mCurrentBmpHeight;

            mMatrixX = mEdgeRect.left + Xoffset * 0.5f;
            mMatrixY = mEdgeRect.top + Yoffset * 0.5f;
//            Log.d("wangzixu", "calcBitmapWH  mMatrixX = " + mMatrixX + ", mMatrixY = " + mMatrixY + ", scale = " + scale);

            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate(Math.round(mMatrixX), Math.round(mMatrixY));

            setImageMatrix(mMatrix);

            mRedundantXSpace = mCurrentBmpWidth - mClipRect.width();
            mRedundantYSpace = mCurrentBmpHeight - mClipRect.height();
        } else if (mFirstFillMode == 2) {//2以边缘框fitcenter的模式
            //留白的填充方式, 图片要在这个方框内显示, 初始显示成留白的方式, 这种显示方式, 初始时图片可能会小于允许的最小系数, 所以需要纠正
            //最大显示成窄边充满的方式, 也是最大的缩放系数, 再大也要返回, 有时候还要双击切换着两种显示形态
            float scaleX = mEdgeRect.width() / mOriBmpWidth;
            float scaleY = mEdgeRect.height() / mOriBmpHeight;
            float scale = Math.min(scaleX, scaleY);
            Log.d("wangzixu", "ZoomImg calcBitmapWH mOriBmpWidth = " + mOriBmpWidth
                    + ", mOriBmpHeight = " + mOriBmpHeight + ", scaleX = " + scaleX + ", scaleY = " + scaleY);

            if (mHasClipRect) {//允许的最小缩放系数, 有剪裁框时, 保证图片边缘不能进入剪裁框
                float clipScale = Math.max(mClipRect.width() / mOriBmpWidth, mClipRect.height() / mOriBmpHeight);
                if (scale < clipScale) {
                    scale = clipScale;
                }
            }

            //初始化一些信息, 主要是初始化三个缩放系数, 后续计算都是根据这些缩放系数计算的
            mCurrentScale = scale;
            mZoomMinScale = scale;
            mZommMaxScale = Math.max(scaleX, scaleY) * 4;
            mDoubleClickMinScale = mZoomMinScale;
            mDoubleClickMaxScale = Math.max(scaleX, scaleY);
            if (mDoubleClickMaxScale <= mDoubleClickMinScale + 0.1) {
                mDoubleClickMaxScale = mZommMaxScale;
            }

            mCurrentBmpWidth = scale * mOriBmpWidth;
            mCurrentBmpHeight = scale * mOriBmpHeight;
            float Xoffset = mEdgeRect.width() - mCurrentBmpWidth;
            float Yoffset = mEdgeRect.height() - mCurrentBmpHeight;

            mMatrixX = mEdgeRect.left + Xoffset * 0.5f;
            mMatrixY = mEdgeRect.top + Yoffset * 0.5f;
            //Log.d("wangzixu", "clipimg calcBitmapWH called mClipRect = " + mClipRect.toString());
            //Log.d("wangzixu", "clipimg calcBitmapWH called mMatrixX = " + mMatrixX + ", mMatrixY = " + mMatrixY);

            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate(Math.round(mMatrixX), Math.round(mMatrixY));
            setImageMatrix(mMatrix);

            mRedundantXSpace = mCurrentBmpWidth - mClipRect.width();
            mRedundantYSpace = mCurrentBmpHeight - mClipRect.height();
        }

        checkSide();
    }

    /**
     * 初始化的时候带上了图片的位移等信息, 图片中的bitmap位移和缩放大小
     */
    public void setClipInitInfo(float offsetX, float offsetY, float clipScale) {
        mCurrentScale = clipScale;

        mCurrentBmpWidth = mCurrentScale * mOriBmpWidth;
        mCurrentBmpHeight = mCurrentScale * mOriBmpHeight;

        mMatrixX = offsetX;
        mMatrixY = offsetY;
        //Log.d("wangzixu", "clipimg calcBitmapWH called mMatrixX = " + mMatrixX + ", mMatrixY = " + mMatrixY);

        mMatrix.setScale(mCurrentScale, mCurrentScale);
        mMatrix.postTranslate(Math.round(mMatrixX), Math.round(mMatrixY));

        mRedundantXSpace = mCurrentBmpWidth - mClipRect.width();
        mRedundantYSpace = mCurrentBmpHeight - mClipRect.height();

        setImageMatrix(mMatrix);
    }

    private void setCenter(MotionEvent event) {
        try {
            int pointerCount = event.getPointerCount();
            if (pointerCount == 1) {
                mCenter.set(event.getX(), event.getY());
            } else {
                float x = (event.getX(1) + event.getX(0)) * 0.5f;
                float y = (event.getY(1) + event.getY(0)) * 0.5f;
                mCenter.set(x, y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("wangzixu", "clipimageview onSizeChanged w = " + w + ", h = " + h);
        mWidth = getWidth();
        mHeight = getHeight();
        if (mWidth != 0 && mHeight != 0) {
            calcBitmapWH();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
//        Log.d("wangzixu", "ZoomImg calcBitmapWH setImageBitmap bm = " + bm.getWidth() + ", " + bm.getHeight());
        mBitmap = bm;
        calcBitmapWH();
    }

    //setImageBitmap 内部实现会调用到此方法
//    @Override
//    public void setImageDrawable(Drawable drawable) {
//        super.setImageDrawable(drawable);
//        Log.d("wangzixu", "clipimageview setImageDrawable");
////        mBitmap = getBitmapFromDrawable(drawable);
////        setup();
//    }

//    @Override
//    public void setImageResource(@DrawableRes int resId) {
//        super.setImageResource(resId);
//        mBitmap = getBitmapFromDrawable(getDrawable());
//        setup();
//    }
//
//    @Override
//    public void setImageURI(Uri uri) {
//        super.setImageURI(uri);
//        mBitmap = uri != null ? getBitmapFromDrawable(getDrawable()) : null;
//        setup();
//    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void requestParentDisallowIntercept(boolean disallow) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallow);
        }
    }

    private void initConsumeXy() {
        if (mConsumeXY == null) {
            mConsumeXY = new float[2];
        } else {
            mConsumeXY[0] = 0f;
            mConsumeXY[1] = 0f;
        }
    }

    public float[] getConsumeXy() {
        return mConsumeXY;
    }

    public boolean setCanRequestParentDisllowIntercept(boolean can) {
        return mCanRequestParentDisllowIntercept = can;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float[] touchEvent = handleTouchEvent(event);
        Log.d("wangzixu", "Zoom onTouchEvent mMode = " + mMode + ", touchEvent = " + touchEvent[0] + ", " + touchEvent[1]);

        if (mCanRequestParentDisllowIntercept
                && (mMode == ZOOM || mMode == ZOOM_ANIM || touchEvent[0] != 0 || touchEvent[1] != 0)) {
            requestParentDisallowIntercept(true);
        }
//        else if (mMode == DRAG) { //说明是拖动状态, 并且无距离消耗, 说明到达了某个边界
//            requestParentDisallowIntercept(false);
//        }
        return true;
    }

    /**
     * 返回x,y方向上消耗的距离
     *
     * @param event
     * @return
     */
    public float[] handleTouchEvent(MotionEvent event) {
        initConsumeXy();
        if (mIsZoomAniming) {
            return mConsumeXY;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mMode = NONE;
                mPointCount = 1;
                mDownX = mLastX = event.getX(0);
                mDownY = mLastY = event.getY(0);
                mCanClick = true;
                mDownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mPointCount++;
                mCanClick = false;
                if (mPointCount > 2 || mMode == ZOOM_ANIM) {
                    break;
                }

                mOldDistant = getDistance(event);

                if (mOldDistant >= mTouchSlop) {
                    mMode = ZOOM;
                    mIsFirstScale = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPointCount--;
                if (mPointCount > 1) {
                    break;
                }
                mMode = NONE;
                if ((mCurrentScale < mZoomMinScale || mCurrentScale > mZommMaxScale)
                        && !mIsZoomAniming) {
                    mMode = ZOOM_ANIM;
                    startScaleAnim(mCurrentScale, mZoomMinScale);
                } else {
                    if (mRedundantXSpace > 0 || mRedundantYSpace > 0) {
                        mMode = DRAG;
                    }
                    if (mMode == DRAG) {
                        int pointerIndex = event.getActionIndex();
                        mLastX = event.getX(1 - pointerIndex);
                        mLastY = event.getY(1 - pointerIndex);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == NONE && (mRedundantXSpace > 0 || mRedundantYSpace > 0)) { //事件还未确定, 并且可以支持拖动
                    float currentX = event.getX(0);
                    float currentY = event.getY(0);
                    float deltaX = currentX - mLastX;
                    float deltaY = currentY - mLastY;

                    if (Math.abs(deltaX) >= mTouchSlop || Math.abs(deltaY) >= mTouchSlop) { //进入拖动状态
                        mCanClick = false; //手指动了, 就不能相应点击事件了

                        if ((!mIsOnLeftSide && deltaX > mTouchSlop)
                                || (!mIsOnRightSide && deltaX < -mTouchSlop)
                                || (!mIsOnTopSide && deltaY > mTouchSlop)
                                || (!mIsOnBottomSide && deltaY < -mTouchSlop)) {
                            mMode = DRAG;
                            mLastX = currentX;
                            mLastY = currentY;
                            mConsumeXY[0] = (int) deltaX;
                            mConsumeXY[1] = (int) deltaY;
                        }
                    }
                } else if (mMode == DRAG) {
                    mCanClick = false;
                    float currentX = event.getX(0);
                    float currentY = event.getY(0);
                    float deltaX = currentX - mLastX;
                    float deltaY = currentY - mLastY;
                    Log.d("wangzixu", "Zoom onTouchEvent mMode DRAG currentX = " + currentX + ", mLastX = " + mLastX);

                    mLastX = currentX;
                    mLastY = currentY;
                    Log.d("wangzixu", "Zoom onTouchEvent mMode DRAG deltaX = " + deltaX + ", deltaY = " + deltaY);
                    mConsumeXY = checkAndSetTranslate(deltaX, deltaY);
                } else if (mMode == ZOOM) {
                    mCurrentDistant = getDistance(event);
                    if (mIsFirstScale || mOldDistant == 0) { //初次开始动，总有个突兀的跳变，所以消除掉第一次，防抖
                        mOldDistant = mCurrentDistant;
                        mIsFirstScale = false;
                        break;
                    }
                    float scaleFactor = mCurrentDistant / mOldDistant;
                    float deltaScale = Math.abs(scaleFactor - 1.0f);
                    if (deltaScale < 0.0001) {
                        break;
                    }
                    mOldDistant = mCurrentDistant;

                    if (scaleFactor > 1.03f) {
                        scaleFactor = 1.03f;
                    } else if (scaleFactor < 0.97f) {
                        scaleFactor = 0.97f;
                    }
                    setCenter(event);
                    zoomImg(scaleFactor);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCanClick) {
                    float deltaX = Math.abs(event.getX() - mDownX);
                    float deltaY = Math.abs(event.getY() - mDownY);
                    if (deltaX < mTouchSlop && deltaY < mTouchSlop) {
                        if (mDoubleClickZoom) {
                            if (mClickRunnableReady) { //说明已经有一个点击了, 再点进来就是双击了
                                mClickRunnableReady = false;
                                removeCallbacks(mClickRunable);
                                onDoubleClickZoom(event.getX(), event.getY());
                            } else {
                                removeCallbacks(mClickRunable);
                                postDelayed(mClickRunable, mDoubleClickDuration);
                                mClickRunnableReady = true;
                            }
                        } else {
                            if (mOnClickListener != null) {
                                mOnClickListener.onClick(this);
                            }
                        }
                    }
                }
                initConsumeXy();
                mMode = NONE;
                mPointCount = 0;
                break;
        }
        return mConsumeXY;
    }

    private void startScaleAnim(float start, float end) {
        if (mIsZoomAniming) {
            return;
        }
        mIsZoomAniming = true;
        ValueAnimator anim = ValueAnimator.ofFloat(start, end);
        anim.setDuration(250);
        anim.setInterpolator(sInterpolator);
        if (zoomBacklistener == null) {
            zoomBacklistener = new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (Float) animation.getAnimatedValue();
                    float scaleFactor = f / mCurrentScale;
                    zoomImg(scaleFactor);
                }
            };
        }
        anim.addUpdateListener(zoomBacklistener);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                checkAndSetTranslate(1, 1); //是缩放动画完成后图片边缘不进入最小剪切框或者边缘狂, 纠正一下
                mIsZoomAniming = false;
                mMode = NONE;
            }
        });
        anim.start();
    }

    private void zoomImg(float scaleFactor) {
        if (scaleFactor == 1.0f) {
            return;
        }

        mCurrentScale *= scaleFactor;

        float px, py; //缩放的中心点，如果图片宽小于边框宽了，中点就是边框的中心，否则就是双指的中心
        px = mRedundantXSpace <= 0 ? mClipRect.centerX() : mCenter.x;
        py = mRedundantYSpace <= 0 ? mClipRect.centerY() : mCenter.y;

        mMatrix.postScale(scaleFactor, scaleFactor, px, py);
        getMatrixXY(mMatrix);

        calcRedundantSpace();

        //X方向有裕量的情况下，图片边缘不能进入最小框
        float dx = 0, dy = 0;
        if (mRedundantXSpace >= 0) {
            if (mMatrixX > mClipRect.left) {
                dx = mClipRect.left - mMatrixX;
                //mMatrix.postTranslate(mClipRect.left - mMatrixX, 0);
            } else if (mMatrixX + mCurrentBmpWidth < mClipRect.right) {
                dx = mClipRect.right - mCurrentBmpWidth - mMatrixX;
                //mMatrix.postTranslate(mClipRect.right - mCurrentBmpWidth - mMatrixX, 0);
            }
        }

        if (mRedundantYSpace >= 0) {
            if (mMatrixY > mClipRect.top) {
                dy = mClipRect.top - mMatrixY;
                //mMatrix.postTranslate(0, mClipRect.top - mMatrixY);
            } else if (mMatrixY + mCurrentBmpHeight < mClipRect.bottom) {
                dy = mClipRect.bottom - mCurrentBmpHeight - mMatrixY;
                //mMatrix.postTranslate(0, mClipRect.bottom - mCurrentBmpHeight - mMatrixY);
            }
        }

        mMatrix.postTranslate(dx, dy);
        setImageMatrix(mMatrix);
        checkSide();
    }

    private float getDistance(MotionEvent event) {
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        return (float) Math.sqrt((x * x + y * y));
    }

    public boolean isAlreadyLoadBigBmp() {
        return mAlreadyLoadBigBmp;
    }

    public void setAlreadyLoadBigBmp(boolean alreadyLoadBigBmp) {
        mAlreadyLoadBigBmp = alreadyLoadBigBmp;
    }

    /**
     * 因为图片边缘不能进入最小框，所以需要知道最小框和图片宽高之间的差值，
     * 即x，y方向的冗余量，负数说明图片小于最小框
     */
    private void calcRedundantSpace() {
        mCurrentBmpWidth = mOriBmpWidth * mCurrentScale;
        mCurrentBmpHeight = mOriBmpHeight * mCurrentScale;

        mRedundantXSpace = mCurrentBmpWidth - mClipRect.width();
        mRedundantYSpace = mCurrentBmpHeight - mClipRect.height();
    }

    /**
     * 确定matrix的左上角点
     */
    private void getMatrixXY(Matrix m) {
        m.getValues(mTmpArray);
        mMatrixX = mTmpArray[Matrix.MTRANS_X];
        mMatrixY = mTmpArray[Matrix.MTRANS_Y];
    }

    /**
     * 返回x和y方向上的实际消耗的值
     *
     * @param deltaX
     * @param deltaY
     * @return
     */
    private float[] checkAndSetTranslate(float deltaX, float deltaY) {
        if (mRedundantXSpace <= 0) {
            deltaX = 0;
        } else {
            if (mMatrixX + deltaX > mClipRect.left) { //移动完后图片就进入最小框左边缘了，需要处理
                deltaX = mClipRect.left - mMatrixX;
            } else if (mMatrixX + deltaX + mCurrentBmpWidth < mClipRect.right) {
                deltaX = mClipRect.right - mCurrentBmpWidth - mMatrixX;
            }
        }

        if (mRedundantYSpace <= 0) {
            deltaY = 0;
        } else {
            if (mMatrixY + deltaY > mClipRect.top) {
                deltaY = mClipRect.top - mMatrixY;
            } else if (mMatrixY + deltaY + mCurrentBmpHeight < mClipRect.bottom) {
                deltaY = mClipRect.bottom - mCurrentBmpHeight - mMatrixY;
            }
        }

        if (deltaX != 0 || deltaY != 0) {
            mMatrix.postTranslate(deltaX, deltaY);
            setImageMatrix(mMatrix);
            checkSide();
        }
        return new float[]{deltaX, deltaY};
    }

    private void checkSide() {
        getMatrixXY(mMatrix);

        mIsOnLeftSide = false;
        mIsOnRightSide = false;
        mIsOnTopSide = false;
        mIsOnBottomSide = false;
        if (mMatrixX >= mClipRect.left) {
            mIsOnLeftSide = true;
        }

        if (mMatrixX + mCurrentBmpWidth <= mClipRect.right) {
            mIsOnRightSide = true;
        }

        if (mMatrixY >= mClipRect.top) {
            mIsOnTopSide = true;
        }

        if (mMatrixY + mCurrentBmpHeight <= mClipRect.bottom) {
            mIsOnBottomSide = true;
        }
    }

    public int getTouchMode() {
        return mMode;
    }

    public boolean canHorizontalDrag() {
        return mCurrentScale >= mZoomMinScale && mRedundantXSpace > 0;
    }

    public boolean canVerticalDrag() {
        return mCurrentScale >= mZoomMinScale && mRedundantYSpace > 0;
    }

    @Override
    public Matrix getMatrix() {
        return mMatrix;
    }

    public RectF getClipRect() {
        return mClipRect;
    }

    public Bitmap getCurrentBmp() {
        return mBitmap;
    }

    public void setDoubleClickZoom(boolean can) {
        mDoubleClickZoom = can;
    }
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mOnClickListener = l;
    }
    //点击相关end

    //双击相关begin
    private volatile boolean mClickRunnableReady;
    private Runnable mClickRunable = new Runnable() {
        @Override
        public void run() {
            mClickRunnableReady = false;
            if (mOnClickListener != null) {
                mOnClickListener.onClick(ZoomImageView.this);
            }
        }
    };
    public void onDoubleClickZoom(float centerX, float centerY) {
        mCenter.set(centerX, centerY);
        if (mCurrentScale <= mDoubleClickMinScale) {
            startScaleAnim(mCurrentScale, mDoubleClickMaxScale);
        } else {
            startScaleAnim(mCurrentScale, mDoubleClickMinScale);
        }
    }
    //双击相关end

    //***为了模拟ios的抬手后自动滚动一段距离而实现begin
//    MyScroller mScroller;
//
//    public void flingDistence(int dx, int dy) {
//        if (mScroller == null) {
//            mScroller = new MyScroller(getContext());
//        }
////		mScroller.setDuration(Math.abs(dx) * 3);
//        mScroller.startScroll(0, 0, dx, dy);
//        mLastX = 0;
//        mLastY = 0;
//        invalidate();
//    }
//
//    public void stopFling() {
//        if (mScroller != null) {
//            mScroller.forceFinished(true);
//        }
//    }
//
//    @Override
//    public void computeScroll() {
//        if (mScroller == null) {
//            return;
//        }
//        if (mScroller.computeScrollOffset()) {
//            float currentX = mScroller.getCurrentX();
//            float currentY = mScroller.getCurrentY();
//            float deltaX = currentX - mLastX;
//            float deltaY = currentY - mLastY;
//            mLastX = currentX;
//            mLastY = currentY;
//
//            Log.d("zoomview", "currentX, deltaX = " + currentX + ", " + deltaX);
//            checkAndSetTranslate(deltaX, deltaY);
//        }
//    }
    //***为了模拟ios的抬手后自动滚动一段距离而实现end
}
