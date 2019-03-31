package com.wzx.wzxfoundation.base;

public interface BaseViewInterface {
    void onResume();
    void onPause();
    void onDestory();
    boolean onBackPress();

    void showLoadingLayout();
    void showNoContentLayout();
    void showErrorLayout(int errcode, String errMsg);
    void dismissAllPromptLayout();
    boolean isShowLoadingLayout();
}
