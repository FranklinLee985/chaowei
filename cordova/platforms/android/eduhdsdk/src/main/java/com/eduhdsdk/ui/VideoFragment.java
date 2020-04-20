package com.eduhdsdk.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.classroomsdk.Config;
import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.common.ToolsType;
import com.classroomsdk.common.VideoPaint;
import com.classroomsdk.manage.SharePadMgr;
import com.classroomsdk.manage.WBSession;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.tools.ScreenScale;
import com.classroomsdk.utils.FullScreenTools;
import com.eduhdsdk.BuildConfig;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.room.RoomClient;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.viewutils.FullScreenControlUtil;
import com.eduhdsdk.viewutils.MoveFullBoardUtil;
import com.eduhdsdk.viewutils.PlaybackControlUtils;
import com.eduhdsdk.toolcase.ToolsEraserPopupWindow;
import com.eduhdsdk.toolcase.ToolsVideoPenPopupWindow;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tkwebrtc.EglBase;
import org.tkwebrtc.EglRenderer;
import org.tkwebrtc.RendererCommon;
import org.tkwebrtc.SurfaceViewRenderer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/***
 *    mp4播放界面
 */
public class VideoFragment extends TKBaseFragment {

    private static VideoFragment mInstance = null;
    private RelativeLayout lin_video_play;
    private SurfaceViewRenderer suf_mp4;
    private ImageView img_close_mp4;
    private LinearLayout lin_video_control;
    private ImageView img_play_mp4;
    private TextView txt_mp4_name;
    private TextView txt_mp4_time;
    private SeekBar sek_mp4;
    private ImageView img_voice_mp4;
    private SeekBar sek_voice_mp4;
    //底层画板
    private VideoPaint videoPaint;
    //上层画板
    private VideoPaint videoPaintTop;
    //画笔工具
    private LinearLayout tools_include;
    private ImageView tools_pen, tools_eraser, tools_out;
    //画笔pop
    private ToolsVideoPenPopupWindow mToolsPenPopupWindow;
    //橡皮pop
    private ToolsEraserPopupWindow mToolsEraserPopupWindow;

    private String shareMediaPeerId;
    private Map<String, Object> shareMediaAttrs;
    private double vol = 0.5;
    private boolean isMute = false;
    private double ratio = (double) 16 / (double) 9;

    private EglRenderer.FrameListener frameListener;
    private RelativeLayout re_laoding;
    private ImageView loadingImageView;
    //白板全屏右下角视频界面
    private RelativeLayout rel_fullscreen_mp4videoitem, fullscreen_inback_rel;
    private SurfaceViewRenderer fullscreen_sf_video;
    private ImageView fullscreen_bg_video_back, fullscreen_img_video_back;
    //左下角画中画suf大小
    private RelativeLayout.LayoutParams fullscreen_video_param;

    //宽高
    public int containerWidth;
    public int containerHeight;

    private PlaybackControlUtils playbackControlUtils;//回放进度条的显示隐藏工具类

    public void setStream(String shareMediaPeerId, Map<String, Object> shareMediaAttrs) {
        this.shareMediaPeerId = shareMediaPeerId;
        this.shareMediaAttrs = shareMediaAttrs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(setView(), container, false);

        //下层画板
        videoPaint = view.findViewById(R.id.videoPaint);
        videoPaint.setPadMgr(SharePadMgr.getInstance());
        videoPaint.setContext(getActivity());
        videoPaint.setSoundEffectsEnabled(false);
        videoPaint.setDrawShow(false);
        videoPaint.setClickable(true);
        //上层画板
        videoPaintTop = view.findViewById(R.id.videoPaintTop);
        videoPaintTop.setPadMgr(SharePadMgr.getInstance());
        videoPaintTop.setContext(getActivity());
        videoPaintTop.setDrawShow(true);
        videoPaintTop.setSoundEffectsEnabled(false);
        videoPaintTop.setClickable(true);

        //画笔工具
        tools_include = view.findViewById(R.id.tools_include);
        tools_pen = tools_include.findViewById(R.id.tools_pen);
        tools_eraser = tools_include.findViewById(R.id.tools_eraser);
        tools_out = tools_include.findViewById(R.id.tools_out);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tools_include.getLayoutParams();
        layoutParams.leftMargin = FullScreenTools.getStatusBarHeight(getContext());
        tools_include.setLayoutParams(layoutParams);
        return view;
    }

    @Override
    protected int setView() {
        return R.layout.tk_fragment_video;
    }

    @Override
    protected void init(View view) {

        lin_video_play = (RelativeLayout) view.findViewById(R.id.lin_video_play);
        suf_mp4 = (SurfaceViewRenderer) view.findViewById(R.id.suf_mp4);


        re_laoding = (RelativeLayout) view.findViewById(R.id.re_laoding);
        loadingImageView = (ImageView) view.findViewById(R.id.loadingImageView);

        img_close_mp4 = (ImageView) view.findViewById(R.id.img_close_mp4);
        lin_video_control = (LinearLayout) view.findViewById(R.id.lin_video_control);

        img_play_mp4 = (ImageView) lin_video_control.findViewById(R.id.img_play);
        txt_mp4_name = (TextView) lin_video_control.findViewById(R.id.txt_media_name);
        txt_mp4_time = (TextView) lin_video_control.findViewById(R.id.txt_media_time);

        sek_mp4 = (SeekBar) lin_video_control.findViewById(R.id.sek_media);
        sek_mp4.setPadding((int) (10 * ScreenScale.getWidthScale()), 0, (int) (10 * ScreenScale.getWidthScale()), 0);

        img_voice_mp4 = (ImageView) lin_video_control.findViewById(R.id.img_media_voice);
        sek_voice_mp4 = (SeekBar) lin_video_control.findViewById(R.id.sek_media_voice);
        sek_voice_mp4.setPadding((int) (10 * ScreenScale.getWidthScale()), 0, (int) (10 * ScreenScale.getWidthScale()), 0);

        //白板全屏右下角视频界面
        rel_fullscreen_mp4videoitem = (RelativeLayout) view.findViewById(R.id.rel_fullscreen_mp4videoitem);
        fullscreen_sf_video = (SurfaceViewRenderer) rel_fullscreen_mp4videoitem.findViewById(R.id.fullscreen_sf_video);
        try {
            EglBase eglBase = RoomClient.getInstance().getPreEgl();
            EglBase eglBase1 = RoomClient.getInstance().getPreEgl();
            suf_mp4.init(eglBase1.getEglBaseContext(), null);
            fullscreen_sf_video.init(eglBase.getEglBaseContext(), null);
            RoomClient.getInstance().setPreEglMap(eglBase, true);
            RoomClient.getInstance().setPreEglMap(eglBase1, true);
        } catch (RuntimeException e) {
            if (BuildConfig.DEBUG)
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        fullscreen_bg_video_back = (ImageView) rel_fullscreen_mp4videoitem.findViewById(R.id.fullscreen_bg_video_back);
        fullscreen_img_video_back = (ImageView) rel_fullscreen_mp4videoitem.findViewById(R.id.fullscreen_img_video_back);
        fullscreen_inback_rel = rel_fullscreen_mp4videoitem.findViewById(R.id.re_suf_background);

        if (RoomSession.isShowVideoWB) {
            videoPaint.setVisibility(View.VISIBLE);
        } else {
            videoPaint.setVisibility(View.INVISIBLE);
            tools_include.setVisibility(View.INVISIBLE);
        }

        ScreenScale.scaleView(view, "VideoFragment");
        //白板全屏右下角界面大小
        if (fullscreen_video_param != null) {
            if (fullscreen_sf_video != null)
                fullscreen_sf_video.setLayoutParams(fullscreen_video_param);
            fullscreen_bg_video_back.setLayoutParams(fullscreen_video_param);
            fullscreen_img_video_back.setLayoutParams(fullscreen_video_param);
            fullscreen_inback_rel.setLayoutParams(fullscreen_video_param);
        }
        //初始
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        containerWidth = dm.widthPixels;
        containerHeight = dm.heightPixels;
        if (rel_fullscreen_mp4videoitem != null) {
            MoveFullBoardUtil.getInstance().SetWH(containerWidth, containerHeight);
            MoveFullBoardUtil.getInstance().SetViewOnTouchListener(rel_fullscreen_mp4videoitem);
        }
        if (playbackControlUtils == null) {
            playbackControlUtils = new PlaybackControlUtils(getContext(), null);
        }

        //初始化画笔工具
        initTools();
    }

    /**
     * 初始化画笔工具
     */
    private void initTools() {

        tools_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mToolsPenPopupWindow != null) {
                    videoPaintTop.setVisibility(View.VISIBLE);
                    tools_pen.setImageResource(R.drawable.tk_video_pen_press);
                    tools_eraser.setImageResource(R.drawable.tk_video_xiangpi_default);
                    videoPaintTop.setToolsType(ToolsType.pen);
                    if (mToolsEraserPopupWindow != null) {
                        mToolsEraserPopupWindow.dismisspop();
                    }
                    mToolsPenPopupWindow.showPopPen(tools_pen, tools_include.getWidth());
                }
            }
        });

        tools_eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mToolsEraserPopupWindow != null) {
                    tools_pen.setImageResource(R.drawable.tk_video_pen_default);
                    tools_eraser.setImageResource(R.drawable.tk_video_xiangpi_press);
                    videoPaint.setToolsType(ToolsType.eraser);
                    videoPaint.requestParentFocus(true);
                    videoPaintTop.setVisibility(View.GONE);
                    if (mToolsPenPopupWindow != null) {
                        mToolsPenPopupWindow.dismisspop();
                    }
                    mToolsEraserPopupWindow.showPopEraserToRight(tools_eraser, tools_include.getWidth());
                }
            }
        });

        tools_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER && RoomSession.isClassBegin &&
                        RoomControler.isShowVideoWhiteBoard()) {
                    TKRoomManager.getInstance().delMsg("VideoWhiteboard",
                            "VideoWhiteboard", "__all", null);
                    tools_include.setVisibility(View.GONE);
                    videoPaintTop.requestParentFocus(false);
                    videoPaint.requestParentFocus(false);
                }
                TKRoomManager.getInstance().playMedia(true);
            }
        });

        //笔
        mToolsPenPopupWindow = new ToolsVideoPenPopupWindow(getActivity());
        mToolsPenPopupWindow.SetOnToolsListener(new ToolsVideoPenPopupWindow.onToolsPenListener() {
            @Override
            public void SelectedColor(int color) {
                videoPaintTop.setToolsPenColor(color);
            }

            @Override
            public void SeekBarProgress(int progress) {
                videoPaintTop.setToolsPenProgress(progress);
            }
        });

        //橡皮
        mToolsEraserPopupWindow = new ToolsEraserPopupWindow(getActivity(), true);
        mToolsEraserPopupWindow.SetonToolsListener(new ToolsEraserPopupWindow.onToolsListener() {
            @Override
            public void SeekBarSize(int size) {
                videoPaint.setToolsEraserWidth(size);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    static public VideoFragment getInstance() {
        synchronized (VideoFragment.class) {
            if (mInstance == null) {
                mInstance = new VideoFragment();
            }
            return mInstance;
        }
    }

    /**
     * 全屏画中画隐藏
     */
    public void setFullscreenHide() {
        fullscreen_sf_video.setVisibility(View.GONE);
        FullScreenControlUtil.changeFullSreenSate(rel_fullscreen_mp4videoitem, null, false);
    }

    public void changeInBackground(boolean inback, int role) {
        FullScreenControlUtil.changeInBackground(rel_fullscreen_mp4videoitem, inback, role);
    }

    /**
     * 全屏画中画显示
     *
     * @param peerId 用户id
     */
    public void setFullscreenShow(String peerId) {
        if (fullscreen_sf_video != null) {
            FullScreenControlUtil.changeFullSreenSate(rel_fullscreen_mp4videoitem,
                    TKRoomManager.getInstance().getUser(peerId), true);
        }
    }

    public void setFullscreenShow(String peerId, boolean isShow) {
        if (fullscreen_sf_video != null) {
            FullScreenControlUtil.changeFullSreenSate(rel_fullscreen_mp4videoitem,
                    TKRoomManager.getInstance().getUser(peerId), isShow);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        re_laoding.setVisibility(View.VISIBLE);
        Glide.with(getActivity()).asGif()
                .load(R.drawable.tk_loading)
                .into(loadingImageView);

        if (shareMediaPeerId != null) {
            //suf_mp4.setZOrderOnTop(false);
            suf_mp4.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            TKRoomManager.getInstance().playMedia(shareMediaPeerId, suf_mp4);
            suf_mp4.requestLayout();

            suf_mp4.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    videoPaint.setPadSizeAndMode(3, width, height);
                    videoPaintTop.setPadSizeAndMode(3, width, height);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });

            if (txt_mp4_name != null) {
                txt_mp4_name.setText((String) shareMediaAttrs.get("filename"));
            }

            //fullscreen_sf_video.setZOrderOnTop(true);
            suf_mp4.setZOrderMediaOverlay(false);
            fullscreen_sf_video.setZOrderOnTop(true);
            fullscreen_sf_video.setZOrderMediaOverlay(true);
        }

        //视频播放时不显示画笔，点击弹出进度条
        videoPaintTop.setTouchHandler(new VideoPaint.TouchHandler() {
            @Override
            public void handler() {
                playbackControlUtils.startHideTimer(lin_video_control);
            }
        });

        //画布不显示时 点击视频界面弹出进度条
        lin_video_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackControlUtils.startHideTimer(lin_video_control);
            }
        });

        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
            img_close_mp4.setVisibility(View.VISIBLE);
            lin_video_control.setVisibility(View.VISIBLE);
        } else {
            img_close_mp4.setVisibility(View.INVISIBLE);
            lin_video_control.setVisibility(View.INVISIBLE);
        }

        img_close_mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                TKRoomManager.getInstance().stopShareMedia();
                TKRoomManager.getInstance().delMsg("VideoWhiteboard", "VideoWhiteboard",
                        "__all", null);
                img_close_mp4.setClickable(false);
            }
        });

        img_play_mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackControlUtils.startHideTimer(lin_video_control);
                if (RoomSession.isPublishMp4) {
                    boolean ispause = (Boolean) shareMediaAttrs.get("pause") == null ?
                            false : (Boolean) shareMediaAttrs.get("pause");

                    if (ispause && RoomControler.isNotCloseVideoPlayer() && totleTime == 100) {
                        TKRoomManager.getInstance().seekMedia(0);
                    }

                    TKRoomManager.getInstance().playMedia(ispause);
                    if (ispause) {
                        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER && RoomSession.isClassBegin && RoomControler.isShowVideoWhiteBoard()) {
                            TKRoomManager.getInstance().delMsg("VideoWhiteboard", "VideoWhiteboard", "__all", null);
                        }
                    } else {
                        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER && RoomSession.isClassBegin && RoomControler.isShowVideoWhiteBoard()) {
                            JSONObject js = new JSONObject();
                            try {
                                js.put("videoRatio", ratio);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            TKRoomManager.getInstance().pubMsg("VideoWhiteboard", "VideoWhiteboard", "__all", js.toString(), true, "ClassBegin", null);
                        }
                    }

                    if (Integer.parseInt(WBSession.whiteboardcolorIndex) - 1 == 0) {
                        videoPaintTop.setToolsPenColor(Color.parseColor(Config.mColor[5]));
                        mToolsPenPopupWindow.colorSelectorView.setmSelectIndex(5);
                    } else {
                        videoPaintTop.setToolsPenColor(Color.parseColor(Config.mColor[0]));
                        mToolsPenPopupWindow.colorSelectorView.setmSelectIndex(0);
                    }
                    mToolsPenPopupWindow.seekBar.setProgress(10);
                } else {
                    ShareDoc media = WhiteBoradConfig.getsInstance().getCurrentMediaDoc();
                    WhiteBoradConfig.getsInstance().setCurrentMediaDoc(media);
                    String strSwfpath = media.getSwfpath();
                    if (strSwfpath != null && !TextUtils.isEmpty(strSwfpath)) {
                        int pos = strSwfpath.lastIndexOf('.');
                        strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
                        String url = "http://" + WhiteBoradConfig.getsInstance().getFileServierUrl() + ":" +
                                WhiteBoradConfig.getsInstance().getFileServierPort() + strSwfpath;

                        HashMap<String, Object> attrMap = new HashMap<String, Object>();
                        attrMap.put("filename", media.getFilename());
                        attrMap.put("fileid", media.getFileid());

                        if (RoomSession.isClassBegin) {
                            TKRoomManager.getInstance().startShareMedia(url, true, "__all", attrMap);
                        } else {
                            TKRoomManager.getInstance().startShareMedia(url, true, TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                        }
                    }
                }
            }
        });

        sek_mp4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                playbackControlUtils.startHideTimer(lin_video_control);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playbackControlUtils.startHideTimer(lin_video_control);
                double currenttime = 0;
                if (isfromUser && shareMediaAttrs != null) {
                    currenttime = ((double) pro / (double) seekBar.getMax()) * (int) shareMediaAttrs.get("duration");
                    TKRoomManager.getInstance().seekMedia((long) currenttime);
                }
            }
        });

        TKRoomManager.getInstance().setRemoteAudioVolume(vol, shareMediaPeerId, 2);
        img_voice_mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMute) {
                    TKRoomManager.getInstance().setRemoteAudioVolume(vol, shareMediaPeerId, 2);
                    img_voice_mp4.setImageResource(R.drawable.tk_icon_voice);
                    sek_voice_mp4.setProgress((int) (vol * 100));
                } else {
                    TKRoomManager.getInstance().setRemoteAudioVolume(0, shareMediaPeerId, 2);
                    img_voice_mp4.setImageResource(R.drawable.tk_icon_no_voice);
                    sek_voice_mp4.setProgress(0);
                }
                isMute = !isMute;
            }
        });
        sek_voice_mp4.setProgress((int) (vol * 100));
        sek_voice_mp4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = (float) progress / (float) seekBar.getMax();
                if (vol > 0) {
                    img_voice_mp4.setImageResource(R.drawable.tk_icon_voice);
                } else {
                    img_voice_mp4.setImageResource(R.drawable.tk_icon_no_voice);
                }
                TKRoomManager.getInstance().setRemoteAudioVolume(vol, shareMediaPeerId, 2);
                if (fromUser) {
                    VideoFragment.this.vol = vol;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        frameListener = new EglRenderer.FrameListener() {
            @Override
            public void onFrame(final Bitmap bitmap) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        re_laoding.setVisibility(View.GONE);
                        playbackControlUtils.startHideTimer(lin_video_control);
                        setSync();
                    }
                });
            }
        };

        suf_mp4.addFrameListener(frameListener, 0);
    }

    /**
     * 隐藏加载框
     *
     * @param peerIdVideo
     */
    public void hideLaoding(String peerIdVideo) {
        if (re_laoding != null) {
            re_laoding.setVisibility(View.GONE);
        }
    }

    /**
     * 开始接受到媒体流
     */
    public void setSync() {
        //当白板全屏且企业配置画中画
        if (RoomSession.fullScreen && RoomControler.isFullScreenVideo()) {
            //显示全屏同步画中画
            //当房间类型为0时1to1
            RoomUser roomUser = null;
            if (RoomInfo.getInstance().getRoomType() == 0) {
                //学生只能看到老师
                if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
                    for (int i = 0; i < RoomSession.playingList.size(); i++) {
                        if (0 == RoomSession.playingList.get(i).role) {
                            roomUser = RoomSession.playingList.get(i);
                            break;
                        }
                    }
                    FullScreenControlUtil.changeFullSreenSate(rel_fullscreen_mp4videoitem, roomUser, true);
                } else {
                    //其他身份看到的都是学生
                    for (int i = 0; i < RoomSession.playingList.size(); i++) {
                        if (RoomSession.playingList.get(i).role == 2) {
                            roomUser = RoomSession.playingList.get(i);
                            break;
                        }
                    }
                    FullScreenControlUtil.changeFullSreenSate(rel_fullscreen_mp4videoitem, roomUser, true);
                }
            } else {
                //一对多只显示老师
                for (int i = 0; i < RoomSession.playingList.size(); i++) {
                    if (RoomSession.playingList.get(i).role == 0) {
                        roomUser = RoomSession.playingList.get(i);
                        break;
                    }
                }
                FullScreenControlUtil.changeFullSreenSate(rel_fullscreen_mp4videoitem, roomUser, true);
            }
        } else {
            FullScreenControlUtil.changeFullSreenSate(rel_fullscreen_mp4videoitem, null, false);
        }
    }

    int totleTime;

    /**
     * 网络媒体播放进度，状态回调
     *
     * @param onUpdateAttributeAttrs 流自定义扩展属性
     * @param pos                    播放进度
     * @param isPlay                 是否在播放
     */
    public void controlMedia(Map<String, Object> onUpdateAttributeAttrs, long pos, boolean isPlay) {

        if (RoomControler.isShowVideoWhiteBoard()) {
            if (isPlay) {
                if (RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                    tools_include.setVisibility(View.VISIBLE);
                    videoPaintTop.requestParentFocus(true);
                }
            } else {
                videoPaintTop.requestParentFocus(false);
                videoPaint.clearPab();
                tools_include.setVisibility(View.GONE);
            }
        }

        if (sek_mp4 != null) {
            int curtime = (int) ((double) pos / (int) onUpdateAttributeAttrs.get("duration") * 100);
            totleTime = curtime;
            sek_mp4.setProgress(curtime);
        }
        if (img_play_mp4 != null) {
            if (!isPlay) {
                img_play_mp4.setImageResource(R.drawable.tk_btn_pause_normal);
            } else {
                img_play_mp4.setImageResource(R.drawable.tk_btn_play_normal);
            }
        }

        if (txt_mp4_time != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
            Date curDate = new Date(pos);
            Date daDate = new Date((int) onUpdateAttributeAttrs.get("duration"));
            String strcur = formatter.format(curDate);
            String strda = formatter.format(daDate);
            txt_mp4_time.setText(strcur + "/" + strda);
        }

        if (txt_mp4_name != null) {
            txt_mp4_name.setText((String) onUpdateAttributeAttrs.get("filename"));
        }

        if (onUpdateAttributeAttrs.containsKey("width") && onUpdateAttributeAttrs.containsKey("height") &&
                ((int) onUpdateAttributeAttrs.get("width")) != 0 && ((int) onUpdateAttributeAttrs.get("height")) != 0) {
            int wid = (int) onUpdateAttributeAttrs.get("width");
            int hid = (int) onUpdateAttributeAttrs.get("height");
            ratio = (double) wid / (double) hid;
        }
    }


    Timer timer = new Timer();

    @Override
    public void onDestroyView() {
        //恢复初始视频框
        MoveFullBoardUtil.getInstance().clean();
        RoomSession.jsVideoWBTempMsg = new JSONArray();
        if (suf_mp4 != null) {
            suf_mp4.release();
            suf_mp4 = null;
        }
        if (fullscreen_sf_video != null) {
            fullscreen_sf_video.release();
            fullscreen_sf_video = null;
        }
        isMute = false;
        if (videoPaint != null) {
            videoPaint = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (suf_mp4 != null && frameListener != null) {
            suf_mp4.removeFrameListener(frameListener);
            frameListener = null;
        }
        mInstance = null;
        super.onDestroyView();
    }

    /**
     * 信令信息
     *
     * @param add                 true信令添加，false信令删除
     * @param id                  信令id
     * @param name                信令名称
     * @param ts                  消息发送时间戳
     * @param data                信令参数
     * @param fromID              发送者id
     * @param associatedMsgID     消息关联消息的 id （该消息删除时会跟随删除）
     * @param associatedUserID    消息关联用户的 id （该用户退出时会跟随删除）
     * @param jsonObjectRemoteMsg 消息携带数据
     */
    protected void onRemoteMsg(final boolean add, final String id, final String name, final long ts, final Object data, final String fromID, final String associatedMsgID, final String associatedUserID, JSONObject jsonObjectRemoteMsg) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (add) {
                        if (name.equals("VideoWhiteboard")) {
                            if (videoPaint != null) {
                                if (suf_mp4 != null) {
                                    suf_mp4.setZOrderMediaOverlay(false);
                                    suf_mp4.setZOrderOnTop(false);
                                }
                                videoPaint.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (name.equals("VideoWhiteboard")) {
                            videoPaint.clearPab();
                        }
                    }
                }
            });
        }
    }

    /**
     * 回放时清除所有信令
     */
    protected void roomPlaybackClearAll() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    /**
     * 房间失去连接
     */
    protected void roomDisConnect() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    /**
     * 离开房间
     */
    protected void onRoomLeaved() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    public void setFullscreen_video_param(RelativeLayout.LayoutParams fullscreen_video_param) {
        this.fullscreen_video_param = fullscreen_video_param;
    }
}
