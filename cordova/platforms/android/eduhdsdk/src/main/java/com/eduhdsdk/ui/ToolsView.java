package com.eduhdsdk.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.classroomsdk.Config;
import com.classroomsdk.common.GlobalToolsType;
import com.classroomsdk.common.ToolsFormType;
import com.classroomsdk.common.ToolsPenType;
import com.classroomsdk.common.ToolsType;
import com.classroomsdk.manage.SharePadMgr;
import com.classroomsdk.manage.WhiteBoradConfig;
import com.eduhdsdk.R;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.toolcase.ToolsEraserPopupWindow;
import com.eduhdsdk.toolcase.ToolsFontPopupWindow;
import com.eduhdsdk.toolcase.ToolsFormPopupWindow;
import com.eduhdsdk.toolcase.ToolsPenPopupWindow;
import com.eduhdsdk.ui.holder.TKBaseRootHolder;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;


public class ToolsView {
    private Context mContext;
    private boolean isShow = true;

    private int viewWidth;
    private int viewHeight;

    //画笔pop
    private ToolsPenPopupWindow mtoolsPenPopupWindow;
    //文字
    private ToolsFontPopupWindow mtoolsFontPopupWindow;
    //形状
    private ToolsFormPopupWindow mtoolsFormPopupWindow;
    //橡皮
    private ToolsEraserPopupWindow mtoolsEraserPopupWindow;
    /**
     * 那种类型
     */
    private ToolsType mToolsType = ToolsType.defaule;
    private TKBaseRootHolder mRootHolder;
    private View view;

    public ToolsView(Context context, TKBaseRootHolder mTKBaseRootHolder, View view) {
        this.mContext = context;
        this.mRootHolder = mTKBaseRootHolder;
        this.view = view;
        initPop();

        //只对学生生效
        if (TKRoomManager.getInstance().getMySelf().role == 2) {
            //涂鸦工具隐藏鼠标：开启后画笔栏不显示鼠标
            if (RoomControler.isHiddenMouse()) {
                mRootHolder.iv_default.setVisibility(View.GONE);
                mRootHolder.view1.setVisibility(View.GONE);
                mRootHolder.view2.setVisibility(View.GONE);
                if (mRootHolder.iv_pen.getVisibility() == View.VISIBLE) {
                    selectToolPen();//如果默认工具条默认箭头和形状隐藏，默认的画笔为钢笔
                }
            }
            // 开启后画笔栏不显示形状工具
            if (RoomControler.isHiddenShapeTool()) {
                mRootHolder.iv_form.setVisibility(View.GONE);
                mRootHolder.view7.setVisibility(View.GONE);
                mRootHolder.view8.setVisibility(View.GONE);
                if (mRootHolder.iv_default.getVisibility() == View.GONE && mRootHolder.iv_pen.getVisibility() == View.VISIBLE) {
                    selectToolPen();
                }
            }
        }
    }

    /**
     * 如果默认工具条默认箭头和形状隐藏，默认的画笔为钢笔
     */
    private void setDefaultTools() {
        mRootHolder.iv_pen.setImageResource(R.drawable.tk_tools_pen_selected);
        mToolsType = ToolsType.pen;
        //设置画布画笔类型
        WhiteBoradConfig.getsInstance().setToolsType(mToolsType);
        //设置顶层画笔显示还是隐藏
        WhiteBoradConfig.getsInstance().setVisibilityTop(true);
        GlobalToolsType.global_type = mToolsType;
        sendToolType(false);
    }

    /**
     * 是否显示tools
     *
     * @param isShow
     */
    public void showTools(boolean isShow) {
        //当回放时 不显示工具条
        if (isShow && TKRoomManager.getInstance().getMySelf().role >= 0) {
            mRootHolder.tools_include.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) mRootHolder.tools_include.getLayoutParams();
            layoutParams.height = (int) (viewHeight);
            layoutParams.width = (int) (viewHeight* 0.152);
            mRootHolder.tools_include.setLayoutParams(layoutParams);
            WhiteBoradConfig.getsInstance().setToolsType(mToolsType);
        } else {
            mRootHolder.tools_include.setVisibility(View.GONE);
            //关闭PopupWindow
            dismissPop();
            WhiteBoradConfig.getsInstance().setToolsType(ToolsType.defaule);
        }
        if (mToolsType == ToolsType.defaule) {
            WhiteBoradConfig.getsInstance().setHideDraw(true);
        } else {
            WhiteBoradConfig.getsInstance().setHideDraw(false);
        }
    }

    /**
     * 关闭PopupWindow
     */
    public void dismissPop() {
        if (mtoolsPenPopupWindow != null) {
            mtoolsPenPopupWindow.colorSelectorView.cleanDefaultColor();
            mtoolsPenPopupWindow.dismisspop();
        }

        if (mtoolsFontPopupWindow != null) {
            mtoolsFontPopupWindow.colorSelectorView.cleanDefaultColor();
            mtoolsFontPopupWindow.dismisspop();
        }

        if (mtoolsFormPopupWindow != null) {
            mtoolsFormPopupWindow.colorSelectorView.cleanDefaultColor();
            mtoolsFormPopupWindow.dismisspop();
        }

        if (mtoolsEraserPopupWindow != null) {
            mtoolsEraserPopupWindow.dismisspop();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tool_default) {//鼠标
                selectToolDefault();

                sendToolType(true);

            } else if (id == R.id.tools_pen) {//画笔
                if (mtoolsPenPopupWindow != null) {
                    selectToolPen();
                    mtoolsPenPopupWindow.showPopPen(mRootHolder.iv_pen, mRootHolder.tools_include.getWidth(), view);
                    sendToolType(false);
                }
            } else if (id == R.id.tools_font) {//文字
                if (mtoolsFontPopupWindow != null) {
                    mRootHolder.iv_default.setImageResource(R.drawable.tk_tools_mouse_default);
                    mRootHolder.iv_pen.setImageResource(R.drawable.tk_tools_pen_default);
                    mRootHolder.iv_font.setImageResource(R.drawable.tk_tools_text_selected);
                    mRootHolder.iv_form.setImageResource(R.drawable.tk_tools_juxing_default);
                    mRootHolder.iv_eraser.setImageResource(R.drawable.tk_tools_xiangpi_default);
                    mToolsType = ToolsType.font;
                    //设置画布画笔类型
                    WhiteBoradConfig.getsInstance().setToolsType(mToolsType);
                    GlobalToolsType.global_type = mToolsType;
                    //设置顶层画笔显示还是隐藏
                    WhiteBoradConfig.getsInstance().setVisibilityTop(true);
                    WhiteBoradConfig.getsInstance().setHideDraw(false);
                    mtoolsFontPopupWindow.showPopPen(mRootHolder.iv_font, mRootHolder.tools_include.getWidth());
                }
                sendToolType(false);

            } else if (id == R.id.tools_form) {//矩形框
                if (mtoolsFormPopupWindow != null) {
                    mRootHolder.iv_default.setImageResource(R.drawable.tk_tools_mouse_default);
                    mRootHolder.iv_pen.setImageResource(R.drawable.tk_tools_pen_default);
                    mRootHolder.iv_font.setImageResource(R.drawable.tk_tools_text_default);
                    mRootHolder.iv_form.setImageResource(R.drawable.tk_tools_juxing_selected);
                    mRootHolder.iv_eraser.setImageResource(R.drawable.tk_tools_xiangpi_default);
                    mToolsType = ToolsType.form;
                    //设置画布画笔类型
                    WhiteBoradConfig.getsInstance().setToolsType(mToolsType);
                    //设置顶层画笔显示还是隐藏
                    WhiteBoradConfig.getsInstance().setVisibilityTop(true);
                    GlobalToolsType.global_type = mToolsType;
                    WhiteBoradConfig.getsInstance().setHideDraw(false);
                    mtoolsFormPopupWindow.showPopPen(mRootHolder.iv_form, mRootHolder.tools_include.getWidth());
                }
                sendToolType(false);

            } else if (id == R.id.tools_eraser) {//橡皮檫
                if (mtoolsEraserPopupWindow != null) {
                    mRootHolder.iv_default.setImageResource(R.drawable.tk_tools_mouse_default);
                    mRootHolder.iv_pen.setImageResource(R.drawable.tk_tools_pen_default);
                    mRootHolder.iv_font.setImageResource(R.drawable.tk_tools_text_default);
                    mRootHolder.iv_form.setImageResource(R.drawable.tk_tools_juxing_default);
                    mRootHolder.iv_eraser.setImageResource(R.drawable.tk_tools_xiangpi_selected);
                    mToolsType = ToolsType.eraser;
                    //设置画布画笔类型
                    WhiteBoradConfig.getsInstance().setToolsType(mToolsType);
                    //设置顶层画笔显示还是隐藏
                    WhiteBoradConfig.getsInstance().setVisibilityTop(false);
                    GlobalToolsType.global_type = mToolsType;
                    WhiteBoradConfig.getsInstance().setHideDraw(false);
                    mtoolsEraserPopupWindow.showPopPen(mRootHolder.iv_eraser, mRootHolder.tools_include.getWidth());

                }
                sendToolType(false);

            } else if (id == R.id.iv_top_arrows || id == R.id.tools_top) {//展开or关闭
                if (!isShow) {
                    isShow = true;
                    mRootHolder.tools_form_all.setVisibility(View.VISIBLE);
                    mRootHolder.tools_bottom_line.setVisibility(View.VISIBLE);
                    mRootHolder.iv_top_arrow.setImageResource(R.drawable.tk_tools_jiantou_top);
                    ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) mRootHolder.tools_include.getLayoutParams();
                    layoutParams.height = (int) (viewHeight);
                    layoutParams.width = (int) (viewHeight* 0.152);
                    mRootHolder.tools_include.setLayoutParams(layoutParams);
                } else {
                    isShow = false;
                    mRootHolder.tools_form_all.setVisibility(View.GONE);
                    mRootHolder.tools_bottom_line.setVisibility(View.GONE);
                    mRootHolder.iv_top_arrow.setImageResource(R.drawable.tk_tools_jiantou_buttom);
                    ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) mRootHolder.tools_include.getLayoutParams();
                    layoutParams.height = mRootHolder.tools_top.getMeasuredHeight() + mRootHolder.iv_top_arrow.getMeasuredHeight();
                    layoutParams.width = (int) (viewHeight  * 0.152);
                    mRootHolder.tools_include.setLayoutParams(layoutParams);
                }
            }
        }
    };

    SharePadMgr.SelectMouseListener mSelectMouseListener = new SharePadMgr.SelectMouseListener() {
        @Override
        public void selectMouse(boolean select) {
            if (select) {
                selectToolDefault();
            } else {
                selectToolPen();
                setToolPenColor();
            }
        }
    };

    /**
     * 选中鼠标
     */
    private void selectToolDefault() {
        mRootHolder.iv_default.setImageResource(R.drawable.tk_tools_mouse_selected);
        mRootHolder.iv_pen.setImageResource(R.drawable.tk_tools_pen_default);
        mRootHolder.iv_font.setImageResource(R.drawable.tk_tools_text_default);
        mRootHolder.iv_form.setImageResource(R.drawable.tk_tools_juxing_default);
        mRootHolder.iv_eraser.setImageResource(R.drawable.tk_tools_xiangpi_default);
        mToolsType = ToolsType.defaule;
        //设置画布画笔类型
        WhiteBoradConfig.getsInstance().setToolsType(mToolsType);
        //设置顶层画笔显示还是隐藏
        if (WhiteBoradConfig.getsInstance().getCurrentFileDoc().isDynamicPPT()
                || WhiteBoradConfig.getsInstance().getCurrentFileDoc().isH5Docment()) {
            WhiteBoradConfig.getsInstance().setVisibilityTop(false);
        } else {
            WhiteBoradConfig.getsInstance().setVisibilityTop(true);
        }
        WhiteBoradConfig.getsInstance().setHideDraw(true);
        GlobalToolsType.global_type = mToolsType;
    }

    /**
     * 选中画笔
     */
    private void selectToolPen() {
        mRootHolder.iv_default.setImageResource(R.drawable.tk_tools_mouse_default);
        mRootHolder.iv_pen.setImageResource(R.drawable.tk_tools_pen_selected);
        mRootHolder.iv_font.setImageResource(R.drawable.tk_tools_text_default);
        mRootHolder.iv_form.setImageResource(R.drawable.tk_tools_juxing_default);
        mRootHolder.iv_eraser.setImageResource(R.drawable.tk_tools_xiangpi_default);
        mToolsType = ToolsType.pen;
        //设置画布画笔类型
        WhiteBoradConfig.getsInstance().setToolsType(mToolsType);
        //设置顶层画笔显示还是隐藏
        WhiteBoradConfig.getsInstance().setVisibilityTop(true);
        GlobalToolsType.global_type = mToolsType;
        WhiteBoradConfig.getsInstance().setHideDraw(false);
    }

    //设置画笔颜色
    public void setToolPenColor() {
        ConcurrentHashMap<String, Object> properties = TKRoomManager.getInstance().getMySelf().properties;
        if (properties.containsKey("primaryColor")) {
            //未设置过颜色
            if (mtoolsPenPopupWindow.colorSelectorView.defaultIndex < 0) {
                GlobalToolsType.global_pencolor = Color.parseColor((String) properties.get("primaryColor"));
                WhiteBoradConfig.getsInstance().setmToolsPenColor(Color.parseColor((String) properties.get("primaryColor")));
            } else if (mToolsType == ToolsType.pen && !Config.mColor[mtoolsPenPopupWindow.colorSelectorView.mSelectIndex].equals(properties.get("primaryColor"))) {
                //当本地的颜色和信令颜色不一致时按本地标准
                GlobalToolsType.global_pencolor = Color.parseColor(Config.mColor[mtoolsPenPopupWindow.colorSelectorView.mSelectIndex]);
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                        "__all", "primaryColor", Config.mColor[mtoolsPenPopupWindow.colorSelectorView.mSelectIndex]);
            }
        }
    }

    public void doLayout(int viewWidth, int viewHeight) {
        if (this.viewHeight == viewHeight && this.viewWidth == viewWidth) {
            return;
        }
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        if (isShow) {
            mRootHolder.tools_form_all.setVisibility(View.VISIBLE);
            mRootHolder.tools_bottom_line.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRootHolder.tools_include.getLayoutParams();
            layoutParams.height = (int) (viewHeight * 0.8);
            layoutParams.width = (int) (viewHeight * 0.8 * 0.152);
            layoutParams.rightMargin = 8;
            mRootHolder.tools_include.setLayoutParams(layoutParams);
        } else {
            mRootHolder.tools_form_all.setVisibility(View.GONE);
            mRootHolder.tools_bottom_line.setVisibility(View.GONE);
            mRootHolder.iv_top_arrow.setImageResource(R.drawable.tk_tools_jiantou_buttom);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRootHolder.tools_include.getLayoutParams();
            layoutParams.height = mRootHolder.tools_top.getMeasuredHeight() + mRootHolder.iv_top_arrow.getMeasuredHeight();
            layoutParams.width = (int) (viewHeight * 0.8 * 0.152);
            layoutParams.rightMargin = 8;
            mRootHolder.tools_include.setLayoutParams(layoutParams);
        }
    }

    /**
     * 发送更改画笔类型
     *
     * @param b true=鼠标
     */
    public void sendToolType(boolean b) {
        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            JSONObject data = new JSONObject();
            try {
                data.put("sourceInstanceId", "default");
                data.put("selectMouse", b);
                TKRoomManager.getInstance().pubMsg("whiteboardMarkTool", "whiteboardMarkTool", "__all", data.toString(), true, null, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 初始化pop
     */
    private void initPop() {
        //笔
        mtoolsPenPopupWindow = new ToolsPenPopupWindow(mContext, true, true, 1);
        mtoolsPenPopupWindow.SetOnToolsListener(new ToolsPenPopupWindow.onToolsPenListener() {
            @Override
            public void SelectedColor(int color) {
                //设置画笔颜色
                WhiteBoradConfig.getsInstance().setmToolsPenColor(color);
                GlobalToolsType.global_pencolor = color;
            }

            @Override
            public void SelectedPen(ToolsPenType penType) {
                //设置画笔类型
                WhiteBoradConfig.getsInstance().setmToolsPenType(penType);
                GlobalToolsType.global_pentype = penType;
            }

            @Override
            public void SeekBarProgress(int progress) {
                //设置画笔大小
                WhiteBoradConfig.getsInstance().setmToolsPenProgress(progress);
                GlobalToolsType.global_pensize = progress;
            }
        });
        //文字
        mtoolsFontPopupWindow = new ToolsFontPopupWindow(mContext, true);
        mtoolsFontPopupWindow.SetOnToolsListener(new ToolsFontPopupWindow.onToolsPenListener() {
            @Override
            public void SelectedColor(int color) {
                //设置文字颜色
                WhiteBoradConfig.getsInstance().setmToolsFontColor(color);
                GlobalToolsType.global_fontcolor = color;
            }

            @Override
            public void SeekBarProgress(int progress) {
                //设置文字大小
                WhiteBoradConfig.getsInstance().setmToolsFontSize(progress);
                GlobalToolsType.global_fontsize = progress;
            }
        });
        //形状
        mtoolsFormPopupWindow = new ToolsFormPopupWindow(mContext);
        mtoolsFormPopupWindow.SetOnToolsListener(new ToolsFormPopupWindow.onToolsFormListener() {
            @Override
            public void SelectedColor(int color) {
                //图形颜色
                WhiteBoradConfig.getsInstance().setmToolsFormColor(color);
                GlobalToolsType.global_formcolor = color;
            }

            @Override
            public void SelectedForm(ToolsFormType penType) {
                //图像类型
                WhiteBoradConfig.getsInstance().setmToolsFormType(penType);
                GlobalToolsType.global_formtype = penType;
            }

            @Override
            public void SeekBarProgress(int progress) {
                //图形大小
                WhiteBoradConfig.getsInstance().setmToolsFormWidth(progress);
                GlobalToolsType.global_formsize = progress;
            }
        });
        //橡皮
        mtoolsEraserPopupWindow = new ToolsEraserPopupWindow(mContext, true);
        mtoolsEraserPopupWindow.SetonToolsListener(new ToolsEraserPopupWindow.onToolsListener() {
            @Override
            public void SeekBarSize(int size) {
                //橡皮擦大小
                WhiteBoradConfig.getsInstance().setmToolsEraserWidth(size);
                GlobalToolsType.global_erasersize = size;
            }
        });
    }

    public void setonClick() {
        //工具监听
        mRootHolder.tools_top.setOnClickListener(onClickListener);
        mRootHolder.iv_default.setOnClickListener(onClickListener);
        mRootHolder.iv_pen.setOnClickListener(onClickListener);
        mRootHolder.iv_font.setOnClickListener(onClickListener);
        mRootHolder.iv_form.setOnClickListener(onClickListener);
        mRootHolder.iv_eraser.setOnClickListener(onClickListener);
        mRootHolder.iv_top_arrow.setOnClickListener(onClickListener);
        SharePadMgr.getInstance().setSelectMouseListener(mSelectMouseListener);
    }
}
