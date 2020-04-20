package com.classroomsdk.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.classroomsdk.bean.TL_PadAction;
import com.classroomsdk.interfaces.FaceShareControl;
import com.classroomsdk.interfaces.WhitePadInterface;
import com.classroomsdk.manage.SharePadMgr;
import com.classroomsdk.utils.ColorUtils;

import java.util.HashMap;
import java.util.List;

/**
 * 视频标注画板工具
 */

public class VideoPaint extends View implements SharePadMgr.DataChangeListenerVideo, SharePadMgr.TopDataChangeListenerVideo {
    private TL_PadAction m_tl_CurrentPadAction = null;

    private TL_PadAction.factoryType m_nActionMode = null;

    private Context mContext;

    private boolean m_bActionfill;
    private int m_nPenWidth = 10;
    private int m_nPenColor = 0xffED3E3A;
    private RectF m_rcOriginBK = new RectF();
    private int m_nOldWidth;
    private int m_nOldHeight;
    private RectF m_rcBK = new RectF();
    private RectF m_orgRcBK = null;
    private int m_nBitHashCode = 0;
    private static final int ActionBorder = 15;
    private FaceShareControl m_iSync;
    private SharePadMgr m_thisPadMgr;
    private float dbZoomScale = (float) 1.000000;
    private String UUID;

    private boolean requestParentFocus;
    private TouchHandler mTouchHandler;

    //工具类型
    private ToolsType mToolsType = ToolsType.pen;

    //笔类型
    private ToolsPenType mToolsPenType = ToolsPenType.fountainPen;
    //笔颜色
    private int mToolsPenColor = 0xffED3E3A;
    //笔宽度
    private int mToolsPenProgress = 10;

    //橡皮宽高
    private int mToolsEraserWidth = 10;

    //turn是顶层画笔  false是底部接受信令绘制类
    private boolean isDisplayDraw = false;

    public VideoPaint(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //画板大小模式
    int padSizeMode = 2;
    //视频宽高
    int videoWid = 0;
    int videoHid = 0;

    public void setPadSize(int padSizeMode) {
        this.padSizeMode = padSizeMode;
    }

    public void setPadSizeAndMode(int padSizeMode, int videoWid, int videoHid) {
        this.padSizeMode = padSizeMode;
        this.videoWid = videoWid;
        this.videoHid = videoHid;
        CheckBkImageSize();
        postInvalidate();
    }

    public void CheckBkImageSize() {
        m_nOldHeight = this.getHeight();
        m_nOldWidth = this.getWidth();

        int nimageHeight = Math.min(m_nOldHeight, m_nOldWidth);
        int nimagew = nimageHeight;

        if (padSizeMode == 1) {
            nimageHeight = nimageHeight / 4 * 3;
        } else if (padSizeMode == 2) {
            nimageHeight = nimageHeight / 16 * 9;
        } else if (padSizeMode == 3) {
            m_rcOriginBK.left = (m_nOldWidth - videoWid) / 2;
            m_rcOriginBK.right = m_rcOriginBK.left + videoWid;
            m_rcOriginBK.top = (m_nOldHeight - videoHid) / 2;
            m_rcOriginBK.bottom = m_rcOriginBK.top + videoHid;
            m_rcBK = new RectF(m_rcOriginBK);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = (int) m_rcBK.width();
            params.height = (int) m_rcBK.height();
            setLayoutParams(params);
            return;
        }

        if (m_nOldHeight == 0 || nimageHeight == 0 || m_nOldWidth == 0 || nimagew == 0) return;
        double bkness = m_nOldWidth * 1.0 / m_nOldHeight;
        double imageness = nimagew * 1.0 / nimageHeight;

        double realness = 0;

        if (imageness > bkness) {
            realness = nimagew * 1.0 / m_nOldWidth;
            double realHeight = nimageHeight * 1.0 / realness;
            m_rcOriginBK.left = 0;
            m_rcOriginBK.right = m_nOldWidth;
            m_rcOriginBK.top = (float) (Math.abs(m_nOldHeight - realHeight) / 2);
            m_rcOriginBK.bottom = (float) (m_rcOriginBK.top + realHeight);

        } else {
            realness = nimageHeight * 1.0 / m_nOldHeight;
            double realWidth = nimagew * 1.0 / realness;
            m_rcOriginBK.left = (float) (Math.abs(m_nOldWidth - realWidth) / 2);
            m_rcOriginBK.right = (float) (m_rcOriginBK.left + realWidth);
            m_rcOriginBK.top = 0;
            m_rcOriginBK.bottom = m_nOldHeight;//m_rcBK.top + realHeight;
        }
        m_rcBK = new RectF(m_rcOriginBK);
        dbZoomScale = (float) 1.000000;
    }

    public void SetAction(TL_PadAction.factoryType nAction, boolean bisFIll) {
        m_nActionMode = nAction;
        m_bActionfill = bisFIll;
    }

    @Override
    public void onDraw(Canvas cvs) {
        if (this.isInEditMode()) return;
        int nHeight = this.getHeight();
        int nWidth = this.getWidth();

        if (nHeight != m_nOldHeight || nWidth != m_nOldWidth) {
            CheckBkImageSize();
        }
        if (PaintBk(cvs)) {
            PaintActions(cvs);
        }
    }

    boolean PaintBk(Canvas cvs) {
        //是顶层绘制 只需要透明绘制层
        if (isDisplayDraw) {
            if (m_rcBK.isEmpty()) CheckBkImageSize();
            Paint linePaint = new Paint();
            linePaint.setColor(Color.TRANSPARENT);
            cvs.drawRect(m_rcBK, linePaint);
            return true;
        } else {
            Paint linePaint = new Paint();
            linePaint.setColor(Color.TRANSPARENT);
            cvs.drawRect(m_rcBK, linePaint);
            return true;

        }
    }

    void PaintActions(Canvas cvs) {
        //是底层数据只在收到数据重绘
        if (!isDisplayDraw) {
            String strkey = "videoDrawBoard-" + 1;
            if (!getPadMgr().mSumLettyVideo.isEmpty()) {
                HashMap<String, List<TL_PadAction>> mSumLettyVideo = (HashMap<String, List<TL_PadAction>>) getPadMgr().mSumLettyVideo;
                if (mSumLettyVideo.containsKey(strkey)) {
                    List<TL_PadAction> padActions = mSumLettyVideo.get(strkey);
                    for (int i = 0; i < padActions.size(); i++) {
                        PaintPadAction(padActions.get(i), cvs);
                    }
                }
            }
            //重绘完成清空顶层数据
            getPadMgr().informTopVideo();
        } else {
            //暂存顶层画笔数据 收到信令清空
            if (getPadMgr().topActionsVideo != null && getPadMgr().topActionsVideo.size() > 0) {
                List<TL_PadAction> topaction = getPadMgr().topActionsVideo;
                for (int i = 0; i < topaction.size(); i++) {
                    PaintPadAction(topaction.get(i), cvs);
                }
            }
        }

        if (m_tl_CurrentPadAction != null) {
            PaintPadAction(m_tl_CurrentPadAction, cvs);
        }
    }

    /***
     *    绘制图形
     * @param tl_pa
     * @param cvs
     */
    void PaintPadAction(TL_PadAction tl_pa, Canvas cvs) {
        switch (tl_pa.nActionMode) {

            case ft_markerPen: {   //标记

//                if (tl_pa.points.size() <= 2) {
//                    break;
//                }

                Paint linePaint = new Paint();
                linePaint.setAntiAlias(true);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                linePaint.setStrokeJoin(Paint.Join.ROUND);
                linePaint.setColor(tl_pa.nPenColor);
                linePaint.setStrokeWidth(tl_pa.nPenWidth / penWidthRatio() /** dbZoomScale*/);


                if (!isDisplayDraw) {
                    tl_pa.alActionPoint.clear();
                    for (int i = 0; i < tl_pa.points.size(); i++) {
                        tl_pa.alActionPoint.add(UnWhithXYLinePath(tl_pa.points.get(i)));
                    }
                }

                int nSize = tl_pa.alActionPoint.size();
                if (nSize <= 2) {
                    PointF pointF = unRelativePoint(tl_pa.alActionPoint.get(0));
                    cvs.drawPoint(pointF.x, pointF.y, linePaint);
                    break;
                }

                cvs.drawPath(getMarkPenPath(tl_pa), linePaint);

            }
            break;
            case ft_Eraser: {  //橡皮擦
                if (tl_pa.points.size() <= 2) {
                    break;
                }
                tl_pa.alActionPoint.clear();
                for (int i = 0; i < tl_pa.points.size(); i++) {
                    tl_pa.alActionPoint.add(UnWhithXYLinePath(tl_pa.points.get(i)));
                }
                int nSize = tl_pa.alActionPoint.size();
                if (nSize <= 2) {
                    break;
                }

                Paint linePaint = new Paint();
                linePaint.setAlpha(0);
                linePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                linePaint.setAntiAlias(true);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                linePaint.setStrokeWidth(tl_pa.nPenWidth / penWidthRatio() /** dbZoomScale*/);
                cvs.drawPath(getMarkPenPath(tl_pa), linePaint);

            }
            break;
            default:
                break;
        }
    }

    /**
     * 计算画笔宽度比值
     */
    private float penWidthRatio() {
        float hight = 960 * 1.0f;
        float width = hight / 9 * 16;
        if (this.padSizeMode == 3 && this.videoHid != 0 && this.videoWid != 0) {
            width = hight / this.videoHid * this.videoWid;
        }
        return width / m_rcBK.width();
    }

    private PointF UnWhithXYLinePath(PointF pointF) {
        double hight = 960 * 1.0;
        double width = hight / 9 * 16;
        if (this.padSizeMode == 3 && this.videoHid != 0 && this.videoWid != 0) {
            width = hight / this.videoHid * this.videoWid;
        }
        PointF a1 = new PointF();
        a1.x = (float) (pointF.x / width);
        a1.y = (float) (pointF.y / hight);
        return a1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int nAction = event.getAction();
        switch (nAction) {

            case MotionEvent.ACTION_DOWN:
                if (requestParentFocus) {
                    OnTouchDown(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (requestParentFocus) {
                    OnTouchUp(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (requestParentFocus) {
                    OnTouchMove(event);
                    getParent().requestDisallowInterceptTouchEvent(requestParentFocus);
                }
                break;
        }

        if (requestParentFocus) {
            getParent().requestDisallowInterceptTouchEvent(requestParentFocus);
            return true;
        } else {
            if (mTouchHandler != null) {
                mTouchHandler.handler();
            }
            return false;
        }

    }

    public boolean OnTouchDown(MotionEvent event) {
        //无画笔类型
        if (mToolsType == ToolsType.defaule) {
            return true;
        }

        if (mToolsType == ToolsType.pen) {
            if (mToolsPenType == ToolsPenType.fountainPen) {
                m_nActionMode = TL_PadAction.factoryType.ft_markerPen;
                m_bActionfill = true;
            }
            m_nPenWidth = mToolsPenProgress;
            m_nPenColor = mToolsPenColor;
        } else if (mToolsType == ToolsType.eraser) {
            m_nActionMode = TL_PadAction.factoryType.ft_Eraser;
            m_bActionfill = true;
            m_nPenWidth = mToolsEraserWidth;
        }

        if (m_nActionMode == null) return true;

        if (event.getX() < m_rcBK.left || event.getX() > m_rcBK.right || event.getY() < m_rcBK.top || event.getY() > m_rcBK.bottom) {
            return true;
        }

        float downX = event.getX();
        float downY = event.getY();
        switch (m_nActionMode) {
            case ft_markerPen:
            case ft_Eraser: {
                if (m_tl_CurrentPadAction == null) {
                    m_tl_CurrentPadAction = new TL_PadAction();
                    UUID = getPadMgr().getUUID();
                    m_tl_CurrentPadAction.sID = m_tl_CurrentPadAction.hashCode() + "";

                    //0: default  1: 小白板  2 ： 视频标注
                    m_tl_CurrentPadAction.boardType = 2;
                    //模式
                    m_tl_CurrentPadAction.nActionMode = m_nActionMode;
                    m_tl_CurrentPadAction.id = UUID + "###" + "_SharpsChange_videoDrawBoard" + "_" + 1;
                    double penwidth = m_nPenWidth * 1.0 * 60 / 100 * penWidthRatio() * dbZoomScale;
                    m_tl_CurrentPadAction.nPenWidth = (int) penwidth;
                    if (m_nActionMode == TL_PadAction.factoryType.ft_markerPen && !m_bActionfill) {
                        Integer[] rgb = ColorUtils.RGB(m_nPenColor);
                        String argb = ColorUtils.toHexArgb(80, rgb[0], rgb[1], rgb[2]);
                        m_tl_CurrentPadAction.nPenColor = Color.parseColor(argb);
                    } else {
                        m_tl_CurrentPadAction.nPenColor = m_nPenColor;
                    }

                    m_tl_CurrentPadAction.bIsFill = m_bActionfill;
                    m_tl_CurrentPadAction.isDraw = true;
                    m_tl_CurrentPadAction.mClear = 0;

                    m_tl_CurrentPadAction.alActionPoint.add(relativePoint(new PointF(downX, downY)));
                    WhithXY(m_tl_CurrentPadAction, relativePoint(new PointF(downX, downY)));

                    if (m_nActionMode == TL_PadAction.factoryType.ft_markerPen || m_nActionMode == TL_PadAction.factoryType.ft_Eraser) {
                        m_tl_CurrentPadAction.LinePath = new Path();
                        m_tl_CurrentPadAction.LinePath.moveTo(downX, downY);
                    }
                }
            }
            break;
            default:
                break;
        }
        return true;
    }

    public boolean OnTouchMove(MotionEvent event) {

        if (m_nActionMode == null) return true;
        float moveX = event.getX();
        float moveY = event.getY();
        switch (m_nActionMode) {

            case ft_Eraser:
            case ft_markerPen: {
                if (m_tl_CurrentPadAction != null) {
                    m_tl_CurrentPadAction.alActionPoint.add(relativePoint(new PointF(moveX, moveY)));

                    WhithXY(m_tl_CurrentPadAction, relativePoint(new PointF(moveX, moveY)));
                    this.invalidate();
                }
            }
            break;
            default:
                break;

        }
        return true;
    }

    public boolean OnTouchUp(MotionEvent event) {
        if (m_nActionMode == null) return true;
        float upX = event.getX();
        float upY = event.getY();
        switch (m_nActionMode) {
            case ft_markerPen:
            case ft_Eraser: {
                if (m_tl_CurrentPadAction != null) {
                    m_tl_CurrentPadAction.alActionPoint.add(relativePoint(new PointF(upX, upY)));
                    WhithXY(m_tl_CurrentPadAction, relativePoint(new PointF(upX, upY)));

                    calculateActionsRect(m_tl_CurrentPadAction);

                    if (m_tl_CurrentPadAction.CoverArea == null || m_tl_CurrentPadAction.CoverArea.isEmpty()) {
                        m_tl_CurrentPadAction = null;
                        return true;
                    }

                    if (m_iSync != null) {
                        m_iSync.SendActions(WhitePadInterface.ADD_ACTION, m_tl_CurrentPadAction);
                    }
                    //添加临时数据缓存
                    getPadMgr().topActionsVideo.add(m_tl_CurrentPadAction);

                    Rect rcClip = m_tl_CurrentPadAction.CoverArea;

                    m_tl_CurrentPadAction = null;
                    if (rcClip != null && rcClip.isEmpty()) {
                        this.invalidate(rcClip);
                    } else {
                        this.invalidate();
                    }
                }
            }
            break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    /**
     * 计算
     *
     * @param tl_pa
     */
    public void calculateActionsRect(TL_PadAction tl_pa) {
        switch (tl_pa.nActionMode) {

            case ft_markerPen: {
                if (tl_pa.points.size() <= 2) {
                    break;
                }
                tl_pa.alActionPoint.clear();
                for (int i = 0; i < tl_pa.points.size(); i++) {
                    tl_pa.alActionPoint.add(UnWhithXYLinePath(tl_pa.points.get(i)));
                }
                int nSize = tl_pa.alActionPoint.size();
                if (nSize <= 2) {

                    break;
                }
                tl_pa.HotRegion = new Region();
                Rect rcBK = new Rect((int) m_rcBK.left, (int) m_rcBK.top, (int) m_rcBK.right, (int) m_rcBK.bottom);
                for (int j = 0; j < nSize - 1; j++) {
                    PointF pt1 = tl_pa.alActionPoint.get(j);
                    PointF pt2 = tl_pa.alActionPoint.get(j + 1);
                    PointF ptStart = unRelativePoint(pt1);
                    PointF ptstop = unRelativePoint(pt2);

                    Path hotPath = new Path();
                    getShotlineHotPath(hotPath, ptStart, ptstop);
                    //                    WhithXY(tl_pa,pt1,pt2);
                    Region part = new Region();
                    part.setPath(hotPath, new Region(rcBK));

                    tl_pa.HotRegion.op(part, Op.UNION);
                }
                tl_pa.CoverArea = tl_pa.HotRegion.getBounds();
            }
            break;
            case ft_Eraser: {
                if (tl_pa.points.size() <= 2) {
                    break;
                }
                tl_pa.alActionPoint.clear();
                for (int i = 0; i < tl_pa.points.size(); i++) {
                    tl_pa.alActionPoint.add(UnWhithXYLinePath(tl_pa.points.get(i)));
                }
                int nSize = tl_pa.alActionPoint.size();
                if (nSize <= 2) {

                    break;
                }
                tl_pa.HotRegion = new Region();
                Rect rcBK = new Rect((int) m_rcBK.left, (int) m_rcBK.top, (int) m_rcBK.right, (int) m_rcBK.bottom);
                for (int j = 0; j < nSize - 1; j++) {
                    PointF pt1 = tl_pa.alActionPoint.get(j);
                    PointF pt2 = tl_pa.alActionPoint.get(j + 1);
                    PointF ptStart = unRelativePoint(pt1);
                    PointF ptstop = unRelativePoint(pt2);

                    Path hotPath = new Path();
                    getShotlineHotPath(hotPath, ptStart, ptstop);
                    //                    WhithXY(tl_pa,pt1,pt2);
                    Region part = new Region();
                    part.setPath(hotPath, new Region(rcBK));

                    tl_pa.HotRegion.op(part, Op.UNION);
                }
                tl_pa.CoverArea = tl_pa.HotRegion.getBounds();
            }
            break;

            default:
                break;
        }
    }

    public void getShotlineHotPath(Path path, PointF aF, PointF bF) {

        float hotWidth = ActionBorder;

        PointF a, b;
        if (aF.x >= bF.x) {
            a = bF;
            b = aF;
        } else {
            a = aF;
            b = bF;
        }

        float fLine = (float) Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
        float widthoff = 0;
        float heightoff = 0;
        if (fLine != 0) {
            widthoff = hotWidth * Math.abs(a.y - b.y) / fLine;
            heightoff = hotWidth * Math.abs(a.x - b.x) / fLine;
        }

        PointF aX = new PointF(), aY = new PointF(), bX = new PointF(), bY = new PointF();

        if (a.y <= b.y) {
            aX.x = a.x - widthoff;
            aY.x = a.x + widthoff;

            bX.x = b.x - widthoff;
            bY.x = b.x + widthoff;
        } else {
            aX.x = a.x + widthoff;
            aY.x = a.x - widthoff;

            bX.x = b.x + widthoff;
            bY.x = b.x - widthoff;
        }


        if (a.x <= b.x) {
            aX.y = a.y + heightoff;
            aY.y = a.y - heightoff;

            bX.y = b.y + heightoff;
            bY.y = b.y - heightoff;
        } else {
            aX.y = a.y + heightoff;
            aY.y = a.y - heightoff;

            bX.y = b.y + heightoff;
            bY.y = b.y - heightoff;
        }
        //        path.moveTo(aX.x, aX.y);
        //        path.lineTo(aY.x, aY.y);
        //        path.lineTo(bY.x, bY.y);
        //        path.lineTo(bX.x, bX.y);

        //lines.quadTo(x ,y,(ptf.x + x) / 2, (ptf.y + y) / 2);
        path.moveTo(aX.x, aX.y);
        path.quadTo(aX.x, aX.y, (aX.x + aY.x) / 2, (aX.y + aY.y) / 2);
        path.quadTo(aY.x, aY.y, (bX.x + aY.x) / 2, (bX.y + aY.y) / 2);
        path.quadTo(bX.x, bY.y, (bY.x + bX.x) / 2, (bY.y + bX.y) / 2);

        ////        path.lineTo(aY.x, aY.y);
        //        path.lineTo(bY.x, bY.y);
        //        path.lineTo(bX.x, bX.y);
        path.close();
    }

    private void WhithXY(TL_PadAction tl_pa, PointF ptStart) {
        double hight;
        double width;
        hight = 960 * 1.0;
        width = hight / 9 * 16;
        if (this.padSizeMode == 3 && this.videoHid != 0 && this.videoWid != 0) {
            width = hight / this.videoHid * this.videoWid;
        }
        PointF a1 = new PointF(), a2 = new PointF();
        a1.x = (float) (width * ptStart.x);
        a1.y = (float) (hight * ptStart.y);

        tl_pa.points.add(a1);

    }

    public PointF relativePoint(PointF point) {
        PointF real = new PointF();
        //是否是文字输入坐标
        if (this.m_orgRcBK != null) {
            real.x = (point.x - m_orgRcBK.left) / m_orgRcBK.width();
            real.y = (point.y - m_orgRcBK.top) / m_orgRcBK.height();
            m_orgRcBK = null;
        } else {
            real.x = (point.x - m_rcBK.left) / m_rcBK.width();
            real.y = (point.y - m_rcBK.top) / m_rcBK.height();
        }
        return real;
    }

    public PointF unRelativePoint(PointF point) {
        PointF real = new PointF();
        if (point != null) {
            real.x = m_rcBK.left + m_rcBK.width() * point.x;//(0.0,168.0,480.0,648.0)
            real.y = m_rcBK.top + m_rcBK.height() * point.y;//(0,168)
        }
        return real;
    }


    public void setSyncInterface(FaceShareControl iSync) {
        this.m_iSync = iSync;
    }

    public Path getMarkPenPath(TL_PadAction tl_pa) {
        if (tl_pa.nActionMode == TL_PadAction.factoryType.ft_markerPen || tl_pa.nActionMode == TL_PadAction.factoryType.ft_Eraser) {
            Path lines = new Path();
            float x = 0, y = 0;
            for (int i = 0; i < tl_pa.alActionPoint.size(); i++) {
                PointF ptf = unRelativePoint(tl_pa.alActionPoint.get(i));

                /*if (ptf.y < m_rcBK.top) {
                    ptf.y = m_rcBK.top;
                } else if (ptf.y > m_rcBK.bottom) {
                    ptf.y = m_rcBK.bottom;
                }
                if (ptf.x < m_rcBK.left) {
                    ptf.x = m_rcBK.left;
                } else if (ptf.x > m_rcBK.right) {
                    ptf.x = m_rcBK.right;
                }*/

                if (i == 0) {
                    lines.moveTo(ptf.x, ptf.y);
                    x = ptf.x;
                    y = ptf.y;
                } else {
                    //                    lines.lineTo(ptf.x, ptf.y);
                    lines.quadTo(x, y, (ptf.x + x) / 2, (ptf.y + y) / 2);
                    x = ptf.x;
                    y = ptf.y;
                }
            }
            return lines;
        }
        return null;
    }

    @Override
    public void onChange() {
        new Handler(getPadMgr().mAppContext.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler(getPadMgr().mAppContext.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    @Override
    public void onAttachedToWindow() {

        //ture 监听顶部画笔类刷新 false底部绘制类
        if (isDisplayDraw) {
            getPadMgr().addOnTopDataChangeListenerVideo(this);
        } else {
            getPadMgr().addOnDataChangeListenerVideo(this);
        }
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
//        getPadMgr().removeOnDataChangeListenerVideo(this);
        getPadMgr().mSumLettyVideo.clear();
        super.onDetachedFromWindow();
    }

    /**
     * 清空画板
     */
    public void clearPab() {
        getPadMgr().mSumLettyVideo.clear();
        getPadMgr().mBasePaint.clear();
        onChange();
    }

    public SharePadMgr getPadMgr() {
        return m_thisPadMgr;
    }

    public void setPadMgr(SharePadMgr m_thisPadMgr) {
        this.m_thisPadMgr = m_thisPadMgr;
        setSyncInterface(m_thisPadMgr);
    }

    public void setContext(Context context) {
        this.mContext = context;
    }


    /**
     * 设置是顶层还是底层
     *
     * @param isdraw
     */
    public void setDrawShow(boolean isdraw) {
        this.isDisplayDraw = isdraw;
    }

    /**
     * 请求父View是否拦截
     *
     * @param focus
     */
    public void requestParentFocus(boolean focus) {
        requestParentFocus = focus;
    }

    //点击处理
    public interface TouchHandler {
        void handler();
    }

    public void setTouchHandler(TouchHandler touchHandler) {
        mTouchHandler = touchHandler;
    }

    /**
     * 是那种类型
     *
     * @param type
     */
    public void setToolsType(ToolsType type) {
        this.mToolsType = type;
    }

    public void setToolsPenColor(int mToolsPenColor) {
        this.mToolsPenColor = mToolsPenColor;
        this.m_nPenColor = mToolsPenColor;
    }

    public void setToolsPenProgress(int mToolsPenProgress) {
        this.mToolsPenProgress = mToolsPenProgress;
    }

    public void setToolsEraserWidth(int mToolsEraserWidth) {
        this.mToolsEraserWidth = mToolsEraserWidth;
    }
}




