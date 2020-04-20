package com.eduhdsdk.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FixItemListView extends ListView {

    private int mMaxItemCount;

    public FixItemListView(Context context) {
        super(context);
    }

    public FixItemListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixItemListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        resetListViewHeight(mMaxItemCount);
    }

    public void setFixItemCount(int count) {
        resetListViewHeight(count);
    }

    private void resetListViewHeight(int mMaxItemCount) {
        ListAdapter listAdapter = getAdapter();
        if (listAdapter == null || mMaxItemCount == 0 || listAdapter.getCount() <= 0 || mMaxItemCount == this.mMaxItemCount) {
            return;
        }
        View itemView = listAdapter.getView(0, null, this);
        itemView.measure(0, 0);
        int itemHeight = itemView.getMeasuredHeight();
        int itemCount = listAdapter.getCount();
        LinearLayout.LayoutParams layoutParams = null;
        if (itemCount <= mMaxItemCount) {
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * mMaxItemCount);
        }
        setLayoutParams(layoutParams);
        this.mMaxItemCount = mMaxItemCount;
    }

}
