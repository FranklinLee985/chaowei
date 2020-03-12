package com.talkplus;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/7/21.
 */

public class ToastUtils {

    /**
     * 之前显示的内容
     */
    private static String oldMsg;
    /**
     * Toast对象
     */
    private static Toast toast = null;
    /**
     * 第一次时间
     */
    private static long oneTime = 0;
    /**
     * 第二次时间
     */
    private static long twoTime = 0;

    /**
     * 显示Toast
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (message.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    /***
     *     自定义Toast
     * @param context
     * @param message
     */
    public static void customToast(Context context, String message) {
        //1、构建Toast对象
        Toast toast = new Toast(context);
        //显示文本
        TextView tv = new TextView(context);
        tv.setText(message);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.BLACK);
        tv.setTextSize(15);
        tv.setPadding(10, 10, 10, 10);
        //2、构建Toast显示图片
       /* ImageView tv = new ImageView(this);
        tv.setImageResource(R.drawable.lss);*/
        //3、设置显示出来的view
        toast.setView(tv);
        //4、设置Toast显示位置 (屏幕顶端中间位置开始算)
        toast.setGravity(Gravity.CENTER, 0, 0);
        //5、设置时常
        toast.setDuration(Toast.LENGTH_SHORT);
        //6、显示
        toast.show();
    }
}
