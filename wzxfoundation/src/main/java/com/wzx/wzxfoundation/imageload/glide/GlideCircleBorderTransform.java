package com.wzx.wzxfoundation.imageload.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by Weidongjian on 2015/7/29.
 */
public class GlideCircleBorderTransform extends BitmapTransformation {
    private Paint mBorderPaint;
    private float mBorderWidth;

    public GlideCircleBorderTransform(Context context, int borderWidth, int borderColor) {
        //因为画圆环时，画笔的线宽是里外各一半，所以乘以2，先画圆环，再画原图，并且半径可以一致
        mBorderWidth = borderWidth * 2;

        mBorderPaint = new Paint();
        mBorderPaint.setDither(true);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }


    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        //canvas画空心框时，肯定有线的宽度，框的大小是线宽的中心位置，也就是框的里外各有一半线宽
//        int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
        int size = Math.min(source.getWidth(), source.getHeight());

        Bitmap squared;
        if (source.getWidth() == source.getHeight()) {
            squared = source;
        } else {
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            squared = Bitmap.createBitmap(source, x, y, size, size);
        }

        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        //先画圆，再画图
        Canvas canvas = new Canvas(result);
        float r = size / 2f; //圆心
        float radius = (size - mBorderWidth) / 2;
        if (mBorderPaint != null) {
            canvas.drawCircle(r, r, radius, mBorderPaint);
        }

        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        canvas.drawCircle(r, r, radius, paint);

        return result;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}