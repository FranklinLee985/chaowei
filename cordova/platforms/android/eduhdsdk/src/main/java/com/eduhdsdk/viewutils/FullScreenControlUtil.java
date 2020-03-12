package com.eduhdsdk.viewutils;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.classroomsdk.common.RoomControler;
import com.eduhdsdk.R;
import com.eduhdsdk.ui.holder.TKBaseRootHolder;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import org.tkwebrtc.RendererCommon;
import org.tkwebrtc.SurfaceViewRenderer;

/**
 * Created by fucc on 2018/11/16.
 */

public class FullScreenControlUtil {

    /**
     * 控制画中画显示隐藏逻辑
     *
     * @param rel_fullscreen_videoitem 小窗口父布局
     * @param roomUser                 显示用户
     * @param isShow                   是否显示
     */
    public static void changeFullSreenSate(RelativeLayout rel_fullscreen_videoitem, final RoomUser roomUser, boolean isShow) {
        if (rel_fullscreen_videoitem == null) {
            return;
        }
        final SurfaceViewRenderer fullscreen_sf_video = (SurfaceViewRenderer) rel_fullscreen_videoitem.findViewById(R.id.fullscreen_sf_video);
        ImageView fullscreen_bg_video_back = (ImageView) rel_fullscreen_videoitem.findViewById(R.id.fullscreen_bg_video_back);
        ImageView fullscreen_img_video_back = (ImageView) rel_fullscreen_videoitem.findViewById(R.id.fullscreen_img_video_back);
        if (isShow && RoomControler.isFullScreenVideo()) {
            if (roomUser != null && !roomUser.peerId.isEmpty()) {
                rel_fullscreen_videoitem.setVisibility(View.VISIBLE);
                //有视频
                if (roomUser.publishState > 1 && roomUser.publishState < 4 && !roomUser.disablevideo
                        && roomUser.hasVideo) {
                    fullscreen_sf_video.setVisibility(View.VISIBLE);
                    fullscreen_bg_video_back.setVisibility(View.INVISIBLE);
                    fullscreen_img_video_back.setVisibility(View.INVISIBLE);
                    TKRoomManager.getInstance().playVideo(roomUser.peerId, fullscreen_sf_video,
                            RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);

                    return;
                }
            }
        }
        //逻辑改动没有视频就隐藏画中画
        //隐藏画中画窗口
        fullscreen_sf_video.setVisibility(View.INVISIBLE);
        fullscreen_img_video_back.setVisibility(View.INVISIBLE);
        fullscreen_bg_video_back.setVisibility(View.INVISIBLE);
        rel_fullscreen_videoitem.setVisibility(View.GONE);
    }

    //显示用户按了home键
    public static void changeInBackground(Context context, TKBaseRootHolder mHolder, boolean isShow, int role) {
        if (isShow) {
            mHolder.fullscreen_inback.setVisibility(View.VISIBLE);
            if (role == 0) {
                mHolder.fullscreen_inback_txt.setText(R.string.tea_background);
            } else if (role == 2) {
                mHolder.fullscreen_inback_txt.setText(R.string.stu_background);
            }
        } else {
            mHolder.fullscreen_inback.setVisibility(View.GONE);
        }
    }

    //显示用户按了home键
    public static void changeInBackground(RelativeLayout relativeLayout, boolean isShow, int role) {
        if (relativeLayout == null) {
            return;
        }
        RelativeLayout sufbackground = relativeLayout.findViewById(R.id.re_suf_background);
        TextView fullscreen_inback_txt = relativeLayout.findViewById(R.id.tv_inback);
        if (isShow) {
            sufbackground.setVisibility(View.VISIBLE);
            if (role == 0) {
                fullscreen_inback_txt.setText(R.string.tea_background);
            } else if (role == 2) {
                fullscreen_inback_txt.setText(R.string.stu_background);
            }
        } else {
            sufbackground.setVisibility(View.GONE);
        }
    }
}
