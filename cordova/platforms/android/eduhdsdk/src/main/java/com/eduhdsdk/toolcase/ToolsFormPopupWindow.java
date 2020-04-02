package com.eduhdsdk.toolcase;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.classroomsdk.common.ToolsFormType;
import com.eduhdsdk.R;
import com.eduhdsdk.ui.ColorSelectorView;

public class ToolsFormPopupWindow {

    private Context mContext;
    private PopupWindow popupWindow;

    public onToolsFormListener listener;

    public ToolsFormType type = ToolsFormType.hollow_rectangle;
    public ColorSelectorView colorSelectorView;

    /**
     * 选中颜色值
     */
    public interface onToolsFormListener {
        //颜色值
        void SelectedColor(int color);

        //何种画笔
        void SelectedForm(ToolsFormType penType);

        //seekbar进度daxiao
        void SeekBarProgress(int progress);
    }

    public void SetOnToolsListener(onToolsFormListener toolsListener) {
        this.listener = toolsListener;
    }


    public static int mSeekbarProgress = 10;

    public ToolsFormPopupWindow(Context context) {
        this.mContext = context;
        initview();
    }

    private void initview() {
        View contextview = LayoutInflater.from(mContext).inflate(R.layout.tk_item_form, null, false);

        final ImageView iv_hollow_rectangle = contextview.findViewById(R.id.iv_frame_hollow_rectangle);
        final ImageView iv_solie_rectangle = contextview.findViewById(R.id.iv_frame_solie_rectangle);
        final ImageView iv_hollow_circle = contextview.findViewById(R.id.iv_frame_hollow_circle);
        final ImageView iv_solie_circle = contextview.findViewById(R.id.iv_frame_solie_circle);
        colorSelectorView = contextview.findViewById(R.id.color_select);
        colorSelectorView.setColorSelectResultListen(colorListener);

        SeekBar seekBar = contextview.findViewById(R.id.seek_bar);

        iv_hollow_rectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = ToolsFormType.hollow_rectangle;
                iv_hollow_rectangle.setImageResource(R.drawable.tk_frame_icon_hollow_rectangle_selected);
                iv_solie_rectangle.setImageResource(R.drawable.tk_frame_icon_solie_rectangle_default);
                iv_hollow_circle.setImageResource(R.drawable.tk_frame_icon_hollow_circle_default);
                iv_solie_circle.setImageResource(R.drawable.tk_frame_icon_solie_circle_default);
                if (listener != null) {
                    listener.SelectedForm(type);
                }
            }
        });
        iv_solie_rectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = ToolsFormType.solid_rectangle;
                iv_hollow_rectangle.setImageResource(R.drawable.tk_frame_icon_hollow_rectangle_default);
                iv_solie_rectangle.setImageResource(R.drawable.tk_frame_icon_solie_rectangle_selected);
                iv_hollow_circle.setImageResource(R.drawable.tk_frame_icon_hollow_circle_default);
                iv_solie_circle.setImageResource(R.drawable.tk_frame_icon_solie_circle_default);
                if (listener != null) {
                    listener.SelectedForm(type);
                }
            }
        });
        iv_hollow_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = ToolsFormType.hollow_circle;
                iv_hollow_rectangle.setImageResource(R.drawable.tk_frame_icon_hollow_rectangle_default);
                iv_solie_rectangle.setImageResource(R.drawable.tk_frame_icon_solie_rectangle_default);
                iv_hollow_circle.setImageResource(R.drawable.tk_frame_icon_hollow_circle_selected);
                iv_solie_circle.setImageResource(R.drawable.tk_frame_icon_solie_circle_default);
                if (listener != null) {
                    listener.SelectedForm(type);
                }
            }
        });
        iv_solie_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = ToolsFormType.solid_circle;
                iv_hollow_rectangle.setImageResource(R.drawable.tk_frame_icon_hollow_rectangle_default);
                iv_solie_rectangle.setImageResource(R.drawable.tk_frame_icon_solie_rectangle_default);
                iv_hollow_circle.setImageResource(R.drawable.tk_frame_icon_hollow_circle_default);
                iv_solie_circle.setImageResource(R.drawable.tk_frame_icon_solie_circle_selected);
                if (listener != null) {
                    listener.SelectedForm(type);
                }
            }
        });
        seekBar.setProgress(10);
        seekBar.setMax(92);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekbarProgress = seekBar.getProgress() + 8;
                if (listener != null) {
                    listener.SeekBarProgress(mSeekbarProgress);
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


    public void showPopPen(View view, int parentWidth) {
        if (popupWindow != null) {
            int viewWidth = view.getWidth();
            int viewheight = view.getHeight();
            int leftOffset = (parentWidth - viewWidth) / 2;
            popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int width = popupWindow.getContentView().getMeasuredWidth();
            int height = popupWindow.getContentView().getMeasuredHeight();
            colorSelectorView.changeColorSelect();
            popupWindow.showAsDropDown(view, -(width + leftOffset), -((height / 2) + (viewheight / 2)));
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
