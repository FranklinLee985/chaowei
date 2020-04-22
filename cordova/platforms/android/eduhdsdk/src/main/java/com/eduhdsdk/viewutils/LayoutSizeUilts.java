package com.eduhdsdk.viewutils;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eduhdsdk.R;
import com.eduhdsdk.ui.holder.VideoItem;
import com.eduhdsdk.ui.holder.VideoItemToMany;

import org.tkwebrtc.RendererCommon;


/**
 * Created by Administrator on 2017/11/22/022.
 */

public class LayoutSizeUilts {

    /***
     *
     * @param it
     * @param rel_wb_container   分屏时 视频框大小
     * @param x
     * @param y
     */
    public static void videoSize(VideoItemToMany it, RelativeLayout rel_wb_container, int x, int y, double nameLabelHeight) {
        ViewGroup.LayoutParams rel_wb_containerLayoutParams = rel_wb_container.getLayoutParams();

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.width = rel_wb_containerLayoutParams.width / x;
        relparam.height = rel_wb_containerLayoutParams.height / y;

        it.parent.setLayoutParams(relparam);
        it.re_background.setLayoutParams(relparam);
        it.bg_video_back.setLayoutParams(relparam);
        it.rel_video_label.setLayoutParams(relparam);
        it.rel_video_label.setBackgroundResource(R.color.window_back_black);

//        LinearLayout.LayoutParams imc = (LinearLayout.LayoutParams) it.img_mic.getLayoutParams();
//        imc.width = (int) (nameLabelHeight / 3 * 2);
//        imc.height = (int) (nameLabelHeight / 3 * 2);
//        imc.rightMargin = (int) nameLabelHeight;
//        it.img_mic.setLayoutParams(imc);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) it.sf_video.getLayoutParams();
        int width = rel_wb_containerLayoutParams.width / x;
        int height = rel_wb_containerLayoutParams.height / y;

        if (height * 4 / 3 <= width) {
            sf_videoParam.width = height * 4 / 3;
            sf_videoParam.height = height;
        } else {
            sf_videoParam.width = width;
            sf_videoParam.height = width * 3 / 4;
        }
        sf_videoParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        it.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        it.sf_video.setLayoutParams(sf_videoParam);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) it.lin_name_label.getLayoutParams();
        nameparam.width = rel_wb_containerLayoutParams.width / x;
        it.lin_name_label.setLayoutParams(nameparam);
        /*it.lin_name_label.setVisibility(View.INVISIBLE);*/

        //奖杯图标
        FrameLayout.LayoutParams icon_gif_par = (FrameLayout.LayoutParams) it.icon_gif.getLayoutParams();
        icon_gif_par.height = (int) nameLabelHeight;
        icon_gif_par.width = (int) nameLabelHeight;
        it.icon_gif.setLayoutParams(icon_gif_par);

        //奖杯数量
        FrameLayout.LayoutParams txt_gift_par = (FrameLayout.LayoutParams) it.txt_gift_num.getLayoutParams();
        txt_gift_par.height = (int) (nameLabelHeight / 4 * 3);
        it.txt_gift_num.setLayoutParams(txt_gift_par);
        it.txt_gift_num.setPadding((int) (nameLabelHeight + 5), 0, (int) nameLabelHeight / 3, 0);


//        LinearLayout.LayoutParams txt_name_par = (LinearLayout.LayoutParams) it.txt_name.getLayoutParams();
//        txt_name_par.leftMargin = (int) nameLabelHeight;
//        txt_name_par.rightMargin = it.lin_gift.getWidth() + (int) nameLabelHeight + 16;
//        it.txt_name.setLayoutParams(txt_name_par);
    }


    /***
     *
     * @param it
     * @param rel_wb_container   分屏时 13路视频老师框大小
     * @param x
     * @param y
     */
    public static void videoThirteenSize(VideoItem it, RelativeLayout rel_wb_container, int x, int y, double nameLabelHeight) {

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.width = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4;
        relparam.height = rel_wb_container.getHeight() / y;
        it.parent.setLayoutParams(relparam);

        RelativeLayout.LayoutParams backparam = (RelativeLayout.LayoutParams) it.re_background.getLayoutParams();
        backparam.width = relparam.width;
        backparam.height = relparam.height;
        it.re_background.setLayoutParams(backparam);

        RelativeLayout.LayoutParams linparam = (RelativeLayout.LayoutParams) it.rel_video_label.getLayoutParams();
        linparam.width = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4;
        linparam.height = (int) (rel_wb_container.getHeight() / y - nameLabelHeight);
        it.rel_video_label.setLayoutParams(linparam);

        LinearLayout.LayoutParams imc = (LinearLayout.LayoutParams) it.img_mic.getLayoutParams();
        imc.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        imc.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        it.img_mic.setLayoutParams(imc);

        RelativeLayout.LayoutParams imhand = (RelativeLayout.LayoutParams) it.img_hand.getLayoutParams();
        imhand.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imhand.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        it.img_hand.setLayoutParams(imhand);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) it.sf_video.getLayoutParams();
        /*sf_videoParam.width = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4;
        sf_videoParam.height = rel_wb_container.getHeight() / y;*/
        it.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        it.sf_video.setLayoutParams(sf_videoParam);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) it.lin_name_label.getLayoutParams();
        nameparam.width = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4;
        nameparam.height = (int) nameLabelHeight;
        it.lin_name_label.setLayoutParams(nameparam);
    }

    /***
     * @param it
     * @param printWidth
     * @param printHeight
     * @param nameLabelHeight   13路视频移动大小
     */
    public static void moveVideoSize(VideoItemToMany it, double printWidth, double printHeight, double nameLabelHeight) {

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.width = (int) printWidth;
        relparam.height = (int) printHeight;
        it.parent.setLayoutParams(relparam);

        RelativeLayout.LayoutParams backparam = (RelativeLayout.LayoutParams) it.re_background.getLayoutParams();
        backparam.width = relparam.width;
        backparam.height = relparam.height;
        it.re_background.setLayoutParams(backparam);

        RelativeLayout.LayoutParams linparam = (RelativeLayout.LayoutParams) it.rel_video_label.getLayoutParams();
        linparam.width = (int) printWidth;
        linparam.height = (int) printHeight;
        it.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) it.sf_video.getLayoutParams();
        sf_videoParam.width = (int) printWidth;
        sf_videoParam.height = (int) printHeight;
        it.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        it.sf_video.setLayoutParams(sf_videoParam);

        //话筒图标
        LinearLayout.LayoutParams imc1 = (LinearLayout.LayoutParams) it.img_mic.getLayoutParams();
        imc1.height = (int) (backparam.height * ((double) 40 / (double) 250));
        imc1.width = (int) (backparam.height * ((double) 40 / (double) 250));
        it.img_mic.setLayoutParams(imc1);

        //音量进度条
        LinearLayout.LayoutParams volume1 = (LinearLayout.LayoutParams) it.volume.getLayoutParams();
        volume1.height = (int) (backparam.height * ((double) 40 / (double) 250));
        volume1.width = (int) (backparam.height * ((double) 55 / (double) 250));
        it.volume.setLayoutParams(volume1);

        //举手
        LinearLayout.LayoutParams imhand = (LinearLayout.LayoutParams) it.img_hand.getLayoutParams();
        imhand.height = (int) (printHeight * ((double) 40 / (double) 250));
        imhand.width = (int) (printHeight * ((double) 40 / (double) 250));
        it.img_hand.setLayoutParams(imhand);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) it.lin_name_label.getLayoutParams();
        nameparam.width = (int) printWidth;
        nameparam.height = (int) nameLabelHeight;
        it.lin_name_label.setLayoutParams(nameparam);

        //教室用户昵称
        LinearLayout.LayoutParams txt_name_par = (LinearLayout.LayoutParams) it.txt_name.getLayoutParams();
        txt_name_par.height = LinearLayout.LayoutParams.MATCH_PARENT;
        txt_name_par.leftMargin = 4;
        txt_name_par.gravity = Gravity.CENTER_VERTICAL;
        it.txt_name.setLayoutParams(txt_name_par);
    }

}
