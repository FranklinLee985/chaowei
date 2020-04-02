package com.classroomsdk.http;

import java.io.File;

/**
 * date 2018/11/16
 * version
 * describe 网络请求回调接口
 *
 * @author hxd
 */

public interface DownLoadFileCallBack {
    void onSuccess(int statusCode, File file);

    void onFailure(int statusCode, File file, Throwable throwable);

    void onProgress(long bytesWritten, long totalSize);
}
