package com.classroomsdk.interfaces;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/11.
 */

public interface IWBCallback {

    void pubMsg(String js);

    void delMsg(String js);

    void onPageFinished();

    void changePageFullScreen(String isFull);

    void receiveJSActionCommand(String stateJson);

    void setProperty(String jsonProperty);

    void saveValueByKey(String key, String value);

    void getValueByKey(String key, int callBackId);

    void documentPreloadingEnd();

    void documentLoadSuccessOrFailure(String stateJson);

}
