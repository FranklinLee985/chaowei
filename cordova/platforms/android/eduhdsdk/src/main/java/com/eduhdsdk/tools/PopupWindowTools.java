package com.eduhdsdk.tools;

import android.text.TextUtils;
import android.view.View;
import android.widget.Chronometer;
import android.widget.PopupWindow;

import com.classroomsdk.tools.ScreenScale;

/**
 * 工具箱内popupwindow的工具类
 * Created by YF on 2019/1/2 0002.
 */

public class PopupWindowTools {

    public static void movePopupWindow(PopupWindow popupWindow, int offsetX, int offsetY) {
        if (popupWindow.isShowing()) {
            popupWindow.update(offsetX, offsetY, -1, -1, true);
        }
    }

    public static void movePopupWindow(PopupWindow popupWindow, View rootView, double moveX, double movieY, boolean isHaiping) {
        if (rootView == null) {
            return;
        }
        int popupWindowHeight = popupWindow.getHeight();
        if (popupWindowHeight <= 0) {
            popupWindow.getContentView().measure(0, 0);
            popupWindowHeight = popupWindow.getContentView().getMeasuredHeight();
        }

        int[] location = new int[2];
        rootView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        int pw = width - popupWindow.getWidth();
        int ph = height - popupWindowHeight;
        int offsetX = (int) (x + pw * moveX);
        int offsetY = (int) (y + ph * movieY);

        int leftEdge = 0;
        if (isHaiping) {
            leftEdge = ScreenScale.getStatusBarHeight();
        }

        if (offsetX <= leftEdge) {
            offsetX = leftEdge;
        }

        if (offsetY >= ScreenScale.getScreenHeight() - popupWindowHeight) {
            offsetY = ScreenScale.getScreenHeight() - popupWindowHeight;
        }

        if (popupWindow.isShowing()) {
            popupWindow.update(offsetX, offsetY, -1, -1, true);
        }
    }

    /**
     * @param cmt Chronometer控件
     * @return 小时+分钟+秒数  的所有秒数
     */
    public static int getChronometerSeconds(Chronometer cmt) {
        int totalss = 0;
        String string = cmt.getText().toString();

        if(TextUtils.isEmpty(string) || "".equals(string)){
            return totalss;
        }

        if (string.length() == 8) {

            String[] split = string.split(":");
            String string2 = split[0];
            int hour = Integer.parseInt(string2);
            int Hours = hour * 3600;
            String string3 = split[1];
            int min = Integer.parseInt(string3);
            int Mins = min * 60;
            int SS = Integer.parseInt(split[2]);
            totalss = Hours + Mins + SS;
            return totalss;
        } else if (string.length() == 5) {

            String[] split = string.split(":");
            String string3 = split[0];
            int min = Integer.parseInt(string3);
            int Mins = min * 60;
            int SS = Integer.parseInt(split[1]);

            totalss = Mins + SS;
            return totalss;
        }
        return totalss;
    }
}
