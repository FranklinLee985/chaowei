package com.eduhdsdk.tools;

import com.eduhdsdk.R;

/**
 * 对外提供appName和logo的设置方法
 */
public class ResourceSetManage {

    private static ResourceSetManage mInstance;
    private int appLogo;                        //应用logo
    private int appName;                        //应用名

    public static ResourceSetManage getInstance() {
        synchronized (ResourceSetManage.class) {
            if (mInstance == null) {
                mInstance = new ResourceSetManage();
            }
            return mInstance;
        }
    }

    public int getAppLogo() {
        return appLogo;
    }

    /**
     * 设置applogo图片 用于通知栏的图片修改  例：R.drawable.logo
     * @param appLogo
     */
    public void setAppLogo(int appLogo) {
        this.appLogo = appLogo;
    }

    public int getAppName() {
        return appName == 0 ? R.string.tk_name : appName;
    }
    /**
     * 设置应用名 用于教室中提示的文案修改 例：R.string.appname
     * @param appName
     */
    public void setAppName(int appName) {
        this.appName = appName;
    }
}
