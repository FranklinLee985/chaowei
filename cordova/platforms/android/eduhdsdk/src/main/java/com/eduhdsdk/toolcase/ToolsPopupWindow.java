package com.eduhdsdk.toolcase;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.message.SendingSignalling;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.ui.BasePopupWindow;

/**
 * 工具箱
 * Created by Administrator on 2018/12/27 0027.
 */

public class ToolsPopupWindow implements View.OnClickListener {

    private static ToolsPopupWindow instance;
    public PopupWindow toolsPopupWindow;
    private View contentView;
    private ImageView up_arr;
    private LinearLayout ll_tools_datiqi, ll_tools_zhuanpan, ll_tools_jishiqi, ll_tools_qiangda, ll_tools_xiaobaiban;
    private CheckBox cb_tools_datiqi, cb_tools_zhuanpan, cb_tools_jishiqi, cb_tools_qiangda, cb_tools_xiaobaiban;

    private Context mContext;

    public static final int TOOLS_DATIQI = 1;//答题器
    public static final int TOOLS_ZHUANPAN = 2;//转盘
    public static final int TOOLS_JISHIQI = 3;//计时器
    public static final int TOOLS_QIANGDA = 4;//抢答
    public static final int TOOLS_XIAOBAIBAN = 5;//小白板

    public static synchronized ToolsPopupWindow getInstance() {
        if (instance == null) {
            instance = new ToolsPopupWindow();
        }
        return instance;
    }

    public void resetInstance() {
        instance = null;
    }

    public void setActivityAndCall(Activity activity) {
        this.mContext = activity;
        initPopupWindow();
    }

    public void initPopupWindow() {
        if (mContext == null)
            return;
        contentView = LayoutInflater.from(mContext).inflate(R.layout.tk_layout_tools_pop, null);

        ScreenScale.scaleView(contentView, "AllActionUtils");
        up_arr = contentView.findViewById(R.id.up_arr);
        ll_tools_datiqi = contentView.findViewById(R.id.ll_tools_datiqi);
        ll_tools_zhuanpan = contentView.findViewById(R.id.ll_tools_zhuanpan);
        ll_tools_jishiqi = contentView.findViewById(R.id.ll_tools_jishiqi);
        ll_tools_qiangda = contentView.findViewById(R.id.ll_tools_qiangda);
        ll_tools_xiaobaiban = contentView.findViewById(R.id.ll_tools_xiaobaiban);

        ll_tools_datiqi.setOnClickListener(this);
        ll_tools_zhuanpan.setOnClickListener(this);
        ll_tools_jishiqi.setOnClickListener(this);
        ll_tools_qiangda.setOnClickListener(this);
        ll_tools_xiaobaiban.setOnClickListener(this);

        cb_tools_datiqi = contentView.findViewById(R.id.cb_tools_datiqi);
        cb_tools_zhuanpan = contentView.findViewById(R.id.cb_tools_zhuanpan);
        cb_tools_jishiqi = contentView.findViewById(R.id.cb_tools_jishiqi);
        cb_tools_qiangda = contentView.findViewById(R.id.cb_tools_qiangda);
        cb_tools_xiaobaiban = contentView.findViewById(R.id.cb_tools_xiaobaiban);

        toolsPopupWindow = new BasePopupWindow(mContext);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        toolsPopupWindow.setWidth(contentView.getMeasuredWidth());
        toolsPopupWindow.setContentView(contentView);
        toolsPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        toolsPopupWindow.setFocusable(false);
        toolsPopupWindow.setOutsideTouchable(true);


        if (RoomInfo.getInstance().getRoomType() == 0) {
            ll_tools_qiangda.setVisibility(View.GONE);
        }
        if (!RoomControler.isHasAnswerMachine()) {
            ll_tools_datiqi.setVisibility(View.GONE);
        }
        if (!RoomControler.isHasTurntable()) {
            ll_tools_zhuanpan.setVisibility(View.GONE);
        }
        if (!RoomControler.isHasTimer()) {
            ll_tools_jishiqi.setVisibility(View.GONE);
        }
        if (!RoomControler.isHasResponderAnswer()) {
            ll_tools_qiangda.setVisibility(View.GONE);
        }
        if (!RoomControler.isHasWhiteBoard()) {
            ll_tools_xiaobaiban.setVisibility(View.GONE);
        }
    }

    public void showPopupWindow(View rootView) {
        if (contentView == null)
            return;
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
    }

    public PopupWindow getToolsPopupWindow() {
        return toolsPopupWindow;
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
        if (i == R.id.ll_tools_datiqi) {//答题器
            cb_tools_datiqi.setChecked(true);
            cb_tools_datiqi.setEnabled(false);
            ll_tools_datiqi.setEnabled(false);

            SendingSignalling.getInstance().sendAnswerMessage();

        } else if (i == R.id.ll_tools_zhuanpan) {//转盘

            cb_tools_zhuanpan.setChecked(true);
            cb_tools_zhuanpan.setEnabled(false);
            ll_tools_zhuanpan.setEnabled(false);

            ToolCaseMgr.getInstance().showPopupWindowForLottery(true, true);

        } else if (i == R.id.ll_tools_jishiqi) {//计时器

            cb_tools_jishiqi.setChecked(true);
            cb_tools_jishiqi.setEnabled(false);
            ll_tools_jishiqi.setEnabled(false);

            SendingSignalling.getInstance().sendTimerMessage();

        } else if (i == R.id.ll_tools_qiangda) {//抢答
            cb_tools_qiangda.setChecked(true);
            cb_tools_qiangda.setEnabled(false);
            ll_tools_qiangda.setEnabled(false);

            SendingSignalling.getInstance().sendResponderMessage();

        } else if (i == R.id.ll_tools_xiaobaiban) {//小白板
            cb_tools_xiaobaiban.setChecked(true);
            cb_tools_xiaobaiban.setEnabled(false);
            ll_tools_xiaobaiban.setEnabled(false);

            SendingSignalling.getInstance().sendSmallBoardMessage();
        }
        dismiss();
    }

    /**
     * 恢复 答题卡为可点击状态
     */
    public void setAnswerBtnReset() {
        if (cb_tools_datiqi != null) {
            if (cb_tools_datiqi.isChecked()) {
                cb_tools_datiqi.setChecked(false);
                cb_tools_datiqi.setEnabled(true);
                ll_tools_datiqi.setEnabled(true);
            }
        }
    }

    /**
     * 答题卡为不可点击状态
     */
    public void setAnswerBtnChecked() {
        if (cb_tools_datiqi != null) {
            if (!cb_tools_datiqi.isChecked()) {
                cb_tools_datiqi.setChecked(true);
                cb_tools_datiqi.setEnabled(false);
                ll_tools_datiqi.setEnabled(false);
            }
        }
    }

    /**
     * 恢复 抢答为可点击状态
     */
    public void setResponderBtnReset() {
        if (cb_tools_qiangda != null) {
            if (cb_tools_qiangda.isChecked()) {
                cb_tools_qiangda.setChecked(false);
                cb_tools_qiangda.setEnabled(true);
                ll_tools_qiangda.setEnabled(true);
            }
        }
    }

    /**
     * 抢答为不可点击状态
     */
    public void setResponderBtnChecked() {
        if (cb_tools_qiangda != null) {
            if (!cb_tools_qiangda.isChecked()) {
                cb_tools_qiangda.setChecked(true);
                cb_tools_qiangda.setEnabled(false);
                ll_tools_qiangda.setEnabled(false);
            }
        }
    }

    /**
     * 恢复 转盘为可点击状态
     */
    public void setLotteryBtnReset() {
        if (cb_tools_zhuanpan != null) {
            if (cb_tools_zhuanpan.isChecked()) {
                cb_tools_zhuanpan.setChecked(false);
                cb_tools_zhuanpan.setEnabled(true);
                ll_tools_zhuanpan.setEnabled(true);
            }
        }
    }

    /**
     * 转盘为不可点击状态
     */
    public void setLotteryBtnChecked() {
        if (cb_tools_zhuanpan != null) {
            if (!cb_tools_zhuanpan.isChecked()) {
                cb_tools_zhuanpan.setChecked(true);
                cb_tools_zhuanpan.setEnabled(false);
                ll_tools_zhuanpan.setEnabled(false);
            }
        }
    }

    /**
     * 恢复计时器为可点击状态
     */
    public void setTimerBtnReset() {
        if (cb_tools_jishiqi != null) {
            if (cb_tools_jishiqi.isChecked()) {
                cb_tools_jishiqi.setChecked(false);
                cb_tools_jishiqi.setEnabled(true);
                ll_tools_jishiqi.setEnabled(true);
            }
        }
    }

    /**
     * 计时器为不可点击状态
     */
    public void setTimerBtnChecked() {
        if (cb_tools_jishiqi != null) {
            if (!cb_tools_jishiqi.isChecked()) {
                cb_tools_jishiqi.setChecked(true);
                cb_tools_jishiqi.setEnabled(false);
                ll_tools_jishiqi.setEnabled(false);
            }
        }
    }

    /**
     * 恢复小白板为可点击状态
     */
    public void setWhiteBoardBtnReset() {
        if (cb_tools_xiaobaiban != null) {
            if (cb_tools_xiaobaiban.isChecked()) {
                cb_tools_xiaobaiban.setChecked(false);
                cb_tools_xiaobaiban.setEnabled(true);
                ll_tools_xiaobaiban.setEnabled(true);
            }
        }
    }

    /**
     * 小白板为不可点击状态
     */
    public void setWhiteBoardBtnChecked() {
        if (cb_tools_xiaobaiban != null) {
            if (!cb_tools_xiaobaiban.isChecked()) {
                cb_tools_xiaobaiban.setChecked(true);
                cb_tools_xiaobaiban.setEnabled(false);
                ll_tools_xiaobaiban.setEnabled(false);
            }
        }
    }
}
