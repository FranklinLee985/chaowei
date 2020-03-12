package com.classroomsdk.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.classroomsdk.bean.TL_PadAction;
import com.classroomsdk.custom.WhiteInputPop;
import com.classroomsdk.interfaces.EditTextInputControl;
import com.classroomsdk.interfaces.FaceShareControl;
import com.classroomsdk.interfaces.OnSendClickListener;
import com.classroomsdk.interfaces.WhitePadInterface;
import com.classroomsdk.manage.SharePadMgr;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmallPaint extends View implements SharePadMgr.DataChangeListenerSmall, SharePadMgr.TopDataChangeListenerSmall {

    private TL_PadAction m_tl_CurrentPadAction = null;

    private TL_PadAction.factoryType m_nActionMode = null;

    private Context mContext;

    private boolean m_bActionfill;
    private int m_nPenWidth = 10;
    private int m_nPenColor = 0xff5AC9FA;
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
    private WhiteInputPop inputWindowPop;

    private View mView;

    //移动白板坐标
    private PointF mfMovePoint = new PointF();

    //文字显示区域宽度  可能存在换行
    private int nTextWidth;

    //工具类型
    private ToolsType mToolsType = ToolsType.defaule;

    //笔类型
    private ToolsPenType mToolsPenType = ToolsPenType.fountainPen;
    //笔颜色
    private int mToolsPenColor = 0xff5AC9FA;
    //笔宽度
    private int mToolsPenProgress = 10;


    //文字类型
    //    private ToolsPenType mToolsFontType;
    //文字颜色颜色
    private int mToolsFontColor = 0xff5AC9FA;
    //文字大小
    private int mToolsFontSize = 10;

    //形状
    private ToolsFormType mToolsFormType = ToolsFormType.hollow_rectangle;
    //颜色
    private int mToolsFormColor = 0xff5AC9FA;
    //宽度
    private int mToolsFormWidth = 10;
    //橡皮宽高
    private int mToolsEraserWidth = 10;
    //标识是顶层还是底层
    private boolean isDisplayDraw = false;


    public SmallPaint(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    int padSizeMode = 2;

    public void setPadSize(int padSizeMode) {
        this.padSizeMode = padSizeMode;
    }

    private EditTextInputControl mEditTextInputControl;


    public void CheckBkImageSize() {
        m_nOldHeight = this.getHeight();
        m_nOldWidth = this.getWidth();

        int nimageHeight = Math.min(m_nOldHeight, m_nOldWidth);
        int nimagew = nimageHeight;

        if (padSizeMode == 1) {
            nimageHeight = nimageHeight / 4 * 3;
        } else if (padSizeMode == 2) {
            nimageHeight = nimageHeight / 16 * 9;
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
            CumputeActions(cvs);

        }
    }

    /**
     * 计算所属画板当前用户 及 用户所有已画数据
     *
     * @param cvs
     */
    private void CumputeActions(Canvas cvs) {
        if (!isDisplayDraw) {
            RoomUser roomUser = TKRoomManager.getInstance().getMySelf();
            if (roomUser.role == 0 || roomUser.role == 4 || roomUser.role == -1) {
                if (getPadMgr().mSmallPaintDoc != null) {
                    if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_prepareing")) {
                        String strkey = "blackBoardCommon" + "-" + 1;
                        PaintActions(cvs, strkey);
                        //分发状态老师只能点击看  不能花
                    } else if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_dispenseed")) {
                        //如果在分发状态点到自己画板 这时可以画自己画板 如果是老师 显示老师 学生显示学生
                        if (getPadMgr().mSmallPaintDoc.getCurrentTapKey().equals("blackBoardCommon")) {
                            String strkey = "blackBoardCommon" + "-" + 1;
                            PaintActions(cvs, strkey);
                        } else {
                            String strkey = getPadMgr().mSmallPaintDoc.getCurrentTapKey() + "-" + 1;
                            PaintActions(cvs, strkey);
                        }
                        //回收状态下  所有人不能绘制 老师可点击学生画板绘制 学生不能绘制
                    } else if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_recycle")) {
                        String strkey = getPadMgr().mSmallPaintDoc.getCurrentTapKey() + "-" + 1;
                        PaintActions(cvs, strkey);
                        //再次分发下  老师不能画学生了  可画自己
                    } else if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_againDispenseed")) {
                        if (getPadMgr().mSmallPaintDoc.getCurrentTapKey().equals("blackBoardCommon")) {
                            String strkey = "blackBoardCommon" + "-" + 1;
                            PaintActions(cvs, strkey);
                        } else {
                            String strkey = getPadMgr().mSmallPaintDoc.getCurrentTapKey() + "-" + 1;
                            PaintActions(cvs, strkey);
                        }
                    }
                }
            }

            if (roomUser.role == 2) {

                if (getPadMgr().mSmallPaintDoc != null) {
                    if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_prepareing")) {
                        String strkey = roomUser.peerId + "-" + 1;
                        PaintActions(cvs, strkey);
                        //如果是学生  在分发状态下 学生可画 老师不可画学生  学生以自己画笔为主
                    } else if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_dispenseed")) {
                        String strkey = roomUser.peerId + "-" + 1;
                        PaintActions(cvs, strkey);


                        //如果是学生  在回收状态下 学生不可画 老师可点击学生随意画 这是画笔为选中着id
                    } else if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_recycle")) {
                        String strkey = getPadMgr().mSmallPaintDoc.getCurrentTapKey() + "-" + 1;
                        PaintActions(cvs, strkey);
                        //如果是学生 在再次分发状态 学生显示自己id为主的画笔
                    } else if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_againDispenseed")) {

                        String strkey = roomUser.peerId + "-" + 1;
                        PaintActions(cvs, strkey);
                    }
                }
            }
            getPadMgr().informTopSmall();
        } else {
            //暂存顶层画笔数据 收到信令清空
            if (getPadMgr().topActionsSmall != null && getPadMgr().topActionsSmall.size() > 0) {
                List<TL_PadAction> topaction = getPadMgr().topActionsSmall;
                for (int i = 0; i < topaction.size(); i++) {
                    PaintPadAction(topaction.get(i), cvs);
                }
            }
        }


        if (m_tl_CurrentPadAction != null) {
            PaintPadAction(m_tl_CurrentPadAction, cvs);
        }
    }

    boolean PaintBk(Canvas cvs) {
        if (m_rcBK.isEmpty()) CheckBkImageSize();
        //顶层绘制透明
        if (isDisplayDraw) {
            Paint linePaint = new Paint();
            linePaint.setColor(Color.TRANSPARENT);
            cvs.drawRect(m_rcBK, linePaint);
            return true;
        } else {
            if (getPadMgr().mCurrentShareDoc != null) {
                Paint linePaint = new Paint();
                linePaint.setColor(Color.WHITE);
                cvs.drawRect(m_rcBK, linePaint);

                return true;
            }
        }
        return false;
    }

    List<TL_PadAction> actions = null;

    void PaintActions(Canvas cvs, String strkey) {
        if (strkey == null) return;
        if (!getPadMgr().mSumLettySmall.isEmpty()) {
            HashMap<String, List<TL_PadAction>> mSumLettysmall = (HashMap<String, List<TL_PadAction>>) getPadMgr().mSumLettySmall;
            if (mSumLettysmall.containsKey(strkey)) {
                List<TL_PadAction> padActions = mSumLettysmall.get(strkey);
                if (getPadMgr().mSmallPaintDoc != null) {

                    if (TKRoomManager.getInstance().getMySelf().role != 2 && getPadMgr().mSmallPaintDoc.getCurrentTapKey().equals("blackBoardCommon")) {
                        if (padActions != null) {
                            for (int i = 0; i < padActions.size(); i++) {
                                PaintPadAction(padActions.get(i), cvs);
                            }
                        }

                    } else {
                        if (getPadMgr().mBasePaint != null) {
                            if (actions == null) {
                                actions = new ArrayList<>();
                            }
                            actions.clear();
                            if (getPadMgr().mBasePaint != null && getPadMgr().mBasePaint.size() > 0)
                                actions.addAll(getPadMgr().mBasePaint);
                            if (padActions != null && padActions.size() > 0)
                                actions.addAll(padActions);

                            for (int i = 0; i < actions.size(); i++) {
                                PaintPadAction(actions.get(i), cvs);
                            }
                        }
                    }
                }

            } else {
                List<TL_PadAction> padActions = mSumLettysmall.get(strkey);
                if (getPadMgr().mBasePaint != null) {
                    if (actions == null) {
                        actions = new ArrayList<>();
                    }
                    actions.clear();
                    if (getPadMgr().mBasePaint != null && getPadMgr().mBasePaint.size() > 0)
                        actions.addAll(getPadMgr().mBasePaint);
                    if (padActions != null && padActions.size() > 0)
                        actions.addAll(padActions);

                    for (int i = 0; i < actions.size(); i++) {
                        PaintPadAction(actions.get(i), cvs);
                    }
                }

            }
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
                Paint linePaint = new Paint();
                linePaint.setAntiAlias(true);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                linePaint.setStrokeJoin(Paint.Join.ROUND); //连接处样式
                linePaint.setColor(tl_pa.nPenColor);
                linePaint.setStrokeWidth(tl_pa.nPenWidth / penWidthRatio() /** dbZoomScale*/);

                if (!isDisplayDraw) {
                    tl_pa.alActionPoint.clear();
                    for (int i = 0; i < tl_pa.points.size(); i++) {
                        tl_pa.alActionPoint.add(UnWhithXYLinePath(tl_pa.points.get(i)));
                    }
                }
                if (tl_pa.points.size() <= 2) {
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
                if (!isDisplayDraw) {
                    tl_pa.alActionPoint.clear();
                    for (int i = 0; i < tl_pa.points.size(); i++) {
                        tl_pa.alActionPoint.add(UnWhithXYLinePath(tl_pa.points.get(i)));
                    }
                }

                int nSize = tl_pa.alActionPoint.size();
                if (nSize <= 2) {
                    break;
                }

                //                int layerId = cvs.saveLayer(0, 0, cvs.getWidth(), cvs.getHeight(), null, Canvas.ALL_SAVE_FLAG);
                Paint linePaint = new Paint();
//                linePaint.setAlpha(0);
                linePaint.setColor(Color.WHITE);
//                linePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                linePaint.setAntiAlias(true);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeCap(Paint.Cap.ROUND);
                linePaint.setStrokeWidth(tl_pa.nPenWidth / penWidthRatio() /** dbZoomScale*/);
                cvs.drawPath(getMarkPenPath(tl_pa), linePaint);
                //                cvs.restoreToCount(layerId);
            }
            break;
            case ft_Text: {   //文本
                if (UnWhithXYLine(tl_pa) == null) break;
                TextPaint textPaint = new TextPaint();
                textPaint.setColor(tl_pa.nPenColor);
                textPaint.setTextSize(tl_pa.nPenWidth / penWidthRatio() /** dbZoomScale*/);
                PointF ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                cvs.save();
                if (ptStart == null) {
                    break;
                }
                if (ptStart.y < m_rcBK.top) {
                    ptStart.y = m_rcBK.top;
                }
                if (ptStart.y > m_rcBK.bottom) {
                    ptStart.y = m_rcBK.bottom - tl_pa.nPenWidth /** dbZoomScale*/ - 10;
                }


                if (ptStart.x < m_rcBK.left) {
                    ptStart.x = m_rcBK.left;
                }
                if (ptStart.x > m_rcBK.right) {
                    ptStart.x = m_rcBK.right;
                }
                StaticLayout layout; //闂佽法鍠愰弸濠氬箯閻戣姤鏅搁柡鍌樺�栫�氾拷
                if (cvs.getWidth() > ptStart.x) {
                    layout = new StaticLayout(tl_pa.sText, textPaint, (int) (cvs.getWidth() - ptStart.x), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
                } else {
                    layout = new StaticLayout(tl_pa.sText, textPaint, cvs.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
                }
                cvs.translate(ptStart.x, ptStart.y);
                layout.draw(cvs);
                cvs.restore();
            }
            break;
            default:
                break;
        }
    }

    private PointF UnWhithXYLinePath(PointF pointF) {
        double hight = 960 * 1.0;
        double width = hight / 9 * 16;
        PointF a1 = new PointF();
        a1.x = (float) (pointF.x / width);
        a1.y = (float) (pointF.y / hight);
        return a1;
    }


    private String UnWhithXYLine(TL_PadAction tl_pa) {
        if (tl_pa.points.size() == 0) return null;
        tl_pa.alActionPoint.clear();
        double hight = 960 * 1.0;
        double width = hight / 9 * 16;
        PointF ptStart = null, ptstop = null;
        if (tl_pa.points.size() == 2) {
            ptStart = tl_pa.points.get(0);
            ptstop = tl_pa.points.get(1);
        } else if (tl_pa.points.size() == 1) {
            ptStart = tl_pa.points.get(0);
            ptstop = tl_pa.ptSizingEndPointf;
        }

        if (tl_pa.nActionMode == TL_PadAction.factoryType.ft_Rectangle || tl_pa.nActionMode == TL_PadAction.factoryType.ft_Ellipse) {
            PointF a1 = new PointF(), a2 = new PointF();
            a1.x = (float) (ptStart.x / width);
            a1.y = (float) (ptStart.y / hight);

            a2.x = (float) ((ptstop.x + ptStart.x) / width);
            a2.y = (float) ((ptstop.y + ptStart.y) / hight);


            tl_pa.alActionPoint.add(a1);
            tl_pa.alActionPoint.add(a2);
        } else if (tl_pa.nActionMode == TL_PadAction.factoryType.ft_Text) {
            PointF a1 = new PointF();
            a1.x = (float) (ptStart.x / width);
            a1.y = (float) (ptStart.y / hight);

            tl_pa.alActionPoint.add(a1);
        } else {
            PointF a1 = new PointF(), a2 = new PointF();
            a1.x = (float) (ptStart.x / width);
            a1.y = (float) (ptStart.y / hight);

            a2.x = (float) (ptstop.x / width);
            a2.y = (float) (ptstop.y / hight);


            tl_pa.alActionPoint.add(a1);
            tl_pa.alActionPoint.add(a2);
        }

        return "success";
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int nAction = event.getAction();
        boolean bhandle = true;

        if (TKRoomManager.getInstance().getMySelf().role != 4) {
            switch (nAction) {
                case MotionEvent.ACTION_DOWN:
                    bhandle = OnTouchDown(event);
                    break;
                case MotionEvent.ACTION_UP:
                    bhandle = OnTouchUp(event);
                    break;
                case MotionEvent.ACTION_MOVE: {
                    bhandle = OnTouchMove(event);
                    if (bhandle) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    break;
                }
            }
        }

        if (bhandle) {
            getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        }
        return super.onTouchEvent(event);
    }

    String id;

    public boolean OnTouchDown(MotionEvent event) {
        if (getPadMgr().mSmallPaintDoc != null) {
            if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_prepareing") && getPadMgr().mSmallPaintDoc.getCurrentTapKey().equals("blackBoardCommon")) {
                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                    id = getPadMgr().mSmallPaintDoc.getCurrentTapKey();
                } else {
                    return true;
                }
                //分发状态 老师可画自己 不可花学生
            } else if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_dispenseed")) {
                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                    if (getPadMgr().mSmallPaintDoc.getCurrentTapKey().equals("blackBoardCommon")) {
                        id = getPadMgr().mSmallPaintDoc.getCurrentTapKey();
                    } else {
                        return true;
                    }
                } else {
                    id = TKRoomManager.getInstance().getMySelf().peerId;
                }
            } else if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_recycle")) {
                //回收状态下 如果是学生 不能进行任何操作
                if (TKRoomManager.getInstance().getMySelf().role == 2) {
                    return true;
                }
                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                    id = getPadMgr().mSmallPaintDoc.getCurrentTapKey();
                }
            } else if (getPadMgr().mSmallPaintDoc.getBlackBoardState().equals("_againDispenseed")) {
                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                    if (getPadMgr().mSmallPaintDoc.getCurrentTapKey().equals("blackBoardCommon")) {
                        id = getPadMgr().mSmallPaintDoc.getCurrentTapKey();
                    } else {
                        return true;
                    }

                } else {
                    id = TKRoomManager.getInstance().getMySelf().peerId;
                }
            }
        }


        if (mToolsType == ToolsType.defaule) {
            return true;
        } else if (mToolsType == ToolsType.pen) {
            m_nActionMode = TL_PadAction.factoryType.ft_markerPen;
            m_bActionfill = true;
            m_nPenWidth = mToolsPenProgress;
            m_nPenColor = mToolsPenColor;
        } else if (mToolsType == ToolsType.font) {
            m_nActionMode = TL_PadAction.factoryType.ft_Text;
            m_bActionfill = true;
            m_nPenWidth = mToolsFontSize;
            m_nPenColor = mToolsFontColor;
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

                    m_tl_CurrentPadAction.nDocID = id;
                    m_tl_CurrentPadAction.nPage = "1";
                    //0: default  1: 小白板  2 ： 视频标注
                    m_tl_CurrentPadAction.boardType = 1;
                    m_tl_CurrentPadAction.nActionMode = m_nActionMode;
                    m_tl_CurrentPadAction.id = UUID + "###" + "_SharpsChange_" + m_tl_CurrentPadAction.nDocID + "_" + m_tl_CurrentPadAction.nPage;
                    double penwidth = m_nPenWidth * 1.0 * 60 / 100 * penWidthRatio() * dbZoomScale;
                    m_tl_CurrentPadAction.nPenWidth = (int) penwidth;
                    m_tl_CurrentPadAction.nPenColor = m_nPenColor;
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
            case ft_Text: {
                insertText(id, downX, downY);

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

                    if (m_iSync != null)
                        m_iSync.SendActions(WhitePadInterface.ADD_ACTION, m_tl_CurrentPadAction);
                    //添加小白板顶层画笔到缓存
                    getPadMgr().topActionsSmall.add(m_tl_CurrentPadAction);
                    /*if (m_tl_CurrentPadAction.CoverArea == null || m_tl_CurrentPadAction.CoverArea.isEmpty()) {
                        m_tl_CurrentPadAction = null;
                        return true;
                    }*/
                    m_tl_CurrentPadAction = null;
                    this.invalidate();
                }
            }
            break;
            default:
                break;
        }
        return true;
    }

    /**
     * 计算画笔宽度比值
     */
    private float penWidthRatio() {
        float hight = 960 * 1.0f;
        float width = hight / 9 * 16;
        return width / m_rcBK.width();
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

                    tl_pa.HotRegion.op(part, Region.Op.UNION);
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

                    tl_pa.HotRegion.op(part, Region.Op.UNION);
                }
                tl_pa.CoverArea = tl_pa.HotRegion.getBounds();
            }
            break;


            case ft_Text: {
                if (UnWhithXYLine(tl_pa) == null) break;
                TextPaint textPaint = new TextPaint();
                textPaint.setColor(tl_pa.nPenColor);
                textPaint.setTextSize(20.0F);
                PointF ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                int widthMax = 0;
                StaticLayout layout = new StaticLayout(tl_pa.sText, textPaint, (int) (m_rcBK.width() - ptStart.x), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);

                int line = 0;
                for (int i = 0; i < layout.getLineCount(); i++) {//layout.getLineCount()
                    line++;
                    int nWidth = (int) layout.getLineWidth(i);
                    widthMax = Math.max(nWidth, widthMax);
                }
                nTextWidth = line > 1 ? widthMax-2 : layout.getWidth();
                Rect rcReg = new Rect();
                rcReg.left = (int) ptStart.x;
                rcReg.top = (int) ptStart.y;
                rcReg.right = rcReg.left + widthMax;
                rcReg.bottom = rcReg.top + layout.getHeight();
                tl_pa.HotRegion = new Region();
                tl_pa.HotRegion.set(rcReg);

                tl_pa.CoverArea = tl_pa.HotRegion.getBounds();
            }
            break;
            default:
                break;
        }
    }

    public void insertText(final String id, final float x, final float y) {

        m_orgRcBK = new RectF(m_rcBK);

        inputWindowPop.showBoardPopupWindow(mView, true, new OnSendClickListener() {
            @Override
            public void ShowText(String text) {
                onInsertText(id, text, x, y);
            }

            @Override
            public void textChange(String text) {
                mEditTextInputControl.changeTextInput(text);
            }
        });

        //回调落笔的位置
        if (mEditTextInputControl != null) {
            mEditTextInputControl.showTextInput(x, y, (int) (m_nPenWidth / (960 / 9 * 16 / m_rcBK.width()) /** dbZoomScale*/), m_nPenColor);
        }

        //隐藏输入框
        inputWindowPop.chat_input_popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mEditTextInputControl.removeEditText();
            }
        });

    }

    public void onInsertText(String id, String strtext, float x, float y) {
        m_tl_CurrentPadAction = new TL_PadAction();

        UUID = getPadMgr().getUUID();
        m_tl_CurrentPadAction.sID = m_tl_CurrentPadAction.hashCode() + "";
        m_tl_CurrentPadAction.nDocID = id;
        m_tl_CurrentPadAction.nPage = "1";
        m_tl_CurrentPadAction.id = UUID + "###" + "_SharpsChange_" + m_tl_CurrentPadAction.nDocID + "_" + m_tl_CurrentPadAction.nPage;
        m_tl_CurrentPadAction.boardType = 1;
        m_tl_CurrentPadAction.nActionMode = TL_PadAction.factoryType.ft_Text;
        double penwidth = m_nPenWidth * 1.0 * 60 / 100 * penWidthRatio() /** dbZoomScale*/;
        m_tl_CurrentPadAction.nPenWidth = (int) penwidth;
        m_tl_CurrentPadAction.nPenColor = m_nPenColor;
        m_tl_CurrentPadAction.bIsFill = m_bActionfill;
        m_tl_CurrentPadAction.alActionPoint = new ArrayList<PointF>();
        m_tl_CurrentPadAction.alActionPoint.add(relativePoint(new PointF(x, y)));
        WhithXY(m_tl_CurrentPadAction, relativePoint(new PointF(x, y)));
        m_tl_CurrentPadAction.sText = strtext;
        calculateActionsRect(m_tl_CurrentPadAction);
        m_tl_CurrentPadAction.nTextWidth = nTextWidth;
        if (m_iSync != null)
            m_iSync.SendActions(WhitePadInterface.ADD_ACTION, m_tl_CurrentPadAction);
        getPadMgr().topActionsSmall.add(m_tl_CurrentPadAction);
        this.invalidate();
        m_tl_CurrentPadAction = null;
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

        path.moveTo(aX.x, aX.y);
        path.quadTo(aX.x, aX.y, (aX.x + aY.x) / 2, (aX.y + aY.y) / 2);
        path.quadTo(aY.x, aY.y, (bX.x + aY.x) / 2, (bX.y + aY.y) / 2);
        path.quadTo(bX.x, bY.y, (bY.x + bX.x) / 2, (bY.y + bX.y) / 2);

        path.close();
    }

    private void WhithXY(TL_PadAction tl_pa, PointF ptStart) {
        double hight;
        double width;
        hight = 960 * 1.0;
        width = hight / 9 * 16;

        if (tl_pa.nActionMode == TL_PadAction.factoryType.ft_Text) {
            PointF a1 = new PointF();
            a1.x = (float) (width * ptStart.x);
            a1.y = (float) (hight * ptStart.y);
            tl_pa.points.add(a1);
        } else {
            PointF a1 = new PointF();
            a1.x = (float) (width * ptStart.x);
            a1.y = (float) (hight * ptStart.y);

            tl_pa.points.add(a1);
        }

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
                if (i == 0) {
                    lines.moveTo(ptf.x, ptf.y);
                    x = ptf.x;
                    y = ptf.y;
                } else {
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
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    //小白板画笔刷新
    @Override
    public void onRefresh() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
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
            getPadMgr().addOnTopDataChangeListenerSmall(this);
        } else {
            getPadMgr().addOnDataChangeListenerSmall(this);
        }
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        //        getPadMgr().removeOnDataChangeListenersm(this);
        //        getPadMgr().mSumLetty.clear();
        super.onDetachedFromWindow();
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

    public void initInputPop(Activity activity, View view) {
        inputWindowPop = new WhiteInputPop(activity);
        this.mView = view;
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
     * 是那种类型
     *
     * @param type
     */
    public void setToolsType(ToolsType type) {
        this.mToolsType = type;
    }


    public void setmToolsPenType(ToolsPenType mToolsPenType) {
        this.mToolsPenType = mToolsPenType;
    }

    public void setmToolsPenColor(int mToolsPenColor) {
        this.mToolsPenColor = mToolsPenColor;
    }

    public void setmToolsPenProgress(int mToolsPenProgress) {
        this.mToolsPenProgress = mToolsPenProgress;
    }

    public void setmToolsFontColor(int mToolsFontColor) {
        this.mToolsFontColor = mToolsFontColor;
    }

    public void setmToolsFontSize(int mToolsFontSize) {
        this.mToolsFontSize = mToolsFontSize;
    }

    public void setmToolsFormType(ToolsFormType mToolsFormType) {
        this.mToolsFormType = mToolsFormType;
    }

    public void setmToolsFormColor(int mToolsFormColor) {
        this.mToolsFormColor = mToolsFormColor;
    }

    public void setmToolsFormWidth(int mToolsFormWidth) {
        this.mToolsFormWidth = mToolsFormWidth;
    }

    public void setmToolsEraserWidth(int mToolsEraserWidth) {
        this.mToolsEraserWidth = mToolsEraserWidth;
    }

    public void setmEditTextInputControl(EditTextInputControl mEditTextInputControl) {
        this.mEditTextInputControl = mEditTextInputControl;
    }
}


