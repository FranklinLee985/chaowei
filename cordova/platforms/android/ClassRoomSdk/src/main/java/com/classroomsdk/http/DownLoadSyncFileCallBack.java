package com.classroomsdk.http;

import com.loopj.android.http.ResponseHandlerInterface;

import java.io.File;

import cz.msebera.android.httpclient.HttpResponse;

/**
 * date 2018/11/16
 * version
 * describe 网络请求回调接口
 *
 * @author hxd
 */

public interface DownLoadSyncFileCallBack {
    void onSuccess(int statusCode, File file);

    void onFailure(int statusCode, File file, Throwable throwable);

    void onProgress(long bytesWritten, long totalSize);

    void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response);

    void onStart();
}
