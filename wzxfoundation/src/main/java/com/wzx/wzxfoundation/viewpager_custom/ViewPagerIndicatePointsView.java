package com.wzx.wzxfoundation.viewpager_custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2018/4/26.
 */
public class ViewPagerIndicatePointsView extends View {
    private Context mContext;
    private ArrayList<Integer> mPoints = new ArrayList<>();
    private int mPointCount;
    private int mRadius;
    private int mGap; //点和点之间的距离
    private int mWidth; //控件宽度, 需要自己计算
    private Paint mPaint;
    private Paint mSelectPaint;
    private int mCurrentPos;
    private float mPositionOffset;
    private float mPy; //画点的y轴位置
    private int mRedBiggerR = 0;

    public ViewPagerIndicatePointsView(Context context) {
        this(context, null);
    }

    public ViewPagerIndicatePointsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicatePointsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        mPaint = new Paint();
        mPaint.setColor(0xFFADADAD);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);

        mSelectPaint = new Paint();
        mSelectPaint.setColor(0xFFFE2446);
//        mSelectPaint.setColor(0xFFFFFFFF);
        mSelectPaint.setDither(true);
        mSelectPaint.setAntiAlias(true);
    }

    public void setPaintColor(int paintColor, int selectColor) {
        mPaint.setColor(paintColor);
        mSelectPaint.setColor(selectColor);
        invalidate();
    }

    /**
     * @param count 数量
     * @param gap 间距
     * @param radius 正常点的半径
     * @param biggerR 红点的半径比正常点的半径大多少, 如果0, 就表示红点半径和正常点一样大
     */
    public void setPoints(int count, int gap, int radius, int biggerR) {
        mPointCount = count;
        mGap = gap;
        mRadius = radius;
        mRedBiggerR = biggerR;

        //根据点的数量和间距和直径, 计算出整个view需要宽度
        //后来加个个需求,红点的半径比正常的大, 所有宽度左右两边各加biggerR
        mWidth = mPointCount * mRadius * 2 + (mPointCount - 1) * mGap + 2*biggerR;
        Log.d("wangzixu", "uploadimgpoint mWidth = " + mWidth);
        requestLayout();
        //invalidate();
    }

    public int getPointsCount() {
        return mPointCount;
    }

    public void setSelectPoint(int pos) {
        mCurrentPos = pos;
        invalidate();
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPos = position;
        mPositionOffset = positionOffset;
        invalidate();
        Log.d("wangzixu", "onPageScrolled position = " + position + ", positionOffset = " + positionOffset);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPy = getHeight() * 0.5f;
//        Log.d("wangzixu", "uploadimgpoint onSizechange w h = " + w +", h = " + h);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPy <= 0) {
            return;
        }

        int gap = mGap + mRadius * 2;

        //画正常点
        for (int i = 0; i < mPointCount; i++) {
            canvas.drawCircle(mRadius + i * gap + mRedBiggerR, mPy, mRadius, mPaint);
        }

        //画红点
        canvas.drawCircle(mRadius + (mCurrentPos + mPositionOffset) * gap + mRedBiggerR, mPy, mRadius + mRedBiggerR, mSelectPaint);
    }
}
