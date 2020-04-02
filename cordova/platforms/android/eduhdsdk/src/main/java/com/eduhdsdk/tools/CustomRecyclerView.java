package com.eduhdsdk.tools;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomRecyclerView extends RecyclerView{
    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    //事件拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        //获取所有view
        int count = getChildCount();
        if (count == 0) {
            return true;
        }

        int x = (int) e.getRawX();
        int y = (int) e.getRawY();


        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            //如果当前触摸点在字view上，由子view去处理，不做拦截，其他由Recyclerview处理
            if (isTouchPointInView(view, x, y)) {
                return false;
            }
        }
        //返回ture Recyclerview拦截下，此view ontouchEvent返回false,不处理本view所有事件，由上层onTouchEvent处理
        return true;
    }

    //不处理
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }

    //判断是否在点击view上
    private boolean isTouchPointInView(View targetView, int xAxis, int yAxis) {
        if (targetView== null) {
            return false;
        }
        int[] location = new int[2];
        targetView.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + targetView.getMeasuredWidth();
        int bottom = top + targetView.getMeasuredHeight();
        if (yAxis >= top && yAxis <= bottom && xAxis >= left
                && xAxis <= right) {
            return true;
        }
        return false;
    }
}
