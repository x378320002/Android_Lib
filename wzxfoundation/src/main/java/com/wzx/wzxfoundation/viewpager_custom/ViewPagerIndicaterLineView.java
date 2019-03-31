package com.wzx.wzxfoundation.viewpager_custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangzixu on 2018/4/26.
 */
public class ViewPagerIndicaterLineView extends View {
    private Context mContext;
    private int mCount;
    private int mGap; //点和点之间的距离
    private int mWidth; //控件宽度, 需要自己计算
    private Paint mSelectPaint;
    private int mLineLength; //画线段的长度
    private int mLineLengthShrink; //画可伸缩线段的长度,
    private int mLineStart; //画线段的起点
    private int mHeight;
    private float mRadiusX; //画圆角矩形的x,y 半径
    private float mRadiusY;

    public ViewPagerIndicaterLineView(Context context) {
        this(context, null);
    }

    public ViewPagerIndicaterLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicaterLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        mSelectPaint = new Paint();
        mSelectPaint.setColor(0xFFFFFFFF);
        mSelectPaint.setDither(true);
        mSelectPaint.setAntiAlias(true);
    }

    public void setCount(int count) {
        mCount = count;
        if (mCount <= 1) {
            mLineLength = 0;
        } else {
            mLineLength = getWidth()/mCount;
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mCount <= 1) {
            mLineLength = 0;
        } else {
            mLineLength = getWidth()/mCount;
        }
        mLineLengthShrink = mLineLength;
        mHeight = getHeight();
        mRadiusX = mRadiusY = mHeight*0.5f;
    }

    public void setSelectPoint(int pos, float offset) {
//        if (offset <= 0.5f) {
//            mLineStart = mLineLength*pos;
//            mLineLengthShrink = (int) (mLineLength + mLineLength*offset*2);
//        } else {
//            int start = mLineLength*pos;
//            mLineStart = (int) ((2*offset - 1.0f)*mLineLength + start);
//            mLineLengthShrink = (int) (mLineLength + mLineLength*(1.0f-offset)*2);
//        }

        mLineStart = (int) (mLineLength*(pos + offset));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLineLength <= 0) {
            return;
        }
        canvas.drawRoundRect(mLineStart, 0, mLineStart+ mLineLengthShrink, mHeight, mRadiusX, mRadiusY, mSelectPaint);
    }
}
