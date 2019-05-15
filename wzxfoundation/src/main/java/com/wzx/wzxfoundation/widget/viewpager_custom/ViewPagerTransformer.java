package com.wzx.wzxfoundation.widget.viewpager_custom;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by wangzixu on 2017/1/17.
 */
public class ViewPagerTransformer {

    /**
     * 景深切换效果
     */
    public static class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    /**
     * 景深切换效果
     */
    public static class DepthPageTransformer2 implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
//                view.setTranslationX(0);
                view.setTranslationX(-pageWidth * position*2);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    /**
     * 图片视差滚动
     */
    public static class ParallaxTransformer implements ViewPager.PageTransformer {
        private float PARALLAX_COEFFICIENT = -0.75f;
        //视差滚动其中的内容
        private int mContentViewId;

        public ParallaxTransformer(int contentView) {
            mContentViewId = contentView;
        }

        @Override
        public void transformPage(View view, float position) {
            float scrollXOffset = view.getWidth() * PARALLAX_COEFFICIENT;
            View page = view.findViewById(mContentViewId);

            if (page != null && position >= -1 && position <= 1) { // [-1,1]
                page.setTranslationX(scrollXOffset * position);
            }
        }
    }

    /**
     * 左右变小, 中间放大的卡片效果, viewpager和viewpager的根布局都得加clipChildren=false属性
     */
    public static class LoopTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;

        @Override
        public void transformPage(View view, float position) {
            ///**
            // * 过滤那些 <-1 或 >1 的值，使它区于【-1，1】之间
            // */
            //if (position < -1) {
            //    position = -1;
            //} else if (position > 1) {
            //    position = 1;
            //}
            ///**
            // * 判断是前一页 1 + position ，右滑 pos -> -1 变 0
            // * 判断是后一页 1 - position ，左滑 pos -> 1 变 0
            // */
            //float tempScale = position < 0 ? 1 + position : 1 - position; // [0,1]
            //float scaleValue = MIN_SCALE + tempScale * 0.1f; // [0,1]
            //view.setScaleX(scaleValue);
            //view.setScaleY(scaleValue);

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setScaleX(MIN_SCALE);
                view.setScaleY(MIN_SCALE);
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                float scale = (1.0f - MIN_SCALE) * position + 1.0f;
                view.setScaleX(scale);
                view.setScaleY(scale);
            } else if (position <= 1) { // (0,1]
                float scale = (MIN_SCALE - 1.0f) * position + 1.0f;
                view.setScaleX(scale);
                view.setScaleY(scale);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                // This page is way off-screen to the left.
                view.setScaleX(MIN_SCALE);
                view.setScaleY(MIN_SCALE);
            }
        }
    }
}
