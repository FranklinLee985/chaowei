package com.eduhdsdk.room;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.widget.Toast;
import com.classroomsdk.manage.WBSession;
import com.eduhdsdk.R;
import com.eduhdsdk.interfaces.JoinmeetingCallBack;
import com.eduhdsdk.interfaces.MeetingNotify;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.ui.OneToManyActivity;
import com.eduhdsdk.ui.OneToOneActivity;
import com.talkcloud.room.TKPlayBackManager;
import com.talkcloud.room.TKRoomManager;

import org.tkwebrtc.EglBase;

import java.util.HashMap;
import java.util.Map;

import skin.support.SkinCompatManager;
import android.util.Log;
/***
 * xiaoyang for Customer
 */
public class RoomClient {

    private MeetingNotify notify;
    private JoinmeetingCallBack callBack;
    static private RoomClient mInstance = null;
    private Activity activity;
    private int type = 3;
    private ArrayMap<EglBase, Boolean> preEglMap = new ArrayMap<>();
    public static String webServer = "global.talk-cloud.net";
	private static final String TAG="RoomClient";  
    //public static String webServer = "global.talk-cloud.neiwang";
    //public static String webServer = "demo.talk-cloud.net";

    static public RoomClient getInstance() {
        synchronized (RoomClient.class) {
            if (mInstance == null) {
                mInstance = new RoomClient();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        activity = null;
        callBack = null;
        mInstance = null;
    }

    /**
     * @param activity 正常进教室
     * @param map
     */
    public void joinRoom(Activity activity, Map<String, Object> map) {
        this.activity = activity;
        initTKRoom(activity, map);
        checkRoom(activity, map);
    }

    /***
     *
     * @param activity   链接进教室
     * @param temp
     */
    public void joinRoom(Activity activity, String temp) {
		Log.i(TAG, "joinRoom enter."); 
        this.activity = activity;
        temp = Uri.decode(temp);
        String[] temps = temp.split("&");
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < temps.length; i++) {
            String[] t = temps[i].split("=");
            if (t.length > 1) {
                map.put(t[0], t[1]);
            }
        }
        if (map.containsKey("path")) {
            String tempPath = "http://" + map.get("path");
            map.put("path", tempPath);
        }
		Log.i(TAG, "joinRoom initTKRoom."); 
        initTKRoom(activity, map);
		Log.i(TAG, "joinRoom checkRoom."); 
        checkRoom(activity, map);
			Log.i(TAG, "joinRoom exit."); 
    }


    ///


    public void joinRoomEx(Activity activity, String temp) {
		Log.i(TAG, "joinRoomEx enter."); 
        this.activity = activity;
        temp = Uri.decode(temp);
        String[] temps = temp.split("&");
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < temps.length; i++) {
            String[] t = temps[i].split("=");
            if (t.length > 1) {
                map.put(t[0], t[1]);
            }
        }
      /*  if (map.containsKey("path")) {
            String tempPath = "http://" + map.get("path");
            map.put("path", tempPath);
        }*/

        map.put("port", 80);

        //新加皮肤字段   1 PC   2 Android   3 IOS
        map.put("clientType", "2");
		Log.i(TAG, "joinRoomEx initTKRoom."); 
        initTKRoom(activity, map);
		Log.i(TAG, "joinRoomEx checkRoom."); 
        checkRoom(activity, map);
		Log.i(TAG, "joinRoomEx exit."); 
    }

    /**
     * 初始化 TKRoomManager
     */
    public void initTKRoom(Activity activity, Map<String, Object> map) {
        // 获取端口
        if (map.get("port") instanceof Integer) {
            RoomVariable.port = (int) map.get("port");
        } else if (map.get("port") instanceof String) {
            boolean isNum = ((String) map.get("port")).matches("[0-9]+");
            if (isNum) {
                RoomVariable.port = Integer.parseInt((String) map.get("port"));
            }
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put(TKRoomManager.max_reconnect_count, 5);
        if (RoomVariable.port == 80) {
            params.put(TKRoomManager.useSecureSocket, false);
        } else if (RoomVariable.port == 443) {
            params.put(TKRoomManager.useSecureSocket, true);
        }
        TKRoomManager.init(activity.getApplicationContext(), "talkplus", params);
        TKRoomManager.getInstance().registerRoomObserver(RoomSession.getInstance());
        TKRoomManager.getInstance().registerMediaFrameObserver(RoomSession.getInstance());
        WBSession.getInstance().addobservers(activity);
    }

    /***
     * @param activity   链接看回放
     * @param temp      c v
     */
    public void joinPlayBackRoom(final Activity activity, String temp) {
        this.activity = activity;
        String[] temps = temp.split("&");
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < temps.length; i++) {
            String[] t = temps[i].split("=");
            if (t.length > 1) {
                map.put(t[0], t[1]);
            }
        }

        if (map.containsKey("path")) {
            String tempPath = "http://" + map.get("path");
            map.put("path", tempPath);
        }

        initTKRoom(activity, map);

        RoomVariable.host = map.get("host") instanceof String ? (String) map.get("host") : "";
        RoomVariable.serial = map.get("serial") instanceof String ? (String) map.get("serial") : "";
        RoomVariable.nickname = map.get("nickname") instanceof String ? (String) map.get("nickname") : "";
        RoomVariable.userid = map.get("userid") instanceof String ? (String) map.get("userid") : "";
        RoomVariable.password = map.get("password") instanceof String ? (String) map.get("password") : "";
        RoomVariable.param = map.get("param") instanceof String ? (String) map.get("param") : "";
        RoomVariable.domain = map.get("domain") instanceof String ? (String) map.get("domain") : "";
        RoomVariable.finalnickname = Uri.encode(RoomVariable.nickname);
        RoomVariable.path = map.get("path") instanceof String ? (String) map.get("path") : "";

        if (map.containsKey("type")) {
            if (map.get("type") instanceof Integer) {
                type = (int) map.get("type");
            } else if (map.get("type") instanceof String) {
                boolean isNum = ((String) map.get("type")).matches("[0-9]+");
                if (isNum) {
                    type = Integer.parseInt(map.get("type") + "");
                }
            }
        }

        //获取设备名称
        RoomCheck.getInstance().getmobilename(RoomVariable.host, RoomVariable.port);
        String url = RoomVariable.path + "room.json";
        TKPlayBackManager.getInstance().getPlayBackRoomJson(url);
    }

    public void checkRoom(final Activity activity, Map<String, Object> map) {

        RoomVariable.host = map.get("host") instanceof String ? (String) map.get("host") : "";
        RoomVariable.serial = map.get("serial") instanceof String ? (String) map.get("serial") : "";
        RoomVariable.nickname = map.get("nickname") instanceof String ? (String) map.get("nickname") : "";
        RoomVariable.userid = map.get("userid") instanceof String ? (String) map.get("userid") : "";
        RoomVariable.password = map.get("password") instanceof String ? (String) map.get("password") : "";
        RoomVariable.param = map.get("param") instanceof String ? (String) map.get("param") : "";
        RoomVariable.domain = map.get("domain") instanceof String ? (String) map.get("domain") : "";
        RoomVariable.servername = map.get("servername") instanceof String ? (String) map.get("servername") : "";
        RoomVariable.path = map.get("path") instanceof String ? (String) map.get("path") : "";
        RoomVariable.clientType = map.get("clientType") instanceof String ? (String) map.get("clientType") : "2";
        RoomVariable.finalnickname = Uri.encode(RoomVariable.nickname);

        int userrole = 2;
        if (map.get("userrole") instanceof Integer) {
            userrole = (int) map.get("userrole");
        } else if (map.get("userrole") instanceof String) {
            boolean isNum = ((String) map.get("userrole")).matches("[0-9]+");
            if (isNum) {
                userrole = Integer.parseInt((String) map.get("userrole"));
            }
        }

        //获取设备名称
        RoomCheck.getInstance().getmobilename(RoomVariable.host, RoomVariable.port);

        HashMap<String, Object> params = new HashMap<String, Object>();
        if (!RoomVariable.param.isEmpty()) params.put("param", RoomVariable.param);

        params.put("userid", RoomVariable.userid);
        params.put("password", RoomVariable.password);
        params.put("serial", RoomVariable.serial);
        params.put("userrole", userrole);
        params.put("nickname", RoomVariable.nickname);
        params.put("volume", 100);

        //新加皮肤字段   1 PC   2 Android   3 IOS
        params.put("clientType", RoomVariable.clientType);
        params.put("mobilenameOnList", RoomVariable.mobilenameNotOnList);

        if (RoomVariable.domain != null && !RoomVariable.domain.isEmpty())
            params.put("domain", RoomVariable.domain);
        if (RoomVariable.servername != null && !RoomVariable.servername.isEmpty())
            params.put("servername", RoomVariable.servername);

        if (userrole == 2 && RoomCheck.getInstance().checkKickOut(activity, RoomVariable.serial)) {
            Toast.makeText(activity, activity.getString(R.string.kick_out), Toast.LENGTH_SHORT).show();
            callBack.callBack(100);
            return;
        }

        if (TextUtils.isEmpty(RoomVariable.password) && userrole != 2) {
            if (callBack != null)
                callBack.callBack(4110);
            else
                Toast.makeText(activity, activity.getString(R.string.checkmeeting_error_4110), Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            UiModeManager uiModeManager = (UiModeManager) activity.getSystemService(Context.UI_MODE_SERVICE);
            if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
                hashMap.put("devicetype", "AndroidTV");
            } else {
                if (Tools.isPad(activity)) {
                    hashMap.put("devicetype", "AndroidPad");
                } else {
                    hashMap.put("devicetype", "AndroidPhone");
                }
            }

            if (!TextUtils.isEmpty(RoomVariable.host)) {
                TKRoomManager.getInstance().joinRoom(RoomVariable.host, RoomVariable.port,
                        RoomVariable.finalnickname, params, hashMap);
            }
        }
    }

    public void regiestInterface(MeetingNotify notify, com.eduhdsdk.interfaces.JoinmeetingCallBack callBack) {
        this.notify = notify;
        this.callBack = callBack;
    }

    public void setNotify(MeetingNotify notify) {
        this.notify = notify;
    }

    public void setCallBack(com.eduhdsdk.interfaces.JoinmeetingCallBack callBack) {
        this.callBack = callBack;
    }

    /***
     *
     * @param code  checkroom 回调
     */
    public void joinRoomcallBack(int code) {
        if (this.callBack != null && activity != null) {
            if (code == 0) {
                Intent intent = null;
                String name = SkinCompatManager.getInstance().getCurSkinName();
                if (RoomInfo.getInstance().getColourid() != null
                        && RoomInfo.getInstance().getColourid().equals("black")) {
                    if (name == null || name.equals("") || (!TextUtils.isEmpty(name) && !name.equals("black_skin.zip"))) {
                        //黑色
                        SkinCompatManager.getInstance().loadSkin("black_skin.zip",
                                SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                    }
                } else if (RoomInfo.getInstance().getColourid() != null
                        && RoomInfo.getInstance().getColourid().equals("tigerlily")) {
                    //橘色
                    if (name == null || name.equals("") || (!TextUtils.isEmpty(name) && !name.equals("orange_skin.zip"))) {
                        SkinCompatManager.getInstance().loadSkin("orange_skin.zip",
                                SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                    }
                } else {
                    if (!TextUtils.isEmpty(name)) {
                        //紫色
                        SkinCompatManager.getInstance().restoreDefaultTheme();
                    }
                }

                if (RoomInfo.getInstance().getRoomType() == 0 && !RoomControler.isShowAssistantAV()) {
                    intent = new Intent(activity, OneToOneActivity.class);
                } else {
                    intent = new Intent(activity, OneToManyActivity.class);
                }
                activity.startActivity(intent);
            }
            callBack.callBack(code);
        }
    }

    /***
     *
     * @param code
     * @param response   回放获取 roomJosn回调
     */
    public void onPlayBackRoomJson(int code, String response) {
        if (this.callBack != null && activity != null) {
            Intent intent = null;
            if (code == 0) {
                joinPlayBackRoom();

                String name = SkinCompatManager.getInstance().getCurSkinName();
                if (RoomInfo.getInstance().getColourid() != null
                        && RoomInfo.getInstance().getColourid().equals("black")) {
                    //黑色
                    if (name == null || name.equals("") || (!TextUtils.isEmpty(name) && !name.equals("black_skin.zip"))) {
                        SkinCompatManager.getInstance().loadSkin("black_skin.zip",
                                SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                    }
                } else if (RoomInfo.getInstance().getColourid() != null
                        && RoomInfo.getInstance().getColourid().equals("tigerlily")) {
                    //橘色
                    SkinCompatManager.getInstance().loadSkin("orange_skin.zip",
                            SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                } else {
                    //紫色
                    if (!TextUtils.isEmpty(name)) {
                        SkinCompatManager.getInstance().restoreDefaultTheme();
                    }
                }

                if (RoomInfo.getInstance().getRoomType() == 0 && !RoomControler.isShowAssistantAV()) {
                    intent = new Intent(activity, OneToOneActivity.class);
                } else {
                    intent = new Intent(activity, OneToManyActivity.class);
                }
                activity.startActivity(intent);
            }
            callBack.callBack(code);
        }
    }

    private void joinPlayBackRoom() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (RoomVariable.param != null && !RoomVariable.param.isEmpty())
            params.put("param", RoomVariable.param);
        if (RoomVariable.domain != null && !RoomVariable.domain.isEmpty())
            params.put("domain", RoomVariable.domain);
        if (RoomVariable.finalnickname != null && !RoomVariable.finalnickname.isEmpty())
            params.put("servername", RoomVariable.finalnickname);
        if (RoomVariable.path != null && !RoomVariable.path.isEmpty()) {
            params.put("playback", true);
        }
        if (!TextUtils.isEmpty(RoomVariable.path)) {
            params.put("path", RoomVariable.path);
        }
        if (type != -1) {
            params.put("type", type);
        }
        params.put("password", RoomVariable.password);
        params.put("nickname", RoomVariable.nickname);
        params.put("volume", 100);
        params.put("mobilenameOnList", RoomVariable.mobilenameNotOnList);
        params.put("serial", RoomInfo.getInstance().getSerial());
        params.put("userrole", RoomVariable.userrole);
        RoomVariable.params = params;

        if (!TextUtils.isEmpty(RoomVariable.host)) {
            TKPlayBackManager.getInstance().joinPlayBackRoom(RoomVariable.host, RoomVariable.port,
                    RoomVariable.nickname, RoomVariable.params, new HashMap<String, Object>());
        }
    }

    public void kickout(int res) {
        if (notify != null) {
            notify.onKickOut(res);
        }
    }

    public EglBase getPreEgl() {
        for (EglBase eglBase : preEglMap.keySet()) {
            if (!preEglMap.get(eglBase))
                return eglBase;
        }
        return EglBase.create();
    }

    public void setPreEglMap(EglBase eglBase, Boolean isCreate) {
        this.preEglMap.put(eglBase, isCreate);
    }

    public void onResetVideo() {
        if (this.preEglMap != null) {
            for (EglBase eglBase : preEglMap.keySet())
                preEglMap.put(eglBase, false);
        }
    }

    /***
     * 警告权限
     * @param code 1没有视频权限2没有音频权限
     */
    public void warning(int code) {
        if (notify != null) {
            notify.onWarning(code);
        }
    }

    public void onClassBegin() {
        if (notify != null) {
            notify.onClassBegin();
        }
    }

    public void onClassDismiss() {
        if (notify != null) {
            notify.onClassDismiss();
        }
    }

    public void onLeaveRoom() {
        if (notify != null) {
            notify.onLeaveRoom();
        }
    }
}
