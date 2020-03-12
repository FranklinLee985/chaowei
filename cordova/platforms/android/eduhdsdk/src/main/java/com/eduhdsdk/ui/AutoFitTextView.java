package com.eduhdsdk.ui;

/**
 * Created by Administrator on 2018/1/30.
 */

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;

import skin.support.widget.SkinCompatTextView;


/**
 * 自定义TextView，文本内容自动调整字体大小以适应TextView的大小
 *
 * @author yzp
 */
public class AutoFitTextView extends SkinCompatTextView {
    private Paint mTextPaint;
    private float mMaxTextSize; // 获取当前所设置文字大小作为最大文字大小
    private float mMinTextSize = 2;    //最小的字体大小
    public int height;

    public AutoFitTextView(Context context) {
        super(context);
    }

    public AutoFitTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.TOP);
        setLines(1);
        initialise();
    }

    private void initialise() {
        mTextPaint = new TextPaint();
        mTextPaint.set(this.getPaint());
        //默认的大小是设置的大小，如果撑不下了 就改变
        mMaxTextSize = this.getTextSize();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (height != getMeasuredHeight()) {
            height = getMeasuredHeight();
            refitText(height);
        }
    }

    private void refitText(int height) {
        if (height > 0) {
            int availableHeight = height - this.getPaddingTop() - this.getPaddingBottom() - 2;   //减去边距为字体的实际高度
            float trySize = mMaxTextSize;
            mTextPaint.setTextSize(trySize);
            while (mTextPaint.descent() - mTextPaint.ascent() < availableHeight) {   //测量的字体高度过大，不断地缩放
                trySize += 1;  //字体不断地增大来适应
                mTextPaint.setTextSize(trySize);
            }
            while (mTextPaint.descent() - mTextPaint.ascent() > availableHeight) {   //测量的字体高度过大，不断地缩放
                trySize -= 1;  //字体不断地减小来适应
                if (trySize <= mMinTextSize) {
                    trySize = mMinTextSize;  //最小为这个
                    mTextPaint.setTextSize(trySize);
                    break;
                }
                mTextPaint.setTextSize(trySize);
            }
            setTextSize(px2sp(getContext(), trySize));
        }
        initialise();
    }


    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static float px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / fontScale);
    }
}
