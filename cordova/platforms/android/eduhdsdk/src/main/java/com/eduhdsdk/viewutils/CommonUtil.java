package com.eduhdsdk.viewutils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.ui.holder.OneToManyRootHolder;
import com.eduhdsdk.ui.holder.TKBaseRootHolder;

/**
 * Created by fucc on 2019/4/4.
 */

public class CommonUtil {


    public static void changeBtimapColor(ImageView imageView, String color) {
        Drawable drawable = imageView.getDrawable().mutate();
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(color));
        imageView.setImageDrawable(wrappedDrawable);
    }

    public static void setTimeVisibility(TKBaseRootHolder mRootHolder, int visibility) {
        mRootHolder.txt_hour.setVisibility(visibility);
        mRootHolder.txt_mao_01.setVisibility(visibility);
        mRootHolder.txt_min.setVisibility(visibility);
        mRootHolder.txt_mao_02.setVisibility(visibility);
        mRootHolder.txt_ss.setVisibility(visibility);
    }
}
