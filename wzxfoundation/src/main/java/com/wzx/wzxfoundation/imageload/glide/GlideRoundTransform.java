package com.wzx.wzxfoundation.imageload.glide;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by Weidongjian on 2015/7/29.
 */
public class GlideRoundTransform extends BitmapTransformation {

    private float radius = 0f;
    private boolean isSquare = false;

    public GlideRoundTransform(Context context, int dp, boolean isSquare) {
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
        this.isSquare = isSquare;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int w = source.getWidth();
        int h = source.getHeight();
        Bitmap ori;
        if (isSquare) {
            w = h = Math.min(w, h);

            //截取中间的位置的方法，暂时不用，因为是长图，截取开头反而更好
//            int x = (source.getWidth() - w) / 2;
//            int y = (source.getHeight() - h) / 2;
//            ori = Bitmap.createBitmap(source, x, y, w, h);
            ori = source;
        } else {
            ori = source;
        }

        Bitmap result = pool.get(w, h, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(ori, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);

        RectF rectF = new RectF(0f, 0f, w, h);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}