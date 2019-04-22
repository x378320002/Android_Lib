package com.wzx.wzxfoundation.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by wangzixu on 2018/5/25.
 */
public abstract class BaseView extends FrameLayout implements BaseViewInterface {
    private View mErrorLayout;
    private View mLoadingLayout;
    private View mNoContentLayout;
    protected Context mContext;
    protected boolean mResumed;

    public BaseView(@NonNull Context context) {
        this(context, null);
    }

    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            LayoutInflater.from(context).inflate(layoutId, this, true);
        }
        post(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }
    protected abstract int getLayoutId();


    public abstract void init();

    private OnClickListener mRetryClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if ((mErrorLayout != null && v == mErrorLayout)
                ||(mNoContentLayout != null && v == mNoContentLayout)) {
                dismissAllPromptLayout();
                onClickRetry();
            }
        }
    };

    protected void onClickRetry() {

    }

    /**
     * 设置四种提示框，loading，网络错误，服务器错误，无内容
     */
    final public void setPromptLayout(View loadingLayout, View errorLayout , View noContentLayout) {
        mLoadingLayout = loadingLayout;
        mErrorLayout = errorLayout;
        mNoContentLayout = noContentLayout;

        if (mLoadingLayout != null) mLoadingLayout.setOnClickListener(mRetryClickListener);
        if (mErrorLayout != null) mErrorLayout.setOnClickListener(mRetryClickListener);
        if (mNoContentLayout != null) mNoContentLayout.setOnClickListener(mRetryClickListener);
    }



    @Override
    public void showLoadingLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.VISIBLE);
        if (mErrorLayout != null) mErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNoContentLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mErrorLayout != null) mErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorLayout(int errCode, String errMsg) {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
        if (mErrorLayout != null) mErrorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissAllPromptLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mErrorLayout != null) mErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean isShowLoadingLayout() {
        if (mLoadingLayout != null) {
            return mLoadingLayout.getVisibility() == View.VISIBLE;
        } else {
            return false;
        }
    }
    //*******************4种提示框相关的布局 end*************************

    @Override
    public void onResume() {
        mResumed = true;
    }

    @Override
    public void onPause() {
        mResumed = false;
    }

    @Override
    public void onDestory() {

    }

    @Override
    public boolean onBackPress() {
        if (getContainer() != null) {
            return getContainer().onBackPress();
        }
        return false;
    }

    /**
     * 是否需要消费掉后退事件, 如果此页面消费掉back, 请返回true
     * @return
     */
    public boolean consumeBack() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof BaseView) {
                BaseView childNavi = (BaseView) child;
                if (childNavi.consumeBack()) {
                    return true;
                }
            }
        }
        return false;
    }

    public BaseViewContainer getContainer() {
        BaseViewContainer container = null;
        View parent = (View) getParent();
        try {
            while (true) {
                if (parent == null) {
                    break;
                }
                if (parent instanceof BaseViewContainer) {
                    container = (BaseViewContainer) parent;
                    break;
                }
                parent = (View) parent.getParent();
            }
        } catch (Exception e) {
            //nothing
        }
        return container;
    }

    public void startNavigatorView(BaseView newView) {
        if (getContainer() != null) {
            getContainer().startBaseView(newView);
        }
    }
}
