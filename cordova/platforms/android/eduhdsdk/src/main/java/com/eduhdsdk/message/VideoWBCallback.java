package com.eduhdsdk.message;

/**
 * Created by Administrator on 2017/5/11.
 */

public interface VideoWBCallback {

    void pubMsg(String js);

    void delMsg(String js);

    void onPageFinished();

    void exitAnnotation(String state);

    void saveValueByKey(String key,String value);

    void getValueByKey(String key,int callBackId);

}
