package com.example.kotlindemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ProgressBar;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;

/**
 * @author wangzixu
 * @version 1.0
 * @date 2020/12/16
 * @desc
 */
public class MyProgress extends ProgressBar {
    private float mRadius = 18;

    public MyProgress(Context context) {
        this(context, null);
    }

    public MyProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MyProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final float[] roundedCorners = new float[] { mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius};

        //构造一个背景Drawable,圆角的, 作为背景
        ShapeDrawable shapeDrawable0 = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));
        shapeDrawable0.getPaint().setColor(0x99ffffff);

        //构造进度条Drawable
        //1,因为里面的进度条也需要圆角, 所以先构造ShapeDrawable
        ShapeDrawable shapeDrawable1 = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));
        //2,因为进度条是需要画图片bitmap的,所以生成一个bitmapShader, 把bitmapShader设置给paint后, paint画的东西就是这个图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.webview_christmas_progress_item);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        shapeDrawable1.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable1.getPaint().setShader(bitmapShader);
        //3,因为进度条是需要不断上涨的, 所以可以用一个ScaleDrawable包裹一下,
        // ScaleDrawable是对包装的drawable进行缩放处理, ClipDrawable是对对包装的drawable进行剪切处理
        ScaleDrawable scaleDrawable = new ScaleDrawable(shapeDrawable1, Gravity.START, 1.0f, -1.0f);

        //想办法构造一个LayerDrawable, 设置给progress
        Drawable[] drawables = new Drawable[1];
        drawables[0] = scaleDrawable;
//        drawables[1] = scaleDrawable;

        //自定义Progress, 关键就是自定义这个LayerDrawable, 里面的id是固定的
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        layerDrawable.setId(0, android.R.id.progress);
//        layerDrawable.setId(1, android.R.id.progress);

        setProgressDrawable(layerDrawable);
    }
}
