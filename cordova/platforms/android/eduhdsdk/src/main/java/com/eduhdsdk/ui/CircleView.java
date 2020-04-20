package com.eduhdsdk.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by YF on 2018/11/8 0008.
 */

public class CircleView extends View {

    //    定义画笔
    Paint paint;
    String color = "#00000000";
    private int width = 8;
    private int height = 8;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //        实例化画笔对象
        paint = new Paint();
//        给画笔设置颜色
        paint.setColor(Color.parseColor(color));
//        设置画笔属性
        paint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);//设置画笔粗细
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(heightMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*四个参数：
                参数一：圆心的x坐标
                参数二：圆心的y坐标
                参数三：圆的半径
                参数四：定义好的画笔
                */
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
    }

    public void setColor(String color) {
        this.color = color;
        paint.setColor(Color.parseColor(color));
        postInvalidate();
    }

    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
        postInvalidate();
    }
}
