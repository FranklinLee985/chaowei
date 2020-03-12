package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.eduhdsdk.R;
import com.eduhdsdk.tools.KeyBoardUtil;

import static android.content.Context.WINDOW_SERVICE;

/**
 * @Author: Ke.Chen
 * @Description:
 * @Date :2019-12-24 11:43
 **/
public class EyeProtectionUtil {

    //浮层窗权限的返回code
    public static final int requestCodes = 100;
    public static WindowManager wm;
    public static WindowManager.LayoutParams params;
    public static View countDownView;
    private static int sdkInt;

    /**
     * 弹出权限的对话框
     */
    public static void showDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.nothing);
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.tk_layout_crash_dialog, null);
        TextView textView = v.findViewById(R.id.tv_room_need_pwd);
        TextView cancel = v.findViewById(R.id.bt_cancel_crash_message);
        TextView sure = v.findViewById(R.id.bt_send_crash_message);
        cancel.setText(activity.getResources().getString(R.string.cancel));
        sure.setText(activity.getResources().getString(R.string.settings_now));
        textView.setText(activity.getResources().getString(R.string.permission_prompt));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    sdkInt = Build.VERSION.SDK_INT;
                    if (sdkInt >= Build.VERSION_CODES.O) {//8.0以上
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                        activity.startActivityForResult(intent, requestCodes);
                    } else if (sdkInt >= Build.VERSION_CODES.M) {//6.0-8.0
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + activity.getPackageName()));
                        activity.startActivityForResult(intent, requestCodes);
                    }
                }
            }
        });
        dialog.setContentView(v);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = KeyBoardUtil.dp2px(activity, 300f); // 宽度
        lp.height = KeyBoardUtil.dp2px(activity, 160f); // 高度
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    /**
     * 开启,关闭悬浮窗的方法
     */
    public static void openSuspensionWindow(Activity activity, boolean open) {
        wm = (WindowManager) activity.getSystemService(
                WINDOW_SERVICE); // 注意：这里必须是全局的context
        // 判断UI控件是否存在，存在则移除，确保开启任意次应用都只有一个悬浮窗
        if (activity.isFinishing() || wm == null)
            return;
        if (open) {

            if (countDownView != null) {
                wm.removeView(countDownView);
            }
            params = new WindowManager.LayoutParams();
            // 系统级别的窗口
            // 系统级别的窗口
            if (sdkInt >= Build.VERSION_CODES.O) {//8.0以上
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }/*if (sdkInt >= Build.VERSION_CODES.M) {//6.0--8.0
                params.type = WindowManager.LayoutParams.TYPE_PHONE;//降低系统级别优先级
            }*/ else {
                params.type = WindowManager.LayoutParams.TYPE_PHONE;//降低系统级别优先级
            }
            // 居中显示
            params.gravity = Gravity.CENTER;
            // 设置背景透明
            params.format = PixelFormat.TRANSPARENT;
            //https://www.jianshu.com/p/c91448e1c7d1
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            countDownView = new View(activity); // 不依赖activity的生命周期
            countDownView = View.inflate(activity,
                    R.layout.countdown_weight, null);
            if (wm != null && !activity.isFinishing())
                wm.addView(countDownView, params);
        } else {
            if (wm != null) {
                wm.removeView(countDownView);
                countDownView = null;
            }
        }
    }
}
