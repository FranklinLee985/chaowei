package com.talkplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.classroomsdk.http.DownLoadFileCallBack;
import com.classroomsdk.http.HttpHelp;
import com.classroomsdk.http.ResponseCallBack;
import com.eduhdsdk.BuildVars;
import com.eduhdsdk.room.RoomClient;
import com.loopj.android.http.RequestParams;
import com.talkcloud.plus.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2018/11/23/023
 */

public class AutoUpdateUtil {

    static private AutoUpdateUtil mInstance = null;
    private ProgressDialog dialog;
    private String target;
    private Activity mActivity;
    // APK下载地址
    private String apkDownLoadUrl = "";

    static public AutoUpdateUtil getInstance() {
        synchronized (AutoUpdateUtil.class) {
            if (mInstance == null) {
                mInstance = new AutoUpdateUtil();
            }
            return mInstance;
        }
    }

    public void resetInstance() {
        mInstance = null;
    }

    //检测自动更新
    public void checkForUpdates(Activity activity) {
        mActivity = activity;
        String url = BuildVars.REQUEST_HEADER + RoomClient.webServer + ":" +
                80 + "/ClientAPI/getupdateinfo";
        RequestParams params = new RequestParams();
        try {
            PackageManager manager = activity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
            params.put("type", 9);//type代表Android手机
            params.put("version", "2019110500");//版本升级日期
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHelp.getInstance().post(url, params, new ResponseCallBack() {
            @Override
            public void success(int statusCode, JSONObject response) {
                try {
                    final int nRet = response.getInt("result");
                    if (nRet == 0) {
                        apkDownLoadUrl = response.optString("updateaddr");
                        final int code = response.optInt("updateflag");
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (code != 0) {
                                    checkForUpdataDialog(code);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }

    // 制定更新提示框
    public void checkForUpdataDialog(int code) {
        AlertDialog updateDialog = null;
        AlertDialog.Builder build = new AlertDialog.Builder(mActivity);
        build.setTitle(mActivity.getString(R.string.update_dialog_title));
        String msg = "";
        if (code == 1) {
            msg = mActivity.getString(R.string.find_new_version_fouce);
        } else if (code == 2) {
            msg = mActivity.getString(R.string.find_new_version);
            build.setNegativeButton(mActivity.getString(R.string.update_dialog_negative_button),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        build.setMessage(msg);
        build.setPositiveButton(mActivity.getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        downLoadApp(mActivity);
                        arg0.dismiss();
                    }
                });
        if (updateDialog == null) {
            updateDialog = build.create();
            updateDialog.setCancelable(false);
            updateDialog.setCanceledOnTouchOutside(false);
        }
        if (updateDialog != null && !updateDialog.isShowing()) {
            updateDialog.show();
        }
    }

    /***
     *
     * @param activity   下载APK
     */
    public void downLoadApp(final Activity activity) {
        target = Environment.getExternalStorageDirectory().getAbsolutePath() + "/talkcloudHD.apk";
        if (!apkDownLoadUrl.contains("http://")) {
            apkDownLoadUrl = BuildVars.REQUEST_HEADER + apkDownLoadUrl;
        }
        if (apkDownLoadUrl != null && !apkDownLoadUrl.isEmpty()) {
            java.io.File localFile = new java.io.File(target);
            if (!localFile.exists()) {//创建文件夹
                localFile.getParentFile().mkdirs();
            }
            HttpHelp.getInstance().downLoadFile(apkDownLoadUrl, localFile, new DownLoadFileCallBack() {
                @Override
                public void onSuccess(int statusCode, File file) {
                    dialog.dismiss();
                    // 安装apk
                    installApk(activity);
                }

                @Override
                public void onFailure(int statusCode, File file, Throwable throwable) {

                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    initProgressDialog(activity, totalSize, bytesWritten);
                }
            });

        }
    }

    //下载进度
    protected void initProgressDialog(Activity activity, long total, long current) {

        if (activity.isFinishing()) {
            return;
        }

        if (dialog == null) {
            dialog = new ProgressDialog(activity);
        }
        dialog.setTitle(activity.getString(com.eduhdsdk.R.string.updateing));//设置标题
        dialog.setMessage("");//设置dialog内容
        //        dialog.setIcon(R.drawable.tk_icon_word);//设置图标，与为Title左侧
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 水平线进度条
        // dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//圆形进度条
        dialog.setMax((int) total);//最大值
        dialog.setProgress((int) current);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //安装APK
    protected void installApk(Activity activity) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上){
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File file = new File(target);
            Uri apkUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);//在AndroidManifest中的android:authorities值
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            activity.startActivity(intent);
        } else {
            Uri data = Uri.parse("file://" + target);
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }
}
