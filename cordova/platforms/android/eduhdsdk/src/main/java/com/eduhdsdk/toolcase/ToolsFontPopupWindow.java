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
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.classroomsdk.common.ToolsPenType;
import com.classroomsdk.utils.FullScreenTools;
import com.classroomsdk.utils.KeyBoardUtil;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.ui.ColorSelectorView;
import com.talkcloud.room.TKRoomManager;

public class ToolsFontPopupWindow {

    private Context mContext;
    private PopupWindow popupWindow;
    private onToolsPenListener listener;

    public ToolsPenType type = ToolsPenType.fountainPen;
    private RelativeLayout frame_rl;
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

    //ture 白板区域  false 小白板区域
    public ToolsFontPopupWindow(Context context, boolean direction) {
        this.mContext = context;
        initview(direction);
    }

    //初始化
    private void initview(boolean direction) {
        View contextview = LayoutInflater.from(mContext).inflate(R.layout.tk_item_font, null, false);
        frame_rl = contextview.findViewById(R.id.tk_tools_frame_rl);
        LinearLayout frame_ll = contextview.findViewById(R.id.frame_ll);
        RelativeLayout rl_seekbar = contextview.findViewById(R.id.rl_seekbar);

        ImageView frame_font_right = contextview.findViewById(R.id.frame_font_right);
        ImageView frame_font_bottom = contextview.findViewById(R.id.frame_font_bottom);

        colorSelectorView = contextview.findViewById(R.id.font_color_select);
        colorSelectorView.setColorSelectResultListen(colorListener);

        if (direction) {
            frame_font_right.setVisibility(View.VISIBLE);
            //如果是白板区域，并且当前用户是学生，并且企业配置项开启(文字不显示画笔大小)配置
            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT && RoomControler.isShowFontSize()) {
                rl_seekbar.setVisibility(View.GONE);
                //重新计算隐藏后选择画笔颜色区域大小
                RelativeLayout.LayoutParams frame = (RelativeLayout.LayoutParams) frame_ll.getLayoutParams();
                frame.height = KeyBoardUtil.dp2px(mContext, 100);
                frame_ll.setLayoutParams(frame);
            }
        } else {
            frame_font_bottom.setVisibility(View.VISIBLE);
        }

        SeekBar seekBar = contextview.findViewById(R.id.seek_bar);


        seekBar.setProgress(10);
        seekBar.setMax(92);
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
     * 工具栏显示
     *
     * @param view
     * @param parentWidth
     */
    public void showPopPen(View view, int parentWidth) {
        if (popupWindow != null) {
            int viewWidth = view.getWidth();
            int viewheight = view.getHeight();
            int leftOffset = (parentWidth - viewWidth) / 2;

            colorSelectorView.changeColorSelect();
            popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int width = popupWindow.getContentView().getMeasuredWidth();
            int height = popupWindow.getContentView().getMeasuredHeight();
            popupWindow.showAsDropDown(view, -(width + leftOffset), -((height / 2) + (viewheight / 2)));
        }
    }

    /**
     * 小白板显示
     *
     * @param view
     * @param types
     * @param isHaiping
     */
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
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }
}
