package com.classroomsdk.utils;

import android.app.Activity;

import com.classroomsdk.Config;
import com.classroomsdk.common.RoomControler;
import com.classroomsdk.http.HttpHelp;
import com.classroomsdk.http.ResponseCallBack;
import com.loopj.android.http.RequestParams;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 获取ppt备注和下载的工具类
 * Created by fucc on 2019/1/24.
 */

public class PPTRemarkUtil {

    private static volatile PPTRemarkUtil Instance = null;

    private Map<String, JSONObject> remarks = new HashMap<>();

    private ChangePPtRemarkIF changePPtRemarkIF;

    private Activity activity;

    public static PPTRemarkUtil getInstance() {
        PPTRemarkUtil localInstance = Instance;
        if (localInstance == null) {
            synchronized (PPTRemarkUtil.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new PPTRemarkUtil();
                }
            }
        }
        return localInstance;
    }

    public void resetInstance() {
        Instance = null;
    }

    public void getPPTRemark(String webaddress, final String fileid, final int pagenum) {
        if (fileid == null || fileid.equals("0") || !RoomControler.isHasCoursewareNotes() || TKRoomManager.getInstance().getMySelf().role != 0) {
            return;
        }
        if (remarks.containsKey(fileid)) {
            if (changePPtRemarkIF != null) {
                try {
                    JSONObject jsonObject = remarks.get(fileid);
                    String remark = getRemarkStr(jsonObject, pagenum);
                    changePPtRemarkIF.changePPtRemark(remark, fileid, pagenum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        //下载
        final String url = Config.REQUEST_HEADER + webaddress + "/ClientAPI/getfileremark";
        final RequestParams params = new RequestParams();
        params.put("fileid", fileid);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HttpHelp.getInstance().post(url, params, new ResponseCallBack() {
                    @Override
                    public void success(int statusCode, JSONObject response) {
                        try {
                            final int nRet = response.getInt("result");
                            if (nRet == 0) {
                                if (changePPtRemarkIF != null) {
                                    String remark = getRemarkStr(response, pagenum);
                                    changePPtRemarkIF.changePPtRemark(remark, fileid, pagenum);
                                }
                                remarks.put(fileid, response);
                            } else {
                                changePPtRemarkIF.changePPtRemark("", fileid, pagenum);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
                        changePPtRemarkIF.changePPtRemark("", fileid, pagenum);
                    }
                });
            }
        });

    }

    private String getRemarkStr(JSONObject response, int pagenum) throws JSONException {
        String remark = "";
        Iterator<String> keys = response.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            JSONObject object = response.optJSONObject(next);
            if (object != null && String.valueOf(pagenum).equals(object.getString("pageid"))) {
                remark = object.getString("remark");
                break;
            }
        }
        return remark;
    }

    public void setChangePPtRemarkIF(ChangePPtRemarkIF changePPtRemarkIF) {
        this.changePPtRemarkIF = changePPtRemarkIF;
    }

    //显示ppt备注回调
    public interface ChangePPtRemarkIF {
        void changePPtRemark(String remark, String fileid, int pagenum);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
