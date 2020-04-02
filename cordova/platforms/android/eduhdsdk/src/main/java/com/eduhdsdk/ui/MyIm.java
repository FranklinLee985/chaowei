package com.eduhdsdk.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by Administrator on 2018/7/31/031.
 */

public class MyIm extends ImageSpan {

    public MyIm(Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {

        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            //添加间距，解决个别手机表情行数较多时最后一行显示不全问题
            int spacing = (int) top / 2;
            fm.ascent = -bottom - spacing;
            fm.top = -bottom - spacing;
            fm.bottom = top + spacing;
            fm.descent = top + spacing;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int transY = 0;
        transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}
