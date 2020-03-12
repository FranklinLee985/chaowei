package com.classroomsdk.manage;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;

import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.bean.ShowPageBean;
import com.classroomsdk.common.Packager;
import com.classroomsdk.common.ToolsFormType;
import com.classroomsdk.common.ToolsPenType;
import com.classroomsdk.common.ToolsType;
import com.classroomsdk.fragment.FaceShareFragment;
import com.classroomsdk.fragment.WBFragment;
import com.classroomsdk.interfaces.IWBStateCallBack;
import com.classroomsdk.interfaces.ShowPageInterface;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class WhiteBoradConfig {

    private static volatile WhiteBoradConfig cInstance;
    private WhiteBoradManager mManager = WhiteBoradManager.getInstance();
    private WBFragment fragment;
    private FaceShareFragment fragmentView;
    public int totalpagenum;
    public int currentNumber;

    //刷新页码回调
    ShowPageInterface showPageInterface;

    public static WhiteBoradConfig getsInstance() {
        if (cInstance == null) {
            synchronized (WhiteBoradConfig.class) {
                if (cInstance == null) {
                    cInstance = new WhiteBoradConfig();
                }
            }
        }
        return cInstance;
    }

    public void resetInstance(){
        cInstance = null;
    }

    /**
     * 创建白板并注册
     */

    public WBFragment CreateWhiteBorad(IWBStateCallBack callBack) {
        fragment = new WBFragment();
        registerDelegate(callBack, fragment);
        return fragment;
    }

//    /**
//     * 工具箱内 popupwindow关闭的回调
//     *
//     * @param callBack
//     */
//    public void setToolsPopupWindowCloseCallBack(ToolsPopupWindowCloseCallBack callBack) {
//        if (fragment != null) {
//            fragment.setToolsPopupWindowCloseCallBack(callBack);
//        }
//        if (fragmentView != null) {
//            fragmentView.setToolsPopupWindowCloseCallBack(callBack);
//        }
//    }

    /**
     * Fragment大小
     *
     * @param width  宽
     * @param height 高
     */
    public void SetTransmitWindowSize(int width, int height) {
        if (fragment != null && WBSession.isPageFinish) {
            fragment.transmitWindowSize(width, height);
        }
    }

    /**
     * 退出清除缓存及白板
     */
    public void clear() {
        fragment = null;
        mManager.clear();
    }

    /**
     * 白板是否是回放
     *
     * @param playBack true|false   是|不是
     */
    public void setPlayBack(boolean playBack) {
        if (fragment != null) {
            fragment.setPlayBack(playBack);
        }
    }

    /**
     * 是否是刘海屏
     *
     * @param isHaiping true|false   是|不是
     */
    public void isLiuHaiping(boolean isHaiping) {
        if (fragmentView != null) {
            fragmentView.setLiuHaiping(isHaiping);
        }
    }

    /**
     * 添加文档
     *
     * @param serial       教室号
     * @param path         文件路径
     * @param isClassBegin 是否上课
     */
    public void uploadRoomFile(String serial, String path, boolean isClassBegin, int writedb) {
        mManager.uploadRoomFile(serial, path, isClassBegin, writedb);
    }

    /**
     * 房间链接丢失
     */
    public void roomConnectionLost() {
        SharePadMgr.getInstance().onPlayBackClearAll();
    }

    /**
     * 删除文档
     *
     * @param roomNum      教室号
     * @param fileid       文件id
     * @param ismedia      是否音频文件
     * @param isClassBegin 是否已上课
     */
    public void delRoomFile(String roomNum, long fileid, boolean ismedia, boolean isClassBegin) {
        mManager.delRoomFile(roomNum, fileid, ismedia, isClassBegin);
    }

    /**
     * 切换文档
     *
     * @param doc 文档对象
     */
    public void localChangeDoc(ShareDoc doc) {
        if (fragment != null && WBSession.isPageFinish) {
            mManager.localChangeDoc(doc);
        }

        ShareDoc currentFile = WhiteBoradManager.getInstance().getCurrentFileDoc();
        if (currentFile != null) {
            ShowPageBean mCurrentShareDoc = Packager.getShowPageBean(currentFile);
            SharePadMgr.getInstance().showCourseSelectPage(mCurrentShareDoc);
        }
    }

    /**
     * 切换文档
     */
    public void localChangeDoc() {
        if (fragment != null && WBSession.isPageFinish) {
            fragment.localChangeDoc();
        }
    }

    /**
     * 获取 host
     *
     * @return
     */
    public String getFileServierUrl() {
        return mManager.getFileServierUrl();
    }

    /**
     * 获取port
     *
     * @return
     */
    public int getFileServierPort() {
        return mManager.getFileServierPort();
    }

    /**
     * 获取当前课件库文件
     *
     * @return
     */
    public ShareDoc getCurrentFileDoc() {
        return mManager.getCurrentFileDoc();
    }

    /**
     * 获取当前媒体库文件
     *
     * @return
     */
    public ShareDoc getCurrentMediaDoc() {
        return mManager.getCurrentMediaDoc();
    }

    /**
     * 设置当前媒体文件
     *
     * @param doc
     */
    public void setCurrentMediaDoc(ShareDoc doc) {
        mManager.setCurrentMediaDoc(doc);
    }

    /**
     * 设置当前课件库文件
     *
     * @param doc
     */
    public void setCurrentFileDoc(ShareDoc doc) {
        mManager.setCurrentFileDoc(doc);
    }

    /**
     * 获取课件库 教室文件
     *
     * @return
     */
    public ArrayList<ShareDoc> getClassDocList() {
        return mManager.getClassDocList();
    }

    /**
     * 获取课件库 公用文件
     *
     * @return
     */
    public ArrayList<ShareDoc> getAdminDocList() {
        return mManager.getAdminDocList();
    }

    /**
     * 获取媒体库 教室文件
     *
     * @return
     */
    public ArrayList<ShareDoc> getClassMediaList() {
        return mManager.getClassMediaList();
    }

    /**
     * 获取媒体库 公用文件
     *
     * @return
     */
    public ArrayList<ShareDoc> getAdminmMediaList() {
        return mManager.getAdminmMediaList();
    }

    /**
     * 获取所有课件库文件
     *
     * @return
     */
    public List<ShareDoc> getDocList() {
        return mManager.getDocList();
    }

    /**
     * 获取所有媒体库文件
     *
     * @return
     */
    public ArrayList<ShareDoc> getMediaList() {
        return mManager.getMediaList();
    }

    /**
     * 回放播放控制
     *
     * @param isplay
     */
    public void playbackPlayAndPauseController(boolean isplay) {
        mManager.playbackPlayAndPauseController(isplay);
    }

    /**
     * 是否显示工具箱
     *
     * @param isShow 是否显示 true 显示
     */
    public void showToolbox(boolean isShow) {
        if (fragment != null && WBSession.isPageFinish) {
            try {
                JSONObject js = new JSONObject();
                js.put("isShow", isShow);
                fragment.interactiveJS("'toolbox'", js.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否显示画笔工具
     *
     * @param isShow 是否显示
     */
    public void choosePen(boolean isShow) {
        if (fragment != null && WBSession.isPageFinish) {
            try {
                JSONObject js = new JSONObject();
                js.put("isShow", isShow);
                fragment.interactiveJS("'chooseShow'", js.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否显示自定义奖杯
     *
     * @param isShow true 显示
     */
    public void showCustomTrophy(boolean isShow) {
        if (fragment != null && WBSession.isPageFinish) {
            try {
                JSONObject js = new JSONObject();
                js.put("isShow", isShow);
                fragment.interactiveJS("'custom_trophy'", js.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下一页
     *
     * @return 是否切到上一页
     */
    public void nextPage() {
        if (fragment != null && WBSession.isPageFinish) {
            ShareDoc shareDoc = mManager.getCurrentFileDoc();
            fragment.interactiveJSPaging(shareDoc, true, totalpagenum > currentNumber ? true : false);
        }
    }

    /**
     * 上一页
     */
    public void prePage() {
        if (fragment != null && WBSession.isPageFinish) {
            ShareDoc shareDoc = mManager.getCurrentFileDoc();
            fragment.interactiveJSPaging(shareDoc, false, false);
        }
    }

    /**
     * 跳转到指定页
     *
     * @param pageNum 跳转的页码
     */
    public void skipToPageNum(int pageNum) {
        if (fragment != null && WBSession.isPageFinish) {
            fragment.interactiveJSSelectPage(pageNum);
        }
    }

    /**
     * 放大或缩小白板
     *
     * @param EnlargeOrNarrow true 放大  false 缩小
     */
    public void EnlargeOrNarrowWhiteboard(boolean EnlargeOrNarrow) {
        if (fragment != null && WBSession.isPageFinish) {
            if (EnlargeOrNarrow) {
                fragment.interactiveJS("'whiteboardSDK_enlargeWhiteboard'", null);
            } else {
                fragment.interactiveJS("'whiteboardSDK_narrowWhiteboard'", null);
            }
        }
    }

    /**
     * 全屏  退出全屏  WebPage
     *
     * @param isFull true 全屏 false 退出全屏
     */
    public void changeWebPageFullScreen(boolean isFull) {
        if (fragment != null && WBSession.isPageFinish) {
            fragment.changeWebPageFullScreen(isFull);
        }
    }

    /**
     * 通知全屏放大
     *
     * @param isFull
     */
    public void sendJSPageFullScreen(boolean isFull) {
        if (fragment != null && WBSession.isPageFinish) {
            fragment.sendJSPageFullScreen(isFull);
        }
    }

    /**
     * 关闭PPT
     */
    public void closeNewPptVideo() {
        if (fragment != null && WBSession.isPageFinish) {
            fragment.closeNewPptVideo();
        }
    }

    /**
     * 是否打开备注
     *
     * @param isopen
     */
    public void openDocumentRemark(boolean isopen) {
        if (fragment != null && WBSession.isPageFinish) {
            String action;
            if (isopen) {
                action = "'whiteboardSDK_openDocumentRemark'";
            } else {
                action = "'whiteboardSDK_closeDocumentRemark'";
            }
            fragment.interactiveJS(action, null);
        }
    }

    /**
     * 注册白板
     */
    private void registerDelegate(IWBStateCallBack callBack, Fragment fragment) {
        mManager.setWBCallBack(callBack);
        mManager.setLocalControl((WBFragment) fragment);
    }

    public int getTotalpagenum() {
        return totalpagenum;
    }

    public int getCurrentNumber() {
        return currentNumber;
    }

    /**
     * 隐藏白板
     *
     * @param isHide
     */
    public void hideWalkView(boolean isHide) {
        if (fragment != null) {
            fragment.hideWalkView(isHide);
        }
    }

    /***
     *    原生态白板
     * @return
     */
    public Fragment CreateWhiteBoardView() {
        fragmentView = FaceShareFragment.newInstance("", "");
        return fragmentView;
    }

    /**
     * 设置原生态白板大小
     *
     * @param width  宽
     * @param height 高
     */
    public void SetFaceShareSize(int width, int height) {
        if (fragmentView != null) {
            fragmentView.transmitFaceShareSize(width, height);
        }
    }

    /**
     * 设置画布是否全屏
     *
     * @param isFull
     */
    public void setPaintFaceShareFullScreen(boolean isFull) {
        if (fragmentView != null && WBSession.isPageFinish) {
            fragmentView.setFaceShareFullScreen(isFull);
        }
    }

    /**
     * 刷新页码回调
     *
     * @param showPageInterface
     */
    public void setShowPageInterface(ShowPageInterface showPageInterface) {
        this.showPageInterface = showPageInterface;
    }

    public void sendMotionEvent(MotionEvent event) {
        if (fragment != null && WBSession.isPageFinish) {
            fragment.scrollXWalkView(event);
        }
    }


    /************************画笔属性设置************************/

    /**
     * 设置画笔默认为鼠标
     */
    public void setPenToolsType() {
        if (fragmentView != null && WBSession.isPageFinish && TKRoomManager.getInstance().getMySelf().role == 0) {
            fragmentView.sendToolType(true);
        }
    }

    /**
     * 设置工具条状态
     *
     * @param toolsType
     */
    public void setToolsType(ToolsType toolsType) {
        if (fragmentView != null) {
            fragmentView.setToolsType(toolsType);
        }
    }

    /**
     * 设置顶层画笔显示还是隐藏
     *
     * @param visibilityTop true 显示 false 隐藏
     */
    public void setVisibilityTop(boolean visibilityTop) {
        if (fragmentView != null) {
            fragmentView.setVisibilityTop(visibilityTop);
        }
    }

    /**
     * 设置画笔是否隐藏
     *
     * @param isHideDraw true不显示  false显示
     */
    public void setHideDraw(boolean isHideDraw) {
        if (fragmentView != null) {
            fragmentView.setHideDraw(isHideDraw);
        }
    }

    /**
     * 设置画笔颜色
     *
     * @param color 0xff5AC9FA
     */
    public void setmToolsPenColor(int color) {
        if (fragmentView != null) {
            fragmentView.setmToolsPenColor(color);
        }
    }

    /**
     * 设置画笔类型
     *
     * @param penType 钢笔 荧光笔 直线 箭头
     */
    public void setmToolsPenType(ToolsPenType penType) {
        if (fragmentView != null) {
            fragmentView.setmToolsPenType(penType);
        }
    }

    /**
     * 设置画笔大小
     *
     * @param size 1 - 100
     */
    public void setmToolsPenProgress(int size) {
        if (fragmentView != null) {
            fragmentView.setmToolsPenProgress(size);
        }
    }


    /**
     * 设置文字颜色
     *
     * @param color
     */
    public void setmToolsFontColor(int color) {
        if (fragmentView != null) {
            fragmentView.setmToolsFontColor(color);
        }
    }

    /**
     * 设置文字大小
     *
     * @param size
     */
    public void setmToolsFontSize(int size) {
        if (fragmentView != null) {
            fragmentView.setmToolsFontSize(size);
        }
    }

    /**
     * 设置形状颜色
     *
     * @param color
     */
    public void setmToolsFormColor(int color) {
        if (fragmentView != null) {
            fragmentView.setmToolsFormColor(color);
        }
    }

    /**
     * 设置形状类型
     *
     * @param toolsFormType
     */
    public void setmToolsFormType(ToolsFormType toolsFormType) {
        if (fragmentView != null) {
            fragmentView.setmToolsFormType(toolsFormType);
        }
    }

    /**
     * 设置形状画笔大小
     *
     * @param size
     */
    public void setmToolsFormWidth(int size) {
        if (fragmentView != null) {
            fragmentView.setmToolsFormWidth(size);
        }
    }

    /**
     * 设置橡皮大小
     *
     * @param size
     */
    public void setmToolsEraserWidth(int size) {
        if (fragmentView != null) {
            fragmentView.setmToolsEraserWidth(size);
        }
    }


    /******************************画笔属性设置完毕****************************/


    /*****************************翻页属性设置开始*******************************/
    /**
     * 设置备注界面是否显示
     *
     * @param visibility true 显示 false 隐藏
     */
    public void setVisibilityRemark(boolean visibility) {
        if (fragmentView != null) {
            fragmentView.setVisibilityRemark(visibility);
        }
    }


    /**
     * 放大或缩小画布  并返回放大和缩小的系数
     *
     * @param isfull 放大还是缩小
     * @param isTop  是否是顶部画布
     * @return
     */
    public float getScale(boolean isfull, boolean isTop) {
        if (fragmentView != null) {
            return fragmentView.getLargeOrSmallScale(isfull, isTop);
        }
        return 0;
    }


    /**
     * 重置画布大小
     *
     * @param isTop 是否是顶部画布
     * @return
     */
    public float getresetLargeOrSmallscale(boolean isTop) {
        if (fragmentView != null) {
            return fragmentView.resetLargeOrSmallView(isTop);
        }
        return 0;
    }


    /*****************************翻页属性设置结束*******************************/


}
