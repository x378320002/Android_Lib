package com.wzx.wzxfoundation.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.LinkedList;

/**
 * 实现自定义的view互相跳转功能, 这个类维护了所有经过其跳转的baseview
 */
public class BaseViewContainer extends BaseView {
    private LinkedList<BaseView> mBaseViews = new LinkedList<>();
    private boolean mIsInited;

    public BaseViewContainer(@NonNull Context context) {
        super(context);
    }

    public BaseViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void init() {
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!mIsInited) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child instanceof BaseView) {
                    initWithBaseView((BaseView) child);
                    break;
                }
            }
        }
    }

    public synchronized void initWithBaseView(BaseView newView) {
        mBaseViews.clear();
        mBaseViews.addLast(newView);
        mIsInited = true;
    }

    public void startBaseView(BaseView newView) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(newView, lp);
        //当前view的生命周期
        BaseView current = mBaseViews.peekLast();
        if (current != null) {
            current.onPause();
            current.setVisibility(GONE);
        }

        //新view的生命周期
        newView.onResume();
        mBaseViews.addLast(newView);
    }

    @Override
    public void onResume() {
        BaseView current = mBaseViews.peekLast();
        if (current != null) {
            current.onResume();
        }
    }

    @Override
    public void onPause() {
        BaseView current = mBaseViews.peekLast();
        if (current != null) {
            current.onPause();
        }
    }

    @Override
    public void onDestory() {
//        BaseView current = mBaseViews.peekLast();
//        if (current != null) {
//            current.onDestory();
//        }
        //销毁了, 全部的view都销毁
        for (BaseView view : mBaseViews) {
            if (view != null) {
                view.onDestory();
            }
        }
        mBaseViews.clear();
    }

    @Override
    public boolean onBackPress() {
        //先让里面的子view响应返回事件
        BaseView current = mBaseViews.peekLast();
        if (current != null) {
            if (current.consumeBack()) {
                return true;
            }
        }

        if (mBaseViews.size() <= 1) {
            return false;
        }
        mBaseViews.removeLast();
        BaseView  newView = mBaseViews.peekLast();

        //新view的生命周期
        newView.setVisibility(VISIBLE);
        newView.onResume();

        //当前view的生命周期
        current.onPause();
        current.onDestory();
        current.setVisibility(GONE);
        removeView(current);
        return true;
    }
}
