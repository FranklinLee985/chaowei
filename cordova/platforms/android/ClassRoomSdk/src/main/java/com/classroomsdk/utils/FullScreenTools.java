package com.classroomsdk.utils;

import android.content.Context;

import java.lang.reflect.Method;


public class FullScreenTools {

    /***
     *
     * @param context   华为刘海屏
     * @return
     */
    public static boolean hasNotchInScreen(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }

    public static int[] getNotchSize(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }

    //获取系统状态栏高度接口:
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //OPPO
    public static boolean hasNotchInOppo(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    //vivo
    public static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;//是否有凹槽
    public static final int ROUNDED_IN_SCREEN_VOIO = 0x00000008;//是否有圆角

    public static boolean hasNotchInScreenAtVoio(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("com.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(FtFeature, NOTCH_IN_SCREEN_VOIO);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }

    public static boolean hasROUNDEDInScreenAtVoio(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("com.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(FtFeature, ROUNDED_IN_SCREEN_VOIO);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }
}
