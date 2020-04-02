package com.eduhdsdk.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2018/12/3/003.
 */

public class ChlistView extends ListView {


    private final boolean NEED_CHECK_FIELDS = true;

    private float mLastMotionY;
    private float mDiffY;

    public ChlistView(Context context) {
        super(context);
    }

    public ChlistView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChlistView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void requestLayout() {
        if (NEED_CHECK_FIELDS) {
            checkFields();
        }
        super.requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (NEED_CHECK_FIELDS) {
            checkFields();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void layoutChildren() {
        try {
            super.layoutChildren();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void checkFields() {

        try {
            final Class<AdapterView> c = (Class<AdapterView>) Class.forName("android.widget.AdapterView");
            Field field;
            boolean accessible;

            field = c.getDeclaredField("mBlockLayoutRequests");
            accessible = field.isAccessible();
            field.setAccessible(true);

            field.setAccessible(accessible);

            field = c.getDeclaredField("mInLayout");
            accessible = field.isAccessible();
            field.setAccessible(true);

            field.setAccessible(accessible);
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                mDiffY = event.getY() - mLastMotionY;

                if (isNeedIntercept()) {
                    return super.onInterceptTouchEvent(event);
                } else {
                    return false;
                }
            }
            case MotionEvent.ACTION_DOWN: {
                mLastMotionY = event.getY();
                mDiffY = 0;
                break;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isNeedIntercept()) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    private boolean isNeedIntercept() {
        return Math.abs(mDiffY) > 10;
    }
}
