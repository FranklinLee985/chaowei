package com.eduhdsdk.toolcase;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

import com.classroomsdk.manage.WBSession;
import com.classroomsdk.utils.NotificationCenter;
import com.classroomsdk.utils.SoundPlayUtils;
import com.eduhdsdk.entity.RoomCacheMessage;
import com.eduhdsdk.interfaces.ShowingPopupWindowInterface;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.viewutils.PlayBackSeekPopupWindow;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by fucc on 2019/2/28.
 */

public class ToolCaseMgr implements NotificationCenter.NotificationCenterDelegate,
        ShowingPopupWindowInterface, LotteryPopupWindow.OnLotteryResultListener, TimerPopupWindw.TimeEndCallBack {

    private static ToolCaseMgr tcInstance;
    private Context mActivity;
    private View mRootView;
    //当课件信息回调时候显示popup
    private boolean hasWhiteBoradAction;
    //消息缓存
    private List<RoomCacheMessage> messageBuffer = Collections.synchronizedList(new ArrayList<RoomCacheMessage>());

    private PlayBackSeekPopupWindow mPlayBackSeekPopupWindow;

    //小白板
    public SmallWhiteBoardPopupWindow mSmallWhiteBoardPopupWindow;
    public boolean isClassBegin = false;

    public static ToolCaseMgr getInstance() {
        if (tcInstance == null) {
            synchronized (ToolCaseMgr.class) {
                if (tcInstance == null) {
                    tcInstance = new ToolCaseMgr();
                }
            }
        }
        return tcInstance;
    }

    public void resetInstance(){
        SoundPlayUtils.getInstance().resetInstance();
        AnswerPopupWindow.getInstance().resetInstance();
        LotteryPopupWindow.getInstance().resetInstance();
        ResponderPopupWindow.getInstance().resetInstance();
        TimerPopupWindw.getInstance().resetInstance();
        tcInstance = null;
    }

    public ToolCaseMgr() {
        NotificationCenter.getInstance().addObserver(this, WBSession.onRemoteMsg);
        NotificationCenter.getInstance().addObserver(this, WBSession.onUserChanged);
    }

    @Override
    public void didReceivedNotification(final int id, final Object... args) {

        if (args == null || mActivity == null ||
                (!RoomSession.isClassBegin && id == WBSession.onRemoteMsg && (boolean) args[5])) {
            return;
        }

        if (!hasWhiteBoradAction) {
            addMessageToBuffer(id, args);
            return;
        }

        ((Activity) mActivity).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    //收到信令消息回调
                    case WBSession.onRemoteMsg:
                        boolean add = (boolean) args[0];
                        String namePub = (String) args[2];
                        long pubMsgTS = (long) args[3];
                        Object dataPub = args[4];
                        boolean inListPub = (boolean) args[5];
                        JSONObject serverData = (JSONObject) args[9];
                        if (add) {
                            onRemotePubMsg(namePub, pubMsgTS, dataPub, inListPub, serverData);
                        } else {
                            onRemoteDelMsg(namePub, pubMsgTS);
                        }
                        break;
                    //用户属性改变
                    case WBSession.onUserChanged:
                        RoomUser propertyUser = (RoomUser) args[0];
                        Map<String, Object> map = (Map<String, Object>) args[1];
                        onUserChanged(propertyUser, map);
                        break;
                }
            }
        });
    }

    private void onRemotePubMsg(final String name, final long pubMsgTS, Object data, final boolean inListPub, final JSONObject jsonObject) {
        String strdata = null;
        if (data instanceof String) {
            strdata = (String) data;
        } else if (data instanceof Map) {
            strdata = new JSONObject((Map) data).toString();
        }
        if (mActivity != null) {
            String finalStrdata = strdata;
            switch (name) {
                case "Question":  //答题器
                    acceptQuestion(finalStrdata, pubMsgTS, inListPub);
                    break;
                case "GetQuestionCount": //答题器，获取到的统计结果
                    AnswerPopupWindow.getInstance().setQuestionCount(jsonObject);//答题器,公布答案
                    break;
                case "PublishResult": //答题器 公布答案
                    acceptPublishResult(finalStrdata);
                    break;
                case "dial":  // 转盘
                    acceptdial(finalStrdata);
                    break;
                case "timer":  //计时器
                    accepttimer(finalStrdata, inListPub, pubMsgTS);
                    break;
                case "qiangDaQi":  //抢答器
                    acceptqiangDaQi(finalStrdata, pubMsgTS);
                    break;
                case "QiangDaZhe":  //抢答器  有人抢中
                    acceptQiangDaZhe(finalStrdata);
                    break;
            }
        }
    }

    /***
     *      //抢答器  有人抢中
     * @param strdata
     */
    private void acceptQiangDaZhe(String strdata) {
        try {
            JSONObject jsdata = new JSONObject(strdata);
            String userAdmin = jsdata.getString("userAdmin");
            boolean isClick = jsdata.getBoolean("isClick");
            if (isClick && userAdmin != null && !"".equals(userAdmin)) {
                ResponderPopupWindow.getInstance().setTextView(userAdmin);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *    搶答器
     * @param strdata
     */
    private void acceptqiangDaQi(String strdata, long ts) {
        try {
            JSONObject jsdata = new JSONObject(strdata);
            boolean begin = jsdata.getBoolean("begin");
            boolean isShow = jsdata.getBoolean("isShow");
            if (begin || isShow) {
                if (TKRoomManager.getInstance().getMySelf().role != 2) {
                    showPopupWindowForResponder(isShow, begin, ts);
                }
                if (isShow && begin) {
                    showPopupWindowForResponder(isShow, begin, ts);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *   计时器
     * @param strdata
     */
    private void accepttimer(String strdata, boolean inList, long ts) {

        JSONObject timerJsonObject = null;
        try {
            timerJsonObject = new JSONObject(strdata);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (timerJsonObject == null) {
            return;
        }

        if (timerJsonObject.optBoolean("isShow") || timerJsonObject.optBoolean("isStatus") ||
                timerJsonObject.optBoolean("isRestart")) {
            if (TKRoomManager.getInstance().getMySelf().role != 2) {
                showPopupWindowForTimer();
            }
        }

        if (!timerJsonObject.optBoolean("isShow")) {
            if (timerJsonObject.optBoolean("isStatus")) {
                if (TKRoomManager.getInstance().getMySelf().role == 2) {
                    showPopupWindowForTimer();
                }
            }

            if (inList) {
                showPopupWindowForTimer();
            }
        }
        TimerPopupWindw.getInstance().disposeMsg(timerJsonObject, ts, inList);
    }

    /***
     *   答题器 公布答案
     * @param strdata
     */
    private void acceptdial(String strdata) {
        boolean isShow = true;
        float angle = 0;
        boolean ignoreAnimation = false;

        try {
            JSONObject jsmdata = new JSONObject(strdata);
            isShow = jsmdata.optBoolean("isShow");
            String rotationAngle = jsmdata.optString("rotationAngle");
            if (rotationAngle != null && !"".equals(rotationAngle)) {
                String[] strings = rotationAngle.split("\\(");
                String[] strings1 = strings[1].split("deg");
                angle = Float.parseFloat(strings1[0]);
                ignoreAnimation = (angle > 0) && !isShow && !getShowStateForLottery();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//            if (TKRoomManager.getInstance().getMySelf().role == 2) {
//                showPopupWindowForLottery(isShow);
//            }
        showPopupWindowForLottery(isShow, false);
        if (angle > 0) {
            showPopupWindowForLotteryStart(angle, ignoreAnimation);
        }
    }

    /***
     *    答題器
     * @param strdata
     */
    private void acceptQuestion(String strdata, long ts, boolean inListPub) {
        try {
            JSONObject jsdata = new JSONObject(strdata);
            String action = jsdata.getString("action");
            if (action.equals("start")) {//开始答题
                showPopupWindowForAnswer();
                AnswerPopupWindow.getInstance().setStartData(jsdata, ts);
            }
            if (action.equals("end")) {//结束答题
                showPopupWindowForAnswer();
                AnswerPopupWindow.getInstance().setEndData(jsdata, inListPub);
            }
            if (action.equals("open")) {//open 老师在重新答题用的，学生可以忽略
                if (TKRoomManager.getInstance().getMySelf().role != 2) {
                    showPopupWindowForAnswer();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *    //答题器 公布答案
     * @param strdata
     */
    private void acceptPublishResult(String strdata) {
        try {
            JSONObject jsdata = new JSONObject(strdata);
            boolean hasPub = jsdata.getBoolean("hasPub");
            AnswerPopupWindow.getInstance().publishResultOperation(hasPub);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onRemoteDelMsg(final String name, long ts) {
        if (mActivity != null) {
            switch (name) {
                case "timer":  // 计时器
                    TimerPopupWindw.getInstance().dismiss();
                    break;
                case "Question":  //答题器
                    AnswerPopupWindow.getInstance().dismiss();
                    break;
                case "dial":  //转盘
                    LotteryPopupWindow.getInstance().dismiss();
                    break;
                case "qiangDaQi":  //抢答器
                    ResponderPopupWindow.getInstance().dismiss();
                    break;
                case "BlackBoard_new":   //关闭小白板信令
                    closeSmallWhiteBoard();
                    break;
                case "ClassBegin":
                    TimerPopupWindw.getInstance().dismiss();
                    AnswerPopupWindow.getInstance().dismiss();
                    LotteryPopupWindow.getInstance().dismiss();
                    ResponderPopupWindow.getInstance().dismiss();
                    closeSmallWhiteBoard();
                    break;
            }
        }
    }

    /**
     * 用户属性改变
     *
     * @param roomUser 改变属性的用户
     * @param map      改变的属性集合
     */
    private void onUserChanged(final RoomUser roomUser, final Map<String, Object> map) {
        if (mActivity != null) {
            //下台用户隐藏抢答器
            if (roomUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("publishstate")) {
                int publishstate = (int) map.get("publishstate");
                if (publishstate == 0) {
                    ResponderPopupWindow.getInstance().dismiss();
                }
            }
        }
    }

    /**
     * 答题卡的popupwindow
     */
    public void showPopupWindowForAnswer() {
        AnswerPopupWindow.getInstance().showPopupWindow(mRootView);
        AnswerPopupWindow.getInstance().setPlayBackSeekPopupWindow(mPlayBackSeekPopupWindow);
    }

    /**
     * 转盘的popupwindow
     */
    public void showPopupWindowForLottery(boolean isShow, boolean isSend) {
        LotteryPopupWindow.getInstance().showPopupWindow(mRootView, isShow, isSend);
        LotteryPopupWindow.getInstance().setOnLotteryResultListener(this);
    }

    /**
     * 抢答的popupwindow
     */
    public void showPopupWindowForResponder(boolean isShow, boolean begin, long ts) {
        //未上台没有抢答器, 巡课有抢答器, 回放有抢答器
        if (TKRoomManager.getInstance().getMySelf().getPublishState() != 0 || TKRoomManager.getInstance().getMySelf().role == 4 || TKRoomManager.getInstance().getMySelf().role == -1) {
            ResponderPopupWindow.getInstance().showPopupWindow(mRootView);
            ResponderPopupWindow.getInstance().setRootView(mRootView);
            ResponderPopupWindow.getInstance().isRole(isShow, begin, ts);
        }
    }

    /**
     * 计时器的popupwindow
     */
    public void showPopupWindowForTimer() {
        TimerPopupWindw.getInstance().showPopupWindow(mRootView);
        TimerPopupWindw.getInstance().setPlayBackSeekPopupWindow(mPlayBackSeekPopupWindow);
        TimerPopupWindw.getInstance().setTimeEndCallBack(this);
        SoundPlayUtils.getInstance().init(mActivity );//初始化播放声音
    }

    /**
     * 启动转盘
     */
    public void showPopupWindowForLotteryStart(float totalAngle, boolean ignoreAnimation) {
        LotteryPopupWindow.getInstance().rotatingAnimStudent(totalAngle, ignoreAnimation);
    }

    /**
     * 获取转盘显示状态
     */
    private boolean getShowStateForLottery() {
        return (LotteryPopupWindow.getInstance().getPopupWindow() != null) ?
                LotteryPopupWindow.getInstance().getPopupWindow().isShowing() : false;
    }

    /**
     * @param activity  上下文环境
     * @param mRootView 工具显示区域
     */
    public void setActivity(Activity activity, final View mRootView) {
        this.mRootView = mRootView;
        this.mActivity = activity;
        AnswerPopupWindow.getInstance().setmACtivity(activity);
        AnswerPopupWindow.getInstance().setShowingPopupWindowInterface(this);

        LotteryPopupWindow.getInstance().setmActivity(activity);
        LotteryPopupWindow.getInstance().setShowingPopupWindowInterface(this);

        ResponderPopupWindow.getInstance().setmActivity(activity);
        ResponderPopupWindow.getInstance().setShowingPopupWindowInterface(this);

        TimerPopupWindw.getInstance().setActivity(activity);
        TimerPopupWindw.getInstance().setShowingPopupWindowInterface(this);

        mSmallWhiteBoardPopupWindow = new SmallWhiteBoardPopupWindow(activity);
        mSmallWhiteBoardPopupWindow.setHaiping(isHaiping);
        mSmallWhiteBoardPopupWindow.setShowingPopupWindowInterface(this);
    }

    @Override
    public void popupWindowShowing(int type) {
        if (type == ToolsPopupWindow.TOOLS_DATIQI) {//答题器
            ToolsPopupWindow.getInstance().setAnswerBtnChecked();
        } else if (type == ToolsPopupWindow.TOOLS_ZHUANPAN) {//转盘
            ToolsPopupWindow.getInstance().setLotteryBtnChecked();
        } else if (type == ToolsPopupWindow.TOOLS_JISHIQI) {//计时器
            ToolsPopupWindow.getInstance().setTimerBtnChecked();
        } else if (type == ToolsPopupWindow.TOOLS_QIANGDA) {//抢答
            ToolsPopupWindow.getInstance().setResponderBtnChecked();
        } else if (type == ToolsPopupWindow.TOOLS_XIAOBAIBAN) {//小白板
            ToolsPopupWindow.getInstance().setWhiteBoardBtnChecked();
        }
    }


    @Override
    public void onLotteryResult(float result) {
        try {
            JSONObject data = new JSONObject();
            data.put("rotationAngle", "rotate(" + result + "deg)");
            data.put("isShow", false);
            TKRoomManager.getInstance().pubMsg("dial", "dialMesg", "__all", data.toString(),
                    true, "ClassBegin", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endCallBack() {
        SoundPlayUtils.getInstance().play();
    }

    private boolean isHaiping = false;

    public void setLiuHaiping(boolean isHaiping) {
        this.isHaiping = isHaiping;
    }

    public void setPopupWindowVisibility(int visibility) {
        AnswerPopupWindow.getInstance().setVisibility(visibility);
        LotteryPopupWindow.getInstance().setVisibility(visibility);
        ResponderPopupWindow.getInstance().setVisibility(visibility);
        TimerPopupWindw.getInstance().setVisibility(visibility);
        if (mSmallWhiteBoardPopupWindow != null) {
            mSmallWhiteBoardPopupWindow.setVisibility(visibility);
        }
    }

    public void showSmallWhiteBoard() {
        if (mSmallWhiteBoardPopupWindow != null) {
            mSmallWhiteBoardPopupWindow.initPopwindow(mRootView);
            mSmallWhiteBoardPopupWindow.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ToolsPopupWindow.getInstance().setWhiteBoardBtnReset();
                }
            });
        }
    }

    /**
     * 关闭小白板
     */
    public void closeSmallWhiteBoard() {
        if (mSmallWhiteBoardPopupWindow != null) {
            mSmallWhiteBoardPopupWindow.ClosePopPen();
        }
    }

    /**
     * 移动工具箱内，popupwindow的位置
     */
    public void movePopupwindow(String name, Object data) {
        if (name.equals("ResponderDrag") || name.equals("AnswerDrag") || name.equals("DialDrag") || name.equals("TimerDrag")) {
            String strdata = null;
            if (data instanceof String) {
                strdata = (String) data;
            } else if (data instanceof Map) {
                strdata = new JSONObject((Map) data).toString();
            }
            try {
                JSONObject jsdata = new JSONObject(strdata);
                boolean isDrag = jsdata.getBoolean("isDrag");
                double percentLeft = Double.parseDouble(jsdata.getString("percentLeft"));
                double percentTop = Double.parseDouble(jsdata.getString("percentTop"));
                if (isDrag) {
                    if (name.equals("ResponderDrag")) {
                        ResponderPopupWindow.getInstance().movePopupWindow(mRootView, percentLeft, percentTop, isHaiping);
                    } else if (name.equals("AnswerDrag")) {
                        AnswerPopupWindow.getInstance().movePopupWindow(mRootView, percentLeft, percentTop, isHaiping);
                    } else if (name.equals("DialDrag")) {
                        LotteryPopupWindow.getInstance().movePopupWindow(mRootView, percentLeft, percentTop, isHaiping);
                    } else if (name.equals("TimerDrag")) {
                        TimerPopupWindw.getInstance().movePopupWindow(mRootView, percentLeft, percentTop, isHaiping);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void setHasWhiteBoradAction() {
        if (this.hasWhiteBoradAction) {
            return;
        }

        AnswerPopupWindow.getInstance().initPopupWindow();
        LotteryPopupWindow.getInstance().initPopupWindow();
        ResponderPopupWindow.getInstance().initPopupWindow();
        TimerPopupWindw.getInstance().initPopupWindow();

        this.hasWhiteBoradAction = true;
        if (messageBuffer != null && messageBuffer.size() > 0) {
            synchronized (RoomSession.class) {
                for (int x = 0; x < messageBuffer.size(); x++) {
                    didReceivedNotification(messageBuffer.get(x).getKey(),
                            messageBuffer.get(x).getObjects());
                }
                messageBuffer.clear();
            }
        }
    }

    public void setPlayBackSeekPopupWindow(PlayBackSeekPopupWindow window) {
        this.mPlayBackSeekPopupWindow = window;
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

    public void cleanData(boolean hasWhiteBorad) {
        hasWhiteBoradAction = hasWhiteBorad;
        TimerPopupWindw.getInstance().dismiss();
        AnswerPopupWindow.getInstance().dismiss();
        LotteryPopupWindow.getInstance().dismiss();
        ResponderPopupWindow.getInstance().dismiss();
        closeSmallWhiteBoard();
    }
}
