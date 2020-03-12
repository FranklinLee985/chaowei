package com.eduhdsdk.room;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.classroomsdk.http.HttpHelp;
import com.classroomsdk.http.ResponseCallBack;
import com.eduhdsdk.BuildVars;
import com.eduhdsdk.R;
import com.eduhdsdk.tools.PermissionTest;
import com.eduhdsdk.tools.ResourceSetManage;
import com.eduhdsdk.tools.Tools;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/11/20/020.  检测摄像头和麦克风
 */
public class RoomCheck {

    static private RoomCheck mInstance = null;

    static public RoomCheck getInstance() {
        synchronized (RoomCheck.class) {
            if (mInstance == null) {
                mInstance = new RoomCheck();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        mInstance = null;
    }

    //检测摄像头
    public void checkCamera(Context context) {
        RoomUser roomUser = TKRoomManager.getInstance().getMySelf();
        if (roomUser != null && !roomUser.hasVideo) {
            Tools.showDialog(context, R.string.remind, context.getString(R.string.camera_hint,
                    context.getString(ResourceSetManage.getInstance().getAppName())), new Tools.OnDialogClick() {
                @Override
                public void dialog_ok(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    //检测麦克风
    public void checkMicrophone(Context context) {
        RoomUser roomUser = TKRoomManager.getInstance().getMySelf();
        if (roomUser != null && !roomUser.hasAudio) {
            Tools.showDialog(context, R.string.remind,
                    context.getString(R.string.mic_hint,context.getString(ResourceSetManage.getInstance().getAppName()))
                    , new Tools.OnDialogClick() {
                @Override
                public void dialog_ok(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    /***
     *
     * @param context
     * @param meetingid   教室号
     * @return
     */
    public boolean checkKickOut(Context context, String meetingid) {
        SharedPreferences preferences = context.getSharedPreferences("KickOutPersonInfo", Context.MODE_PRIVATE);
        String roomNumber = preferences.getString("RoomNumber", null);
        Long time = preferences.getLong("Time", 0);
        if (!TextUtils.isEmpty(roomNumber) && roomNumber.equals(meetingid)) {
            if (System.currentTimeMillis() - time <= 60 * 3 * 1000) {
                return true;
            }
        }
        return false;
    }

    //获取设备名称
    public void getmobilename(String host, int port) {
        String url = BuildVars.REQUEST_HEADER + host + ":" + port + "/ClientAPI/getmobilename";

        HttpHelp.getInstance().post(url, new ResponseCallBack() {
            @Override
            public void success(int statusCode, JSONObject response) {
                try {
                    int nRet = response.getInt("result");
                    if (nRet == 0) {
                        String mobilename = response.optJSONArray("mobilename").toString();
                        RoomVariable.mobilename = mobilename;
                        try {
                            String brand = Build.MODEL;
                            if (mobilename != null && !mobilename.isEmpty()) {
                                JSONArray mNames = new JSONArray(mobilename);
                                for (int i = 0; i < mNames.length(); i++) {
                                    if (brand.toLowerCase().equals(mNames.optString(i).toLowerCase())) {
                                        RoomVariable.mobilenameNotOnList = false;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
