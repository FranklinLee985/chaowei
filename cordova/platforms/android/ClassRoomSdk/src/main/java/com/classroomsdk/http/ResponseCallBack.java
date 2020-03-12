package com.classroomsdk.http;

import org.json.JSONObject;

/**
 * date 2018/11/16
 * version
 * describe 网络请求回调接口
 *
 * @author hxd
 */

public interface ResponseCallBack {
    void success(int statusCode, JSONObject response);

    void failure(int statusCode, Throwable throwable, JSONObject errorResponse);
}
