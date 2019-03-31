package com.wzx.wzxfoundation.refreshlayout;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.wzx.wzxfoundation.R;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by wangzixu on 2017/9/1.
 */
public class MyPtrHeader extends FrameLayout implements PtrUIHandler {
    public MyPtrHeader(@NonNull Context context) {
        this(context, null);
    }

    public MyPtrHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyPtrHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.wzx_ptrheader, this);
    }



    @Override
    public void onUIReset(PtrFrameLayout frame) {
        Log.d("myptr", "onUIReset");
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        Log.d("myptr", "onUIRefreshPrepare");
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        Log.d("myptr", "onUIRefreshBegin");
    }

    //适用于加载更多的那个库
//    @Override
//    public void onUIRefreshComplete(PtrFrameLayout frame, boolean isHeader) {
//        Log.d("myptr", "onUIRefreshComplete");
//        mTvTitle.setText("加载完成");
//    }
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        Log.d("myptr", "onUIRefreshComplete");
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
