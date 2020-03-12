package com.classroomsdk.viewUi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.classroomsdk.R;

import java.util.ArrayList;
import java.util.List;

import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatTextView;

/**
 * 时间滚动选择器
 * <p>
 * （实现循环滚动 性能略差要优化）
 * <p>
 * Created by fucc
 */
public class WheelView extends ScrollView {
    public static final String TAG = WheelView.class.getSimpleName();
    //选中的文字大小
    private int SelectTextSize = 23;
    //未选中的文字大小
    private int OtherTestSize = 15;
    //是否在滚动
    public boolean isScroll = false;
    //是否禁止滚动
    public boolean isNoScroll = false;
    // 绘制分割线的画笔
    private Paint paint;
    // view宽度
    private int viewWidth;
    public ArrayList<String> nums;

    private int mRootWidth;

    public void setRootWidth(int width) {
        mRootWidth = width;
        SelectTextSize = px2dip(mRootWidth * 20 / 341);
        OtherTestSize = px2dip(mRootWidth * 15 / 341);
    }

    public static class OnWheelViewListener {
        public void onSelected(int selectedIndex, String item) {
        }
    }

    private Context context;
//    private ScrollView scrollView;

    private LinearLayout views;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    List<String> items;

    private List<String> getItems() {
        return items;
    }

    public void setItems(List<String> list) {
        if (null == items) {
            items = new ArrayList<String>();
        }
        items.clear();

//        // 前面和后面补全
//        for (int i = 0; i < offset; i++) {
//            items.add(0, "");
//            items.add("");
//        }
        //补全前面
        for (int i = list.size() / 2; i < list.size(); i++) {
            items.add(list.get(i));
        }

        items.addAll(list);

        //补全后面
        for (int i = 0; i < list.size() / 2; i++) {
            items.add(list.get(i));
        }
        initData();
    }


    public static final int OFF_SET_DEFAULT = 1;
    int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    int displayItemCount; // 每页显示的数量

    int selectedIndex = 1;


    private void init(Context context) {
        this.context = context;
        this.setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        this.addView(views);

        paint = new Paint();
        paint.setColor(SkinCompatResources.getColor(context, R.color.tools_timer_line));
        paint.setStrokeWidth(dip2px(1f));

        nums = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                nums.add("0" + i);
            } else {
                nums.add("" + i);
            }
        }

        scrollerTask = new Runnable() {

            public void run() {

                int newY = getScrollY();
                if (initialY - newY == 0) { // stopped
                    isScroll = false;
                    final int remainder = initialY % itemHeight;
                    final int divided = initialY / itemHeight;
                    if (remainder == 0) {
                        selectedIndex = divided + offset;
                        onSeletedCallBack();
                    } else {
                        if (remainder > itemHeight / 2) {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                                    selectedIndex = divided + 1 + offset;
                                    onSeletedCallBack();
                                }
                            });
                        } else {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    WheelView.this.smoothScrollTo(0, initialY - remainder);
                                    selectedIndex = divided + offset;
                                    onSeletedCallBack();
                                }
                            });
                        }
                    }
                } else {
                    isScroll = true;
                    initialY = getScrollY();
                    WheelView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (items == null && widthMeasureSpec > 0) {
            setItems(nums);
        }
    }

    int initialY;

    Runnable scrollerTask;
    int newCheck = 50;

    public void startScrollerTask() {
        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;

        for (String item : items) {
            views.addView(createView(item));
        }
        refreshItemView(itemHeight * (nums.size() / 2));
    }

    int itemHeight = 0;

    private SkinCompatTextView createView(String item) {
        SkinCompatTextView tv = new SkinCompatTextView(context);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, SelectTextSize);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        int padding = dip2px(5);
        tv.setPadding(padding, padding, padding, padding);
        if (0 == itemHeight) {
//            itemHeight = getViewMeasuredHeight(tv);
            itemHeight = mRootWidth * 45 / 341;
            int viewsHeight = itemHeight * displayItemCount;
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewsHeight));
            ViewGroup.LayoutParams lp = this.getLayoutParams();
            lp.height = viewsHeight;
            this.setLayoutParams(lp);
        }
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, OtherTestSize);
        return tv;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        refreshItemView(t);

        if (t > oldt) {
            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
            scrollDirection = SCROLL_DIRECTION_UP;
        }
    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            SkinCompatTextView itemView = (SkinCompatTextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                itemView.setTextAppearance(context, R.style.tools_timer_select);
                itemView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SelectTextSize);
            } else {
                itemView.setTextAppearance(context, R.style.tools_timer_unselect);
                itemView.setTextSize(TypedValue.COMPLEX_UNIT_SP, OtherTestSize);
            }
        }
    }

    /**
     * 获取选中区域的边界
     */
    int[] selectedAreaBorder;

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }


    private int scrollDirection = -1;
    private static final int SCROLL_DIRECTION_UP = 0;
    private static final int SCROLL_DIRECTION_DOWN = 1;

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[0], viewWidth * 5 / 6, obtainSelectedAreaBorder()[0], paint);
                canvas.drawLine(viewWidth * 1 / 6, obtainSelectedAreaBorder()[1], viewWidth * 5 / 6, obtainSelectedAreaBorder()[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @SuppressLint("WrongConstant")
            @Override
            public int getOpacity() {
                return 0;
            }
        };


        super.setBackgroundDrawable(background);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    /**
     * 选中回调
     */
    private void onSeletedCallBack() {
        updateScroolSelect(selectedIndex);
        if (null != onWheelViewListener) {
            onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }

    }

    public void setSeletion(int position) {
        updateScroolSelect(position + nums.size() / 2);
    }

    public void updateScroolSelect(int position) {
        if (position < nums.size() / 2) {
            position += nums.size();
        } else if (position >= nums.size() / 2 + nums.size()) {
            position -= nums.size();
        }
        selectedIndex = position;
        this.post(new Runnable() {
            @Override
            public void run() {
                WheelView.this.smoothScrollTo(0, (selectedIndex - 1) * itemHeight);
            }
        });
    }

    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    public int getSeletedIndex() {
        return selectedIndex - offset;
    }


    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isNoScroll){
            return true;
        }
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    private OnWheelViewListener onWheelViewListener;

    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dip(float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    public void setNoScroll(boolean noScroll) {
        isNoScroll = noScroll;
    }
}
