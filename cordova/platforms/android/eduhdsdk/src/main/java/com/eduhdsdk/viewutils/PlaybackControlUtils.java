package com.eduhdsdk.viewutils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.eduhdsdk.tools.AnimationUtil;

/**
 * 回放控制条的隐藏显示
 * Created by YF on 2018/12/26 0026.
 */

public class PlaybackControlUtils {

    public final static int MSG_HIDE = 0x01;
    public boolean isShowing = false;
    private HideHandler mHideHandler;
    private View view;
    private DismissPopupWindowListener mDismissPopupWindowListener;
    private Context mContext;

    public PlaybackControlUtils(Context context, DismissPopupWindowListener dismissPopupWindowListener) {
        this.mContext = context.getApplicationContext();
        this.mDismissPopupWindowListener = dismissPopupWindowListener;
        mHideHandler = new HideHandler();
    }

    public class HideHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HIDE:
                    //回放时  隐藏动画
                    if (isShowing) {
                        AnimationUtil.getInstance(mContext).backView(view);
                        isShowing = false;
                        if (mDismissPopupWindowListener != null) {
                            mDismissPopupWindowListener.dismissPopupWindow();
                        }
                    }
                    break;
            }
        }
    }

    public void connectLost() {
        AnimationUtil.getInstance(mContext).backView(view);
        isShowing = false;
    }

    private Runnable hideRunable = new Runnable() {

        @Override
        public void run() {
            mHideHandler.obtainMessage(MSG_HIDE).sendToTarget();
        }
    };

    public void startHideTimer(View view) {//开始计时,三秒后执行runable
        this.view = view;
        mHideHandler.removeCallbacks(hideRunable);
        if (!isShowing) {
            //回放时  进度条显示动画
            AnimationUtil.getInstance(mContext).moveUpView(view);
            isShowing = true;
        }
        mHideHandler.postDelayed(hideRunable, 3000);
    }

    public void endHideTimer() {//移除runable,将不再计时
        mHideHandler.removeCallbacks(hideRunable);
    }

    public void resetHideTimer() {//重置计时
        mHideHandler.removeCallbacks(hideRunable);
        mHideHandler.postDelayed(hideRunable, 3000);
    }

    public interface DismissPopupWindowListener {
        void dismissPopupWindow();
    }

}
