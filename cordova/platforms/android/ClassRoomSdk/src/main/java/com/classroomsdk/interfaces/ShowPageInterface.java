package com.classroomsdk.interfaces;

import com.classroomsdk.bean.CaptureImg;
import com.classroomsdk.bean.ShowPageBean;

public interface ShowPageInterface {

    //回调showpage 返回界面 计算页码
    void SetShowPage(ShowPageBean showPageBean);
    // 给showPageBean重新赋值
    void setShowPageBean(ShowPageBean showPageBean);

    void setViewState();

}
