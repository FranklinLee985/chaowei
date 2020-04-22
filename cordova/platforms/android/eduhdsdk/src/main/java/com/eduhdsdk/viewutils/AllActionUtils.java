package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.ui.BasePopupWindow;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import skin.support.content.res.SkinCompatResources;

/**
 * Created by Administrator on 2018/4/24/024.
 * 全体操作弹窗
 */

public class AllActionUtils implements View.OnClickListener {

    private Activity activity;
    private PopupWindow actionWindow;
    private AllPopupWindowClick all_popup_click;

    private ImageView up_arr, iv_mute, iv_unmute, iv_send_gift, iv_all_recovery, iv_all_audio;
    private LinearLayout ll_mute, ll_unmute, ll_send_gift, ll_all_recovery, ll_all_audio;
    private TextView txt_mute;
    private TextView txt_unmute;
    private TextView txt_send_gift;
    private TextView txt_all_recovery, txt_all_audio;
    private View contentView;
    private boolean mShowAllActionView;
    //判断弹框弹出时，用户的点击是在底部控件的内部还是外部
    boolean isInView = true;


    public AllActionUtils(Activity activity, AllActionUtils.AllPopupWindowClick all_popup_click) {
        this.activity = activity;
        this.all_popup_click = all_popup_click;
        initPop();
    }

    private void initPop() {
        contentView = LayoutInflater.from(activity).inflate(R.layout.tk_layout_all_action_pop, null);
        ScreenScale.scaleView(contentView, "AllActionUtils");
        actionWindow = new BasePopupWindow(activity);

        ll_mute = (LinearLayout) contentView.findViewById(R.id.ll_mute);
        ll_unmute = (LinearLayout) contentView.findViewById(R.id.ll_unmute);
        ll_send_gift = (LinearLayout) contentView.findViewById(R.id.ll_send_gift);
        ll_all_recovery = (LinearLayout) contentView.findViewById(R.id.ll_all_recovery);
        ll_all_audio = (LinearLayout) contentView.findViewById(R.id.ll_all_audio);

        up_arr = contentView.findViewById(R.id.up_arr);
        iv_mute = (ImageView) contentView.findViewById(R.id.iv_mute);
        iv_unmute = (ImageView) contentView.findViewById(R.id.iv_unmute);
        iv_send_gift = (ImageView) contentView.findViewById(R.id.iv_send_gift);
        iv_all_recovery = (ImageView) contentView.findViewById(R.id.iv_all_recovery);
        iv_all_audio = (ImageView) contentView.findViewById(R.id.iv_all_audio);

        txt_mute = contentView.findViewById(R.id.txt_mute);
        txt_unmute = contentView.findViewById(R.id.txt_unmute);
        txt_send_gift = contentView.findViewById(R.id.txt_send_gift);
        txt_all_recovery = contentView.findViewById(R.id.txt_all_recovery);
        txt_all_audio = contentView.findViewById(R.id.txt_all_audio);
        txt_all_audio.setText(R.string.audio_teaching);

        if (RoomInfo.getInstance().getRoomType() == 0) {
            ll_mute.setVisibility(View.GONE);
            ll_unmute.setVisibility(View.GONE);
            ll_send_gift.setVisibility(View.GONE);
            ll_all_recovery.setVisibility(View.GONE);

            ll_all_audio.setOnClickListener(this);
        } else {
            ll_mute.setVisibility(View.VISIBLE);
            ll_unmute.setVisibility(View.VISIBLE);
            ll_send_gift.setVisibility(View.VISIBLE);
            ll_all_recovery.setVisibility(View.VISIBLE);

            ll_mute.setOnClickListener(this);
            ll_unmute.setOnClickListener(this);
            ll_send_gift.setOnClickListener(this);
            ll_all_recovery.setOnClickListener(this);
        }

        if (RoomControler.isSwitchAudioClassroom()) {
            ll_all_audio.setVisibility(View.VISIBLE);
            ll_all_audio.setOnClickListener(this);
        } else {
            ll_all_audio.setVisibility(View.GONE);
        }
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        actionWindow.setWidth(contentView.getMeasuredWidth());
        actionWindow.setContentView(contentView);
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        //这里给它设置了弹出的时间，
//        imm.toggleSoftInput(1000, InputMethodManager.HIDE_NOT_ALWAYS);
        actionWindow.setBackgroundDrawable(new BitmapDrawable());
        actionWindow.setFocusable(false);
        actionWindow.setOutsideTouchable(true);

        actionWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (all_popup_click != null) {
                    all_popup_click.all_control_window_close();
                }
            }
        });
    }

    public void showAllActionView(final View view, final View cb_view, boolean isMute,
                                  boolean is_have_student, boolean showAllActionView) {
        if (contentView == null) {
            return;
        }

        if (isMute) {
            setAllMute();
        } else {
            setAllTalk();
        }

        if (!is_have_student) {
            setNoStudent();
        }

        mShowAllActionView = showAllActionView;
        setTeachingState(showAllActionView);


        actionWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isInView = Tools.isInView(event, cb_view);
                return false;
            }
        });

        if (RoomSession._bigroom) {
            setGifStatu();
        }

        int[] reb_wb_board = new int[2];
        view.getLocationInWindow(reb_wb_board);

        RelativeLayout.LayoutParams up_arr_params = (RelativeLayout.LayoutParams) up_arr.getLayoutParams();
        if (ScreenScale.getScreenWidth() - (reb_wb_board[0] + view.getWidth() / 2) < contentView.getMeasuredWidth() / 2) {
            up_arr_params.setMargins(contentView.getMeasuredWidth() / 2 + reb_wb_board[0] + view.getWidth() / 4 - (ScreenScale.getScreenWidth() - contentView.getMeasuredWidth() + contentView.getMeasuredWidth() / 2)
                    , 0, 0, 0);
        } else {
            up_arr_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }

        up_arr.setLayoutParams(up_arr_params);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xPos = view.getWidth() / 2 - contentView.getMeasuredWidth() / 2;
        actionWindow.showAsDropDown(view, xPos, 0, Gravity.BOTTOM);
    }

    @Override
    public void onClick(View v) {
        dismissPopupWindow();
        if (v.getId() == R.id.ll_mute) {
            allMute();
        } else if (v.getId() == R.id.ll_unmute) {
            allUnmute();
        } else if (v.getId() == R.id.ll_send_gift) {
            if (all_popup_click != null) {
                all_popup_click.all_send_gift();
            }
        } else if (v.getId() == R.id.ll_all_recovery) {
            if (all_popup_click != null) {
                all_popup_click.all_recovery();
            }
        } else if (v.getId() == R.id.ll_all_audio) {
            mShowAllActionView = !mShowAllActionView;
            changeTeachingState();
        }
    }

    /***
     *  全体发言
     */
    private void allUnmute() {
        RoomSession.getInstance().getPlatformMemberList();
        for (int i = 0; i < RoomSession.playingList.size(); i++) {
            RoomUser roomUser = RoomSession.playingList.get(i);
            if (roomUser.role == 2) {
                if (roomUser.getPublishState() == 2) {
                    TKRoomManager.getInstance().changeUserProperty(roomUser.peerId,
                            "__all", "publishstate", 3);
                } else if (roomUser.getPublishState() == 4) {
                    TKRoomManager.getInstance().changeUserProperty(roomUser.peerId,
                            "__all", "publishstate", 1);
                }
            }
        }
    }

    /***
     *   全体禁言
     */
    private void allMute() {
        RoomSession.getInstance().getPlatformMemberList();
        for (int i = 0; i < RoomSession.playingList.size(); i++) {
            RoomUser roomUser = RoomSession.playingList.get(i);
            if (roomUser.role == 2) {
                if (roomUser.getPublishState() == 3) {
                    TKRoomManager.getInstance().changeUserProperty(roomUser.peerId,
                            "__all", "publishstate", 2);
                } else if (roomUser.getPublishState() == 1) {
                    TKRoomManager.getInstance().changeUserProperty(roomUser.peerId,
                            "__all", "publishstate", 4);
                }
            }
        }
    }

    /**
     * 课堂状态改变
     */
    private void changeTeachingState() {
        if (mShowAllActionView) {
            TKRoomManager.getInstance().pubMsg("OnlyAudioRoom", "OnlyAudioRoom",
                    "__all", null, true, null, null);
        } else {
            TKRoomManager.getInstance().delMsg("OnlyAudioRoom", "OnlyAudioRoom",
                    "__all", null);
        }
    }

    public void dismissPopupWindow() {
        if (actionWindow != null) {
            actionWindow.dismiss();
        }
    }

    public void setGifStatu() {
        if (actionWindow != null && iv_send_gift != null) {
            iv_send_gift.setImageResource(R.drawable.tk_jiangli_default);
            ll_send_gift.setClickable(false);
        }
    }

    /**
     * 全体静音
     */
    public void setAllMute() {
        if (iv_mute != null && iv_unmute != null) {
            ll_mute.setClickable(false);
            ll_unmute.setClickable(true);
            ll_all_recovery.setClickable(true);
            ll_send_gift.setClickable(true);
            iv_all_recovery.setImageResource(R.drawable.tk_fuwei_default);
            txt_all_recovery.setTextColor(Color.WHITE);
            iv_send_gift.setImageResource(R.drawable.tk_jiangli_default);
            txt_send_gift.setTextColor(Color.WHITE);
            iv_mute.setImageResource(R.drawable.tk_jingyin_disable);
            txt_mute.setTextAppearance(activity, R.style.unselect_action);
            iv_unmute.setImageResource(R.drawable.tk_button_talk_all);
            txt_unmute.setTextColor(Color.WHITE);
        }
    }

    /**
     * 全体发言
     */
    public void setAllTalk() {
        if (iv_mute != null && iv_unmute != null) {
            ll_unmute.setClickable(false);
            ll_mute.setClickable(true);
            ll_all_recovery.setClickable(true);
            ll_send_gift.setClickable(true);
            iv_unmute.setImageResource(R.drawable.tk_button_talk_all_unclickable);
            txt_unmute.setTextAppearance(activity, R.style.unselect_action);
            iv_mute.setImageResource(R.drawable.tk_jingyin_default);
            txt_mute.setTextColor(Color.WHITE);
            iv_all_recovery.setImageResource(R.drawable.tk_fuwei_default);
            txt_all_recovery.setTextColor(Color.WHITE);
            iv_send_gift.setImageResource(R.drawable.tk_jiangli_default);
            txt_send_gift.setTextColor(Color.WHITE);
        }
    }

    /**
     * 台上没有学生
     */
    public void setNoStudent() {
        if (iv_mute != null && iv_unmute != null) {
            ll_all_recovery.setClickable(false);
            ll_mute.setClickable(false);
            ll_send_gift.setClickable(false);
            ll_unmute.setClickable(false);
            iv_mute.setImageResource(R.drawable.tk_jingyin_disable);
            txt_mute.setTextAppearance(activity, R.style.unselect_action);
            iv_unmute.setImageResource(R.drawable.tk_button_talk_all_unclickable);
            txt_unmute.setTextAppearance(activity, R.style.unselect_action);
            iv_all_recovery.setImageResource(R.drawable.tk_fuwei_disable);
            txt_all_recovery.setTextAppearance(activity, R.style.unselect_action);
            iv_send_gift.setImageResource(R.drawable.tk_jiangli_disable);
            txt_send_gift.setTextAppearance(activity, R.style.unselect_action);
        }
    }

    /**
     * 设置课堂状态
     *
     * @param showAllActionView
     */
    public void setTeachingState(boolean showAllActionView) {
        if (showAllActionView) {
            iv_all_audio.setImageResource(R.drawable.tk_audio_open);
            txt_all_audio.setTextColor(SkinCompatResources.getColor(activity, R.color.all_action_audio_text));
        } else {
            iv_all_audio.setImageResource(R.drawable.tk_audio_default);
            txt_all_audio.setTextColor(activity.getResources().getColor(R.color.white));
        }
    }

    /**
     * 定义popupwindow的接口，通过接口和activity进行通信
     */
    public interface AllPopupWindowClick {

        void all_send_gift();

        void all_recovery();

        void all_control_window_close();
    }
}
