package com.eduhdsdk.toolcase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.interfaces.ShowingPopupWindowInterface;
import com.eduhdsdk.room.RoomOperation;
import com.eduhdsdk.tools.MovePopupwindowTouchListener;
import com.eduhdsdk.tools.PopupWindowTools;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

import skin.support.SkinCompatManager;
import skin.support.content.res.SkinCompatResources;

import static com.eduhdsdk.toolcase.ToolsPopupWindow.TOOLS_QIANGDA;

/**
 * 抢答器
 * Created by YF on 2018/12/29 0029.
 */

public class ResponderPopupWindow implements View.OnClickListener, MovePopupwindowTouchListener.onMoveListener {
    private PopupWindow popupWindow;
    private ImageView responder_img_close;
    private ImageView responder_img_gif;
    private TextView responder_tv_hint;
    private TextView responder_tv_btn;
    private TimeHandler handler;
    private MovePopupwindowTouchListener movePopupwindowTouchListener;//拖动
    private ShowingPopupWindowInterface showingPopupWindowInterface;//显示的回调
    private Context mActivity;
    private View rootView;
    public View contentView;
    private boolean isShow = false;
    private double moveX, movieY;
    private int offsetX, offsetY;
    private boolean isHaiping;

    private static ResponderPopupWindow instance;

    @Override
    public void onMove(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void resetInstance() {
        instance = null;
    }

    public static ResponderPopupWindow getInstance() {
        if (instance == null) {
            instance = new ResponderPopupWindow();
        }
        return instance;
    }

    private ResponderPopupWindow() {

    }

    public void setmActivity(Context activity) {
        this.mActivity = activity;
    }

    /**
     * 初始化popupwindow
     */
    public void initPopupWindow() {
        contentView = LayoutInflater.from(mActivity).inflate(R.layout.tk_layout_tools_responder, null);

        responder_img_close = contentView.findViewById(R.id.responder_img_close);
        responder_img_gif = contentView.findViewById(R.id.responder_img_gif);
        responder_tv_hint = contentView.findViewById(R.id.responder_tv_hint);
        responder_tv_btn = contentView.findViewById(R.id.responder_tv_btn);

        responder_img_close.setOnClickListener(this);
        responder_tv_btn.setOnClickListener(this);
        showGif();

        if (TKRoomManager.getInstance().getMySelf().role == 4) {
            responder_img_close.setVisibility(View.GONE);
            responder_tv_btn.setBackgroundResource(R.drawable.tk_tools_btn_select);
            responder_tv_btn.setTextColor(SkinCompatResources.getColor(mActivity, R.color.tk_tools_btn_text_select_color));
        }

        if (handler == null) {
            handler = new TimeHandler();
        }
        if (popupWindow == null) {
            popupWindow = new PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        popupWindow.setContentView(contentView);
        // 设置PopupWindow的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        popupWindow.setOutsideTouchable(false);
        // 设置PopupWindow是否能响应点击事件
        popupWindow.setTouchable(true);
        contentView.setTag(TOOLS_QIANGDA);
        if (movePopupwindowTouchListener == null) {
            movePopupwindowTouchListener = new MovePopupwindowTouchListener(popupWindow, mActivity);
            movePopupwindowTouchListener.setOnMoveListener(this);
        }
        contentView.setOnTouchListener(movePopupwindowTouchListener);

        popupWindow.setOnDismissListener(
                new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ToolsPopupWindow.getInstance().setResponderBtnReset();
                    }
                });
    }

    public void setVisibility(int visibility) {
        if (popupWindow != null) {
            if (visibility == View.GONE) {
                popupWindow.dismiss();
                popupWindow.setTouchable(false);
            } else if (visibility == View.VISIBLE) {
                if (rootView != null && isShow) {
                    showPopupWindow(rootView);
                    if (moveX != 0 || movieY != 0) {
                        movePopupWindow(rootView, moveX, movieY, isHaiping);
                    }
                    if (offsetX != 0 || offsetY != 0) {
                        PopupWindowTools.movePopupWindow(popupWindow, offsetX, offsetY);
                    }
                }
                popupWindow.setTouchable(true);
            }
            popupWindow.update();
        }
    }

    public void showPopupWindow(View rootView) {
        isShow = true;
        if (contentView == null) {
            initPopupWindow();
        }
        this.rootView = rootView;
        movePopupwindowTouchListener.setView(rootView);
        if (!popupWindow.isShowing()) {
            responder_tv_btn.setEnabled(true);
            if (TKRoomManager.getInstance().getMySelf().role == 4) {
                responder_tv_hint.setText(mActivity.getString(R.string.responder_click));
                responder_tv_btn.setText(mActivity.getString(R.string.responder_nostart));
            } else {
                responder_tv_hint.setText(mActivity.getString(R.string.responder_click));
                responder_tv_btn.setText(mActivity.getString(R.string.responder_start));
            }
        }
//        isRole();
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {//巡课
            responder_img_close.setEnabled(false);
            responder_tv_btn.setEnabled(false);
        }
        if (showingPopupWindowInterface != null) {
            showingPopupWindowInterface.popupWindowShowing(TOOLS_QIANGDA);
        }
        popupWindow.setWidth(rootView.getMeasuredHeight() / 5 * 3);
        popupWindow.setHeight(rootView.getMeasuredHeight() / 5 * 3);

        showGif();
        dolayout(rootView.getMeasuredHeight() / 5 * 3);
        int[] location = new int[2];
        rootView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        int pw = width - popupWindow.getWidth();
        int ph = height - popupWindow.getHeight();
        popupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, x + pw / 2, y + ph / 2);
        if (LayoutPopupWindow.getInstance().layoutState != 1) {
            instance.setVisibility(View.GONE);
        }
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    /**
     * 显示抢答 Gif 图
     */
    private void showGif() {
        if (((Activity) mActivity).isFinishing()) {
            return;
        }
        String name = SkinCompatManager.getInstance().getCurSkinName();
        if (!TextUtils.isEmpty(name)) {
            if (name.equals("black_skin.zip")) {
                Glide.with(mActivity).asGif().load(R.drawable.tk_qiangdaqi_gif_black)
                        .into(responder_img_gif);
            } else if (name.equals("orange_skin.zip")) {
                Glide.with(mActivity).asGif().load(R.drawable.tk_qiangdaqi_gif_orange)
                        .into(responder_img_gif);
            }
        } else {
            Glide.with(mActivity).asGif().load(R.drawable.tk_qiangdaqi_gif)
                    .into(responder_img_gif);
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
        this.rootView = rootView;
        this.moveX = moveX;
        this.movieY = movieY;
        this.isHaiping = isHaiping;
        PopupWindowTools.movePopupWindow(popupWindow, rootView, moveX, movieY, isHaiping);
    }

    public void setRandom() {
        Random rand = new Random();
        float x = rand.nextFloat();
        float y = rand.nextFloat();
        movePopupWindow(rootView, x, y, false);
    }

    private int randomCount = 0;
    private Handler randomHandler = new Handler();
    private Runnable randomRunnable = new Runnable() {
        @Override
        public void run() {
            if (randomCount < 3) {
                randomCount++;
                if (randomCount == 2) {
                    if (TKRoomManager.getInstance().getMySelf().role == 4) {
                        responder_tv_hint.setText(mActivity.getString(R.string.responder_click));
                        responder_tv_btn.setText(mActivity.getString(R.string.responder_nostart));
                    } else {
                        responder_tv_hint.setText(mActivity.getString(R.string.responder_click));
                        responder_tv_btn.setText(mActivity.getString(R.string.responder_start));
                    }
                    responder_tv_btn.setEnabled(true);
                }
                setRandom();
                randomHandler.postDelayed(this, 1000);
            } else {
                randomCount = 0;
                randomHandler.removeCallbacks(this);
            }
        }
    };

    /**
     * 判断用户的身份
     *
     * @param isShow 是否显示
     * @param begin  是否考试抢答
     * @param ts     信令发送的时间
     */
    public void isRole(boolean isShow, boolean begin, long ts) {
        if (TKRoomManager.getInstance().getMySelf().role == 2) {
            int t = (int) ((RoomOperation.serviceTime - ts));
            if (t > 0) {
                if (t < 8) {
                    if (t < 3) {
                        responder_img_close.setVisibility(View.GONE);
                        if (TKRoomManager.getInstance().getMySelf().role == 4) {
                            responder_tv_hint.setText(mActivity.getString(R.string.responder_nostart));
                            responder_tv_btn.setText(mActivity.getString(R.string.responder_nostart));
                        } else {
                            responder_tv_hint.setText(mActivity.getString(R.string.responder_ready));
                            responder_tv_btn.setText(mActivity.getString(R.string.responder_ready));
                        }
                        responder_tv_btn.setEnabled(false);
                        handler.removeCallbacks(timeRunable);
                        handler.postDelayed(timeRunable, 1000 * (8 - t));
                        randomHandler.removeCallbacks(randomRunnable);
                        randomCount = 0;
                        randomHandler.postDelayed(randomRunnable, 1000);
                    } else {
                        responder_img_close.setVisibility(View.GONE);
                        if (TKRoomManager.getInstance().getMySelf().role == 4) {
                            responder_tv_hint.setText(mActivity.getString(R.string.responder_click));
                            responder_tv_btn.setText(mActivity.getString(R.string.responder_nostart));
                        } else {
                            responder_tv_hint.setText(mActivity.getString(R.string.responder_click));
                            responder_tv_btn.setText(mActivity.getString(R.string.responder_start));
                        }
                        responder_tv_btn.setEnabled(true);
                        handler.removeCallbacks(timeRunable);
                        handler.postDelayed(timeRunable, 1000 * (8 - t));
//                    randomHandler.removeCallbacks(randomRunnable);
                        randomCount = 0;
//                    randomHandler.postDelayed(randomRunnable, 1000);
                    }
                } else {
                    responder_img_close.setVisibility(View.GONE);
                    responder_tv_hint.setText(mActivity.getString(R.string.responder_nobody));
                    responder_tv_btn.setText(mActivity.getString(R.string.responder_nobody));
                    responder_tv_btn.setEnabled(false);
                    handler.removeCallbacks(timeRunable);
//                handler.postDelayed(timeRunable, 1000 * 8);
                    randomHandler.removeCallbacks(randomRunnable);
                    randomCount = 0;
//                randomHandler.postDelayed(randomRunnable, 1000);
                }
            } else {
                responder_img_close.setVisibility(View.GONE);
                if (TKRoomManager.getInstance().getMySelf().role == 4) {
                    responder_tv_btn.setText(mActivity.getString(R.string.responder_nostart));
                    responder_tv_btn.setText(mActivity.getString(R.string.responder_nostart));
                } else {
                    responder_tv_hint.setText(mActivity.getString(R.string.responder_ready));
                    responder_tv_btn.setText(mActivity.getString(R.string.responder_ready));
                }
                responder_tv_btn.setEnabled(false);
                handler.removeCallbacks(timeRunable);
                handler.postDelayed(timeRunable, 8 * 1000);
                randomHandler.removeCallbacks(randomRunnable);
                randomCount = 0;
                randomHandler.postDelayed(randomRunnable, 1000);
            }


        } else {
            if (isShow && begin) {
                if (RoomOperation.serviceTime - ts > 0) {
                    int t = (int) ((RoomOperation.serviceTime - ts));
                    if (t < 8) {
                        responder_tv_hint.setText(mActivity.getString(R.string.responder_answering));
                        responder_tv_btn.setEnabled(false);
                        responder_tv_btn.setText(mActivity.getString(R.string.responder_answering_other));
                        handler.removeCallbacks(timeRunable);
                        handler.postDelayed(timeRunable, 1000 * (8 - t));
                    } else {
                        responder_tv_hint.setText(mActivity.getString(R.string.responder_nobody));
                        if (TKRoomManager.getInstance().getMySelf().role == 4) {
                            responder_tv_btn.setText(mActivity.getString(R.string.responder_nostart));
                        } else {
                            responder_tv_btn.setText(mActivity.getString(R.string.responder_restart));
                        }
                        responder_tv_btn.setEnabled(true);
                    }
                } else {
                    responder_tv_hint.setText(mActivity.getString(R.string.responder_answering));
                    responder_tv_btn.setEnabled(false);
                    responder_tv_btn.setText(mActivity.getString(R.string.responder_answering_other));
                    handler.removeCallbacks(timeRunable);
                    handler.postDelayed(timeRunable, 1000 * 8);
                }
            } else {
                responder_tv_btn.setEnabled(true);
                if (TKRoomManager.getInstance().getMySelf().role == 4) {
                    responder_tv_hint.setText(mActivity.getString(R.string.responder_click));
                    responder_tv_btn.setText(mActivity.getString(R.string.responder_nostart));
                } else {
                    responder_tv_hint.setText(mActivity.getString(R.string.responder_click));
                    responder_tv_btn.setText(mActivity.getString(R.string.responder_start));
                }
                //handler.removeCallbacks(timeRunable);
                //handler.postDelayed(timeRunable, 1000 * 8);
            }
        }
        if (TKRoomManager.getInstance().getMySelf().role == 4
                || TKRoomManager.getInstance().getMySelf().role == -1) {//巡课和回放
            responder_img_close.setEnabled(false);
            responder_tv_btn.setEnabled(false);
        }
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    /**
     * 有人抢中
     */
    public void setTextView(String username) {
        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            if (responder_tv_btn.isEnabled()) {
                return;
            }
        } else if (TKRoomManager.getInstance().getMySelf().role == 2) {
            if (!responder_tv_btn.isEnabled()) {
                return;
            }
        }

        if (handler != null) {
            handler.removeCallbacks(timeRunable);
        }

        if (TKRoomManager.getInstance().getMySelf().role == 0 || TKRoomManager.getInstance().getMySelf().role == 4) {
            responder_tv_hint.setText(username);
            responder_tv_btn.setText(mActivity.getString(R.string.responder_rob));
            responder_tv_btn.setEnabled(true);
        } else if (TKRoomManager.getInstance().getMySelf().role == 2) {
            responder_tv_btn.setEnabled(false);
            responder_tv_btn.setText(mActivity.getString(R.string.responder_already));
            if (TKRoomManager.getInstance().getMySelf().nickName.equals(username)) {
                responder_tv_hint.setText(mActivity.getString(R.string.responder_winning));
            } else {
                responder_tv_hint.setText(username);
            }
            randomHandler.removeCallbacks(randomRunnable);
            randomCount = 0;
            movePopupWindow(rootView, 0.5, 0.5, false);
        } else {
            responder_tv_hint.setText(username);
            responder_tv_btn.setText(mActivity.getString(R.string.responder_rob));
        }
        changeSelectState();
    }

    public void dismiss() {
        isShow = false;
        changeUnselectState();
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                handler.removeCallbacks(timeRunable);
                randomHandler.removeCallbacks(randomRunnable);
                randomCount = 0;
                popupWindow.dismiss();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.responder_img_close) {//关闭popup
            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                TKRoomManager.getInstance().delMsg("qiangDaQi", "qiangDaQiMesg", "__all", new HashMap<String, Object>());
                dismiss();
            }
        } else if (i == R.id.responder_tv_btn) {//开始抢答
            if (TKRoomManager.getInstance().getMySelf().role == 2) {
                try {
                    JSONObject data = new JSONObject();
                    data.put("userAdmin", TKRoomManager.getInstance().getMySelf().nickName);
                    data.put("isClick", true);
                    TKRoomManager.getInstance().pubMsg("QiangDaZhe", "QiangDaZhe_" + TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", data.toString(), true, "qiangDaQiMesg", null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                if (responder_tv_btn.getText().toString().equals(mActivity.getString(R.string.responder_restart)) ||
                        responder_tv_btn.getText().toString().equals(mActivity.getString(R.string.responder_rob))) {
                    if (responder_tv_btn.getText().toString().equals(mActivity.getString(R.string.responder_restart))) {
                        TKRoomManager.getInstance().delMsg("qiangDaQi", "qiangDaQiMesg", "__all", new HashMap<String, Object>());
                        try {
                            JSONObject data = new JSONObject();
                            data.put("isShow", true);
                            data.put("begin", false);
                            data.put("userAdmin", "");
                            TKRoomManager.getInstance().pubMsg("qiangDaQi", "qiangDaQiMesg",
                                    "__all", data.toString(), true, null, null);

                            JSONObject dataDrag = new JSONObject();
                            dataDrag.put("percentLeft", 0.5);
                            dataDrag.put("percentTop", 0.5);
                            dataDrag.put("isDrag", true);
                            TKRoomManager.getInstance().pubMsg("ResponderDrag", "ResponderDrag", "__all", dataDrag.toString(), false, null, null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.removeCallbacks(timeRunable);
                    responder_tv_btn.setEnabled(true);
                    if (TKRoomManager.getInstance().getMySelf().role == 4) {
                        responder_tv_hint.setText("");
                        responder_tv_btn.setText(mActivity.getString(R.string.responder_nostart));
                    } else {
                        responder_tv_hint.setText(mActivity.getString(R.string.responder_click));
                        responder_tv_btn.setText(mActivity.getString(R.string.responder_start));
                    }
                } else {
                    responder_tv_hint.setText(mActivity.getString(R.string.responder_answering));
                    responder_tv_btn.setEnabled(false);
                    responder_tv_btn.setText(mActivity.getString(R.string.responder_answering_other));
                    handler.removeCallbacks(timeRunable);
                    handler.postDelayed(timeRunable, 1000 * 8);
                    try {
                        JSONObject data = new JSONObject();
                        data.put("isShow", true);
                        data.put("begin", true);
                        data.put("userAdmin", "");
                        TKRoomManager.getInstance().pubMsg("qiangDaQi", "qiangDaQiMesg", "__all", data.toString(), true, null, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            changeUnselectState();
        }
    }

    public class TimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (TKRoomManager.getInstance().getMySelf().role == 2) {
                        responder_tv_hint.setText(mActivity.getString(R.string.responder_nobody));
                        responder_tv_btn.setText(mActivity.getString(R.string.responder_nobody));
                        responder_tv_btn.setEnabled(false);
                        movePopupWindow(rootView, 0.5, 0.5, false);
                    } else {
                        responder_tv_hint.setText(mActivity.getString(R.string.responder_nobody));
                        if (TKRoomManager.getInstance().getMySelf().role == 4) {
                            responder_tv_btn.setText(mActivity.getString(R.string.responder_nostart));
                        } else {
                            responder_tv_btn.setText(mActivity.getString(R.string.responder_restart));
                        }
                        responder_tv_btn.setEnabled(true);
                    }
                    break;
            }
        }
    }

    /**
     * 抢中后的颜色背景
     */
    private void changeSelectState() {
        responder_tv_btn.setBackgroundResource(R.drawable.tk_tools_btn_select);
        responder_tv_btn.setTextColor(SkinCompatResources.getColor(mActivity, R.color.tk_tools_btn_text_select_color));
    }

    /**
     * 其他状态的颜色背景
     */
    private void changeUnselectState() {
        if (responder_tv_btn != null) {
            if (TKRoomManager.getInstance().getMySelf().role == 4) {
                responder_tv_btn.setBackgroundResource(R.drawable.tk_tools_btn_select);
                responder_tv_btn.setTextColor(SkinCompatResources.getColor(mActivity, R.color.tk_tools_btn_text_select_color));
            } else {
                responder_tv_btn.setBackgroundResource(R.drawable.tk_tools_btn);
                responder_tv_btn.setTextColor(SkinCompatResources.getColor(mActivity, R.color.white));
            }
        }
    }

    public void setShowingPopupWindowInterface(ShowingPopupWindowInterface showingPopupWindowInterface) {
        this.showingPopupWindowInterface = showingPopupWindowInterface;
    }

    private Runnable timeRunable = new Runnable() {

        @Override
        public void run() {
            handler.obtainMessage(0).sendToTarget();
        }
    };

    public void dolayout(int width) {
        FrameLayout.LayoutParams closeLayoutParams = (FrameLayout.LayoutParams) responder_img_close.getLayoutParams();
        closeLayoutParams.width = width / 9;
        closeLayoutParams.height = width / 9;
        responder_img_close.setLayoutParams(closeLayoutParams);

        FrameLayout.LayoutParams hintLayoutParams = (FrameLayout.LayoutParams) responder_tv_hint.getLayoutParams();
        hintLayoutParams.topMargin = width / 3;
        responder_tv_hint.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width / 3 * 0.45 / 3 - 3));
        responder_tv_hint.setLayoutParams(hintLayoutParams);

        FrameLayout.LayoutParams btnLayoutParams = (FrameLayout.LayoutParams) responder_tv_btn.getLayoutParams();
        btnLayoutParams.width = width / 3;
        btnLayoutParams.height = (int) (width / 3 * 0.45);
        btnLayoutParams.bottomMargin = width / 4;
        responder_tv_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width / 3 * 0.45 / 3));
        responder_tv_btn.setLayoutParams(btnLayoutParams);
    }

}
