package com.classroomsdk.interfaces;

/**
 * Created by Administrator on 2017/5/29.
 */

public interface ILocalControl {

    void localChangeDoc();

    void playbackPlayAndPauseController(boolean isplay);

    //刷新预加载 调用H5预加载
    void onFileList();
}
