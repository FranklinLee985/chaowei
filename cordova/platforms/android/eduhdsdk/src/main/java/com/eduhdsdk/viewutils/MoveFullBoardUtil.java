package com.eduhdsdk.viewutils;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


public class MoveFullBoardUtil {

    private View view;
    private static MoveFullBoardUtil Instance;
    //宽高
    private int containerWidth = 0;
    private int containerHeight = 0;
    private RelativeLayout.LayoutParams viewParams = null;

    public static MoveFullBoardUtil getInstance() {
        if (Instance == null) {
            synchronized (MoveFullBoardUtil.class) {
                if (Instance == null) {
                    Instance = new MoveFullBoardUtil();
                }
            }
        }
        return Instance;

    }

    public void resetInstance() {
        Instance = null;
    }


    /**
     * 设置可移动区域大小
     *
     * @param containerWidth1  宽
     * @param containerHeight1 高
     */
    public void SetWH(int containerWidth1, int containerHeight1) {
        this.containerHeight = containerHeight1;
        this.containerWidth = containerWidth1;
    }

    /**
     * 设置控件监听
     *
     * @param view
     */
    public void SetViewOnTouchListener(final View view) {
        this.view = view;
        if (view != null) {

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (containerWidth == 0 || containerHeight == 0) {
                        return false;
                    }
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            return true;
                        case MotionEvent.ACTION_MOVE:

                            if (event.getRawX() - view.getWidth() / 2 >= 0 &&
                                    event.getRawX() + view.getWidth() / 2 <= containerWidth &&
                                    event.getRawY() - view.getHeight() / 2 >= 0 &&
                                    event.getRawY() + view.getHeight() / 2 <= containerHeight) {

                                if (viewParams == null) {
                                    viewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                }
                                viewParams.rightMargin = 0;
                                viewParams.bottomMargin = 0;
                                viewParams.leftMargin = (int) (event.getRawX() - view.getWidth() / 2);
                                viewParams.topMargin = (int) (event.getRawY() - view.getHeight() / 2);
                                view.setLayoutParams(viewParams);
                            }

                    }
                    return false;
                }
            });
        }
    }

    public void clean() {
        //关闭之后恢复初始位置 不关闭，位置会一直记录
        if (view != null) {
            RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            view.setLayoutParams(viewParams);
        }

    }
}
