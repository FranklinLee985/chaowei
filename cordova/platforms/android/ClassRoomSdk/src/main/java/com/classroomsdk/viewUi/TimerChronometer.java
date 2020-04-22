package com.classroomsdk.viewUi;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Chronometer;
import com.classroomsdk.R;
import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatHelper;
import skin.support.widget.SkinCompatSupportable;
import skin.support.widget.SkinCompatTextHelper;

import static skin.support.widget.SkinCompatHelper.INVALID_ID;

/**
 * date 2019/1/10
 * version
 * describe
 *
 * @author hxd
 */
public class TimerChronometer extends Chronometer implements SkinCompatSupportable {
    private SkinCompatTextHelper mHelper;

    int textColor;

    public TimerChronometer(Context context) {
        super(context);
    }

    public TimerChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public TimerChronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = new SkinCompatTextHelper(this);
        mHelper.loadFromAttributes(attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.timerChronometer);
        textColor = typedArray.getResourceId(R.styleable.timerChronometer_chronometerColor, INVALID_ID);
        typedArray.recycle();
        applyTextColorResource();
    }

    private void applyTextColorResource() {
        int checkResourceId = SkinCompatHelper.checkResourceId(textColor);
        if (checkResourceId != INVALID_ID) {
            int color = SkinCompatResources.getColor(getContext(), textColor);
            setTextColor(color);
        }
    }

    @Override
    public void applySkin() {
        if (mHelper != null) {
            mHelper.applySkin();
        }
        applyTextColorResource();
    }
}
