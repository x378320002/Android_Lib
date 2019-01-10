package com.wzx.android_lib.zoomview_viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangzixu on 2018/12/25.
 * 和ZoomImageView搭配使用的时候, 可以完美支持放大缩小图片的viewpager
 */
public class ZoomViewPager extends ViewPager {
    private ZoomImageView mZoomImg;
    private boolean mOnlyZoomImage; //是否只把事件给了zoomimage
    private float mLastX, mLastY;
    private int mEdgeScroll; //当前边界

    public ZoomViewPager(Context context) {
        super(context);
    }

    public ZoomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mEdgeScroll = Integer.MIN_VALUE;
            mZoomImg = findZoomImg(this, ev.getRawX(), ev.getRawY());
            mLastX = ev.getX();
            mLastY = ev.getY();
        }

        if (mZoomImg != null) {
            int scrollX = getScrollX();
            int offset = scrollX % getWidth();
            Log.d("wangzixu", "dispatchTouchEvent mZoomImg scrollx = " + scrollX);
            if (offset == 0) { //说明当前vp正好显示一屏幕
                mEdgeScroll = scrollX;

                //zoomimg相应事件的过程begin
                mZoomImg.setCanRequestParentDisllowIntercept(false);
                mZoomImg.onTouchEvent(ev);
                int touchMode = mZoomImg.getTouchMode();
                float[] consumeXy = mZoomImg.getConsumeXy();
                if (consumeXy[0] != 0 || consumeXy[1] != 0 || touchMode == ZoomImageView.ZOOM || touchMode == ZoomImageView.ZOOM_ANIM) {
                    mOnlyZoomImage = true;
                    return true;
                } else {
                    //图片拖动转换到viewpager滑动的过程
                    if (mOnlyZoomImage) {
                        mOnlyZoomImage = false;
                        mLastX = ev.getX();

                        //触到了临界点, 两个view都需要用down初始化一下, 进入重新确定后续的事件谁先相应,
                        //如果不用daown初始化, 原来相应的view会持续相应, 或者新相应的view会产生跳变
                        MotionEvent obtain = MotionEvent.obtain(ev);
                        obtain.setAction(MotionEvent.ACTION_DOWN);
                        mZoomImg.onTouchEvent(obtain);
                        return super.dispatchTouchEvent(obtain);
                    }
                }
                //zoomimg相应事件的过程end
            } else {
                //vp响应事件的过程
                if (mEdgeScroll > Integer.MIN_VALUE && (mZoomImg.canHorizontalDrag()) ) {
                    float x = ev.getX();
                    float deltaX = mLastX - x; //向左滑, scrollX逐渐变大, 所以delta应该是旧的减去新的
                    mLastX = x;

                    Log.d("wangzixu", "dispatchTouchEvent mEdgeScroll scrollx = " + scrollX + ",  edge = " + mEdgeScroll + ", deltaX = " + deltaX);
                    if ((scrollX < mEdgeScroll && scrollX + deltaX > mEdgeScroll) //左滑
                            || (scrollX > mEdgeScroll && scrollX + deltaX < mEdgeScroll)) { //右滑
                        //viewpager滑动转换到图片拖动的过程
                        scrollTo(mEdgeScroll, getScrollY());

                        //触到了临界点, 两个view都需要用down初始化一下, 进入重新确定后续的事件谁先相应,
                        //如果不用daown初始化, 原来相应的view会持续相应, 或者新相应的view会产生跳变
                        MotionEvent obtain = MotionEvent.obtain(ev);
                        obtain.setAction(MotionEvent.ACTION_DOWN);
                        mZoomImg.onTouchEvent(obtain);
                        return super.dispatchTouchEvent(obtain);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    //寻找手指头按下的点上的zoomiamgeview
    int[] mLocation = new int[]{0, 0};
    public ZoomImageView findZoomImg(ViewGroup viewGroup, float x, float y) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() == VISIBLE) {
                childAt.getLocationOnScreen(mLocation);
                if (x > mLocation[0] && x < mLocation[0] + childAt.getWidth()
                        && y > mLocation[1] && y < mLocation[1] + childAt.getHeight()) {
                    if (childAt instanceof ViewGroup) {
                        ViewGroup vg = (ViewGroup) childAt;
                        return findZoomImg(vg, x, y);
                    } else {
                        if (childAt instanceof ZoomImageView) {
                            return (ZoomImageView) childAt;
                        }
                    }
                }
            }
        }
        return null;
    }
}
