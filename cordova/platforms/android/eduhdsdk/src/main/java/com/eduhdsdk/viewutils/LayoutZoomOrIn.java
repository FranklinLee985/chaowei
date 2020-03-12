package com.eduhdsdk.viewutils;

import android.view.View;
import android.widget.RelativeLayout;

import com.eduhdsdk.ui.holder.VideoItemToMany;

import org.tkwebrtc.RendererCommon;

/**
 * Created by Administrator on 2017/12/11/011.
 */

public class LayoutZoomOrIn {

    public static void zoomMouldVideoItem(VideoItemToMany videoItem, double scale, RelativeLayout rel_students, View v_students, RelativeLayout rel_wb) {
        if (videoItem.parent.getHeight() * scale < rel_students.getHeight()
                && videoItem.parent.getHeight() * scale > v_students.getHeight()) {
            if (scale > 1) {
                scaleMouldVedioItem(videoItem, scale, rel_students, v_students, rel_wb);
            } else {
                narrowMouldVideoItem(videoItem, scale, rel_students, v_students);
            }
        }
    }

    public static void narrowMouldVideoItem(VideoItemToMany videoItem, double scale, RelativeLayout rel_students, View v_students) {
        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) videoItem.parent.getLayoutParams();
        relparam.topMargin = videoItem.parent.getTop() + (int) ((videoItem.parent.getHeight() - videoItem.parent.getHeight() * scale) / 2);
        relparam.leftMargin = (int) (videoItem.parent.getLeft() + (videoItem.parent.getWidth() - videoItem.parent.getWidth() * scale) / 2);

       if (videoItem.parent.getRight() == rel_students.getRight() || videoItem.parent.getRight() > rel_students.getRight()) {
            relparam.leftMargin = (int) (rel_students.getRight() - videoItem.parent.getLayoutParams().width);
        }

        relparam.width = (int) (videoItem.parent.getWidth() * scale);
        relparam.height = (int) (videoItem.parent.getHeight() * scale);
        videoItem.parent.setLayoutParams(relparam);

        RelativeLayout.LayoutParams linparam = (RelativeLayout.LayoutParams) videoItem.rel_video_label.getLayoutParams();
        linparam.width = (int) (videoItem.parent.getWidth() * scale);
//        linparam.height = (int) (videoItem.parent.getHeight() * scale - videoItem.lin_name_label.getHeight());
        linparam.height = (int) (videoItem.parent.getHeight() * scale);
        videoItem.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) videoItem.sf_video.getLayoutParams();
       /* sf_videoParam.width = (int) (videoItem.parent.getWidth() * scale);
        sf_videoParam.height = (int) (videoItem.parent.getHeight() * scale);*/
        sf_videoParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoItem.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        videoItem.sf_video.setLayoutParams(sf_videoParam);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) videoItem.lin_name_label.getLayoutParams();
        nameparam.width = (int) (videoItem.parent.getWidth() * scale);
        nameparam.height = videoItem.lin_name_label.getHeight();
        videoItem.lin_name_label.setLayoutParams(nameparam);
    }

    public static void scaleMouldVedioItem(VideoItemToMany videoItem, double scale, RelativeLayout rel_students, View v_students, RelativeLayout rel_wb) {

        int oldWidth = videoItem.parent.getWidth();
        int oldHeight = videoItem.parent.getHeight();
        double width = oldWidth * scale;
        double height = oldHeight * scale;

        //视频框大小不能大于课件区
        if (rel_wb.getLayoutParams().width > rel_wb.getLayoutParams().height) {
            if (height > rel_wb.getLayoutParams().height) {
                height = rel_wb.getLayoutParams().height;
                width = height * 4 / 3;
            }
        } else {
            if (width > rel_wb.getLayoutParams().width) {
                width = rel_wb.getLayoutParams().width;
                height = width * 3 / 4;
            }
        }

        int left = videoItem.parent.getLeft();
        int top = videoItem.parent.getTop();

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) videoItem.parent.getLayoutParams();

        relparam.topMargin = (int) (top - (height - oldHeight) / 2);

        if ((width - oldWidth) / 2 < left) {
            relparam.leftMargin = (int) (left - (width - oldWidth) / 2);
        } else {
            relparam.leftMargin = 0;
        }

        relparam.width = (int) width;
        relparam.height = (int) height;
        videoItem.parent.setLayoutParams(relparam);

        RelativeLayout.LayoutParams linparam = (RelativeLayout.LayoutParams) videoItem.rel_video_label.getLayoutParams();
        linparam.width = relparam.width;
        linparam.height = relparam.height;
        videoItem.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) videoItem.sf_video.getLayoutParams();
        sf_videoParam.width = (int) width;
        sf_videoParam.height = (int) height;
        sf_videoParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoItem.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        videoItem.sf_video.setLayoutParams(sf_videoParam);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) videoItem.lin_name_label.getLayoutParams();
        nameparam.width = relparam.width;
        nameparam.height = videoItem.lin_name_label.getHeight();
        videoItem.lin_name_label.setLayoutParams(nameparam);
    }

    public static void zoomMsgMouldVideoItem(VideoItemToMany videoItem, double scale, double printWidth, double printHeight, int maxHehiht) {

        if (printHeight * scale > maxHehiht) {
            scale = maxHehiht / printHeight;
        }

        videoItem.width = (int) (printWidth * scale);
        videoItem.height = (int) (printHeight * scale);
        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) videoItem.parent.getLayoutParams();
        relparam.width = (int) (printWidth * scale);
        relparam.height = (int) (printHeight * scale);
        videoItem.parent.setLayoutParams(relparam);

        RelativeLayout.LayoutParams linparam = (RelativeLayout.LayoutParams) videoItem.rel_video_label.getLayoutParams();
        linparam.width = (int) (printWidth * scale);
        linparam.height = (int) (printHeight * scale);
        videoItem.rel_video_label.setLayoutParams(linparam);

        if (videoItem.sf_video != null) {
            RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) videoItem.sf_video.getLayoutParams();
            sf_videoParam.width = (int) (printWidth * scale);
            sf_videoParam.height = (int) (printHeight * scale);
            sf_videoParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
            videoItem.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            videoItem.sf_video.setLayoutParams(sf_videoParam);
        }

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) videoItem.lin_name_label.getLayoutParams();
        nameparam.width = (int) (printWidth * scale);
        nameparam.height = videoItem.lin_name_label.getHeight();
        videoItem.lin_name_label.setLayoutParams(nameparam);

        RelativeLayout.LayoutParams re_backgroud = (RelativeLayout.LayoutParams) videoItem.re_background.getLayoutParams();
        re_backgroud.width = (int) (printWidth * scale);
        re_backgroud.height = (int) (printHeight * scale);
        videoItem.re_background.setLayoutParams(re_backgroud);
    }

    public static void layoutVideo(VideoItemToMany it, int x, int y) {
        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.topMargin = y;
        relparam.leftMargin = x;
        relparam.bottomMargin = 0;
        relparam.rightMargin = 0;
        relparam.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relparam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        it.parent.setLayoutParams(relparam);
    }

    public static void layoutMouldVideo(VideoItemToMany it, int x, int y, int bottom) {
        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
//        relparam.bottomMargin = bottom;
        relparam.topMargin = y;
        relparam.leftMargin = x;
        relparam.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relparam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        it.parent.setLayoutParams(relparam);
    }
}
