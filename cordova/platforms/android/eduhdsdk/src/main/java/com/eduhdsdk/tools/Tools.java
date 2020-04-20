package com.eduhdsdk.tools;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.eduhdsdk.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/17.
 */

public class Tools {

    public static ProgressDialog progressDialog;
    public static boolean isDialogShow = true;

    public static boolean isTure(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else if (o instanceof Number) {
            Number n = (Number) o;
            return n.longValue() != 0;
        } else {
            return false;
        }
    }

    public static long toLong(Object o) {
        long temLong = 0;
        if (o instanceof Integer) {
            int tem = (Integer) o;
            temLong = tem;
        } else if (o instanceof String) {
            String temstr = (String) o;
            temLong = Long.valueOf(temstr);
        } else {
            temLong = (Long) o;
        }
        return temLong;
    }

    public static void ShowProgressDialog(Context context, final String message) {

        progressDialog = new ProgressDialog(context, android.R.style.Theme_Holo_Light_Dialog);
        if (message != null) {
            progressDialog.setMessage(message);
        }
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
        isDialogShow = true;
    }

    /**
     * 弹出对话框，只有确定按钮
     *
     * @param activity
     * @param message
     */
    public static void showAlertDialog(final Activity activity, final String message) {

        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.nothing);
        View v = LayoutInflater.from(activity).inflate(R.layout.tk_layout_net_dialog, null);

        TextView tv_dialog_message = (TextView) v.findViewById(R.id.tv_exit_dialog_message);
        tv_dialog_message.setText(message);
        v.findViewById(R.id.iv_close_popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        v.findViewById(R.id.bt_ok_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
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

    public static void HideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            isDialogShow = false;
            progressDialog = null;
        }
    }

    /**
     * 弹出自定义文字的对话框
     *
     * @param activity
     * @param title
     * @param message
     * @param dialogClick
     */
    public static void showDialog(Context activity, int title, String message, final OnDialogClick dialogClick) {

        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.nothing);
        View v = LayoutInflater.from(activity).inflate(R.layout.tk_layout_exit_dialog, null);

        TextView tv_dialog_title = v.findViewById(R.id.tv_dialog_title);
        TextView tv_dialog_message = v.findViewById(R.id.tv_exit_dialog_message);
        tv_dialog_title.setText(title);
        tv_dialog_message.setText(message);
        v.findViewById(R.id.iv_close_popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        v.findViewById(R.id.bt_cancel_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        v.findViewById(R.id.bt_ok_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialogClick.dialog_ok(dialog);
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

    public interface OnDialogClick {
        void dialog_ok(Dialog dialog);
    }

    /**
     * 跳转到权限设置界面
     */
    public static void getAppDetailSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

    /**
     * 判断点击事件是否在指定view的内部
     *
     * @param event
     * @param view
     * @return
     */
    public static boolean isInView(MotionEvent event, View view) {
        int[] view_data = new int[2];
        view.getLocationInWindow(view_data);
        if (event.getX() > view_data[0] && event.getX() < (view_data[0] + view.getWidth()) && event.getY() > view_data[1] && event.getY() < (view_data[1] + view.getHeight())) {
            return true;
        } else if (event.getRawX() > view_data[0] && event.getRawX() < (view_data[0] + view.getWidth()) && event.getRawY() > view_data[1] && event.getRawY() < (view_data[1] + view.getHeight())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }


    public static boolean isMp4(String fileType) {
        if (fileType == null || fileType.isEmpty()) {
            return false;
        }
        if (fileType.toLowerCase().endsWith("mp4") || fileType.toLowerCase().endsWith("webm")) {
            return true;
        } else {
            return false;
        }
    }

    //时间转换时分秒
    public static String secToTime(long time) {
        String timeStr = "00:00:00";
        String H = "";
        String M = "";
        String S = "";
        long temps = time / 1000;
        long tempm = temps / 60;
        long temph = tempm / 60;
        long sec = temps - tempm * 60;
        tempm = tempm - temph * 60;
        H = temph == 0 ? "00" : temph >= 10 ? temph + "" : "0" + temph;
        M = tempm == 0 ? "00" : tempm >= 10 ? tempm + "" : "0" + tempm;
        S = sec == 0 ? "00" : sec >= 10 ? sec + "" : "0" + sec;
        if (TextUtils.equals("00", H)) {
            timeStr = M + ":" + S;
        } else {
            timeStr = H + ":" + M + ":" + S;
        }
        return timeStr;
    }

    public static float spacing(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }
        return 0.0f;
    }

    public static int getScreenWidth(Activity activity) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        return width;
    }

    public static int getScreenHeight(Activity activity) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;       // 屏幕高度（像素）
        return height;
    }
}
