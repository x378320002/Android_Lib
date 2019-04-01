package com.wzx.wzxfoundation.widget.refreshlayout;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.wzx.wzxfoundation.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by wangzixu on 2017/9/1.
 */
public class MyPtrHeader_Lottie extends FrameLayout implements PtrUIHandler {
    private LottieAnimationView mView;

    public MyPtrHeader_Lottie(@NonNull Context context) {
        this(context, null);
    }

    public MyPtrHeader_Lottie(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyPtrHeader_Lottie(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.wzx_ptrheader_lottie, this);
        mView = findViewById(R.id.progress_bar);
    }

    //1
    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        Log.d("myptr", "onUIRefreshPrepare");
        mView.setProgress(0);
    }

    //2
    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        Log.d("myptr", "onUIRefreshBegin");
        mView.playAnimation();
    }

    //3
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        Log.d("myptr", "onUIRefreshComplete");
    }

    //4
    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mView.setProgress(0);
        mView.cancelAnimation();
        Log.d("myptr", "onUIReset");
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
//        final int mOffsetToRefresh = ptrIndicator.getOffsetToRefresh();
//        final int currentPos = ptrIndicator.getCurrentPosY();
//        final int lastPos = ptrIndicator.getLastPosY();
//
//
//        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
//            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
//                mTvTitle.setText("下拉刷新");
//                if (mIvArrow != null) {
//                    mIvArrow.clearAnimation();
//                    mIvArrow.startAnimation(mReverseFlipAnimation);
//                }
//            }
//        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
//            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
//                mTvTitle.setText("释放刷新");
//                if (mIvArrow != null) {
//                    mIvArrow.clearAnimation();
//                    mIvArrow.startAnimation(mFlipAnimation);
//                }
//            }
//        }
    }
}
