package com.eduhdsdk.room;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.classroomsdk.http.HttpHelp;
import com.classroomsdk.http.ResponseCallBack;
import com.eduhdsdk.BuildVars;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.tools.Tools;
import com.loopj.android.http.RequestParams;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2018/11/19/019.
 * <p>
 * 房间操作调用方法
 */
public class RoomOperation {

    static private RoomOperation mInstance = null;
    public static int start = 0;
    public static int max = 19;

    private boolean isSending = false;

    public static long classStartTime, serviceTime, localTime, syetemTime;
    public static Timer timerAddTime, timerAfterLeaved, numberTimer, tSendGift;
    private boolean isToast = false;


    static public RoomOperation getInstance() {
        synchronized (RoomOperation.class) {
            if (mInstance == null) {
                mInstance = new RoomOperation();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        mInstance = null;
    }

    /***
     *    大并发时，获取房间的用户列表和人数
     */
    public void getBigRoomUnmberAndUsers(final Context context) {
        if (RoomSession._bigroom) {
            if (numberTimer == null) {
                numberTimer = new Timer();
                final int[] role = {1, 2};
                TKRoomManager.getInstance().getRoomUserNum(role, null);

                final HashMap hashMap = new HashMap<String, Object>();
                hashMap.put("ts", "asc");
                hashMap.put("role", "asc");
                TKRoomManager.getInstance().getRoomUsers(role, start, max, null, hashMap);

                numberTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TKRoomManager.getInstance().getRoomUserNum(role, null);
                                TKRoomManager.getInstance().getRoomUsers(role, start, max, null, hashMap);
                            }
                        });
                    }
                }, 2000, 1000);
            }
        }
    }

    /***
     *    下课后是否有时间节点退出教室
     * @param context
     */
    public void getSystemTime(final Context context) {
        String timeUrl = BuildVars.REQUEST_HEADER + RoomVariable.host + ":" + RoomVariable.port + "/ClientAPI/systemtime";
        HttpHelp.getInstance().post(timeUrl, new ResponseCallBack() {
            @Override
            public void success(int statusCode, final JSONObject response) {

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            syetemTime = response.getLong("time");

                            if (TKRoomManager.getInstance().getRoomProperties() != null) {
                                if (RoomInfo.getInstance().getEndtime() <= syetemTime) {
                                    Toast.makeText(context, context.getString(R.string.checkmeeting_error_5001),
                                            Toast.LENGTH_SHORT).show();
                                    if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                                        TKRoomManager.getInstance().leaveRoom();
                                    }
                                    return;
                                } else if (RoomInfo.getInstance().getEndtime() > syetemTime &&
                                        RoomInfo.getInstance().getEndtime() - 5 * 60 <= syetemTime) {

                                    long time = RoomInfo.getInstance().getEndtime() - syetemTime;

                                    if (time >= 60) {
                                        Toast.makeText(context, time / 60 + context.getString(R.string.end_class_tip_minute),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, time + context.getString(R.string.end_class_tip_second),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    startClass();
                                } else {
                                    startClass();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                RoomClient.getInstance().joinRoomcallBack(-1);
            }
        });
    }

    public void startClass() {
        TKRoomManager.getInstance().stopShareMedia();
        try {
            long expires = TKRoomManager.getInstance().getRoomProperties().optLong("endtime") + 5 * 60;
            if (!RoomControler.isNotLeaveAfterClass()) {
                TKRoomManager.getInstance().delMsg("__AllAll", "__AllAll", "__none",
                        new HashMap<String, Object>());
            }
            TKRoomManager.getInstance().pubMsg("ClassBegin", "ClassBegin",
                    "__all", new JSONObject().put("recordchat", true).toString(), true, expires);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendClassBeginToPhp();
    }

    /***
     *    发送上课消息给PHP
     */
    private void sendClassBeginToPhp() {
        if (!(TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER)) {
            return;
        }
        String webFun_controlroom = BuildVars.REQUEST_HEADER + RoomVariable.host + ":" +
                RoomVariable.port + "/ClientAPI" + "/roomstart";
        RequestParams params = new RequestParams();

        params.put("serial", RoomInfo.getInstance().getSerial());
        params.put("companyid", RoomInfo.getInstance().getCompanyid());

        //不是自动上课  点击上课 需要传的参数
        if (!RoomControler.isAutoClassBegin()) {
            params.put("userid", TKRoomManager.getInstance().getMySelf().peerId);
            params.put("roleid", TKRoomManager.getInstance().getMySelf().role);
        }

        HttpHelp.getInstance().post(webFun_controlroom, params, new ResponseCallBack() {
            @Override
            public void success(int statusCode, JSONObject response) {
                try {
                    int nRet = response.getInt("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    /***
     *   发送下课消息给PHP
     */
    public void sendClassDissToPhp() {
        TKRoomManager.getInstance().stopShareMedia();

        String webFun_controlroom = BuildVars.REQUEST_HEADER + RoomVariable.host + ":" +
                RoomVariable.port + "/ClientAPI" + "/roomover";
        RequestParams params = new RequestParams();

        params.put("act", 3);
        params.put("serial", RoomInfo.getInstance().getSerial());
        params.put("companyid", RoomInfo.getInstance().getCompanyid());

        //不是自动下课   不是有时间节点退出教室  点击下课 需要传的参数
        if (!RoomControler.isAutoClassDissMiss() && !RoomControler.haveTimeQuitClassroomAfterClass()) {
            params.put("userid", TKRoomManager.getInstance().getMySelf().peerId);
            params.put("roleid", TKRoomManager.getInstance().getMySelf().role);
        }

        HttpHelp.getInstance().post(webFun_controlroom, params, new ResponseCallBack() {
            @Override
            public void success(int statusCode, JSONObject response) {
                try {
                    int nRet = response.getInt("result");
                    if (nRet != 0) {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    /**
     * 发送奖杯
     *
     * @param userMap
     * @param map
     */
    public void sendGift(final HashMap<String, RoomUser> userMap, final Map<String, Object> map, final Context context) {
        synchronized (RoomOperation.class) {
            if (isSending) {
                return;
            }
            isSending = true;
            tSendGift = new Timer();

            tSendGift.schedule(new TimerTask() {
                int count = 0;

                @Override
                public void run() {
                    if (count == 2) {
                        isSending = false;
                        tSendGift.cancel();
                    } else {
                        count++;
                    }
                }
            }, 0, 1000);

            String url = BuildVars.REQUEST_HEADER + RoomVariable.host + ":" + RoomVariable.port + "/ClientAPI/sendgift";
            RequestParams params = new RequestParams();
            params.put("serial", RoomInfo.getInstance().getSerial());
            params.put("sendid", TKRoomManager.getInstance().getMySelf().peerId);
            params.put("sendname", TKRoomManager.getInstance().getMySelf().nickName);
            HashMap<String, String> js = new HashMap<String, String>();
            for (RoomUser u : userMap.values()) {
                js.put(u.peerId, u.nickName);
            }
            params.put("receivearr", js);
            HttpHelp.getInstance().post(url, params, new ResponseCallBack() {
                @Override
                public void success(int statusCode, JSONObject response) {
                    try {
                        int nRet = response.getInt("result");
                        if (nRet == 0) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (RoomUser u : userMap.values()) {
                                        long giftnumber = 0;
                                        if (u.properties.containsKey("giftnumber")) {
                                            giftnumber = u.properties.get("giftnumber") instanceof Integer ? (int) u.properties.get("giftnumber") : (long) u.properties.get("giftnumber");
                                        }
                                        giftnumber++;

                                        HashMap<String, Object> gift_send_data = new HashMap<String, Object>();
                                        gift_send_data.put("giftnumber", giftnumber);
                                        if (map != null) {
                                            gift_send_data.put("giftinfo", map);
                                        }
                                        TKRoomManager.getInstance().changeUserProperty(u.peerId, "__all", gift_send_data);
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                }
            });
        }
    }

    /***
     *   获取系统时间（PHP）
     */
    public void getSystemNowTime(final Context context) {
        if (timerAfterLeaved != null) {
            timerAfterLeaved.cancel();
            timerAfterLeaved = null;
        }
        timerAfterLeaved = new Timer();

        String timeUrl = BuildVars.REQUEST_HEADER + RoomVariable.host + ":" + RoomVariable.port + "/ClientAPI/systemtime";

        HttpHelp.getInstance().post(timeUrl, new ResponseCallBack() {
            @Override
            public void success(int statusCode, JSONObject response) {
                try {
                    if (TKRoomManager.getInstance().getRoomProperties() != null) {
                        syetemTime = response.optLong("time");
                        final long endClassTime = TKRoomManager.getInstance().getRoomProperties().optLong("endtime");
                        if (endClassTime > syetemTime) {

                            long time = TKRoomManager.getInstance().getRoomProperties().optLong("endtime") - syetemTime;
                            if (time <= 5 * 60) {
                                if (time - 5 * 60 == syetemTime) {
                                    isToast = true;
                                }
                                if (time >= 60) {
                                    Toast.makeText(context, time / 60 +
                                            context.getString(R.string.end_class_tip_minute), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, time +
                                            context.getString(R.string.end_class_tip_second), Toast.LENGTH_SHORT).show();
                                }
                            }

                            timerAfterLeaved.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    syetemTime += 1;

                                    if (endClassTime - syetemTime == 5 * 60 && !isToast) {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context,
                                                        context.getString(R.string.end_class_time), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    if (endClassTime == syetemTime) {
                                        if (timerAfterLeaved != null) {
                                            timerAfterLeaved.cancel();
                                            timerAfterLeaved = null;
                                        }
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                TKRoomManager.getInstance().leaveRoom();
                                            }
                                        });
                                    }
                                }
                            }, 1000, 1000);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    /***
     *   举手的逻辑
     * @param textView
     */
    public void handAction(TextView textView) {

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!RoomSession.isClassBegin) {
                    return true;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        RoomUser roomUser = TKRoomManager.getInstance().getMySelf();
                        if (roomUser != null && roomUser.getPublishState() != 0) {
                            TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                    "__all", "raisehand", true);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        RoomUser user = TKRoomManager.getInstance().getMySelf();
                        //判断是否在台上
                        if (user.getPublishState() == 0) {
                            if (TKRoomManager.getInstance().getMySelf().properties.containsKey("raisehand")) {
                                boolean israisehand = Tools.isTure(TKRoomManager.getInstance().getMySelf().properties.get("raisehand"));
                                if (israisehand) {
                                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                            "__all", "raisehand", false);
                                } else {
                                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                            "__all", "raisehand", true);
                                }
                            } else {
                                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                        "__all", "raisehand", true);
                            }
                        } else {
                            TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                    "__all", "raisehand", false);
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void releaseTimer() {
        if (timerAddTime != null) {
            timerAddTime.cancel();
            timerAddTime = null;
        }

        if (timerAfterLeaved != null) {
            timerAfterLeaved.cancel();
            timerAfterLeaved = null;
        }
    }

    public void isAutoClassBegin() {
        if (!RoomSession.isClassBegin) {
            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER && RoomControler.isAutoClassBegin()) {
                try {
                    long expires = TKRoomManager.getInstance().getRoomProperties().getLong("endtime") + 5 * 60;
                    if (RoomControler.isNotLeaveAfterClass()) {
                        TKRoomManager.getInstance().delMsg("__AllAll", "__AllAll", "__none",
                                new HashMap<String, Object>());
                    }
                    TKRoomManager.getInstance().pubMsg("ClassBegin", "ClassBegin",
                            "__all", new JSONObject().put("recordchat", true).toString(),
                            true, expires);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
