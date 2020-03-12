package com.eduhdsdk.ui;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.fragment.FaceShareFragment;
import com.classroomsdk.fragment.WBFragment;
import com.classroomsdk.interfaces.FragmentUserVisibleHint;
import com.classroomsdk.interfaces.IWBStateCallBack;
import com.classroomsdk.manage.ProLoadingDoc;
import com.classroomsdk.manage.WBSession;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.tools.ScreenScale;
import com.classroomsdk.viewUi.DownloadProgressView;
import com.eduhdsdk.BuildConfig;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.ChatListAdapter;
import com.eduhdsdk.adapter.FileExpandableListAdapter;
import com.eduhdsdk.adapter.MediaExpandableListAdapter;
import com.eduhdsdk.adapter.MemberListAdapter;
import com.eduhdsdk.comparator.PeerIDComparator;
import com.eduhdsdk.entity.ChatData;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.entity.MoveVideoInfo;
import com.eduhdsdk.interfaces.TranslateCallback;
import com.eduhdsdk.message.SendingSignalling;
import com.eduhdsdk.room.RoomCheck;
import com.eduhdsdk.room.RoomClient;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomDeviceSet;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomOperation;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.room.RoomVariable;
import com.eduhdsdk.room.SetRoomInfor;
import com.eduhdsdk.toolcase.LayoutPopupWindow;
import com.eduhdsdk.toolcase.ToolCaseMgr;
import com.eduhdsdk.toolcase.ToolsPopupWindow;
import com.eduhdsdk.tools.AnimationUtil;
import com.eduhdsdk.tools.HttpTextView;
import com.eduhdsdk.tools.KeyBoardUtil;
import com.eduhdsdk.tools.MonitorService;
import com.eduhdsdk.tools.PhotoUtils;
import com.eduhdsdk.tools.ResourceSetManage;
import com.eduhdsdk.tools.ShowTrophyUtil;
import com.eduhdsdk.tools.SkinTool;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.tools.Translate;
import com.eduhdsdk.tools.VideoTtemTouchEvent;
import com.eduhdsdk.ui.holder.OneToManyRootHolder;
import com.eduhdsdk.ui.holder.VideoItemToMany;
import com.eduhdsdk.viewutils.AllActionUtils;
import com.eduhdsdk.viewutils.CommonUtil;
import com.eduhdsdk.viewutils.CoursePopupWindowUtils;
import com.eduhdsdk.viewutils.EyeProtectionUtil;
import com.eduhdsdk.viewutils.FullScreenControlUtil;
import com.eduhdsdk.viewutils.InputWindowPop;
import com.eduhdsdk.viewutils.LayoutZoomOrIn;
import com.eduhdsdk.viewutils.MemberListPopupWindowUtils;
import com.eduhdsdk.viewutils.MoveFullBoardUtil;
import com.eduhdsdk.viewutils.OneToManyFreeLayoutUtil;
import com.eduhdsdk.viewutils.OnetoManyLayoutUtil;
import com.eduhdsdk.viewutils.PlayBackSeekPopupWindow;
import com.eduhdsdk.viewutils.PlaybackControlUtils;
import com.eduhdsdk.viewutils.SendGiftPopUtils;
import com.eduhdsdk.viewutils.UploadPhotoPopupWindowUtils;
import com.eduhdsdk.viewutils.VideoTtemLayoutUtils;
import com.eduhdsdk.viewutils.WifiStatusPop;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;
import com.talkcloud.room.TkVideoStatsReport;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.tkwebrtc.EglBase;
import org.tkwebrtc.RendererCommon;
import org.tkwebrtc.SurfaceViewRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import skin.support.annotation.Skinable;

@Skinable
public class OneToManyActivity extends TKBaseActivity implements View.OnClickListener, IWBStateCallBack,
        CompoundButton.OnCheckedChangeListener, InputWindowPop.InputSelectImageListener,
        CoursePopupWindowUtils.PopupWindowClick, AllActionUtils.AllPopupWindowClick,
        MemberListPopupWindowUtils.CloseMemberListWindow, LayoutPopupWindow.SwitchLayout,
        TranslateCallback, UploadPhotoPopupWindowUtils.UploadPhotoPopupWindowClick, FragmentUserVisibleHint {

    //MP3音频播放图标
    private GifDrawable gifDrawable;

    private RelativeLayout.LayoutParams fullscreen_video_param;

    private ArrayList<VideoItemToMany> videoItems = new ArrayList<VideoItemToMany>();
    private ArrayList<VideoItemToMany> notMoveVideoItems = new ArrayList<VideoItemToMany>();
    private ArrayList<VideoItemToMany> movedVideoItems = new ArrayList<VideoItemToMany>();
    private MemberListAdapter memberListAdapter;
    private FileExpandableListAdapter fileListAdapter;
    private MediaExpandableListAdapter mediaListAdapter;
    private ChatListAdapter chlistAdapter;

    private WBFragment wbFragment;
    private FragmentManager mediafragmentManager;
    private FragmentTransaction ft;
    private VideoFragment videofragment;

    //白板放大或缩小 true放大  false缩小
    private boolean isZoom = false;
    private boolean isBackApp = false;
    private Map<String, Object> mediaAttrs;
    private String mediaPeerId;

    private double vol = 0.5;
    private boolean isMediaMute = false;

    private ScreenFragment screenFragment;
    private MovieFragment movieFragment;

    private JSONObject videoarr = null;
    private Iterator<String> sIterator = null;

    private Map<String, MoveVideoInfo> stuMoveInfoMap = new HashMap<String, MoveVideoInfo>();
    private Map<String, Float> scalemap = new HashMap<String, Float>();
    private boolean isPauseLocalVideo = false;
    private boolean isOpenCamera = false;

    private ArrayList<String> screenID = new ArrayList<>();

    private double printWidth, printHeight, nameLabelHeight;
    private AllActionUtils allActionUtils;
    private SendGiftPopUtils sendGiftPopUtils;

    private boolean isFrontCamera = true;

    OneToManyRootHolder mRootHolder;
    //工具条
    private ToolsView toolsView;
    //翻页
    private PagesView mPagesView;

    //举手3秒倒计时记时
    int showHandTxtTime = -1;
    private PlaybackControlUtils playbackControlUtils;//回放进度条的显示隐藏工具类
    private View view;

    //网络状态显示
    public WifiStatusPop wifiStatusPop;

    public static List<VideoItemToMany> videoItemToManies = new ArrayList<>();
    private Fragment mWb_proto;

    //主讲视频主讲位置唯一标注
    private String soleOnlyId = "only";
    //点击视频框显示的popwindow的用户id
    private String solepopwindowPid;
    //预加载时是否点击跳过
    private boolean isJumpOver = false;
    public RelativeLayout.LayoutParams tool_bar_param;

    // 全屏大图
    private FullScreenImageView mFullScreenImageView;

    private boolean isInflated;
    private View inflate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = LayoutInflater.from(this).inflate(R.layout.tk_activity_one_to_many, null);
        setContentView(view);
        mRootHolder = new OneToManyRootHolder(view);
        mRootHolder.vs_play_back.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                isInflated = true;
            }
        });
        //初始化数据
        initData();
        //绑定监听事件
        bindListener();
    }

    /***
     *  设置控件大小
     */
    private void setViewSize() {
        //标准视频框的大小
        printWidth = (wid - 8 * 8) / 7;
        printHeight = (int) ((printWidth * (double) hid_ratio / (double) wid_ratio));
        nameLabelHeight = (int) (printWidth * ((double) 45 / (double) 240));

        //课件全屏右下角界面大小
        fullscreen_video_param = new RelativeLayout.LayoutParams(0, 0);
        fullscreen_video_param.width = (wid - 8 * 8) / 7;
        fullscreen_video_param.height = (wid - 8 * 8) / 7 * 3 / 4 + 16;
        fullscreen_video_param.rightMargin = KeyBoardUtil.dp2px(OneToManyActivity.this, 8);
        fullscreen_video_param.bottomMargin = KeyBoardUtil.dp2px(OneToManyActivity.this, 8);

        if (mRootHolder.fullscreen_inback != null) {
            mRootHolder.fullscreen_inback.setLayoutParams(fullscreen_video_param);
        }
        if (mRootHolder.fullscreen_sf_video != null) {
            mRootHolder.fullscreen_sf_video.setLayoutParams(fullscreen_video_param);
        }
        if (mRootHolder.fullscreen_bg_video_back != null) {
            mRootHolder.fullscreen_bg_video_back.setLayoutParams(fullscreen_video_param);
        }
        if (mRootHolder.fullscreen_img_video_back != null) {
            mRootHolder.fullscreen_img_video_back.setLayoutParams(fullscreen_video_param);
        }
        if (mRootHolder.fullscreen_inback_txt != null) {
            mRootHolder.fullscreen_inback_txt.setLayoutParams(fullscreen_video_param);
        }

        //MP3播放进度条界面
        FrameLayout.LayoutParams audioParam = (FrameLayout.LayoutParams) mRootHolder.lin_audio_seek.getLayoutParams();
        audioParam.width = mScreenValueWidth * 4 / 11;
        audioParam.height = hid * 29 / 194;
        mRootHolder.lin_audio_seek.setLayoutParams(audioParam);
        mRootHolder.lin_audio_seek.setPadding(audioParam.height * 24 / 25, 0, 0, 0);

        //MP3播放图标layout
        FrameLayout.LayoutParams imgDiskParam = (FrameLayout.LayoutParams) mRootHolder.fl_play_disk.getLayoutParams();
        imgDiskParam.height = audioParam.height * 24 / 25;
        imgDiskParam.width = imgDiskParam.height;
        mRootHolder.fl_play_disk.setLayoutParams(imgDiskParam);

        //顶部工具栏
        tool_bar_param = (RelativeLayout.LayoutParams) mRootHolder.rel_tool_bar.getLayoutParams();
        tool_bar_param.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        tool_bar_param.height = (int) (wid / 7 * 3 / 4 * 0.4);
        mRootHolder.rel_tool_bar.setLayoutParams(tool_bar_param);
        if (mRootHolder.rel_play_back_bar != null) {
            mRootHolder.rel_play_back_bar.setLayoutParams(tool_bar_param);
        }

        //MP3播放
        RelativeLayout.LayoutParams relControlLayout = (RelativeLayout.LayoutParams) mRootHolder.rel_control_layout.getLayoutParams();
        relControlLayout.height = hid - tool_bar_param.height - ((mScreenValueWidth - 8 * 8) / 7 * 3 / 4) - 12;
        relControlLayout.leftMargin = chatControlLeftMargin;
        mRootHolder.rel_control_layout.setLayoutParams(relControlLayout);

        //聊天界面大小
        ViewGroup.LayoutParams chatlistparams = mRootHolder.lin_bottom_chat.getLayoutParams();
        chatlistparams.width = mScreenValueWidth * 4 / 13;
        chatlistparams.height = mRootHolder.rel_control_layout.getLayoutParams().height * 4 / 7 +
                mRootHolder.rl_message.getMeasuredHeight();
        mRootHolder.lin_bottom_chat.setLayoutParams(chatlistparams);

        RelativeLayout.LayoutParams chatLayoutParams = (RelativeLayout.LayoutParams) mRootHolder.lv_chat_list.getLayoutParams();
        chatLayoutParams.width = mScreenValueWidth * 4 / 13;
        chatLayoutParams.height = mRootHolder.rel_control_layout.getLayoutParams().height * 4 / 7;
        mRootHolder.lv_chat_list.setLayoutParams(chatLayoutParams);

        if (isHaiping) {
            /*RelativeLayout.LayoutParams ll_par = (RelativeLayout.LayoutParams) mRootHolder.ll_top.getLayoutParams();
            ll_par.leftMargin = heightStatusBar;
            mRootHolder.ll_top.setLayoutParams(ll_par);*/

            RelativeLayout.LayoutParams re_top_right_par = (RelativeLayout.LayoutParams) mRootHolder.re_top_right.getLayoutParams();
            re_top_right_par.rightMargin = heightStatusBar;
            mRootHolder.re_top_right.setLayoutParams(re_top_right_par);
        }

        RelativeLayout.LayoutParams students_param = new RelativeLayout.LayoutParams(0, 0);
        students_param.width = wid;
        students_param.height = (wid - 8 * 8) / 7 * 3 / 4 + 16;
        students_param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mRootHolder.v_students.setLayoutParams(students_param);
    }

    private void crateToolsPage(View view) {
        //工具条添加
        if (toolsView == null) {
            toolsView = new ToolsView(this, mRootHolder, view);
        }
        toolsView.setonClick();

        //底部翻页添加
        if (mPagesView == null) {
            mPagesView = new PagesView(this, mRootHolder, view);
        }
        mPagesView.setonClick();

        //当没有配置企业和不是老师时不显示是备注
        if (!RoomControler.isHasCoursewareNotes() || TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_TEACHER) {
            //翻页工具条备注按钮隐藏
            if (mPagesView != null) {
                mPagesView.setiVRemark(false);
                mPagesView.setVisiblityremark(false);
            }
        }

        if (!RoomSession.isClassBegin || TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {
            toolsView.showTools(false);
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {

        if (mRootHolder.loadingImageView != null) {
            SkinTool.getmInstance().setLoadingSkin(this, mRootHolder.loadingImageView);
        }
        mRootHolder.txt_class_begin.setText(R.string.classbegin);
        try {
            mRootHolder.fullscreen_sf_video.init(EglBase.create().getEglBaseContext(), null);
        } catch (RuntimeException e) {
            if (BuildConfig.DEBUG)
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mRootHolder.fullscreen_sf_video.setZOrderOnTop(true);
        mRootHolder.fullscreen_sf_video.setZOrderMediaOverlay(true);

        mRootHolder.sek_mp3.setPadding((int) (10 * ScreenScale.getWidthScale()), 0,
                (int) (10 * ScreenScale.getWidthScale()), 0);
        mRootHolder.sek_voice_mp3.setProgress((int) (vol * 100));
        mRootHolder.sek_voice_mp3.setPadding((int) (10 * ScreenScale.getWidthScale()), 0,
                (int) (10 * ScreenScale.getWidthScale()), 0);

        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.tk_play_mp3_gif);
            mRootHolder.img_disk.setImageDrawable(gifDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //课件库
        coursePopupWindowUtils = new CoursePopupWindowUtils(this);
        coursePopupWindowUtils.setPopupWindowClick(this);

        memberListPopupWindowUtils = new MemberListPopupWindowUtils(this, RoomSession.memberList);
        memberListPopupWindowUtils.setPopupWindowClick(this);

        //聊天键盘PopupWindow
        mInputWindowPop = new InputWindowPop(this, RoomSession.chatList);

        allActionUtils = new AllActionUtils(this, this);

        ToolsPopupWindow.getInstance().setActivityAndCall(this);
        LayoutPopupWindow.getInstance().setActivityAndCall(this, 1);
        LayoutPopupWindow.getInstance().setSwitchLayout(this);

        sendGiftPopUtils = new SendGiftPopUtils(this);
        sendGiftPopUtils.preLoadImage();

        chlistAdapter = new ChatListAdapter(RoomSession.chatList, this);
        mRootHolder.lv_chat_list.setAdapter(chlistAdapter);

        chlistAdapter.setOnChatListImageClickListener(new ChatListAdapter.OnChatListImageClickListener() {
            @Override
            public void onChatListImageClick(String image) {
                if (mFullScreenImageView == null) {
                    mFullScreenImageView = new FullScreenImageView(OneToManyActivity.this, (RelativeLayout) view);
                }
                mFullScreenImageView.show(image);
            }
        });

        fileListAdapter = coursePopupWindowUtils.getFileExpandableListAdapter();
        mediaListAdapter = coursePopupWindowUtils.getMediaExpandableListAdapter();
        memberListAdapter = memberListPopupWindowUtils.getMemberListAdapter();

        wifiStatusPop = new WifiStatusPop(this);
        wifiStatusPop.wifiStatusPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (wifiStatusPop.wifiStatus == 1) {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_you_sanjiao_down);
                } else if (wifiStatusPop.wifiStatus == 2) {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_zhong_sanjiao_down);
                } else {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_cha_sanjiao_down);
                }
            }
        });

        wbFragment = WhiteBoradConfig.getsInstance().CreateWhiteBorad(this);

        mWb_proto = WhiteBoradConfig.getsInstance().CreateWhiteBoardView();

        WhiteBoradConfig.getsInstance().setPlayBack(RoomSession.isPlayBack);
        WhiteBoradConfig.getsInstance().isLiuHaiping(isHaiping);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        FragmentTransaction ft1 = fragmentManager.beginTransaction();

        if (!wbFragment.isAdded()) {
            ft.add(R.id.wb_container, wbFragment);
            ft.commit();
        }
        if (!mWb_proto.isAdded()) {
            ft1.add(R.id.wb_protogenesis, mWb_proto);
            ft1.commit();
        }

        if (mWb_proto instanceof FaceShareFragment) {
            ((FaceShareFragment) mWb_proto).setFragmentUserVisibleHint(this);
        }

        ToolCaseMgr.getInstance().setActivity(this, mRootHolder.rel_wb);
        ToolCaseMgr.getInstance().setLiuHaiping(isHaiping);
        //初始化工具箱
        ToolCaseMgr.getInstance().setHasWhiteBoradAction();
    }

    /**
     * 截图显示隐藏
     *
     * @param visibility View.VISIBLE View.GONE
     */
    @Override
    public void setWbProtoVisibility(int visibility) {
        if (mWb_proto != null) {
            ((FaceShareFragment) mWb_proto).setVisibility(visibility);
        }
    }

    /**
     * 绑定监听事件
     */
    private void bindListener() {
        //绑定聊天信息回调
        Translate.getInstance().setCallback(this);

        mRootHolder.txt_class_begin.setOnClickListener(this);
        mRootHolder.img_back.setOnClickListener(this);
        mRootHolder.flipCamera.setOnClickListener(this);
        mRootHolder.iv_open_input.setOnClickListener(this);
        mRootHolder.cb_choose_shut_chat.setOnClickListener(this);
        mRootHolder.cb_choose_photo.setOnCheckedChangeListener(this);
        mRootHolder.cb_message.setOnCheckedChangeListener(this);
        mRootHolder.cb_control.setOnCheckedChangeListener(this);
        mRootHolder.cb_file_person_media_list.setOnCheckedChangeListener(this);
        mRootHolder.cb_tool_case.setOnCheckedChangeListener(this);
        mRootHolder.cb_member_list.setOnCheckedChangeListener(this);
        mRootHolder.lin_wifi.setOnClickListener(this);
        mRootHolder.cb_tool_layout.setOnCheckedChangeListener(this);
        //预加载跳过
        mRootHolder.fl_downloadProgress.setJumpOverClieck(new DownloadProgressView.JumpOverClieck() {
            @Override
            public void jumpover() {
                isJumpOver = true;
                //点击跳过 后台继续默认下载，隐藏预加载进度，H5传递不带baseurl的默认课件
                ProLoadingDoc.getInstance().postTksdk();
                mRootHolder.fl_downloadProgress.setVisibility(View.GONE);
                setTitleBarContentVisibility(View.VISIBLE);
            }
        });
        //MP3媒体播放相关监听
        bindMediaListener();
        //初始化白板全屏移动视频窗
        if (mRootHolder != null) {
            MoveFullBoardUtil.getInstance().SetViewOnTouchListener(mRootHolder.rel_fullscreen_videoitem);
        }
    }

    /**
     * MP3媒体播放相关监听
     */
    private void bindMediaListener() {

        mRootHolder.img_close_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TKRoomManager.getInstance().stopShareMedia();
                mRootHolder.lin_audio_seek.setVisibility(View.INVISIBLE);
                mRootHolder.fl_play_disk.setVisibility(View.INVISIBLE);
            }
        });

        //MP3播放暂停监听
        mRootHolder.img_play_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaAttrs != null) {
                    if (RoomSession.isPublishMp3) {
                        TKRoomManager.getInstance().playMedia((Boolean) mediaAttrs.get("pause") == null ?
                                false : (Boolean) mediaAttrs.get("pause"));
                    } else {

                        ShareDoc media = WhiteBoradConfig.getsInstance().getCurrentMediaDoc();
                        String strSwfpath = media.getSwfpath();
                        int pos = strSwfpath.lastIndexOf('.');
                        strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
                        String url = "http://" + WhiteBoradConfig.getsInstance().getFileServierUrl() + ":" + WhiteBoradConfig.getsInstance().getFileServierPort() + strSwfpath;
                        HashMap<String, Object> attrMap = new HashMap<String, Object>();
                        attrMap.put("filename", media.getFilename());
                        attrMap.put("fileid", media.getFileid());
                        if (RoomSession.isClassBegin) {
                            TKRoomManager.getInstance().startShareMedia(url, false, "__all", attrMap);
                        } else {
                            TKRoomManager.getInstance().startShareMedia(url, false,
                                    TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                        }
                    }
                }
            }
        });

        //MP3播放进度条监听
        mRootHolder.sek_mp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pro = 0;
            boolean isfromUser = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    pro = progress;
                    isfromUser = fromUser;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double currenttime = 0;
                if (isfromUser && mediaAttrs != null) {
                    currenttime = ((double) pro / (double) seekBar.getMax()) * (int) mediaAttrs.get("duration");
                    TKRoomManager.getInstance().seekMedia((long) currenttime);
                }
            }
        });

        mRootHolder.img_voice_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMediaMute) {
                    TKRoomManager.getInstance().setRemoteAudioVolume(vol, mediaPeerId, 2);
                    mRootHolder.img_voice_mp3.setImageResource(R.drawable.tk_icon_voice);
                    mRootHolder.sek_voice_mp3.setProgress((int) (vol * 100));
                } else {
                    TKRoomManager.getInstance().setRemoteAudioVolume(0, mediaPeerId, 2);
                    mRootHolder.img_voice_mp3.setImageResource(R.drawable.tk_icon_no_voice);
                    mRootHolder.sek_voice_mp3.setProgress(0);
                }
                isMediaMute = !isMediaMute;
            }
        });

        //MP3音量进度条监听
        mRootHolder.sek_voice_mp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = (float) progress / (float) seekBar.getMax();
                if (vol > 0) {
                    mRootHolder.img_voice_mp3.setImageResource(R.drawable.tk_icon_voice);
                } else {
                    mRootHolder.img_voice_mp3.setImageResource(R.drawable.tk_icon_no_voice);
                }
                TKRoomManager.getInstance().setRemoteAudioVolume(vol, mediaPeerId, 2);
                if (fromUser) {
                    OneToManyActivity.this.vol = vol;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mRootHolder.eye_protection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //判断是否有悬浮窗的权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//版本6.0以上的设备需要动态申请该权限
                    if (!Settings.canDrawOverlays(OneToManyActivity.this)) {//没有该权限
                        mRootHolder.eye_protection.setChecked(false);
                        EyeProtectionUtil.showDialog(OneToManyActivity.this);
                    } else {//有该权限
                        EyeProtectionUtil.openSuspensionWindow(OneToManyActivity.this, isChecked);
                    }
                } else {
                    EyeProtectionUtil.openSuspensionWindow(OneToManyActivity.this, isChecked);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        if (TKRoomManager.getInstance().getMySelf() != null && RoomSession.isInRoom) {
            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT || TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                TKRoomManager.getInstance().setInBackGround(false);
                if (isBackApp) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "isInBackGround", false);
                }
                mRootHolder.flipCamera.setVisibility(View.VISIBLE);
            } else {
                mRootHolder.flipCamera.setVisibility(View.GONE);
            }
        }
        if (isBackApp) {
            stopService(new Intent(this, MonitorService.class));
        }
        isBackApp = false;
        isOpenCamera = false;

        super.onStart();

        if (!RoomSession.isInRoom) {
            mRootHolder.re_loading.setVisibility(View.VISIBLE);
            mRootHolder.tv_load.setText(R.string.joining_classroom_home);
        }
        if (mRootHolder.eye_protection.isChecked()) {
            //EyeProtectionUtil.openSuspensionWindow(OneToManyActivity.this, true);
        }
    }

    @Override
    protected void onStop() {
        if (!isFinishing()) {
            TKRoomManager.getInstance().setInBackGround(true);
            if (!isBackApp) {
                    Intent mMonitorService = new Intent(this, MonitorService.class);
                    mMonitorService.putExtra(MonitorService.KEY, OneToManyActivity.class.getName());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(mMonitorService);
                    } else {
                        startService(mMonitorService);
                    }
                isBackApp = true;
            }
            if (TKRoomManager.getInstance().getMySelf() != null) {
                if (TKRoomManager.getInstance().getMySelf().role == 2 || TKRoomManager.getInstance().getMySelf().role == 0) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "isInBackGround", true);
                }
            }
            hidePopupWindow();
        }
        if (mRootHolder.eye_protection.isChecked()) {
            EyeProtectionUtil.openSuspensionWindow(OneToManyActivity.this, false);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        isZoom = false;
        super.onDestroy();

        if (mRootHolder.fullscreen_sf_video != null) {
            mRootHolder.fullscreen_sf_video.release();
            mRootHolder.fullscreen_sf_video = null;
        }

        if (videoItems != null && videoItems.size() > 0) {
            for (VideoItemToMany view : videoItems)
                mRootHolder.rel_students.removeView(view.parent);
            videoItems.clear();
        }

        if (videoItemToManies != null && videoItemToManies.size() > 0) {
            videoItemToManies.clear();
        }
        if (mPlayBackSeekPopupWindow != null)
            mPlayBackSeekPopupWindow.dismiss();
        RoomClient.getInstance().onResetVideo();
        RoomSession.getInstance().resetRoomSession();
    }

    //初始化各个角色显示控件
    private void initViewByRoomTypeAndTeacher() {
        mRootHolder.cb_control.setVisibility(View.GONE);
        mRootHolder.cb_file_person_media_list.setVisibility(View.GONE);
        mRootHolder.cb_tool_case.setVisibility(View.GONE);
        mRootHolder.cb_tool_layout.setVisibility(View.GONE);
        mRootHolder.cb_member_list.setVisibility(View.GONE);

        if (TKRoomManager.getInstance().getMySelf().role == 0) {//老师

            mRootHolder.cb_member_list.setVisibility(View.VISIBLE);
            mRootHolder.cb_tool_layout.setVisibility(View.VISIBLE);

            if (!RoomControler.isShowClassBeginButton()) {
                mRootHolder.txt_class_begin.setVisibility(View.VISIBLE);
                if (RoomSession.isClassBegin) {
                    mRootHolder.txt_class_begin.setText(R.string.classdismiss);
                } else {
                    mRootHolder.txt_class_begin.setText(R.string.classbegin);
                }
            } else {
                mRootHolder.txt_class_begin.setVisibility(View.GONE);
            }

            if (RoomSession._possibleSpeak) {
                mRootHolder.cb_choose_shut_chat.setChecked(false);
            } else {
                mRootHolder.cb_choose_shut_chat.setChecked(true);
            }

            //会议隐藏全体控制和工具箱
            if (RoomSession.isClassBegin) {
                if ((!RoomControler.isHasAnswerMachine() && !RoomControler.isHasTurntable() &&
                        !RoomControler.isHasTimer() && !RoomControler.isHasResponderAnswer() &&
                        !RoomControler.isHasWhiteBoard()) || mLayoutState == LayoutPopupWindow.LAYOUT_VIDEO
                        || mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE) {
                    mRootHolder.cb_tool_case.setVisibility(View.GONE);
                } else {
                    mRootHolder.cb_tool_case.setVisibility(View.VISIBLE);
                }
                mRootHolder.cb_control.setVisibility(View.VISIBLE);
            } else {
                mRootHolder.cb_tool_case.setVisibility(View.GONE);
                mRootHolder.cb_control.setVisibility(View.GONE);
            }

            if (mLayoutState != 1) {
                mRootHolder.cb_file_person_media_list.setVisibility(View.GONE);
            } else {
                mRootHolder.cb_file_person_media_list.setVisibility(View.VISIBLE);
            }

        } else if (TKRoomManager.getInstance().getMySelf().role == 2) {//学生

            mRootHolder.txt_class_begin.setVisibility(View.GONE);
            mRootHolder.eye_protection.setVisibility(View.VISIBLE);
            if (RoomSession.isClassBegin) {
                if (TKRoomManager.getInstance().getMySelf().publishState == 2 ||
                        TKRoomManager.getInstance().getMySelf().publishState == 0 ||
                        TKRoomManager.getInstance().getMySelf().publishState == 4) {
                    mRootHolder.txt_hand_up.setClickable(true);
                }
                mRootHolder.txt_hand_up.setVisibility(View.VISIBLE);
                mRootHolder.cb_file_person_media_list.setVisibility(View.GONE);
            } else {
                mRootHolder.txt_hand_up.setVisibility(View.INVISIBLE);
                mRootHolder.cb_choose_photo.setVisibility(View.GONE);
                if (mLayoutState != 1) {
                    mRootHolder.cb_file_person_media_list.setVisibility(View.GONE);
                } else {
                    mRootHolder.cb_file_person_media_list.setVisibility(View.VISIBLE);
                }
            }

            if (RoomControler.isHideStudentWifiStatus()) {
                mRootHolder.lin_wifi.setVisibility(View.GONE);
            }

        } else if (TKRoomManager.getInstance().getMySelf().role == 4) {//巡课

            if (!RoomSession.isClassBegin || RoomControler.patrollerCanClassDismiss() || RoomControler.isShowClassBeginButton()) {
                mRootHolder.txt_class_begin.setVisibility(View.GONE);
            } else {
                mRootHolder.txt_class_begin.setVisibility(View.VISIBLE);
                mRootHolder.txt_class_begin.setText(R.string.classdismiss);
            }
            mRootHolder.flipCamera.setVisibility(View.GONE);
            mRootHolder.rl_member_list.setVisibility(View.VISIBLE);
            mRootHolder.cb_member_list.setVisibility(View.VISIBLE);
            mRootHolder.cb_file_person_media_list.setVisibility(View.VISIBLE);
            mRootHolder.lin_wifi.setVisibility(View.GONE);//巡课隐藏网络状态.
        } else if (TKRoomManager.getInstance().getMySelf().role == -1) { //回放
            CommonUtil.setTimeVisibility(mRootHolder, View.GONE);
            mRootHolder.cb_choose_photo.setVisibility(View.GONE);
            if (mRootHolder.re_play_back != null)
                mRootHolder.re_play_back.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 播放视频
     */
    private void doPlayVideo(String peerId) {
        RoomUser user = TKRoomManager.getInstance().getUser(peerId);
        if (user == null) {
            return;
        }

        //如果上课 && 放大 && 课件全屏同步 && 当前用户是老师 && 当前课件正是全屏
        if (RoomSession.isClassBegin && isZoom && RoomControler.isFullScreenVideo() && RoomSession.fullScreen && user.role == Constant.USERROLE_TEACHER) {
            //发送老师id至videofrgment界面去playvideo
            if (videofragment != null) {
                videofragment.setFullscreenShow(user.peerId);
            } else {
                //同上 发送老师id到moviefragment显示
                if (movieFragment != null) {
                    movieFragment.setFullscreenShow(user.peerId);
                } else {
                    FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem, user, true);
                }
            }
        } else {
            do1vsnStudentPlayVideo(user);
        }
    }

    private int sitpos = -1;

    /**
     * 播放学生video
     *
     * @param user
     */
    private void do1vsnStudentPlayVideo(RoomUser user) {
        boolean hasSit = false;
        sitpos = -1;
        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(user.peerId)) {
                hasSit = true;
                sitpos = i;
                break;
            }
        }

        //不包含 去创建 videoItem
        if (!hasSit) {
            VideoItemToMany stu = null;
            if (videoItemToManies != null && videoItemToManies.size() > 0) {
                for (int i = 0; i < videoItemToManies.size(); i++) {
                    //如果未使用与创建videoitem 赋值
                    if (!videoItemToManies.get(i).isShow) {
                        stu = videoItemToManies.get(i);
                        stu.isShow = true;
                        break;
                    }
                }
            }
            if (stu == null) {
                return;
            }

            stu.peerid = user.peerId;
            stu.role = user.role;
            stu.txt_name.setText(user.nickName);

            if (!RoomSession.isClassBegin) {
                stu.img_pen.setVisibility(View.GONE);
                stu.bg_img_pen.setVisibility(View.GONE);
                stu.img_hand.setVisibility(View.INVISIBLE);
                stu.img_mic.setVisibility(View.INVISIBLE);
                stu.volume.setVisibility(View.GONE);
            }
            //如果是学生，奖杯显示与否
            if (user.role == 2) {
                stu.lin_gift.setVisibility(View.VISIBLE);
            } else {
                stu.lin_gift.setVisibility(View.INVISIBLE);
            }
            //视频框中控件状态
            changeVideoItemState(stu);
            //设置item长按时间
            final VideoItemToMany finalStu = stu;
            stu.parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE || mLayoutState == LayoutPopupWindow.LAYOUT_VIDEO) {
                        return false;
                    }
                    if (screenID != null && screenID.size() > 0) {
                        return false;
                    }
                    if (finalStu.isSplitScreen || !TKRoomManager.getInstance().getMySelf().canDraw ||
                            !RoomSession.isClassBegin) {
                        return false;
                    }

                    finalStu.canMove = true;
                    return false;
                }
            });

            //videoItem的Touch事件
            VideoTtemTouchEvent.eventProcess(videoItems, stu, mRootHolder.rel_students, mRootHolder.v_students,
                    mRootHolder.rel_wb, printWidth, nameLabelHeight, printHeight, stuMoveInfoMap, screenID, this);
            //如果是老师，做标识
            if (user.role == 0) {
                videoItems.add(0, stu);
            } else {
                videoItems.add(stu);
            }

            if (RoomControler.isOnlyShowTeachersAndVideos() && !user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) &&
                    TKRoomManager.getInstance().getMySelf().role == 2 && user.role != 0) {
                stu.setOnlyShowTeachersAndVideos(false);
                stu.parent.setVisibility(View.GONE);
            }
            if (movedVideoItems.size() > 0 && notMoveVideoItems.size() == 0)
                mRootHolder.rel_students.addView(stu.parent, 0);
            else
                mRootHolder.rel_students.addView(stu.parent);
            do1vsnStudentVideoLayout();

        } else if (sitpos != -1) {
            changeVideoItemState(videoItems.get(sitpos));
        }

        for (int i = 0; i < videoItems.size(); i++) {
            final VideoItemToMany it = videoItems.get(i);
            it.parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (screenID.size() > 0) {
                        if (screenID.contains(it.peerid)) {
                            it.isSplitScreen = true;
                        }
                        do1vsnStudentVideoLayout();
                    }

                    if (scalemap != null && scalemap.size() > 0) {
                        if (scalemap.containsKey(it.peerid) && scalemap.get(it.peerid) != null) {
                            if (!it.isSplitScreen) {
                                double scale = scalemap.get(it.peerid);
                                if (videoItems.size() == 1)
                                    LayoutZoomOrIn.zoomMsgMouldVideoItem(it, scale, printWidth, printHeight,
                                            hid - mRootHolder.rel_tool_bar.getLayoutParams().height);
                                else
                                    LayoutZoomOrIn.zoomMsgMouldVideoItem(it, scale, printWidth, printHeight,
                                            mRootHolder.rel_wb_container.getHeight());
                            }
                            scalemap.remove(it.peerid);
                        }
                    }

                    if (!it.isMoved) {
                        if (stuMoveInfoMap.containsKey(it.peerid) && stuMoveInfoMap.get(it.peerid) != null) {
                            MoveVideoInfo mi = stuMoveInfoMap.get(it.peerid);
                            moveStudent(it.peerid, mi.top, mi.left, mi.isDrag);
                            stuMoveInfoMap.remove(it.peerid);
                        }
                        it.parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    private void moveStudent(String peerid, float top, float left, boolean isDrag) {
        for (int i = 0; i < videoItems.size(); i++) {
            VideoItemToMany it = videoItems.get(i);
            if (videoItems.get(i).peerid.equals(peerid)) {
                int width;
                int height;
                if (it.width < 0) {
                    width = it.parent.getWidth();
                    height = it.parent.getHeight();
                } else {
                    width = it.width;
                    height = it.height;
                }
                if (isDrag) {
                    //当只显示自己和老师时
                    if (!it.isMoved && !it.isOnlyShowTeachersAndVideos) {
                        RoomUser user = TKRoomManager.getInstance().getUser(peerid);
                        if (user.getPublishState() == 3 || user.getPublishState() == 2) {
                            it.sf_video.setVisibility(View.VISIBLE);
                            TKRoomManager.getInstance().playVideo(peerid, it.sf_video,
                                    RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                            OneToManyFreeLayoutUtil.getInstance().doLayout((int) printWidth,
                                    (int) printHeight, it, -1, -1);
                        }

                        if (user.getPublishState() == 3 || user.getPublishState() == 1) {
                            TKRoomManager.getInstance().playAudio(peerid);
                        }
                    }

                    int wid = (int) (mRootHolder.rel_students.getWidth() - width);
                    int hid = (int) (mRootHolder.rel_students.getHeight() - height - mRootHolder.v_students.getHeight());
                    top = top * hid + mRootHolder.v_students.getHeight() * (videoItems.size() == 1 ? top : 1);
                    left = left * wid;
                    it.isMoved = isDrag;
                    if (left < 0) {
                        left = 0;
                    }

                    if (left > wid) {
                        left = wid;
                    }

                    if (top < mRootHolder.rel_wb.getTop()) {
                        top = mRootHolder.rel_wb.getTop();
                    }

                    int bottom = (int) (top + height);
                    if (bottom > mRootHolder.rel_students.getHeight()) {
                        bottom = mRootHolder.rel_students.getHeight();
                    }

                    if (width < printWidth || height < printHeight) {
                        OneToManyFreeLayoutUtil.getInstance().doLayout((int) printWidth, (int) printHeight, it, -1, -1);
                    }
                    LayoutZoomOrIn.layoutMouldVideo(it, (int) left, (int) top, bottom);
                } else {
                    if (it.isMoved) {
                        it.isMoved = false;
                        //当只显示自己和老师 拖拽回去时关掉声音
                        if (!it.isOnlyShowTeachersAndVideos) {
                            TKRoomManager.getInstance().unPlayAudio(it.peerid);
                            TKRoomManager.getInstance().unPlayVideo(it.peerid);
                            it.parent.setVisibility(View.GONE);
                            it.sf_video.setVisibility(View.GONE);
                        }
                    }
                }
                break;
            }
        }
        do1vsnStudentVideoLayout();
    }

    //停止播放学生摄像头
    private void do1vsnStudentUnPlayVideo(String peerId) {
        if (peerId == null || peerId.isEmpty()) {
            return;
        }
        TKRoomManager.getInstance().unPlayVideo(peerId);
        RoomUser roomUser = TKRoomManager.getInstance().getUser(peerId);
        if (roomUser == null) {
            return;
        }
        //预创建之后的videoitem 下台时只需移除从集合中的数据，恢复初始状态值，
        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(peerId)) {
                if (roomUser.getPublishState() == 0) {
                    resetVideoitemMoved(videoItems.get(i));
                    videoItemToManies.remove(videoItems.get(i));
                    mRootHolder.rel_students.removeView(videoItems.get(i).parent);
                    videoItems.remove(i);
                    do1vsnStudentVideoLayout();
                    VideoItemToMany videoItemToMany = new VideoItemToMany(this);
                    videoItemToManies.add(videoItemToMany);
                } else {
                    videoItems.get(i).sf_video.setVisibility(View.INVISIBLE);
                    videoItems.get(i).img_video_back.setVisibility(View.VISIBLE);
                    videoItems.get(i).bg_video_back.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    boolean is_show_student_window = true;

    private void showStudentControlPop(final View view, final RoomUser user, final int index) {
        if (!is_show_student_window) {
            is_show_student_window = true;
            videoItems.get(index).view_choose_selected.setVisibility(View.GONE);
            return;
        }
        if (TKRoomManager.getInstance().getMySelf() == null || TKRoomManager.getInstance().getMySelf().peerId == null) {
            return;
        }

        if (!RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
            videoItems.get(index).view_choose_selected.setVisibility(View.GONE);
            return;
        }

        if (!(TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) && !user.peerId.endsWith(TKRoomManager.getInstance().getMySelf().peerId)) {
            videoItems.get(index).view_choose_selected.setVisibility(View.GONE);
            return;
        }

        if (user.peerId.endsWith(TKRoomManager.getInstance().getMySelf().peerId) && !RoomControler.isAllowStudentControlAV()) {
            videoItems.get(index).view_choose_selected.setVisibility(View.GONE);
            return;
        }

        if (!RoomSession.isClassBegin) {
            if (!RoomControler.isReleasedBeforeClass()) {
                videoItems.get(index).view_choose_selected.setVisibility(View.GONE);
                return;
            }
        }
        //赋值
        solepopwindowPid = user.peerId;
        videoItems.get(index).view_choose_selected.setVisibility(View.VISIBLE);
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tk_popup_student_control, null);

        ImageView img_up_arr = (ImageView) contentView.findViewById(R.id.up_arr);
        ImageView img_down_arr = (ImageView) contentView.findViewById(R.id.down_arr);

        LinearLayout lin_candraw = (LinearLayout) contentView.findViewById(R.id.lin_candraw);
        LinearLayout lin_up_sd = (LinearLayout) contentView.findViewById(R.id.lin_up_sd);
        LinearLayout lin_audio = (LinearLayout) contentView.findViewById(R.id.lin_audio);
        LinearLayout lin_gift = (LinearLayout) contentView.findViewById(R.id.lin_gift);
        //视频切换
        LinearLayout lin_change = (LinearLayout) contentView.findViewById(R.id.lin_change);
        final ImageView img_candraw = (ImageView) contentView.findViewById(R.id.img_candraw);
        final ImageView img_up_sd = (ImageView) contentView.findViewById(R.id.img_up_sd);
        final ImageView img_audio = (ImageView) contentView.findViewById(R.id.img_audio);
        final TextView txt_candraw = (TextView) contentView.findViewById(R.id.txt_candraw);
        final TextView txt_up_sd = (TextView) contentView.findViewById(R.id.txt_up_sd);
        final TextView txt_audio = (TextView) contentView.findViewById(R.id.txt_audio);

        LinearLayout lin_single_fuwe = (LinearLayout) contentView.findViewById(R.id.lin_single_fuwe);
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER && (videoItems.get(index).isMoved || videoItems.get(index).isSplitScreen)) {
            lin_single_fuwe.setVisibility(View.VISIBLE);
        } else {
            lin_single_fuwe.setVisibility(View.GONE);
        }

        //视频切换
        if (mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE && index != 0 &&
                TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
            lin_change.setVisibility(View.VISIBLE);
            lin_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    studentPopupWindow.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("doubleId", videoItems.get(index).peerid);
                        TKRoomManager.getInstance().pubMsg("MainPeopleExchangeVideo",
                                "MainPeopleExchangeVideo", "__all", jsonObject.toString(),
                                true, "ClassBegin", null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            lin_change.setVisibility(View.GONE);
        }

        if (index >= 0) {
            if (videoItems.get(index).isSplitScreen) {
                lin_candraw.setVisibility(View.GONE);
            } else {
                if (user.role != 1 && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                    lin_candraw.setVisibility(View.VISIBLE);
                } else {
                    lin_candraw.setVisibility(View.GONE);
                }
            }
        }

        lin_single_fuwe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentPopupWindow.dismiss();
                videoItems.get(index).isMoved = false;
                if (screenID.contains(videoItems.get(index).peerid)) {
                    screenID.remove(videoItems.get(index).peerid);
                }
                if (videoItems.get(index).isSplitScreen) {
                    videoItems.get(index).isSplitScreen = false;
                    SendingSignalling.getInstance().sendDoubleClickVideoRecovery(videoItems.get(index).peerid);
                }
                do1vsnStudentVideoLayout();
                SendingSignalling.getInstance().sendStudentMove(videoItems, mRootHolder.rel_students, mRootHolder.v_students);
            }
        });

        LinearLayout lin_video_control = (LinearLayout) contentView.findViewById(R.id.lin_video_control);
        final ImageView img_video_control = (ImageView) contentView.findViewById(R.id.img_camera);
        final TextView txt_video = (TextView) contentView.findViewById(R.id.txt_camera);

        if (RoomSession.isOnliyAudioRoom) {
            lin_video_control.setVisibility(View.GONE);
        } else {
            lin_video_control.setVisibility(View.VISIBLE);
        }

        if (user.role == 1) {
            lin_gift.setVisibility(View.GONE);
            lin_up_sd.setVisibility(View.GONE);
        } else {
            if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                lin_gift.setVisibility(View.GONE);
                lin_up_sd.setVisibility(View.GONE);
            } else {
                lin_gift.setVisibility(View.VISIBLE);
                lin_up_sd.setVisibility(View.VISIBLE);
            }
        }

        if (user.getPublishState() == 0 || user.getPublishState() == 1 || user.getPublishState() == 4) {
            img_video_control.setImageResource(R.drawable.tk_icon_close_vidio);
            txt_video.setText(R.string.video_off);
        } else {
            img_video_control.setImageResource(R.drawable.tk_icon_open_vidio);
            txt_video.setText(R.string.video_on);
        }

        if (user.disableaudio || !user.hasAudio) {
            lin_audio.setVisibility(View.GONE);
        } else {
            lin_audio.setVisibility(View.VISIBLE);
            if (user.getPublishState() == 0 || user.getPublishState() == 2 || user.getPublishState() == 4) {
                img_audio.setImageResource(R.drawable.tk_icon_close_audio);
                txt_audio.setText(R.string.close_audio);
            } else {
                img_audio.setImageResource(R.drawable.tk_icon_open_audio);
                txt_audio.setText(R.string.open_audio);
            }
        }

        if (user.properties.containsKey("candraw")) {
            boolean candraw = Tools.isTure(user.properties.get("candraw"));
            if (candraw) {      //可以画图
                img_candraw.setImageResource(R.drawable.tk_icon_shouquan);
                txt_candraw.setText(R.string.candraw);
            } else {      //不可以画图
                img_candraw.setImageResource(R.drawable.tk_icon_quxiaoshouquan);
                txt_candraw.setText(R.string.no_candraw);
            }
        } else {     //没给过画图权限
            img_candraw.setImageResource(R.drawable.tk_icon_quxiaoshouquan);
            txt_candraw.setText(R.string.no_candraw);
        }
        if (user.getPublishState() > 0) {  //只要视频开启就是上台
            img_up_sd.setImageResource(R.drawable.tk_icon_on_platform);
            txt_up_sd.setText(R.string.up_std);
        } else {
            img_up_sd.setImageResource(R.drawable.tk_icon_out_platform);
            txt_up_sd.setText(R.string.down_std);
        }

        lin_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getPublishState() == 0 || user.getPublishState() == 2 || user.getPublishState() == 4) {
                    img_audio.setImageResource(R.drawable.tk_icon_open_audio);
                    txt_audio.setText(R.string.open_audio);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "publishstate", user.getPublishState() == 0 || user.getPublishState() == 4 ? 1 : 3);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                } else {
                    img_audio.setImageResource(R.drawable.tk_icon_close_audio);
                    txt_audio.setText(R.string.close_audio);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "publishstate", user.getPublishState() == 3 ? 2 : 4);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_candraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.properties.containsKey("candraw")) {
                    boolean candraw = Tools.isTure(user.properties.get("candraw"));
                    if (candraw) {    //不可以画图
                        img_candraw.setImageResource(R.drawable.tk_icon_quxiaoshouquan);
                        txt_candraw.setText(R.string.no_candraw);
                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                    } else {   //可以画图
                        img_candraw.setImageResource(R.drawable.tk_icon_shouquan);
                        txt_candraw.setText(R.string.candraw);
                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", true);
                    }
                } else {    //可以画图
                    img_candraw.setImageResource(R.drawable.tk_icon_shouquan);
                    txt_candraw.setText(R.string.candraw);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", true);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, RoomUser> receiverMap = new HashMap<String, RoomUser>();
                receiverMap.put(user.peerId, user);

                int size = RoomInfo.getInstance().getTrophyList().size();
                if (size == 1) {
                    // 只有一个奖杯，直接发送，不显示选择框
                    sendGiftPopUtils.sendGift(RoomInfo.getInstance().getTrophyList().get(0), receiverMap);
                } else if (size > 1) {
                    //自定义奖杯
                    sendGiftPopUtils.showSendGiftPop(mRootHolder.rel_wb.getWidth() / 10 * 5,
                            mRootHolder.rel_wb.getHeight() / 10 * 9, mRootHolder.rel_wb, receiverMap,
                            false, 0);
                } else {
                    //默认奖杯
                    RoomOperation.getInstance().sendGift(receiverMap, null, OneToManyActivity.this);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_up_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getPublishState() > 0) {   //只要视频开启就是上台
                    img_up_sd.setImageResource(R.drawable.tk_icon_out_platform);
                    txt_up_sd.setText(R.string.down_std);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "publishstate", 0);
                    if (user.role != 1) {
                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                    }
                } else {
                    img_up_sd.setImageResource(R.drawable.tk_icon_on_platform);
                    txt_up_sd.setText(R.string.up_std);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "publishstate", 3);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_video_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当全音频时不开启视频
                if (RoomSession.isOnliyAudioRoom) {
                    return;
                }
                if (user.getPublishState() == 0 || user.getPublishState() == 1 || user.getPublishState() == 4) {
                    img_video_control.setImageResource(R.drawable.tk_icon_open_vidio);
                    txt_video.setText(R.string.video_on);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "publishstate", user.getPublishState() == 0 || user.getPublishState() == 4 ? 2 : 3);
                } else {
                    img_video_control.setImageResource(R.drawable.tk_icon_close_vidio);
                    txt_video.setText(R.string.video_off);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "publishstate", user.getPublishState() == 2 ? 4 : 1);
                }
                studentPopupWindow.dismiss();
            }
        });

        //弹框高度和长度都设置成固定值（dp）
        if (TKRoomManager.getInstance().getMySelf().role == 2) {
            studentPopupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToManyActivity.this, 150f), KeyBoardUtil.dp2px(OneToManyActivity.this, 70f));
        } else {
            studentPopupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToManyActivity.this, 380f), KeyBoardUtil.dp2px(OneToManyActivity.this, 70f));
        }

        studentPopupWindow.setContentView(contentView);

        studentPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //如果只有一位 助教点击老师下台 老师popwindow消失会数组越界
                if (videoItems.size() > index) {
                    if (videoItems.get(index).peerid.equals(user.peerId)) {
                        videoItems.get(index).view_choose_selected.setVisibility(View.GONE);
                    }
                }
            }
        });

        studentPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                is_show_student_window = !Tools.isInView(event, view);
                return false;
            }
        });
        studentPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        studentPopupWindow.setFocusable(false);
        studentPopupWindow.setOutsideTouchable(true);

        setArrPosition(img_up_arr, img_down_arr, index, studentPopupWindow);
    }

    private void setArrPosition(ImageView img_up_arr, ImageView img_down_arr, int index, PopupWindow popupWindow) {

        //获取点击视频框相对于屏幕左边的距离
        int[] video_item = new int[2];
        videoItems.get(index).parent.getLocationInWindow(video_item);
        if ((mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE && index == 0) || (mLayoutState == LayoutPopupWindow.LAYOUT_VIDEO && videoItems.size() == 1)) {
            int width = videoItems.get(index).parent.getWidth();
            int hight = videoItems.get(index).parent.getHeight();
            popupWindow.showAsDropDown(videoItems.get(index).parent, width / 2 - popupWindow.getWidth() / 2, -hight / 2 - popupWindow.getHeight() / 2);
            return;
        }
        RelativeLayout.LayoutParams down_arr_param = (RelativeLayout.LayoutParams) img_down_arr.getLayoutParams();
        RelativeLayout.LayoutParams up_arr_param = (RelativeLayout.LayoutParams) img_up_arr.getLayoutParams();
        int view_self_height = videoItems.get(index).parent.getHeight();
        int ab_height = video_item[1];

        if (videoItems.get(index).isSplitScreen) {
            img_down_arr.setVisibility(View.VISIBLE);
            img_up_arr.setVisibility(View.GONE);
            down_arr_param.setMargins(popupWindow.getWidth() / 2, 0, 0, 0);
            if (video_item[1] - popupWindow.getHeight() > 0) {
                popupWindow.showAtLocation(videoItems.get(index).parent, Gravity.NO_GRAVITY, video_item[0] + (videoItems.get(index).parent.getWidth() - popupWindow.getWidth()) / 2, video_item[1] - popupWindow.getHeight());
            } else {
                popupWindow.showAtLocation(videoItems.get(index).parent, Gravity.NO_GRAVITY, video_item[0] + (videoItems.get(index).parent.getWidth() - popupWindow.getWidth()) / 2, video_item[1]);
            }
        } else {
            int show_position_x = video_item[0];
            int show_position_y = 0;

            if ((ScreenScale.getScreenHeight() - ab_height - view_self_height) > 200) {
                //弹框在下边
                img_up_arr.setVisibility(View.VISIBLE);
                img_down_arr.setVisibility(View.GONE);
                show_position_y = video_item[1] + videoItems.get(index).parent.getHeight();
            } else {
                //弹框在上边
                img_up_arr.setVisibility(View.GONE);
                img_down_arr.setVisibility(View.VISIBLE);
                show_position_y = video_item[1] - popupWindow.getHeight();
            }

            if ((ScreenScale.getScreenWidth() - video_item[0]) < (popupWindow.getWidth() / 2)) {
                //弹框在最右边，需要调整三角形的margin值
                int margin_left = video_item[0] + videoItems.get(index).parent.getMeasuredWidth() / 2 - (ScreenScale.getScreenWidth() - popupWindow.getWidth());
                up_arr_param.setMargins(margin_left, 0, 0, 0);
                down_arr_param.setMargins(margin_left, 0, 0, 0);
                popupWindow.showAtLocation(videoItems.get(index).parent, Gravity.NO_GRAVITY, show_position_x, show_position_y);

            } else {
                if ((video_item[0] + videoItems.get(index).parent.getWidth() / 2) < popupWindow.getWidth() / 2) {
                    up_arr_param.setMargins((videoItems.get(index).parent.getWidth() / 2 + video_item[0]), 0, 0, 0);
                    down_arr_param.setMargins((videoItems.get(index).parent.getWidth() / 2), 0, 0, 0);
                } else {
                    up_arr_param.setMargins((popupWindow.getWidth() / 2), 0, 0, 0);
                    down_arr_param.setMargins((popupWindow.getWidth() / 2), 0, 0, 0);
                }
                popupWindow.showAtLocation(videoItems.get(index).parent, Gravity.NO_GRAVITY, show_position_x + (videoItems.get(index).parent.getWidth() - popupWindow.getWidth()) / 2, show_position_y);
            }
        }
    }


    boolean is_show_teacher_window = true;

    private void showTeacherControlPop(final RoomUser user, final int index) {
        if (!is_show_teacher_window) {
            videoItems.get(index).view_choose_selected.setVisibility(View.GONE);
            is_show_teacher_window = true;
            return;
        }

        if (!(TKRoomManager.getInstance().getMySelf().role == 0)) {
            videoItems.get(index).view_choose_selected.setVisibility(View.GONE);
            return;
        }

        if (!RoomControler.isReleasedBeforeClass()) {
            if (!RoomSession.isClassBegin) {
                videoItems.get(index).view_choose_selected.setVisibility(View.GONE);
                return;
            }
        }
        //赋值
        solepopwindowPid = user.peerId;
        videoItems.get(index).view_choose_selected.setVisibility(View.VISIBLE);

        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tk_pop_teacher_control, null);
        ImageView img_up_arr = (ImageView) contentView.findViewById(R.id.up_arr);
        ImageView img_down_arr = (ImageView) contentView.findViewById(R.id.down_arr);
        LinearLayout lin_video_control = (LinearLayout) contentView.findViewById(R.id.lin_video_control);
        LinearLayout lin_audio_control = (LinearLayout) contentView.findViewById(R.id.lin_audio_control);
        LinearLayout lin_change = (LinearLayout) contentView.findViewById(R.id.lin_change);
        final ImageView img_video_control = (ImageView) contentView.findViewById(R.id.img_camera);
        final ImageView img_audio_control = (ImageView) contentView.findViewById(R.id.img_audio);
        final TextView txt_video = (TextView) contentView.findViewById(R.id.txt_camera);
        final TextView txt_audio = (TextView) contentView.findViewById(R.id.txt_audio);

        LinearLayout lin_single_fuwe = (LinearLayout) contentView.findViewById(R.id.lin_single_fuwe);
        if (RoomSession.isClassBegin && (videoItems.get(index).isMoved || videoItems.get(index).isSplitScreen)) {
            lin_single_fuwe.setVisibility(View.VISIBLE);
        } else {
            lin_single_fuwe.setVisibility(View.GONE);
        }

        if (RoomSession.isOnliyAudioRoom) {
            lin_video_control.setVisibility(View.GONE);
        } else {
            lin_video_control.setVisibility(View.VISIBLE);
        }

        LinearLayout lin_all_fuwe = (LinearLayout) contentView.findViewById(R.id.lin_all_fuwe);
        RelativeLayout.LayoutParams arr_param_teacher = (RelativeLayout.LayoutParams) img_up_arr.getLayoutParams();
        RelativeLayout.LayoutParams arr_down_teacher = (RelativeLayout.LayoutParams) img_down_arr.getLayoutParams();

        teaPopupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToManyActivity.this, 253f),
                KeyBoardUtil.dp2px(OneToManyActivity.this, 70f));
        teaPopupWindow.setContentView(contentView);

        if (mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE && index != 0) {
            lin_change.setVisibility(View.VISIBLE);
            lin_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teaPopupWindow.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("doubleId", videoItems.get(index).peerid);
                        TKRoomManager.getInstance().pubMsg("MainPeopleExchangeVideo", "MainPeopleExchangeVideo", "__all", jsonObject.toString(), true, "ClassBegin", null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            lin_change.setVisibility(View.GONE);
        }

        if (user.getPublishState() == 0 || user.getPublishState() == 2 || user.getPublishState() == 4) {
            img_audio_control.setImageResource(R.drawable.tk_icon_close_audio);
            txt_audio.setText(R.string.close_audio);
        } else {
            img_audio_control.setImageResource(R.drawable.tk_icon_open_audio);
            txt_audio.setText(R.string.open_audio);
        }
        if (user.getPublishState() == 0 || user.getPublishState() == 1 || user.getPublishState() == 4) {
            img_video_control.setImageResource(R.drawable.tk_icon_close_vidio);
            txt_video.setText(R.string.video_off);
        } else {
            img_video_control.setImageResource(R.drawable.tk_icon_open_vidio);
            txt_video.setText(R.string.video_on);
        }

        lin_single_fuwe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teaPopupWindow.dismiss();
                videoItems.get(index).isMoved = false;
                if (screenID.contains(videoItems.get(index).peerid)) {
                    screenID.remove(videoItems.get(index).peerid);
                }
                if (videoItems.get(index).isSplitScreen) {
                    videoItems.get(index).isSplitScreen = false;
                    SendingSignalling.getInstance().sendDoubleClickVideoRecovery(videoItems.get(index).peerid);
                }
                do1vsnStudentVideoLayout();
                SendingSignalling.getInstance().sendStudentMove(videoItems, mRootHolder.rel_students, mRootHolder.v_students);
            }
        });

        lin_all_fuwe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teaPopupWindow.dismiss();
                recoveryAllVideoTtems();
            }
        });

        lin_video_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当全音频时不开启视频
                if (RoomSession.isOnliyAudioRoom) {
                    return;
                }
                if (user.getPublishState() == 0 || user.getPublishState() == 1 || user.getPublishState() == 4) {
                    img_video_control.setImageResource(R.drawable.tk_icon_open_vidio);
                    txt_video.setText(R.string.video_on);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "publishstate", user.getPublishState() == 0 || user.getPublishState() == 4 ? 2 : 3);
                } else {
                    img_video_control.setImageResource(R.drawable.tk_icon_close_vidio);
                    txt_video.setText(R.string.video_off);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "publishstate", user.getPublishState() == 2 ? 4 : 1);
                }
                teaPopupWindow.dismiss();
            }
        });

        lin_audio_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getPublishState() == 0 || user.getPublishState() == 2 || user.getPublishState() == 4) {
                    img_audio_control.setImageResource(R.drawable.tk_icon_open_audio);
                    txt_audio.setText(R.string.open_audio);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "publishstate", user.getPublishState() == 0 || user.getPublishState() == 4 ? 1 : 3);
                } else {
                    img_audio_control.setImageResource(R.drawable.tk_icon_close_audio);
                    txt_audio.setText(R.string.close_audio);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "publishstate", user.getPublishState() == 3 ? 2 : 4);
                }
                teaPopupWindow.dismiss();
            }
        });

        teaPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                is_show_teacher_window = !Tools.isInView(event, videoItems.get(index).parent);
                return false;
            }
        });

        teaPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        teaPopupWindow.setFocusable(false);
        teaPopupWindow.setOutsideTouchable(true);
        teaPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //如果只有一位 助教点击老师下台 老师popwindow消失会数组越界
                if (videoItems.size() > index) {
                    if (videoItems.get(index).peerid.equals(user.peerId)) {
                        videoItems.get(index).view_choose_selected.setVisibility(View.GONE);
                    }
                }
            }
        });
        //获取点击view左上角相对于屏幕原点的x，y值
        int[] video_item = new int[2];
        videoItems.get(index).parent.getLocationInWindow(video_item);

        if ((mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE && index == 0)
                || (mLayoutState == LayoutPopupWindow.LAYOUT_VIDEO && videoItems.size() == 1)) {
            int width = videoItems.get(index).parent.getWidth();
            int hight = videoItems.get(index).parent.getHeight();
            teaPopupWindow.showAsDropDown(videoItems.get(index).parent, width / 2
                    - teaPopupWindow.getWidth() / 2, -hight / 2 - teaPopupWindow.getHeight() / 2);
            return;
        }

        int view_self_height = videoItems.get(index).parent.getHeight();
        int ab_height = video_item[1];

        if (videoItems.get(index).isSplitScreen) {
            img_down_arr.setVisibility(View.VISIBLE);
            img_up_arr.setVisibility(View.GONE);
            arr_down_teacher.setMargins(teaPopupWindow.getWidth() / 2, 0, 0, 0);
            if (videoItems.size() == 1) {
                teaPopupWindow.showAtLocation(videoItems.get(index).parent, Gravity.NO_GRAVITY,
                        video_item[0] + (videoItems.get(index).parent.getWidth() - teaPopupWindow.getWidth()) / 2, video_item[1]);
            } else {
                teaPopupWindow.showAtLocation(videoItems.get(index).parent, Gravity.NO_GRAVITY,
                        video_item[0] + (videoItems.get(index).parent.getWidth() - teaPopupWindow.getWidth()) / 2,
                        video_item[1] - teaPopupWindow.getHeight());
            }
        } else {
            int show_position_x = video_item[0];
            int show_position_y = 0;

            if ((ScreenScale.getScreenHeight() - ab_height - view_self_height) > 200) {
                //弹框在下边
                img_up_arr.setVisibility(View.VISIBLE);
                img_down_arr.setVisibility(View.GONE);
                show_position_y = video_item[1] + videoItems.get(index).parent.getHeight();
            } else {
                //弹框在上边
                img_up_arr.setVisibility(View.GONE);
                img_down_arr.setVisibility(View.VISIBLE);
                show_position_y = video_item[1] - teaPopupWindow.getHeight();
            }

            if ((ScreenScale.getScreenWidth() - video_item[0]) < teaPopupWindow.getWidth()) {
                //弹框在最右边，需要调整三角形的margin值
                int margin_left = video_item[0] + videoItems.get(index).parent.getMeasuredWidth() / 2
                        - (ScreenScale.getScreenWidth() - teaPopupWindow.getWidth());
                arr_param_teacher.setMargins(margin_left, 0, 0, 0);
                arr_down_teacher.setMargins(margin_left, 0, 0, 0);
                teaPopupWindow.showAtLocation(videoItems.get(index).parent, Gravity.NO_GRAVITY, show_position_x, show_position_y);
            } else {
                if ((video_item[0] + videoItems.get(index).parent.getWidth() / 2) < teaPopupWindow.getWidth() / 2) {
                    arr_param_teacher.setMargins((videoItems.get(index).parent.getWidth() / 2 + video_item[0]), 0, 0, 0);
                    arr_down_teacher.setMargins((videoItems.get(index).parent.getWidth() / 2), 0, 0, 0);
                } else {
                    arr_param_teacher.setMargins((teaPopupWindow.getWidth() / 2), 0, 0, 0);
                    arr_down_teacher.setMargins((teaPopupWindow.getWidth() / 2), 0, 0, 0);
                }
                teaPopupWindow.showAtLocation(videoItems.get(index).parent, Gravity.NO_GRAVITY, show_position_x
                        + (videoItems.get(index).parent.getWidth() - teaPopupWindow.getWidth()) / 2, show_position_y);
            }
        }
    }

    private void recoveryAllVideoTtems() {
        for (int x = 0; x < videoItems.size(); x++) {
            if (videoItems.get(x).isSplitScreen) {
                SendingSignalling.getInstance().sendDoubleClickVideoRecovery(videoItems.get(x).peerid);
            }
            videoItems.get(x).isSplitScreen = false;
            videoItems.get(x).isMoved = false;
        }
        scalemap.clear();
        screenID.clear();
        stuMoveInfoMap.clear();
        do1vsnStudentVideoLayout();
        //sendSplitScreen();
        SendingSignalling.getInstance().sendStudentMove(videoItems, mRootHolder.rel_students, mRootHolder.v_students);
        SendingSignalling.getInstance().sendScaleVideoItem(videoItems, false, printHeight);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_back || id == R.id.img_play_back_out) {
            showExitDialog();
        } else if (id == R.id.txt_class_begin) {
            if (RoomSession.isClassBegin) {
                showClassDissMissDialog();
            } else {
                if (RoomControler.haveTimeQuitClassroomAfterClass()) {
                    RoomOperation.getInstance().getSystemTime(this);
                } else {
                    RoomOperation.getInstance().startClass();
                }
            }
        } else if (id == R.id.flip_camera) {
            if (TKRoomManager.getInstance().getMySelf().hasVideo) {
                if (isFrontCamera) {
                    isFrontCamera = false;
                    TKRoomManager.getInstance().selectCameraPosition(false);
                } else {
                    isFrontCamera = true;
                    TKRoomManager.getInstance().selectCameraPosition(true);
                }
            } else {
                Toast.makeText(OneToManyActivity.this, getString(R.string.tips_camera), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.iv_open_input) {  //打开键盘
            boolean disablechat = TKRoomManager.getInstance().getMySelf().properties.containsKey("disablechat");
            if (Tools.isTure(disablechat) && Tools.isTure(TKRoomManager.getInstance().getMySelf().properties.get("disablechat"))) {
                Toast.makeText(OneToManyActivity.this, getString(R.string.the_user_is_forbid_speak), Toast.LENGTH_SHORT).show();
            } else {
                if (TKRoomManager.getInstance().getMySelf().role != 4) {
                    mInputWindowPop.showInputPopupWindow(mRootHolder.rel_wb.getWidth() * 6 / 10,
                            mRootHolder.rel_wb.getHeight(), mRootHolder.rel_wb, mRootHolder.cb_message, 0, false, this);
                }
            }
        } else if (id == R.id.cb_choose_shut_chat) {

            SendingSignalling.getInstance().sendBanChatMessage(mRootHolder.cb_choose_shut_chat.isChecked());

        } else if (id == R.id.lin_wifi) {
            if (wifiStatusPop.wifiStatusPop.isShowing()) {
                wifiStatusPop.dismiss();
                return;
            }
            wifiStatusPop.showWifiStatusPop(view);
            if (wifiStatusPop.wifiStatus == 1) {
                mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_you_sanjiao_up);
            } else if (wifiStatusPop.wifiStatus == 2) {
                mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_zhong_sanjiao_up);
            } else {
                mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_cha_sanjiao_up);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        switch (requestCode) {
            case PhotoUtils.PHOTO_REQUEST_CAREMA:
                if (isPauseLocalVideo) {
                    TKRoomManager.getInstance().resumeLocalCamera();
                    isPauseLocalVideo = !isPauseLocalVideo;
                }
                isOpenCamera = false;
                if (resultCode == RESULT_OK) {
                    //拍照上传图片
                    PhotoUtils.uploadCaremaImage(uri, this, requestCode, data);
                }
                break;
            case PhotoUtils.ALBUM_IMAGE:
                if (resultCode == RESULT_OK) {
                    //选择上传图片
                    PhotoUtils.uploadAlbumImage(this, data, mSelectImageType);
                }
                break;
            case EyeProtectionUtil.requestCodes:
                if (resultCode == RESULT_OK) {
                    mRootHolder.eye_protection.setChecked(true);
                }
                break;
        }
    }

    /**
     * 上课前播放自己本地视频
     */
    private void playSelfBeforeClassBegin() {
        RoomUser me = TKRoomManager.getInstance().getMySelf();
        if (me.role == 4 || me.role == -1) {
            return;
        }
        RoomSession.getInstance().getUserPublishStateList();
        //上课前是否发布音视频
        if (RoomControler.isReleasedBeforeClass() && me.getPublishState() == 0) {
            if (RoomSession.publishState.size() < RoomInfo.getInstance().getMaxVideo()) {
                if (RoomSession.teaPublish || me.role == 0) {
                    if (RoomSession.isOnliyAudioRoom) {
                        TKRoomManager.getInstance().changeUserProperty(me.peerId, "__all", "publishstate", 1);
                    } else {
                        TKRoomManager.getInstance().changeUserProperty(me.peerId, "__all", "publishstate", 3);
                    }
                } else {
                    if (RoomInfo.getInstance().getVideoSize() > 1) {
                        if (RoomSession.publishState.size() < RoomInfo.getInstance().getMaxVideo() - 1) {
                            TKRoomManager.getInstance().changeUserProperty(me.peerId, "__all", "publishstate", 3);
                        }
                    }
                }
            }
        } else {
            do1vsnClassBeginPlayVideo(me, me.peerId != null);
        }
    }

    /**
     * 上课前播放视频
     *
     * @param user  要播放的用户
     * @param force 用户id是否有 true有，false没有
     */
    private void do1vsnClassBeginPlayVideo(final RoomUser user, boolean force) {

        boolean hasSit = false;
        sitpos = -1;
        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(user.peerId)) {
                hasSit = true;
                sitpos = i;
            }
        }

        if (!hasSit) {

            VideoItemToMany stu = new VideoItemToMany(OneToManyActivity.this);
            stu.peerid = user.peerId;
            stu.role = user.role;
            stu.txt_name.setText(user.nickName);

            if (!RoomSession.isClassBegin) {//没上课视频框的权限图标不显示
                stu.img_pen.setVisibility(View.GONE);
                stu.img_pen.setVisibility(View.GONE);
                stu.img_hand.setVisibility(View.INVISIBLE);
                stu.img_mic.setVisibility(View.INVISIBLE);
                stu.volume.setVisibility(View.GONE);
            }
            changeVideoItemState(stu);
            if (user.role == 2) {
                stu.lin_gift.setVisibility(View.VISIBLE);
            } else {
                stu.lin_gift.setVisibility(View.INVISIBLE);
            }
            videoItems.add(stu);

            if (RoomControler.isOnlyShowTeachersAndVideos() && !user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) &&
                    TKRoomManager.getInstance().getMySelf().role == 2 && user.role != 0) {
                stu.setOnlyShowTeachersAndVideos(false);
                stu.parent.setVisibility(View.GONE);
            }

            mRootHolder.rel_students.addView(stu.parent);
            do1vsnStudentVideoLayout();

        } else if (force) {
            if (sitpos != -1) {
                changeVideoItemState(videoItems.get(sitpos));
            }
        }
    }

    /***
     *    上课后取消发布本地视频
     */
    private void unPlaySelfAfterClassBegin() {
        RoomUser me = TKRoomManager.getInstance().getMySelf();
        if (me.role == -1 || me.role == 4) {
            return;
        }
        if (me.getPublishState() != 3) {
            TKRoomManager.getInstance().unPlayVideo(me.peerId);
            for (int i = 0; i < videoItems.size(); i++) {
                if (videoItems.get(i).peerid.equals(me.peerId)) {
                    resetVideoitem(videoItems.get(i));
                    mRootHolder.rel_students.removeView(videoItems.get(i).parent);
                    videoItems.remove(i);
                    do1vsnStudentVideoLayout();
                }
            }
        }
    }

    private void setBackgroundOrReception(boolean b, RoomUser RoomUser) {
        for (int x = 0; x < videoItems.size(); x++) {
            if (videoItems.get(x).peerid.equals(RoomUser.peerId)) {
                if (b) {
                    videoItems.get(x).re_background.setVisibility(View.VISIBLE);
                } else {
                    videoItems.get(x).re_background.setVisibility(View.GONE);
                }
                if (videoItems.get(x).tv_home != null) {
                    if (RoomUser != null && RoomUser.role == 0) {
                        videoItems.get(x).tv_home.setText(R.string.tea_background);
                    } else {
                        videoItems.get(x).tv_home.setText(R.string.stu_background);
                    }
                }
            }
        }
    }

    private void removeMovieFragment() {
        //显示popupwindow
        setPopupWindowVisibility(View.VISIBLE);
        movieFragment = MovieFragment.getInstance();
        mediafragmentManager = getSupportFragmentManager();
        ft = mediafragmentManager.beginTransaction();
        if (movieFragment.isAdded()) {
            ft.remove(movieFragment);
            ft.commitAllowingStateLoss();
        }
        movieFragment = null;
    }

    private void removeScreenFragment() {
        //显示popupwindow
        setPopupWindowVisibility(View.VISIBLE);
        screenFragment = ScreenFragment.getInstance();
        mediafragmentManager = getSupportFragmentManager();
        ft = mediafragmentManager.beginTransaction();
        if (screenFragment.isAdded()) {
            ft.remove(screenFragment);
            ft.commitAllowingStateLoss();
        }
        screenFragment = null;
    }

    @Override
    public void onRoomDocChange(boolean isdel, boolean ismedia, ShareDoc doc) {
        if (RoomControler.isDocumentClassification()) {
            WhiteBoradConfig.getsInstance().getClassDocList();
            WhiteBoradConfig.getsInstance().getAdminDocList();
            WhiteBoradConfig.getsInstance().getClassMediaList();
            WhiteBoradConfig.getsInstance().getAdminmMediaList();
        } else {
            WhiteBoradConfig.getsInstance().getDocList();
            WhiteBoradConfig.getsInstance().getMediaList();
        }
        if (!ismedia) {
            fileListAdapter.changDocData(doc);
        }
        fileListAdapter.notifyDataSetChanged();
        mediaListAdapter.notifyDataSetChanged();
    }

    /***
     *     白板放大缩小
     * @param isZoom  true放大  false缩小
     */
    @Override
    public void onWhiteBoradZoom(final boolean isZoom) {
        this.isZoom = isZoom;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isZoom) {
                    setWhiteBoradEnlarge(isZoom);
                } else {
                    setWhiteBoradNarrow(isZoom);
                }
                //白板是否全屏控制聊天弹窗
//                whiteBoardZoomToChat(isZoom);
                //产品需求改动默认全部展示
                whiteBoardZoomToChat(false);
                if (RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == 0 && RoomControler.isFullScreenVideo()) {
                    //发送全屏信令
                    SendingSignalling.getInstance().sendFullScreenMsg(isZoom);
                }
            }
        });
    }

    /***
     *  白板放大
     * @param isZoom
     */
    private void setWhiteBoradEnlarge(boolean isZoom) {
        this.isZoom = isZoom;
        //隐藏视频框
        hideSurfaceview();
        mRootHolder.rel_students.setVisibility(View.GONE);
        mRootHolder.lin_audio_seek.setVisibility(View.GONE);
        mRootHolder.fl_play_disk.setVisibility(View.GONE);
        mRootHolder.rel_tool_bar.setVisibility(View.GONE);
        mRootHolder.side_view.setVisibility(View.GONE);
        mRootHolder.v_students.setVisibility(View.INVISIBLE);
        //设置白板大小
        setWhiteBoardSize();
        //设置画布大小
        setWhiteBoardCanvasSize();
        if (mPagesView != null) {
            mPagesView.setfull(isZoom);
            mPagesView.SetFragementView(mRootHolder.rel_wb_container);
        }
        WhiteBoradConfig.getsInstance().sendJSPageFullScreen(isZoom);
    }

    /***
     *     白板缩小
     * @param isZoom
     */
    private void setWhiteBoradNarrow(boolean isZoom) {
        if (LayoutPopupWindow.LAYOUT_VIDEO == mLayoutState || LayoutPopupWindow.LAYOUT_DOUBLE == mLayoutState) {
            return;
        }
        this.isZoom = isZoom;
        //设置白板大小
        setWhiteBoardSize();
        //设置画笔大小
        setWhiteBoardCanvasSize();
        if (mPagesView != null) {
            mPagesView.setfull(isZoom);
            mPagesView.SetFragementView(mRootHolder.rel_wb_container);
        }

        //当有视频未被拖拽时
        if (notMoveVideoItems.size() != 0) {
            mRootHolder.v_students.setVisibility(View.VISIBLE);
        }
        mRootHolder.rel_students.setVisibility(View.VISIBLE);
        mRootHolder.rel_tool_bar.setVisibility(View.VISIBLE);
        // mRootHolder.side_view.setVisibility(View.VISIBLE);

        if (RoomSession.isPublishMp3) {
            mRootHolder.fl_play_disk.setVisibility(View.VISIBLE);
            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                mRootHolder.lin_audio_seek.setVisibility(View.VISIBLE);
            }
        }

        if (!RoomSession.isPublishMp4 && !RoomSession.isShareFile && !RoomSession.isShareScreen) {
            RoomSession.getInstance().getPlatformMemberList();
            for (int i = 0; i < RoomSession.playingList.size(); i++) {
                RoomUser user = RoomSession.playingList.get(i);
                if (user == null) {
                    return;
                }
                doPlayVideo(user.peerId);
            }
        }

        if (!RoomSession.isClassBegin) {
            playSelfBeforeClassBegin();
        }
        WhiteBoradConfig.getsInstance().sendJSPageFullScreen(isZoom);
    }

    /**
     * 白板是否全屏控制聊天弹窗
     */
    private void whiteBoardZoomToChat(boolean isZoom) {
        if (isZoom) {
            mRootHolder.cb_message.setChecked(false);
            mRootHolder.cb_message.setVisibility(View.GONE);
            mRootHolder.tv_no_read_message_number.setVisibility(View.GONE);
        } else {
            mRootHolder.cb_message.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mRootHolder.tv_no_read_message_number.getText().toString())) {
                mRootHolder.tv_no_read_message_number.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 默认总页数为0，通过这个值设置页数选择弹框中listView的数据
     */
    int scale = 1;//白板的缩放比例 0是4：3   1是16：9 默认
    int dolayoutsum4 = 0;
    int dolayoutsum16 = 0;
    int dolayoutsumall = 0;
    double irregular = 0.0;

    @Override
    public void onWhiteBoradAction(final String stateJson) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(stateJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonObject != null && jsonObject.has("scale")) {
                        scale = jsonObject.optInt("scale");
                        if (scale == 2) {
                            irregular = jsonObject.optDouble("irregular");
                            // GeneralFile(PDF / 图片) 的 scale 都是2，需要重新计算白板大小
                            dolayoutsumall = 0;
                        }

                        if (scale == 0) {
                            if (dolayoutsum4 == 0) {
                                dolayoutsum4++; //1
                            }

                        } else if (scale == 1) {
                            if (dolayoutsum16 == 0) {
                                dolayoutsum16++;
                            }

                        } else if (scale == 2) {
                            if (dolayoutsumall == 0) {
                                dolayoutsumall++;
                            }
                        }

                        if (dolayoutsum4 == 1) {
                            dolayoutsum4++;  //2
                            setWhiteBoardSize();
                            setWhiteBoardCanvasSize();
                            dolayoutsum16 = 0;
                            dolayoutsumall = 0;
                        }
                        if (dolayoutsum16 == 1) {
                            dolayoutsum16++;
                            setWhiteBoardSize();
                            setWhiteBoardCanvasSize();
                            dolayoutsum4 = 0;
                            dolayoutsumall = 0;
                        }

                        if (dolayoutsumall == 1) {
                            dolayoutsumall++;
                            setWhiteBoardSize();
                            setWhiteBoardCanvasSize();
                            dolayoutsum4 = 0;
                            dolayoutsum16 = 0;
                        }

                        if (mPagesView != null) {
                            mPagesView.setAction(stateJson);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //设置View的大小
        setViewSize();
    }

    /**
     * 文件下载解压进度回调
     *
     * @param index 进度
     * @param type  1 下载 2 解压
     */
    @Override
    public void onDownloadProgress(final int index, final int type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRootHolder != null) {
                    if (type == 1) {
                        if (mRootHolder.fl_downloadProgress.getVisibility() == View.GONE && !isJumpOver) {
                            mRootHolder.fl_downloadProgress.setVisibility(View.VISIBLE);
                            setTitleBarContentVisibility(View.GONE);
                        }
                        mRootHolder.fl_downloadProgress.setProgress(getString(R.string.docDownload), index);
                    }
                    if (type == 2) {
                        if (index >= 0 && index <= 99) {
                            if (mRootHolder.fl_downloadProgress.getVisibility() == View.GONE && !isJumpOver) {
                                mRootHolder.fl_downloadProgress.setVisibility(View.VISIBLE);
                                setTitleBarContentVisibility(View.GONE);
                            }
                        }
                        mRootHolder.fl_downloadProgress.setProgress(getString(R.string.doc_unzipping), index);
                        if (index == 100) {
                            mRootHolder.fl_downloadProgress.setVisibility(View.GONE);
                            setTitleBarContentVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void hideDownload(final boolean ishide) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRootHolder.fl_downloadProgress != null) {
                    if (ishide && !isJumpOver) {
                        mRootHolder.fl_downloadProgress.setVisibility(View.VISIBLE);
                        setTitleBarContentVisibility(View.GONE);
                    } else {
                        mRootHolder.fl_downloadProgress.setVisibility(View.GONE);
                        setTitleBarContentVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void clear() {
        TKRoomManager.getInstance().registerRoomObserver(null);
        for (int i = 0; i < videoItems.size(); i++) {
            videoItems.get(i).sf_video.release();
        }
        WhiteBoradConfig.getsInstance().clear();
        RoomDeviceSet.closeSpeaker(this);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    public void showExitDialog() {
        Tools.showDialog(OneToManyActivity.this, R.string.remind, getString(R.string.logouts), new Tools.OnDialogClick() {
            @Override
            public void dialog_ok(Dialog dialog) {
                sendGiftPopUtils.deleteImage();
                TKRoomManager.getInstance().leaveRoom();
                dialog.dismiss();
            }
        });
    }

    public void showClassDissMissDialog() {
        Tools.showDialog(OneToManyActivity.this, R.string.remind, getString(R.string.make_sure_class_dissmiss), new Tools.OnDialogClick() {
            @Override
            public void dialog_ok(Dialog dialog) {
                TKRoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new HashMap<String, Object>());
                mRootHolder.txt_class_begin.setVisibility(View.GONE);
                RoomOperation.getInstance().sendClassDissToPhp();
                dialog.dismiss();
            }
        });
    }

    public void readyForPlayVideo(String shareMediaPeerId, Map<String, Object> shareMediaAttrs) {

        if (isZoom && RoomControler.isFullScreenVideo()) {
            FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem, null, false);
        }

        //隐藏popupwindow
        setPopupWindowVisibility(View.GONE);
        mRootHolder.rel_fullscreen_videoitem.findViewById(R.id.fullscreen_sf_video).setVisibility(View.GONE);
        videofragment = VideoFragment.getInstance();
        videofragment.setFullscreen_video_param(fullscreen_video_param);
        videofragment.setStream(shareMediaPeerId, shareMediaAttrs);
        mediafragmentManager = getSupportFragmentManager();
        ft = mediafragmentManager.beginTransaction();
        if (!videofragment.isAdded()) {
            mRootHolder.video_container.setVisibility(View.VISIBLE);
            ft.replace(R.id.video_container, videofragment);
            ft.commitAllowingStateLoss();
        }
    }

    public void removeVideoFragment() {
        //显示popupwindow
        setPopupWindowVisibility(View.VISIBLE);
        if (mRootHolder.video_container != null) {
            mRootHolder.video_container.setVisibility(View.GONE);
        }

        videofragment = VideoFragment.getInstance();
        mediafragmentManager = getSupportFragmentManager();
        ft = mediafragmentManager.beginTransaction();
        mediaListAdapter.setLocalfileid(-1);
        ft.remove(videofragment);
        ft.commitAllowingStateLoss();
        videofragment = null;
    }

    private void changeUserState(RoomUser user) {
        if (user == null) {
            return;
        }

        for (int i = 0; i < videoItems.size(); i++) {
            if (user.peerId.equals(videoItems.get(i).peerid)) {
                if (user.properties.containsKey("candraw")) {
                    boolean candraw = Tools.isTure(user.properties.get("candraw"));
                    if (candraw) {
                        videoItems.get(i).img_pen.setImageResource(R.drawable.tk_icon_shouquan);
                        if (RoomSession.isClassBegin) {
                            videoItems.get(i).img_pen.setVisibility(View.VISIBLE);//可以画图bg_
                            videoItems.get(i).bg_img_pen.setVisibility(View.VISIBLE);//可以画图bg_
                            if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && toolsView != null) {
                                toolsView.showTools(true);
                            }
                        } else {
                            videoItems.get(i).img_pen.setVisibility(View.GONE);
                            videoItems.get(i).bg_img_pen.setVisibility(View.GONE);
                            if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && toolsView != null) {
                                toolsView.showTools(false);
                            }
                        }
                    } else {
                        videoItems.get(i).img_pen.setImageResource(R.drawable.tk_icon_quxiaoshouquan);
                        videoItems.get(i).img_pen.setVisibility(View.GONE);//不可以画图
                        videoItems.get(i).bg_img_pen.setVisibility(View.GONE);//不可以画图
                        if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && toolsView != null) {
                            toolsView.showTools(false);
                        }
                    }
                } else {
                    videoItems.get(i).img_pen.setVisibility(View.GONE);//没给过画图权限
                    videoItems.get(i).bg_img_pen.setVisibility(View.GONE);//没给过画图权限
                    videoItems.get(i).img_pen.setImageResource(R.drawable.tk_icon_quxiaoshouquan);
                    if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && toolsView != null) {
                        toolsView.showTools(false);
                    }
                }

                if (user.properties.containsKey("primaryColor")) {
                    String primaryColor = (String) user.properties.get("primaryColor");
                    if (!TextUtils.isEmpty(primaryColor)) {
                        CommonUtil.changeBtimapColor(videoItems.get(i).img_pen, primaryColor);
                        toolsView.setToolPenColor();
                    }
                } else {
                    SetRoomInfor.getInstance().setUserPenColor(user);
                }

                if (user.properties.containsKey("raisehand")) {
                    boolean israisehand = Tools.isTure(user.properties.get("raisehand"));
                    if (israisehand) {
                        if (user.role == 2) {
                            videoItems.get(i).img_hand.setVisibility(View.VISIBLE);//正在举手
                        }
                    } else {
                        videoItems.get(i).img_hand.setVisibility(View.INVISIBLE);//同意了，或者拒绝了
                    }
                } else {
                    videoItems.get(i).img_hand.setVisibility(View.INVISIBLE);//还没举手
                }

                if (user.properties.containsKey("giftnumber")) {
                    long giftnumber = user.properties.get("giftnumber") instanceof Integer ? (int) user.properties.get("giftnumber") : (long) user.properties.get("giftnumber");
                    videoItems.get(i).txt_gift_num.setText(String.valueOf(giftnumber));
                } else {
                    videoItems.get(i).txt_gift_num.setText("0");
                }

                if (user.properties.containsKey("isInBackGround")) {
                    boolean isinback = Tools.isTure(user.properties.get("isInBackGround"));
                    setBackgroundOrReception(isinback, user);
                }
            }
        }
        //设置监听
        SortVideoItemsOnClick();
    }

    /***
     *  视频框状态改变
     */
    private void changeVideoState() {
        if (!isZoom) {
            //获取用户视频状态
            for (int i = 0; i < videoItems.size(); i++) {
                changeVideoItemState(videoItems.get(i));
            }
        } else {
            if (RoomControler.isFullScreenVideo() && RoomSession.fullScreen) {
                for (int i = 0; i < RoomSession.playingList.size(); i++) {
                    if (RoomSession.playingList.get(i).role == 0) {
                        FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem, RoomSession.playingList.get(i), true);
                        break;
                    }
                }
            }
        }
    }

    /***
     *  视频框状态改变
     * @param videoItem
     */
    private void changeVideoItemState(VideoItemToMany videoItem) {

        if (videoItem == null || videoItem.peerid == null || videoItem.peerid.isEmpty()) {
            return;
        }

        RoomUser roomUser = TKRoomManager.getInstance().getUser(videoItem.peerid);
        if (roomUser == null) {
            return;
        }

        if (RoomSession.isOnliyAudioRoom) {
            videoItem.bg_video_back.setVisibility(View.VISIBLE);
            videoItem.img_video_back.setVisibility(View.VISIBLE);
            if (videoItem.sf_video != null) {
                videoItem.sf_video.setVisibility(View.INVISIBLE);
            }
            videoItem.img_video_back.setImageResource(R.drawable.tk_zhanwei_audio);

        } else {
            videoItem.img_video_back.setImageResource(R.drawable.tk_icon_camera_close);

            if (roomUser.disablevideo || !roomUser.hasVideo) {
                if (videoItem.sf_video != null) {
                    videoItem.sf_video.setVisibility(View.INVISIBLE);
                }
                videoItem.bg_video_back.setVisibility(View.VISIBLE);
                videoItem.img_video_back.setVisibility(View.VISIBLE);
                videoItem.img_video_back.setImageResource(R.drawable.tk_icon_no_camera);
            } else {
                if (roomUser.getPublishState() > 1 && roomUser.getPublishState() < 4 && !RoomSession.isShareFile && !RoomSession.isPublishMp4 && !RoomSession.isShareScreen) {
                    if (videoItem.sf_video != null) {
                        videoItem.sf_video.setVisibility(View.VISIBLE);
                    }
                    //videoItem.bg_video_back.setVisibility(View.GONE);
                    videoItem.img_video_back.setVisibility(View.GONE);
                    if (videoItem.isOnlyShowTeachersAndVideos || videoItem.isSplitScreen || videoItem.isMoved) {
                        // 全屏时，不playvideo
                        if (!(RoomSession.isClassBegin && isZoom && RoomControler.isFullScreenVideo() && RoomSession.fullScreen)) {
                            TKRoomManager.getInstance().playVideo(roomUser.peerId, videoItem.sf_video,
                                    RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                        }
                    } else {
                        TKRoomManager.getInstance().unPlayVideo(roomUser.peerId);
                    }
                } else {
                    if (!RoomSession.isClassBegin && !RoomControler.isReleasedBeforeClass()) {
                        if (videoItem.sf_video != null) {
                            videoItem.sf_video.setVisibility(View.VISIBLE);
                        }
                        //videoItem.bg_video_back.setVisibility(View.GONE);
                        videoItem.img_video_back.setVisibility(View.GONE);
                        if (videoItem.isOnlyShowTeachersAndVideos || videoItem.isSplitScreen || videoItem.isMoved) {
                            // 全屏时，不playvideo
                            if (!(RoomSession.isClassBegin && isZoom && RoomControler.isFullScreenVideo() && RoomSession.fullScreen)) {
                                TKRoomManager.getInstance().playVideo(roomUser.peerId, videoItem.sf_video,
                                        RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                            }
                        } else {
                            TKRoomManager.getInstance().unPlayVideo(roomUser.peerId);
                        }
                    } else {
                        if (videoItem.sf_video != null) {
                            videoItem.sf_video.setVisibility(View.INVISIBLE);
                        }
                        videoItem.bg_video_back.setVisibility(View.VISIBLE);
                        videoItem.img_video_back.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    /**
     * 设置白板大小
     */
    public void setWhiteBoardSize() {
        if (LayoutPopupWindow.LAYOUT_VIDEO == mLayoutState || LayoutPopupWindow.LAYOUT_DOUBLE == mLayoutState) {
            return;
        }
        int height = 0;
        RelativeLayout.LayoutParams rel_wb_layoutparams = (RelativeLayout.LayoutParams) mRootHolder.rel_wb.getLayoutParams();
        if (isZoom) {
            rel_wb_layoutparams.width = wid;
            rel_wb_layoutparams.height = hid;
        } else {
            int toolbarHeight = mRootHolder.rel_tool_bar.getLayoutParams().height;
            if (notMoveVideoItems.size() == 0) {
                rel_wb_layoutparams.width = wid;
                rel_wb_layoutparams.height = hid - toolbarHeight;
                mRootHolder.v_students.setVisibility(View.INVISIBLE);
            } else {
                int studentsHeight = (wid - 8 * 8) / 7 * hid_ratio / wid_ratio + 16;
                if ("MI PAD 4".equals(android.os.Build.MODEL)) {//判断是否为小米pad4，解决白板问题
                    height = (hid - studentsHeight - 4);
                    rel_wb_layoutparams.width = wid;
                    rel_wb_layoutparams.height = height;
                } else {
                    height = (hid - toolbarHeight - studentsHeight - 4);
                    rel_wb_layoutparams.width = wid;
                    rel_wb_layoutparams.height = height;
                }
                mRootHolder.v_students.setVisibility(View.VISIBLE);
            }
        }

        mRootHolder.rel_wb.setLayoutParams(rel_wb_layoutparams);
        mRootHolder.rel_wb_container.setLayoutParams(rel_wb_layoutparams);

        if (toolsView != null) {
            toolsView.doLayout(rel_wb_layoutparams.width, rel_wb_layoutparams.height);
        }
        if (mPagesView != null) {
            mPagesView.doLayout(rel_wb_layoutparams.width, rel_wb_layoutparams.height);
            mPagesView.SetFragementView(mRootHolder.rel_wb_container);
        }

        if (isHaiping) {
            RelativeLayout.LayoutParams rel_students_par = (RelativeLayout.LayoutParams) mRootHolder.rel_students.getLayoutParams();
            rel_students_par.leftMargin = heightStatusBar;
            mRootHolder.rel_students.setLayoutParams(rel_students_par);

            RelativeLayout.LayoutParams rel_wb_par = (RelativeLayout.LayoutParams) mRootHolder.rel_wb.getLayoutParams();
            rel_wb_par.leftMargin = heightStatusBar;
            mRootHolder.rel_wb.setLayoutParams(rel_wb_par);
        }

        if (wbFragment != null && WBSession.isPageFinish) {
            WhiteBoradConfig.getsInstance().SetTransmitWindowSize(rel_wb_layoutparams.width, rel_wb_layoutparams.height);
        }
    }

    /***
     *   设置白板画布的大小
     */
    private void setWhiteBoardCanvasSize() {

        int WhiteBoardheight = mRootHolder.rel_wb.getLayoutParams().height;
        int WhiteBoardwidth = mRootHolder.rel_wb.getLayoutParams().width;

        if (scale == 0) {//4:3
            WhiteBoardwidth = WhiteBoardheight * 4 / 3;
            if (WhiteBoardwidth > wid) {//宽度超出屏幕
                WhiteBoardwidth = wid;
                WhiteBoardheight = wid * 3 / 4;
            }
        } else if (scale == 1) {//16:9
            WhiteBoardwidth = WhiteBoardheight * 16 / 9;
            if (WhiteBoardwidth > wid) {//宽度超出屏幕
                WhiteBoardwidth = wid;
                WhiteBoardheight = wid * 9 / 16;
            }
        } else if (scale == 2) {//没有比例 设置为最大
            WhiteBoardwidth = wid;
            if (irregular * WhiteBoardheight <= WhiteBoardwidth) {
                WhiteBoardwidth = (int) (irregular * WhiteBoardheight);
            } else {
                WhiteBoardheight = (int) (WhiteBoardwidth / irregular);
            }
        }

        if (wbFragment != null && WBSession.isPageFinish) {
            WhiteBoradConfig.getsInstance().SetFaceShareSize(WhiteBoardwidth, WhiteBoardheight);
            WhiteBoradConfig.getsInstance().setPaintFaceShareFullScreen(isZoom);
        }
    }

    //计算可移动videoitems 点击摄像头放大
    public void setmoveVideoItems() {
        ArrayList<VideoItemToMany> splitScreen = new ArrayList<VideoItemToMany>();
        splitScreen.clear();
        boolean isScreen = false;
        for (int x = 0; x < movedVideoItems.size(); x++) {
            if (movedVideoItems.get(x).isSplitScreen) {
                isScreen = true;
                splitScreen.add(movedVideoItems.get(x));
            }
        }
        if (isScreen) {
            int size = movedVideoItems.size();
            if (size > 0 && size <= 2) {
                VideoTtemLayoutUtils.screenLessThree(splitScreen, mRootHolder.rel_wb_container, nameLabelHeight);
            }
        }
    }

    /**
     * 设置标题栏处返回按键外的其他控件显示隐藏
     *
     * @param visible View.Visible View.Gone
     */
    private void setTitleBarContentVisibility(int visible) {
        CommonUtil.setTimeVisibility(mRootHolder, visible);
        mRootHolder.lin_wifi.setVisibility(visible);
        mRootHolder.re_top_right.setVisibility(visible);
    }

    //学生布局
    public void do1vsnStudentVideoLayout() {
        //如果是主讲视频 每次上台前判断 是否是上次保存在主讲位置id
        if (mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE) {
            transitionVideoItems();
        }
        notMoveVideoItems.clear();
        movedVideoItems.clear();
        for (int i = 0; i < videoItems.size(); i++) {
            videoItems.get(i).parent.setVisibility(View.VISIBLE);
            if (videoItems.get(i).isSplitScreen) {
                movedVideoItems.add(videoItems.get(i));
            } else if (videoItems.get(i).isMoved) {
                movedVideoItems.add(videoItems.get(i));
            } else if (videoItems.get(i).isOnlyShowTeachersAndVideos) {
                notMoveVideoItems.add(videoItems.get(i));
            } else {
                videoItems.get(i).parent.setVisibility(View.GONE);
            }
        }

        switch (mLayoutState) {
            case LayoutPopupWindow.LAYOUT_NORMAL:   // 视频置顶 计算视频框方法
                //白板大小
                setWhiteBoardSize();
                setWhiteBoardCanvasSize();
                //拖动状态下videoitems
                setmoveVideoItems();
                OnetoManyLayoutUtil.routineDoLayout(notMoveVideoItems, mScreenValueWidth, wid_ratio, hid_ratio);
                break;
            case LayoutPopupWindow.LAYOUT_DOUBLE:  // 主讲视频
                /*mRootHolder.rel_parent.setBackgroundColor(Color.WHITE);*/
                OnetoManyLayoutUtil.speakVideoDoLayout(notMoveVideoItems, mScreenValueWidth,
                        hid - (int) (wid / 7 * 3 / 4 * 0.4) - mRootHolder.side_view.getHeight(),
                        wid_ratio, hid_ratio, mRootHolder, (int) printWidth, (int) printHeight, soleOnlyId);
                break;
            case LayoutPopupWindow.LAYOUT_VIDEO:     // 自由视频布局
                /*mRootHolder.rel_parent.setBackgroundColor(Color.WHITE);*/
                //mRootHolder.rel_fullscreen_videoitem.findViewById(R.id.fullscreen_sf_video).setVisibility(View.GONE);
                OneToManyFreeLayoutUtil.getInstance().freeVideoDoLayout(notMoveVideoItems, mScreenValueWidth,
                        hid - toolBarHeight - 4, heightStatusBar, wid_ratio, hid_ratio);
                break;
            default:
                break;
        }
        //重新设置监听
        SortVideoItemsOnClick();
    }

    //设置videoitem监听
    public void SortVideoItemsOnClick() {
        for (int i = 0; i < videoItems.size(); i++) {
            final int finalI = i;
            final View view = videoItems.get(i).parent;
            if (videoItems.get(i).role == 0 || videoItems.get(i).role == 1) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTeacherControlPop(TKRoomManager.getInstance().getUser(videoItems.get(finalI).peerid), finalI);
                    }
                });
            } else {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (videoItems.size() > finalI) {
                            RoomUser user = TKRoomManager.getInstance().getUser(videoItems.get(finalI).peerid);
                            showStudentControlPop(view, user, finalI);
                        }
                    }
                });
            }
        }
    }

    //排序videoitems 在主讲模式下 学生替换老师，老师位于学生位置第一位
    public void SortVideoItems(ArrayList<VideoItemToMany> videos) {
        int num = 0;
        Iterator<VideoItemToMany> item = videos.iterator();
        VideoItemToMany videoItem1 = null;
        VideoItemToMany videoItem = null;
        while (item.hasNext()) {

            VideoItemToMany videoItemToMany = item.next();
            if (num == 0) {
                videoItem1 = videoItemToMany;
                item.remove();
                if (videoItemToMany.role == 0) {
                    break;
                }
                num++;
                continue;
            }
            if (videoItemToMany.role == 0) {
                videoItem = videoItemToMany;
                item.remove();
                break;
            }
        }
        //排序
        PeerIDComparator peerIDComparator = new PeerIDComparator();
        peerIDComparator.setisUp(true);
        Collections.sort(videos, peerIDComparator);

        if (videoItem1 != null) {
            videos.add(0, videoItem1);
        }
        if (videoItem != null) {
            videos.add(1, videoItem);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_file_person_media_list) {   //文件列表
            if (isChecked) {
                mRootHolder.cb_file_person_media_list.setEnabled(false);
                if (mRootHolder.cb_tool_case.isChecked()) {
                    mRootHolder.cb_tool_case.setChecked(false);
                }
                coursePopupWindowUtils.showCoursePopupWindow(mRootHolder.rel_parent, mRootHolder.cb_file_person_media_list,
                        mRootHolder.rel_parent.getWidth() / 10 * 5, mRootHolder.rel_parent.getHeight());
            } else {
                coursePopupWindowUtils.dismissPopupWindow();
            }
        } else if (id == R.id.cb_member_list) {    //  花名册
            mRootHolder.iv_hand.setVisibility(View.INVISIBLE);
            controlHandViewVisiable(false);
            if (isChecked) {
                if (mRootHolder.cb_tool_case.isChecked()) {
                    mRootHolder.cb_tool_case.setChecked(false);
                }
                mRootHolder.cb_member_list.setEnabled(false);
                RoomOperation.getInstance().getBigRoomUnmberAndUsers(this);
                memberListPopupWindowUtils.showMemberListPopupWindow(mRootHolder.rel_parent, mRootHolder.cb_member_list, mRootHolder.rel_parent.getWidth() / 10 * 5, mRootHolder.rel_parent.getHeight());
            } else {
                if (RoomOperation.numberTimer != null) {
                    RoomOperation.numberTimer.cancel();
                    RoomOperation.numberTimer = null;
                }
                memberListPopupWindowUtils.dismissPopupWindow();
            }
        } else if (id == R.id.cb_tool_case) {    //  工具箱
            if (isChecked) {
                mRootHolder.cb_tool_case.setEnabled(false);
                ToolsPopupWindow.getInstance().showPopupWindow(buttonView);
                ToolsPopupWindow.getInstance().getToolsPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mRootHolder.cb_tool_case.setChecked(false);
                        mRootHolder.cb_tool_case.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRootHolder.cb_tool_case.setEnabled(true);
                            }
                        }, 100);
                    }
                });
            } else {
                ToolsPopupWindow.getInstance().dismiss();
            }
        } else if (id == R.id.cb_control) {   //  全体行为
            KeyBoardUtil.hideInputMethod(this);
            if (isChecked) {
                mRootHolder.cb_control.setEnabled(false);
                if (mRootHolder.cb_tool_case.isChecked()) {
                    mRootHolder.cb_tool_case.setChecked(false);
                }
                allActionUtils.showAllActionView(buttonView, mRootHolder.cb_control, isMute, is_have_student, isAudioTeaching);
            } else {
                allActionUtils.dismissPopupWindow();
            }
        } else if (id == R.id.cb_message) {    //聊天列表
            if (isChecked) {
                clearNoReadChatMessage();
                //打开聊天列表
                showChatPopupWindow();
            } else {
                closeChatWindow();
            }
        } else if (id == R.id.cb_choose_photo) {
            if (isChecked) {
                UploadPhotoPopupWindowUtils.getInstance().showPopupWindow(this,
                        mRootHolder.cb_choose_photo, this);
            } else {
                UploadPhotoPopupWindowUtils.getInstance().setDismiss();
            }
        } else if (id == R.id.cb_tool_layout) { //布局切换
            if (isChecked) {
                mRootHolder.cb_tool_layout.setEnabled(false);
                LayoutPopupWindow.getInstance().showPopupWindow(buttonView);
                LayoutPopupWindow.getInstance().getLayoutPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mRootHolder.cb_tool_layout.setChecked(false);
                        mRootHolder.cb_tool_layout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRootHolder.cb_tool_layout.setEnabled(true);
                            }
                        }, 100);
                    }
                });
            } else {
                LayoutPopupWindow.getInstance().dismiss();
            }
        }
        KeyBoardUtil.hideInputMethod(this);
    }

    /**
     * 展示聊天列表弹窗
     */
    private void showChatPopupWindow() {
        if (mRootHolder.lv_chat_list.getVisibility() == View.VISIBLE) {
            return;
        }
        mRootHolder.lv_chat_list.setVisibility(View.VISIBLE);
        mRootHolder.lv_chat_list.setEnabled(true);
        mRootHolder.cb_message.setChecked(true);
        mRootHolder.cb_message.setVisibility(View.VISIBLE);

        if (TKRoomManager.getInstance().getMySelf().role == 0) {//老师
            //留言板按钮  聊天输入框按钮 全体禁言
            mRootHolder.iv_open_input.setVisibility(View.VISIBLE);
            mRootHolder.cb_choose_shut_chat.setVisibility(View.VISIBLE);
        } else if (TKRoomManager.getInstance().getMySelf().role == 2) {//学生
            //留言板按钮  聊天输入框按钮 全体禁言
            mRootHolder.iv_open_input.setVisibility(View.VISIBLE);
            mRootHolder.cb_choose_shut_chat.setVisibility(View.GONE);
        } else if (TKRoomManager.getInstance().getMySelf().role == 4) {
            mRootHolder.iv_open_input.setVisibility(View.INVISIBLE);
            mRootHolder.cb_choose_shut_chat.setVisibility(View.GONE);
        } else if (TKRoomManager.getInstance().getMySelf().role == -1) {//回放
            mRootHolder.iv_open_input.setVisibility(View.VISIBLE);
            mRootHolder.cb_choose_shut_chat.setVisibility(View.GONE);
        }

        if (mRootHolder.lv_chat_list.getVisibility() == View.VISIBLE) {
            mRootHolder.lv_chat_list.startAnimation((AlphaAnimation) AnimationUtils.
                    loadAnimation(OneToManyActivity.this, R.anim.tk_chatpopshow_anim));
        }
        if (mRootHolder.iv_open_input.getVisibility() == View.VISIBLE) {
            mRootHolder.iv_open_input.startAnimation((AnimationSet) AnimationUtils.
                    loadAnimation(OneToManyActivity.this, R.anim.tk_chat_button_show_anim));
        }
        if (mRootHolder.cb_choose_shut_chat.getVisibility() == View.VISIBLE) {
            mRootHolder.cb_choose_shut_chat.startAnimation((AnimationSet) AnimationUtils.
                    loadAnimation(OneToManyActivity.this, R.anim.tk_chat_button_show_anim));
        }
    }

    @Override
    public void close_window() {
        mRootHolder.cb_file_person_media_list.setChecked(false);
        mRootHolder.cb_file_person_media_list.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootHolder.cb_file_person_media_list.setEnabled(true);
            }
        }, 100);
    }

    @Override
    public void take_photo() {
        cameraClickListener(TKBaseActivity.KEJIAN_SELECT_IMAGE);
    }

    @Override
    public void choose_photo() {
        photoClickListener(TKBaseActivity.KEJIAN_SELECT_IMAGE);
    }

    public void closeChatWindow() {
        //聊天列表隐藏动画
        AnimationUtil.getInstance(this).hideChatLists(mRootHolder.lv_chat_list, this);
        //输入框按钮隐藏动画
        AnimationUtil.getInstance(this).hideViewAniamtion(mRootHolder.iv_open_input, this);
        //全体禁言按钮隐藏动画
        AnimationUtil.getInstance(this).hideViewAniamtion(mRootHolder.cb_choose_shut_chat, this);
    }

    /**
     * 判断是否是全体静音
     */
    boolean isMute = true;
    /**
     * 判断台上是否有学生
     */
    boolean is_have_student = false;
    /**
     * 判断当前是否是纯音频教室
     */
    boolean isAudioTeaching = false;

    /**
     * 检测全体静音
     */
    private void checkMute() {
        isMute = true;
        is_have_student = false;
        for (RoomUser u : TKRoomManager.getInstance().getUsers().values()) {
            if ((u.publishState == 1 || u.publishState == 3) && u.role == 2) {
                isMute = false;
            }
            if (u.role == 2 && u.publishState > 0) {
                is_have_student = true;
            }
        }
        if (is_have_student) {
            if (isMute) {
                allActionUtils.setAllMute();
            } else {
                allActionUtils.setAllTalk();
            }
        } else {
            allActionUtils.setNoStudent();
        }
    }

    @Override
    public void all_send_gift() {
        HashMap<String, RoomUser> receiverMap = new HashMap<String, RoomUser>();
        for (RoomUser u : TKRoomManager.getInstance().getUsers().values()) {
            if (u.role == 2) {
                receiverMap.put(u.peerId, u);
            }
        }
        if (receiverMap.size() != 0) {
            if (RoomInfo.getInstance().getTrophyList().size() > 0) {
                //自定义奖杯
                sendGiftPopUtils.showSendGiftPop(mRootHolder.rel_wb.getWidth() / 10 * 5,
                        mRootHolder.rel_wb.getHeight() / 10 * 8, mRootHolder.rel_wb, receiverMap,
                        false, 0);
            } else {
                //默认奖杯
                RoomOperation.getInstance().sendGift(receiverMap, null, this);
            }
        }
    }

    /***
     *  全体恢复
     */
    @Override
    public void all_recovery() {
        recoveryAllVideoTtems();
    }

    @Override
    public void all_control_window_close() {
        mRootHolder.cb_control.setChecked(false);
        mRootHolder.cb_control.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootHolder.cb_control.setEnabled(true);
            }
        }, 100);
    }

    @Override
    public void close_member_list_window() {
        mRootHolder.cb_member_list.setChecked(false);
        mRootHolder.cb_member_list.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRootHolder.cb_member_list.setEnabled(true);
            }
        }, 100);

        if (RoomOperation.numberTimer != null) {
            RoomOperation.numberTimer.cancel();
            RoomOperation.numberTimer = null;
        }
    }

    //信息收到的回调
    @Override
    public void onResult(final int index, final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (RoomSession.chatList.size() > index) {
                    RoomSession.chatList.get(index).setTrans(true);
                    RoomSession.chatList.get(index).setTrans(result);
                    View view = mRootHolder.lv_chat_list.getChildAt(index -
                            mRootHolder.lv_chat_list.getFirstVisiblePosition());
                    HttpTextView txt_ch_msg = (HttpTextView) view.findViewById(R.id.txt_ch_msg);
                    ImageView img_translation = (ImageView) view.findViewById(R.id.img_translation);
                    TextView txt_eng_msg = (TextView) view.findViewById(R.id.txt_eng_msg);
                    View viewDivider = (View) view.findViewById(R.id.view);
                    ChatData ch = RoomSession.chatList.get(index);
                    chlistAdapter.setTranslation(ch, index, txt_ch_msg, txt_eng_msg, img_translation, viewDivider);
                }
            }
        });
    }

    /**
     * 上传图片，选择相机
     */
    @Override
    public void cameraClickListener() {
        cameraClickListener(TKBaseActivity.KEJIAN_SELECT_IMAGE);
    }

    /**
     * 上传图片，选择相册
     */
    @Override
    public void photoClickListener() {
        photoClickListener(TKBaseActivity.KEJIAN_SELECT_IMAGE);
    }

    /**
     * 上传图片，选择相机
     */
    public void cameraClickListener(int type) {
        mSelectImageType = type;
        if (!isPauseLocalVideo) {
            TKRoomManager.getInstance().pauseLocalCamera();
            isPauseLocalVideo = !isPauseLocalVideo;
        }
        isOpenCamera = true;
        isBackApp = true;
        PhotoUtils.openCamera(OneToManyActivity.this);
    }

    /**
     * 上传图片，选择相册
     */
    @Override
    public void photoClickListener(int type) {
        mSelectImageType = type;
        isBackApp = true;
        PhotoUtils.openAlbum(OneToManyActivity.this);
    }

    //fragment显示回调
    @Override
    public void setUserVisibleHint() {
        ScreenScale.scaleView(view, "OneToManyActivity  ----    onCreate");
        //创建侧边工具条及 底部翻页按钮
        crateToolsPage(view);
    }

    @Override
    public void toSwitch(int layoutState) {
        if (mLayoutState == layoutState) {
            return;
        }
        mLayoutState = layoutState;
        VideoTtemTouchEvent.mLayoutState = layoutState;
        switch (mLayoutState) {
            case LayoutPopupWindow.LAYOUT_NORMAL:   // 视频置顶
                OnetoManyLayoutUtil.ShowView(mRootHolder);
                resetTeatherPostion();
                recoveryAllVideoTtems();
                break;
            case LayoutPopupWindow.LAYOUT_DOUBLE:  // 主讲视频
                OnetoManyLayoutUtil.hideView(mRootHolder, mScreenValueWidth, hid - toolBarHeight - 4);
                soleOnlyId = "only";
                recoveryAllVideoTtems();
                break;
            case LayoutPopupWindow.LAYOUT_VIDEO:     // 自由视频布局
                resetTeatherPostion();
                OnetoManyLayoutUtil.hideView(mRootHolder, mScreenValueWidth, hid - toolBarHeight - 4);
                recoveryAllVideoTtems();
                break;
        }
    }

    /**
     * 置頂時，让老师视频显示在第一个位置
     */
    private void resetTeatherPostion() {
        if (videoItems.size() > 1) {
            for (int x = 0; x < videoItems.size(); x++) {
                if (videoItems.get(x).role == 0 && x != 0) {
                    Collections.swap(videoItems, 0, x);
                }
            }
        }
    }

    class AddTime extends TimerTask {
        @Override
        public void run() {
            RoomOperation.serviceTime += 1;
            RoomOperation.localTime = RoomOperation.serviceTime - RoomOperation.classStartTime;
            if (RoomSession.isClassBegin) {
                showTime();
            }
        }
    }

    private void showTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //举手倒计时
                if (showHandTxtTime >= 0) {
                    showHandTxtTime++;
                    if (showHandTxtTime >= 4) {
                        controlHandViewVisiable(false);
                        showHandTxtTime = -1;
                    }
                }
                String H = "";
                String M = "";
                String S = "";
                long temps = RoomOperation.localTime;
                long tempm = temps / 60;
                long temph = tempm / 60;
                long sec = temps - tempm * 60;
                tempm = tempm - temph * 60;
                H = temph == 0 ? "00" : temph >= 10 ? temph + "" : "0" + temph;
                M = tempm == 0 ? "00" : tempm >= 10 ? tempm + "" : "0" + tempm;
                S = sec == 0 ? "00" : sec >= 10 ? sec + "" : "0" + sec;
                if (mRootHolder != null) {
                    mRootHolder.txt_hour.setText(H);
                    mRootHolder.txt_min.setText(M);
                    mRootHolder.txt_ss.setText(S);
                }

                try {
                    if (TKRoomManager.getInstance().getRoomProperties() != null && TKRoomManager.getInstance().getRoomProperties().optLong("endtime") - RoomOperation.serviceTime == 60 * 5) {
                        if (TKRoomManager.getInstance().getMySelf().role == 0 && RoomControler.haveTimeQuitClassroomAfterClass()) {
                            Toast.makeText(OneToManyActivity.this, getString(R.string.end_class_time), Toast.LENGTH_LONG).show();
                        }
                    }

                    if (TKRoomManager.getInstance().getRoomProperties() != null && RoomControler.haveTimeQuitClassroomAfterClass() && RoomOperation.serviceTime >= TKRoomManager.getInstance().getRoomProperties().optLong("endtime") && TKRoomManager.getInstance().getMySelf().role != -1) {
                        if (RoomOperation.timerAddTime != null) {
                            RoomOperation.timerAddTime.cancel();
                            RoomOperation.timerAddTime = null;
                        }
                        if (RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == 0) {
                            try {
                                TKRoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new JSONObject().put("recordchat", true).toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            RoomOperation.getInstance().sendClassDissToPhp();
                        }
                        RoomClient.getInstance().onClassDismiss();
                        TKRoomManager.getInstance().leaveRoom();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (RoomControler.haveTimeQuitClassroomAfterClass() && !RoomSession.isClassBegin) {
            if (RoomOperation.timerAfterLeaved != null) {
                RoomOperation.timerAfterLeaved.cancel();
                RoomOperation.timerAfterLeaved = null;
            }
            RoomOperation.getInstance().getSystemNowTime(this);
        }
    }

    /**
     * 设置未读消息数
     *
     * @param num
     */
    private void setNoReadChatMessage(int num) {
        if (mRootHolder.tv_no_read_message_number != null) {
            if (!isZoom) {
                mRootHolder.tv_no_read_message_number.setVisibility(View.VISIBLE);
            }
            if (num > 99) {
                mRootHolder.tv_no_read_message_number.setText("99+");
            } else {
                mRootHolder.tv_no_read_message_number.setText(num + "");
            }
        }
    }

    /**
     * 清空未读消息数
     */
    private void clearNoReadChatMessage() {
        RoomSession.chatDataCache.clear();
        if (mRootHolder.tv_no_read_message_number != null) {
            mRootHolder.tv_no_read_message_number.setVisibility(View.INVISIBLE);
            mRootHolder.tv_no_read_message_number.setText("");
        }
    }

    public void PreCreateVideoItem(boolean isFlage) {
        int num = 6;
        if (isFlage) {
            num = 7;
        } else {
            num = 6;
        }
        for (int i = 0; i < num; i++) {
            VideoItemToMany videoItemToMany = new VideoItemToMany(this);
            videoItemToManies.add(videoItemToMany);
        }
    }

    /***
     *   用户音频状态改变
     * @param userIdAudio     用户 id
     * @param statusAudio      音频状态 0 取消发布 1 发布
     */
    @Override
    public void onUserAudioStatus(String userIdAudio, int statusAudio) {
        //预创建视频框
        preCreateVideoItem();

        RoomUser roomUser = TKRoomManager.getInstance().getUser(userIdAudio);
        if (roomUser == null) {
            return;
        }

        if (statusAudio > 0) {
            doPlayAudio(userIdAudio);
        } else {
            doUnPlayAudio(userIdAudio);
            if (!RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == 0 && TKRoomManager.getInstance().getUser(userIdAudio).role == 0 && TKRoomManager.getInstance().getMySelf().peerId.equals(userIdAudio)) {
                playSelfBeforeClassBegin();
            }
        }
        changeUserState(TKRoomManager.getInstance().getUser(userIdAudio));
        memberListAdapter.notifyDataSetChanged();
        if (studentPopupWindow != null) {
            studentPopupWindow.dismiss();
        }
    }

    private void doUnPlayAudio(String userIdAudio) {

        RoomUser roomUser = TKRoomManager.getInstance().getUser(userIdAudio);
        if (roomUser == null || userIdAudio == null || userIdAudio.isEmpty()) {
            return;
        }

        TKRoomManager.getInstance().unPlayAudio(userIdAudio);

        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(userIdAudio)) {
                if (roomUser.getPublishState() == 0) {
                    resetVideoitemMoved(videoItems.get(i));
                    resetVideoitem(videoItems.get(i));
                    mRootHolder.rel_students.removeView(videoItems.get(i).parent);
                    videoItems.remove(i);
                    do1vsnStudentVideoLayout();
                } else {
                    videoItems.get(i).img_mic.setImageResource(R.drawable.tk_icon_audio_disable);
                    videoItems.get(i).volume.setVisibility(View.GONE);
                }
            }
        }
    }

    /***
     *  下台重置视频的移动和视频双击状态
     * @param videoItemToMany
     */
    private void resetVideoitemMoved(VideoItemToMany videoItemToMany) {
        if (videoItemToMany.isMoved) {
            SendingSignalling.getInstance().sendStudentNoMove(videoItemToMany.peerid);
            videoItemToMany.isMoved = false;
        }
        if (videoItemToMany.isSplitScreen) {
            SendingSignalling.getInstance().sendDoubleClickVideoRecovery(videoItemToMany.peerid);
            videoItemToMany.isSplitScreen = false;
        }
    }

    private void doPlayAudio(String audioUserId) {
        if (TextUtils.isEmpty(audioUserId)) {
            return;
        }

        RoomUser user = TKRoomManager.getInstance().getUser(audioUserId);
        if (user == null) {
            return;
        }
        do1vsnStudentPlayVideo(user);

        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(audioUserId)) {
                if (videoItems.get(i).isMoved || videoItems.get(i).isSplitScreen || videoItems.get(i).isOnlyShowTeachersAndVideos) {
                    TKRoomManager.getInstance().playAudio(audioUserId);
                } else {
                    TKRoomManager.getInstance().unPlayAudio(audioUserId);
                }
            }
        }
    }

    /***
     *    视频数据首帧回调
     * @param peerIdVideo      peerID
     */
    @Override
    public void onFirstVideoFrame(String peerIdVideo) {
        if (videofragment != null) {
            videofragment.hideLaoding(peerIdVideo);
        }
    }

    /***
     *    网络连接丢失
     */
    @Override
    public void onConnectionLost() {

        mediaListAdapter.setLocalfileid(-1);
        scalemap.clear();
        notMoveVideoItems.clear();
        movedVideoItems.clear();
        for (int x = 0; x < videoItems.size(); x++) {
            mRootHolder.rel_students.removeView(videoItems.get(x).parent);
            resetVideoitem(videoItems.get(x));
        }
        videoItems.clear();
        videoItemToManies.clear();

        mRootHolder.re_loading.setVisibility(View.VISIBLE);
        mRootHolder.tv_load.setText(getString(R.string.connected));

        UploadPhotoPopupWindowUtils.getInstance().setDismiss();

        removeVideoFragment();
        removeScreenFragment();
        removeMovieFragment();
        changeVideoState();
        mRootHolder.video_container.setVisibility(View.GONE);

        if (!RoomSession.isPublishMp3) {
            if (mRootHolder.img_disk != null) {
                mRootHolder.img_disk.clearAnimation();

                if (gifDrawable != null) {
                    gifDrawable.stop();
                }
            }
            mRootHolder.lin_audio_seek.setVisibility(View.INVISIBLE);
            mRootHolder.fl_play_disk.setVisibility(View.INVISIBLE);
        }

        ToolCaseMgr.getInstance().cleanData(true);

        if (mPagesView != null) {
            mPagesView.resetLargeOrSmallView();
        }
        //重置mlayout 及 popuplayout
        mLayoutState = 1;
        LayoutPopupWindow.getInstance().reset();
        if (mPlayBackSeekPopupWindow != null) {
            mPlayBackSeekPopupWindow.connectLost();
        }
    }

    /***
     *   获取大并发教室中人数
     * @param roomUserNumberCode    获取是否成功 0-成功
     * @param roomUserNumber   人数
     */
    @Override
    public void onRoomUserNumber(int roomUserNumberCode, int roomUserNumber) {
        if (roomUserNumberCode == 0) {
            if (memberListPopupWindowUtils != null && memberListPopupWindowUtils.isShowing()) {
                memberListPopupWindowUtils.setTiteNumber(roomUserNumber);
            }
        }
    }

    /***
     *  获取大并发用户列表回调
     * @param roomUserCode         获取是否成功 0-成功
     * @param userList    用户列表（成功）
     */
    @Override
    public void onRoomUser(int roomUserCode, ArrayList<RoomUser> userList) {
        if (roomUserCode == 0 && memberListAdapter != null) {
            memberListAdapter.setUserList(userList);
            memberListAdapter.notifyDataSetChanged();
        }
    }

    /***
     *  音量回调
     * @param volumePeerId   用户 id
     * @param volume   音量
     */
    @Override
    public void onAudioVolume(String volumePeerId, int volume) {
        RoomUser roomUser = TKRoomManager.getInstance().getUser(volumePeerId);
        if (roomUser != null) {
            for (int x = 0; x < videoItems.size(); x++) {
                if (videoItems.get(x).peerid.equals(volumePeerId)) {
                    if (volume >= 5) {
                        videoItems.get(x).img_mic.setVisibility(View.VISIBLE);
                        videoItems.get(x).volume.setVisibility(View.VISIBLE);
                        if (roomUser != null && roomUser.getPublishState() == 1 || roomUser.getPublishState() == 3) {
                            videoItems.get(x).img_mic.setImageResource(R.drawable.tk_icon_sound);
                            if (volume <= 5) {
                                videoItems.get(x).volume.setIndex(0);
                            } else if (volume > 5 && volume < 5000) {
                                videoItems.get(x).volume.setIndex(1);
                            } else if (volume > 5000 && volume < 10000) {
                                videoItems.get(x).volume.setIndex(2);
                            } else if (volume > 10000 && volume < 20000) {
                                videoItems.get(x).volume.setIndex(3);
                            } else if (volume > 20000 && volume < 30000) {
                                videoItems.get(x).volume.setIndex(4);
                            }
                        } else {
                            videoItems.get(x).img_mic.setImageResource(R.drawable.tk_icon_close_voice);
                            videoItems.get(x).volume.setVisibility(View.GONE);
                        }
                    } else {
                        if (roomUser != null && roomUser.getPublishState() == 1 || roomUser.getPublishState() == 3) {
                            videoItems.get(x).img_mic.setVisibility(View.VISIBLE);
                            videoItems.get(x).volume.setVisibility(View.VISIBLE);
                            videoItems.get(x).volume.setIndex(0);
                        } else {
                            videoItems.get(x).img_mic.setImageResource(R.drawable.tk_icon_close_voice);
                            videoItems.get(x).img_mic.setVisibility(View.VISIBLE);
                            videoItems.get(x).volume.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }

    /***
     *     警告回调
     * @param onWarning   1751 摄像头打开   1752 摄像头关闭
     */
    @Override
    public void onWarning(int onWarning) {
        if (10001 == onWarning) {
            if (isOpenCamera) {
                PhotoUtils.openCamera(OneToManyActivity.this);
            }
        }
    }

    /***
     *    错误信息回调
     * @param errorCode    错误码
     * @param errMsg        错误信息
     */
    @Override
    public void onError(int errorCode, String errMsg) {
        if (errorCode == 10004) {  //UDP连接不同
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.showAlertDialog(OneToManyActivity.this, getString(R.string.udp_alert));
                }
            });
        } else if (10002 == errorCode) {

            mRootHolder.re_loading.setVisibility(View.GONE);

            removeVideoFragment();
            removeScreenFragment();
            removeMovieFragment();
            mRootHolder.video_container.setVisibility(View.GONE);

            mediaListAdapter.setLocalfileid(-1);
            scalemap.clear();
            for (int x = 0; x < videoItems.size(); x++) {
                mRootHolder.rel_students.removeView(videoItems.get(x).parent);
            }
            videoItems.clear();
            clear();
            finish();
        } else if (errorCode == 10005) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.showAlertDialog(OneToManyActivity.this, getString(R.string.fire_wall_alert));
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "udpstate", 2);
                }
            });
        }
    }

    /***
     *  进入房间成功回调
     */
    @Override
    public void onRoomJoin() {

        if (videoItemToManies != null && videoItemToManies.size() < 6) {
            for (int i = 0; i < 12; i++) {
                VideoItemToMany videoItemToMany = new VideoItemToMany(this);
                videoItemToManies.add(videoItemToMany);
            }
        }

        for (RoomUser roomUser : TKRoomManager.getInstance().getUsers().values()) {
            if (roomUser != null && roomUser.publishState == 4) {
                if (roomUser.role == 0 || (roomUser.role == 2 && RoomControler.isReleasedBeforeClass())) {
                    onUserVideoStatus(roomUser.peerId, 0);
                    onUserAudioStatus(roomUser.peerId, 0);
                    do1vsnStudentPlayVideo(roomUser);
                }
            }
        }
        TKRoomManager.getInstance().setInBackGround(false);
        //关闭loading
        mRootHolder.re_loading.setVisibility(View.GONE);

        //如果是回放，隐藏各类控件
        if (TKRoomManager.getInstance().getMySelf().role == -1) {
            //回放进度条的显示隐藏工具类,初始化
            if (mRootHolder.vs_play_back != null) {
                if (!isInflated) {
                    inflate = mRootHolder.vs_play_back.inflate();
                }

                mRootHolder.re_play_back = inflate.findViewById(R.id.re_play_back);
                //******   回放titleBar
                mRootHolder.rel_play_back_bar = inflate.findViewById(R.id.re_back_bar);
                if (tool_bar_param != null) {
                    mRootHolder.rel_play_back_bar.setLayoutParams(tool_bar_param);
                }
                //返回
                mRootHolder.img_play_back_out = inflate.findViewById(R.id.img_play_back_out);
                //回放媒体名字
                mRootHolder.tv_back_name = inflate.findViewById(R.id.tv_back_name);
            }
            mRootHolder.img_play_back_out.setOnClickListener(this);
            mRootHolder.tv_back_name.setText(RoomInfo.getInstance().getRoomName());

            if (playbackControlUtils == null) {
                playbackControlUtils = new PlaybackControlUtils(this, new PlaybackControlUtils.DismissPopupWindowListener() {
                    @Override
                    public void dismissPopupWindow() {
                        if (mPlayBackSeekPopupWindow != null) {
                            mPlayBackSeekPopupWindow.dismiss();
                        }
                    }
                });
            }
            if (mPlayBackSeekPopupWindow == null) {
                mPlayBackSeekPopupWindow = new PlayBackSeekPopupWindow(OneToManyActivity.this, mRootHolder.re_play_back);
            }
            mPlayBackSeekPopupWindow.startTimer(playbackControlUtils);
            ToolCaseMgr.getInstance().setPlayBackSeekPopupWindow(mPlayBackSeekPopupWindow);

            mRootHolder.re_play_back.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        if (Tools.isInView(event, mRootHolder.cb_message)) {
                            if (mRootHolder.cb_message.isChecked()) {
                                mRootHolder.cb_message.setChecked(false);
                            } else {
                                mRootHolder.cb_message.setChecked(true);
                            }
                        } else {
                            if (!playbackControlUtils.isShowing) {
                                playbackControlUtils.startHideTimer(mPlayBackSeekPopupWindow.rel_play_back);
                                mPlayBackSeekPopupWindow.showPopupWindow();
                            }
                        }
                    }
                    return true;
                }
            });
        }

        //举手逻辑
        RoomOperation.getInstance().handAction(mRootHolder.txt_hand_up);
        //检测摄像头
        RoomCheck.getInstance().checkCamera(this);
        //检测麦克风
        RoomCheck.getInstance().checkMicrophone(this);
        setCheckBoxEnabled();
        //弹窗聊天框
        showChatPopupWindow();
        wifiStatusPop.setRoomId(RoomInfo.getInstance().getSerial());

        int roomLayoutState = RoomInfo.getInstance().getRoomlayout();
        if (roomLayoutState == 2) {
            // 主讲
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_DOUBLE, "MainPeople");
        } else if (roomLayoutState == 3) {
            // 自由
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_VIDEO, "OnlyVideo");
        } else {
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_NORMAL, "CoursewareDown");
        }

        initViewByRoomTypeAndTeacher();
    }

    private void setCheckBoxEnabled() {
        mRootHolder.cb_choose_photo.setEnabled(true);
        mRootHolder.cb_member_list.setEnabled(true);
        mRootHolder.cb_file_person_media_list.setEnabled(true);
        mRootHolder.cb_tool_case.setEnabled(true);
        mRootHolder.cb_control.setEnabled(true);
        mRootHolder.cb_message.setEnabled(true);
        mRootHolder.cb_choose_shut_chat.setEnabled(true);
        mRootHolder.cb_tool_layout.setEnabled(true);
    }

    /***
     *    离开房间回调
     */
    @Override
    public void onRoomLeave() {

        removeVideoFragment();
        removeScreenFragment();
        removeMovieFragment();

        mediaListAdapter.setLocalfileid(-1);
        scalemap.clear();
        videoItemToManies.clear();

        if (gifDrawable != null) {
            gifDrawable.stop();
        }

        if (mRootHolder.img_disk != null) {
            mRootHolder.img_disk.clearAnimation();
        }

        TKRoomManager.getInstance().destroy();
        clear();
        finish();
    }

    /***
     *    其他用户进入房间的回调
     * @param roomUser          进入的用户
     * @param inList            是否在我之前进入房间，true—之前，false—之后
     */
    @Override
    public void onUserJoined(RoomUser roomUser, boolean inList) {
        changeUserState(roomUser);
        chlistAdapter.notifyDataSetChanged();
        memberListAdapter.notifyDataSetChanged();
        memberListPopupWindowUtils.setTiteNumber(RoomSession.memberList.size());
    }

    /***
     *     其他用户离开房间
     * @param roomUser      离开的用户
     */
    @Override
    public void onUserLeft(RoomUser roomUser) {
        stuMoveInfoMap.remove(roomUser.peerId);
        chlistAdapter.notifyDataSetChanged();
        memberListAdapter.notifyDataSetChanged();
        memberListPopupWindowUtils.setTiteNumber(RoomSession.memberList.size());

        //当老师离开教室隐藏画中画
        if (TKRoomManager.getInstance().getMySelf().role != 0 && isZoom && roomUser.role == 0 &&
                RoomControler.isFullScreenVideo() && RoomSession.isClassBegin) {
            FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                    null, false);
        }

        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(roomUser.peerId)) {
                //重置video初始化
                resetVideoitem(videoItems.get(i));
                mRootHolder.rel_students.removeView(videoItems.get(i).parent);
                videoItems.remove(i);
            }
        }
        do1vsnStudentVideoLayout();
        checkMute();
    }

    /***
     *   用户属性改变
     * @param roomUser    改变属性的用户
     * @param map          改变的属性集合
     * @param fromId      改变该用户属性的用户的用户 ID
     */
    @Override
    public void onUserPropertyChanged(RoomUser roomUser, Map<String, Object> map, String fromId) {
        //预创建视频框
        preCreateVideoItem();

        if (map.containsKey("publishstate")) {
            if (roomUser.getPublishState() == 0) {
                if (screenID.contains(roomUser.peerId)) {
                    SendingSignalling.getInstance().sendDoubleClickVideoRecovery(roomUser.peerId);
                }
                if (stuMoveInfoMap.containsKey(roomUser.peerId)) {
                    SendingSignalling.getInstance().sendStudentNoMove(roomUser.peerId);
                }
                if (roomUser.role == 0 && roomUser.peerId.equals(solepopwindowPid)) {
                    if (teaPopupWindow != null) {
                        teaPopupWindow.dismiss();
                    }
                }
                if (roomUser.role == 2 && roomUser.peerId.equals(solepopwindowPid)) {
                    if (studentPopupWindow != null) {
                        studentPopupWindow.dismiss();
                    }
                }
                doUnPlayAudio(roomUser.peerId);
            } else if (roomUser.getPublishState() == 1) {
                doPlayAudio(roomUser.peerId);
            } else if (roomUser.getPublishState() == 4) {
                doPlayVideo(roomUser.peerId);
            }
        }

        //插播摄像头切换画中画状态
        if (RoomSession.isClassBegin && RoomControler.isFullScreenVideo() && RoomSession.fullScreen && map.containsKey("hasvideo")) {
            boolean hasvideo = (boolean) map.get("hasvideo");
            for (int i = 0; i < RoomSession.playingList.size(); i++) {
                if (RoomSession.playingList.get(i).role == 0) {
                    if (videofragment != null) {
                        videofragment.setFullscreenShow(RoomSession.playingList.get(i).peerId, hasvideo);
                    } else {
                        if (movieFragment != null) {
                            movieFragment.setFullscreenShow(RoomSession.playingList.get(i).peerId, hasvideo);
                        } else {
                            FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem, RoomSession.playingList.get(i), hasvideo);
                        }
                    }
                    break;
                }
            }
        }

        if (map.containsKey("disablechat")) {
            if (roomUser == null) {
                return;
            }
            boolean disablechat = Tools.isTure(map.get("disablechat"));
            if (TKRoomManager.getInstance().getMySelf().peerId.equals(roomUser.peerId)) {
                if (disablechat) {
                    mRootHolder.iv_open_input.setImageResource(R.drawable.tk_shuru_default);
                } else {
                    mRootHolder.iv_open_input.setImageResource(R.drawable.tk_shuru);
                }
            }
        }


        if (TKRoomManager.getInstance().getMySelf().properties.containsKey("raisehand")) {
            boolean israisehand = Tools.isTure(TKRoomManager.getInstance().getMySelf().properties.get("raisehand"));
            RoomUser selfUser = TKRoomManager.getInstance().getMySelf();
            if (israisehand) {
                if (TKRoomManager.getInstance().getMySelf().role == 0) {//如果是老师才显示
                    mRootHolder.iv_hand.setVisibility(View.VISIBLE);
                    controlHandViewVisiable(true);
                }
                if (selfUser != null && selfUser.publishState == 0) {
                    mRootHolder.txt_hand_up.setText(R.string.no_raise);
                } else {
                    mRootHolder.txt_hand_up.setText(R.string.raiseing);
                    mRootHolder.txt_hand_up.setBackgroundResource(R.drawable.tk_commom_btn_handup);
                    mRootHolder.txt_hand_up.setTextAppearance(OneToManyActivity.this, R.style.three_color_hands_up);
                }
            } else {
                mRootHolder.iv_hand.setVisibility(View.INVISIBLE);
                controlHandViewVisiable(false);
                mRootHolder.txt_hand_up.setText(R.string.raise); //同意了，或者拒绝了
                mRootHolder.txt_hand_up.setBackgroundResource(R.drawable.tk_commom_btn_xiake);
                mRootHolder.txt_hand_up.setTextAppearance(OneToManyActivity.this, R.style.three_color_hands_up);
            }
        } else {
            if (map.containsKey("raisehand")) {
                boolean israisehand = Tools.isTure(map.get("raisehand"));
                if (israisehand) {
                    if (TKRoomManager.getInstance().getMySelf().role == 0) {//如果是老师才显示
                        mRootHolder.iv_hand.setVisibility(View.VISIBLE);
                        controlHandViewVisiable(true);
                    }
                } else {
                    mRootHolder.iv_hand.setVisibility(View.INVISIBLE);
                    controlHandViewVisiable(false);
                }
            }
            mRootHolder.txt_hand_up.setText(R.string.raise); //还没举手
            mRootHolder.txt_hand_up.setBackgroundResource(R.drawable.tk_commom_btn_xiake);
            mRootHolder.txt_hand_up.setTextAppearance(OneToManyActivity.this, R.style.three_color_hands_up);
        }

        if (map.containsKey("giftnumber") && !roomUser.peerId.equals(fromId)) {
            for (int i = 0; i < videoItems.size(); i++) {
                if (roomUser.peerId.equals(videoItems.get(i).peerid)) {
                    if (videoItems.get(i).isOnlyShowTeachersAndVideos || videoItems.get(i).isSplitScreen ||
                            videoItems.get(i).isMoved) {
                        ShowTrophyUtil.showManyTrophyIntention(videoItems.get(i).sf_video, map,
                                this, wid, hid, mRootHolder.rel_parent);
                    }
                    break;
                }
            }
        }

        if (roomUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("candraw")) {
            boolean candraw = Tools.isTure(map.get("candraw"));
            if (candraw) {
                if (TKRoomManager.getInstance().getMySelf().role != 4 && mLayoutState == LayoutPopupWindow.LAYOUT_NORMAL) {
                    mRootHolder.cb_choose_photo.setVisibility(View.VISIBLE);
                }
                if (roomUser.role == 2) {
                    //获取画笔权限是随机画笔的颜色
                    SetRoomInfor.getInstance().setUserPenColor(roomUser);
                }
                toolsView.showTools(true);
            } else {
                mRootHolder.cb_choose_photo.setVisibility(View.GONE);
                UploadPhotoPopupWindowUtils.getInstance().setDismiss();
                toolsView.showTools(false);
            }
        }

        if (roomUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("volume")) {
            Number n_volume = (Number) map.get("volume");
            int int_volume = n_volume.intValue();
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, int_volume, 0);
        }

        changeUserState(roomUser);
        checkMute();
        memberListAdapter.notifyDataSetChanged();
    }

    /**
     * 恢复预创建videoitem数据
     *
     * @param videoItemToMany
     */
    public void resetVideoitem(final VideoItemToMany videoItemToMany) {
        //videoItems.get(i).sf_video.release();
        // videoItems.get(i).sf_video = null;
        videoItemToMany.isShow = false;
        videoItemToMany.peerid = "";
        videoItemToMany.role = -1;
        videoItemToMany.isMoved = false;
        videoItemToMany.isSplitScreen = false;
        videoItemToMany.sf_video.clearImage();
        //奖杯显示
        videoItemToMany.lin_gift.setVisibility(View.VISIBLE);
        videoItemToMany.img_hand.setVisibility(View.VISIBLE);
        videoItemToMany.img_pen.setVisibility(View.VISIBLE);
        videoItemToMany.bg_img_pen.setVisibility(View.VISIBLE);
        videoItemToMany.lin_name_label.setVisibility(View.VISIBLE);
        videoItemToMany.img_mic.setVisibility(View.VISIBLE);
        videoItemToMany.volume.setVisibility(View.VISIBLE);
        videoItemToMany.img_mic.setImageResource(R.drawable.tk_icon_sound);
        videoItemToMany.view_choose_selected.setVisibility(View.GONE);
        videoItemToMany.re_background.setVisibility(View.GONE);
    }

    /***
     *  用户视频状态改变
     * @param peerId   用户 id
     * @param state    视频状态 0 取消发布 1 发布
     */
    @Override
    public void onUserVideoStatus(String peerId, int state) {
        //预创建视频框
        preCreateVideoItem();
        RoomUser roomUser = TKRoomManager.getInstance().getUser(peerId);
        if (roomUser == null) {
            return;
        }
        if (state > 0) {
            doPlayVideo(peerId);
        } else {
            do1vsnStudentUnPlayVideo(peerId);
        }
        changeUserState(TKRoomManager.getInstance().getUser(peerId));
        memberListAdapter.notifyDataSetChanged();
        if (studentPopupWindow != null) {
            studentPopupWindow.dismiss();
        }
    }

    /***
     *  预创建视频框
     */
    private void preCreateVideoItem() {
        if (videoItemToManies != null && videoItemToManies.size() > 0) {
            if (RoomSession.videoList.size() > 4 && videoItemToManies.size() <= 6) {
                PreCreateVideoItem(false);
            } else if (RoomSession.videoList.size() > 10 && videoItemToManies.size() <= 12) {
                PreCreateVideoItem(false);
            } else if (RoomSession.videoList.size() > 16 && videoItemToManies.size() <= 18) {
                PreCreateVideoItem(true);
            }
        }
    }

    /***
     *    收到文本消息
     * @param roomUser     发送文本消息的用户
     */
    @Override
    public void onMessageReceived(RoomUser roomUser) {
        if (!roomUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && !mRootHolder.cb_message.isChecked()) {
            setNoReadChatMessage(RoomSession.chatDataCache.size());
        } else if (mRootHolder.cb_message.isChecked()) {
            RoomSession.chatDataCache.clear();
        }
        chlistAdapter.notifyDataSetChanged();
    }

    /***
     *  收到信令消息回调
     * @param name        消息名字
     * @param pubMsgTS          消息发送时间戳
     * @param data        消息携带数据
     * @param inList      消息是否在消息列表中
     */
    @Override
    public void onRemotePubMsg(String name, long pubMsgTS, Object data, boolean inList) {
        String strdata = null;
        if (data != null) {
            if (data instanceof String) {
                strdata = (String) data;
            } else if (data instanceof Map) {
                strdata = new JSONObject((Map) data).toString();
            }
        }

        switch (name) {
            case "ClassBegin":  // 上课
                acceptSignalingClassBegin(inList);
                break;
            case "UpdateTime":   // 时间
                acceptSignalingUpdateTime();
                break;
            case "BigRoom":    // 大并发
                acceptSignalingBigRoom();
                break;
            case "StreamFailure":   // 流失败
                acceptSignalingStreamFailure(strdata);
                break;
            case "videoDraghandle":  // 拖拽
                acceptSignalingVideoDraghandle(strdata, inList);
                break;
            /*case "VideoSplitScreen":   // 分屏
                acceptSignalingVideoSplitScreen(strdata);
                break;*/
            case "VideoChangeSize":  // 视频框大小改变
                acceptSignalingVideoChangeSize(strdata);
                break;
            case "EveryoneBanChat":   // 全体禁言
                acceptSignalingEveryoneBanChat(pubMsgTS, inList);
                break;
            case "ShowPage":
                mPagesView.onHidePageNumberPop();
                break;
            case "FullScreen":    // 课件全屏同步
                acceptSignalingFullScreen(pubMsgTS, strdata, inList);
                break;
           /* case "ChatShow":   // 打开聊天框
                acceptSignalingChatShow();
                break;*/
            case "OnlyAudioRoom":   // 纯音频
                acceptSignalingOnlyAudioRoom(inList);
                break;
            case "doubleClickVideo":   // 视频双击
                acceptSignalingDoubleClickVideo(strdata);
                break;

            case "switchLayout":   // 切换布局
                mPagesView.onHidePageNumberPop();
                acceptSwitchLayout(strdata, inList);
                break;

            case "MainPeopleExchangeVideo": //主讲模式下 切换主讲位置信令
                accepMainPeopleExchangeVideo(strdata);
                break;
        }
    }

    /**
     * 主讲模式下 切换主讲位置信令
     *
     * @param
     */
    private void accepMainPeopleExchangeVideo(Object data) {
        Map<String, Object> mapdata = null;
        if (data instanceof String) {
            String str = (String) data;
            try {
                JSONObject js = new JSONObject(str);
                mapdata = Tools.toMap(js);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mapdata = (Map<String, Object>) data;
        }
        if (mapdata.containsKey("doubleId")) {
            String pId = (String) mapdata.get("doubleId");
            soleOnlyId = pId;
            transitionVideoItems();
            do1vsnStudentVideoLayout();
        }
        if (studentPopupWindow != null) {
            if (studentPopupWindow.isShowing()) {
                studentPopupWindow.dismiss();
            }
        }
    }

    public void transitionVideoItems() {
        if (soleOnlyId.equals("only")) {
            return;
        }
        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(soleOnlyId)) {
                Collections.swap(videoItems, 0, i);
                SortVideoItems(videoItems);
                break;
            }
        }
    }

    /**
     * 接收视频布局切换
     */
    private void acceptSwitchLayout(Object data, boolean inList) {
        if (RoomControler.isOnlyShowTeachersAndVideos() && TKRoomManager.getInstance().getMySelf().role == 2) {
            return;
        }

        //白板全屏时先缩回白板
        if (isZoom) {
            onWhiteBoradZoom(false);
        }

        Map<String, Object> mapdata = null;
        if (data instanceof String) {
            String str = (String) data;
            try {
                JSONObject js = new JSONObject(str);
                mapdata = Tools.toMap(js);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mapdata = (Map<String, Object>) data;
        }
        /**
         'CoursewareDown', 视频置顶
         'VideoDown', 视频置底
         'Encompassment' 视频围绕
         'Bilateral'主讲排列
         'MorePeople' 主讲视频
         'OnlyVideo' 自由视频
         'oneToOne' 常规布局(一对一)
         'oneToOneDoubleDivision' 双师布局(一对一)
         'oneToOneDoubleVideo' 视频布局(一对一)
         */
        String nowLayout = (String) mapdata.get("nowLayout");
        if ("CoursewareDown".equals(nowLayout) || "VideoDown".equals(nowLayout) ||
                "Encompassment".equals(nowLayout) || "Bilateral".equals(nowLayout)) {
            // 常规布局
            //以上几种布局默认全部常规
            nowLayout = "CoursewareDown";
            OnetoManyLayoutUtil.ShowView(mRootHolder);
            resetTeatherPostion();
            if (!inList) {
                recoveryAllVideoTtems();
            }
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_NORMAL, nowLayout);
            setPopupWindowVisibility(View.VISIBLE);

        } else if ("MainPeople".equals(nowLayout)) {
            // 主讲
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_DOUBLE, nowLayout);
            setPopupWindowVisibility(View.GONE);
        } else if ("OnlyVideo".equals(nowLayout)) {
            // 自由
            mRootHolder.rel_fullscreen_videoitem.findViewById(R.id.fullscreen_sf_video).setVisibility(View.GONE);
            LayoutPopupWindow.getInstance().clickItem(LayoutPopupWindow.LAYOUT_VIDEO, nowLayout);
            setPopupWindowVisibility(View.GONE);
        }
    }

    /**
     * 接受纯音频教室切换
     *
     * @param inList
     */
    private void acceptSignalingOnlyAudioRoom(boolean inList) {
        isAudioTeaching = true;
        SetRoomInfor.getInstance().closeVideoAfterOpenOnlyAudioRoom(inList);
        mediaListAdapter.notifyDataChangeOnlyAudioRoom();

        for (int i = 0; i < videoItems.size(); i++) {
            videoItems.get(i).sf_video.setVisibility(View.INVISIBLE);
            videoItems.get(i).img_video_back.setVisibility(View.VISIBLE);
            videoItems.get(i).bg_video_back.setVisibility(View.VISIBLE);
            if (RoomSession.isOnliyAudioRoom) {
                videoItems.get(i).img_video_back.setImageResource(R.drawable.tk_zhanwei_audio);
            } else {
                videoItems.get(i).img_video_back.setImageResource(R.drawable.tk_icon_camera_close);
            }
        }
    }

    /***
     *  接受到双击视频放到白板的信令
     * @param data
     */
    private void acceptSignalingDoubleClickVideo(Object data) {
        if (mLayoutState > 1) {
            return;
        }

        //白板全屏时先缩回白板
        if (isZoom) {
            onWhiteBoradZoom(false);
        }

        mRootHolder.cb_message.setChecked(false);
        if (studentPopupWindow != null) {
            studentPopupWindow.dismiss();
        }

        JSONObject splitScreen = null;
        if (data instanceof String) {
            String str = (String) data;
            try {
                splitScreen = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            splitScreen = new JSONObject((Map<String, Object>) data);
        }

        String doubleId = splitScreen.optString("doubleId");
        screenID.clear();
        boolean isScreen = splitScreen.optBoolean("isScreen");
        if (isScreen) {
            stuMoveInfoMap.clear();
            screenID.add(doubleId);
        }

        if (videoItems.size() > 0) {
            for (int x = 0; x < videoItems.size(); x++) {
                if (screenID.contains(videoItems.get(x).peerid)) {
                    videoItems.get(x).isSplitScreen = true;

                    //当只显示自己和老师时
                    if (!videoItems.get(x).isOnlyShowTeachersAndVideos) {
                        RoomUser user = TKRoomManager.getInstance().getUser(videoItems.get(x).peerid);
                        if (user.getPublishState() == 3 || user.getPublishState() == 2) {
                            videoItems.get(x).sf_video.setVisibility(View.VISIBLE);
                            TKRoomManager.getInstance().playVideo(videoItems.get(x).peerid, videoItems.get(x).sf_video,
                                    RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                        }
                        if (user.getPublishState() == 3 || user.getPublishState() == 1) {
                            TKRoomManager.getInstance().playAudio(videoItems.get(x).peerid);
                        }
                    }
                } else {
                    videoItems.get(x).isSplitScreen = false;
                    videoItems.get(x).isMoved = false;

                    //当只显示自己和老师 拖拽回去时关掉声音
                    if (!videoItems.get(x).isOnlyShowTeachersAndVideos) {
                        TKRoomManager.getInstance().unPlayAudio(videoItems.get(x).peerid);
                        TKRoomManager.getInstance().unPlayVideo(videoItems.get(x).peerid);
                        videoItems.get(x).parent.setVisibility(View.GONE);
                        videoItems.get(x).sf_video.setVisibility(View.GONE);
                    }
                }
            }
            do1vsnStudentVideoLayout();
        }
    }

    /* *//***
     *   接收到打开聊天框的信令
     *//*
    private void acceptSignalingChatShow() {
        if (TKRoomManager.getInstance().getMySelf().role == -1) {
            if (!(mRootHolder.lv_chat_list.getVisibility() == View.VISIBLE)) {
                clearNoReadChatMessage();
                //打开聊天列表
                showChatPopupWindow();
            }
        }
    }*/

    /***
     *  接受到课件全屏同步信令
     * @param pubMsgTS
     * @param inList
     */
    private void acceptSignalingFullScreen(long pubMsgTS, Object data, boolean inList) {
        JSONObject jsonObject = null;
        if (data instanceof String) {
            String str = (String) data;
            try {
                jsonObject = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObject == null) {
            return;
        }

        String fullScreenType = jsonObject.optString("fullScreenType");
        /**
         * fullScreenType :
         * courseware_file 白板全屏
         * stream_video pc双击视频全屏（不做处理）
         * stream_media 播放视频点击全屏
         */
        if (fullScreenType.equals("courseware_file") || fullScreenType.equals("stream_media")) {//白板全屏
            if (RoomSession.isClassBegin && RoomControler.isFullScreenVideo()) {

                hidePopupWindow();
                //关闭画笔工具弹窗
                toolsView.dismissPop();
                setWhiteBoradEnlarge(true);
                //白板是否全屏控制聊天弹窗
                whiteBoardZoomToChat(false);

                for (int i = 0; i < RoomSession.playingList.size(); i++) {
                    if (RoomSession.playingList.get(i).role == 0) {
                        if (videofragment != null) {
                            videofragment.setFullscreenShow(RoomSession.playingList.get(i).peerId);
                        } else {
                            if (movieFragment != null) {
                                movieFragment.setFullscreenShow(RoomSession.playingList.get(i).peerId);
                            } else {
                                FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem, RoomSession.playingList.get(i), true);
                            }
                        }
                    }
                }
            }
        }
    }

    /***
     *     接受到全体禁言信令
     * @param pubMsgTS
     *  @param inList
     */
    private void acceptSignalingEveryoneBanChat(long pubMsgTS, boolean inList) {
        ChatData ch = new ChatData();
        ch.setStystemMsg(true);
        ch.setMsgTime(System.currentTimeMillis());
        ch.setMessage(getString(R.string.chat_prompt_yes));
        ch.setTrans(false);
        ch.setChatMsgState(1);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = null;
        if (StringUtils.isEmpty(RoomVariable.path)) {
            curDate = new Date(System.currentTimeMillis());//获取当前时间
        } else {
            curDate = new Date(pubMsgTS);
        }
        String str = formatter.format(curDate);
        ch.setTime(str);
        RoomSession.chatList.add(ch);

        chlistAdapter.notifyDataSetChanged();

        if (TKRoomManager.getInstance().getMySelf().role != 0) {
            if (inList) {
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "disablechat", true);
            }
        }
        //更新全体禁言状态
        mRootHolder.cb_choose_shut_chat.setChecked(true);
    }

    /***
     *     接受到视频框大小改变信令
     * @param data
     */
    private void acceptSignalingVideoChangeSize(Object data) {
        if (mLayoutState > 1) {
            return;
        }

        if (studentPopupWindow != null) {
            studentPopupWindow.dismiss();
        }
        JSONObject mapdata = null;
        if (data instanceof String) {
            String str = (String) data;
            try {
                mapdata = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mapdata = new JSONObject((Map<String, Object>) data);
        }
        JSONObject scaleVideoData = mapdata.optJSONObject("ScaleVideoData");
        if (scaleVideoData != null) {
            Iterator<String> scaleKeys = scaleVideoData.keys();
            while (scaleKeys.hasNext()) {
                String peerid = scaleKeys.next();
                JSONObject videoinfo = scaleVideoData.optJSONObject(peerid);
                float scale = (float) videoinfo.optDouble("scale");
                for (int x = 0; x < videoItems.size(); x++) {
                    if (videoItems.get(x).peerid.equals(peerid)) {
                        scalemap.put(peerid, scale);
                        LayoutZoomOrIn.zoomMsgMouldVideoItem(videoItems.get(x), scale, printWidth, printHeight, mRootHolder.rel_wb.getHeight());
                        break;
                    }
                }
            }
        }
        do1vsnStudentVideoLayout();
    }

    /***
     *    接受到分屏信令
     * @param data
     *//*
    private void acceptSignalingVideoSplitScreen(Object data) {
        mRootHolder.cb_message.setChecked(false);
        if (studentPopupWindow != null) {
            studentPopupWindow.dismiss();
        }
        JSONObject splitScreen = null;
        if (data instanceof String) {
            String str = (String) data;
            try {
                splitScreen = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            splitScreen = new JSONObject((Map<String, Object>) data);
        }
        screen = splitScreen.optJSONArray("userIDArry");
        try {
            screenID.clear();
            for (int y = 0; y < screen.length(); y++) {
                String peerid = (String) screen.get(y);
                screenID.add(peerid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (videoItems.size() > 0) {
            for (int x = 0; x < videoItems.size(); x++) {
                if (screenID.contains(videoItems.get(x).peerid)) {
                    videoItems.get(x).isSplitScreen = true;
                } else {
                    videoItems.get(x).isSplitScreen = false;
                    videoItems.get(x).isMoved = false;
                }
            }
            do1vsnStudentVideoLayout();
        }
    }*/

    /***
     *    接受到移动信令
     * @param data
     */
    private void acceptSignalingVideoDraghandle(Object data, boolean inList) {
        if (RoomSession.fullScreen || (screenID != null && screenID.size() > 0) || mLayoutState > 1) {
            return;
        }

        if (studentPopupWindow != null) {
            studentPopupWindow.dismiss();
        }
        JSONObject mapdata = null;
        if (data instanceof String) {
            String str = (String) data;
            try {
                mapdata = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mapdata = new JSONObject((Map<String, Object>) data);
        }
        videoarr = mapdata.optJSONObject("otherVideoStyle");
        if (videoarr != null) {
            sIterator = videoarr.keys();
            while (sIterator.hasNext()) {
                String peerid = sIterator.next();
                // 根据key获得value, value也可以是JSONObject,JSONArray,使用对应的参数接收即可
                JSONObject videoinfo = videoarr.optJSONObject(peerid);
                if (videoinfo != null) {
                    float left = (float) videoinfo.optDouble("percentLeft");
                    float top = (float) videoinfo.optDouble("percentTop");
                    boolean isDrag = Tools.isTure(videoinfo.opt("isDrag"));
                    MoveVideoInfo mi = new MoveVideoInfo();
                    if (inList && TKRoomManager.getInstance().getMySelf().peerId.equals(peerid)) {
                        mi.top = 0;
                        mi.left = 0;
                        mi.isDrag = false;
                    } else {
                        mi.top = top;
                        mi.left = left;
                        mi.isDrag = isDrag;
                    }
                    stuMoveInfoMap.put(peerid, mi);
                    if (inList) {
                        continue;
                    }
                    moveStudent(peerid, top, left, isDrag);
                }
            }
        }

        if (inList) {
            JSONObject jsonObject = new JSONObject();
            JSONObject moveData = new JSONObject();
            try {
                Set set = stuMoveInfoMap.keySet();
                if (set != null) {
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        JSONObject md = new JSONObject();
                        String peerid = (String) iterator.next();
                        MoveVideoInfo moveVideoInfo = stuMoveInfoMap.get(peerid);
                        md.put("percentTop", moveVideoInfo.top);
                        md.put("percentLeft", moveVideoInfo.left);
                        md.put("isDrag", moveVideoInfo.isDrag);
                        moveData.put(peerid, md);
                    }
                }
                jsonObject.put("otherVideoStyle", moveData);
                if (TKRoomManager.getInstance().getMySelf().role >= 0) {
                    TKRoomManager.getInstance().pubMsg("videoDraghandle", "videoDraghandle", "__allExceptSender", jsonObject.toString(), true, "ClassBegin", null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     *
     * @param data  接受到失败信令
     */
    private void acceptSignalingStreamFailure(Object data) {
        Map<String, Object> mapdata = null;
        if (data instanceof String) {
            String str = (String) data;
            try {
                JSONObject js = new JSONObject(str);
                mapdata = Tools.toMap(js);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mapdata = (Map<String, Object>) data;
        }
        String stupeerid = (String) mapdata.get("studentId");

        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            RoomUser u = TKRoomManager.getInstance().getUser(stupeerid);
            if (u != null) {
                if (u.properties.containsKey("passivityPublish")) {
                    int failuretype = -1;
                    if (u.properties.get("failuretype") != null) {
                        failuretype = (Integer) u.properties.get("failuretype");
                    }
                    switch (failuretype) {
                        case 1:
                            Toast.makeText(this, R.string.udp_faild, Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(this, R.string.publish_faild, Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(this, R.string.member_overload, Toast.LENGTH_LONG).show();
                            break;
                        case 4:
                            Toast.makeText(this, u.nickName + getResources().getString(R.string.select_back_hint), Toast.LENGTH_LONG).show();
                            break;
                        case 5:
                            Toast.makeText(this, R.string.udp_break, Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                u.properties.remove("passivityPublish");
            }
        }
    }

    /***
     *   接受到大并发信令
     */
    private void acceptSignalingBigRoom() {
        if (memberListPopupWindowUtils.isShowing()) {
            RoomOperation.getInstance().getBigRoomUnmberAndUsers(this);
        }
        allActionUtils.setGifStatu();
    }

    /***
     *   接受到时间更新信令
     */
    private void acceptSignalingUpdateTime() {
        if (RoomSession.isClassBegin) {
            if (RoomOperation.timerAddTime == null) {
                RoomOperation.timerAddTime = new Timer();
                RoomOperation.timerAddTime.schedule(new AddTime(), 1000, 1000);
            }
        }
    }

    /**
     * 接受到上课信令
     */
    private void acceptSignalingClassBegin(boolean inList) {
        //上课改变按钮状态
        initViewByRoomTypeAndTeacher();
        setWhiteBoradNarrow(false);

        if (TKRoomManager.getInstance().getMySelf().role == 0 && !inList) {
            LayoutPopupWindow.getInstance().setPubMsg();
        }

        if (!RoomControler.isReleasedBeforeClass()) {
            unPlaySelfAfterClassBegin();
        }
        //上课后是否自动发布音视频
        SetRoomInfor.getInstance().publishVideoAfterClass();

        //弹窗聊天框
        showChatPopupWindow();
        if (mPagesView != null) {
            mPagesView.resetLargeOrSmallView();
        }

        if (TKRoomManager.getInstance().getMySelf().role == 2 || TKRoomManager.getInstance().getMySelf().role == 4) {
            if (TKRoomManager.getInstance().getMySelf().canDraw) {
                toolsView.showTools(true);
            } else {
                toolsView.showTools(false);
            }
        } else {
            //上课时设置老师画笔颜色
            //SetRoomInfor.getInstance().setTeacherPenColor();
            SetRoomInfor.getInstance().setUserPenColor(TKRoomManager.getInstance().getMySelf());
            toolsView.showTools(true);
        }
    }

    /***
     *   收到信令消息删除回调
     * @param name            消息名字
     * @param delMsgTS              消息发送时间戳
     */
    @Override
    public void onRemoteDelMsg(String name, long delMsgTS) {

        switch (name) {
            case "ClassBegin": // 下课
                acceptSignalingClassOver();
                break;

           /* case "VideoSplitScreen":  //取消全体分屏
                acceptSignalingCancelVideoSplitScreen();
                break;*/

            case "EveryoneBanChat":  //取消全体禁言
                acceptSignalingCancelEveryoneBanChat(delMsgTS);
                break;

            case "FullScreen":   //取消课件全屏同步
                acceptSignalingCancelFullScreen();
                break;

            case "OnlyAudioRoom":  // 取消全音频教室
                mediaListAdapter.notifyDataChangeOnlyAudioRoom();
                isAudioTeaching = false;
                allActionUtils.setTeachingState(isAudioTeaching);
                break;
        }
    }

    /***
     *    接受到取消课件全屏同步
     */
    private void acceptSignalingCancelFullScreen() {
        SurfaceViewRenderer fullscreen_sf_video = mRootHolder.rel_fullscreen_videoitem.findViewById(R.id.fullscreen_sf_video);
        fullscreen_sf_video.clearImage();
        isZoom = false;
        setWhiteBoradNarrow(false);
        //白板是否全屏控制聊天弹窗
        whiteBoardZoomToChat(false);
        //白板缩小直接隐藏画中画 房间里老师可能不在
        if (videofragment != null) {
            videofragment.setFullscreenHide();
        } else {
            if (movieFragment != null) {
                movieFragment.setFullscreenHide();
            } else {
                //关闭画笔工具弹窗
                toolsView.dismissPop();
                //退出全屏恢复初始坐标
                MoveFullBoardUtil.getInstance().clean();
                FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem, null, false);
            }
        }
    }

    /***
     *     接受到取消全体禁言
     * @param delMsgTS
     */
    private void acceptSignalingCancelEveryoneBanChat(long delMsgTS) {
        ChatData ch = new ChatData();
        ch.setStystemMsg(true);
        ch.setMsgTime(System.currentTimeMillis());
        ch.setMessage(getString(R.string.chat_prompt_no));
        ch.setTrans(false);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = null;
        if (StringUtils.isEmpty(RoomVariable.path)) {
            curDate = new Date(System.currentTimeMillis());
        } else {
            curDate = new Date(delMsgTS);
        }
        String str = formatter.format(curDate);
        ch.setTime(str);

        RoomSession.chatList.add(ch);
        chlistAdapter.notifyDataSetChanged();
        //更新全体禁言状态
        mRootHolder.cb_choose_shut_chat.setChecked(false);
    }

    /***
     *   接受到取消全体分屏信令
     */
    private void acceptSignalingCancelVideoSplitScreen() {
        screenID.clear();
        if (videoItems.size() > 0) {
            for (int x = 0; x < videoItems.size(); x++) {
                videoItems.get(x).isSplitScreen = false;
            }
        }
        do1vsnStudentVideoLayout();
    }

    /***
     *   收到下课信令
     */
    private void acceptSignalingClassOver() {
        if (TKRoomManager.getInstance().getMySelf().role == -1) {
            finish();
            return;
        }
        //下课后改变按钮状态
        initViewByRoomTypeAndTeacher();

        if (TKRoomManager.getInstance().getMySelf().role != 0) {
            if (!RoomControler.isNotLeaveAfterClass()) {
                sendGiftPopUtils.deleteImage();
                TKRoomManager.getInstance().leaveRoom();
            }
        }
        mRootHolder.txt_hand_up.setClickable(false);
        mRootHolder.txt_hand_up.setText(R.string.raise);
        mRootHolder.txt_hour.setText("00");
        mRootHolder.txt_min.setText("00");
        mRootHolder.txt_ss.setText("00");
        recoveryAllVideoTtems();
        memberListAdapter.notifyDataSetChanged();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!RoomControler.isNotLeaveAfterClass()) {
                    RoomSession.chatList.clear();
                    chlistAdapter.notifyDataSetChanged();
                }
            }
        }, 250);

        if (mPagesView != null) {
            mPagesView.resetLargeOrSmallView();
        }
        toolsView.showTools(false);
    }

    /***
     *   回放结束回调
     */
    @Override
    public void onPlayBackEnd() {
        if (mPlayBackSeekPopupWindow != null) {
            mPlayBackSeekPopupWindow.onPlayBackEnd();
        }
    }

    /***
     *     回放播放进度回调
     * @param backTimePos   当前时间
     */
    @Override
    public void onPlayBackUpdateTime(long backTimePos) {
        if (mPlayBackSeekPopupWindow != null) {
            mPlayBackSeekPopupWindow.onPlayBackUpdateTime(backTimePos);
        }
    }

    /***
     *    网络媒体播放进度，状态回调
     * @param attributesPeerId    用户 Id
     * @param streamPos       播放进度
     * @param isPlay    是否在播放
     * @param onUpdateAttribute   流自定义扩展属性
     */
    @Override
    public void onUpdateAttributeStream(String attributesPeerId, long streamPos, boolean isPlay, Map<String, Object> onUpdateAttribute) {
        if (onUpdateAttribute.containsKey("video") && (boolean) onUpdateAttribute.get("video")) {
            if (videofragment == null) {
                if (wbFragment != null) {
                    WhiteBoradConfig.getsInstance().closeNewPptVideo();
                }
                isMediaMute = false;
                mediaListAdapter.setLocalfileid(onUpdateAttribute.get("fileid"));
            } else {
                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                    videofragment.controlMedia(onUpdateAttribute, streamPos, isPlay);
                }
            }
        } else {
            if (mRootHolder.sek_mp3 != null) {
                int curtime = (int) ((double) streamPos / (int) onUpdateAttribute.get("duration") * 100);
                mRootHolder.sek_mp3.setProgress(curtime);
            }
            if (mRootHolder.img_play_mp3 != null) {
                if (!isPlay) {
                    mRootHolder.img_play_mp3.setImageResource(R.drawable.tk_play);
                    gifDrawable.start();
                } else {
                    mRootHolder.img_play_mp3.setImageResource(R.drawable.tk_pause);
                    gifDrawable.stop();
                }
            }
            if (mRootHolder.txt_mp3_time != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
                Date curDate = new Date(streamPos);//获取当前时间
                Date daDate = new Date((int) onUpdateAttribute.get("duration"));
                String strcur = formatter.format(curDate);
                String strda = formatter.format(daDate);
                mRootHolder.txt_mp3_time.setText(strcur + "/" + strda);
            }
            if (mRootHolder.txt_mp3_name != null) {
                mRootHolder.txt_mp3_name.setText((String) onUpdateAttribute.get("filename"));
            }
        }
    }

    /***
     *    回放清除所有的数据的回调
     */
    @Override
    public void onPlayBackClearAll() {
        if (mRootHolder.rel_fullscreen_videoitem != null) {
            mRootHolder.rel_fullscreen_videoitem.setVisibility(View.GONE);
        }
        ToolCaseMgr.getInstance().cleanData(true);
        playBackClearAll();
        RoomSession.getInstance().resetRoomSession();

        if (mPlayBackSeekPopupWindow != null) {
            mPlayBackSeekPopupWindow.onPlayBackClearAll();
        }
        if (chlistAdapter != null) {
            chlistAdapter.notifyDataSetChanged();
        }

        for (int i = 0; i < videoItems.size(); i++) {
            resetVideoitem(videoItems.get(i));
            mRootHolder.rel_students.removeView(videoItems.get(i).parent);
        }
        videoItems.clear();

        if (videofragment != null) {
            mediafragmentManager = getSupportFragmentManager();
            ft = mediafragmentManager.beginTransaction();
            ft.remove(videofragment);
            ft.commitAllowingStateLoss();
            videofragment = null;
        }

        if (screenFragment != null) {
            screenFragment = ScreenFragment.getInstance();
            mediafragmentManager = getSupportFragmentManager();
            ft = mediafragmentManager.beginTransaction();
            if (screenFragment.isAdded()) {
                ft.remove(screenFragment);
                ft.commitAllowingStateLoss();
            }
            screenFragment = null;
        }

        if (movieFragment != null) {
            movieFragment = MovieFragment.getInstance();
            mediafragmentManager = getSupportFragmentManager();
            ft = mediafragmentManager.beginTransaction();
            if (movieFragment.isAdded()) {
                ft.remove(movieFragment);
                ft.commitAllowingStateLoss();
            }
            movieFragment = null;
        }
        mRootHolder.video_container.setVisibility(View.GONE);
    }

    /***
     *   回放时清除所有的信令
     */
    private void playBackClearAll() {
        for (int x = 0; x < videoItems.size(); x++) {
            videoItems.get(x).isSplitScreen = false;
            videoItems.get(x).isMoved = false;
        }
        screenID.clear();
        stuMoveInfoMap.clear();
        do1vsnStudentVideoLayout();
    }

    /***
     *   有屏幕共享的回调
     * @param peerIdScreen     共享者用户 id
     * @param state      媒体共享状态 0 停止 1 开始
     */
    @Override
    public void onShareScreenState(String peerIdScreen, int state) {
        mRootHolder.cb_message.setChecked(false);
        if (state == 0) {
            removeScreenFragment();
            changeVideoState();
            TKRoomManager.getInstance().unPlayScreen(peerIdScreen);
            mRootHolder.video_container.setVisibility(View.GONE);

        } else if (state == 1) {
            mRootHolder.rel_fullscreen_videoitem.findViewById(R.id.fullscreen_sf_video).setVisibility(View.GONE);
            screenFragment = ScreenFragment.getInstance();
            mediafragmentManager = getSupportFragmentManager();
            ft = mediafragmentManager.beginTransaction();

            if (wbFragment != null) {
                WhiteBoradConfig.getsInstance().closeNewPptVideo();
            }

            hidePopupWindow();
            hideSurfaceview();
            //关闭画笔工具弹窗
            toolsView.dismissPop();

            screenFragment.setPeerId(peerIdScreen);
            if (!screenFragment.isAdded()) {
                mRootHolder.video_container.setVisibility(View.VISIBLE);
                ft.replace(R.id.video_container, screenFragment);
                ft.commitAllowingStateLoss();
                //隐藏popupwindow
                setPopupWindowVisibility(View.GONE);
            }
        }
    }

    /***
     *  有文件媒体共享的回调
     * @param peerIdShareFile      共享者用户 id
     * @param state       媒体共享状态 0 停止 1 开始
     */
    @Override
    public void onShareFileState(String peerIdShareFile, int state) {
        mRootHolder.cb_message.setChecked(false);

        if (state == 0) {
            removeMovieFragment();
            changeVideoState();
            TKRoomManager.getInstance().unPlayFile(peerIdShareFile);
            mRootHolder.video_container.setVisibility(View.GONE);
            WhiteBoradConfig.getsInstance().hideWalkView(false);

        } else if (state == 1) {
            mRootHolder.rel_fullscreen_videoitem.findViewById(R.id.fullscreen_sf_video).setVisibility(View.GONE);
            movieFragment = MovieFragment.getInstance();
            movieFragment.setFullscreen_video_param(fullscreen_video_param);
            mediafragmentManager = getSupportFragmentManager();
            ft = mediafragmentManager.beginTransaction();

            hidePopupWindow();
            hideSurfaceview();
            //关闭画笔工具弹窗
            toolsView.dismissPop();

            WhiteBoradConfig.getsInstance().hideWalkView(true);

            if (isZoom && RoomControler.isFullScreenVideo()) {
                FullScreenControlUtil.changeFullSreenSate(mRootHolder.rel_fullscreen_videoitem,
                        null, false);
            }

            if (wbFragment != null) {
                WhiteBoradConfig.getsInstance().closeNewPptVideo();
            }

            movieFragment.setShareFilePeerId(peerIdShareFile);
            if (!movieFragment.isAdded()) {
                mRootHolder.video_container.setVisibility(View.VISIBLE);
                ft.replace(R.id.video_container, movieFragment);
                ft.commitAllowingStateLoss();
                setPopupWindowVisibility(View.GONE);
            }
        }
    }

    /***
     *   有网络媒体文件共享的回调    mp4/mp3
     * @param shareMediaPeerId   共享者用户 id
     * @param shareMediaState    媒体共享状态 0 停止 1 开始
     * @param shareMediaAttrs    自定义数据
     */
    @Override
    public void onShareMediaState(String shareMediaPeerId, int shareMediaState, Map<String, Object> shareMediaAttrs) {

        this.mediaAttrs = shareMediaAttrs;
        this.mediaPeerId = shareMediaPeerId;
        mediaListAdapter.setShareMediaAttrs(shareMediaAttrs);

        if (shareMediaState == 0) {    //媒体结束播放
            mediaListAdapter.setLocalfileid(-1);
            TKRoomManager.getInstance().unPlayMedia(shareMediaPeerId);

            if (shareMediaAttrs.containsKey("video")) {
                if ((boolean) shareMediaAttrs.get("video")) {
                    removeVideoFragment();
                    changeVideoState();
                } else {
                    if (mRootHolder != null) {
                        mRootHolder.lin_audio_seek.setVisibility(View.INVISIBLE);
                        mRootHolder.fl_play_disk.setVisibility(View.INVISIBLE);
                        mRootHolder.sek_mp3.setProgress(0);
                        mRootHolder.img_disk.clearAnimation();
                    }
                    gifDrawable.stop();
                }
            }

            WhiteBoradConfig.getsInstance().hideWalkView(false);

        } else if (shareMediaState == 1) {    //媒体开始播放
            if (wbFragment != null) {
                WhiteBoradConfig.getsInstance().closeNewPptVideo();
            }
            //关闭画笔工具弹窗
            toolsView.dismissPop();
            isMediaMute = false;
            mediaListAdapter.setLocalfileid(shareMediaAttrs.get("fileid"));

            if (RoomSession.isPublishMp4) {
                hidePopupWindow();
                hideSurfaceview();
                mRootHolder.cb_message.setChecked(false);
                WhiteBoradConfig.getsInstance().hideWalkView(true);
                readyForPlayVideo(shareMediaPeerId, shareMediaAttrs);
            } else {
                mRootHolder.fl_play_disk.setVisibility(View.VISIBLE);
                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                    mRootHolder.lin_audio_seek.setVisibility(View.VISIBLE);
                    mRootHolder.img_play_mp3.setVisibility(View.VISIBLE);
                } else {
                    mRootHolder.lin_audio_seek.setVisibility(View.INVISIBLE);
                    mRootHolder.img_play_mp3.setVisibility(View.INVISIBLE);
                }

                gifDrawable.start();

                if (shareMediaAttrs.containsKey("pause")) {
                    if ((boolean) shareMediaAttrs.get("pause")) {
                        gifDrawable.stop();
                    }
                }

                vol = 0.5;
                mRootHolder.sek_voice_mp3.setProgress((int) (vol * 100));
                int da = (int) shareMediaAttrs.get("duration");
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
                Date daDate = new Date(da);
                String mp3Duration = formatter.format(daDate);
                mRootHolder.txt_mp3_time.setText("00:00" + "/" + mp3Duration);
                if (mRootHolder.txt_mp3_name != null) {
                    mRootHolder.txt_mp3_name.setText((String) shareMediaAttrs.get("filename"));
                }
            }
        }
    }


    private void controlHandViewVisiable(boolean visiable) {
        if (visiable) {
            mRootHolder.hand_txt.setVisibility(View.VISIBLE);
            if (showHandTxtTime < 0) {
                showHandTxtTime = 0;
            }
        } else {
            mRootHolder.hand_txt.setVisibility(View.GONE);
            showHandTxtTime = -1;
        }
    }

    @Override
    public void onVideoStatsReport(String videoId, TkVideoStatsReport tkVideoStatsReport) {
        if (TKRoomManager.getInstance().getMySelf().peerId.equals(videoId)) {
            if (tkVideoStatsReport.video_net_level <= 2) {
                mRootHolder.img_wifi.setImageResource(R.drawable.tk_wifi_you);
                mRootHolder.txt_wifi_status.setText(R.string.wifi_you);
                mRootHolder.txt_wifi_status.setTextColor(getResources().getColor(R.color.wifi_you));
                if (wifiStatusPop.wifiStatusPop.isShowing()) {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_you_sanjiao_up);
                } else {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_you_sanjiao_down);
                }
                wifiStatusPop.setWifiStatus(1);
            } else if (tkVideoStatsReport.video_net_level <= 4) {
                mRootHolder.img_wifi.setImageResource(R.drawable.tk_wifi_zhong);
                mRootHolder.txt_wifi_status.setText(R.string.wifi_zhong);
                mRootHolder.txt_wifi_status.setTextColor(getResources().getColor(R.color.wifi_zhong));
                if (wifiStatusPop.wifiStatusPop.isShowing()) {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_zhong_sanjiao_up);
                } else {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_zhong_sanjiao_down);
                }
                wifiStatusPop.setWifiStatus(2);
            } else if (tkVideoStatsReport.video_net_level <= 6) {
                mRootHolder.img_wifi.setImageResource(R.drawable.tk_wifi_cha);
                mRootHolder.txt_wifi_status.setText(R.string.wifi_cha);
                mRootHolder.txt_wifi_status.setTextColor(getResources().getColor(R.color.wifi_cha));
                if (wifiStatusPop.wifiStatusPop.isShowing()) {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_cha_sanjiao_up);
                } else {
                    mRootHolder.img_wifi_down.setImageResource(R.drawable.tk_wifi_cha_sanjiao_down);
                }
                wifiStatusPop.setWifiStatus(3);
            }
            if (tkVideoStatsReport.totalPackets > 0) {
                wifiStatusPop.setPageloseAndDelay(String.valueOf((double) (tkVideoStatsReport.packetsLost /
                        tkVideoStatsReport.totalPackets)), String.valueOf(tkVideoStatsReport.currentDelay));
            }
        }
    }

    /**
     * 在进入教室之前的信令发送完毕后会调用
     * 用于教室的初始化
     *
     * @param infoCode
     * @param message
     */
    @Override
    public void onInfo(int infoCode, String message) {
        super.onInfo(infoCode, message);
        if (infoCode == 1506) {
            if (!RoomSession.isClassBegin) {
                playSelfBeforeClassBegin();
            }
        }
    }

    /***
     *   隐藏视频框
     */
    private void hideSurfaceview() {
        for (int i = 0; i < videoItems.size(); i++) {
            videoItems.get(i).sf_video.setZOrderMediaOverlay(false);
            videoItems.get(i).sf_video.setVisibility(View.GONE);
            videoItems.get(i).bg_video_back.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                break;
        }
    }
}
