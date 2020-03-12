package com.eduhdsdk.ui;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.classroomsdk.common.JSWhitePadInterface;
import com.classroomsdk.manage.ProLoadingDoc;
import com.classroomsdk.manage.SharePadMgr;
import com.classroomsdk.manage.WBSession;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.manage.WhiteBoradManager;
import com.classroomsdk.utils.NotificationCenter;
import com.classroomsdk.utils.PPTRemarkUtil;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.message.BroadcastReceiverLanguage;
import com.eduhdsdk.message.BroadcastReceiverMgr;
import com.eduhdsdk.room.RoomCheck;
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
import com.eduhdsdk.tools.FullScreenTools;
import com.eduhdsdk.viewutils.CoursePopupWindowUtils;
import com.eduhdsdk.viewutils.InputWindowPop;
import com.eduhdsdk.viewutils.MemberListPopupWindowUtils;
import com.eduhdsdk.tools.SoundPlayUtils;
import com.eduhdsdk.tools.Translate;
import com.eduhdsdk.viewutils.MoveFullBoardUtil;
import com.eduhdsdk.viewutils.OneToManyFreeLayoutUtil;
import com.eduhdsdk.viewutils.PlayBackSeekPopupWindow;
import com.eduhdsdk.viewutils.UploadPhotoPopupWindowUtils;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;
import com.talkcloud.room.TkVideoStatsReport;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/15/015.
 */

public class TKBaseActivity extends FragmentActivity implements NotificationCenter.NotificationCenterDelegate {

    public int mSelectImageType;
    /**
     * 聊天时选择图片上传
     */
    public static final int CHAT_SELECT_IMAGE = 101;
    /**
     * 课件选择图片上传
     */
    public static final int KEJIAN_SELECT_IMAGE = 102;

    public AudioManager audioManager;
    public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
    public BroadcastReceiverMgr mBroadcastReceiver;
    public BroadcastReceiverLanguage broadcastReceiverLanguage;
    private PowerManager pm;
    private PowerManager.WakeLock mWakeLock;

    protected PopupWindow studentPopupWindow, teaPopupWindow;
    //课件
    protected CoursePopupWindowUtils coursePopupWindowUtils;
    //花名册
    protected MemberListPopupWindowUtils memberListPopupWindowUtils;
    //打开键盘弹窗
    protected InputWindowPop mInputWindowPop;

    //是否是刘海屏
    public Boolean isHaiping = false;
    //屏幕的宽度 高度
    int wid, hid;

    //刘海高度
    int heightStatusBar;
    //屏幕去除刘海高度之后的有效宽度
    int mScreenValueWidth;
    //聊天控制布局，mp3播放布局 距离左边间距
    int chatControlLeftMargin;
    //顶部工具栏的高
    protected int toolBarHeight;

    //回放进度条的PopupWindow
    PlayBackSeekPopupWindow mPlayBackSeekPopupWindow;

    //视频默认比率
    public int wid_ratio = 4;
    public int hid_ratio = 3;
    private Context mContext;

    public boolean vivo;

    int mLayoutState = 1;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // app在后台被杀死后，恢复时，savedInstanceState不为null
        if (savedInstanceState != null) {
            TKRoomManager.getInstance().leaveRoom();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        RoomSession.getInstance().init(this);
        mContext = this;
        //注册广播
        registerBroadcast();

        boolean huawei = FullScreenTools.hasNotchInScreen(this);
        boolean oppo = FullScreenTools.hasNotchInOppo(this);
        vivo = FullScreenTools.hasNotchInScreenAtVoio(this);

        if (huawei || oppo) {
            isHaiping = true;
        }
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 50, 0);
    }

    private void registerBroadcast() {
        broadcastReceiverLanguage = new BroadcastReceiverLanguage();
        mBroadcastReceiver = new BroadcastReceiverMgr();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(B_PHONE_STATE);
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        intentFilter.setPriority(Integer.MAX_VALUE);

        registerReceiver(broadcastReceiverLanguage, intentFilter);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRoomJoin);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRoomLeave);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onConnectionLost);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onError);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onWarning);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUserJoin);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUserLeft);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUserPropertyChanged);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onKickedout);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onMessageReceived);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRemotePubMsg);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRemoteDelMsg);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUpdateAttributeStream);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onPlayBackClearAll);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onPlayBackUpdateTime);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onPlayBackDuration);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onPlayBackEnd);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onShareMediaState);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onShareScreenState);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onShareFileState);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onAudioVolume);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onFirstVideoFrame);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUserAudioStatus);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUserVideoStatus);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRoomUser);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRoomUserNumber);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onVideoStatsReport);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onInfo);

        //巡课不发送声音
        if ((TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_LASSPATROL)
                && (TKRoomManager.getInstance().getMySelf().publishState == 1 ||
                TKRoomManager.getInstance().getMySelf().publishState == 3)) {
            TKRoomManager.getInstance().enableSendMyVoice(true);
        }

        if (RoomSession.isClassBegin) {
            TKRoomManager.getInstance().pubMsg("UpdateTime",
                    "UpdateTime", TKRoomManager.getInstance().getMySelf().peerId,
                    null, false, null, null);
        }

        RoomDeviceSet.closeSpeaker(getApplicationContext());
        TKRoomManager.getInstance().setMuteAllStream(false);

        //当只显示老师和学生自己时 不开启其他人的声音
        if (!RoomControler.isOnlyShowTeachersAndVideos()) {
            TKRoomManager.getInstance().enableOtherAudio(false);
        }
        RoomSession.getInstance().onStart();
        mWakeLock.acquire();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWakeLock.release();
        RoomSession.getInstance().onStop();
        NotificationCenter.getInstance().removeObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterBroadcast();
        RoomVariable.getInstance().resetRoomVariable();
        LayoutPopupWindow.getInstance().reset();

        ToolCaseMgr.getInstance().cleanData(false);
        ToolCaseMgr.getInstance().resetInstance();
        ToolsPopupWindow.getInstance().resetInstance();
        UploadPhotoPopupWindowUtils.getInstance().resetInstance();
        WhiteBoradManager.getInstance().resetInstance();
        WhiteBoradConfig.getsInstance().resetInstance();
        WBSession.getInstance().resetInstance();
        LayoutPopupWindow.getInstance().resetInstance();
        ProLoadingDoc.getInstance().resetInstance();
        SharePadMgr.getInstance().resetInstance();
        PPTRemarkUtil.getInstance().resetInstance();
        RoomCheck.getInstance().resetInstance();
        RoomInfo.getInstance().resetInstance();
        SetRoomInfor.getInstance().resetInstance();
        RoomOperation.getInstance().resetInstance();
        RoomVariable.getInstance().resetInstance();
        SoundPlayUtils.release();
        Translate.getInstance().resetInstance();
        MoveFullBoardUtil.getInstance().resetInstance();
        OneToManyFreeLayoutUtil.getInstance().resetInstance();
        JSWhitePadInterface.getInstance().resetInstance();
        NotificationCenter.getInstance().resetInstance();
    }

    //注销广播
    private void unregisterBroadcast() {
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        if (broadcastReceiverLanguage != null) {
            unregisterReceiver(broadcastReceiverLanguage);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 这里来获取容器的宽和高
        if (mScreenValueWidth == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            wid = dm.widthPixels;
            hid = dm.heightPixels;

            /*int a = dm.widthPixels / 7;
            int b = dm.widthPixels / 7 * 3 / 4;   // b = 3a/4
            int c = (int) (dm.widthPixels / 7 * 1.6);  // c = 1.6 * a
            int d = (int) (dm.widthPixels / 7 * 1.6 * 3 / 4); // d = 3c/4*/

            MoveFullBoardUtil.getInstance().SetWH(wid, hid);

            mScreenValueWidth = wid;

            /*if (isHaiping || vivo) {
                heightStatusBar = FullScreenTools.getStatusBarHeight(this);
                mScreenValueWidth = wid - heightStatusBar;
            }*/

            chatControlLeftMargin = wid / 70 * 3 / 8 + heightStatusBar;
            //顶部工具栏的高
            toolBarHeight = (int) (wid / 7 * 3 / 4 * 0.4);
        }
    }

    @Override
    public void didReceivedNotification(final int id, final Object... args) {

        if (args == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    // 成功进入房间的回调
                    case RoomSession.onRoomJoin:
                        SetRoomInfor.getInstance().setRoomInformation(mContext);
                        SetRoomInfor.getInstance().setRoomCustomCup(mContext);
                        onRoomJoin();
                        break;

                    //离开房间的回调
                    case RoomSession.onRoomLeave:
                        onRoomLeave();
                        break;

                    //网络连接丢失
                    case RoomSession.onConnectionLost:
                        onConnectionLost();
                        break;

                    //错误信息回调
                    case RoomSession.onError:
                        int errorCode = (int) args[0];
                        String errMsg = (String) args[1];
                        onError(errorCode, errMsg);
                        break;

                    //警告回调信息
                    case RoomSession.onWarning:
                        int onWarning = (int) args[0];
                        onWarning(onWarning);
                        break;

                    //其他用户进入房间的回调
                    case RoomSession.onUserJoin:
                        RoomUser user = (RoomUser) args[0];
                        boolean inList = (boolean) args[1];
                        onUserJoined(user, inList);
                        break;

                    //其他用户离开房间
                    case RoomSession.onUserLeft:
                        RoomUser roomUser = (RoomUser) args[0];
                        onUserLeft(roomUser);
                        break;

                    //用户属性改变
                    case RoomSession.onUserPropertyChanged:
                        RoomUser propertyUser = (RoomUser) args[0];
                        Map<String, Object> map = (Map<String, Object>) args[1];
                        String fromId = (String) args[2];
                        onUserPropertyChanged(propertyUser, map, fromId);
                        break;

                    //自己被请出房间的回调
                    case RoomSession.onKickedout:

                        break;

                    //收到文本消息
                    case RoomSession.onMessageReceived:
                        RoomUser chatUser = (RoomUser) args[0];
                        onMessageReceived(chatUser);
                        break;

                    //收到信令消息回调
                    case RoomSession.onRemotePubMsg:
                        String namePub = (String) args[1];
                        long pubMsgTS = (long) args[2];
                        Object dataPub = (Object) args[3];
                        boolean inListPub = (boolean) args[4];
                        onRemotePubMsg(namePub, pubMsgTS, dataPub, inListPub);
                        break;

                    //收到信令消息删除回调
                    case RoomSession.onRemoteDelMsg:
                        String nameDel = (String) args[1];
                        long delMsgTS = (long) args[2];
                        onRemoteDelMsg(nameDel, delMsgTS);
                        break;

                    //网络媒体播放进度，状态回调
                    case RoomSession.onUpdateAttributeStream:
                        String attributesPeerId = (String) args[0];
                        long streamPos = (long) args[1];
                        boolean isPlay = (Boolean) args[2];
                        Map<String, Object> onUpdateAttribute = (Map<String, Object>) args[3];
                        onUpdateAttributeStream(attributesPeerId, streamPos, isPlay, onUpdateAttribute);
                        break;

                    //回放播放进度回调
                    case RoomSession.onPlayBackUpdateTime:
                        long backTimePos = (long) args[0];
                        onPlayBackUpdateTime(backTimePos);
                        break;

                    //回放清除所有的数据的回调
                    case RoomSession.onPlayBackClearAll:
                        onPlayBackClearAll();
                        break;

                    //回放起止时间回调
                    case RoomSession.onPlayBackDuration:
                        long startTime = (long) args[0];
                        long endTime = (long) args[1];
                        onPlayBackDuration(startTime, endTime);
                        break;

                    //回放结束回调
                    case RoomSession.onPlayBackEnd:
                        onPlayBackEnd();
                        break;

                    //有网络媒体文件共享的回调(MP3/MP4)
                    case RoomSession.onShareMediaState: //MP4 MP3
                        String shareMediaPeerId = (String) args[0];
                        int shareMediaState = (int) args[1];
                        Map<String, Object> shareMediaAttrs = (Map<String, Object>) args[2];
                        onShareMediaState(shareMediaPeerId, shareMediaState, shareMediaAttrs);
                        break;

                    //有屏幕共享的回调
                    case RoomSession.onShareScreenState:
                        String peerIdScreen = (String) args[0];
                        int stateScreen = (int) args[1];
                        onShareScreenState(peerIdScreen, stateScreen);
                        break;

                    //有文件媒体共享的回调
                    case RoomSession.onShareFileState:
                        String peerIdShareFile = (String) args[0];
                        int stateShareFile = (int) args[1];
                        onShareFileState(peerIdShareFile, stateShareFile);
                        break;

                    //音量回调
                    case RoomSession.onAudioVolume:
                        String volumePeerId = (String) args[0];
                        int volume = (int) args[1];
                        onAudioVolume(volumePeerId, volume);
                        break;

                    //视频数据首帧回调
                    case RoomSession.onFirstVideoFrame:
                        String peerIdVideo = (String) args[0];
                        onFirstVideoFrame(peerIdVideo);
                        break;

                    //获取大并发用户列表回调
                    case RoomSession.onRoomUser:
                        int roomUserCode = (int) args[0];
                        ArrayList<RoomUser> userList = (ArrayList<RoomUser>) args[1];
                        onRoomUser(roomUserCode, userList);
                        break;

                    //获取大并发教室中人数
                    case RoomSession.onRoomUserNumber:
                        int roomUserNumberCode = (int) args[0];
                        int roomUserNumber = (int) args[1];
                        onRoomUserNumber(roomUserNumberCode, roomUserNumber);
                        break;

                    //用户音频状态改变
                    case RoomSession.onUserAudioStatus:
                        String userIdAudio = (String) args[0];
                        int statusAudio = (int) args[1];
                        onUserAudioStatus(userIdAudio, statusAudio);
                        break;

                    //切换纯音频教室回调
                    case RoomSession.onAudioRoomSwitch:
                        break;

                    //用户视频状态改变
                    case RoomSession.onUserVideoStatus:
                        String peerId = (String) args[0];
                        int state = (int) args[1];
                        onUserVideoStatus(peerId, state);
                        break;
                    //网络状态
                    case RoomSession.onVideoStatsReport:
                        String videoId = (String) args[0];
                        TkVideoStatsReport tkVideoStatsReport = (TkVideoStatsReport) args[1];
                        onVideoStatsReport(videoId, tkVideoStatsReport);
                        break;
                    //在进入教室之前的信令发送完毕后会调用
                    case RoomSession.onInfo:
                        int infocode = (int) args[0];
                        String message = (String) args[1];
                        onInfo(infocode, message);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void setPopupWindowVisibility(int visibility) {
        ToolCaseMgr.getInstance().setPopupWindowVisibility(visibility);
        setWbProtoVisibility(visibility);
    }

    protected void hidePopupWindow() {

        if (studentPopupWindow != null) {
            studentPopupWindow.dismiss();
        }

        if (teaPopupWindow != null) {
            teaPopupWindow.dismiss();
        }

        if (coursePopupWindowUtils != null) {
            coursePopupWindowUtils.dismissPopupWindow();
        }

        if (memberListPopupWindowUtils != null) {
            memberListPopupWindowUtils.dismissPopupWindow();
        }

        if (mInputWindowPop != null) {
            mInputWindowPop.dismissPopupWindow();
            mInputWindowPop.dismissInputPopupWindow();
        }

        ToolsPopupWindow.getInstance().dismiss();
    }

    public void setWbProtoVisibility(int visibility) {

    }

    /**
     * 在进入教室之前的信令发送完毕后会调用
     * 用于教室的初始化
     *
     * @param infoCode
     * @param message
     */
    public void onInfo(int infoCode, String message) {

    }

    public void onVideoStatsReport(String videoId, TkVideoStatsReport tkVideoStatsReport) {

    }

    public void onUserAudioStatus(String audioUserId, int audioState) {
    }

    public void onUserVideoStatus(String videoUserId, int videoState) {
    }

    public void onRoomUserNumber(int roomUserNumberCode, int roomUserNumber) {
    }

    public void onRoomUser(int code, ArrayList<RoomUser> listUsers) {
    }

    public void onFirstVideoFrame(String peerIdVideo) {
    }

    public void onAudioVolume(String volumePeerId, int volume) {
    }

    public void onShareFileState(String peerIdShareFile, int stateShareFile) {
    }

    public void onShareScreenState(String peerIdScreen, int stateScreen) {
    }

    public void onShareMediaState(String shareMediaPeerId, int shareMediaState, Map<String, Object> shareMediaAttrs) {
    }

    public void onPlayBackEnd() {
    }

    /***
     *    回放起止时间回调
     * @param startTime     开始时间
     * @param endTime       结束时间
     */
    public void onPlayBackDuration(long startTime, long endTime) {
        mPlayBackSeekPopupWindow.onPlayBackDuration(startTime, endTime);
    }

    public void onPlayBackClearAll() {
    }

    public void onPlayBackUpdateTime(long backTimePos) {
    }

    public void onUpdateAttributeStream(String attributesPeerId, long streamPos, boolean isPlay, Map<String, Object> dateAttributeAttrs) {
    }

    public void onRemoteDelMsg(String nameDel, long delMsgTS) {
    }

    public void onRemotePubMsg(String namePub, long pubMsgTS, Object dataPub, boolean inListPub) {
    }

    public void onMessageReceived(RoomUser chatUser) {
    }

    public void onUserPropertyChanged(RoomUser propertyUser, Map<String, Object> map, String fromId) {
    }

    public void onUserLeft(RoomUser roomUserLeft) {
    }

    public void onUserJoined(RoomUser roomUser, boolean inListUse) {
    }

    public void onWarning(int onWarning) {
    }

    public void onError(int errorCode, String errMsg) {
    }

    public void onConnectionLost() {
    }

    public void onRoomLeave() {
    }

    public void onRoomJoin() {

    }

}
