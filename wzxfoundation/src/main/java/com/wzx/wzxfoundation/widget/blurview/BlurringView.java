package com.wzx.wzxfoundation.widget.blurview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangzixu on 2018/6/14.
 * 动态高斯模糊的view, 盖在一个view上, 用来实时模糊其背后的view
 * 适合模糊滑动的view, 如放在recycleview上面, 滑动时就产生实时模糊的效果
 * 典型用法如下:
 *          view->监听draw->blurview.invalidate->blurview.draw->view.draw
 *          以上流程会形成draw循环, 需要判断当前帧是否模糊过了, 同一个draw只能模糊一次
 *
 *         mBlurringView = findViewById(R.id.blurview);
 *         View view = findViewById(R.id.fragment_container); //需要被模糊的view
 *         mBlurringView.setBlurredView(view);
 *
 *         view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
 *             @Override
 *             public void onDraw() {
 * //                LogHelper.d("wangzixu", "BlurringView getViewTreeObserver ----onDraw");
 *                 if (mBlurringView.isBlurDrawed()) { //因为会形成draw调用循环, 所以模糊前需要判断, 同一个draw只能模糊一次
 *                     mBlurringView.setBlurDrawed(false);
 *                     return;
 *                 }
 *
 *                 if (!mChildHide) {
 *                      //调用此方法使mBlurringView主动draw, 便会模糊, 模糊的原理是调用view的draw
 *                     mBlurringView.invalidate();
 *                 }
 *             }
 *         });
 */
public class BlurringView extends View {
    private int mBlurRadius;
    private int mDownsampleFactor;
    private int mOverlayColor;
    //高斯view的draw是被被模糊view的draw调用的, 高斯view的draw中又会调用此被模糊的draw, 为了防止递归循环调用,需要检测是否被调用过了, 如果调用过了, 设置为false, 时下次draw再调用
    private boolean mBlurDrawed;
    private View mBlurredView;
    private int mBlurredViewWidth, mBlurredViewHeight;

    private boolean mDownsampleFactorChanged;
    private Bitmap mBitmapToBlur; //等在被模糊的bitmap
    private Bitmap mBlurredBitmap; //模糊完的bitmap
    private Canvas mBlurringCanvas;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput, mBlurOutput;

    public BlurringView(Context context) {
        this(context, null);
    }

    public BlurringView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBlurRadius = 10;
        mDownsampleFactor = 10;
        mOverlayColor = 0xAAFFFFFF;
        mDownsampleFactorChanged = true;

        initializeRenderScript(context);
    }

    private void initializeRenderScript(Context context) {
        mRenderScript = RenderScript.create(context);
        mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
        mBlurScript.setRadius(mBlurRadius);
    }

    public void setOverlayColor(int color) {
        mOverlayColor = color;
    }

    public boolean isBlurDrawed() {
        return mBlurDrawed;
    }

    public void setBlurDrawed(boolean blurDrawed) {
        mBlurDrawed = blurDrawed;
    }

    /**
     * @param blurRadius
     * @param downsampleFactor 模糊系数请劲量设置成能被1整除开的数, 如5, 10, 20, 25等
     */
    public void setRadiusAndFactor(int blurRadius, int downsampleFactor) {
        mBlurRadius = blurRadius;
        mBlurScript.setRadius(mBlurRadius);

        if (downsampleFactor <= 0) {
            throw new IllegalArgumentException("Downsample factor must be greater than 0.");
        }

        if (mDownsampleFactor != downsampleFactor) {
            mDownsampleFactor = downsampleFactor;
            mDownsampleFactorChanged = true;
        }
    }

    public void setBlurredView(View blurredView) {
        mBlurredView = blurredView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mBlurDrawed && mBlurredView != null) {
            if (prepare()) {
                if (mOnDrawBlurListener != null) {
                    mOnDrawBlurListener.onPreBlurDraw();
                }
                mBlurDrawed = true;
                // If the background of the blurred view is a color drawable, we use it to clear
                // the blurring canvas, which ensures that edges of the child views are blurred
                // as well; otherwise we clear the blurring canvas with a transparent color.
                //如果mBlurredView有背景色需要画上这个背景色, 否则画透明
                if (mBlurredView.getBackground() != null && mBlurredView.getBackground() instanceof ColorDrawable) {
                    mBitmapToBlur.eraseColor(((ColorDrawable) mBlurredView.getBackground()).getColor());
                } else {
//                    mBitmapToBlur.eraseColor(Color.TRANSPARENT);
                    mBitmapToBlur.eraseColor(Color.WHITE);
                }
                mBlurredView.draw(mBlurringCanvas);
                blur();

                canvas.save();
//                canvas.translate(mBlurredView.getX() - ((View)getParent()).getX(), mBlurredView.getY() - ((View)getParent()).getY());
                canvas.scale(mDownsampleFactor, mDownsampleFactor);
                canvas.drawBitmap(mBlurredBitmap, 0, 0, null);
                canvas.restore();

                if (mOnDrawBlurListener != null) {
                    mOnDrawBlurListener.onAfterBlurDraw();
                }
            }
            canvas.drawColor(mOverlayColor);
        }
    }

    protected boolean prepare() {
        final int width = mBlurredView.getWidth();
        final int height = mBlurredView.getHeight();

        final int myW = getWidth();
        final int myH = getHeight();

        if (mBlurringCanvas == null || mDownsampleFactorChanged
                || mBlurredViewWidth != width || mBlurredViewHeight != height) {

            mDownsampleFactorChanged = false;

            mBlurredViewWidth = width;
            mBlurredViewHeight = height;

            int scaledWidth = (int) (myW / mDownsampleFactor + 1f);
            int scaledHeight = (int) (myH / mDownsampleFactor + 1f);

            // The following manipulation is to avoid some RenderScript artifacts at the edge.
            scaledWidth = scaledWidth - scaledWidth % 4 + 4;
            scaledHeight = scaledHeight - scaledHeight % 4 + 4;

            if (mBlurredBitmap == null
                    || mBlurredBitmap.getWidth() != scaledWidth
                    || mBlurredBitmap.getHeight() != scaledHeight) {
                mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (mBlurredBitmap == null) {
                    return false;
                }

                mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (mBitmapToBlur == null) {
                    return false;
                }
            }

            mBlurringCanvas = new Canvas(mBitmapToBlur);
            mBlurringCanvas.scale(1f / mDownsampleFactor, 1f / mDownsampleFactor);
//            mBlurringCanvas.translate(0, 1f*(myH-height)/mDownsampleFactor);
            mBlurringCanvas.translate(0, myH-height);
            mBlurInput = Allocation.createFromBitmap(mRenderScript, mBitmapToBlur, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
//            mBlurInput = Allocation.createFromBitmap(mRenderScript, mBitmapToBlur);
            mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());
//            mBlurInput_Output = Allocation.createFromBitmap(mRenderScript, mBlurredBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        }
        return true;
    }

    protected void blur() {
        mBlurInput.copyFrom(mBitmapToBlur);
        mBlurScript.setInput(mBlurInput);
        mBlurScript.forEach(mBlurOutput);
        mBlurOutput.copyTo(mBlurredBitmap);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRenderScript != null) {
            mRenderScript.destroy();
        }
    }

    public void setOnDrawBlurListener(onDrawBlurListener onDrawBlurListener) {
        mOnDrawBlurListener = onDrawBlurListener;
    }

    onDrawBlurListener mOnDrawBlurListener;
    public interface onDrawBlurListener {
        void onPreBlurDraw();
        void onAfterBlurDraw();
    }
}
