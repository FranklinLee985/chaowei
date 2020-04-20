package com.eduhdsdk.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by fucc on 2019/1/24.
 * 备注的父布局
 * 修改触摸分发
 */

public class LongToucherLinearLayout extends LinearLayout {

    public long downtime;

    public boolean isIntercept = false;
    public float downX;
    public float downY;

    public LongToucherLinearLayout(Context context) {
        super(context);
    }

    public LongToucherLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LongToucherLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int actionMasked = ev.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                downtime = System.currentTimeMillis();
                downX = ev.getX();
                downY = ev.getY();
                setTag((int) ev.getRawX() + "&&" + (int) ev.getRawY());
                isIntercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (System.currentTimeMillis() - downtime > 600) {
                    isIntercept = true;
                } else {
                    isIntercept = false;
                }
                break;
        }

        if (isIntercept) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }
}
