package com.classroomsdk.common;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;


import com.classroomsdk.interfaces.IWBCallback;
import com.classroomsdk.utils.Tools;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/3.
 */

public class JSWhitePadInterface {

    private static JSWhitePadInterface mInstance;
    public static boolean isClassbegin = false;
    private IWBCallback callBack;


    public static JSWhitePadInterface getInstance() {
        if (mInstance == null) {
            synchronized (JSWhitePadInterface.class) {
                if (mInstance == null) {
                    mInstance = new JSWhitePadInterface();
                }
            }
        }
        return mInstance;
    }

    public void resetInstance() {
        mInstance = null;
    }

    public void setWBCallBack(IWBCallback wbCallBack) {
        this.callBack = wbCallBack;
    }

    /**
     * 消息
     *
     * @param js
     */
    @JavascriptInterface
    public void pubMsg(String js) {
        if (callBack != null)
            callBack.pubMsg(js);
    }

    @JavascriptInterface
    public void delMsg(String js) {
        if (callBack != null)
            callBack.delMsg(js);
    }

    /**
     * 白板加载成功
     *
     * @param temp
     */
    @JavascriptInterface
    public void onPageFinished(String temp) {
        if (callBack != null)
            callBack.onPageFinished();
    }

    @JavascriptInterface
    public void printLogMessage(String msg) {

    }

    @JavascriptInterface
    public void changeWebPageFullScreen(String isFull) {
        if (callBack != null) {
            callBack.changePageFullScreen(isFull);
        }
    }

    @JavascriptInterface
    public void unpublishNetworkMedia(String jsonObject) {
        if (callBack != null) {

        }
    }

    @JavascriptInterface
    public void publishNetworkMedia(final String videoData) {
        TKRoomManager.getInstance().stopShareMedia();
        try {
            JSONObject jsdata = new JSONObject(videoData);
            String url = jsdata.optString("url");
            JSONObject attributes = jsdata.optJSONObject("attributes");
            String source = jsdata.optString("source");

            long fileid = ((Number) attributes.opt("fileid")).longValue();
            boolean isvideo = Tools.isTure(jsdata.opt("video"));

            HashMap<String, Object> attrMap = new HashMap<String, Object>();
            attrMap.put("filename", "");
            attrMap.put("fileid", fileid);
            attrMap.put("source", source);

            if (isClassbegin) {
                TKRoomManager.getInstance().startShareMedia(url, isvideo, "__all", attrMap);
            } else {
                TKRoomManager.getInstance().startShareMedia(url, isvideo, TKRoomManager.getInstance().getMySelf().peerId, attrMap);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void sendActionCommand(String jsonObject) {
        if (callBack != null) {
            try {
                JSONObject json = new JSONObject(jsonObject);
                if (json.optString("action").equals("viewStateUpdate")) {
                    String stateJson = json.optString("cmd");
                    callBack.receiveJSActionCommand(stateJson);
                } else if (json.optString("action").equals("preloadingFished")) {
                    callBack.documentPreloadingEnd();
                } else if (json.optString("action").equals("documentLoadSuccessOrFailure")) {
                    String stateJson = json.optString("cmd");
                    callBack.documentLoadSuccessOrFailure(stateJson);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @JavascriptInterface
    public void setProperty(String jsonProperty) {
        if (callBack != null) {
            callBack.setProperty(jsonProperty);
        }
    }

    @JavascriptInterface
    public void saveValueByKey(String data) {
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsData = new JSONObject(data);
                String key = jsData.optString("key");
                String value = jsData.optString("value");
                if (callBack != null) {
                    callBack.saveValueByKey(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @JavascriptInterface
    public void getValueByKey(String data) {
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsData = new JSONObject(data);
                String key = jsData.optString("key");
                int callbackID = jsData.optInt("callbackID");
                if (callBack != null) {
                    callBack.getValueByKey(key, callbackID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
