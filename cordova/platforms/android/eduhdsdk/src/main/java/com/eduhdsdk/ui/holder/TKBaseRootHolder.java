package com.eduhdsdk.ui.holder;

import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eduhdsdk.R;

import org.tkwebrtc.SurfaceViewRenderer;

import pl.droidsonroids.gif.GifImageView;

/**
 * date 2018/11/19
 * version
 * describe  View持有基类
 *
 * @author hxd
 */
public class TKBaseRootHolder {

    protected View mRootView;

    //******   loading界面
    public RelativeLayout re_loading;
    // loading图片
    public ImageView loadingImageView;
    // loading文字
    public TextView tv_load;

    //****************************************   顶部工具条
    public RelativeLayout rel_tool_bar;
    //******   左边返回按钮 和 上课时间 layout
    public LinearLayout ll_top;
    //退出按钮
    public ImageView img_back;
    //上课
    public TextView txt_class_begin;
    //举手
    public TextView txt_hand_up;
    //时 分冒号
    public TextView txt_mao_01;
    //分 秒冒号
    public TextView txt_mao_02;
    //时
    public TextView txt_hour;
    //分
    public TextView txt_min;
    //秒
    public TextView txt_ss;
    //wifi layout
    public LinearLayout lin_wifi;
    //wifi 图标
    public ImageView img_wifi;
    //wifi status
    public TextView txt_wifi_status;
    //wifi down三角图标
    public ImageView img_wifi_down;

    //******   右边工具栏layout
    public LinearLayout re_top_right;
    //花名册layout
    public RelativeLayout rl_member_list;
    //花名册
    public CheckBox cb_member_list;
    //花名册小红点（举手指示）
    public ImageView iv_hand;
    //举手文本提示
    public TextView hand_txt;

    //媒体库
    public CheckBox cb_file_person_media_list;
    //工具箱
    public CheckBox cb_tool_case;
    //布局控制
    public CheckBox cb_tool_layout;
    //全体控制
    public CheckBox cb_control;
    //选相册
    public CheckBox cb_choose_photo;
    //翻转摄像头
    public ImageView flipCamera;

    //******   mp3 播放界面
    public RelativeLayout rel_control_layout;
    public FrameLayout lin_audio_control;
    //***  mp3 mp4 图标layout
    public FrameLayout fl_play_disk;
    //mp3 mp4 图标
    public GifImageView img_disk;
    //播放暂停按钮
    public ImageView img_play_mp3;
    //***  进度条layout
    public LinearLayout lin_audio_seek;
    //文件名字
    public TextView txt_mp3_name;
    //播放时长/音频时长
    public TextView txt_mp3_time;
    //播放进度条
    public SeekBar sek_mp3;
    //音量图标
    public ImageView img_voice_mp3;
    //音量进度条
    public SeekBar sek_voice_mp3;
    //关闭按钮
    public ImageView img_close_mp3;

    //******   聊天控制按钮界面
    public RelativeLayout lin_bottom_chat;
    //留言板layout
    public RelativeLayout rl_message;
    //留言板
    public CheckBox cb_message;
    //未读消息数
    public TextView tv_no_read_message_number;
    //打开聊天输入框
    public ImageView iv_open_input;
    //全体禁言
    public CheckBox cb_choose_shut_chat;
    //聊天listview
    public ListView lv_chat_list;

    //******   白板全屏右下角视频界面
    public RelativeLayout rel_fullscreen_videoitem;
    //视频SurfaceView
    public SurfaceViewRenderer fullscreen_sf_video;
    //视频背景色
    public ImageView fullscreen_bg_video_back;
    //视频占位图
    public ImageView fullscreen_img_video_back;
    //视频在后台占位
    public RelativeLayout fullscreen_inback;
    //视频占位图提示
    public TextView fullscreen_inback_txt;

    //****************************************   回放容器
    public ViewStub vs_play_back;
    public RelativeLayout re_play_back;
    //******   回放titleBar
    public RelativeLayout rel_play_back_bar;
    //返回
    public ImageView img_play_back_out;
    //回放媒体名字
    public TextView tv_back_name;

    //白板工具条
    public LinearLayout tools_include;
    //形状工具
    public LinearLayout tools_form_all;
    //横线
    public ImageView tools_bottom_line;
    //顶部
    public LinearLayout tools_top;
    //底部
    public ImageView iv_top_arrow;


    //工具
    public ImageView iv_default;
    public ImageView iv_pen;
    public ImageView iv_font;
    public ImageView iv_form;
    public ImageView iv_eraser;
    public View view1;
    public View view2;
    public View view7;
    public View view8;

    public View tools_form_front, tools_form_after;

    //翻页工具
    public LinearLayout pages_include_ll;
    public LinearLayout page_include;
    //左右
    public ImageView iv_left;
    public ImageView iv_right;
    //页码选择箭头
    public ImageView page_iv_arrow;
    //页码
    public TextView tv_nums;
    //放大
    public ImageView iv_large;
    //缩小
    public ImageView iv_small;
    //全屏
    public ImageView iv_full;
    //ppt标注
    public ImageView iv_remark;
    public LinearLayout ll_remark;
    public TextView tv_remark;

    //播放界面
    public FrameLayout video_container;
    public CheckBox eye_protection;


    protected void findView() {
        //******   loading界面
        re_loading = (RelativeLayout) mRootView.findViewById(R.id.re_laoding);
        // loading图片
        loadingImageView = (ImageView) re_loading.findViewById(R.id.loadingImageView);
        // loading文字
        tv_load = (TextView) re_loading.findViewById(R.id.tv_load);


        //****************************************   顶部工具条
        rel_tool_bar = (RelativeLayout) mRootView.findViewById(R.id.title_bar);
        //******   左边返回按钮 和 上课时间 layout
        ll_top = (LinearLayout) rel_tool_bar.findViewById(R.id.ll_top);
        //退出按钮
        img_back = (ImageView) rel_tool_bar.findViewById(R.id.img_back);
        //上课
        txt_class_begin = (TextView) rel_tool_bar.findViewById(R.id.txt_class_begin);
        //举手
        txt_hand_up = (TextView) rel_tool_bar.findViewById(R.id.txt_hand_up);
        //护眼
        eye_protection = rel_tool_bar.findViewById(R.id.eye_protection);
        //时 分冒号
        txt_mao_01 = (TextView) rel_tool_bar.findViewById(R.id.txt_mao_01);
        //分 秒冒号
        txt_mao_02 = (TextView) rel_tool_bar.findViewById(R.id.txt_mao_02);
        //时
        txt_hour = (TextView) rel_tool_bar.findViewById(R.id.txt_hour);
        //分
        txt_min = (TextView) rel_tool_bar.findViewById(R.id.txt_min);
        //秒
        txt_ss = (TextView) rel_tool_bar.findViewById(R.id.txt_ss);
        //wifi layout
        lin_wifi = rel_tool_bar.findViewById(R.id.lin_wifi);
        //wifi 图标
        img_wifi = rel_tool_bar.findViewById(R.id.img_wifi);
        //wifi 状态
        txt_wifi_status = rel_tool_bar.findViewById(R.id.txt_wifi_status);
        //wifi 三角标
        img_wifi_down = rel_tool_bar.findViewById(R.id.img_wifi_down);
        //******   右边工具栏layout
        re_top_right = (LinearLayout) rel_tool_bar.findViewById(R.id.re_top_right);
        //花名册layout
        rl_member_list = (RelativeLayout) rel_tool_bar.findViewById(R.id.rl_member_list);
        //花名册
        cb_member_list = (CheckBox) rel_tool_bar.findViewById(R.id.cb_member_list);
        //花名册小红点（举手指示）
        iv_hand = (ImageView) rel_tool_bar.findViewById(R.id.iv_hand);
        hand_txt = rel_tool_bar.findViewById(R.id.txt_hand_content);
        //媒体库
        cb_file_person_media_list = (CheckBox) rel_tool_bar.findViewById(R.id.cb_file_person_media_list);
        //工具箱
        cb_tool_case = (CheckBox) rel_tool_bar.findViewById(R.id.cb_tool_case);
        //布局控制
        cb_tool_layout = (CheckBox) rel_tool_bar.findViewById(R.id.cb_tool_layout);
        //全体控制
        cb_control = (CheckBox) rel_tool_bar.findViewById(R.id.cb_control);
        //选相册
        cb_choose_photo = (CheckBox) rel_tool_bar.findViewById(R.id.cb_choose_photo);
        //翻转摄像头
        flipCamera = (ImageView) rel_tool_bar.findViewById(R.id.flip_camera);


        //******   mp3 播放界面
        rel_control_layout = (RelativeLayout) mRootView.findViewById(R.id.rel_control_layout);
        lin_audio_control = mRootView.findViewById(R.id.lin_audio_control);
        //***  mp3 mp4 图标layout
        fl_play_disk = (FrameLayout) lin_audio_control.findViewById(R.id.fl_play_disk);
        //mp3 mp4 图标
        img_disk = (GifImageView) lin_audio_control.findViewById(R.id.img_disk);
        //播放暂停按钮
        img_play_mp3 = (ImageView) lin_audio_control.findViewById(R.id.img_play);
        //***  进度条layout
        lin_audio_seek = (LinearLayout) mRootView.findViewById(R.id.lin_audio_seek);
        //文件名字
        txt_mp3_name = (TextView) lin_audio_control.findViewById(R.id.txt_media_name);
        //播放时长/音频时长
        txt_mp3_time = (TextView) lin_audio_control.findViewById(R.id.txt_media_time);
        //播放进度条
        sek_mp3 = (SeekBar) lin_audio_control.findViewById(R.id.sek_media);
        //音量图标
        img_voice_mp3 = (ImageView) lin_audio_control.findViewById(R.id.img_media_voice);
        //音量进度条
        sek_voice_mp3 = (SeekBar) lin_audio_control.findViewById(R.id.sek_media_voice);
        //关闭按钮
        img_close_mp3 = lin_audio_control.findViewById(R.id.img_close_mp3);

        //******   聊天控制按钮界面
        lin_bottom_chat = mRootView.findViewById(R.id.lin_bottom_chat);
        //留言板layout
        rl_message = (RelativeLayout) lin_bottom_chat.findViewById(R.id.rl_message);
        //留言板
        cb_message = (CheckBox) lin_bottom_chat.findViewById(R.id.cb_message);
        //未读消息数
        tv_no_read_message_number = (TextView) lin_bottom_chat.findViewById(R.id.tv_no_read_message_number);
        //打开聊天输入框
        iv_open_input = (ImageView) lin_bottom_chat.findViewById(R.id.iv_open_input);
        //全体禁言
        cb_choose_shut_chat = (CheckBox) lin_bottom_chat.findViewById(R.id.cb_choose_shut_chat);
        //聊天列表
        lv_chat_list = lin_bottom_chat.findViewById(R.id.lv_chat);

        //******   白板全屏右下角视频界面
        rel_fullscreen_videoitem = (RelativeLayout) mRootView.findViewById(R.id.rel_fullscreen_videoitem);
        //视频SurfaceView
        fullscreen_sf_video = (SurfaceViewRenderer) rel_fullscreen_videoitem.findViewById(R.id.fullscreen_sf_video);
        //视频背景色
        fullscreen_bg_video_back = (ImageView) rel_fullscreen_videoitem.findViewById(R.id.fullscreen_bg_video_back);
        //视频占位图
        fullscreen_img_video_back = (ImageView) rel_fullscreen_videoitem.findViewById(R.id.fullscreen_img_video_back);
        //后台占位图
        fullscreen_inback = rel_fullscreen_videoitem.findViewById(R.id.re_suf_background);
        //后台占位文字提示
        fullscreen_inback_txt = rel_fullscreen_videoitem.findViewById(R.id.tv_inback);

        //****************************************   回放容器
        vs_play_back=mRootView.findViewById(R.id.vs_play_back);


        //白板工具栏
        tools_include = mRootView.findViewById(R.id.tools_include);
        tools_form_all = tools_include.findViewById(R.id.ll_tools_all);
        tools_bottom_line = tools_include.findViewById(R.id.tools_bottom_line);
        tools_top = tools_include.findViewById(R.id.tools_top);
        iv_default = tools_include.findViewById(R.id.tool_default);
        iv_pen = tools_include.findViewById(R.id.tools_pen);
        iv_font = tools_include.findViewById(R.id.tools_font);
        iv_form = tools_include.findViewById(R.id.tools_form);
        iv_eraser = tools_include.findViewById(R.id.tools_eraser);
        iv_top_arrow = tools_include.findViewById(R.id.iv_top_arrows);
        view1 = tools_include.findViewById(R.id.view1);
        view2 = tools_include.findViewById(R.id.view2);
        view7 = tools_include.findViewById(R.id.view7);
        view8 = tools_include.findViewById(R.id.view8);


        //翻页工具栏
        pages_include_ll = mRootView.findViewById(R.id.pages_include_ll);
        page_include = mRootView.findViewById(R.id.pages_include);
        iv_left = page_include.findViewById(R.id.page_iv_left);
        tv_nums = page_include.findViewById(R.id.page_tv_nums);
        iv_right = page_include.findViewById(R.id.page_iv_right);
        page_iv_arrow = page_include.findViewById(R.id.page_iv_arrow);
        iv_large = page_include.findViewById(R.id.page_iv_large);
        iv_small = page_include.findViewById(R.id.page_iv_small);
        iv_full = page_include.findViewById(R.id.page_iv_full_screen);
        iv_remark = page_include.findViewById(R.id.page_iv_remark);

        //ppt备注
        ll_remark = mRootView.findViewById(R.id.remark_ll);
        tv_remark = mRootView.findViewById(R.id.remark_tv);

        //播放界面
        video_container = (FrameLayout) mRootView.findViewById(R.id.video_container);

    }
}
