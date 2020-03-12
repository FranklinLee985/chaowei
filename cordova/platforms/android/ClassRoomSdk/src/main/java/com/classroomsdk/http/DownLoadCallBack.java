package com.classroomsdk.http;

import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * date 2018/11/16
 * version
 * describe 网络请求回调接口
 *
 * @author hxd
 */

public interface DownLoadCallBack {
    void onSuccess(int statusCode, byte[] bytes);

    void onFailure(int statusCode, byte[] bytes, Throwable throwable);

    void onProgress(long bytesWritten, long totalSize);
}
