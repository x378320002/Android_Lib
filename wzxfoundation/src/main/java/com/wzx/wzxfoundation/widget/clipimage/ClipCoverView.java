package com.wzx.wzxfoundation.widget.clipimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wzx.wzxfoundation.R;
import com.wzx.wzxfoundation.util.DisplayUtil;


public class ClipCoverView extends View {
    private int mLeftGap, mTopGap, mRightGap, mBottomGap;
    private int mWidgetOnePaddingLeft, mWidgetOnePaddingTop,
            mWidgetSecondPaddingRight, mWidgetSecondPaddingTop;
    private int mFrameLineWidth, mInnerLineWidth;
    private int mOutFillColor, mInnerLineColor;
    private int mInnerLineHorizontalCount, mInnerLineVerticalCount;
    private int mWidth, mHeight, mScreenWidth, mScreenHeight;
    private float[] mInnerLinePts;
    private Paint mPaint = new Paint();
    private Rect mClipRect = new Rect();
    private Rect mClipRectFrame = new Rect();
    private Drawable mWidget1;
    private Drawable mWidget2;
    private Rect mWidget1Bound, mWidget2Bound;

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public ClipCoverView(Context context) {
        super(context);
    }

    public ClipCoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ClipCoveredViewStyle);
        try {
            mTopGap = a.getDimensionPixelSize(R.styleable.ClipCoveredViewStyle_top_gap, 0);
            mBottomGap = a.getDimensionPixelSize(R.styleable.ClipCoveredViewStyle_bottom_gap, 0);
            mLeftGap = a.getDimensionPixelSize(R.styleable.ClipCoveredViewStyle_left_gap, 0);
            mRightGap = a.getDimensionPixelSize(R.styleable.ClipCoveredViewStyle_right_gap, 0);

            mFrameLineWidth = a.getDimensionPixelSize(
                    R.styleable.ClipCoveredViewStyle_frame_line_width, 4);
            mInnerLineWidth = a.getDimensionPixelSize(
                    R.styleable.ClipCoveredViewStyle_inner_line_width, 1);
            mOutFillColor = a
                    .getColor(R.styleable.ClipCoveredViewStyle_out_fill_color,
                            0x00000000);
            mInnerLineColor = a.getColor(
                    R.styleable.ClipCoveredViewStyle_inner_line_color,
                    0xFFFFFFFF);
            mInnerLineHorizontalCount = a
                    .getInteger(
                            R.styleable.ClipCoveredViewStyle_inner_line_horizontal_count,
                            2);
            mInnerLineVerticalCount = a.getInteger(
                    R.styleable.ClipCoveredViewStyle_inner_line_vertical_count,
                    2);

            mWidget1 = a
                    .getDrawable(R.styleable.ClipCoveredViewStyle_widget_first);
            if (mWidget1 != null) {
                mWidgetOnePaddingLeft = a
                        .getDimensionPixelSize(
                                R.styleable.ClipCoveredViewStyle_widget_first_paddingLeft,
                                0);
                mWidgetOnePaddingTop = a
                        .getDimensionPixelSize(
                                R.styleable.ClipCoveredViewStyle_widget_first_paddingTop,
                                0);
            }

            mWidget2 = a
                    .getDrawable(R.styleable.ClipCoveredViewStyle_widget_second);
            if (mWidget2 != null) {
                mWidgetSecondPaddingRight = a
                        .getDimensionPixelSize(
                                R.styleable.ClipCoveredViewStyle_widget_second_paddingRight,
                                0);
                mWidgetSecondPaddingTop = a
                        .getDimensionPixelSize(
                                R.styleable.ClipCoveredViewStyle_widget_second_paddingTop,
                                0);
            }
        } finally {
            a.recycle();
        }
        //mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        //mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        Point point = DisplayUtil.getRealScreenPoint(context);
        mScreenWidth = point.x;
        mScreenHeight = point.y;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
        Log.d("wangzixu", "onSizeChanged mHeight, mWidth = " + mHeight + " , " + mWidth);
        if (mWidth != 0) {
            init();
        }
    }

    public void setGap(int left, int top, int right, int bottom) {
        mLeftGap = left;
        mTopGap = top;
        mRightGap = right;
        mBottomGap = bottom;
        init();
    }

    private void init() {
//        int clipHeight = mHeight - mTopGap - mBottomGap;
//        int clipWidth = (int) (((float) mScreenWidth / mScreenHeight) * clipHeight);
//        mLeftGap = mRightGap = (mWidth - clipWidth) / 2;

        mClipRect.set(mLeftGap, mTopGap, mWidth - mRightGap, mHeight - mBottomGap);
        mClipRectFrame.set((int) (mClipRect.left + mFrameLineWidth*0.5f)
                , (int) (mClipRect.top + mFrameLineWidth*0.5f)
                , (int) (mClipRect.right - mFrameLineWidth*0.5f),
                (int) (mClipRect.bottom - mFrameLineWidth*0.5f));
        mInnerLinePts = new float[4 * (mInnerLineHorizontalCount + mInnerLineVerticalCount)];
        float hLineGap = mClipRect.height()
                / (float) (mInnerLineHorizontalCount + 1);
        for (int i = 0; i < mInnerLineHorizontalCount; i++) {
            int start = i * 4;
            mInnerLinePts[start] = mClipRect.left;
            mInnerLinePts[start + 1] = mClipRect.top + (hLineGap * (i + 1));
            mInnerLinePts[start + 2] = mClipRect.right;
            mInnerLinePts[start + 3] = mInnerLinePts[start + 1];
        }

        float vLineGap = mClipRect.width()
                / (float) (mInnerLineVerticalCount + 1);
        for (int i = 0; i < mInnerLineVerticalCount; i++) {
            int start = (i + mInnerLineHorizontalCount) * 4;
            mInnerLinePts[start] = mClipRect.left + (vLineGap * (i + 1));
            mInnerLinePts[start + 1] = mClipRect.top;
            mInnerLinePts[start + 2] = mInnerLinePts[start];
            mInnerLinePts[start + 3] = mClipRect.bottom;
        }

        if (mWidget1 != null) {
            if (mWidget1Bound == null) {
                mWidget1Bound = new Rect();
            }
            int left = mClipRect.left + mWidgetOnePaddingLeft;
            int top = mClipRect.top + mWidgetOnePaddingTop;
            mWidget1Bound.set(left, top, mWidget1.getIntrinsicWidth() - left,
                    mWidget1.getIntrinsicHeight());
            mWidget1.setBounds(mWidget1Bound);
        }

        if (mWidget2 != null) {
            if (mWidget2Bound == null) {
                mWidget2Bound = new Rect();
            }
            int right = mClipRect.right - mWidgetSecondPaddingRight;
            int top = mClipRect.top + mWidgetSecondPaddingTop;
            mWidget2Bound.set(right - mWidget2.getIntrinsicWidth(), top, right,
                    top + mWidget2.getIntrinsicHeight());
            mWidget2.setBounds(mWidget2Bound);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画外围黑色区域
        mPaint.setColor(mOutFillColor);
        canvas.drawRect(0, mClipRect.top, mClipRect.left, mClipRect.bottom,
                mPaint);
        canvas.drawRect(0, 0, mWidth, mClipRect.top, mPaint);
        canvas.drawRect(mClipRect.right, mClipRect.top, mWidth,
                mClipRect.bottom, mPaint);
        canvas.drawRect(0, mClipRect.bottom, mWidth, mHeight, mPaint);

        mPaint.setColor(mInnerLineColor);
        //画外部方框
        if (mFrameLineWidth > 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mFrameLineWidth);
            canvas.drawRect(mClipRectFrame, mPaint);
        }

        //画内部线条
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mInnerLineWidth);
        canvas.drawLines(mInnerLinePts, mPaint);
        if (mWidget1 != null) {
            mWidget1.draw(canvas);
        }
        if (mWidget2 != null) {
            mWidget2.draw(canvas);
        }
    }

    public Rect getClipRect() {
        return mClipRect;
    }
}
