package com.eduhdsdk.toolcase;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.ui.BasePopupWindow;
import com.eduhdsdk.ui.SwitchButton;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import skin.support.content.res.SkinCompatResources;

/**
 * 布局切换
 * Created by hxd on 2019/4/8 0027.
 */

public class LayoutPopupWindow implements View.OnClickListener {

    public static final int LAYOUT_NORMAL = 1;
    public static final int LAYOUT_DOUBLE = 2;
    public static final int LAYOUT_VIDEO = 3;

    private static LayoutPopupWindow instance;
    public PopupWindow toolsPopupWindow;
    private View contentView;
    private ImageView up_arr;
    private LinearLayout ll_layout, ll_layout_normal, ll_layout_double, ll_layout_video;
    private CheckBox cb_layout_normal, cb_layout_double, cb_layout_video;
    private TextView tv_layout_normal, tv_layout_double, tv_layout_video;
    LinearLayout ll_bottom;
    SwitchButton sync_switch_button;
    boolean initSwitchButton;

    private Context mContext;
    private boolean isSync = false;
    private SwitchLayout mSwitchLayout;
    public int layoutState = 1;
    private String pubMsgName = "oneToOne";
    private int mType;

    public static synchronized LayoutPopupWindow getInstance() {
        if (instance == null) {
            instance = new LayoutPopupWindow();
        }
        return instance;
    }

    public void resetInstance(){
        instance = null;
    }

    /***
     * @param activity
     * @param type       0为一对一    1为一对多
     */
    public void setActivityAndCall(Activity activity, int type) {
        this.mContext = activity;
        this.mType = type;
        initPopupWindow();
    }

    public void setSwitchLayout(SwitchLayout switchLayout) {
        this.mSwitchLayout = switchLayout;
    }

    public void initPopupWindow() {
        if (mType == 0) {
            contentView = LayoutInflater.from(mContext).inflate(R.layout.tk_layout_one_pop, null);
        } else {
            contentView = LayoutInflater.from(mContext).inflate(R.layout.tk_layout_many_pop, null);
        }

        ScreenScale.scaleView(contentView, "LayoutPopupWindow");
        up_arr = contentView.findViewById(R.id.up_arr);
        ll_layout = contentView.findViewById(R.id.ll_layout);
        ll_layout_normal = contentView.findViewById(R.id.ll_layout_normal);
        ll_layout_double = contentView.findViewById(R.id.ll_layout_double);
        ll_layout_video = contentView.findViewById(R.id.ll_layout_video);
        ll_bottom = contentView.findViewById(R.id.ll_bottom);
        sync_switch_button = contentView.findViewById(R.id.sync_switch_button);
        initSwitchButton = true;

        ll_layout_normal.setOnClickListener(this);
        ll_layout_double.setOnClickListener(this);
        ll_layout_video.setOnClickListener(this);

        cb_layout_normal = contentView.findViewById(R.id.cb_layout_normal);
        cb_layout_double = contentView.findViewById(R.id.cb_layout_double);
        cb_layout_video = contentView.findViewById(R.id.cb_layout_video);

        tv_layout_normal = contentView.findViewById(R.id.tv_layout_normal);
        tv_layout_double = contentView.findViewById(R.id.tv_layout_double);
        tv_layout_video = contentView.findViewById(R.id.tv_layout_video);

        ll_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ll_bottom.getLayoutParams();
                layoutParams.width = ll_layout.getMeasuredWidth();
                ll_bottom.setLayoutParams(layoutParams);
            }
        });

        refreshItem();

        /*sync_switch_button.setOnCheckListener(new SwitchButton.OnCheckListener() {
            @Override
            public void onCheck(boolean isCheck) {
                isSync = isCheck;
                if (isSync ) {
                    setPubMsg();
                }
                if (initSwitchButton) {
                    initSwitchButton = false;
                } else {
                    dismiss();
                }
            }
        });*/

        toolsPopupWindow = new BasePopupWindow(mContext);
        toolsPopupWindow.setContentView(contentView);
        toolsPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        toolsPopupWindow.setFocusable(false);
        toolsPopupWindow.setOutsideTouchable(true);
    }

    public void showPopupWindow(View rootView) {
        if (toolsPopupWindow == null) {
            initPopupWindow();
        }
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int[] reb_wb_board = new int[2];
        rootView.getLocationInWindow(reb_wb_board);

        RelativeLayout.LayoutParams up_arr_params = (RelativeLayout.LayoutParams) up_arr.getLayoutParams();
        if (ScreenScale.getScreenWidth() - (reb_wb_board[0] + rootView.getWidth() / 2) < contentView.getMeasuredWidth() / 2) {
            up_arr_params.setMargins(contentView.getMeasuredWidth() / 2 + reb_wb_board[0] + rootView.getWidth() / 4 - (ScreenScale.getScreenWidth() - contentView.getMeasuredWidth() + contentView.getMeasuredWidth() / 2)
                    , 0, 0, 0);
        } else {
            up_arr_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }

        up_arr.setLayoutParams(up_arr_params);
        int xPos = rootView.getWidth() / 2 - contentView.getMeasuredWidth() / 2;
        if (toolsPopupWindow != null) {
            toolsPopupWindow.showAsDropDown(rootView, xPos, 0, Gravity.BOTTOM);
        }

        if (initSwitchButton) {
            sync_switch_button.turnOn();
        }
    }

    public void dismiss() {
        if (toolsPopupWindow != null) {
            if (toolsPopupWindow.isShowing()) {
                toolsPopupWindow.dismiss();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ll_layout_normal) {//常规布局
            pubMsgName = mType == 0 ? "oneToOne" : "CoursewareDown";
            if (RoomSession.isClassBegin) {
                setPubMsg();
            } else {
                clickItem(LAYOUT_NORMAL, pubMsgName);
            }
        } else if (i == R.id.ll_layout_double) {//双师布局
            pubMsgName = mType == 0 ? "oneToOneDoubleDivision" : "MainPeople";
            if (RoomSession.isClassBegin) {
                setPubMsg();
            } else {
                clickItem(LAYOUT_DOUBLE, pubMsgName);
            }
        } else if (i == R.id.ll_layout_video) {//视频布局
            pubMsgName = mType == 0 ? "oneToOneDoubleVideo" : "OnlyVideo";
            if (RoomSession.isClassBegin) {
                setPubMsg();
            } else {
                clickItem(LAYOUT_VIDEO, pubMsgName);
            }
        }
        dismiss();
    }

    public void clickItem(int state, String name) {
        pubMsgName = name;
        if (layoutState == state) {
            return;
        }
        layoutState = state;
        refreshItem();
        if (mSwitchLayout != null) {
            mSwitchLayout.toSwitch(layoutState);
        }
    }

    private void refreshItem() {
        switch (layoutState) {
            case LAYOUT_NORMAL:
                cb_layout_normal.setChecked(true);
                cb_layout_double.setChecked(false);
                cb_layout_video.setChecked(false);

                tv_layout_normal.setTextColor(SkinCompatResources.getColor(mContext, R.color.color_0077FF));
                tv_layout_double.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_layout_video.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            case LAYOUT_DOUBLE:  // 双师/主讲视频
                cb_layout_normal.setChecked(false);
                cb_layout_double.setChecked(true);
                cb_layout_video.setChecked(false);

                tv_layout_normal.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_layout_double.setTextColor(SkinCompatResources.getColor(mContext, R.color.color_0077FF));
                tv_layout_video.setTextColor(mContext.getResources().getColor(R.color.white));
                break;
            case LAYOUT_VIDEO:  // 双师/主讲视频
                cb_layout_normal.setChecked(false);
                cb_layout_double.setChecked(false);
                cb_layout_video.setChecked(true);

                tv_layout_normal.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_layout_double.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_layout_video.setTextColor(SkinCompatResources.getColor(mContext, R.color.color_0077FF));
                break;
        }
    }

    public void setPubMsg() {
        try {
            JSONObject data = new JSONObject();
            data.put("nowLayout", pubMsgName);
            TKRoomManager.getInstance().pubMsg("switchLayout", "switchLayout", "__all",
                    data.toString(), true, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public PopupWindow getLayoutPopupWindow() {
        return toolsPopupWindow;
    }

    public void reset() {
        layoutState = 1;
        pubMsgName = "oneToOne";
    }

    public interface SwitchLayout {
        void toSwitch(int layoutState);
    }
}
