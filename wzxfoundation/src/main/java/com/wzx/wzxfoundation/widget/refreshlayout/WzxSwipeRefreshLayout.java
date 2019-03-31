package com.wzx.wzxfoundation.widget.refreshlayout;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by wangzixu on 2018/5/9.
 * 系统的下拉刷新太灵敏, 只要有Y方向的滑动位移都会相应, 而米有判断是X还是Y的距离更大
 * 现在增加了拦截规则, 必须是Y方向向下滑动, 并且x方向位移不能超多y方向的一半
 */
public class WzxSwipeRefreshLayout extends SwipeRefreshLayout {
    private float mDownX, mDownY;
    private int mTouchSlop;
    private onRefreshEndListener mOnRefreshEndListener;

    public WzxSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public WzxSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() + 5;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
//        LogHelper.d("wangzixu", "Swipe onInterceptTouchEvent --- action = " + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float x = ev.getX();
                final float y = ev.getY();

                final float xDiff = x - mDownX;
                final float yDiff = y - mDownY;
                if (yDiff < mTouchSlop || yDiff < 2*Math.abs(xDiff)) {
                    return false;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isRefreshing()) { //当刷新的时候把事件拦截掉
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (mOnRefreshEndListener != null) {
            if (refreshing) {
                mOnRefreshEndListener.startAnim();
            } else if (!refreshing && isRefreshing()) {
                mOnRefreshEndListener.end();
            }
        }
        super.setRefreshing(refreshing);
    }

    public void setOnRefreshEndListener(onRefreshEndListener onRefreshEndListener) {
        mOnRefreshEndListener = onRefreshEndListener;
    }

    public interface onRefreshEndListener {
        void end();
        void startAnim();
    }
}
