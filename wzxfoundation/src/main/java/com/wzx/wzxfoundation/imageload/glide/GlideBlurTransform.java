package com.wzx.wzxfoundation.imageload.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.wzx.wzxfoundation.widget.blurview.BlurUtil;

import java.security.MessageDigest;

/**
 * Created by wangzixu on 2018/11/9.
 */
public class GlideBlurTransform extends BitmapTransformation {
    private int radius;
    private int sampling;
    Context mContext;

    public GlideBlurTransform(Context context, int sampling, int radius) {
        mContext = context;
        this.radius = radius;
        this.sampling = sampling;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = BlurUtil.rsBlur(mContext, toTransform, sampling, radius);
        return bitmap;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
    }
}
