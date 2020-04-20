package com.eduhdsdk.toolcase;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.classroomsdk.common.ToolsPenType;
import com.classroomsdk.utils.FullScreenTools;
import com.eduhdsdk.R;
import com.eduhdsdk.ui.ColorSelectorView;

public class ToolsPenPopupWindow {

    private Context mContext;
    private PopupWindow popupWindow;

    private onToolsPenListener listener;

    public ToolsPenType type = ToolsPenType.fountainPen;

    private LinearLayout frame_ll;//

    // 工具箱的画笔颜色为1，其余的为2
    private int typeColor = -1;
    public ColorSelectorView colorSelectorView;

    /**
     * 选中颜色值
     */
    public interface onToolsPenListener {
        //颜色值
        void SelectedColor(int color);

        //何种画笔
        void SelectedPen(ToolsPenType penType);

        //seekbar进度daxiao
        void SeekBarProgress(int progress);
    }

    public void SetOnToolsListener(onToolsPenListener toolsListener) {
        this.listener = toolsListener;
    }


    public static int mSeekbarPenProgress = 10;

    public ToolsPenPopupWindow(Context context, boolean direction, boolean isShowPens, int typeColor) {
        this.mContext = context;
        this.typeColor = typeColor;
        initview(direction, isShowPens, typeColor);
    }

    private void initview(boolean direction, boolean isShowPens, int typeColor) {
        View contextview = LayoutInflater.from(mContext).inflate(R.layout.tk_item_pen, null, false);

        frame_ll = contextview.findViewById(R.id.frame_ll);
        final ImageView iv_fountainPen = contextview.findViewById(R.id.iv_frame_fountainPen);
        final ImageView iv_nitePen = contextview.findViewById(R.id.iv_frame_nitePen);
        final ImageView iv_line = contextview.findViewById(R.id.iv_frame_line);
        final ImageView iv_arrows = contextview.findViewById(R.id.iv_frame_arrows);
        ImageView iv_right_arrows = contextview.findViewById(R.id.frame_right_arrows);
        ImageView iv_bottom_arrows = contextview.findViewById(R.id.frame_bottom_arrows);

        LinearLayout pens_top = contextview.findViewById(R.id.frame_pens_top);
        if (direction) {
            iv_right_arrows.setVisibility(View.VISIBLE);
        } else {
            iv_bottom_arrows.setVisibility(View.VISIBLE);
        }

        if (isShowPens) {
            pens_top.setVisibility(View.VISIBLE);
        } else {
            pens_top.setVisibility(View.GONE);
        }

        colorSelectorView = contextview.findViewById(R.id.pen_color_select);
        colorSelectorView.setColorSelectResultListen(colorListener);

        SeekBar seekBar = contextview.findViewById(R.id.seek_bar);

        iv_fountainPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = ToolsPenType.fountainPen;
                iv_fountainPen.setImageResource(R.drawable.tk_frame_icon_pen_selected);
                iv_nitePen.setImageResource(R.drawable.tk_frame_icon_yingguangbi_default);
                iv_line.setImageResource(R.drawable.tk_frame_icon_line_default);
                iv_arrows.setImageResource(R.drawable.tk_frame_icon_jiantou_default);
                if (listener != null) {
                    listener.SelectedPen(type);
                }
            }
        });
        iv_nitePen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = ToolsPenType.nitePen;
                iv_fountainPen.setImageResource(R.drawable.tk_frame_icon_pen_default);
                iv_nitePen.setImageResource(R.drawable.tk_frame_icon_yingguangbi_selected);
                iv_line.setImageResource(R.drawable.tk_frame_icon_line_default);
                iv_arrows.setImageResource(R.drawable.tk_frame_icon_jiantou_default);
                if (listener != null) {
                    listener.SelectedPen(type);
                }
            }
        });
        iv_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = ToolsPenType.line;
                iv_fountainPen.setImageResource(R.drawable.tk_frame_icon_pen_default);
                iv_nitePen.setImageResource(R.drawable.tk_frame_icon_yingguangbi_default);
                iv_line.setImageResource(R.drawable.tk_frame_icon_line_selected);
                iv_arrows.setImageResource(R.drawable.tk_frame_icon_jiantou_default);
                if (listener != null) {
                    listener.SelectedPen(type);
                }
            }
        });
        iv_arrows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = ToolsPenType.arrows;
                iv_fountainPen.setImageResource(R.drawable.tk_frame_icon_pen_default);
                iv_nitePen.setImageResource(R.drawable.tk_frame_icon_yingguangbi_default);
                iv_line.setImageResource(R.drawable.tk_frame_icon_line_default);
                iv_arrows.setImageResource(R.drawable.tk_frame_icon_jiantou_selected);
                if (listener != null) {
                    listener.SelectedPen(type);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

        seekBar.setProgress(10);
        seekBar.setMax(92);

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


    public void showPopPen(View view, int parentWidth, View parentView) {
        if (popupWindow != null) {
            int viewWidth = view.getWidth();
            int viewheight = view.getHeight();
            int leftOffset = (parentWidth - viewWidth) / 2;
            int width = popupWindow.getContentView().getMeasuredWidth();
            int height = popupWindow.getContentView().getMeasuredHeight();
            colorSelectorView.changeColorSelect();
            popupWindow.showAsDropDown(view, -(width + leftOffset), -((height / 2) + (viewheight / 2)));
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
