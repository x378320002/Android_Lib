package com.wzx.wzxfoundation.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * 生成属性动画的工具类
 */
public class WzxAnimationUtil {
    /**
     * 点击view后使view放大缩小的方法, 大多数用来如点击赞, 图标放大缩小加个动画
     * @param view
     */
    public static void clickBigSmallAnimation(final View view) {
//        LogHelper.d("wangzixu", "view prov x= " + view.getPivotX() + ", y = " + view.getPivotY() + ", w = " + view.getWidth() + ", h = " + view.getHeight());

        final ValueAnimator anim1 = ValueAnimator.ofFloat(0, 1.0f);
        anim1.setDuration(150);
        anim1.setInterpolator(new LinearInterpolator());
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                float scale = 1.0f + f*0.1f;
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });

        ValueAnimator anim2 = ValueAnimator.ofFloat(0, 1.0f);
        anim2.setDuration(200);
        anim2.setInterpolator(new OvershootInterpolator());
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                float scale = 1.1f - f*0.1f;
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(anim1).before(anim2);
        animatorSet.start();
    }

    public static ObjectAnimator scaleAniX(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "scaleX", from, to);
//        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public static ObjectAnimator scaleAniY(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "scaleY", from, to);
//        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public static ObjectAnimator translationX(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "translationX", from, to);
//        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public static ObjectAnimator translationY(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "translationY", from, to);
//        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public static ObjectAnimator alphaAni(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "alpha", from, to);
//        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public static ObjectAnimator rotation1(View view, Float from, Float to, long time, long delayTime) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "rotation", from, to);
//        ani.setInterpolator(new LinearInterpolator());
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
        return ani;
    }

    public static ObjectAnimator rotation(View view, long time, long delayTime, float... values) {
        ObjectAnimator ani = ObjectAnimator.ofFloat(view, "rotation", values);
        ani.setDuration(time);
        ani.setStartDelay(delayTime);
//        ani.setInterpolator(new LinearInterpolator());
        return ani;
    }
}
