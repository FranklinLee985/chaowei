package com.talkplus;

import android.app.Application;
import android.os.Build;

import com.classroomsdk.tools.ScreenScale;
import com.classroomsdk.utils.TKLog;
import com.llew.huawei.verifier.LoadedApkHuaWei;
//import com.talkcloud.plus.BuildConfig;
import io.framework7.classroom.BuildConfig;
import com.tencent.smtt.sdk.QbSdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import skin.support.SkinCompatManager;

/**
 * Created by Administrator on 2018/4/17.
 */

public class MyApplication extends Application {

    public static MyApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };

        // 打印日志
        TKLog.enableLog(true);

        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
        QbSdk.setDownloadWithoutWifi(true);

        ScreenScale.init(this);
        SkinCompatManager.withoutActivity(this)             // 基础控件换肤初始化
                .loadSkin();
        SkinCompatManager.getInstance().setSkinAllActivityEnable(false);

       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);*/

        // debug模式下，关掉Android9的弹窗
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 28) {
            closeAndroidPDialog();
        }

        LoadedApkHuaWei.hookHuaWeiVerifier(this);
    }

    /**
     * 关闭弹窗
     */
    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MyApplication getInstance() {
        return context;
    }
}
