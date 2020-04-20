package com.classroomsdk.manage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;

import com.classroomsdk.bean.CaptureImg;
import com.classroomsdk.bean.LaserPenBean;
import com.classroomsdk.bean.ShareDoc;
import com.classroomsdk.bean.ShowPageBean;
import com.classroomsdk.bean.SmallPaintBean;
import com.classroomsdk.bean.StudentListBean;
import com.classroomsdk.bean.TL_PadAction;
import com.classroomsdk.bean.WB_Common;
import com.classroomsdk.bean.WB_LineOrArrowsBean;
import com.classroomsdk.bean.WB_LinePathBean;
import com.classroomsdk.bean.WB_RectangleBean;
import com.classroomsdk.bean.WB_TextBean;
import com.classroomsdk.bean.Wb_commonTwo;
import com.classroomsdk.bean.WhiteBroadActionBean;
import com.classroomsdk.common.Packager;
import com.classroomsdk.interfaces.CaptureImgInterface;
import com.classroomsdk.interfaces.FaceShareControl;
import com.classroomsdk.interfaces.ShowPageInterface;
import com.classroomsdk.interfaces.SmallBoardInterface;
import com.classroomsdk.interfaces.WhitePadInterface;
import com.classroomsdk.utils.ColorUtils;
import com.classroomsdk.utils.FileDownLoad;
import com.classroomsdk.utils.NotificationCenter;
import com.classroomsdk.utils.PPTRemarkUtil;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class SharePadMgr implements FaceShareControl, WhitePadInterface, NotificationCenter.NotificationCenterDelegate, FileDownLoad.FileDownLoadDelegate {

    private ArrayList<String> DocServerAddrBackupList;
    //文档下载地址域名
    private String DocServerAddr;
    private String DocServerAddrBackup;
    private String host;
    private int port;
    private static SharePadMgr mInstance;
    //白板画笔数据
    public Map<String, List<TL_PadAction>> mSumLetty = new HashMap<>();
    //小白板画笔数据
    public Map<String, List<TL_PadAction>> mSumLettySmall = new HashMap<>();
    //视频标注画笔数据
    public Map<String, List<TL_PadAction>> mSumLettyVideo = new HashMap<>();
    //截图画笔数据
    public Map<String, List<TL_PadAction>> mSumLettyScreen = new HashMap<>();

    //白板顶层画笔暂存
    public List<TL_PadAction> topActions = new ArrayList<>();
    //小白板顶层画笔暂存
    public List<TL_PadAction> topActionsSmall = new ArrayList<>();
    //视频标注顶层画笔暂存
    public List<TL_PadAction> topActionsVideo = new ArrayList<>();
    //截图顶层画笔暂存
    public Map<String, List<TL_PadAction>> topActionScreen = new HashMap<>();
    //大并发状态
    public boolean isBigRoom = false;

    //小白板分发时基础数据 后进入学生会以此基础画笔显示画布
    public List<TL_PadAction> mBasePaint = new ArrayList<>();
    //图片地址
    public Map<String, String> mPath = new HashMap<>();
    public ArrayList mDownPath = new ArrayList();
    public Context mAppContext;
    public Context mContext;
    //白板文档
    public ShowPageBean mCurrentShareDoc = null;
    //小白板文档
    public SmallPaintBean mSmallPaintDoc = null;
    //小白板接口
    private SmallBoardInterface OnSmallClick;
    //老师用户
    public RoomUser mTeacherUser = null;
    //白板刷新
    public DataChangeListener mDataChangeList;
    //白板顶部画笔刷新
    public TopDataChangeListener mTopDataChangeList;
    //鼠标画笔选择切换
    public SelectMouseListener mSelectMouseListener;
    //小白板刷新
    public DataChangeListenerSmall mDataChangeListSmall;
    //小白板顶层画笔
    public TopDataChangeListenerSmall mTopDataChangeListSmall;
    //视频标注画笔刷新
    public DataChangeListenerVideo mDataChangeListVideo;
    public TopDataChangeListenerVideo mTopDataChangeListenerVideo;

    //截图图片保存,key保存画布图片id value保存每个创建的view监听
    public Map<String, DataChangeListener> screenChangeListenermap = new HashMap<>();
    public Map<String, TopDataChangeListener> topScreenChangeListenermap = new HashMap<>();


    //中间层pdf回调
    public ShowWbFragmentViewListener mShowWbFragmentViewListener;

    //翻页监听
    private ShowPageInterface onShowpageListener;
    //截图
    private CaptureImgInterface mCaptureImgInterface;
    //小白板学生集合
    public List<StudentListBean> studentListBeans = new ArrayList<>();
    //白板荧光笔
    public LaserPenBean mLaserPenBean = null;
    //截图
    public CaptureImg mCaptureImg;

    public boolean isFlage = true;
    public String mapKey;

    private boolean isAddTeacher = false;

    /**
     * 翻页监听
     *
     * @param onclichListener
     */
    public void setShowPageOnclichListener(ShowPageInterface onclichListener) {
        this.onShowpageListener = onclichListener;
    }

    /**
     * 截图监听
     *
     * @param captureImgListener
     */
    public void setCaptureImgListener(CaptureImgInterface captureImgListener) {
        this.mCaptureImgInterface = captureImgListener;
    }

    public CaptureImgInterface getCaptureImgListener() {
        return mCaptureImgInterface;
    }

//    public void roomConnectionLost() {
//        mSumLetty.clear();
//    }


    @Override
    public void didStartDownloadFile(CaptureImg captureImg, File file) {
        mCaptureImgInterface.startDownload(captureImg, file.getAbsolutePath());
    }

    /**
     * 文件下载
     *
     * @param key  map  key
     * @param file 文件
     */
    @Override
    public void didFinishLoadingFile(String key, File file, ShowPageBean showPageBean) {

        mDownPath.remove(key);
        if (!key.equals(mCurrentShareDoc.getFiledata().getFileid() + "-" + mCurrentShareDoc.getFiledata().getCurrpage()) ||
                !mCurrentShareDoc.getFiledata().getFileid().equals(WhiteBoradManager.getInstance().getCurrentFileDoc().getFileid() + "")) {
            return;
        }

        if (mShowWbFragmentViewListener != null) {
            mShowWbFragmentViewListener.onHide();
        }

        if (mPath.isEmpty()) {
            mPath.put(key, file.getAbsolutePath());
            if (mDataChangeList != null) {
                mDataChangeList.onChange();
            }
        } else {
            if (!mPath.containsKey(key)) {
                mPath.put(key, file.getAbsolutePath());
                //和当前课件图片相同时才刷新页面
                if (mDataChangeList != null && key.equals(mapKey)) {
                    mDataChangeList.onChange();
                }
            }
        }
        //根据下载图片更新画布大小
        Bitmap bitmap = getCurrentImage();
        if (bitmap != null) {
            double ratio = bitmap.getWidth() * 1.0d / bitmap.getHeight();
            //WhiteBroadActionBean bean = new WhiteBroadActionBean(new WhiteBroadActionBean.Page(false, false, false, false, false, false, 1, 1), "general", 0, 0);
            WhiteBroadActionBean bean = new WhiteBroadActionBean();
            bean.setScale(2);
            bean.setIrregular(ratio);
            //发送 action 设置图片宽高
            WhiteBoradManager.getInstance().onWhiteBoradReceiveActionCommand(bean.toString());
        }
    }

    @Override
    public void didFailedLoadingFile() {

    }

    /**
     * 截图下载返回
     *
     * @param captureImg 截图类
     * @param file       下载文件
     */
    @Override
    public void didFinishLoadingFile(CaptureImg captureImg, File file) {
        //截图下载成功回传界面层处理
        if (mCaptureImgInterface != null) {
            mCaptureImgInterface.setScreenShot(captureImg, file.getAbsolutePath());
        }
    }

    //下载失败
    @Override
    public void didFailedLoadingFile(String key) {
        mDownPath.remove(key);
    }

    //进度
    @Override
    public void didChangedLoadProgress(float progress) {

    }

    //截图顶层画布刷新及数据清空
    public void informTopScreen(CaptureImg captureImg) {
        //顶层画布map 不为空 顶层画布画笔数据不空
        if (!topScreenChangeListenermap.isEmpty() && !topActionScreen.isEmpty()) {
            //顶层画布包含图片监听 && 画笔数据包涵当前图片画笔
            if (topScreenChangeListenermap.containsKey("captureImgBoard" + captureImg.getCaptureImgInfo().getFileid() + "-1") && topActionScreen.containsKey(captureImg.getCaptureImgInfo().getFileid())) {
                //删除
                topActionScreen.remove(captureImg.getCaptureImgInfo().getFileid());
                //刷新
                topScreenChangeListenermap.get("captureImgBoard" + captureImg.getCaptureImgInfo().getFileid() + "-1").onRefresh();
            }
        }
    }

    //白板顶层画布刷新及数据清空
    public void informTop() {
        if (mTopDataChangeList != null && topActions.size() > 0) {
            topActions.clear();
            mTopDataChangeList.onRefresh();
        }
    }

    //小白板顶层画布刷新及数据清空
    public void informTopSmall() {
        if (mTopDataChangeListSmall != null && topActionsSmall.size() > 0) {
            topActionsSmall.clear();
            mTopDataChangeListSmall.onRefresh();
        }
    }

    //视频标注顶层画布刷新及数据清空
    public void informTopVideo() {
        if (mTopDataChangeListenerVideo != null && topActionsVideo.size() > 0) {
            topActionsVideo.clear();
            mTopDataChangeListenerVideo.onRefresh();
        }
    }


    public interface DataChangeListener {
        void onChange();
    }

    //刷新白板顶部画笔
    public interface TopDataChangeListener {
        void onRefresh();
    }

    //是否选中鼠标
    public interface SelectMouseListener {
        void selectMouse(boolean select);
    }

    public interface DataChangeListenerSmall {
        void onChange();
    }

    //小白板刷新
    public interface TopDataChangeListenerSmall {
        void onRefresh();
    }

    public interface DataChangeListenerVideo {
        void onChange();
    }

    public interface TopDataChangeListenerVideo {
        void onRefresh();
    }

    public interface ShowWbFragmentViewListener {
        void onShow(ShowPageBean mCurrentShareDoc);

        // 表示是普通文档  pdf和xwalk 都隐藏
        void onHide();
    }

    //默认白板的创建
    private SharePadMgr() {
        init();
    }

    private void init() {
        ShowPageBean.FiledataBean filedataBean = new ShowPageBean.FiledataBean(1, 1, 0, 0, "0", 1, "whiteboard", "whiteboard", "", "");
        mCurrentShareDoc = new ShowPageBean("default", true, false, false, false, "show", "", filedataBean);
    }

    public void reset() {
        init();
    }

    //单例
    static public SharePadMgr getInstance() {
        synchronized (SharePadMgr.class) {
            if (mInstance == null) {
                mInstance = new SharePadMgr();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        FileDownLoad.getInstance().resetInstance();
        mInstance = null;
    }

    /**
     * 生成一个uuid
     *
     * @return
     */
    public String getUUID() {
        String uuid = UUID.randomUUID().toString().toLowerCase();
        return uuid;
    }

    //订阅数据
    public void setContext(Context cnt) {
        mContext = cnt;
        mAppContext = cnt.getApplicationContext();
        NotificationCenter.getInstance().addObserver(this, WBSession.onRoomConnected);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRemoteMsg);
        NotificationCenter.getInstance().addObserver(this, WBSession.onUserChanged);
        NotificationCenter.getInstance().addObserver(this, WBSession.onWhiteBoardParam);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRoomLeaved);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRoomJoin);
        NotificationCenter.getInstance().addObserver(this, WBSession.onPlayBackClearAll);
        NotificationCenter.getInstance().addObserver(this, WBSession.msgList);
    }

    /**
     * 白板顶部监听
     *
     * @param cls
     */
    public void setSelectMouseListener(SelectMouseListener cls) {
        this.mSelectMouseListener = cls;
    }

    /**
     * 白板监听
     *
     * @param cls
     */
    public void addOnDataChangeListener(DataChangeListener cls) {
        this.mDataChangeList = cls;
    }

    /**
     * 白板顶部监听
     *
     * @param cls
     */
    public void addOnTopDataChangeListener(TopDataChangeListener cls) {
        this.mTopDataChangeList = cls;
    }

    /**
     * 所有截图界面监听
     *
     * @param cls
     */
    public void addOnScreenChangeListener(DataChangeListener cls, CaptureImg captureImg) {
        if (captureImg != null) {
            screenChangeListenermap.put("captureImgBoard" + captureImg.getCaptureImgInfo().getFileid() + "-1", cls);
        }
    }

    /**
     * 所有截图界面顶层
     *
     * @param cls
     */
    public void addOnTopScreenChangeListener(TopDataChangeListener cls, CaptureImg captureImg) {
        if (captureImg != null) {
            topScreenChangeListenermap.put("captureImgBoard" + captureImg.getCaptureImgInfo().getFileid() + "-1", cls);
        }
    }

    /**
     * 小白板监听
     *
     * @param cls
     */
    public void addOnDataChangeListenerSmall(DataChangeListenerSmall cls) {
        this.mDataChangeListSmall = cls;
    }

    /**
     * 小白板顶部画笔监听
     *
     * @param cls
     */
    public void addOnTopDataChangeListenerSmall(TopDataChangeListenerSmall cls) {
        this.mTopDataChangeListSmall = cls;
    }

    /**
     * 视频标注
     *
     * @param cls
     */
    public void addOnDataChangeListenerVideo(DataChangeListenerVideo cls) {
        this.mDataChangeListVideo = cls;
    }

    /**
     * 视频标注 顶层数据更新监听
     *
     * @param cls
     */
    public void addOnTopDataChangeListenerVideo(TopDataChangeListenerVideo cls) {
        this.mTopDataChangeListenerVideo = cls;
    }

    /**
     * 中间层pdf
     *
     * @param cls
     */
    public void setShowWbFragmentViewListener(ShowWbFragmentViewListener cls) {
        this.mShowWbFragmentViewListener = cls;
    }

    public void removeOnDataChangeListener(DataChangeListener cls) {
        //        if (mDataChangeList.contains(cls)) {
        //            mDataChangeList.remove(cls);
        //        }
    }

    /**
     * 删除底层画布监听
     *
     * @param captureImg
     */
    public void removeOnScreenChangeListener(CaptureImg captureImg) {
        String key = "captureImgBoard" + captureImg.getCaptureImgInfo().getFileid() + "-1";
        if (!screenChangeListenermap.isEmpty() && screenChangeListenermap.containsKey(key)) {
            screenChangeListenermap.remove(key);
            //captureImgBoard20190219174043-1
            //删除关闭截图pop所有画笔
            if (!mSumLettyScreen.isEmpty() && mSumLettyScreen.containsKey(key)) {
                mSumLettyScreen.remove(key);
            }
        }
    }

    /**
     * 删除顶层画布监听
     *
     * @param captureImg
     */
    public void removeOnTopScreenChangeListener(CaptureImg captureImg) {
        if (!topScreenChangeListenermap.isEmpty() && topScreenChangeListenermap.containsKey("captureImgBoard-" + captureImg.getCaptureImgInfo().getFileid())) {
            topScreenChangeListenermap.remove("captureImgBoard-" + captureImg.getCaptureImgInfo().getFileid());
        }
    }

    //当前界面bitmap
    public Bitmap btcurrentImage = null;
    //当前界面文档key
    public String btDoc;
    //当前界面文档页
    public String btpage;

    /**
     * 获取shoupage后文件bitmap
     *
     * @return
     */
    public Bitmap getCurrentImage() {
        if (mCurrentShareDoc != null) {
            String key = mCurrentShareDoc.getFiledata().getFileid() + "-" + mCurrentShareDoc.getFiledata().getCurrpage();
            if (mDownPath != null && mDownPath.size() > 0) {
                if (mDownPath.contains(key)) {
                    if (btcurrentImage != null) {
                        return btcurrentImage;
                    }
                }
            }
            if (!mPath.containsKey(key)) return null;
            if (mPath.isEmpty()) return null;
            if (!mPath.containsKey(key)) return null;
            String file = mPath.get(key);
            if (file == null) return null;
            if (btpage == null)
                btpage = "0";
            if (mCurrentShareDoc.getFiledata().getFileid().equals(btDoc) && mCurrentShareDoc.getFiledata().getCurrpage() == Integer.parseInt(btpage) && btcurrentImage != null) {
                return btcurrentImage;
            }

            if (btcurrentImage != null && !btcurrentImage.isRecycled()) {
                btcurrentImage.recycle();
                System.gc();
                btcurrentImage = null;
            }

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file, opts);
            int imageHeight = opts.outHeight;
            int imageWidth = opts.outWidth;
            int nScale = imageHeight * imageWidth / (1920 * 1080);
           /* if (nScale > 1) {
                opts.inSampleSize = nScale;
            }*/
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inJustDecodeBounds = false;

            try {
                btcurrentImage = BitmapFactory.decodeFile(file, opts);
            } catch (OutOfMemoryError err) {
                err.printStackTrace();
            }
            if (btcurrentImage != null) {
                this.btDoc = mCurrentShareDoc.getFiledata().getFileid();
                this.btpage = String.valueOf(mCurrentShareDoc.getFiledata().getCurrpage());
            }
            return btcurrentImage;
        }
        return null;
    }


    /***
     *      发送画笔数据
     * @param nActs
     * @param padaction
     */
    @Override
    public void SendActions(int nActs, Object padaction) {
        //{"eventType":"shapeSaveEvent",
        // "actionName":"AddShapeAction",
        // "shapeId":"b53255f1-26a0-4cd4-75b2-d8cf70bf5a90",
        // "data":{"className":"Line",
        // "data":{"x1":630.9425431766704,"y1":410.8260303121461,"x2":680.4033656568516,"y2":621.3515824072763,"strokeWidth":5,"color":"#eb070e","capStyle":"round","dash":null,"endCapShapes":[null,"arrow"]},
        // "id":"b53255f1-26a0-4cd4-75b2-d8cf70bf5a90"},
        // "whiteboardID":"default",
        // "nickname":"admin"}
        if (padaction instanceof TL_PadAction) {
            if (nActs == WhitePadInterface.ADD_ACTION) {  //添加画笔
                addAction(padaction);
            } else if (nActs == WhitePadInterface.DELETE_ACTION) {

            } else if (nActs == WhitePadInterface.MODIFY_ACTION) {

            }
        }
    }

    /***
     *     发送画笔数据
     * @param padaction
     */
    private void addAction(Object padaction) {
        TL_PadAction action = (TL_PadAction) padaction;
        if (action != null) {
            if (!action.id.contains("###")) return;
            String[] str = action.id.split("_");
            WB_Common wbCommon = null;
            Wb_commonTwo wbCommonTwo = null;
            switch (action.nActionMode) {
                case ft_line:    //线
                case ft_arrowLine:  //箭头
                    PointF Pt1 = null, pt2 = null;
                    if (action.points.size() == 2) {
                        Pt1 = action.points.get(0);
                        pt2 = action.points.get(1);
                    } else if (action.points.size() == 1) {
                        Pt1 = action.points.get(0);
                        pt2 = action.ptSizingEnd;
                    }
                    if (Pt1 == null && pt2 == null) break;

                    String[] strings = new String[2];
                    //区分箭头和直线
                    if (action.nActionMode == TL_PadAction.factoryType.ft_arrowLine) {
                        strings[0] = "";
                        strings[1] = "arrow";
                    } else if (action.nActionMode == TL_PadAction.factoryType.ft_line) {
                        strings[0] = "";
                        strings[1] = "";
                    }

                    WB_LineOrArrowsBean wbLineOrArrowsBean = new WB_LineOrArrowsBean();
                    wbLineOrArrowsBean.setX1(Pt1.x);
                    wbLineOrArrowsBean.setY1(Pt1.y);
                    wbLineOrArrowsBean.setX2(pt2.x);
                    wbLineOrArrowsBean.setY2(pt2.y);
                    wbLineOrArrowsBean.setStrokeWidth(action.nPenWidth);
                    wbLineOrArrowsBean.setCapStyle("round");
                    wbLineOrArrowsBean.setDash("");
                    wbLineOrArrowsBean.setColor(ColorUtils.toRGB(action.nPenColor));
                    wbLineOrArrowsBean.setEndCapShapes(strings);

                    wbCommonTwo = new Wb_commonTwo<>();
                    wbCommonTwo.setClassName("Line");
                    wbCommonTwo.setId(action.id.substring(0, action.id.indexOf("#")));
                    wbCommonTwo.setData(wbLineOrArrowsBean);

                    wbCommon = new WB_Common();
                    wbCommon.setCodeID(action.sID);
                    wbCommon.setEventType("shapeSaveEvent");
                    wbCommon.setActionName("AddShapeAction");
                    wbCommon.setShapeId(wbCommonTwo.getId());
                    wbCommon.setNickname(TKRoomManager.getInstance().getMySelf().nickName != null ? TKRoomManager.getInstance().getMySelf().nickName : "");
                    if (action.boardType == 3) {
                        wbCommon.setWhiteboardID(str[2] != null ? str[2] : "default");
                        wbCommon.setBaseboard(false);
                    } else {
                        wbCommon.setWhiteboardID("default");
                    }
                    wbCommon.setData(wbCommonTwo);
                    break;

                case ft_markerPen:  //标记
                case ft_Eraser:     //橡皮擦
                    WB_LinePathBean wbLinePathBean = new WB_LinePathBean();
                    wbLinePathBean.setOrder(3);
                    wbLinePathBean.setTailSize(3);
                    wbLinePathBean.setSmooth(true);
                    wbLinePathBean.setPointSize(action.nPenWidth);
                    if (action.nActionMode == TL_PadAction.factoryType.ft_markerPen && !action.bIsFill) {
                        Integer[] rgb = ColorUtils.RGB(action.nPenColor);
                        String rgba = "rgba(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + "," + "0.5)";
                        wbLinePathBean.setPointColor(rgba);
                    } else if (action.nActionMode == TL_PadAction.factoryType.ft_markerPen && action.bIsFill) {
                        wbLinePathBean.setPointColor(ColorUtils.toRGB(action.nPenColor));
                    } else {
                        wbLinePathBean.setPointColor("#000");
                    }
                    if (action.points.size() <= 1) break;

                    List<List<Float>> Pairs = new ArrayList<>();
                    for (int i = 0; i < action.points.size(); i++) {
                        List<Float> pts = new ArrayList<>();
                        PointF pointF = action.points.get(i);
                        pts.add(pointF.x);
                        pts.add(pointF.y);
                        Pairs.add(pts);
                    }
                    wbLinePathBean.setPointCoordinatePairs(Pairs);

                    wbCommonTwo = new Wb_commonTwo<>();
                    wbCommonTwo.setClassName(action.nActionMode == TL_PadAction.factoryType.ft_markerPen ? "LinePath" : "ErasedLinePath");
                    wbCommonTwo.setId(action.id.substring(0, action.id.indexOf("#")));
                    wbCommonTwo.setData(wbLinePathBean);

                    wbCommon = new WB_Common();
                    wbCommon.setCodeID(action.sID);
                    wbCommon.setEventType("shapeSaveEvent");
                    wbCommon.setActionName("AddShapeAction");
                    wbCommon.setShapeId(wbCommonTwo.getId());
                    wbCommon.setNickname(TKRoomManager.getInstance().getMySelf().nickName != null ? TKRoomManager.getInstance().getMySelf().nickName : "");

                    if (action.boardType == 1) {
                        wbCommon.setBaseboard(false);
                        wbCommon.setWhiteboardID(action.nDocID);
                        if (mSmallPaintDoc != null) {
                            if (TKRoomManager.getInstance().getMySelf().role == 0 && mSmallPaintDoc.getBlackBoardState().equals("_prepareing")) {
                                wbCommon.setBaseboard(true);
                            }
                        }
                    } else if (action.boardType == 2) {
                        wbCommon.setWhiteboardID("videoDrawBoard");
                        wbCommon.setBaseboard(false);
                    } else if (action.boardType == 3) {
                        wbCommon.setWhiteboardID(str[2] != null ? str[2] : "default");
                        wbCommon.setBaseboard(false);
                    } else {
                        wbCommon.setWhiteboardID("default");
                        wbCommon.setBaseboard(false);
                    }

                    wbCommon.setData(wbCommonTwo);

                    break;
                case ft_Rectangle:  //矩形
                case ft_Ellipse:    //椭圆
                    PointF rectPt1 = null, rectpt2 = null;
                    if (action.points.size() == 2) {
                        rectPt1 = action.points.get(0);
                        rectpt2 = action.points.get(1);
                    } else if (action.points.size() == 1) {
                        rectPt1 = action.points.get(0);
                        rectpt2 = action.ptSizingEnd;
                    }
                    WB_RectangleBean wbRectangleBean = new WB_RectangleBean();
                    wbRectangleBean.setX(rectPt1.x);
                    wbRectangleBean.setY(rectPt1.y);
                    wbRectangleBean.setWidth(rectpt2.x);
                    wbRectangleBean.setHeight(rectpt2.y);
                    wbRectangleBean.setStrokeWidth(action.nPenWidth);
                    wbRectangleBean.setStrokeColor(ColorUtils.toRGB(action.nPenColor));
                    if (action.bIsFill) {
                        wbRectangleBean.setFillColor(ColorUtils.toRGB(action.nPenColor));
                    } else {
                        wbRectangleBean.setFillColor("transparent");
                    }

                    wbCommonTwo = new Wb_commonTwo<>();
                    wbCommonTwo.setClassName(action.nActionMode == TL_PadAction.factoryType.ft_Rectangle ? "Rectangle" : "Ellipse");
                    wbCommonTwo.setId(action.id.substring(0, action.id.indexOf("#")));
                    wbCommonTwo.setData(wbRectangleBean);

                    wbCommon = new WB_Common();
                    wbCommon.setCodeID(action.sID);
                    wbCommon.setEventType("shapeSaveEvent");
                    wbCommon.setActionName("AddShapeAction");
                    wbCommon.setShapeId(wbCommonTwo.getId());
                    wbCommon.setNickname(TKRoomManager.getInstance().getMySelf().nickName != null ? TKRoomManager.getInstance().getMySelf().nickName : "");
                    if (action.boardType == 3) {
                        wbCommon.setWhiteboardID(str[2] != null ? str[2] : "default");
                        wbCommon.setBaseboard(false);
                    } else {
                        wbCommon.setWhiteboardID("default");
                    }
                    wbCommon.setData(wbCommonTwo);
                    break;
                case ft_Text:  //文本
                    WB_TextBean wbTextBean = new WB_TextBean();
                    wbTextBean.setX(action.points.get(0).x - action.nPenWidth);
                    wbTextBean.setY(action.points.get(0).y);
                    wbTextBean.setText(action.sText);
                    wbTextBean.setColor(ColorUtils.toRGB(action.nPenColor));
                    wbTextBean.setFont("normal normal " + action.nPenWidth + "px 微软雅黑");
                    wbTextBean.setForcedWidth(action.nTextWidth);
                    wbTextBean.setForcedHeight(0);
                    wbTextBean.setV(1);
                    wbCommonTwo = new Wb_commonTwo<>();
                    wbCommonTwo.setClassName("Text");
                    wbCommonTwo.setId(action.id.substring(0, action.id.indexOf("#")));
                    wbCommonTwo.setData(wbTextBean);

                    wbCommon = new WB_Common();
                    wbCommon.setCodeID(action.sID);
                    wbCommon.setEventType("shapeSaveEvent");
                    wbCommon.setActionName("AddShapeAction");
                    wbCommon.setShapeId(wbCommonTwo.getId());
                    wbCommon.setNickname(TKRoomManager.getInstance().getMySelf().nickName != null ? TKRoomManager.getInstance().getMySelf().nickName : "");
                    wbCommon.setWhiteboardID("default");
                    if (action.boardType == 1) {
                        wbCommon.setBaseboard(false);
                        wbCommon.setWhiteboardID(action.nDocID);
                        if (mSmallPaintDoc != null) {
                            if (TKRoomManager.getInstance().getMySelf().role == 0 && mSmallPaintDoc.getBlackBoardState().equals("_prepareing")) {
                                wbCommon.setBaseboard(true);
                            }
                        }
                    }
                    if (action.boardType == 3) {
                        wbCommon.setWhiteboardID(str[2] != null ? str[2] : "default");
                        wbCommon.setBaseboard(false);
                    }

                    wbCommon.setData(wbCommonTwo);
                    break;
            }
            if (wbCommon == null) return;
            try {
                JSONObject jsonObject_wbCommon = new JSONObject(wbCommon.toString());

                AssembleBrush(jsonObject_wbCommon, action.id, false, false);
                if (action.boardType == 1) {
                    TKRoomManager.getInstance().pubMsg("SharpsChange", action.id, "__all", jsonObject_wbCommon.toString(), true, "BlackBoard_new", null);
                } else if (action.boardType == 3) {
                    TKRoomManager.getInstance().pubMsg("SharpsChange", action.id, "__all", jsonObject_wbCommon.toString(), true, "CaptureImg_" + action.nDocID, null);
                } else {
                    TKRoomManager.getInstance().pubMsg("SharpsChange", action.id, "__all", jsonObject_wbCommon.toString(), true, null, null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 订阅消息
     *
     * @param id   订阅id
     * @param args 数据
     */
    @Override
    public void didReceivedNotification(final int id, final Object... args) {
        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args == null) {
                        return;
                    }
                    //信令信息
                    if (id == WBSession.onRemoteMsg) {
                        boolean addRemoteMsg = (boolean) args[0];
                        String idRemoteMsg = (String) args[1];
                        String nameRemoteMsg = (String) args[2];
                        long tsRemoteMsg = (long) args[3];
                        Object dataRemoteMsg = (Object) args[4];
                        boolean inList = (boolean) args[5];
                        String fromIDRemoteMsg = (String) args[6];
                        String associatedMsgIDRemoteMsg = (String) args[7];
                        String associatedUserIDRemoteMsg = (String) args[8];
                        JSONObject jsonObjectRemoteMsg = (JSONObject) args[9];
                        if (addRemoteMsg) {
                            onRemotePubMsg(idRemoteMsg, nameRemoteMsg, tsRemoteMsg, dataRemoteMsg, inList, fromIDRemoteMsg, associatedMsgIDRemoteMsg, associatedUserIDRemoteMsg, jsonObjectRemoteMsg);
                        } else {
                            onRemoteDelMsg(idRemoteMsg, nameRemoteMsg, tsRemoteMsg, dataRemoteMsg, inList, fromIDRemoteMsg, associatedMsgIDRemoteMsg, associatedUserIDRemoteMsg, jsonObjectRemoteMsg);
                        }
                        //连接教室成功后
                    } else if (id == WBSession.onRoomConnected) {
                        org.json.JSONArray jsonArray = (org.json.JSONArray) args[0];
                        List list = (List) args[1];
                        org.json.JSONObject jsonObje = (org.json.JSONObject) args[2];
                        onRoomConnected(jsonArray, list, jsonObje);
                        //服务器地址
                    } else if (id == WBSession.onWhiteBoardParam) {
                        ArrayList<String> DocServerAddrBackupList = (ArrayList<String>) args[0];
                        String DocServerAddr = (String) args[1];
                        String DocServerAddrBackup = (String) args[2];
                        String host = (String) args[3];
                        int port = (int) args[4];
                        onWhiteBoardUrl(DocServerAddrBackupList, DocServerAddr, DocServerAddrBackup, host, port);
                        //离开教室
                    } else if (id == WBSession.onRoomLeaved) {
                        resetDate();
                    } else if (id == WBSession.onUserChanged) {
                        RoomUser roomUser = (RoomUser) args[0];
                        Map<String, Object> map = (Map<String, Object>) args[1];
                        if (roomUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("candraw") && !(TKRoomManager.getInstance().getMySelf().role == 0)) {
                            ShareDoc shareDoc = WhiteBoradManager.getInstance().getCurrentFileDoc();
                            if (shareDoc != null) {
                                ShowPageBean mCurrentShareDoc = Packager.getShowPageBean(shareDoc);
                                onShowpageListener.SetShowPage(mCurrentShareDoc);
                            }
                        }
                    } else if (id == WBSession.onPlayBackClearAll) {
                        onPlayBackClearAll();
                    } else if (id == WBSession.msgList) {
                        org.json.JSONArray jsonArray = (org.json.JSONArray) args[0];
                        OnMsgList(jsonArray);
                    }
                }
            });
        }
    }

    /**
     * 回放前进后退数据回调
     *
     * @param jsonArray
     */
    private void OnMsgList(org.json.JSONArray jsonArray) {
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    if (jsonArray.get(i) instanceof org.json.JSONObject) {
                        org.json.JSONObject js = (org.json.JSONObject) jsonArray.get(i);
                        String id = js.optString("id");
                        String strData = js.optString("data");
                        JSONObject json = new JSONObject(strData);
                        String name = js.optString("name");

                        if (name.equals("SharpsChange")) {
                            //类型
                            String eventType = json.getString("eventType");
                            if (eventType != null && eventType.equals("shapeSaveEvent")) {
                                String actionName = json.getString("actionName");
                                if (actionName != null && actionName.equals("AddShapeAction")) {
                                    //添加画笔动作
                                    AssembleBrush(json, id, false, true);
                                }
                            } else if (eventType.equals("redoEvent")) {
                                AssembleBrush(json, id, true, true);
                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void onWhiteBoardUrl(ArrayList<String> docServerAddrBackupList, String docServerAddr, String docServerAddrBackup, String host, int port) {
        this.DocServerAddrBackupList = docServerAddrBackupList;
        this.DocServerAddr = docServerAddr;
        this.DocServerAddrBackup = docServerAddrBackup;
        this.host = host;
        this.port = port;
    }

    /***
     * 房间链接成功
     * @param jsonArray
     * @param list
     * @param jsonObje
     */
    private void onRoomConnected(JSONArray jsonArray, List list, org.json.JSONObject jsonObje) {

        if (WhiteBoradManager.getInstance().getDefaultFileDoc() != null) {
            ShareDoc shareDoc = WhiteBoradManager.getInstance().getDefaultFileDoc();
            if (shareDoc != null) {
                mCurrentShareDoc = Packager.getShowPageBean(shareDoc);
                displayPage(mCurrentShareDoc);
            }
        }

        for (int i = 0; i < list.size(); i++) {
            JSONObject js = new JSONObject((Map<String, Object>) list.get(i));
            String id = js.optString("id");
            String strData = js.optString("data");
            JSONObject json = null;
            String name = js.optString("name");
            try {
                json = new JSONObject(strData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (name.equals("BigRoom")) {
                isBigRoom = true;
            }
        }
    }

    //回访清除数据
    public void onPlayBackClearAll() {
        //清除所有画笔数据
        mSumLetty.clear();
        mSumLettySmall.clear();
        mSumLettyVideo.clear();
        mSumLettyScreen.clear();
        //清除所有top画笔数据
        topActions.clear();
        topActionsSmall.clear();
        topActionsVideo.clear();
        topActionScreen.clear();

        //关闭所有截图pop
        if (mCaptureImgInterface != null) {
            mCaptureImgInterface.ClearAllPop();
        }
        //小白板基础数据
        mBasePaint.clear();
        //截图监听类
        screenChangeListenermap.clear();
        topScreenChangeListenermap.clear();
        //小白板学生清除
        studentListBeans.clear();
    }

    private void resetDate() {
        //小白板类
        mSmallPaintDoc = null;
        //小白板画笔
        mSumLettySmall.clear();
        //小白板基础数据
        mBasePaint.clear();
        //学生集和
        studentListBeans.clear();
        //大并发
        isBigRoom = false;
    }

    private void onRemoteDelMsg(String id, String name, long ts, Object data, boolean inList, String fromID, String associatedMsgID, String associatedUserID, org.json.JSONObject jsonObject) {

        JSONObject jsonData = null;
        if (data != null) {
            try {
                if (data instanceof String) {
                    jsonData = new JSONObject((String) data);
                } else if (data instanceof Map) {
                    jsonData = new org.json.JSONObject((Map) data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        switch (name) {
            case "SharpsChange":   //清除所有画笔数据
                acceptDelSharpsChange(jsonData, id, fromID);
                break;

            case "BlackBoard_new":   //关闭小白板信令
                acceptDelBlackBoard_new();
                break;

            case "ClassBegin":   //下课信令
                if (onShowpageListener != null) {
                    onShowpageListener.setViewState();
                }
                //清除涂鸦
                if (mDataChangeList != null) {
                    onPlayBackClearAll();
                    mDataChangeList.onChange();
                }
                break;

            case "CaptureImg":    //删除截图
                if (mCaptureImgInterface != null) {
                    mCaptureImgInterface.dissmisScreenPop(id);
                }
                break;

            case "VideoWhiteboard":   //视频标注后 继续播放或者关闭视频 的时候  接收此信息，清除画笔信息
                mSumLettyVideo.clear();
                mBasePaint.clear();
                break;

            case "UserHasNewBlackBoard": //小白板学生退出
                acceptDelUserHasNewBlackBoard(jsonData);
                break;
        }
    }

    //小白板学生退出
    private void acceptDelUserHasNewBlackBoard(JSONObject strdata) {
        if (strdata != null) {
            StudentListBean studentListBean = new StudentListBean();
            studentListBean.setId(strdata.optString("id"));
            studentListBean.setNickname(strdata.optString("nickname"));
            studentListBean.setRole(strdata.optInt("role"));
            studentListBean.setPublishstate(strdata.optInt("publishstate"));

            if (mSmallPaintDoc != null) {
                //如果当前选中用户为退出学生用户，学生退出后默认跳转到老师Tab项
                if (mSmallPaintDoc.getCurrentTapKey().equals(studentListBean.getId())) {
                    mSmallPaintDoc.setCurrentTapKey("blackBoardCommon");
                }
                Map<String, Object> prepareing = new HashMap<>();
                prepareing.put("blackBoardState", mSmallPaintDoc.getBlackBoardState());
                prepareing.put("currentTapKey", "blackBoardCommon");
                prepareing.put("currentTapPage", 1);
                TKRoomManager.getInstance().pubMsg("BlackBoard_new", "BlackBoard_new", "__all", new JSONObject(prepareing).toString(), true, "ClassBegin", "");
            }
            if (studentListBeans != null && studentListBeans.size() > 0) {
                Iterator<StudentListBean> listBeanIterator = studentListBeans.iterator();
                while (listBeanIterator.hasNext()) {
                    StudentListBean Ite = listBeanIterator.next();
                    if (Ite.getId().equals(studentListBean.getId())) {
                        listBeanIterator.remove();
                        //刷新tab
                        if (OnSmallClick != null) {
                            OnSmallClick.setStudents(studentListBeans);
                        }
                        //刷新tab所对应的画笔数据
                        if (mDataChangeListSmall != null) {
                            mDataChangeListSmall.onChange();
                        }
                        break;
                    }
                }
            }
        }
    }

    /***
     *   关闭小白板信令
     */
    private void acceptDelBlackBoard_new() {
        mSmallPaintDoc = null;
        mSumLettySmall.clear();
        mBasePaint.clear();
        studentListBeans.clear();
    }

    /***
     *   清除所有画笔数据
     * @param json
     * @param id
     */
    private void acceptDelSharpsChange(JSONObject json, String id, String fromID) {
        if (json != null) {
            //类型 (回放时eventType的值不正确，故去掉对eventType的判断)
            String eventType = json.optString("eventType");
            String actionName = json.optString("actionName");
            String clearActionId = json.optString("clearActionId");
            //撤销画笔
            if (actionName != null && actionName.equals("AddShapeAction")) {
                //撤销画笔动作
                undoEventBrush(json, id);
            }
            //清除当前白板所有画笔
            if (actionName != null && actionName.equals("ClearAction")) {
                ClearEvent(id, true, fromID, clearActionId);
            }
            if (onShowpageListener != null) {
                onShowpageListener.SetShowPage(mCurrentShareDoc);
            }
        }
    }

    /**
     * 添加信令
     *
     * @param id               信令ID 基本为UUID
     * @param name             信令name
     * @param ts               时间戳 服务器默认添加
     * @param data             信令包含数据
     * @param inList           //在之前还是之后
     * @param fromID           发出者id
     * @param associatedMsgID  与那条信令绑定 如绑定信令删除 此信令删除
     * @param associatedUserID 与那个用户绑定 如绑定用户退出 此信令删除
     * @param jsonObject
     */
    private void onRemotePubMsg(final String id, String name, long ts, Object data, boolean inList, String fromID, String associatedMsgID, String associatedUserID, JSONObject jsonObject) {
        JSONObject jsonData = null;
        if (data != null) {
            try {
                if (data instanceof String) {
                    jsonData = new JSONObject((String) data);
                } else if (data instanceof Map) {
                    jsonData = new JSONObject((Map) data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        switch (name) {
            case "ClassBegin":  // 上课
                acceptClassBegin();
                break;

            case "SharpsChange":  // 添加画笔
                acceptSharpsChange(jsonData, fromID, id, inList);
                break;

            case "ShowPage":  // 文档信令
                acceptShowPage(jsonData);
                break;

            case "BlackBoard_new":  // 小白板初始信令
                acceptBlackBoard_new(jsonData, id);
                break;

            case "UserHasNewBlackBoard":  // 初始状态分发时学生信令
                acceptUserHasNewBlackBoard(jsonData);
                break;

            case "BigRoom":   //收到此条信令代表大并发教室，此时小白板上台人只显示已上台，未上台不显示
                isBigRoom = true;
                break;

            case "CaptureImg":  //客户端截屏
                acceptCaptureImg(jsonData);
                break;

            case "CaptureImgDrag":  //客户端截屏拖动
                acceptCaptureImgDrag(jsonData);
                break;

            case "CaptureImgResize":  //客户端截屏缩放
                acceptCaptureImgResize(jsonData);
                break;

            case "whiteboardMarkTool":  //画笔和鼠标切换
                if (fromID != null) {
                    if (fromID.equals(TKRoomManager.getInstance().getMySelf().peerId) &&
                            TKRoomManager.getInstance().getMySelf().role == 0) {
                        return;
                    }
                    if (TKRoomManager.getInstance().getUser(fromID) != null && TKRoomManager.getInstance().getUser(fromID).role == 2) {
                        return;
                    }
                }
                acceptwhiteboardMarkTool(jsonData);
                break;
        }
    }

    /***
     *    画笔鼠标切换
     */
    private void acceptwhiteboardMarkTool(JSONObject json) {
        if (json != null) {
            boolean selectMouse = json.optBoolean("selectMouse");
            if (mSelectMouseListener != null) {
                mSelectMouseListener.selectMouse(selectMouse);
            }
        }
    }

    /***
     *      截屏放大缩小
     * @param json
     */
    private void acceptCaptureImgResize(JSONObject json) {
        if (json != null) {
            String captureid = json.optString("id");
            //放大系数
            Double scale = json.optDouble("scale");
            if (mCaptureImgInterface != null) {
                mCaptureImgInterface.SetCaptureImgSize(captureid, scale);
            }
        }
    }

    /***
     *    客户端截屏拖动
     * @param json
     */
    private void acceptCaptureImgDrag(JSONObject json) {
        if (json != null) {
            //截图id
            String CaptureImgID = json.optString("id");
            JSONObject object = json.optJSONObject("position");
            //距离左边距离
            Double percentLeft = object.optDouble("percentLeft");
            //距离右边距离
            Double percentTop = object.optDouble("percentTop");
            boolean isDrag = object.optBoolean("isDrag");
            if (mCaptureImgInterface != null) {
                mCaptureImgInterface.SetCaptureImgDrag(CaptureImgID, percentLeft, percentTop, isDrag);
            }
        }
    }

    /***
     *    客户端截屏
     * @param strdata
     */
    private void acceptCaptureImg(JSONObject strdata) {
        if (strdata != null) {
//            if (mCaptureImg == null) {
            mCaptureImg = new CaptureImg();
//            }

            JSONObject jsonInfo = strdata.optJSONObject("captureImgInfo");
            CaptureImg.CaptureImgInfoBean infoBean = new CaptureImg.CaptureImgInfoBean();
            infoBean.setResult(jsonInfo.optInt("result"));
            infoBean.setSwfpath(jsonInfo.optString("swfpath"));
            infoBean.setPagenum(jsonInfo.optInt("pagenum"));
            infoBean.setFileid(jsonInfo.optString("fileid"));
            infoBean.setDownloadpath(jsonInfo.optString("downloadpath"));
            infoBean.setSize(jsonInfo.optInt("size"));
            infoBean.setStatus(jsonInfo.optInt("status"));
            infoBean.setFilename(jsonInfo.optString("filename"));
            infoBean.setDynamicppt(jsonInfo.optInt("dynamicppt"));
            infoBean.setFileprop(jsonInfo.optInt("fileprop"));
            infoBean.setCospdfpath(jsonInfo.optJSONObject("cospdfpath"));
            infoBean.setCospath(jsonInfo.optString("cospath"));
            infoBean.setRealUrl(jsonInfo.optString("realUrl"));
            infoBean.setIsContentDocument(jsonInfo.optInt("isContentDocument"));
            mCaptureImg.setCaptureImgInfo(infoBean);

            JSONObject jsonSize = strdata.optJSONObject("remSize");
            CaptureImg.RemSizeBean sizeBean = new CaptureImg.RemSizeBean();
            sizeBean.setWidth(jsonSize.optDouble("width"));
            sizeBean.setHeight(jsonSize.optDouble("height"));
            mCaptureImg.setRemSize(sizeBean);

            DownloadCaptureImg(mCaptureImg);
        }
    }

    /***
     *    初始状态分发时学生信令
     * @param strdata
     */
    private void acceptUserHasNewBlackBoard(JSONObject strdata) {
        if (strdata != null) {
            StudentListBean studentListBean = new StudentListBean();
            studentListBean.setId(strdata.optString("id"));
            studentListBean.setNickname(strdata.optString("nickname"));
            studentListBean.setRole(strdata.optInt("role"));
            studentListBean.setPublishstate(strdata.optInt("publishstate"));


            if (studentListBean != null) {
                boolean isflage = false;
                for (int i = 0; i < studentListBeans.size(); i++) {
                    if (studentListBeans.get(i).getId().equals(studentListBean.getId())) {
                        isflage = true;
                    }
                }
                //ios会把老师发过来 防止出错
                if (studentListBean.getRole() == 0) {
                    isflage = true;
                }
                if (!isflage) {
                    studentListBeans.add(studentListBean);
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (OnSmallClick != null) {
                            OnSmallClick.setStudents(studentListBeans);
                            if (mSmallPaintDoc != null) {
                                OnSmallClick.setStatus(mSmallPaintDoc);
                            }
                        }
                    }
                });
            }
        }
    }

    /***
     *    小白板初始化信令
     * @param strdata
     * @param id
     */
    private void acceptBlackBoard_new(JSONObject strdata, String id) {
        if (strdata != null) {
            SmallWhiteBoard(id, strdata);
        }
    }

    ShowPageBean currentShareDoc = null;
    ShowPageBean.FiledataBean filedataBean;

    /***
     *    文档信令
     * @param json
     */
    private void acceptShowPage(JSONObject json) {

        if (json == null) {
            return;
        }
        if (currentShareDoc == null) {
            currentShareDoc = new ShowPageBean();
        }
        currentShareDoc.setSourceInstanceId(json.optString("sourceInstanceId"));
        currentShareDoc.setGeneralFile(json.optBoolean("isGeneralFile") &&
                !json.optBoolean("isDynamicPPT") && !json.optBoolean("isH5Document"));
        currentShareDoc.setMedia(json.optBoolean("isMedia"));
        currentShareDoc.setDynamicPPT(json.optBoolean("isDynamicPPT"));
        currentShareDoc.setH5Document(json.optBoolean("isH5Document"));
        currentShareDoc.setAction(json.optString("action"));
        currentShareDoc.setMediaType(json.optString("mediaType"));

        JSONObject filedata = json.optJSONObject("filedata");
        if (currentShareDoc.getFiledata() == null) {
            filedataBean = new ShowPageBean.FiledataBean();
        }
        filedataBean.setCurrpage(filedata.optInt("currpage"));
        filedataBean.setPptslide(filedata.optInt("pptslide"));
        filedataBean.setPptstep(filedata.optInt("pptstep"));
        filedataBean.setSteptotal(filedata.optInt("steptotal"));
        filedataBean.setFileid(filedata.optString("fileid"));
        filedataBean.setPagenum(filedata.optInt("pagenum"));
        filedataBean.setFilename(filedata.optString("filename"));
        filedataBean.setFiletype(filedata.optString("filetype"));
        filedataBean.setIsContentDocument(filedata.optInt("isContentDocument"));
        filedataBean.setSwfpath(filedata.optString("swfpath"));
        filedataBean.setCospdfpath(filedata.optString("cospdfpath"));

        currentShareDoc.setFiledata(filedataBean);
        WhiteBoradConfig.getsInstance().setCurrentFileDoc(Packager.pageDoc(json));

        if (!currentShareDoc.isMedia()) {
            showCourseSelectPage(currentShareDoc);
        }
    }

    /***
     *    添加画笔
     * @param json
     * @param fromID
     * @param id
     */
    private void acceptSharpsChange(JSONObject json, String fromID, String id, boolean inList) {
        if (json == null) {
            return;
        }

        try {
            json.put("fromID", fromID);
            //类型
            String eventType = json.optString("eventType");
            String actionName = json.optString("actionName");
            String whiteboardID = json.optString("whiteboardID");
            String clearActionId = json.optString("clearActionId");
            if (eventType != null && eventType.equals("shapeSaveEvent")) {   //添加画笔动作
                if (actionName != null && actionName.equals("AddShapeAction")) {
                    AssembleBrush(json, id, false, inList);
                }
            } else if (eventType.equals("clearEvent")) {   //恢复和清除
                if (actionName.equals("ClearAction")) {
                    ClearEvent(id, false, fromID, clearActionId);

                }
            } else if (eventType != null && eventType.equals("redoEvent")) {
                if (actionName != null && actionName.equals("AddShapeAction")) {
                    //恢复时重新添加画笔 移除上次画笔数据 再次撤销时，撤销id为此次添加id
                    String authorUserId = json.getJSONObject("otherInfo").getString("authorUserId");
                    json.put("fromID", authorUserId);
                    AssembleBrush(json, id, true, inList);
                } else if (actionName != null && actionName.equals("ClearAction")) {
                    ClearEvent(id, false, fromID, clearActionId);
                }
                //荧光笔信令
            } else if (eventType != null && eventType.equals("laserMarkEvent")) {
                if (!id.contains("###")) return;
                String[] str = id.split("_");
                String Mapkey = str[2] + "-" + str[3];
                String docid = "0";
                if (mCurrentShareDoc != null) {
//                    docid = mCurrentShareDoc.getFiledata().getFileid() + "-" + mCurrentShareDoc.getFiledata().getCurrpage();
                    docid = mCurrentShareDoc.getFiledata().getFileid();
                }
                if (actionName.equals("show")) {    //显示
                    if (mLaserPenBean == null) {
                        mLaserPenBean = new LaserPenBean();
                        mLaserPenBean.mapKey = Mapkey;
                    }
                }
                if (actionName.equals("move")) {    //移动
                    if (mLaserPenBean == null) return;
                    if (!Mapkey.equals(mLaserPenBean.mapKey)) {
                        return;
                    }
                    //移动时坐标
                    JSONObject laser = json.optJSONObject("laser");
                    double left = laser.optDouble("left");
                    double top = laser.optDouble("top");
                    mLaserPenBean.left = left;
                    mLaserPenBean.top = top;
                    if (str[2].equals(docid)) {
                        //刷新课件荧光笔
                        mTopDataChangeList.onRefresh();
                    }
                    if (!topScreenChangeListenermap.isEmpty() && topScreenChangeListenermap.containsKey(Mapkey)) {
                        topScreenChangeListenermap.get(Mapkey).onRefresh();
                    }
                }
                //隐藏
                if (actionName.equals("hide")) {
                    mLaserPenBean = null;
                    //刷新课件荧光笔
                    if (str[2].equals(docid)) {
                        mTopDataChangeList.onRefresh();
                    }
                    //如果key和截图的相等 刷新荧光笔
                    if (!topScreenChangeListenermap.isEmpty() && topScreenChangeListenermap.containsKey(Mapkey)) {
                        topScreenChangeListenermap.get(Mapkey).onRefresh();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     *  接受上课信令
     */
    private void acceptClassBegin() {
        //上课后获取一次老师用户
        getTeacherUser();
        if (onShowpageListener != null) {
            onShowpageListener.setViewState();
        }
    }

    /***
     *    显示页面
     * @param currentShareDoc
     */
    public void displayPage(ShowPageBean currentShareDoc) {
        if (onShowpageListener != null) {
            onShowpageListener.setShowPageBean(currentShareDoc);
        }

        if (currentShareDoc != null) {
            DownLoadFile(currentShareDoc);
        }

        if (onShowpageListener != null) {
            onShowpageListener.SetShowPage(currentShareDoc);
        }
    }

    public void showCourseSelectPage(ShowPageBean shareDoc) {
        mCurrentShareDoc = shareDoc;
        displayPage(shareDoc);
    }

    /**
     * 老师user
     *
     * @return
     */
    public RoomUser getTeacherUser() {
        for (RoomUser user : TKRoomManager.getInstance().getUsers().values()) {
            if (user.role == 0) {
                mTeacherUser = user;
                return mTeacherUser;
            }
        }
        return null;
    }

    /**
     * 处理小白板
     *
     * @param id
     * @param strdata
     */
    private void SmallWhiteBoard(String id, JSONObject strdata) {
        if (mSmallPaintDoc == null) {
            mSmallPaintDoc = new SmallPaintBean();
        }
        mSmallPaintDoc.setCurrentTapKey(strdata.optString("currentTapKey"));
        mSmallPaintDoc.setBlackBoardState(strdata.optString("blackBoardState"));
        mSmallPaintDoc.setCurrentTapPage(strdata.optInt("currentTapPage"));

        if (mSmallPaintDoc != null) {
            //添加学生数据 及 发送小白板状态至pop
            sendStatus(mSmallPaintDoc);
            //初始化状态
            if (mSmallPaintDoc.getBlackBoardState().equals("_prepareing") && mSmallPaintDoc.getCurrentTapKey().equals("blackBoardCommon")) {
                //开始分发状态 信令先返回老师   这时保存老师未分发前基础数据
            } else if (mSmallPaintDoc.getBlackBoardState().equals("_dispenseed") /*&& mSmallPaintDoc.getCurrentTapKey().equals("blackBoardCommon")*/) {
                if (OnSmallClick != null && !isAddTeacher) {
                    isAddTeacher = true;
                    OnSmallClick.setTeacher(new StudentListBean("blackBoardCommon", "老师", 0));
                }
                SendStudent();
                if (mDataChangeListSmall != null) {
                    mDataChangeListSmall.onChange();
                }
            } else if (mSmallPaintDoc.getBlackBoardState().equals("_recycle")) {
                SendStudent();
                //如果是回收状态  老师可以在画板操作 学生不能操作 ，不能点击翻页及任何操作，只能看
                if (mDataChangeListSmall != null) {
                    mDataChangeListSmall.onChange();
                }
            } else if (mSmallPaintDoc.getBlackBoardState().equals("_againDispenseed")) {
                SendStudent();
                if (mDataChangeListSmall != null) {
                    mDataChangeListSmall.onChange();
                }
            }
        }
    }

    public void SendStudent() {
        RoomUser user = TKRoomManager.getInstance().getMySelf();
        //当老师和学生的时候
        if (user.role == 2 || user.role == 0) {
            boolean isflage = false;
            if (studentListBeans.size() > 0) {
                //循环刷新学生集合中是否包含自己，如果包含，不再发出自己
                for (int i = 0; i < studentListBeans.size(); i++) {
                    if (studentListBeans.get(i).getId().equals(user.peerId)) {
                        isflage = true;
                        break;
                    }
                }
            }
            //不包含  发出自己
            if (!isflage) {
                //发出自己确认是否是大并发，并且已上台，未上台不发出自己
                if (!isBigRoom || user.publishState > 0) {
                    Map<String, Object> stuprepareing = new HashMap<>();
                    stuprepareing.put("role", user.role);
                    stuprepareing.put("id", user.role == 0 ? "blackBoardCommon" : user.peerId);
                    stuprepareing.put("nickname", user.nickName);
                    if (user.publishState > 0) {
                        stuprepareing.put("publishstate", user.publishState);
                    }
                    TKRoomManager.getInstance().pubMsg("UserHasNewBlackBoard", "_" + user.peerId,
                            "__all", new JSONObject(stuprepareing).toString(), true, "BlackBoard_new", user.peerId);
                }
            }
        }
    }

    /**
     * pop状态
     *
     * @param mSmallPaintDoc
     */
    public synchronized void sendStatus(final SmallPaintBean mSmallPaintDoc) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (OnSmallClick != null) {
//                    OnSmallClick.setStudents(studentListBeans);
                    OnSmallClick.setStatus(mSmallPaintDoc);
                }
            }
        });
    }

    /***
     *    下载文档
     * @param mCurrentShareDoc
     */
    private void DownLoadFile(ShowPageBean mCurrentShareDoc) {
        //判断是否是普通文档
        if (mCurrentShareDoc.isGeneralFile()) {
            //是否是白板
            if (!mCurrentShareDoc.getFiledata().getFileid().equals("0")) {
                mapKey = mCurrentShareDoc.getFiledata().getFileid() + "-" + mCurrentShareDoc.getFiledata().getCurrpage();
                if (!mPath.containsKey(mapKey)) {
                    //图片地址
                    String file = mCurrentShareDoc.getFiledata().getSwfpath();

                    //.svg 图片H5显示
                    if (file.endsWith(".svg") || file.endsWith(".gif") || file.endsWith(".html")) {
                        if (mShowWbFragmentViewListener != null) {
                            mShowWbFragmentViewListener.onShow(mCurrentShareDoc);
                        }
                        if (mDataChangeList != null) {
                            mDataChangeList.onChange();
                        }
                    } else {
                        if (mDownPath.contains(mapKey)) {
                            return;
                        }
                        mDownPath.add(mapKey);
                        String[] files = file.split("\\.");
                        if (files.length != 2) return;
                        String phonefile = files[0] + "-" + mCurrentShareDoc.getFiledata().getCurrpage() + "." + files[1];
                        String fileaddress = "https://" + getDocServerAddr() + "/" + phonefile;
                        FileDownLoad.getInstance().delegate = this;
                        FileDownLoad.getInstance().start(mPath, mContext, mCurrentShareDoc, fileaddress);
                    }
                } else {
                    if (mShowWbFragmentViewListener != null) {
                        mShowWbFragmentViewListener.onHide();
                    }
                    Bitmap bitmap = getCurrentImage();
                    if (bitmap != null) {
                        double ratio = bitmap.getWidth() * 1.0d / bitmap.getHeight();
                        WhiteBroadActionBean bean = new WhiteBroadActionBean();
                        bean.setScale(2);
                        bean.setIrregular(ratio);

                        WhiteBroadActionBean.Page page = new WhiteBroadActionBean.Page();
                        page.setCurrentPage(mCurrentShareDoc.getFiledata().getCurrpage());
                        page.setTotalPage(mCurrentShareDoc.getFiledata().getPagenum());
                        bean.setPage(page);

                        //发送 action 设置图片宽高
                        WhiteBoradManager.getInstance().onWhiteBoradReceiveActionCommand(bean.toString());
                    }

                    if (mDataChangeList != null) {
                        mDataChangeList.onChange();
                    }
                }
            } else {
                if (mShowWbFragmentViewListener != null) {
                    mShowWbFragmentViewListener.onHide();
                }

                if (mDataChangeList != null) {
                    mDataChangeList.onChange();
                }

                WhiteBroadActionBean bean = new WhiteBroadActionBean();
                bean.setScale(1);
                WhiteBroadActionBean.Page page = new WhiteBroadActionBean.Page();
                page.setCurrentPage(mCurrentShareDoc.getFiledata().getCurrpage());
                page.setTotalPage(mCurrentShareDoc.getFiledata().getPagenum());
                bean.setPage(page);
                WhiteBoradManager.getInstance().onWhiteBoradReceiveActionCommand(bean.toString());
            }
        } else {   //动态PPT 或者H5课件
            if (mShowWbFragmentViewListener != null) {
                mShowWbFragmentViewListener.onShow(mCurrentShareDoc);
            }
            if (mDataChangeList != null) {
                mDataChangeList.onChange();
            }
        }
        ShowPageBean.FiledataBean filedata = mCurrentShareDoc.getFiledata();
        PPTRemarkUtil.getInstance().getPPTRemark(host, filedata.getFileid(), filedata.getCurrpage() - 1);
    }

    /**
     * 下载截图文件
     *
     * @param mCaptureImg 截图实体类
     */
    private void DownloadCaptureImg(CaptureImg mCaptureImg) {
        if (mCaptureImg != null) {

            //图片地址
            String file = mCaptureImg.getCaptureImgInfo().getSwfpath();

            String[] files = file.split("\\.");
            if (files.length != 2) return;
            String phonefile = files[0] + "-1" + "." + files[1];
            //图片地址
            String fileaddress = "https://" + getDocServerAddr() + "/" + phonefile;
            FileDownLoad.getInstance().delegate = this;
            FileDownLoad.getInstance().start(mContext, mCaptureImg, fileaddress);
        }
    }

    /**
     * 清除或恢复当前页所有画笔
     *
     * @param id      信令id
     * @param isClear true 恢复本页所有画笔   false 清除本页所有画笔  注（如果本页已花10笔，清除只是10笔，
     *                再花5笔，清除只是5笔 ，点恢复只恢复5笔数据，不恢复15笔）
     */
    private void ClearEvent(String id, boolean isClear, String fromID, String clearActionId) {
        if (id.contains("###")) {
            String[] strs = id.split("_");
            if (strs.length != 5) return;
            String Mapkey = strs[3] + "-" + strs[4];
            if (mSumLetty.isEmpty()) return;
            RoomUser roomUser = TKRoomManager.getInstance().getUser(fromID);
            if (mSumLetty.containsKey(Mapkey)) {
                List<TL_PadAction> listaction = mSumLetty.get(Mapkey);
                boolean isFlage = false;
                boolean isFlage1 = false;
                if (listaction.size() > 0) {

                    for (int i = 0; i < listaction.size(); i++) {
                        TL_PadAction action = listaction.get(i);
                        if (roomUser != null && roomUser.role == 2) {
                            if (action.fromID.equals(fromID)) {

                                if (isClear) {
                                    action.mClear--;
                                } else {
                                    action.clearActionId = clearActionId;
                                    action.mClear++;
                                }
                            }
                        } else {
                            if (isClear) {
                                //第一次循环，为了查找界面中是否有id相同的数据，如果有只对id相同数据操作，没有，操作全部，同下
                                if (action.clearActionId.equals(clearActionId)) {
                                    action.mClear--;
                                    isFlage = true;
                                }
                                if (i == listaction.size() - 1) {
                                    if (!isFlage) {
                                        for (int j = 0; j < listaction.size(); j++) {
                                            TL_PadAction action1 = listaction.get(j);
                                            if (isClear) {
                                                action1.mClear--;
                                            } else {
                                                action1.mClear++;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (action.clearActionId.equals(clearActionId)) {
                                    action.mClear++;
                                    isFlage1 = true;
                                }
                                if (i == listaction.size() - 1) {
                                    if (!isFlage1) {
                                        for (int j = 0; j < listaction.size(); j++) {
                                            TL_PadAction action1 = listaction.get(j);
                                            if (isClear) {
                                                action1.mClear--;
                                            } else {
                                                action1.mClear++;
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                    if (mDataChangeList != null) {
                        mDataChangeList.onChange();
                    }
                }
            }
        }
    }

    /***
     *    撤销画笔
     * @param json
     * @param id
     */
    private void undoEventBrush(JSONObject json, String id) {
        String shapeId = json.optString("shapeId");
        if (!id.contains("###")) return;
        String[] str = id.split("_");
        String Mapkey = str[2] + "-" + str[3];
        if (mSumLetty.isEmpty()) return;
        if (!mSumLetty.containsKey(Mapkey)) return;
        List<TL_PadAction> listaction = mSumLetty.get(Mapkey);
        if (listaction.size() > 0) {
            for (int i = listaction.size() - 1; i >= 0; i--) {
                if (shapeId.equals(listaction.get(i).id)) {
                    listaction.get(i).isDraw = false;
                    break;
                }
            }
            if (mDataChangeList != null) {
                mDataChangeList.onChange();
            }
        }
    }

    /***
     *        添加画笔
     * @param data
     * @param sharpid
     * @param isRedo
     */
    private void AssembleBrush(JSONObject data, String sharpid, boolean isRedo, boolean inList) {
        TL_PadAction action = new TL_PadAction();
        String shapeId = data.optString("shapeId");
        String whiteboardID = data.optString("whiteboardID");
        String nickname = data.optString("nickname");
        String fromId = data.optString("fromID");
        String codeID = data.optString("codeID");
        //是否小白板基础数据
        boolean isBaseboard = data.optBoolean("isBaseboard");
        if (!sharpid.contains("###")) return;
        String[] str = sharpid.split("_");
        String Mapkey = str[2] + "-" + str[3];
        if (str.length != 4) return;

        String strData = data.optString("data");
        JSONObject brushjson = null;
        try {
            brushjson = new JSONObject(strData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (brushjson == null) {
            return;
        }
        String classname = brushjson.optString("className");

        switch (classname) {
            case "Line":   //线 2个点的直线
                JSONObject XYjson = brushjson.optJSONObject("data");
                float x1 = (float) XYjson.optDouble("x1");
                float y1 = (float) XYjson.optDouble("y1");
                float x2 = (float) XYjson.optDouble("x2");
                float y2 = (float) XYjson.optDouble("y2");

                int strokeWidth = XYjson.opt("strokeWidth") instanceof String ? Integer.parseInt(XYjson.optString("strokeWidth")) : XYjson.optInt("strokeWidth");

                String color = XYjson.optString("color");
                String capStyle = XYjson.optString("capStyle");
                String dash = XYjson.optString("dash");
                JSONArray jsonArray = (JSONArray) XYjson.opt("endCapShapes");
                action.sID = codeID != null ? codeID : "";
                action.nDocID = str[2];
                action.id = shapeId;
                action.nickname = nickname;
                action.fromID = fromId;
                action.isDraw = true;
                action.mClear = 0;
                action.nPage = str[3];
                action.inList = inList;
                action.nPenWidth = strokeWidth;
                action.whiteboardID = whiteboardID;
                if (jsonArray.length() != 2) return;

                action.nActionMode = jsonArray.opt(1) != null && jsonArray.opt(1).equals("arrow") ? TL_PadAction.factoryType.ft_arrowLine : TL_PadAction.factoryType.ft_line;

                action.nPenColor = Color.parseColor(color);
                PointF xy1 = new PointF(), xy2 = new PointF();
                xy1.x = x1;
                xy1.y = y1;
                xy2.x = x2;
                xy2.y = y2;
                action.points.add(xy1);
                action.points.add(xy2);
                //                UnWhithXYLine(action);
                StoreAction(action, sharpid, isRedo);

                break;
            case "LinePath":   // 所有坐标点的线
            case "ErasedLinePath": //橡皮擦
                JSONObject LineJson = brushjson.optJSONObject("data");
                action.nPenWidth = LineJson.opt("pointSize") instanceof String ? Integer.parseInt(LineJson.optString("pointSize")) : LineJson.optInt("pointSize");
                if (classname.equals("LinePath")) {
                    String colorpoint = LineJson.optString("pointColor");
                    if (colorpoint.contains("#")) {
                        action.nPenColor = Color.parseColor(colorpoint);
                        action.bIsFill = true;
                    } else if (colorpoint.startsWith("rgba")) {
                        action.bIsFill = false;
                        String rgba = colorpoint.substring(colorpoint.indexOf("(") + 1, colorpoint.indexOf(")"));
                        if (rgba.length() > 0) {
                            String[] rgbas = rgba.split(",");
                            if (rgbas.length == 4) {
                                int a = rgbas[3].equals("0.5") ? 80 : 80;//默认全80 半透明
                                int r = Integer.parseInt(rgbas[0]);
                                int g = Integer.parseInt(rgbas[1]);
                                int b = Integer.parseInt(rgbas[2]);
                                String argb = ColorUtils.toHexArgb(a, r, g, b);
                                if (argb != null) {
                                    action.nPenColor = Color.parseColor(argb);
                                }
                            }
                        }
                    } else {
                        break;
                    }
                } else if (classname.equals("ErasedLinePath")) {
                    action.nPenColor = Color.parseColor("#000000");
                }
                action.sID = codeID != null ? codeID : "";
                action.nDocID = str[2];
                action.id = shapeId;
                action.nPage = str[3];
                action.nickname = nickname;
                action.fromID = fromId;
                action.isDraw = true;
                action.mClear = 0;
                action.inList = inList;
                action.baseboard = isBaseboard;
                action.whiteboardID = whiteboardID;
                action.nActionMode = classname.equals("LinePath") ? TL_PadAction.factoryType.ft_markerPen : TL_PadAction.factoryType.ft_Eraser;
//                JSONArray pointCoordinatePairs = JSON.parseArray(LineJson.optString("pointCoordinatePairs"));
                JSONArray pointCoordinatePairs = null;
                try {
                    pointCoordinatePairs = new JSONArray(LineJson.optString("pointCoordinatePairs"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (pointCoordinatePairs != null) {
                    for (int i = 0; i < pointCoordinatePairs.length(); i++) {
                        JSONArray ptfs = pointCoordinatePairs.optJSONArray(i);
                        PointF p = new PointF();
                        p.x = (float) ptfs.optDouble(0);
                        p.y = (float) ptfs.optDouble(1);
                        action.points.add(p);
                        //                    action.alActionPoint.add(UnWhithXYLinePath(p));
                    }
                    StoreAction(action, sharpid, isRedo);
                }
                break;

            case "Rectangle":  //矩形
            case "Ellipse":    //椭圆
                JSONObject RectJson = brushjson.optJSONObject("data");
                action.nPenWidth = RectJson.opt("strokeWidth") instanceof String ? Integer.parseInt(RectJson.optString("strokeWidth")) : RectJson.optInt("strokeWidth");
                action.nPenColor = Color.parseColor(RectJson.optString("strokeColor"));
                action.sID = codeID != null ? codeID : "";
                action.nDocID = str[2];
                action.id = shapeId;
                action.nickname = nickname;
                action.fromID = fromId;
                action.isDraw = true;
                action.mClear = 0;
                action.nPage = str[3];
                action.inList = inList;
                action.whiteboardID = whiteboardID;
                action.nActionMode = classname.equals("Rectangle") ? TL_PadAction.factoryType.ft_Rectangle : TL_PadAction.factoryType.ft_Ellipse;
                action.bIsFill = RectJson.optString("fillColor").equals("transparent") ? false : true;
                PointF startF = new PointF(), stopF = new PointF();
                startF.x = (float) RectJson.optDouble("x");
                startF.y = (float) RectJson.optDouble("y");
                stopF.x = (float) RectJson.optDouble("width");
                stopF.y = (float) RectJson.optDouble("height");
                action.points.add(startF);
                action.points.add(stopF);
                //                UnWhithXYLine(action);
                StoreAction(action, sharpid, isRedo);
                break;
            case "Text": //文本

                JSONObject TextJson = brushjson.optJSONObject("data");
                String font = TextJson.optString("font");
                String[] str1 = font.split(" ");
                String font1 = null;
                if (str1.length == 4) {
                    font1 = !str1[2].isEmpty() && str1[2] != null ? str1[2] : "18px";
                    if (font1.endsWith("px")) {
                        font1 = font1.substring(0, font1.indexOf("p"));
                    }
                }
                action.isDraw = true;
                action.mClear = 0;
                action.nPenWidth = Integer.parseInt(font1 == null ? "18" : font1);
                action.nPenColor = Color.parseColor(TextJson.optString("color"));
                action.sID = codeID != null ? codeID : "";
                action.nDocID = str[2];
                action.fromID = fromId;
                action.id = shapeId;
                action.nickname = nickname;
                action.nPage = str[3];
                action.inList = inList;
                action.baseboard = isBaseboard;
                action.whiteboardID = whiteboardID;
                action.nActionMode = TL_PadAction.factoryType.ft_Text;
                action.bIsFill = true;
                action.sText = TextJson.optString("text");
                PointF ttF = new PointF();
                ttF.x = (float) TextJson.optDouble("x");
                ttF.y = (float) TextJson.optDouble("y");
                action.points.add(ttF);
                //UnWhithXYLine(action);
                StoreAction(action, sharpid, isRedo);
                break;
        }
    }

    /***
     *    画笔数据存储
     * @param action
     * @param sharpid
     * @param isRedo
     */
    private void StoreAction(TL_PadAction action, String sharpid, boolean isRedo) {
        if (action != null) {
            //数据分类
            //正常画板所有数据
            if (action.whiteboardID.equals("default")) {
                AddDefaultMap(action, sharpid, isRedo);
            } else if (action.whiteboardID.equals("videoDrawBoard")) {
                ADDVideoMap(action, sharpid, isRedo);
                //whiteboardID 命名为whiteboardID+图片fileid
            } else if (action.whiteboardID.startsWith("captureImgBoard")) {
                AddScreenMap(action, sharpid, isRedo);
            } else {
                //小白板所有数据
                ADDSmallMap(action, sharpid, isRedo);
            }
        }
    }

    /**
     * 截图画笔
     *
     * @param action  画笔
     * @param sharpid 消息id
     * @param isRedo  是否是撤销添加
     */
    private void AddScreenMap(TL_PadAction action, String sharpid, boolean isRedo) {
        if (!sharpid.contains("###")) return;
        String[] str = sharpid.split("_");
        String Mapkey = str[2] + "-" + str[3];
        if (mSumLettyScreen.isEmpty()) {
            List<TL_PadAction> padActions = new ArrayList<>();
            padActions.add(action);
            mSumLettyScreen.put(Mapkey, padActions);
            if (!screenChangeListenermap.isEmpty() && screenChangeListenermap.containsKey(Mapkey)) {
                DataChangeListener dataChangeListener = screenChangeListenermap.get(Mapkey);
                if (dataChangeListener != null) {
                    dataChangeListener.onChange();
                }
            }
        } else {
            if (mSumLettyScreen.containsKey(Mapkey)) {
                List<TL_PadAction> listaction = mSumLettyScreen.get(Mapkey);
                if (listaction.size() <= 0) return;
                Iterator<TL_PadAction> tl_action = listaction.iterator();
                boolean bHas = false;
                while (tl_action.hasNext()) {
                    TL_PadAction padAction = tl_action.next();
                    if (action.id.equals(padAction.id)) {
                        if (isRedo) {
                            tl_action.remove();  //画笔ID
                        } else {
                            bHas = true;
                        }
                        break;
                    }
                }

                if (!bHas) {
                    listaction.add(action); //重新赋值画笔ID
                    if (!screenChangeListenermap.isEmpty() && screenChangeListenermap.containsKey(Mapkey)) {
                        DataChangeListener dataChangeListener = screenChangeListenermap.get(Mapkey);
                        if (dataChangeListener != null) {
                            dataChangeListener.onChange();
                        }
                    }
                }
            } else {
                List<TL_PadAction> padActions = new ArrayList<>();
                padActions.add(action);
                mSumLettyScreen.put(Mapkey, padActions);
                if (!screenChangeListenermap.isEmpty() && screenChangeListenermap.containsKey(Mapkey)) {
                    DataChangeListener dataChangeListener = screenChangeListenermap.get(Mapkey);
                    if (dataChangeListener != null) {
                        dataChangeListener.onChange();
                    }
                }
            }
        }

    }

    /**
     * 小白板数据存储
     *
     * @param action  画笔
     * @param sharpid 消息id
     * @param isRedo  是否是撤销添加
     */
    private void ADDSmallMap(TL_PadAction action, String sharpid, boolean isRedo) {
        if (!sharpid.contains("###")) return;
        String[] str = sharpid.split("_");
        String Mapkey = str[2] + "-" + str[3];
        //如果画笔数据为blackBoardCommon  添加为小白板基础数据
        if (action.baseboard & action.whiteboardID.equals("blackBoardCommon")) {
            mBasePaint.add(action);
        }
        if (mSumLettySmall.isEmpty()) {
            List<TL_PadAction> padActions = new ArrayList<>();
            padActions.add(action);

            //添加到小白板集合
            mSumLettySmall.put(Mapkey, padActions);
            if (mDataChangeListSmall != null) {
                mDataChangeListSmall.onChange();
            }

        } else {
            if (mSumLettySmall.containsKey(Mapkey)) {
                List<TL_PadAction> listaction = mSumLettySmall.get(Mapkey);
                if (listaction.size() <= 0) {
                    List<TL_PadAction> padActions = new ArrayList<>();
                    padActions.add(action);
                    //添加到小白板集合
                    mSumLettySmall.put(Mapkey, padActions);
                    if (mDataChangeListSmall != null) {
                        mDataChangeListSmall.onChange();

                    }
                } else {
                    Iterator<TL_PadAction> tl_action = listaction.iterator();
                    boolean bHas = false;
                    while (tl_action.hasNext()) {
                        if (action.id.equals(tl_action.next().id)) {
                            if (isRedo) {
                                tl_action.remove();  //画笔ID
                            } else {
                                bHas = true;
                            }
                            break;
                        }
                    }

                    if (!bHas) {
                        listaction.add(action); //重新赋值画笔ID
                        if (mDataChangeListSmall != null) {
                            mDataChangeListSmall.onChange();
                        }
                    }
                }
            } else {
                List<TL_PadAction> padActions = new ArrayList<>();
                padActions.add(action);
                mSumLettySmall.put(Mapkey, padActions);
                if (mDataChangeListSmall != null) {
                    mDataChangeListSmall.onChange();
                }
            }
        }
    }

    /**
     * 视频标注数据存储
     *
     * @param action
     * @param sharpid
     * @param isRedo
     */
    private void ADDVideoMap(TL_PadAction action, String sharpid, boolean isRedo) {
        if (!sharpid.contains("###")) return;
        String[] str = sharpid.split("_");
        String Mapkey = str[2] + "-" + str[3];
        if (mSumLettyVideo.isEmpty()) {
            List<TL_PadAction> padActions = new ArrayList<>();
            padActions.add(action);
            //添加到视频白板集合
            mSumLettyVideo.put(Mapkey, padActions);
            if (mDataChangeListVideo != null) {
                mDataChangeListVideo.onChange();
            }

        } else {
            if (mSumLettyVideo.containsKey(Mapkey)) {
                List<TL_PadAction> listaction = mSumLettyVideo.get(Mapkey);
                if (listaction.size() <= 0) return;
                Iterator<TL_PadAction> tl_action = listaction.iterator();
                boolean bHas = false;
                while (tl_action.hasNext()) {
                    if (action.id.equals(tl_action.next().id)) {
                        if (isRedo) {
                            tl_action.remove();  //画笔ID
                        } else {
                            bHas = true;
                        }
                        break;
                    }
                }
                if (!bHas) {
                    listaction.add(action); //重新赋值画笔ID
                    if (mDataChangeListVideo != null) {
                        mDataChangeListVideo.onChange();
                    }
                }
            } else {
                List<TL_PadAction> padActions = new ArrayList<>();
                padActions.add(action);
                mSumLettyVideo.put(Mapkey, padActions);
                if (mDataChangeListVideo != null) {
                    mDataChangeListVideo.onChange();
                }
            }
        }
    }

    /**
     * 默认白板数据
     *
     * @param action
     * @param sharpid
     * @param isRedo
     */
    private void AddDefaultMap(TL_PadAction action, String sharpid, boolean isRedo) {
        if (!sharpid.contains("###")) return;
        String[] str = sharpid.split("_");
        String Mapkey = str[2] + "-" + str[3];
        if (mSumLetty.isEmpty()) {
            List<TL_PadAction> padActions = new ArrayList<>();
            padActions.add(action);
            mSumLetty.put(Mapkey, padActions);
            if (mDataChangeList != null) {
                mDataChangeList.onChange();
            }
        } else {
            if (mSumLetty.containsKey(Mapkey)) {
                List<TL_PadAction> listaction = mSumLetty.get(Mapkey);
                if (listaction.size() <= 0) return;
                Iterator<TL_PadAction> tl_action = listaction.iterator();
                boolean bHas = false;
                while (tl_action.hasNext()) {
                    TL_PadAction tl_padAction = tl_action.next();
                    if (action.id.equals(tl_padAction.id)) {
                        if (isRedo) {
                            if (tl_padAction.clearActionId != null || !tl_padAction.clearActionId.equals("")) {
                                action.clearActionId = tl_padAction.clearActionId;
                            }
                            tl_action.remove();  //画笔ID
                            action.inList = true;
                        } else {
                            bHas = true;
                        }
                        break;
                    }
                }

                if (!bHas) {
                    listaction.add(action); //重新赋值画笔ID
                    if (mDataChangeList != null) {
                        mDataChangeList.onChange();
                    }
                }
            } else {
                List<TL_PadAction> padActions = new ArrayList<>();
                padActions.add(action);
                mSumLetty.put(Mapkey, padActions);
                if (mDataChangeList != null) {
                    mDataChangeList.onChange();
                }
            }
        }
    }

    public ArrayList<String> getDocServerAddrBackupList() {
        return DocServerAddrBackupList;
    }

    public String getDocServerAddr() {
        return DocServerAddr;
    }

    public String getDocServerAddrBackup() {
        return DocServerAddrBackup;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


    public void setOnuserjoinClick(SmallBoardInterface onuserjoinClick) {
        OnSmallClick = onuserjoinClick;
    }

    /***
     *   重置RoomSession状态
     */
    public void resetSharePadMgr() {
        mPath.clear();
    }
}
