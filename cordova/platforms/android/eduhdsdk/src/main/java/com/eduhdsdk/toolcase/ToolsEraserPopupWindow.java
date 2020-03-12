package com.eduhdsdk.toolcase;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.classroomsdk.utils.FullScreenTools;
import com.eduhdsdk.R;

public class ToolsEraserPopupWindow {

    private Context mContext;
    //橡皮檫右箭头 下箭头
    private ImageView frame_eraser_right, frame_eraser_bottom;
    private PopupWindow popupWindow;

    public onToolsListener listener;

    /**
     * 选中颜色值
     */
    public interface onToolsListener {
        //seekbar进度daxiao
        void SeekBarSize(int size);
    }

    public void SetonToolsListener(onToolsListener toolsListener) {
        this.listener = toolsListener;
    }


    public static int mSeekbarsize = 10;

    public ToolsEraserPopupWindow(Context context, boolean direction) {
        this.mContext = context;
        initview(direction);
    }

    private void initview(boolean direction) {
        View contextview = LayoutInflater.from(mContext).inflate(R.layout.tk_item_eraser, null, false);
        frame_eraser_right = contextview.findViewById(R.id.frame_eraser_right);
        frame_eraser_bottom = contextview.findViewById(R.id.frame_eraser_bottom);

        if (direction) {
            frame_eraser_right.setVisibility(View.VISIBLE);
        } else {
            frame_eraser_bottom.setVisibility(View.VISIBLE);
        }
        SeekBar seekBar = contextview.findViewById(R.id.seek_bar);

        seekBar.setProgress(10);
        seekBar.setMax(92);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mSeekbarsize = seekBar.getProgress() + 8;
                if (listener != null) {
                    listener.SeekBarSize(mSeekbarsize);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.SeekBarSize(seekBar.getProgress() + 8);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.SeekBarSize(seekBar.getProgress() + 8);
                }
            }
        });

        contextview.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(mContext);
        }
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(contextview);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
    }

    /**
     * 显示在工具条左边
     */
    public void showPopPen(View view, int parentWidth) {
        if (popupWindow != null) {
            int viewWidth = view.getWidth();
            int viewheight = view.getHeight();
            int leftOffset = (parentWidth - viewWidth) / 2;
            popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int width = popupWindow.getContentView().getMeasuredWidth();
            int height = popupWindow.getContentView().getMeasuredHeight();
            popupWindow.showAsDropDown(view, -(width + leftOffset), -((height / 2) + (viewheight / 2)));
        }

    }

    /**
     * 显示在工具条右边
     */
    public void showPopEraserToRight(View view, int parentWidth) {
        if (popupWindow != null) {
            frame_eraser_right.setVisibility(View.GONE);
            frame_eraser_bottom.setVisibility(View.GONE);

            int viewheight = view.getHeight();
            int height = popupWindow.getContentView().getMeasuredHeight();
            popupWindow.showAsDropDown(view, parentWidth, -((height / 2) + (viewheight / 2)));
        }

    }

    public void showPopPenSmall(View view, View types, boolean isHaiping) {
        if (popupWindow != null) {
            int width = popupWindow.getContentView().getMeasuredWidth();
            int height = popupWindow.getContentView().getMeasuredHeight();
            //在popupwindow上以相对控件的方式继续弹出 会出多pop添加出错，必须以activity为载体添加pop
            //以第一个pop下控件为屏幕相对坐标 计算第二个pop在整个屏幕的偏移量
            int[] location = new int[2];
            types.getLocationOnScreen(location);
            int x = location[0] - width / 2 + types.getWidth() / 2;
            int y = location[1] - height;
            if (isHaiping) {
                x = x - FullScreenTools.getStatusBarHeight(mContext);
            }
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
        }
    }


    public void dismisspop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

}
