package com.eduhdsdk.ui.holder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eduhdsdk.BuildConfig;
import com.eduhdsdk.R;
import com.eduhdsdk.room.RoomClient;
import com.eduhdsdk.ui.AutoFitTextView;
import com.eduhdsdk.ui.VolumeView;

import org.tkwebrtc.EglBase;
import org.tkwebrtc.RendererCommon;
import org.tkwebrtc.SurfaceViewRenderer;


/**
 * Created by Administrator on 2017/5/22.
 */

public class VideoItemToMany {

    public RelativeLayout parent;
    public SurfaceViewRenderer sf_video;
    public ImageView icon_gif;
    public ImageView img_mic;
    public VolumeView volume;
    public ImageView img_pen;
    public RelativeLayout bg_img_pen;
    public ImageView img_hand;
    public AutoFitTextView txt_name;
    public AutoFitTextView txt_gift_num;
    public RelativeLayout rel_group;
    public ImageView img_video_back;
    public ImageView bg_video_back;
    public FrameLayout lin_gift;
    public LinearLayout lin_name_label;
    public String peerid = "";
    public int role = -1;
    public RelativeLayout rel_video_label;
    public RelativeLayout re_background;
    public TextView tv_home;
    public EglBase eglBase;
    public View view_choose_selected;//选中框背景
    public boolean canMove = false;
    public boolean isMoved = false;
    public boolean isSplitScreen = false;
    public int height = -1;
    public int width = -1;
    public boolean doubleSmallVideo;
    //预创建 下是否已使用
    public boolean isShow = false;

    //当只显示老师和学生时 判断是否显示
    public boolean isOnlyShowTeachersAndVideos = true;

    public VideoItemToMany(Activity activity) {
        initView(activity);
    }

    private void initView(Activity activity) {
        parent = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.tk_item_video_frame_one_to_many, null);

        sf_video = parent.findViewById(R.id.sf_video);
        try {
            eglBase = RoomClient.getInstance().getPreEgl();
            sf_video.init(eglBase.getEglBaseContext(), null);
            RoomClient.getInstance().setPreEglMap(eglBase, true);
        } catch (RuntimeException e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        sf_video.setZOrderMediaOverlay(true);
        sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        //视频框根布局
        rel_video_label = (RelativeLayout) parent.findViewById(R.id.rel_video_label);
        //视频框layout
        rel_group = (RelativeLayout) parent.findViewById(R.id.rel_group);
        //视频占位图
        img_video_back = (ImageView) parent.findViewById(R.id.img_video_back);
        //视频背景色
        bg_video_back = (ImageView) parent.findViewById(R.id.bg_video_back);
        //声音图标
        img_mic = (ImageView) parent.findViewById(R.id.img_mic);
        //音量条
        volume = (VolumeView) parent.findViewById(R.id.volume);
        //画笔图标
        img_pen = (ImageView) parent.findViewById(R.id.img_pen);
        bg_img_pen = (RelativeLayout) parent.findViewById(R.id.bg_img_pen);
        //举手图标
        img_hand = (ImageView) parent.findViewById(R.id.img_hand_up);
        //昵称
        txt_name = (AutoFitTextView) parent.findViewById(R.id.txt_name);
        //礼物layout
        lin_gift = parent.findViewById(R.id.lin_gift);
        //礼物图标
        icon_gif = (ImageView) parent.findViewById(R.id.icon_gif);
        //礼物数量
        txt_gift_num = (AutoFitTextView) parent.findViewById(R.id.txt_gift_num);
        //底部阴影框
        lin_name_label = parent.findViewById(R.id.lin_name_label);
        //视频框选中边框
        view_choose_selected = (View) parent.findViewById(R.id.view_choose_selected);
        re_background = (RelativeLayout) parent.findViewById(R.id.re_background);
        tv_home = (TextView) parent.findViewById(R.id.tv_home);
    }

    public String getPeerid() {
        return peerid;
    }

    public void setOnlyShowTeachersAndVideos(boolean onlyShowTeachersAndVideos) {
        isOnlyShowTeachersAndVideos = onlyShowTeachersAndVideos;
    }
}
