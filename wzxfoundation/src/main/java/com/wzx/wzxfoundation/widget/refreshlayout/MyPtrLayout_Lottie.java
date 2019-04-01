package com.wzx.wzxfoundation.widget.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by wangzixu on 2017/9/1.
 */
public class MyPtrLayout_Lottie extends PtrFrameLayout {
    private MyPtrHeader_Lottie mMyPtrHeader;

    public MyPtrLayout_Lottie(Context context) {
        this(context, null);
    }

    public MyPtrLayout_Lottie(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyPtrLayout_Lottie(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        mMyPtrHeader = new MyPtrHeader_Lottie(getContext());
        setHeaderView(mMyPtrHeader);
        addPtrUIHandler(mMyPtrHeader);
    }
}
