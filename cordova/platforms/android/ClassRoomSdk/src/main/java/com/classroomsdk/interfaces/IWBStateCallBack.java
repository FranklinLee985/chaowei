package com.classroomsdk.interfaces;

import com.classroomsdk.bean.ShareDoc;

/**
 * Created by Administrator on 2017/5/9.
 */

public interface IWBStateCallBack {

    /**
     * 文档状态改变
     * @param isdel
     * @param ismedia
     */
    void onRoomDocChange(boolean isdel, boolean ismedia,ShareDoc doc);

    /**
     * 白板全屏
     * @param isZoom  true放大  false缩小
     */
    void onWhiteBoradZoom(boolean isZoom);

    /**
     * 白板动作
     * @param stateJson
     */
    void onWhiteBoradAction(String stateJson);

    /**
     * 下载进度回调
     * @param index 进度
     * @param type 1 下载 2 解压
     */
    void onDownloadProgress(int index,int type);

    /**
     * 隐藏下载界面
     */
    void hideDownload(boolean ishide);

}
