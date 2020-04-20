package com.talkplus;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;
import com.eduhdsdk.room.RoomClient;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by Administrator on 2018/4/17.
 */

public class LoginUtils {


    /**
     * 2  * 获取版本号
     * 3  * @return 当前应用的版本号
     * 4
     */
    public static String getVersion() {
        try {
            PackageManager manager = MyApplication.getInstance().getPackageManager();
            PackageInfo info = manager.getPackageInfo(MyApplication.getInstance().getPackageName(), 0);
            String version = info.versionName;
            return "v:" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getVersionCode() {
        try {
            PackageManager manager = MyApplication.getInstance().getPackageManager();
            PackageInfo info = manager.getPackageInfo(MyApplication.getInstance().getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public byte[] desCrypto(byte[] datasource, String password) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     *
     * @param textView   测试切换网络
     * @param context
     */
    public static void testNet(TextView textView, final Context context) {
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (RoomClient.webServer.equals("demo.talk-cloud.net")) {
                    RoomClient.webServer = "global.talk-cloud.net";
                } else {
                    RoomClient.webServer = "demo.talk-cloud.net";
                }
                ToastUtils.customToast(context, RoomClient.webServer);
                return false;
            }
        });
    }



}
