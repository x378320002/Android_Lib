package com.wzx.android_lib.doubleclick_zan_view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.wzx.android_lib.R;
import com.wzx.android_lib.util.DisplayUtil;


/**
 * Created by wangzixu on 2018/4/18.
 * 双击跳心的点赞效果, 自定义view, 模仿抖音点赞效果
 */
public class DoubleClickZanImageView extends AppCompatImageView {
    Context mContext;
    int mIvW, mIvH;
    private int mTouchSlop;

    public DoubleClickZanImageView(Context context) {
        this(context, null);
    }

    public DoubleClickZanImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleClickZanImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
        mIvW = DisplayUtil.dip2px(context, 95);
        mIvH = DisplayUtil.dip2px(context, 84);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop() * 3;
    }

//    @Override
//    public void setImageDrawable(Drawable drawable) {
//        super.setImageDrawable(drawable);
//        reConfigureBounds();
//    }

    /**
     * 产品经理需求:
     * 宽度铺满, 等比缩放, 高度:宽度最小不能小于3:4...
     */
//    private void reConfigureBounds() {
//        if (getDrawable() == null) {
//            return;
//        }
//
//        final int dwidth = getDrawable().getIntrinsicWidth();
//        final int dheight = getDrawable().getIntrinsicHeight();
//
//        final int vwidth = getWidth() - getPaddingLeft() - getPaddingRight();
//        final int vheight = getHeight() - getPaddingTop() - getPaddingBottom();
//
//        Matrix matrix = getMatrix();
//
//        float scale;
//        float dx = 0, dy = 0;
//
//        //需求被Boss批评, 要求整改, 去掉3/4的限制
////        if (3*dwidth > 4*dheight) {
////            int desth = (vwidth*3)/4;
////            scale = (float)desth/(float)dheight;
////            dx = (vwidth - dwidth * scale) * 0.5f;
////            dy = (vheight - dheight * scale) * 0.5f;
////        } else {
////        }
//
//        scale = (float) vwidth / (float) dwidth;
//        dy = (vheight - dheight * scale) * 0.5f;
//
//        LogHelper.d("wangzixu", "MyimageView dx,dy = " + dx + ", " + dy);
//        matrix.setScale(scale, scale);
//        matrix.postTranslate(Math.round(dx), Math.round(dy));
//        setImageMatrix(matrix);
//    }

    //**********双击图片点赞相关begin***********
    long[] mHits = new long[2];
    float mLastDownX, mLastDownY;
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        int action = event.getAction();
        boolean b = super.onTouchEvent(event);

        if (action == MotionEvent.ACTION_DOWN && !b) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();

            float x = event.getX();
            float y = event.getY();
            boolean moved = (Math.abs(mLastDownX-x) > mTouchSlop) || (Math.abs(mLastDownY-y) > mTouchSlop);

            mLastDownX = x;
            mLastDownY = y;

            if (!moved && mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                if (mParentView == null) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        mParentView = (ViewGroup) parent;
                    }
                }
                if (mParentView != null) {
                    //双击
                    post(new Runnable() {
                        @Override
                        public void run() {
                            doubleclick(mLastDownX, mLastDownY);
                        }
                    });
                }
            }
        }
        return b;
    }

    private ViewGroup mParentView;
    //动画中随机❤的旋转角度
    float[] mRotations = new float[]{-20f, -10f, 0f, 10f, 20f};
    private int mRotationIndex = 2;

    private void doubleclick(float x, float y) {
        //有连续触摸的时候，创建一个展示心形的图片
        final ImageView iv = new ImageView(mContext);

        //设置展示的位置，需要在手指触摸的位置上方，即触摸点是心形的右下角的位置
        ViewGroup.MarginLayoutParams lp = new FrameLayout.LayoutParams(mIvW, mIvH);
        lp.leftMargin = (int) (x - mIvW * 0.5f);
        lp.topMargin = (int) (y - mIvH);
        //设置图片资源
        iv.setImageResource(R.drawable.ic_launcher_background);
        iv.setLayoutParams(lp);

        //把IV添加到父布局当中
        mParentView.addView(iv);

        //设置控件的动画
        int index = mRotationIndex % mRotations.length;
        mRotationIndex = index + 1;
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(
                scaleAniX(iv, 1.6f, 0.7f, 100, 0))
                .with(scaleAniY(iv, 1.6f, 0.7f, 100, 0))
                .with(alphaAni(iv, 0F, 1F, 100, 0))
                .with(rotation(iv, 0, 0, mRotations[index]))

                .with(scaleAniX(iv, 0.7f, 0.8F, 100, 100))
                .with(scaleAniY(iv, 0.7f, 0.8F, 100, 100))

                .with(scaleAniX(iv, 0.8f, 0.75F, 50, 200))
                .with(scaleAniY(iv, 0.8f, 0.75F, 50, 200))

                .with(translationY(iv, 0F, (float) -mIvH, 500, 450))

                .with(alphaAni(iv, 1F, 0F, 400, 450))

                .with(scaleAniX(iv, 0.75F, 2.6f, 500, 450))
                .with(scaleAniY(iv, 0.75F, 2.6f, 500, 450));

//        animatorSet.play(
//                //缩放动画，X轴2倍缩小至0.9倍
//                scaleAni(iv, "scaleX", 2f, 0.9f, 100, 0))
//                //缩放动画，Y轴2倍缩放至0.9倍
//                .with(scaleAni(iv, "scaleY", 2f, 0.9f, 100, 0))
//                //旋转动画，随机旋转角
//                .with(rotation(iv, 0, 0, mRotations[mRandom.nextInt(mRotations.length)]))
//                //渐变透明动画，透明度从0-1
//                .with(alphaAni(iv, 0F, 1F, 100, 0))
//                //缩放动画，X轴0.9倍缩小至
//                .with(scaleAni(iv, "scaleX", 0.9f, 1F, 50, 150))
//                //缩放动画，Y轴0.9倍缩放至
//                .with(scaleAni(iv, "scaleY", 0.9f, 1F, 50, 150))
//                //位移动画，Y轴从0上移至600
//                .with(translationY(iv, 0F, (float)-mIvH, 800, 400))
//                //透明动画，从1-0
//                .with(alphaAni(iv, 1F, 0F, 300, 400))
//                //缩放动画，X轴1至3倍
//                .with(scaleAni(iv, "scaleX", 1F, 3f, 700, 400))
//                //缩放动画，Y轴1至3倍
//                .with(scaleAni(iv, "scaleY", 1F, 3f, 700, 400));


        //开始动画
        //设置动画结束监听
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mParentView.removeViewInLayout(iv);
            }
        });
        animatorSet.start();

        if (mOnDoubleClickListener != null) {
            mOnDoubleClickListener.onDoubleClick(this);
        }
    }

    public ObjectAnimator scaleAniX(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "scaleX", from, to);
        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public ObjectAnimator scaleAniY(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "scaleY", from, to);
        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public ObjectAnimator translationX(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "translationX", from, to);
        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public ObjectAnimator translationY(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "translationY", from, to);
        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public ObjectAnimator alphaAni(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "alpha", from, to);
        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public ObjectAnimator rotation1(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "rotation", from, to);
        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public ObjectAnimator rotation(View view, long time, long delayTime, float... values) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "rotation", values);
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        ani.setInterpolator(new LinearInterpolator());
        return ani;
    }

    onDoubleClickListener mOnDoubleClickListener;
    public void setOnDoubleClickListener(onDoubleClickListener onDoubleClickListener) {
        mOnDoubleClickListener = onDoubleClickListener;
    }

    public interface onDoubleClickListener {
        void onDoubleClick(View view);
    }
    //**********双击图片点赞相关end***********
}
