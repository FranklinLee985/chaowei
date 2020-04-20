package com.eduhdsdk.tools;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.eduhdsdk.BuildVars;
import com.eduhdsdk.R;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;

/**
 * Created by Administrator on 2018/11/23/023.   日志捕获工具类
 */

public class LogCrashesUtil {

    static private LogCrashesUtil mInstance = null;

    static public LogCrashesUtil getInstance() {
        synchronized (LogCrashesUtil.class) {
            if (mInstance == null) {
                mInstance = new LogCrashesUtil();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        mInstance = null;
    }

    /***
     *
     * @param activity
     * @param HOCKEY_APP_HASH   日志捕获
     */
    public void checkForCrashes(final Context activity, String HOCKEY_APP_HASH) {
        CrashManagerListener listener = new CrashManagerListener() {
            @Override
            public boolean shouldAutoUploadCrashes() {
                return true;
            }

            @Override
            public void onNewCrashesFound() {
                super.onNewCrashesFound();

                final Dialog dialog = new Dialog(activity);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.nothing);
                LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = li.inflate(R.layout.tk_layout_crash_dialog, null);

                v.findViewById(R.id.bt_cancel_crash_message).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                v.findViewById(R.id.bt_send_crash_message).setOnClickListener(new View.OnClickListener() {
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
        };
        CrashManager.register(activity, BuildVars.CRASH_URL, HOCKEY_APP_HASH, listener);
    }
}
