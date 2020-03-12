package com.eduhdsdk.viewutils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.tools.Tools;

/**
 * Created by fucc on 2019/3/20.
 */

public class WifiStatusPop {
    private Context mContext;
    public View contentView;
    public TextView wifi_rooid;
    public TextView wifi_page_lose;
    public TextView wifi_delay;
    public ImageView up_arr;
    public int wifiStatus = 1;
    public PopupWindow wifiStatusPop;

    public WifiStatusPop(Context context) {
        this.mContext = context;
        initWifiStatusPop();
    }

    public void initWifiStatusPop() {
        contentView = LayoutInflater.from(mContext).inflate(R.layout.tk_layout_wifi_status_pop, null);
        ScreenScale.scaleView(contentView, "WifiStatusPop");
        wifi_rooid = contentView.findViewById(R.id.tv_wifi_roomid);
        wifi_page_lose = contentView.findViewById(R.id.tv_wifi_page_lose);
        wifi_delay = contentView.findViewById(R.id.tv_wifi_delay);
        up_arr = contentView.findViewById(R.id.tk_wifi_jianjiao);

        wifi_rooid.setText(mContext.getString(R.string.wifi_roomid));

        wifiStatusPop = new PopupWindow(mContext);
        wifiStatusPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        wifiStatusPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        wifiStatusPop.setContentView(contentView);
        wifiStatusPop.setBackgroundDrawable(new BitmapDrawable());
        wifiStatusPop.setFocusable(false);
        wifiStatusPop.setOutsideTouchable(true);
    }

    public void setRoomId(String roomId) {
        wifi_rooid.setText(mContext.getString(R.string.wifi_roomid) + roomId);
    }

    public void setPageloseAndDelay(String pageLose, String delay) {
        wifi_page_lose.setText(mContext.getString(R.string.wifi_page_lose) + pageLose + "%");
        wifi_delay.setText(mContext.getString(R.string.wifi_delay) + delay + "ms");
    }

    public void showWifiStatusPop(final View rootView) {
        if (wifiStatusPop == null) {
            initWifiStatusPop();
        }
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int[] reb_wb_board = new int[2];
        rootView.getLocationInWindow(reb_wb_board);

        LinearLayout.LayoutParams up_arr_params = (LinearLayout.LayoutParams) up_arr.getLayoutParams();
        if (ScreenScale.getScreenWidth() - (reb_wb_board[0] + rootView.getWidth() / 2) < contentView.getMeasuredWidth() / 2) {
            up_arr_params.setMargins(contentView.getMeasuredWidth() / 2 + reb_wb_board[0] + rootView.getWidth() / 4 - (ScreenScale.getScreenWidth() - contentView.getMeasuredWidth() + contentView.getMeasuredWidth() / 2)
                    , 0, 0, 0);
        } else {
            up_arr_params.gravity = Gravity.CENTER_HORIZONTAL;
        }

        up_arr.setLayoutParams(up_arr_params);
        int xPos = rootView.getWidth() / 2 - contentView.getMeasuredWidth() / 2;
        if (wifiStatusPop != null) {
            wifiStatusPop.showAsDropDown(rootView, xPos, 0, Gravity.BOTTOM);
        }

        wifiStatusPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isInView = Tools.isInView(event, rootView);
                return isInView;
            }
        });
    }

    public void dismiss() {
        if (wifiStatusPop != null) {
            if (wifiStatusPop.isShowing()) {
                wifiStatusPop.dismiss();
            }
        }
    }

    public void setWifiStatus(int wifiStatus) {
        this.wifiStatus = wifiStatus;
    }
}
