package com.eduhdsdk.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.classroomsdk.Config;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

/**
 * Created by zhe on 2019/3/29.
 */
public class ColorSelectorView extends View {

    private static final String TAG = "ColorSelectorView";

    private Context mContext;

    private int mViewHeight;
    private int mViewWidth;

    // 背景颜色
    private String mBgColor = "#00000000";
    // 边框颜色
    private String mBorderColor = "#4DFFFFFF";
    // 选中的Item的选中框颜色
    private String mSelectBorderColor = "#FFFFFF";
    // 圆角半径
    private float mCornersRadius = 2;
    // 边框宽度
    private float mBorderWidth = 1;
    // 选中框宽度
    private float mSelectBorderWidth = 2;
    // view padding
    private float mPadding = 5;

    // 每个颜色块宽高
    private int mItemWidth = 8;
    private int mItemHeight = 19;

    // 弹出框的宽度
    private int mPopWidth = 16;
    //选择器的top
    private int mPopTop = 0;
    //选择器的left
    private int mPopLeft = 0;

    private Paint mPaint;
    private RectF mRect;
    private RectF mColorSelectorRect;
    private Path mPath;

    // 选中的index
    public int mSelectIndex = 0;
    //默认的index
    public int defaultIndex = -1;
    // 显示pop
    private boolean showPop;

    private OnClickColorListener colorSelectResultListen;


    public ColorSelectorView(Context context) {
        this(context, null);
    }

    public ColorSelectorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        setBackgroundColor(Color.parseColor(mBgColor));
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPath = new Path();
        mColorSelectorRect = new RectF();
        mRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        //根据view宽高计算最小比值
        float dip = Math.min((mViewWidth - mPadding * 2 - mBorderWidth * 4) / (Config.mColor.length * 8 + 8 + 19), (mViewHeight - mPadding * 2) / (19 + 16 + 4));

        mItemWidth = (int) (dip * 8);
        mItemHeight = (int) (dip * 19);
        mPopTop = (mViewHeight - mItemHeight * 2) / 2 + mItemHeight;
        mPopLeft = (int) ((mViewWidth - dip * (Config.mColor.length * 8 + 8 + 19)) / 2 + mBorderWidth);
        mPopWidth = (int) (dip * 16);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.STROKE);

        // 左侧圆角矩形边框
        mPaint.setColor(Color.parseColor(mSelectBorderColor));
        mPaint.setStrokeWidth(mBorderWidth);
        mRect.set(mPadding + mPopLeft, mBorderWidth / 2 + mPopTop, mItemHeight + mBorderWidth * 2 - mBorderWidth / 2 + mPadding + mPopLeft, mItemHeight + mBorderWidth * 2 - mBorderWidth / 2 + mPopTop);
        canvas.drawRoundRect(mRect, mCornersRadius, mCornersRadius, mPaint);

        // 弹出框边框
        if (showPop) {
            mRect.set(getItemLeft(mSelectIndex) - mPopWidth / 4, getItemTop(mSelectIndex) - mPopWidth - 10, getItemRight(mSelectIndex) + mPopWidth / 4, getItemTop(mSelectIndex) - 10);
            canvas.drawRoundRect(mRect, mCornersRadius, mCornersRadius, mPaint);
        }
        // 颜色条边框
        mPaint.setColor(Color.parseColor(mBorderColor));
        mColorSelectorRect.set(mBorderWidth / 2 + mItemWidth + mItemHeight + mPadding + mPopLeft, mBorderWidth / 2 + mPopTop, mItemWidth * Config.mColor.length + mBorderWidth * 2 - mBorderWidth / 2 + mItemWidth + mItemHeight + mPadding + mPopLeft, mItemHeight + mBorderWidth * 2 - mBorderWidth / 2 + mPopTop);
        canvas.drawRect(mColorSelectorRect, mPaint);

        mPaint.setStyle(Paint.Style.FILL);

        // 左侧圆角矩形内容
        mPaint.setColor(Color.parseColor(Config.mColor[mSelectIndex]));
        mRect.set(mPadding + mPopLeft + mBorderWidth / 2, mBorderWidth / 2 + mPopTop + mBorderWidth / 2, mItemHeight + mBorderWidth * 2 - mBorderWidth / 2 + mPadding + mPopLeft - mBorderWidth / 2, mItemHeight + mBorderWidth * 2 - mBorderWidth / 2 + mPopTop - mBorderWidth / 2);
        canvas.drawRoundRect(mRect, mCornersRadius, mCornersRadius, mPaint);

        // 弹出框填充
        if (showPop) {
            mPaint.setColor(Color.parseColor(Config.mColor[mSelectIndex]));
            mRect.set(getItemLeft(mSelectIndex) - mPopWidth / 4 + mBorderWidth / 2, getItemTop(mSelectIndex) - mPopWidth + mBorderWidth / 2 - 10, getItemRight(mSelectIndex) + mPopWidth / 4 - mBorderWidth / 2, getItemTop(mSelectIndex) - mBorderWidth / 2 - 10);
            canvas.drawRoundRect(mRect, mCornersRadius, mCornersRadius, mPaint);
            // 弹出框下方三角
            mPaint.setColor(Color.parseColor(mSelectBorderColor));
            mPath.reset();
            mPath.moveTo(getItemLeft(mSelectIndex) + mPopWidth / 8, getItemTop(mSelectIndex) - 10);
            mPath.lineTo(getItemRight(mSelectIndex) - mPopWidth / 8, getItemTop(mSelectIndex) - 10);
            mPath.lineTo(getItemLeft(mSelectIndex) + mPopWidth / 4, getItemTop(mSelectIndex) - 10 + getItemLeft(4) / 32);
            mPath.lineTo(getItemLeft(mSelectIndex) + mPopWidth / 8, getItemTop(mSelectIndex) - 10);
            mPath.close();
            canvas.drawPath(mPath, mPaint);
        }

        // 颜色条
        for (int i = 0; i < Config.mColor.length; i++) {
            String color = Config.mColor[i];
            float left = getItemLeft(i);
            float top = getItemTop(i);
            float right = getItemRight(i);
            float bottom = getItemBottom(i);
            mRect.set(left, top, right, bottom);
            mPaint.setColor(Color.parseColor(color));
            canvas.drawRect(mRect, mPaint);
        }

        mPaint.setStyle(Paint.Style.STROKE);

        // 选择框
        if (showPop) {
            mPaint.setStrokeWidth(mSelectBorderWidth);
            mPaint.setColor(Color.parseColor(mSelectBorderColor));
            mRect.set(getItemLeft(mSelectIndex) - mSelectBorderWidth / 2, getItemTop(mSelectIndex) - mSelectBorderWidth / 2,
                    getItemRight(mSelectIndex) + mSelectBorderWidth / 2, getItemBottom(mSelectIndex) + mSelectBorderWidth / 2);
            canvas.drawRect(mRect, mPaint);
        }
        mPaint.setStyle(Paint.Style.FILL);
    }

    private float getItemLeft(int index) {
        return index * mItemWidth + mBorderWidth + mItemWidth + mItemHeight + mPadding + mPopLeft;
    }

    private float getItemTop(int index) {
        return mBorderWidth + mPopTop;
    }

    private float getItemRight(int index) {
        return (index + 1) * mItemWidth + mBorderWidth + mItemWidth + mItemHeight + mPadding + mPopLeft;
    }

    private float getItemBottom(int index) {
        return mItemHeight + mBorderWidth + mPopTop;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showPop = true;
                float downX = event.getX();
                float downY = event.getY();
                //判断落点在颜色条目上
                if (mColorSelectorRect.contains(downX, downY)) {
                    float tmpX = downX - mColorSelectorRect.left;
                    mSelectIndex = (int) (tmpX / mItemWidth);
                    if (mSelectIndex > Config.mColor.length - 1) {
                        mSelectIndex = Config.mColor.length - 1;
                    } else if (mSelectIndex < 0) {
                        mSelectIndex = 0;
                    }
                    postInvalidate();
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                showPop = true;
                float moveX = event.getX();
                float tmpX = moveX - mColorSelectorRect.left;
                mSelectIndex = (int) (tmpX / mItemWidth);

                if (mSelectIndex > Config.mColor.length - 1) {
                    mSelectIndex = Config.mColor.length - 1;
                } else if (mSelectIndex < 0) {
                    mSelectIndex = 0;
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                performClick();
                showPop = false;
                colorSelectResultListen.setColor(Color.parseColor(Config.mColor[mSelectIndex]));
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                        "__all", "primaryColor", Config.mColor[mSelectIndex]);
                postInvalidate();
                break;
        }
        return true;
    }

    public void setColorSelectResultListen(OnClickColorListener colorSelectResultListen) {
        this.colorSelectResultListen = colorSelectResultListen;
    }

    public void changeColorSelect() {
        if (defaultIndex < 0) {
            RoomUser roomUser = TKRoomManager.getInstance().getMySelf();
            if (roomUser != null && roomUser.properties.containsKey("primaryColor")) {
                String userColor = (String) roomUser.properties.get("primaryColor");
                for (int x = 0; x < Config.mColor.length; x++) {
                    if (userColor.equals(Config.mColor[x])) {
                        mSelectIndex = defaultIndex = x;
                        break;
                    }
                }
            }
        }
        //默认值
        colorSelectResultListen.setColor(Color.parseColor(Config.mColor[mSelectIndex]));
        TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                "__all", "primaryColor", Config.mColor[mSelectIndex]);
    }

    //清除画笔选择颜色
    public void cleanDefaultColor() {
        defaultIndex = -1;
    }

    public interface OnClickColorListener {
        void setColor(int color);
    }

    public void setmSelectIndex(int mSelectIndex) {
        this.mSelectIndex = mSelectIndex;
        postInvalidate();
    }
}
