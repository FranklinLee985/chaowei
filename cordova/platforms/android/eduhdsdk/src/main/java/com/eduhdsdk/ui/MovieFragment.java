package com.eduhdsdk.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.classroomsdk.common.ToolsType;
import com.classroomsdk.common.VideoPaint;
import com.classroomsdk.manage.SharePadMgr;
import com.eduhdsdk.BuildConfig;
import com.eduhdsdk.R;
import com.eduhdsdk.room.RoomClient;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.viewutils.FullScreenControlUtil;
import com.eduhdsdk.viewutils.MoveFullBoardUtil;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tkwebrtc.EglBase;
import org.tkwebrtc.EglRenderer;
import org.tkwebrtc.RendererCommon;
import org.tkwebrtc.SurfaceViewRenderer;

/**
 * Created by Administrator on 2018/2/27/027.  电影共享界面
 */
public class MovieFragment extends TKBaseFragment {


    static private MovieFragment mInstance = null;
    private SurfaceViewRenderer suf_mp4;
    private String peerIdShareFile;
    private EglRenderer.FrameListener frameListener;
    private RelativeLayout re_laoding;
    private ImageView loadingImageView;
    //画板
    private VideoPaint videoPaint;
    //白板全屏右下角视频界面
    private RelativeLayout rel_fullscreen_mp4videoitem;
    private SurfaceViewRenderer fullscreen_sf_video;
    private ImageView fullscreen_bg_video_back, fullscreen_img_video_back;

    private RelativeLayout.LayoutParams fullscreen_video_param;


    //宽高
    public int containerWidth;
    public int containerHeight;

    @Override
    protected int setView() {
        return R.layout.tk_fragment_movie;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(setView(), container, false);

        //画板
        videoPaint = view.findViewById(R.id.videoPaint);
        videoPaint.setPadMgr(SharePadMgr.getInstance());
        videoPaint.setContext(getActivity());
        videoPaint.setSoundEffectsEnabled(false);
        videoPaint.requestParentFocus(false);
        videoPaint.setToolsType(ToolsType.defaule);
        return view;
    }

    @Override
    protected void init(View view) {

        suf_mp4 = (SurfaceViewRenderer) view.findViewById(R.id.suf_mp4);
        suf_mp4.init(EglBase.create().getEglBaseContext(), null);

        re_laoding = (RelativeLayout) view.findViewById(R.id.re_laoding);
        loadingImageView = (ImageView) view.findViewById(R.id.loadingImageView);

        //白板全屏右下角视频界面
        rel_fullscreen_mp4videoitem = (RelativeLayout) view.findViewById(R.id.rel_fullscreen_mp4videoitem);
        fullscreen_sf_video = (SurfaceViewRenderer) rel_fullscreen_mp4videoitem.findViewById(R.id.fullscreen_sf_video);
        try {
            EglBase eglBase = RoomClient.getInstance().getPreEgl();
            fullscreen_sf_video.init(eglBase.getEglBaseContext(), null);
            RoomClient.getInstance().setPreEglMap(eglBase, true);
        } catch (RuntimeException e) {
            if (BuildConfig.DEBUG)
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        fullscreen_bg_video_back = (ImageView) rel_fullscreen_mp4videoitem.findViewById(R.id.fullscreen_bg_video_back);
        fullscreen_img_video_back = (ImageView) rel_fullscreen_mp4videoitem.findViewById(R.id.fullscreen_img_video_back);

        if (RoomSession.isShowVideoWB) {
            videoPaint.setVisibility(View.VISIBLE);
        } else {
            videoPaint.setVisibility(View.INVISIBLE);
        }


        //白板全屏右下角界面大小
        if (fullscreen_video_param != null) {
            fullscreen_sf_video.setLayoutParams(fullscreen_video_param);
            fullscreen_bg_video_back.setLayoutParams(fullscreen_video_param);
            fullscreen_img_video_back.setLayoutParams(fullscreen_video_param);
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
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 媒体共享
     *
     * @param peerIdShareFile 文件id
     */
    public void setShareFilePeerId(String peerIdShareFile) {
        this.peerIdShareFile = peerIdShareFile;
        TKRoomManager.getInstance().playFile(peerIdShareFile, suf_mp4);
    }

    static public MovieFragment getInstance() {
        synchronized (MovieFragment.class) {
            if (mInstance == null) {
                mInstance = new MovieFragment();
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
            FullScreenControlUtil.changeInBackground(  rel_fullscreen_mp4videoitem, inback, role);
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

        if (peerIdShareFile != null) {
            suf_mp4.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            TKRoomManager.getInstance().playFile(peerIdShareFile, suf_mp4);
            suf_mp4.requestLayout();

            suf_mp4.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    videoPaint.setPadSizeAndMode(3, width, height);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });

            suf_mp4.setZOrderMediaOverlay(false);
            fullscreen_sf_video.setZOrderOnTop(true);
            fullscreen_sf_video.setZOrderMediaOverlay(true);
            fullscreen_sf_video.requestLayout();
        }

        frameListener = new EglRenderer.FrameListener() {
            @Override
            public void onFrame(final Bitmap bitmap) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        re_laoding.setVisibility(View.GONE);
                        setSync();
                    }
                });
            }
        };
        suf_mp4.addFrameListener(frameListener, 0);
    }

    /**
     * 开始接受到媒体流
     */
    public void setSync() {
        //当白板全屏且企业配置画中画
        if (RoomSession.fullScreen && RoomControler.isFullScreenVideo()) {
            //显示全屏同步画中画
            rel_fullscreen_mp4videoitem.setVisibility(View.VISIBLE);
            fullscreen_sf_video.setZOrderMediaOverlay(true);
            fullscreen_sf_video.setVisibility(View.VISIBLE);
            //当一对一房间用户不为学生时画中画显示的都是学生
            if (RoomInfo.getInstance().getRoomType() == 0 && TKRoomManager.getInstance().getMySelf().role != 2) {
                RoomUser user = null;
                for (int i = 0; i < RoomSession.playingList.size(); i++) {
                    if (2 == RoomSession.playingList.get(i).role) {
                        user = RoomSession.playingList.get(i);
                        break;
                    }
                }

                FullScreenControlUtil.changeFullSreenSate(rel_fullscreen_mp4videoitem, user, true);

            } else {
                for (int i = 0; i < RoomSession.playingList.size(); i++) {
                    if (RoomSession.playingList.get(i).role == 0) {
                        TKRoomManager.getInstance().playVideo(RoomSession.playingList.get(i).peerId, fullscreen_sf_video,
                                RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onStop() {
        if (suf_mp4 != null && frameListener != null) {
            suf_mp4.removeFrameListener(frameListener);
            frameListener = null;
        }
        re_laoding.setVisibility(View.GONE);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        //恢复初始视频框
        MoveFullBoardUtil.getInstance().clean();
        RoomSession.jsVideoWBTempMsg = new JSONArray();

        if (suf_mp4 != null) {
            suf_mp4.release();
            suf_mp4 = null;
        }

        if (videoPaint != null) {
            videoPaint = null;
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

    }

    public void setFullscreen_video_param(RelativeLayout.LayoutParams fullscreen_video_param) {
        this.fullscreen_video_param = fullscreen_video_param;

        //白板全屏右下角界面大小
        if (fullscreen_sf_video != null && fullscreen_bg_video_back != null
                && fullscreen_img_video_back != null) {
            fullscreen_sf_video.setLayoutParams(fullscreen_video_param);
            fullscreen_bg_video_back.setLayoutParams(fullscreen_video_param);
            fullscreen_img_video_back.setLayoutParams(fullscreen_video_param);
        }
    }
}
