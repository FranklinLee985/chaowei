package com.eduhdsdk.interfaces;

/**工具箱内，关闭popupwindow的接口
 * Created by YF on 2018/12/27 0027.
 */

public interface ToolsPopupWindowCloseCallBack {

    /**
     * 关闭以下功能的监听回调
     *
     * @param type 1=答题器 2=转盘 3=计时器 4=抢答 5=小白板
     */
    void popupWindowDismissCallBack(int type);

    /**
     * 关闭以下功能的监听回调
     * @param type 1=答题器 2=转盘 3=计时器 4=抢答 5=小白板
     */
    void popupWindowShowCallBack(int type);
}
