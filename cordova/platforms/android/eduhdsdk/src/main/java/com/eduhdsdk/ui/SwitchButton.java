package com.eduhdsdk.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.eduhdsdk.R;

import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatHelper;
import skin.support.widget.SkinCompatSupportable;

import static skin.support.widget.SkinCompatHelper.INVALID_ID;

/**
 * date 2019/4/8
 * version
 * describe
 *
 * @author hxd
 */
public class SwitchButton extends View implements SkinCompatSupportable {

    private static final int DEFAULT_TOOGLE_WIDTH = 58;//默认的宽度
    private static final int DEFAULT_TOOGLE_HEIGHT = 36;//默认的高度

    private int borderWidth;//边线宽度

    private int borderColor;//边线颜色

    private int bgColor;//背景颜色

    private int checkedColorResId;//开关为开的时候的背景色资源id

    private int checkedColor;//开关为开的时候的背景色

    private int shadowColor;//开关切换时需要绘制的一层背景色

    private int buttonColor;//按钮的颜色

    private int animationDuration;//动画时间

    private Paint paint;

    private int checkState = 1;//按钮的开关状态【默认为关闭状态】

    private static final int CHECKED = 0;//打开状态

    private static final int UNCHECKED = 1;//关闭状态

    private boolean isAnimation = false;//是否在滑动中

    /**
     * 背景位置
     */
    private float left;
    private float top;
    private float right;
    private float bottom;
    private float centerX;
    private float centerY;

    private float height;//背景高度

    private float width;//背景宽度

    private float viewRadius;//背景半径

    private float buttonRadius;//按钮半径

    private float mButtonX;//按钮的偏移量

    private ValueAnimator valueAnimator;

    private final android.animation.ArgbEvaluator argbEvaluator
            = new android.animation.ArgbEvaluator();

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton, defStyleAttr, 0);
        borderWidth = (int) array.getDimension(R.styleable.SwitchButton_border_width,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        borderColor = array.getColor(R.styleable.SwitchButton_border_color, getResources().getColor(R.color.white));
        bgColor = array.getColor(R.styleable.SwitchButton_bg_color, getResources().getColor(R.color.white));

        checkedColorResId = array.getResourceId(R.styleable.SwitchButton_checked_color, INVALID_ID);

        shadowColor = array.getColor(R.styleable.SwitchButton_shadow_color, getResources().getColor(R.color.button_gray));
        buttonColor = array.getColor(R.styleable.SwitchButton_button_color, getResources().getColor(R.color.white));
        animationDuration = array.getInt(R.styleable.SwitchButton_animation_duration, 400);
        array.recycle();
        applySkin();
        init();
    }

    /**
     * 初始化一些变量设置
     */
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);

        valueAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(animationDuration);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(animatorUpdateListener);
        valueAnimator.addListener(animatorListener);
    }

    private ValueAnimator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (checkState == UNCHECKED) {
                checkState = CHECKED;
                isAnimation = false;
                if (null != onCheckListener) {
                    onCheckListener.onCheck(true);
                }
            } else if (checkState == CHECKED) {
                checkState = UNCHECKED;
                isAnimation = false;
                if (null != onCheckListener) {
                    onCheckListener.onCheck(false);
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float totalOffset = width - 2 * buttonRadius;
            if (checkState == UNCHECKED) {//关闭状态时
                mButtonX = totalOffset * (Float) animation.getAnimatedValue();
                bgColor = (int) argbEvaluator.evaluate(
                        (Float) animation.getAnimatedValue(),
                        getResources().getColor(R.color.white),
                        checkedColor
                );
            } else if (checkState == CHECKED) {//打开状态时
                mButtonX = totalOffset - totalOffset * (Float) animation.getAnimatedValue();
                bgColor = (int) argbEvaluator.evaluate(
                        (Float) animation.getAnimatedValue(),
                        checkedColor, getResources().getColor(R.color.white)
                );
            }
            postInvalidate();
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpec = MeasureSpec.getMode(heightMeasureSpec);

        if (widthSpec == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_TOOGLE_WIDTH, MeasureSpec.EXACTLY);
        }

        if (heightSpec == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_TOOGLE_HEIGHT, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        height = h - borderWidth - borderWidth;
        width = w - borderWidth - borderWidth;

        viewRadius = height * 0.5f;
        buttonRadius = viewRadius - borderWidth;
        //buttonRadius = viewRadius;

        left = borderWidth;
        top = borderWidth;
        right = w - borderWidth;
        bottom = h - borderWidth;

        centerX = (left + right) * 0.5f;
        centerY = (top + bottom) * 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStrokeWidth(borderWidth);
        paint.setColor(getResources().getColor(R.color.white));
        paint.setStyle(Paint.Style.FILL);
        //绘制白色背景
        drawWhitBg(canvas, paint);
        //绘制边线
        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(borderColor);
        paint.setColor(bgColor);
        drawWhitBg(canvas, paint);
        //绘制按钮滑动过程中的渐变边框
        float des = (mButtonX * viewRadius / (width - 2 * viewRadius)) * 0.5f;//滑动过程中渐变背景框的半径
        paint.setColor(bgColor);
        paint.setStrokeWidth(des * 2);
        paint.setStyle(Paint.Style.STROKE);
        if (des == 0) {
            canvas.drawRoundRect(new RectF(left + des + borderWidth, top + des + borderWidth, right - des - borderWidth, bottom - des - borderWidth), viewRadius, viewRadius, paint);
        } else {
            canvas.drawRoundRect(new RectF(left + des, top + des, right - des, bottom - des), viewRadius, viewRadius, paint);
        }

        //填充按钮左边因为画渐变边框而留下来的白框
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        canvas.drawCircle(left + borderWidth + viewRadius, borderWidth + viewRadius, viewRadius - borderWidth, paint);
        canvas.drawRect(
                left + viewRadius + borderWidth, top + borderWidth,
                mButtonX + viewRadius - borderWidth, top + 2 * viewRadius - borderWidth,
                paint);

//        //绘制按钮
        paint.setColor(buttonColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(buttonRadius + mButtonX + borderWidth, centerY, buttonRadius, paint);
        paint.setColor(shadowColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(buttonRadius + mButtonX + borderWidth, centerY, buttonRadius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        int eventAsked = event.getActionMasked();
        switch (eventAsked) {
            case MotionEvent.ACTION_DOWN:
                if (isAnimation) {
                    return false;
                }
                if (checkState == UNCHECKED) {
                    turnOn();
                } else if (checkState == CHECKED) {
                    turnOff();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     * 打开
     */
    public void turnOn() {
        isAnimation = true;
        valueAnimator.start();
    }

    /**
     * 关闭
     */
    public void turnOff() {
        isAnimation = true;
        valueAnimator.start();
    }

    /**
     * 绘制背景
     *
     * @param canvas
     * @param paint
     */
    private void drawWhitBg(Canvas canvas, Paint paint) {
        canvas.drawRoundRect(new RectF(left, top, right, bottom), viewRadius, viewRadius, paint);
    }

    /**
     * 定义一个选中接口回调
     */
    OnCheckListener onCheckListener;

    @Override
    public void applySkin() {
        int checkResourceId = SkinCompatHelper.checkResourceId(checkedColorResId);
        if (checkResourceId != INVALID_ID) {
            checkedColor = SkinCompatResources.getColor(getContext(), checkedColorResId);
        }
    }

    public interface OnCheckListener {
        void onCheck(boolean isCheck);
    }

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }
}
