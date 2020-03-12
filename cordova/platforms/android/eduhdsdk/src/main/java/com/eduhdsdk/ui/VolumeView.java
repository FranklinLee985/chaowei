package com.eduhdsdk.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2018/9/4/004.  音量波动条
 */

public class VolumeView extends View {
    //几个音量条
    private int mTotal = 4;
    //默认音量
    private int mPrecess = 1;
    //音量宽度
    private int mRectWidth = 5;
    //条高度
    private int mRectHeight = 100;
    //条与条间隔距离
    private int space = 6;
    private int mTop = 0;
    private int mBottom;
    private Paint mPaint;
    private int color;
    private RectF rectF;

    public VolumeView(Context context) {
        super(context);
        init();
    }

    public VolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VolumeView(Context context, AttributeSet attrs,
                      int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL.FILL);
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 1; i <= mTotal; i++) {
            drawRect(i, canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRectWidth = space = getMeasuredWidth() / 8;
        mRectHeight = (int) (space * 3.5);
        mTop = (getMeasuredHeight() - mRectHeight) / 2;
        mBottom = mTop + mRectHeight;
        //setMeasuredDimension(4*5 + 4*6,heightMeasureSpec);
    }

    public void setIndex(int index) {
        if (mPrecess == index) {
            return;
        }
        mPrecess = index;
        invalidate();
    }

    /**
     * @param i
     * @param canvas
     */
    private void drawRect(int i, Canvas canvas) {
        color = i <= mPrecess ? Color.parseColor("#FFDC54") : Color.parseColor("#ffffff");
        mPaint.setColor(color);
        rectF.left = getLeft(i);
        rectF.top = mTop;
        rectF.right = rectF.left + mRectWidth;
        rectF.bottom = mBottom;
        canvas.drawRoundRect(rectF, 2, 2, mPaint);
    }

    private int getLeft(int i) {
        return (i - 1) * (mRectWidth + space);
    }
}