package com.eduhdsdk.message;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2017/5/3.
 */

public class JSVideoWhitePadInterface {

    static private JSVideoWhitePadInterface mInstance = null;
    public static boolean isClassbegin = false;

    static public JSVideoWhitePadInterface getInstance() {
        synchronized (JSVideoWhitePadInterface.class) {
            if (mInstance == null) {
                mInstance = new JSVideoWhitePadInterface();
            }
            return mInstance;
        }
    }

    private VideoWBCallback callBack;

    public void setVideoWBCallBack(VideoWBCallback wbCallBack) {
        this.callBack = wbCallBack;
    }

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

    @JavascriptInterface
    public void onPageFinished(String temp) {
        if (callBack != null)
            callBack.onPageFinished();
    }

    @JavascriptInterface
    public void printLogMessage(String msg) {
    }


    @JavascriptInterface
    public void exitAnnotation(String state) {
        if (callBack != null) {
            callBack.exitAnnotation(state);
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
