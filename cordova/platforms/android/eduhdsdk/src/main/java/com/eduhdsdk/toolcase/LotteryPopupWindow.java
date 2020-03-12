package com.eduhdsdk.toolcase;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.interfaces.ShowingPopupWindowInterface;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.tools.MovePopupwindowTouchListener;
import com.eduhdsdk.tools.PopupWindowTools;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

import skin.support.annotation.Skinable;

import static com.eduhdsdk.toolcase.ToolsPopupWindow.TOOLS_ZHUANPAN;

/**
 * 转盘popupwindow
 */
@Skinable
public class LotteryPopupWindow implements View.OnClickListener {

    private PopupWindow popupWindow;

    private OnLotteryResultListener mListener;

    // 背景圆盘
    private ImageView mIvTable;
    // 开始按键
    private ImageView mIvStart;
    // 关闭按键
    private ImageView mIvClose;

    // 转盘是否在旋转中，如果是，start按钮不可点击
    private boolean lotteryRotating = false;
    // 当前旋转的角度
    private int mCurrentAngle = 0;
    private MovePopupwindowTouchListener movePopupwindowTouchListener;//拖动
    private ShowingPopupWindowInterface showingPopupWindowInterface;//显示的回调
    private Context mActivity;
    public View contentView;
    private static LotteryPopupWindow instance;

    public static LotteryPopupWindow getInstance() {
        if (instance == null) {
            instance = new LotteryPopupWindow();
        }
        return instance;
    }

    public void resetInstance() {
        instance = null;
    }

    private LotteryPopupWindow() {

    }

    public void setmActivity(Context activity) {
        this.mActivity = activity;
    }

    /**
     * 初始化popupwindow
     */
    public void initPopupWindow() {
        contentView = LayoutInflater.from(mActivity).inflate(R.layout.tk_layout_tools_lottery, null);
        mIvTable = contentView.findViewById(R.id.iv_tools_table);
        mIvStart = contentView.findViewById(R.id.iv_tools_start);
        mIvClose = contentView.findViewById(R.id.iv_tools_close);

        if (TKRoomManager.getInstance().getMySelf().role == 4) {
            mIvStart.setImageResource(R.drawable.tk_tools_zj_start);
            mIvClose.setVisibility(View.GONE);
        }

        mIvStart.setOnClickListener(this);
        mIvClose.setOnClickListener(this);

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

        if (movePopupwindowTouchListener == null) {
            movePopupwindowTouchListener = new MovePopupwindowTouchListener(popupWindow, mActivity);
        }

        contentView.setTag(TOOLS_ZHUANPAN);
        contentView.setOnTouchListener(movePopupwindowTouchListener);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ToolsPopupWindow.getInstance().setLotteryBtnReset();
            }
        });
    }

    public void setVisibility(int visibility) {
        if (contentView != null && popupWindow != null) {
            contentView.setVisibility(visibility);
            if (visibility == View.GONE) {
                popupWindow.setTouchable(false);
            } else if (visibility == View.VISIBLE) {
                popupWindow.setTouchable(true);
            }
            popupWindow.update();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_tools_start) {
            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                rotatingAnim();
            }
        } else if (id == R.id.iv_tools_close) {
            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                TKRoomManager.getInstance().delMsg("dial", "dialMesg", "__all", new HashMap<String, Object>());
                dismiss();
            }
        }
    }

    private void rotatingAnim() {
        if (lotteryRotating) {
            return;
        }
        // 最少旋转5圈，随机旋转[0-5)圈，随机旋转[0-360)度
        int angle = getRandom(7);
        //float totalAngle = (5 + getRandom(5)) * 360f + angle;
//
//        RotateAnimation rotateAnimation = new RotateAnimation(mCurrentAngle, totalAngle, Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setDuration(3000);
//        rotateAnimation.setFillAfter(true);
//        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
//
//        mIvTable.startAnimation(rotateAnimation);
//
////        mCurrentAngle = (angle + mCurrentAngle);
//
//
//        int result;
//        if (mCurrentAngle < 30) {
//            result = 1;
//        } else if (mCurrentAngle < 90) {
//            result = 6;
//        } else if (mCurrentAngle < 150) {
//            result = 5;
//        } else if (mCurrentAngle < 210) {
//            result = 4;
//        } else if (mCurrentAngle < 270) {
//            result = 3;
//        } else if (mCurrentAngle < 330) {
//            result = 2;
//        } else {
//            result = 1;
//        }
//        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                lotteryRotating = true;
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                lotteryRotating = false;
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
        if (mListener != null) {
//            mListener.onLotteryResult(totalAngle);
            mListener.onLotteryResult(angle * 60 + (getRandom(5) + 2) * 360 + mCurrentAngle);
        }
    }

    public void rotatingAnimStudent(float totalAngle, boolean ignoreAnimation) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mIvTable, "rotation", mCurrentAngle, totalAngle);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        if (ignoreAnimation) {
            anim.setDuration(1);
        } else {
            anim.setDuration(3000);
        }
        mCurrentAngle = (int) totalAngle;
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIvStart.setEnabled(true);
                if (TKRoomManager.getInstance().getMySelf().role != 4) {
                    mIvClose.setVisibility(View.VISIBLE);
                }
                isRole();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIvStart.setEnabled(false);
                mIvClose.setVisibility(View.GONE);
                isRole();
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }
        });
        anim.start();
    }

    private void resetAnimation() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mIvTable, "rotation", 0, 0);
        anim.start();
    }

    /**
     * 获取随机数
     *
     * @param num 随机数范围
     * @return [0, num)之间的任意一个随机数（包括0但不包括num）
     */
    private int getRandom(int num) {
        Random random = new Random();
        return random.nextInt(num);
    }

    public void showPopupWindow(View rootView, boolean isShow, boolean isSend) {
        if (contentView == null) {
            initPopupWindow();
        }
        movePopupwindowTouchListener.setView(rootView);
        if (isShow) {
            show(rootView);
        } else {
            if (!popupWindow.isShowing()) {
                show(rootView);
            }
        }

        if (TKRoomManager.getInstance().getMySelf().role == 0 && isSend) {
            try {
                JSONObject data = new JSONObject();
                data.put("rotationAngle", "rotate(" + 0 + "deg)");
                data.put("isShow", true);
                TKRoomManager.getInstance().pubMsg("dial", "dialMesg", "__all", data.toString(), true, "ClassBegin", null);

                JSONObject dataDrag = new JSONObject();
                dataDrag.put("percentLeft", 0.5);
                dataDrag.put("percentTop", 0.5);
                TKRoomManager.getInstance().pubMsg("DialDrag", "DialDrag", "__all", dataDrag.toString(), false, "ClassBegin", null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void show(View rootView) {
        isRole();
        if (showingPopupWindowInterface != null) {
            showingPopupWindowInterface.popupWindowShowing(TOOLS_ZHUANPAN);
        }
        popupWindow.setWidth(rootView.getMeasuredHeight() / 5 * 3);
        popupWindow.setHeight(rootView.getMeasuredHeight() / 5 * 3);
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

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    /**
     * 判断用户的身份
     */
    public void isRole() {
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
            mIvClose.setVisibility(View.GONE);
            mIvStart.setEnabled(false);
        } else if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {//巡课
            mIvStart.setEnabled(false);
            mIvClose.setEnabled(false);
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
        if (!popupWindow.isShowing()) {
            showPopupWindow(rootView, true, false);
        }
        PopupWindowTools.movePopupWindow(popupWindow, rootView, moveX, movieY, isHaiping);
    }

    public void dismiss() {
        lotteryRotating = false;
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                mCurrentAngle = 0;
                resetAnimation();
                popupWindow.dismiss();
            }
        }
    }

    public void setShowingPopupWindowInterface(ShowingPopupWindowInterface showingPopupWindowInterface) {
        this.showingPopupWindowInterface = showingPopupWindowInterface;
    }

    public void setOnLotteryResultListener(OnLotteryResultListener listener) {
        mListener = listener;
    }

    public interface OnLotteryResultListener {
        void onLotteryResult(float result);
    }

    public void dolayout(int width) {
        FrameLayout.LayoutParams mIvStartLayoutParams = (FrameLayout.LayoutParams) mIvStart.getLayoutParams();
        mIvStartLayoutParams.width = width / 5;
        mIvStartLayoutParams.height = width / 5;
        mIvStart.setLayoutParams(mIvStartLayoutParams);

        FrameLayout.LayoutParams mIvCloseLayoutParams = (FrameLayout.LayoutParams) mIvClose.getLayoutParams();
        mIvCloseLayoutParams.width = width / 9;
        mIvCloseLayoutParams.height = width / 9;
        mIvClose.setLayoutParams(mIvCloseLayoutParams);
    }

}
