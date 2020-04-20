package com.eduhdsdk.toolcase;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.classroomsdk.Config;
import com.classroomsdk.common.ToolsPenType;
import com.classroomsdk.manage.WBSession;
import com.eduhdsdk.R;
import com.eduhdsdk.ui.ColorSelectorView;
import com.talkcloud.room.TKRoomManager;

/**
 * 视频标注的画笔工具
 */
public class ToolsVideoPenPopupWindow {

    private Context mContext;

    private PopupWindow popupWindow;
    private GridView gv;
    public SeekBar seekBar;

    public onToolsPenListener listener;

    public ToolsPenType type = ToolsPenType.fountainPen;
    public ColorSelectorView colorSelectorView;

    /**
     * 选中颜色值
     */
    public interface onToolsPenListener {
        //颜色值
        void SelectedColor(int color);

        //seekbar进度daxiao
        void SeekBarProgress(int progress);
    }

    public void SetOnToolsListener(onToolsPenListener toolsListener) {
        this.listener = toolsListener;
    }


    public static int mSeekbarPenProgress = 10;

    public ToolsVideoPenPopupWindow(Context context) {
        this.mContext = context;
        initview();
    }

    private void initview() {
        View contextview = LayoutInflater.from(mContext).inflate(R.layout.tk_video_item_pen, null, false);

        colorSelectorView = contextview.findViewById(R.id.color_select);
        colorSelectorView.setColorSelectResultListen(colorListener);

        seekBar = contextview.findViewById(R.id.seek_bar);
        seekBar.setProgress(10);
        seekBar.setMax(92);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() == 0)
                    seekBar.setProgress(1);
                mSeekbarPenProgress = seekBar.getProgress() + 8;
                if (listener != null) {
                    listener.SeekBarProgress(mSeekbarPenProgress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.SeekBarProgress(seekBar.getProgress() + 8);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.SeekBarProgress(seekBar.getProgress() + 8);
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
     * 视频标注时 PopupWindow 显示在工具条右边
     * 每次弹出时恢复初始值 默认红色画笔
     */
    public void showPopPen(View view, int parentWidth) {
        if (popupWindow != null) {
            int viewheight = view.getHeight();
            int height = popupWindow.getContentView().getMeasuredHeight();
            popupWindow.showAsDropDown(view, parentWidth, -((height / 2) + (viewheight / 2)));
        }
    }

    ColorSelectorView.OnClickColorListener colorListener = new ColorSelectorView.OnClickColorListener() {
        @Override
        public void setColor(int color) {
            if (listener != null) {
                listener.SelectedColor(color);
            }
        }
    };

    public void dismisspop() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }
}
