package com.eduhdsdk.room;

import android.content.Context;
import android.content.SharedPreferences;

import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.utils.NotificationCenter;
import com.eduhdsdk.entity.ChatData;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.entity.RoomCacheMessage;
import com.eduhdsdk.tools.SoundPlayUtils;
import com.eduhdsdk.tools.Tools;
import com.talkcloud.media.entity.RtcStats;
import com.talkcloud.media.entity.TKAudioFrame;
import com.talkcloud.media.entity.TKVideoFrame;
import com.talkcloud.media.entity.TK_AUDIO_STATE;
import com.talkcloud.media.entity.TK_VIDEO_STATE;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKMediaFrameObserver;
import com.talkcloud.room.TKPlayBackManager;
import com.talkcloud.room.TKPlayBackManagerObserver;
import com.talkcloud.room.TKRoomManager;
import com.talkcloud.room.TKRoomManagerObserver;
import com.talkcloud.room.TkAudioStatsReport;
import com.talkcloud.room.TkVideoStatsReport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * Created by Administrator on 2017/12/12.
 */
public class RoomSession implements TKRoomManagerObserver, TKMediaFrameObserver, TKPlayBackManagerObserver {

    public static final int onRoomJoin = 1;
    public static final int onRoomLeave = 2;
    public static final int onError = 3;
    public static final int onWarning = 4;
    public static final int onUserJoin = 5;
    public static final int onUserLeft = 6;
    public static final int onUserPropertyChanged = 7;
    public static final int onUserVideoStatus = 8;
    public static final int onKickedout = 9;
    public static final int onMessageReceived = 10;
    public static final int onRemotePubMsg = 11;
    public static final int onRemoteDelMsg = 12;
    public static final int onUpdateAttributeStream = 13;
    public static final int onPlayBackClearAll = 14;
    public static final int onPlayBackUpdateTime = 15;
    public static final int onPlayBackDuration = 16;
    public static final int onPlayBackEnd = 17;
    public static final int onShareMediaState = 18;
    public static final int onShareScreenState = 19;
    public static final int onShareFileState = 20;
    public static final int onAudioVolume = 21;
    public static final int onRoomUser = 22;
    public static final int onRoomUserNumber = 23;
    public static final int onConnectionLost = 24;
    public static final int onFirstVideoFrame = 25;
    public static final int onUserAudioStatus = 26;
    public static final int onAudioRoomSwitch = 27;
    public static final int onVideoStatsReport = 28;
    public static final int onInfo = 29;

    //是否上课  true 为上课   false 为没有/下课
    public static boolean isClassBegin = false;
    //mp4/mp3   ture 为播放状态   false为没有并发状态
    public static boolean isPublishMp4 = false;
    public static boolean isPublishMp3 = false;
    //进入房间成功为 ture
    public static boolean isInRoom = false;
    //大并发
    public static boolean _bigroom = false;
    //电影共享 ture 为播放状态   false为没有并发状态
    public static boolean isShareFile = false;
    //屏幕共享  ture 为播放状态   false为没有并发状态
    public static boolean isShareScreen = false;
    //群体可以发聊天消息  true 可以发    flase 为不可以发
    public static boolean _possibleSpeak = true;
    //课件全屏显示时刻  true 显示    flase 不显示
    public static boolean fullScreen = false;
    //老师在台上
    public static boolean teaPublish = false;
    //是否是回放
    public static boolean isPlayBack = false;
    //视频标记白板是否显示   true 显示    flase 不显示
    public static boolean isShowVideoWB = false;
    //教室是否是全音频授课  true 是全音频  false 不是全音频
    public static boolean isOnliyAudioRoom = false;
    //媒体处于暂停还是播放状态  ture 暂停 false播放
    public static boolean isPause = false;

    //聊天消息集合
    public static ArrayList<ChatData> chatList = new ArrayList<ChatData>();
    //花名册人员列表
    public static ArrayList<RoomUser> memberList = new ArrayList<RoomUser>();
    //未读消息
    public static List<ChatData> chatDataCache = new ArrayList<ChatData>();
    //白板数据
    public static JSONArray jsVideoWBTempMsg = new JSONArray();
    //上台人员列表
    public static List<RoomUser> playingList = Collections.synchronizedList(new ArrayList<RoomUser>());
    //上台用户暂存列表
    public static ArrayList<RoomUser> videoList = new ArrayList<RoomUser>();

    //用户列表PublishState大于0的用户
    public static ArrayList<RoomUser> publishState = new ArrayList<RoomUser>();

    static private RoomSession mInstance = null;
    private boolean isActivityStart = false;
    private Context context;

    //消息缓存
    private List<RoomCacheMessage> messageBuffer = Collections.synchronizedList(new ArrayList<RoomCacheMessage>());

    static public RoomSession getInstance() {
        synchronized (RoomSession.class) {
            if (mInstance == null) {
                mInstance = new RoomSession();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        context = null;
        mInstance = null;
    }

    public void init(Context context) {
        this.context = context;
    }


    public void addTempVideoWBRemoteMsg(boolean add, String id, String name, long ts, Object data, String fromID, String associatedMsgID, String associatedUserID) {
        if (add) {
            if (name.equals("VideoWhiteboard")) {
                isShowVideoWB = true;
            }
            JSONObject jsobj = new JSONObject();
            try {
                jsobj.put("id", id);
                jsobj.put("ts", ts);
                jsobj.put("data", data == null ? null : data.toString());
                jsobj.put("name", name);
                jsobj.put("fromID", fromID);
                if (!associatedMsgID.equals("")) {
                    jsobj.put("associatedMsgID", associatedMsgID);
                }
                if (!associatedUserID.equals("")) {
                    jsobj.put("associatedUserID", associatedUserID);
                }

                if (associatedMsgID.equals("VideoWhiteboard") || id.equals("VideoWhiteboard")) {
                    jsVideoWBTempMsg.put(jsobj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            isShowVideoWB = false;
        }
    }

    @Override
    public void onRoomJoined() {

        if (RoomControler.haveTimeQuitClassroomAfterClass()) {
            if (RoomOperation.timerAfterLeaved != null) {
                RoomOperation.timerAfterLeaved.cancel();
                RoomOperation.timerAfterLeaved = null;
            } else {
                RoomOperation.timerAfterLeaved = new Timer();
            }
            RoomOperation.getInstance().getSystemNowTime(context);
        }

        isInRoom = true;
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRoomJoin);
        } else {
            addMessageToBuffer(onRoomJoin);
        }
    }

    /***
     *    离开房间的回调
     */
    @Override
    public void onRoomLeaved() {
        if (RoomInfo.getInstance().getTrophyList() != null && RoomInfo.getInstance().getTrophyList().size() > 0) {
            SoundPlayUtils.releaseTrophy();
        } else {
            SoundPlayUtils.release();
        }
        WhiteBoradConfig.getsInstance().clear();
        resetRoomSession();
        RoomInfo.getInstance().cancellRoomInformation();
        RoomVariable.getInstance().resetRoomVariable();
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRoomLeave);
        } else {
            addMessageToBuffer(onRoomLeave);
        }
    }

    /***
     *  房间链接丢失
     */
    @Override
    public void onConnectionLost() {
        WhiteBoradConfig.getsInstance().roomConnectionLost();

        if (RoomInfo.getInstance().getTrophyList() != null && RoomInfo.getInstance().getTrophyList().size() > 0) {
            SoundPlayUtils.releaseTrophy();
        } else {
            SoundPlayUtils.release();
        }

        resetRoomSession();
        /*RoomVariable.getInstance().resetRoomVariable(); */

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onConnectionLost);
        } else {
            addMessageToBuffer(onConnectionLost);
        }
    }

    /***
     *   错误回调
     * @param errorCode   错误码
     *                    1501 摄像头丢失  1502 socket 5 次链接失败，应退出教室   1503 udp 链接异常(链接成功过)
     *                   1504 udp 链接异常（没有链接成功过，防火墙拦截）  1505 房间视频路数已达上限
     *                   进入房间的错误：3001 服务器过期  3002 公司被冻结  3003 教室被删除或过期
     *                  4007 教室不存在  4008 教室密码错误  4110 该教室需要密码，请输入密码
     * @param errMsg       错误信息
     */
    @Override
    public void onError(int errorCode, String errMsg) {
        if (errorCode == 3001 || errorCode == 3002 || errorCode == 3003 || errorCode == 4007 || errorCode == 4008
                || errorCode == 4110 || errorCode == 0 || errorCode == 4012 ||
                errorCode == 3 || errorCode == 1502 || errorCode == 11 || errorCode == 4113) {

            //获取房间信息
            if (errorCode == 0) {
                RoomInfo.getInstance().getRoomInformation();
            }

            RoomClient.getInstance().joinRoomcallBack(errorCode);

        } else if (errorCode == 10002) {

            WhiteBoradConfig.getsInstance().clear();
            resetRoomSession();
            RoomInfo.getInstance().cancellRoomInformation();
            RoomClient.getInstance().joinRoomcallBack(-1);

        }

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onError, errorCode, errMsg);
        } else {
            addMessageToBuffer(onError, errorCode, errMsg);
        }
    }

    /***
     *     警告回调
     * @param warning   1751 摄像头打开   1752 摄像头关闭
     */
    @Override
    public void onWarning(int warning) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onWarning, warning);
        } else {
            addMessageToBuffer(onWarning, warning);
        }
    }

    /***
     *    其他用户进入房间的回调
     * @param roomUser          进入的用户
     * @param inList            是否在我之前进入房间，true—之前，false—之后
     */
    @Override
    public void onUserJoined(RoomUser roomUser, boolean inList) {
        roomUser.nickName = StringEscapeUtils.unescapeHtml4(roomUser.nickName);
        ChatData ch = new ChatData();
        ch.setState(1);
        ch.setInOut(true);
        ch.setStystemMsg(true);
        ch.setUser(roomUser);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        ch.setTime(str);
        if (roomUser.role != 4) {
            chatList.add(ch);
        }

        if (roomUser != null && inList && roomUser.getPublishState() > 0) {
            if (!videoList.contains(roomUser)) {
//                if (RoomControler.isOnlyShowTeachersAndVideos()) {
//                    if (roomUser.role == 0 || TKRoomManager.getInstance().getMySelf().peerId.equals(roomUser.peerId)) {
//                        videoList.add(roomUser);
//                    }
//                }else {
                if (videoList.size() <= 25) {
                    videoList.add(roomUser);
                }
                // }
            }
        }

        if (inList && roomUser.role != 4) {
            if (roomUser.role == Constant.USERROLE_TEACHER && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER ||
                    (RoomInfo.getInstance().getRoomType() == 0 && roomUser.role == TKRoomManager.getInstance().getMySelf().role)) {
                TKRoomManager.getInstance().evictUser(roomUser.peerId);
            }
            if (roomUser.properties.containsKey("isInBackGround") && TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_STUDENT) {
                if (roomUser == null) {
                    return;
                }
                boolean isinback = Tools.isTure(roomUser.properties.get("isInBackGround"));
                ChatData ch2 = new ChatData();
                ch2.setState(2);
                ch2.setHold(isinback);
                ch2.setStystemMsg(true);
                ch2.setUser(roomUser);
                SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
                Date curDate2 = new Date(System.currentTimeMillis());//获取当前时间
                String str2 = formatter2.format(curDate2);
                ch2.setTime(str2);
                if (roomUser.role != 4) {
                    chatList.add(ch2);
                }
            }
        }
        getMemberList();

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUserJoin, roomUser, inList);
        } else {
            addMessageToBuffer(onUserJoin, roomUser, inList);
        }
    }

    //花名册列表
    public void getMemberList() {
        memberList.clear();
        for (RoomUser u : TKRoomManager.getInstance().getUsers().values()) {
            if (!u.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && (u.role == 2 || u.role == 1)) {
                if (u.role == 1) {
                    memberList.add(0, u);
                } else {
                    memberList.add(u);
                }
            }
        }
    }

    //获取上台人员列表
    public void getPlatformMemberList() {
        playingList.clear();
        for (RoomUser roomUser : videoList) {
            if (roomUser != null && roomUser.getPublishState() > 0 &&
                    playingList.size() < RoomInfo.getInstance().getMaxVideo()) {
                if (roomUser.role == 0) {
                    playingList.add(0, roomUser);
                } else {
                    playingList.add(roomUser);
                }
            }
        }
    }

    //获取用户列表PublishState大于0的用户
    public void getUserPublishStateList() {
        publishState.clear();
        teaPublish = false;
        for (RoomUser roomUser : TKRoomManager.getInstance().getUsers().values()) {
            if (roomUser != null && roomUser.getPublishState() > 0) {
                if (roomUser.role == 0) {
                    publishState.add(0, roomUser);
                    teaPublish = true;
                } else {
                    publishState.add(roomUser);
                }
            }
        }
    }

    /***
     *     其他用户离开房间
     * @param roomUser      离开的用户
     */
    @Override
    public void onUserLeft(RoomUser roomUser) {
        ChatData ch = new ChatData();
        ch.setState(1);
        ch.setInOut(false);
        ch.setStystemMsg(true);
        ch.setUser(roomUser);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        ch.setTime(str);
        if (roomUser != null && roomUser.role != 4) {
            chatList.add(ch);
        }

        if (videoList != null && roomUser != null && videoList.size() > 0 && videoList.contains(roomUser)) {
            videoList.remove(roomUser);
        }

        getMemberList();
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUserLeft, roomUser);
        } else {
            addMessageToBuffer(onUserLeft, roomUser);
        }
    }

    /***
     *   用户属性改变
     * @param roomUser    改变属性的用户
     * @param map          改变的属性集合
     * @param fromId      改变该用户属性的用户的用户 ID
     */
    @Override
    public void onUserPropertyChanged(RoomUser roomUser, Map<String, Object> map, String fromId) {
        if (context == null)
            return;
        if (map.containsKey("isInBackGround")) {
            boolean isinback = Tools.isTure(map.get("isInBackGround"));
            if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_STUDENT) {
                ChatData ch = new ChatData();
                ch.setState(2);
                ch.setHold(isinback);
                ch.setStystemMsg(true);
                ch.setUser(roomUser);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                ch.setTime(str);
                if (roomUser != null && roomUser.role != 4) {
                    chatList.add(ch);
                }
            }
        }

        if (roomUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("servername") && !fromId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
            String servername = String.valueOf(map.get("servername"));
            SharedPreferences sp = context.getSharedPreferences("classroom", context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("servername", servername);
            editor.commit();
            TKRoomManager.getInstance().switchService(servername);
        }

        if (videoList != null && roomUser != null) {
            if (roomUser.getPublishState() > 0) {
                if (!videoList.contains(roomUser)) {
//                    if (RoomControler.isOnlyShowTeachersAndVideos()) {
//                        if (roomUser.role == 0 || TKRoomManager.getInstance().getMySelf().peerId.equals(roomUser.peerId)) {
//                            videoList.add(roomUser);
//                        }
//                    }else {
                    if (videoList.size() <= 25) {
                        videoList.add(roomUser);
                    }
                    // }
                }
            } else {
                if (videoList.contains(roomUser)) {
                    videoList.remove(roomUser);
                }
            }
        }

        getMemberList();
        getPlatformMemberList();

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUserPropertyChanged, roomUser, map, fromId);
        } else {
            addMessageToBuffer(onUserPropertyChanged, roomUser, map, fromId);
        }
    }

    /***
     *    用户视频状态改变  (多流时回调)
     * @param userId           用户 id
     * @param state           视频状态 0 取消发布 1 发布
     * @param deviceId       设备 ID（多流时使用）
     */
    @Override
    public void onUserVideoStatus(String userId, int state, String deviceId) {

    }

    /***
     *   用户音频状态改变
     * @param userId     用户 id
     * @param state      音频状态 0 取消发布 1 发布
     */
    @Override
    public void onUserAudioStatus(String userId, int state) {
        RoomUser roomUser = TKRoomManager.getInstance().getUser(userId);
        if (roomUser != null) {
            roomUser.properties.remove("passivityPublish");
        }

        if (videoList != null && roomUser != null) {
            if (roomUser.getPublishState() > 0) {
                if (!videoList.contains(roomUser)) {
//                    if (RoomControler.isOnlyShowTeachersAndVideos()) {
//                        if (roomUser.role == 0 || TKRoomManager.getInstance().getMySelf().peerId.equals(roomUser.peerId)) {
//                            videoList.add(roomUser);
//                        }
//                    }else {
                    if (videoList.size() <= 25) {
                        videoList.add(roomUser);
                    }
                    //}
                }
            } else {
                if (videoList.contains(roomUser)) {
                    videoList.remove(roomUser);
                }
            }
        }

        getMemberList();
        getPlatformMemberList();

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUserAudioStatus, userId, state);
        } else {
            addMessageToBuffer(onUserAudioStatus, userId, state);
        }
    }

    /***
     *  用户视频状态改变
     * @param userId   用户 id
     * @param state    视频状态 0 取消发布 1 发布
     */
    @Override
    public void onUserVideoStatus(String userId, int state) {
        RoomUser roomUser = TKRoomManager.getInstance().getUser(userId);
        if (roomUser != null) {
            roomUser.properties.remove("passivityPublish");
        }

        if (videoList != null && roomUser != null) {
            if (roomUser.getPublishState() > 0) {
                if (!videoList.contains(roomUser)) {
//                    if (RoomControler.isOnlyShowTeachersAndVideos()) {
//                        if (roomUser.role == 0 || TKRoomManager.getInstance().getMySelf().peerId.equals(roomUser.peerId)) {
//                            videoList.add(roomUser);
//                        }
//                    }else {
                    if (videoList.size() <= 25) {
                        videoList.add(roomUser);
                    }
                    //  }
                }
            } else {
                if (videoList.contains(roomUser)) {
                    videoList.remove(roomUser);
                }
            }
        }

        getMemberList();
        getPlatformMemberList();

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUserVideoStatus, userId, state);
        } else {
            addMessageToBuffer(onUserVideoStatus, userId, state);
        }
    }


    /***
     *   自己被请出房间的回调
     * @param reason   原因
     */
    @Override
    public void onKickedout(int reason) {
        if (reason == 1) {
            SharedPreferences preferences = context.getSharedPreferences("KickOutPersonInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor numberEditor = preferences.edit();
            numberEditor.putString("RoomNumber", RoomInfo.getInstance().getSerial());
            numberEditor.putLong("Time", System.currentTimeMillis());
            numberEditor.commit();
        }

        RoomUser roomUser = TKRoomManager.getInstance().getMySelf();
        if (roomUser != null && roomUser.role == 0) {
            if (roomUser.properties.containsKey("isInBackGround")) {
                boolean isinback = Tools.isTure(roomUser.properties.get("isInBackGround"));
                if (isinback) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "isInBackGround", false);
                }
            }
        }

        TKRoomManager.getInstance().leaveRoom();
        RoomClient.getInstance().kickout(reason == 1 ? RoomVariable.Kickout_ChairmanKickout : RoomVariable.Kickout_Repeat);

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onKickedout, reason);
        } else {
            addMessageToBuffer(onKickedout, reason);
        }
    }

    /***
     *    收到文本消息
     * @param roomUser     发送文本消息的用户
     * @param jsonObject   文本消息，json 格式
     * @param ts            消息发送时间戳
     */
    @Override
    public void onMessageReceived(RoomUser roomUser, JSONObject jsonObject, long ts) {
        ChatData ch = new ChatData();
        ch.setUser(roomUser);
        ch.setStystemMsg(false);
        ch.setMsgTime(System.currentTimeMillis());
        int type = jsonObject.optInt("type");
        if (type == 0) {
            chatDataCache.add(ch);
            String msg = jsonObject.optString("msg");
            String image = jsonObject.optString("msgtype");
            if ("onlyimg".equals(image)) {
                String[] files = msg.split("\\.");
                if (files.length == 2) {
                    String imgName = files[0] + "-1" + "." + files[1];
                    ch.setImage("http://" + WhiteBoradConfig.getsInstance().getFileServierUrl() + ":"
                            + WhiteBoradConfig.getsInstance().getFileServierPort() + imgName);
                }
            } else {
                ch.setMessage(msg);
                ch.setTrans(false);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                Date curDate = null;
                if (StringUtils.isEmpty(RoomVariable.path)) {
                    curDate = new Date(System.currentTimeMillis());//获取当前时间
                } else {
                    curDate = new Date(ts);
                }
                String str = formatter.format(curDate);
                ch.setTime(str);
            }
            chatList.add(ch);
        }

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onMessageReceived, roomUser, jsonObject, ts);
        } else {
            addMessageToBuffer(onMessageReceived, roomUser, jsonObject, ts);
        }
    }

    /***
     * 收到信令消息回调
     *
     * @param id          消息 id
     * @param name        消息名字
     * @param ts          消息发送时间戳
     * @param data        消息携带数据
     * @param inList      消息是否在消息列表中
     * @param fromID       发送者 id
     * @param associatedMsgID  消息关联消息的 id （该消息删除时会跟随删除)
     * @param associatedUserID  消息关联用户的 id （该用户退出时会跟随删除）
     */
    @Override
    public void onRemotePubMsg(String id, String name, long ts, Object data, boolean inList,
                               String fromID, String associatedMsgID, String associatedUserID, JSONObject jsonObject) {
        RoomSession.getInstance().addTempVideoWBRemoteMsg(true, id, name, ts, data, fromID, associatedMsgID, associatedUserID);
        if (name.equals("ClassBegin")) {
            if (isClassBegin) {
                return;
            }
            isClassBegin = true;

            RoomOperation.classStartTime = ts;
            TKRoomManager.getInstance().pubMsg("UpdateTime", "UpdateTime",
                    TKRoomManager.getInstance().getMySelf().peerId, new JSONObject(), false,
                    null, null);

            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER && !inList) {
                TKRoomManager.getInstance().unPlayMedia(TKRoomManager.getInstance().getMySelf().peerId);
            }

            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT && RoomControler.isAutoHasDraw() &&
                    RoomInfo.getInstance().getRoomType() == 0) {
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                        "__all", "candraw", true);
            }
            RoomClient.getInstance().onClassBegin();

        } else if (name.equals("BigRoom")) {
            _bigroom = true;
        } else if (name.equals("UpdateTime")) {
            if (RoomSession.isClassBegin) {
                RoomOperation.serviceTime = ts;
                RoomOperation.localTime = RoomOperation.serviceTime - RoomOperation.classStartTime;
            } else {
                RoomOperation.getInstance().isAutoClassBegin();
            }
        } else if (name.equals("EveryoneBanChat")) {
            _possibleSpeak = false;
        } else if (name.equals("FullScreen")) {
            fullScreen = true;
        } else if (name.equals("OnlyAudioRoom")) {
            isOnliyAudioRoom = true;
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRemotePubMsg, id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID);
        } else {
            addMessageToBuffer(onRemotePubMsg, id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID);
        }
    }

    /***
     * 收到信令消息删除回调
     *
     * @param id               消息 id
     * @param name            消息名字
     * @param ts              消息发送时间戳
     * @param data            消息携带数据
     * @param inList          消息是否在消息列表中
     * @param fromID           发送者 id
     * @param associatedMsgID  消息关联消息的 id （该消息删除时会跟随删除)
     * @param associatedUserID   消息关联用户的 id （该用户退出时会跟随删除）
     */
    @Override
    public void onRemoteDelMsg(String id, String name, long ts, Object data, boolean inList,
                               String fromID, String associatedMsgID, String associatedUserID, JSONObject jsonObject) {
        RoomSession.getInstance().addTempVideoWBRemoteMsg(false, id, name, ts, data, fromID, associatedMsgID, associatedUserID);
        if (name.equals("ClassBegin")) {
            isClassBegin = false;
            if (!RoomControler.isNotLeaveAfterClass() && TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all",
                        "publishState", 0);
                TKRoomManager.getInstance().delMsg("__AllAll", "__AllAll", "__none",
                        new HashMap<String, Object>());
            }

            RoomOperation.localTime = 0;
            if (RoomOperation.timerAddTime != null) {
                if (!RoomControler.haveTimeQuitClassroomAfterClass()) {
                    RoomOperation.timerAddTime.cancel();
                    RoomOperation.timerAddTime = null;
                }
            }

            RoomClient.getInstance().onClassDismiss();
            TKRoomManager.getInstance().unPlayMedia(fromID);

        } else if (name.equals("EveryoneBanChat")) {
            _possibleSpeak = true;
        } else if (name.equals("FullScreen")) {
            fullScreen = false;
        } else if (name.equals("OnlyAudioRoom")) {
            isOnliyAudioRoom = false;
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRemoteDelMsg, id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID);
        } else {
            addMessageToBuffer(onRemoteDelMsg, id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID);
        }
    }

    /***
     * 收到信令消息回调
     *
     * @param id          消息 id
     * @param name        消息名字
     * @param ts          消息发送时间戳
     * @param data        消息携带数据
     * @param inList      消息是否在消息列表中
     * @param fromID       发送者 id
     * @param associatedMsgID  消息关联消息的 id （该消息删除时会跟随删除)
     * @param associatedUserID  消息关联用户的 id （该用户退出时会跟随删除）
     */
    @Override
    public void onRemotePubMsg(String id, String name, long ts, Object data, boolean inList,
                               String fromID, String associatedMsgID, String associatedUserID) {

    }

    /***
     * 收到信令消息删除回调
     *
     * @param id               消息 id
     * @param name            消息名字
     * @param ts              消息发送时间戳
     * @param data            消息携带数据
     * @param inList          消息是否在消息列表中
     * @param fromID           发送者 id
     * @param associatedMsgID  消息关联消息的 id （该消息删除时会跟随删除)
     * @param associatedUserID   消息关联用户的 id （该用户退出时会跟随删除）
     */
    @Override
    public void onRemoteDelMsg(String id, String name, long ts, Object data, boolean inList,
                               String fromID, String associatedMsgID, String associatedUserID) {

    }

    /***
     *    网络媒体播放进度，状态回调
     * @param peerid    用户 Id
     * @param pos       播放进度
     * @param isPlay    是否在播放
     * @param hashMap   流自定义扩展属性
     */
    @Override
    public void onUpdateAttributeStream(String peerid, long pos, boolean isPlay, HashMap<
            String, Object> hashMap) {
        this.isPause = isPlay;
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUpdateAttributeStream, peerid, pos, isPlay, hashMap);
        } else {
            addMessageToBuffer(onUpdateAttributeStream, peerid, pos, isPlay, hashMap);
        }
    }

    /***
     *    回放清除所有的数据的回调
     */
    @Override
    public void onPlayBackClearAll() {
       /* if (memberList != null) {
            memberList.clear();
        }
        if (playingList != null) {
            playingList.clear();
        }
        if (videoList != null) {
            videoList.clear();
        }
        if (publishState != null) {
            publishState.clear();
        }
        if (chatList != null) {
            chatList.clear();
        }*/

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onPlayBackClearAll);
        } else {
            addMessageToBuffer(onPlayBackClearAll);
        }
    }

    /***
     *     回放播放进度回调
     * @param recordStartTime   当前时间
     */
    @Override
    public void onPlayBackUpdateTime(long recordStartTime) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onPlayBackUpdateTime, recordStartTime);
        } else {
            addMessageToBuffer(onPlayBackUpdateTime, recordStartTime);
        }
    }

    /***
     *    回放起止时间回调
     * @param startTime     开始时间
     * @param endTime       结束时间
     */
    @Override
    public void onPlayBackDuration(long startTime, long endTime) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onPlayBackDuration, startTime, endTime);
        } else {
            addMessageToBuffer(onPlayBackDuration, startTime, endTime);
        }
    }

    /***
     *   回放结束回调
     */
    @Override
    public void onPlayBackEnd() {
        TKPlayBackManager.getInstance().pausePlayback();
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onPlayBackEnd);
        } else {
            addMessageToBuffer(onPlayBackEnd);
        }
    }

    /***
     *   有网络媒体文件共享的回调    mp4/mp3
     * @param peerId   共享者用户 id
     * @param state    媒体共享状态 0 停止 1 开始
     * @param attrs    自定义数据
     */
    @Override
    public void onShareMediaState(String peerId, int state, Map<String, Object> attrs) {
        if (state == 0) {
            if (attrs.containsKey("video")) {
                if ((boolean) attrs.get("video")) {
                    isPublishMp4 = false;
                } else {
                    isPublishMp3 = false;
                }
            }
            TKRoomManager.getInstance().delMsg("VideoWhiteboard",
                    "VideoWhiteboard", "__all", null);
        } else if (state == 1) {
            if (attrs.containsKey("video")) {
                if ((boolean) attrs.get("video")) {
                    isPublishMp4 = true;
                } else {
                    isPublishMp3 = true;
                }
            }
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onShareMediaState, peerId, state, attrs);
        } else {
            addMessageToBuffer(onShareMediaState, peerId, state, attrs);
        }
    }

    /***
     *   有屏幕共享的回调
     * @param peerId     共享者用户 id
     * @param state      媒体共享状态 0 停止 1 开始
     */
    @Override
    public void onShareScreenState(String peerId, int state) {
        if (state == 0) {
            isShareScreen = false;
        } else if (state == 1) {
            isShareScreen = true;
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onShareScreenState, peerId, state);
        } else {
            addMessageToBuffer(onShareScreenState, peerId, state);
        }
    }

    /***
     *  有文件媒体共享的回调
     * @param peerId      共享者用户 id
     * @param state       媒体共享状态 0 停止 1 开始
     */
    @Override
    public void onShareFileState(String peerId, int state) {
        if (state == 0) {
            isShareFile = false;
        } else if (state == 1) {
            isShareFile = true;
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onShareFileState, peerId, state);
        } else {
            addMessageToBuffer(onShareFileState, peerId, state);
        }
    }

    /***
     *  音量回调
     * @param peerId   用户 id
     * @param volume   音量
     */
    @Override
    public void onAudioVolume(String peerId, int volume) {
        NotificationCenter.getInstance().postNotificationName(onAudioVolume, peerId, volume);
    }

    /***
     *    获取回放房间信息回掉
     * @param code         响应码   0  成功   非0 不成功
     * @param response     响应数据    错误日志
     */
    @Override
    public void onPlayBackRoomJson(int code, String response) {
        isPlayBack = true;
        if (code == 0) {
            RoomInfo.getInstance().getRoomInformation();
        }
        RoomClient.getInstance().onPlayBackRoomJson(code, response);
    }

    /***
     *    视频监控数据
     * @param peerId                 用户 id
     * @param tkVideoStatsReport    网络状态信息
     */
    @Override
    public void onVideoStatsReport(String peerId, TkVideoStatsReport tkVideoStatsReport) {
        NotificationCenter.getInstance().postNotificationName(onVideoStatsReport, peerId, tkVideoStatsReport);
    }

    /***
     *   音频监控数据
     * @param peerId               用户 id
     * @param tkAudioStatsReport  网络状态信息
     */
    @Override
    public void onAudioStatsReport(String peerId, TkAudioStatsReport tkAudioStatsReport) {

    }

    /***
     *  获取大并发用户列表回调
     * @param code         获取是否成功 0-成功
     * @param arrayList    用户列表（成功）
     */
    @Override
    public void onGetRoomUsersBack(int code, ArrayList<RoomUser> arrayList) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRoomUser, code, arrayList);
        } else {
            addMessageToBuffer(onRoomUser, code, arrayList);
        }
    }

    /***
     *   获取大并发教室中人数
     * @param code    获取是否成功 0-成功
     * @param number   人数
     */
    @Override
    public void onGetRoomUserNumBack(int code, int number) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRoomUserNumber, code, number);
        } else {
            addMessageToBuffer(onRoomUserNumber, code, number);
        }
    }

    /***
     *    切换纯音频教室回调
     * @param fromId     执行该命令的用户 ID
     * @param isSwitch   true-切换， false-不切换
     */
    @Override
    public void onAudioRoomSwitch(String fromId, boolean isSwitch) {

    }

    /***
     *    视频数据首帧回调
     * @param peerId      peerID
     * @param width       视频类型
     * @param height      视频宽
     * @param mediaType   视频高
     */
    @Override
    public void onFirstVideoFrame(String peerId, int mediaType, int width, int height) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onFirstVideoFrame, peerId, width, height, mediaType);
        } else {
            addMessageToBuffer(onFirstVideoFrame, peerId, width, height, mediaType);
        }
    }

    /**
     * 视频数据首帧回掉（多流）
     *
     * @param peerID    用户id
     * @param mediaType 视频类型
     * @param width     宽
     * @param height    高
     * @param cameraId  设备id
     */
    @Override
    public void onFirstVideoFrame(String peerID, int mediaType, int width, int height, String
            cameraId) {
    }

    /***
     *   切换纯音频教室回调
     * @param peerID        用户 Id
     * @param mediaType     视频类型
     *     TK_MEDIA_CAMERA = 0; TK_MEDIA_MIC = 11; TK_MEDIA_SPEAKER = 12;
     *     TK_MEDIA_FILE = 101;  TK_MEDIA_SCREEN = 102; TK_MEDIA_MEDIA = 103;
     */
    @Override
    public void onFirstAudioFrame(String peerID, int mediaType) {

    }

    /***
     *   房间外网络检测回掉
     * @param networkQuality   网络等级
     * @param delay             延迟时间
     */
    @Override
    public void onNetworkQuality(int networkQuality, long delay) {

    }

    /***
     *   获取房间信息
     *   onInfo: 1506||enter room finish
     *   在1506 初始化界面操作
     * @param infoCode  信息代号
     * @param message   描述信息
     */
    @Override
    public void onInfo(int infoCode, String message) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onInfo, infoCode, message);
        } else {
            addMessageToBuffer(onInfo, infoCode, message);
        }
    }

    @Override
    public void onVideoDeviceStateChanged(String s, int i) {

    }

    @Override
    public void onRtcStatsReport(RtcStats rtcStats) {

    }

    @Override
    public void onVideoStateChange(String s, String s1, TK_VIDEO_STATE tk_video_state) {

    }

    @Override
    public void onAudioStateChange(String s, TK_AUDIO_STATE tk_audio_state) {

    }

    private void addMessageToBuffer(int key, Object... args) {
        synchronized (RoomSession.class) {
            if (messageBuffer != null) {
                RoomCacheMessage roomCacheMessage = new RoomCacheMessage();
                roomCacheMessage.setKey(key);
                roomCacheMessage.setObjects(args);
                messageBuffer.add(roomCacheMessage);
            }
        }
    }

    public void onStart() {
        isActivityStart = true;
        if (messageBuffer != null && messageBuffer.size() > 0) {
            synchronized (RoomSession.class) {
                for (int x = 0; x < messageBuffer.size(); x++) {
                    NotificationCenter.getInstance().postNotificationName(messageBuffer.get(x).getKey(),
                            messageBuffer.get(x).getObjects());
                }
                messageBuffer.clear();
            }
        }
    }

    public void onStop() {
        isActivityStart = false;
        messageBuffer.clear();
        RoomOperation.getInstance().releaseTimer();
        _possibleSpeak = true;
        isPlayBack = false;
    }

    /***
     *   重置RoomSession状态
     */
    public void resetRoomSession() {
        isOnliyAudioRoom = false;
        isClassBegin = false;
        isPublishMp3 = false;
        isPublishMp4 = false;
        isShareFile = false;
        isShareScreen = false;
        isPlayBack = false;
        isInRoom = false;
        _bigroom = false;
        teaPublish = false;
        if (playingList != null) {
            playingList.clear();
        }
        if (videoList != null) {
            videoList.clear();
        }
        if (publishState != null) {
            publishState.clear();
        }
        if (chatList != null) {
            chatList.clear();
        }
        if (memberList != null) {
            memberList.clear();
        }
    }

    /***
     *
     * @param tkAudioFrame  音频远端原始数据
     * @param uid            用户 id
     * @param type           类型
     * @return
     */
    @Override
    public boolean onRenderAudioFrame(TKAudioFrame tkAudioFrame, String uid, int type) {
        return false;
    }

    /***
     *
     * @param tkVideoFrame   视频本地原始数据
     * @param id              用户 id
     * @return
     */
    @Override
    public boolean onCaptureVideoFrame(TKVideoFrame tkVideoFrame, String id) {
        return false;
    }

    /***
     *
     * @param tkVideoFrame  视频远端原始数据
     * @param uid            用户 id
     * @param type           类型
     * @return 设备 ID
     */
    @Override
    public boolean onRenderVideoFrame(TKVideoFrame tkVideoFrame, String uid, int type) {
        return false;
    }

    /***
     *
     * @param tkVideoFrame   视频远端原始数据
     * @param uid             用户 id
     * @param type            类型
     * @param deviceId        设备 ID
     * @return
     */
    @Override
    public boolean onRenderVideoFrame(TKVideoFrame tkVideoFrame, String uid, int type, String deviceId) {
        return false;
    }

    /***
     *
     * @param tkAudioFrame  音频本地原始数据
     * @param uid            用户 id
     * @param type           类型
     * @return
     */
    @Override
    public boolean onCaptureAudioFrame(TKAudioFrame tkAudioFrame, String uid, int type) {
        return false;
    }
}
