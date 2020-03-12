package com.eduhdsdk.message;

import android.view.View;
import android.widget.RelativeLayout;

import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.room.RoomCheck;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.ui.holder.VideoItem;
import com.eduhdsdk.ui.holder.VideoItemToMany;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/25/025.
 */

public class SendingSignalling {

    static private SendingSignalling mInstance = null;

    static public SendingSignalling getInstance() {
        synchronized (SendingSignalling.class) {
            if (mInstance == null) {
                mInstance = new SendingSignalling();
            }
            return mInstance;
        }
    }

    /***
     *    发送课件全屏同步信令
     * @param isFullSrcreen
     */
    public void sendFullScreenMsg(boolean isFullSrcreen) {
        JSONObject data = new JSONObject();
        try {
            data.put("fullScreenType", "courseware_file");
            data.put("needPictureInPictureSmall", RoomControler.isFullScreenVideo() && isFullSrcreen && RoomSession.isClassBegin);
            if (isFullSrcreen) {
                TKRoomManager.getInstance().pubMsg("FullScreen", "FullScreen",
                        "__all", data.toString(), true, "ClassBegin", null);
            } else {
                TKRoomManager.getInstance().delMsg("FullScreen", "FullScreen",
                        "__all", data.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *  发送双击放大视频恢复信令
     * @param peerid
     */
    public void sendDoubleClickVideoRecovery(String peerid) {
        JSONObject data = new JSONObject();
        try {
            data.put("doubleId", peerid);
            data.put("isScreen", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TKRoomManager.getInstance().pubMsg("doubleClickVideo",
                "doubleClickVideo", "__all", data.toString(),
                true, null, null);
    }

    /***
     *
     * @param peerid   不移动
     */
    public void sendStudentNoMove(String peerid) {
        try {
            JSONObject data = new JSONObject();
            JSONObject moveData = new JSONObject();
            JSONObject md = new JSONObject();
            md.put("percentTop", 0);
            md.put("percentLeft", 0);
            md.put("isDrag", false);
            moveData.put(peerid, md);
            data.put("otherVideoStyle", moveData);
            if (TKRoomManager.getInstance().getMySelf().role>=Constant.USERROLE_TEACHER) {
                TKRoomManager.getInstance().pubMsg("videoDraghandle", "videoDraghandle",
                        "__allExceptSender", data.toString(), true, "ClassBegin", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *
     * @param videoItems
     * @param rel_students   发送视频框移动信令
     * @param v_students
     */
    public void sendStudentMove(ArrayList<VideoItemToMany> videoItems, RelativeLayout rel_students, View v_students) {
        try {
            JSONObject data = new JSONObject();
            JSONObject moveData = new JSONObject();
            for (int i = 0; i < videoItems.size(); i++) {
                VideoItemToMany it = videoItems.get(i);
                JSONObject md = new JSONObject();
                if (it.isMoved) {
                    double wid = rel_students.getWidth() - it.parent.getWidth();
                    double hid = rel_students.getHeight() - it.parent.getHeight() - v_students.getHeight();
                    double top = (it.parent.getTop() - v_students.getHeight()) / hid;
                    double left = it.parent.getLeft() / wid;
                    md.put("percentTop", top);
                    md.put("percentLeft", left);
                    md.put("isDrag", true);
                } else {
                    md.put("percentTop", 0);
                    md.put("percentLeft", 0);
                    md.put("isDrag", false);
                }
                moveData.put(it.peerid, md);
            }
            data.put("otherVideoStyle", moveData);
            if (TKRoomManager.getInstance().getMySelf().role>=0) {
                TKRoomManager.getInstance().pubMsg("videoDraghandle", "videoDraghandle",
                        "__allExceptSender", data.toString(), true, "ClassBegin", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *      发送视频框缩放信令
     * @param videoItems
     * @param isZoom
     * @param printHeight
     */
    public void sendScaleVideoItem(ArrayList<VideoItemToMany> videoItems, boolean isZoom, double printHeight) {
        try {
            JSONObject data = new JSONObject();
            JSONObject scaleData = new JSONObject();
            for (int i = 0; i < videoItems.size(); i++) {
                VideoItemToMany it = videoItems.get(i);
                JSONObject md = new JSONObject();
                double scale;
                if (isZoom) {
                    scale = it.parent.getHeight() / printHeight;
                } else {
                    scale = 1.0;
                }
                md.put("scale", scale);
                scaleData.put(it.peerid, md);
            }
            data.put("ScaleVideoData", scaleData);
            TKRoomManager.getInstance().pubMsg("VideoChangeSize", "VideoChangeSize",
                    "__allExceptSender", data.toString(), true, "ClassBegin", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *      发送分屏信令
     * @param screenID
     */
    private void sendSplitScreen(ArrayList<String> screenID) {
        try {
            JSONObject data = new JSONObject();
            JSONArray splitData = new JSONArray();
            for (int i = 0; i < screenID.size(); i++) {
                splitData.put(screenID.get(i));
            }
            data.put("userIDArry", splitData);
            if (screenID.size() > 0) {
                TKRoomManager.getInstance().pubMsg("VideoSplitScreen",
                        "VideoSplitScreen", "__allExceptSender", data.toString(),
                        true, "ClassBegin", null);
            } else {
                TKRoomManager.getInstance().delMsg("VideoSplitScreen",
                        "VideoSplitScreen", "__allExceptSender", data.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *    发送取消禁言/禁言信令
     * @param isAllBanSpeak
     */
    public void sendBanChatMessage(boolean isAllBanSpeak) {
        int[] roles = {2};
        HashMap<String, Object> property = new HashMap<>();
        try {
            JSONObject data = new JSONObject();
            data.put("isAllBanSpeak", true);
            if (isAllBanSpeak) {
                TKRoomManager.getInstance().pubMsg("EveryoneBanChat", "EveryoneBanChat", "__all",
                        data.toString(), true, null, null);
            } else {
                TKRoomManager.getInstance().delMsg("EveryoneBanChat", "EveryoneBanChat", "__all",
                        data.toString());
            }
            property.put("disablechat", isAllBanSpeak);
            TKRoomManager.getInstance().changeUserPropertyByRole(roles, "__all", property);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     *  答题器发送信令
     */
    public void sendAnswerMessage() {
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", "open");
                TKRoomManager.getInstance().pubMsg("Question", "Question_" +
                                RoomInfo.getInstance().getSerial(), "__all",
                        data.toString(), true, "ClassBegin", null);

                JSONObject dataDrag = new JSONObject();
                dataDrag.put("percentLeft", 0.5);
                dataDrag.put("percentTop", 0.5);
                TKRoomManager.getInstance().pubMsg("AnswerDrag", "AnswerDrag", "__all", dataDrag.toString(), false, "ClassBegin", null);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /***
     *  计时器发送信令
     */
    public void sendTimerMessage() {
        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(0);
        jsonArray.put(5);
        jsonArray.put(0);
        jsonArray.put(0);
        try {
            data.put("isStatus", false);
            data.put("sutdentTimerArry", jsonArray);
            data.put("isShow", true);
            data.put("isRestart", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
            TKRoomManager.getInstance().pubMsg("timer",
                    "timerMesg", "__all", data.toString(),
                    true, "ClassBegin", null);
        }
        JSONObject dataDrag = new JSONObject();
        try {
            dataDrag.put("percentLeft", 0.5);
            dataDrag.put("percentTop", 0.5);
            TKRoomManager.getInstance().pubMsg("TimerDrag", "TimerDrag", "__all", dataDrag.toString(), false, "ClassBegin", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *  小白板发送信令
     */
    public void sendSmallBoardMessage() {
        //如果是老师  发送准备小白板信令
        if (TKRoomManager.getInstance().getMySelf().role ==Constant.USERROLE_TEACHER) {
            Map<String, Object> prepareing = new HashMap<>();
            prepareing.put("blackBoardState", "_prepareing");
            prepareing.put("currentTapKey", "blackBoardCommon");
            prepareing.put("currentTapPage", 1);
            TKRoomManager.getInstance().pubMsg("BlackBoard_new", "BlackBoard_new", "__all", new JSONObject(prepareing).toString(), true, "ClassBegin", "");
        }
    }

    /***
     *  抢答器发送信令
     */
    public void sendResponderMessage() {
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
            try {
                JSONObject data = new JSONObject();
                data.put("isShow", true);
                data.put("begin", false);
                data.put("userAdmin", "");
                TKRoomManager.getInstance().pubMsg("qiangDaQi", "qiangDaQiMesg",
                        "__all", data.toString(), true, null, null);

                JSONObject dataDrag = new JSONObject();
                dataDrag.put("percentLeft", 0.5);
                dataDrag.put("percentTop", 0.5);
                TKRoomManager.getInstance().pubMsg("ResponderDrag", "ResponderDrag", "__all", dataDrag.toString(), false, "ClassBegin", null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
