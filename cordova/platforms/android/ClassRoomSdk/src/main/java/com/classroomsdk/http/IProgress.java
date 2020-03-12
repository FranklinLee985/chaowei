package com.classroomsdk.http;

/**
 * 文件解压
 */
public interface IProgress {
    void onProgress(int progress);

    void onError(String msg);

    void onDone();
}
