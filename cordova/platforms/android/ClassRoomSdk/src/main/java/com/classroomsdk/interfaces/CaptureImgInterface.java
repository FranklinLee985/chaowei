package com.classroomsdk.interfaces;

import com.classroomsdk.bean.CaptureImg;

/**
 * 截图
 */
public interface CaptureImgInterface {

    /**
     * 开始下载
     */
    void startDownload(CaptureImg captureImg, String file);

    /**
     * 客户端截图回传界面创建pop
     *
     * @param captureImg
     * @param file
     */
    void setScreenShot(CaptureImg captureImg, String file);

    /**
     * 删除截图信令ID
     *
     * @param captureImgID
     */
    void dissmisScreenPop(String captureImgID);

    /**
     * 拖动数据
     *
     * @param id          拖动图片id
     * @param percentLeft 距离左边
     * @param percentTop  距离右边
     * @param isDrag      是否拖动
     */
    void SetCaptureImgDrag(String id, double percentLeft, double percentTop, boolean isDrag);


    /**
     * 截圖放大
     *
     * @param id
     * @param scale
     */
    void SetCaptureImgSize(String id, double scale);

    /**
     * 回放前拖动关闭所有pop
     */
    void ClearAllPop();
}
