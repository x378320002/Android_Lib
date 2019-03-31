package com.wzx.wzxfoundation.widget.alert;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.wzx.wzxfoundation.R;


/**
 * 通用对话框类
 */
public class AlertDialogCancelFollow extends Dialog implements View.OnClickListener {
    private boolean cancelable = true;
    private View.OnClickListener mOnClickListener;
    private String mPortraitUrl;
    private String mName;

    public AlertDialogCancelFollow(Context context, String name, String portraitUrl, View.OnClickListener onClickListener) {
        super(context, R.style.WzxDialog);
        mOnClickListener = onClickListener;
        mPortraitUrl = portraitUrl;
        mName = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dialog_custom_cancelfollow);
//
//        //alertDialog是否可以点击外围消失
//        setCanceledOnTouchOutside(cancelable);
//        setCancelable(cancelable);
//
//        findViewById(R.id.tv_confirm).setOnClickListener(this);
//        findViewById(R.id.cancel).setOnClickListener(this);
//
//        TextView name = findViewById(R.id.tv_name);
//        name.setText(getContext().getString(R.string.cancel_follow, mName));
//
//        ImageView portrait = findViewById(R.id.iv_portrait);
//        Glide.with(getContext()).load(mPortraitUrl).bitmapTransform(new GlideCircleBorderTransform(getContext(), 1, Color.GRAY)).into(portrait);
//
//        final WindowManager.LayoutParams params = this.getWindow().getAttributes();
//        params.gravity = Gravity.CENTER;
//        this.getWindow().setAttributes(params);
    }

    @Override
    public void onClick(View v) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
        dismiss();
    }
}
