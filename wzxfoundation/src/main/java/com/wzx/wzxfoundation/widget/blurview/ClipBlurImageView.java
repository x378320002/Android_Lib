package com.wzx.wzxfoundation.widget.blurview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by wangzixu on 2018/12/24.
 */
public class ClipBlurImageView extends AppCompatImageView {
    private int mTop;
    public ClipBlurImageView(Context context) {
        super(context);
    }

    public ClipBlurImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTopEdge(int top) {
        mTop = top;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, mTop, getWidth(), getHeight());
        super.onDraw(canvas);
        canvas.restore();
    }
}
