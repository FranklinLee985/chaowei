package com.eduhdsdk.tools;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.eduhdsdk.R;

import skin.support.SkinCompatManager;

/**
 * 设置皮肤的loding动画
 * Created by YF on 2018/11/28 0028.
 */

public class SkinTool {
    private static SkinTool mInstance = null;

    public static SkinTool getmInstance() {
        synchronized (SkinTool.class) {
            if (mInstance == null) {
                mInstance = new SkinTool();
            }
            return mInstance;
        }
    }

    /**
     * 加载不同的loading图
     *
     * @param context
     * @param img
     */
    public void setLoadingSkin(Context context, ImageView img) {
        Glide.with(context).asGif().load(R.drawable.tk_loadingpad)
                .into(img);
    }
}
