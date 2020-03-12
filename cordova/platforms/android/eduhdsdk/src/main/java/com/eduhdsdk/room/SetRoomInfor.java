package com.eduhdsdk.room;

import android.content.Context;
import android.text.TextUtils;

import com.classroomsdk.Config;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.manage.WhiteBoradManager;
import com.eduhdsdk.tools.SoundPlayUtils;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;
import com.talkcloud.room.TKVideoMirrorMode;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import org.tkwebrtc.MediaCodecVideoEncoder;

import java.util.Random;

/**
 * Created by Administrator on 2018/11/15/015.   设置房间信息
 */
public class SetRoomInfor {

    static private SetRoomInfor mInstance = null;

    static public SetRoomInfor getInstance() {
        synchronized (SetRoomInfor.class) {
            if (mInstance == null) {
                mInstance = new SetRoomInfor();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        mInstance = null;
    }

    public void setRoomInformation(Context context) {

        TKRoomManager.getInstance().setLocalVideoMirrorMode(TKVideoMirrorMode.TKVideoMirrorModeDisabled);

        TKRoomManager.getInstance().getMySelf().nickName =
                StringEscapeUtils.unescapeHtml4(TKRoomManager.getInstance().getMySelf().nickName);
        WhiteBoradManager.getInstance().setUserrole(TKRoomManager.getInstance().getMySelf().role);
        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            TKRoomManager.getInstance().pubMsg("UpdateTime", "UpdateTime",
                    "__all", new JSONObject(), false, null, null);
        }

        JSONObject jsonRoomInfo = TKRoomManager.getInstance().getRoomProperties();

        if (jsonRoomInfo != null) {
            //MediaCodecVideoEncoder.isVp8HwSupported()  表示是Vp8  ture  表示硬解     false  表示软解
            if (jsonRoomInfo.has("vcodec")) {
                int vcodec = TKRoomManager.getInstance().getRoomProperties().optInt("vcodec");
                switch (vcodec) {
                    case 0:
                        if (!MediaCodecVideoEncoder.isVp8HwSupported() && RoomInfo.getInstance().getWid_ratio() > 640) {
                            TKRoomManager.getInstance().setVideoProfile(640, 480);
                        }
                        break;
                    case 1:
                        if (!MediaCodecVideoEncoder.isVp9HwSupported() && RoomInfo.getInstance().getWid_ratio() > 640) {
                            TKRoomManager.getInstance().setVideoProfile(640, 480);
                        }
                        break;
                    case 2:
                        if (!MediaCodecVideoEncoder.isH264HwSupported() && RoomInfo.getInstance().getWid_ratio() > 640) {
                            TKRoomManager.getInstance().setVideoProfile(640, 480);
                        }
                        break;
                }
            }
        }

        RoomDeviceSet.getGiftNum(RoomInfo.getInstance().getSerial(), TKRoomManager.getInstance().getMySelf().peerId, context);
        WhiteBoradManager.getInstance().setUserrole(TKRoomManager.getInstance().getMySelf().role);

        /*TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                "__all", "isInBackGround", TKRoomManager.getInstance().isInBackGround());*/

        if (!RoomSession.isPublishMp4 && !RoomSession.isShareFile && !RoomSession.isShareScreen) {
            WhiteBoradConfig.getsInstance().hideWalkView(false);
        }

        /*if (RoomControler.isOnlyAudioClassroom()) {
            RoomSession.isOnliyAudioRoom = true;
        }*/
    }

    //上课后是否自动发布音视频
    public void publishVideoAfterClass() {
        if (!RoomSession.isPlayBack) {
            RoomSession.getInstance().getUserPublishStateList();
            if (!RoomControler.isReleasedBeforeClass()) {
                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                    if (RoomSession.isOnliyAudioRoom) {
                        TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                "__all", "publishstate", 1);
                    } else {
                        TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                "__all", "publishstate", 3);
                    }
                } else if (RoomControler.isAutomaticUp() && RoomSession.publishState.size() < RoomInfo.getInstance().getMaxVideo()
                        && TKRoomManager.getInstance().getMySelf().role == 2) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "publishstate", 3);
                } else if (!RoomControler.isAutomaticUp() && RoomControler.isNotLeaveAfterClass()) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "publishstate", 0);
                }
            } else {
                if (TKRoomManager.getInstance().getMySelf().role == 0 && TKRoomManager.getInstance().getMySelf().getPublishState() != 3) {
                    if (RoomSession.isOnliyAudioRoom) {
                        TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                "__all", "publishstate", 1);
                    } else {
                        TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                "__all", "publishstate", 3);
                    }
                } else if (!RoomControler.isAutomaticUp() && TKRoomManager.getInstance().getMySelf().role == 2) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "publishstate", 0);
                } else if (RoomControler.isAutomaticUp() && TKRoomManager.getInstance().getMySelf().publishState != 3 &&
                        RoomSession.publishState.size() < RoomInfo.getInstance().getMaxVideo()) {
                    if (TKRoomManager.getInstance().getMySelf().role == 2 || TKRoomManager.getInstance().getMySelf().role == 0) {
                        TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                "__all", "publishstate", 3);
                    }
                }
            }
        }
    }

    //开启全音频模式时候关闭自己发布的视频
    public void closeVideoAfterOpenOnlyAudioRoom(boolean inListPub) {
        //进入教室之前发送信令
        if (inListPub && RoomSession.isOnliyAudioRoom) {
            RoomSession.getInstance().getUserPublishStateList();
            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                        "__all", "publishstate", 1);
            }
            if (RoomControler.isAutomaticUp() && RoomSession.publishState.size() < RoomInfo.getInstance().getMaxVideo()) {
                if (TKRoomManager.getInstance().getMySelf().role == 2 || TKRoomManager.getInstance().getMySelf().role == 0) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "publishstate", 1);
                }
            }
        }
    }

    //上课时设置老师画笔颜色
    public void setTeacherPenColor() {
        //进入教室之前发送信令
        if (RoomControler.isCustomizeWhiteboard()) {
            String whiteboardcolor = RoomInfo.getInstance().getWhiteboardcolor();
            if (!TextUtils.isEmpty(whiteboardcolor)) {
                if (whiteboardcolor.equals("2")) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "primaryColor", "#ED3E3A");//红色
                } else {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "primaryColor", "#EDEDED");//白色
                }
            }
        } else {
            TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                    "__all", "primaryColor", "#ED3E3A");//红色
        }
    }

    //设置初始时画笔颜色
    public void setUserPenColor(RoomUser roomUser) {
        if (roomUser == null || !roomUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
            return;
        }
        if (RoomInfo.getInstance().getRoomType() == 0) {
            //1对1
            if (roomUser.role == 0) {
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                        "__all", "primaryColor", "#FF0000");//一对一默认红色
            } else {
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                        "__all", "primaryColor", "#000000");//一对一默认黑色
            }
        } else {
            if (RoomControler.isCustomizeWhiteboard()) {
                Random random = new Random();
                int number = random.nextInt(24);
                String whiteboardcolor = RoomInfo.getInstance().getWhiteboardcolor();
                if (TextUtils.isEmpty(whiteboardcolor) || whiteboardcolor.equals(number)) {
                    number = (number + 1) % 24;
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "primaryColor", Config.mColor[number]);
                } else {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "primaryColor", Config.mColor[number]);
                }
            } else {
                //默认白板随机不取白色
                Random random = new Random();
                int number = random.nextInt(24);
                if (number == 2) {
                    number = number + 1;
                }
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                        "__all", "primaryColor", Config.mColor[number]);
            }
        }
    }

    //加载自定义奖杯
    public void setRoomCustomCup(Context context) {
        if (RoomInfo.getInstance().getTrophyList() != null && RoomInfo.getInstance().getTrophyList().size() > 0) {
            SoundPlayUtils.loadTrophy(RoomVariable.host, RoomVariable.port, context);
        } else {
            if ("null".equals(RoomInfo.getInstance().get_MP3Url()) || TextUtils.isEmpty(RoomInfo.getInstance().get_MP3Url())) {
                SoundPlayUtils.init(context);
            } else {
                SoundPlayUtils.loadMP3(RoomVariable.host, RoomVariable.port, context);
            }
        }
    }
}