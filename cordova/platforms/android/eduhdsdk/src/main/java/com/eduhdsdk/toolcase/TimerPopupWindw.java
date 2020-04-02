package com.eduhdsdk.toolcase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.classroomsdk.manage.WBSession;
import com.classroomsdk.viewUi.WheelView;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.interfaces.ShowingPopupWindowInterface;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.tools.MovePopupwindowTouchListener;
import com.eduhdsdk.tools.PopupWindowTools;
import com.eduhdsdk.viewutils.PlayBackSeekPopupWindow;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.eduhdsdk.toolcase.ToolsPopupWindow.TOOLS_JISHIQI;

/**
 * Created by fucc on 2018/12/29.
 * 计时器
 */

public class TimerPopupWindw implements View.OnClickListener, MovePopupwindowTouchListener.onMoveListener {

    private static TimerPopupWindw instance;

    private LinearLayout contentView;

    private TextView timer_title;
    private WheelView hourPicker;
    private WheelView minuterPicker;
    private PopupWindow timerPopupWindow;
    private LinearLayout startTimerContent;
    private RelativeLayout timingContent;
    private LinearLayout timerControl;
    private FrameLayout ll_ponit;
    private TextView tv_start_timer;
    private ImageView img_stop_timer;
    //倒计时时间
    private int time = 0;
    //倒计时状态
    private boolean isTiming = false;
    //handle线程状态
    private boolean isRun = false;
    /**
     * num1：分钟十位数  num2：分钟个位  num3: 秒十位数  num4 :秒十位数
     */
    private TextView num1;
    private TextView num2;
    private TextView num3;
    private TextView num4;
    private ImageView pause_timer_img;

    //倒计时
    @SuppressLint("HandlerLeak")
    private Handler myhandler = new Handler();
    private Runnable mRunnable;
    private ImageView stu_pause;
    private ImageView closePopup;
    private MovePopupwindowTouchListener movePopupwindowTouchListener;//拖动
    private ShowingPopupWindowInterface showingPopupWindowInterface;//显示的回调
    private TimeEndCallBack timeEndCallBack;//计时结束

    private ImageView time_img_point_up;
    private ImageView time_img_point_down;

    private Context mContext;
    private View mRootView;
    private boolean isShow = false;
    private double moveX, movieY;
    private int offsetX, offsetY;
    private boolean isHaiping;

    public static synchronized TimerPopupWindw getInstance() {
        if (instance == null) {
            instance = new TimerPopupWindw();
        }
        return instance;
    }

    public void resetInstance() {
        instance = null;
    }

    public void setActivity(Context mContext) {
        this.mContext = mContext;
    }

    public void initPopupWindow() {
        contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.tk_layout_tools_timer, null);
        timer_title = contentView.findViewById(R.id.timer_title);
        hourPicker = contentView.findViewById(R.id.wp_hour_timer);
        minuterPicker = contentView.findViewById(R.id.wp_mintur_timer);
        initPicker();
        stu_pause = contentView.findViewById(R.id.img_pause_stu_timer);
        ll_ponit = contentView.findViewById(R.id.ll_ponit);
        time_img_point_up = contentView.findViewById(R.id.time_img_point_up);
        time_img_point_down = contentView.findViewById(R.id.time_img_point_down);

        num1 = contentView.findViewById(R.id.tv_hour_num1_timer);
        num2 = contentView.findViewById(R.id.tv_hour_num2_timer);
        num3 = contentView.findViewById(R.id.tv_hour_num3_timer);
        num4 = contentView.findViewById(R.id.tv_hour_num4_timer);

        //设置点击事件
        tv_start_timer = contentView.findViewById(R.id.tv_start_timer);
        tv_start_timer.setOnClickListener(this);
        closePopup = contentView.findViewById(R.id.img_close_timer);
        closePopup.setOnClickListener(this);
        img_stop_timer = contentView.findViewById(R.id.img_stop_timer);
        img_stop_timer.setOnClickListener(this);
        pause_timer_img = contentView.findViewById(R.id.img_pause_timer);
        pause_timer_img.setOnClickListener(this);
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {//巡课，隐藏计时器开始按钮
            tv_start_timer.setVisibility(View.GONE);
            closePopup.setVisibility(View.INVISIBLE);
            hourPicker.setNoScroll(true);
            minuterPicker.setNoScroll(true);
        }
        //布局切换
        startTimerContent = contentView.findViewById(R.id.tools_content_start_timer);
        timingContent = contentView.findViewById(R.id.tools_content_timing_timer);
        timerControl = contentView.findViewById(R.id.tools_content_control);
        if (timerPopupWindow == null) {
            timerPopupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        timerPopupWindow.setContentView(contentView);

        // 设置PopupWindow的背景
        timerPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        timerPopupWindow.setOutsideTouchable(false);
        // 设置PopupWindow是否能响应点击事件
        timerPopupWindow.setTouchable(true);
        contentView.setTag(TOOLS_JISHIQI);

        if (movePopupwindowTouchListener == null) {
            movePopupwindowTouchListener = new MovePopupwindowTouchListener(timerPopupWindow, mContext);
            movePopupwindowTouchListener.setOnMoveListener(this);
        }
        contentView.setOnTouchListener(movePopupwindowTouchListener);

        timerPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ToolsPopupWindow.getInstance().setTimerBtnReset();
            }
        });
    }

    public void setVisibility(int visibility) {
        if (timerPopupWindow != null) {
            if (visibility == View.GONE) {
                timerPopupWindow.dismiss();
                timerPopupWindow.setTouchable(false);
            } else if (visibility == View.VISIBLE) {
                if (mRootView != null && isShow) {
                    showPopupWindow(mRootView);
                    if (moveX != 0 || movieY != 0) {
                        movePopupWindow(mRootView, moveX, movieY, isHaiping);
                    }
                    if (offsetX != 0 || offsetY != 0) {
                        PopupWindowTools.movePopupWindow(timerPopupWindow, offsetX, offsetY);
                    }
                }
                timerPopupWindow.setTouchable(true);
            }
            timerPopupWindow.update();
        }
    }

    private void initPicker() {
        hourPicker.setSeletion(5);
        minuterPicker.setSeletion(0);
        hourPicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (TKRoomManager.getInstance().getMySelf().role == -1) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        minuterPicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (TKRoomManager.getInstance().getMySelf().role == -1) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void showPopupWindow(View rootView) {
        isShow = true;
        mRootView = rootView;
        if (contentView == null) {
            initPopupWindow();
        }
        movePopupwindowTouchListener.setView(rootView);
        if (!timerPopupWindow.isShowing()) {
            if (showingPopupWindowInterface != null) {
                showingPopupWindowInterface.popupWindowShowing(TOOLS_JISHIQI);
            }

            if (WBSession.roomtype == 0) {//0是一对一教室
                int width = rootView.getMeasuredHeight() * 2 / 3;
                hourPicker.setRootWidth(width);
                minuterPicker.setRootWidth(width);
                timerPopupWindow.setWidth(width);
                doLayout(width);
            } else {
                int width = rootView.getMeasuredHeight() * 2 / 3;
                hourPicker.setRootWidth(width);
                minuterPicker.setRootWidth(width);
                timerPopupWindow.setWidth(width);
                doLayout(width);
            }
            int[] location = new int[2];
            rootView.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            int width = rootView.getMeasuredWidth();
            int height = rootView.getMeasuredHeight();
            int pw = width - timerPopupWindow.getWidth();
            int ph = height - timerPopupWindow.getHeight();

            /*timerPopupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, x + pw / 2, y + ph / 2);*/

            if (mRunnable != null) {
                if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_STUDENT) {
                    showTimingView();
                } else {
                    showStudenView();
                }
            } else {
                if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_STUDENT) {
                    showStartTimerView();
                } else {
                    showStudenView();
                }
            }
            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL || TKRoomManager.getInstance().getMySelf().role == -1) {//巡课 回放
                closePopup.setEnabled(false);
                img_stop_timer.setEnabled(false);
                pause_timer_img.setEnabled(false);
            }
            timerPopupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, x + pw / 2, y + ph / 5);

            if (RoomInfo.getInstance().getRoomType() == 0) {
                if (LayoutPopupWindow.getInstance().layoutState == 3) {
                    instance.setVisibility(View.GONE);
                }
            } else {
                if (LayoutPopupWindow.getInstance().layoutState != 1) {
                    instance.setVisibility(View.GONE);
                }
            }

        }
    }

    public PopupWindow getPopupWindow() {
        return timerPopupWindow;
    }

    public void dismiss() {
        isShow = false;
        if (timerPopupWindow != null) {
            if (timerPopupWindow.isShowing()) {
                timerPopupWindow.dismiss();
            }
            stopTimer();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_start_timer) {
            //时间选择在滚动时候
            if (hourPicker.isScroll || minuterPicker.isScroll || TKRoomManager.getInstance().getMySelf().role == 4
                    || TKRoomManager.getInstance().getMySelf().role == -1) {
                return;
            }
            time = Integer.parseInt(hourPicker.getSeletedItem()) * 60 + Integer.parseInt(minuterPicker.getSeletedItem())+2 ;
            hourPicker.setSeletion(5);
            minuterPicker.setSeletion(0);
            doLayout(contentView.getWidth());
            showTimingView();
            //开始倒计时
            startTimer();
            sendMsg(true, false, false);
        } else if (i == R.id.img_close_timer) {
            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                TKRoomManager.getInstance().delMsg("timer", "timerMesg",
                        "__all", new HashMap<String, Object>());
                dismiss();
            }
        } else if (i == R.id.img_stop_timer) {
            //结束定时
            stopTimer();
            sendMsg(false, true, false);
        } else if (i == R.id.img_pause_timer) {
            //暂停定时
            pauseOrStartTimer();
            sendMsg(isTiming, false, false);
        }
    }

    //展示学生看到的界面
    private void showStudenView() {
        timingContent.setVisibility(View.VISIBLE);
        startTimerContent.setVisibility(View.GONE);
        timerControl.setVisibility(View.GONE);
        closePopup.setVisibility(View.GONE);
        stu_pause.setVisibility(View.GONE);
        if (isTiming) {
            stu_pause.setVisibility(View.GONE);
        } else {
            stu_pause.setVisibility(View.VISIBLE);
        }
    }

    //展示计时器选择时间界面
    private void showStartTimerView() {
        startTimerContent.setVisibility(View.VISIBLE);

        hourPicker.setSeletion(5);
        minuterPicker.setSeletion(0);
        if (TKRoomManager.getInstance().getMySelf().role != 4) {
            closePopup.setVisibility(View.VISIBLE);
        }
        timerControl.setVisibility(View.GONE);
        timingContent.setVisibility(View.GONE);
    }

    //展示倒计时界面
    private void showTimingView() {
        timingContent.setVisibility(View.VISIBLE);
        if (TKRoomManager.getInstance().getMySelf().role == 2 || TKRoomManager.getInstance().getMySelf().role == 4) {//当前用户为巡课时 隐藏按钮。
            timerControl.setVisibility(View.GONE);
            closePopup.setVisibility(View.GONE);
        } else {
            timerControl.setVisibility(View.VISIBLE);
            closePopup.setVisibility(View.VISIBLE);
        }
        stu_pause.setVisibility(View.GONE);
        startTimerContent.setVisibility(View.GONE);
        if (time > 0) {
            pause_timer_img.setClickable(true);
            pause_timer_img.setImageResource(R.drawable.tk_tools_timer_pause_default);
        } else {
            pause_timer_img.setImageResource(R.drawable.tk_tools_timer_pause_disable);
            pause_timer_img.setClickable(false);
            isTiming = false;
        }
    }

    //开始倒计时
    public void startTimer() {
        if (time <= 0 || mRunnable != null || isTiming) {
            return;
        }
        stu_pause.setVisibility(View.GONE);
        setShowTimeNun(time);
        isTiming = true;
        mRunnable = new MyRunnable();
        myhandler.postDelayed(mRunnable, 1000);
    }

    private void stopTimer() {
        showStartTimerView();
        time = 0;
        isTiming = false;
        myhandler.removeCallbacks(mRunnable);
        mRunnable = null;
    }

    private void pauseOrStartTimer() {
        if (mRunnable == null) {
            return;
        }
        //暂停定时
        isTiming = !isTiming;
        if (TKRoomManager.getInstance().getMySelf().role != 2) {
            if (isTiming) {
                pause_timer_img.setImageResource(R.drawable.tk_tools_timer_pause_default);
            } else {
                pause_timer_img.setImageResource(R.drawable.tk_tools_timer_start_default);
            }
        } else {
            if (isTiming || TKRoomManager.getInstance().getMySelf().role == 4) {
                stu_pause.setVisibility(View.GONE);
            } else {
                stu_pause.setVisibility(View.VISIBLE);
            }
        }
        if (isTiming && mRunnable != null) {
            myhandler.postDelayed(mRunnable, 1000);
        } else {
            myhandler.removeCallbacks(mRunnable);
        }
    }

    private void setShowTimeNun(int num) {
        int minute = num / 60;
        int second = num % 60;
        num1.setText("" + minute / 10);
        num2.setText("" + minute % 10);
        num3.setText("" + second / 10);
        num4.setText("" + second % 10);
    }

    @Override
    public void onMove(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }


    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            if (time > 0 && isTiming) {
                time--;
                setShowTimeNun(time);
                myhandler.postDelayed(this, 1000);
            }
            if (time == 0) {
                pause_timer_img.setImageResource(R.drawable.tk_tools_timer_pause_disable);
                pause_timer_img.setClickable(false);
                isTiming = false;
                if (timeEndCallBack != null) {
                    timeEndCallBack.endCallBack();
                }
            }
        }
    }

    /**
     * 拖动位置
     *
     * @param rootView
     * @param moveX
     * @param movieY
     */
    public void movePopupWindow(View rootView, double moveX, double movieY, boolean isHaiping) {
        mRootView = rootView;
        this.moveX = moveX;
        this.movieY = movieY;
        this.isHaiping = isHaiping;
        PopupWindowTools.movePopupWindow(timerPopupWindow, rootView, moveX, movieY, isHaiping);
    }

    //处理信令信息
    public void disposeMsg(JSONObject jsonObject, long ts, boolean inList) {
        int totalTime = 0;
        int t = 0;
        JSONArray sutdentTimerArry = jsonObject.optJSONArray("sutdentTimerArry");
        if (sutdentTimerArry == null || sutdentTimerArry.length() < 4) {
            return;
        }


        if (System.currentTimeMillis() / 1000 - ts > 0) {
            t = (int) (System.currentTimeMillis() / 1000 - ts);
        }

        try {
            totalTime = (Integer.parseInt(sutdentTimerArry.get(0).toString()) * 10 + Integer.parseInt(sutdentTimerArry.get(1).toString())) * 60 +
                    Integer.parseInt(sutdentTimerArry.get(2).toString()) * 10 + Integer.parseInt(sutdentTimerArry.get(3).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NumberFormatException e){
            e.printStackTrace();
        }


        if (t == 0 || jsonObject.optBoolean("isRestart")) {
            time = totalTime;
        } else {
            if (inList && !jsonObject.optBoolean("isStatus")) {
                time = totalTime;
            } else {
                if (t > totalTime) {
                    time = 0;
                } else {
                    time = totalTime - t;
                }
            }
        }

        setShowTimeNun(time);

        if (jsonObject.optBoolean("isRestart")) {
            isTiming = false;
            myhandler.removeCallbacks(mRunnable);
            mRunnable = null;
            if (TKRoomManager.getInstance().getMySelf().role == 2) {
                stu_pause.setVisibility(View.VISIBLE);
            } else if (TKRoomManager.getInstance().getMySelf().role == 4) {
                stu_pause.setVisibility(View.VISIBLE);
                stopTimer();
            }
            return;
        }

        if (jsonObject.optBoolean("isStatus")) {
            if (mRunnable == null) {
                showTimingView();
                startTimer();
                return;
            }
            if (!isTiming) {
                pauseOrStartTimer();
            }
        } else {
            if (inList) {
                showTimingView();
                startTimer();
            }
            if (isTiming) {
                pauseOrStartTimer();
            }
        }
    }

    //发送信令信息
    public void sendMsg(boolean isStatus, boolean isRestart, boolean isShow) {
//        int[] nums = {time / 60 / 10, time / 60 % 10, time % 60 / 10, time % 60 % 10};

        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(time / 60 / 10);
        if (isRestart) {
            jsonArray.put(5);
        } else {
            jsonArray.put(time / 60 % 10);
        }
        jsonArray.put(time % 60 / 10);
        jsonArray.put(time % 60 % 10);
        try {
            data.put("isStatus", isStatus);
            data.put("sutdentTimerArry", jsonArray);
            data.put("isShow", isShow);
            data.put("isRestart", isRestart);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TKRoomManager.getInstance().pubMsg("timer",
                "timerMesg", "__all", data.toString(),
                true, "ClassBegin", null);
    }

    public void doLayout(int width) {
        LinearLayout.LayoutParams hourPickerLayoutParams = (LinearLayout.LayoutParams) startTimerContent.getLayoutParams();
        hourPickerLayoutParams.height = width * 143 / 341;
        startTimerContent.setLayoutParams(hourPickerLayoutParams);

        timer_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * 0.15 / 3));
        LinearLayout.LayoutParams tv_start_timerparams = (LinearLayout.LayoutParams) tv_start_timer.getLayoutParams();
        tv_start_timerparams.width = (int) (width * 0.15);
        tv_start_timerparams.height = (int) (width * 0.15);
        tv_start_timer.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * 0.15 / 3));
        tv_start_timer.setLayoutParams(tv_start_timerparams);

        int w = width;//popupwindow的宽-两边据-间距

        RelativeLayout.LayoutParams num1Params = (RelativeLayout.LayoutParams) num1.getLayoutParams();
        num1Params.width = (int) (w * 0.11);
        num1Params.height = (int) (w * 0.11 * 1.28);
        num1.setTextSize((float) (w * 0.11 * 1.28 / 4));
        num1.setLayoutParams(num1Params);

        RelativeLayout.LayoutParams num2Params = (RelativeLayout.LayoutParams) num2.getLayoutParams();
        num2Params.width = (int) (w * 0.11);
        num2Params.height = (int) (w * 0.11 * 1.28);
//        num2Params.leftMargin = 40;
//        num2Params.rightMargin = 40;
        num2.setTextSize((float) (w * 0.11 * 1.28 / 4));
        num2.setLayoutParams(num2Params);

        RelativeLayout.LayoutParams num3Params = (RelativeLayout.LayoutParams) num3.getLayoutParams();
        num3Params.width = (int) (w * 0.11);
        num3Params.height = (int) (w * 0.11 * 1.28);
//        num3Params.leftMargin = 40;
        num3.setTextSize((float) (w * 0.11 * 1.28 / 4));
        num3.setLayoutParams(num3Params);

        RelativeLayout.LayoutParams num4Params = (RelativeLayout.LayoutParams) num4.getLayoutParams();
        num4Params.width = (int) (w * 0.11);
        num4Params.height = (int) (w * 0.11 * 1.28);
//        num4Params.leftMargin = 40;
        num4.setTextSize((float) (w * 0.11 * 1.28 / 4));
        num4.setLayoutParams(num4Params);

        LinearLayout.LayoutParams img_stop_timerParams = (LinearLayout.LayoutParams) img_stop_timer.getLayoutParams();
        img_stop_timerParams.width = (int) (w * 0.1);
        img_stop_timerParams.height = (int) (w * 0.1);
        img_stop_timer.setLayoutParams(img_stop_timerParams);

        LinearLayout.LayoutParams img_pause_timerParams = (LinearLayout.LayoutParams) pause_timer_img.getLayoutParams();
        img_pause_timerParams.width = (int) (w * 0.1);
        img_pause_timerParams.height = (int) (w * 0.1);
        pause_timer_img.setLayoutParams(img_pause_timerParams);

        RelativeLayout.LayoutParams ll_ponitParams = (RelativeLayout.LayoutParams) ll_ponit.getLayoutParams();
        ll_ponitParams.height = (int) (w * 0.11 * 1.28 / 2);
        ll_ponit.setLayoutParams(ll_ponitParams);

        FrameLayout.LayoutParams time_img_point_upParams = (FrameLayout.LayoutParams) time_img_point_up.getLayoutParams();
        time_img_point_upParams.width = w / 60;
        time_img_point_upParams.height = w / 60;
        time_img_point_up.setLayoutParams(time_img_point_upParams);

        FrameLayout.LayoutParams time_img_point_downParams = (FrameLayout.LayoutParams) time_img_point_down.getLayoutParams();
        time_img_point_downParams.width = w / 60;
        time_img_point_downParams.height = w / 60;
        time_img_point_down.setLayoutParams(time_img_point_downParams);
    }

    public void setTimeEndCallBack(TimeEndCallBack timeEndCallBack) {
        this.timeEndCallBack = timeEndCallBack;
    }

    public void setShowingPopupWindowInterface(ShowingPopupWindowInterface showingPopupWindowInterface) {
        this.showingPopupWindowInterface = showingPopupWindowInterface;
    }

    public void setPlayBackSeekPopupWindow(PlayBackSeekPopupWindow window) {
        if (window != null) {
            window.setPlayBackListener(new PlayBackSeekPopupWindow.PlayBackListener() {
                @Override
                public void playingState(boolean state) {
                    if (TKRoomManager.getInstance().getMySelf().role == -1) {
                        if (state && mRunnable != null) {
                            myhandler.postDelayed(mRunnable, 1000);
                        } else {
                            myhandler.removeCallbacks(mRunnable);
                        }
                    }
                }
            });
        }
    }

    /**
     * 计时结束的回调
     */
    public interface TimeEndCallBack {
        void endCallBack();
    }

}
