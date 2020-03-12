package com.eduhdsdk.toolcase;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.classroomsdk.bean.ShowPageBean;
import com.eduhdsdk.R;

import skin.support.content.res.SkinCompatResources;

public class PageNumsPopupWindow {

    private Context mContext;
    private PopupWindow popupWindow;

    public onNumListener listener;
    private ListView listView;
    private PageNumAdapter adapter;
    private int currentNum = 1;
    private int nums = 1;
    TextView tv_nums;
    ImageView page_iv_arrow;
    private ShowPageBean mShowPageBean;
    private int itemHeight;

    /**
     * 选中颜色值
     */
    public interface onNumListener {
        //seekbar进度daxiao
        void setNum(int num, ShowPageBean showPageBean);
    }

    public void SetonNumListener(onNumListener toolsListener) {
        this.listener = toolsListener;
    }


    public PageNumsPopupWindow(Context context) {
        this.mContext = context;
        initview();
    }

    private void initview() {
        View contextview = LayoutInflater.from(mContext).inflate(R.layout.tk_item_page_list, null, false);
        listView = contextview.findViewById(R.id.page_listview);

        adapter = new PageNumAdapter();
        listView.setAdapter(adapter);
        contextview.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        itemHeight = contextview.getMeasuredHeight();
        if (popupWindow == null) {
            popupWindow = new PopupWindow(mContext);
        }
        popupWindow.setContentView(contextview);
        popupWindow.setWidth(contextview.getMeasuredWidth());
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tv_nums.setTextColor(mContext.getResources().getColor(R.color.white));
                page_iv_arrow.setImageResource(R.drawable.tk_icon_xiala_default);
            }
        });
    }

    public void showPopPen(View view, ImageView iv_arrow, int parenthight, ShowPageBean showPageBean) {
        this.tv_nums = (TextView) view;
        this.page_iv_arrow = iv_arrow;
        if (popupWindow != null) {
            this.currentNum = showPageBean.getFiledata().getCurrpage();
            this.nums = showPageBean.getFiledata().getPagenum();
            popupWindow.setHeight(itemHeight * (nums > 4 ? 4 : nums));
            this.mShowPageBean = showPageBean;
            adapter.notifyDataSetChanged();
            int viewWidth = view.getWidth();
            int viewheight = view.getHeight();
            int leftOffset = (parenthight - viewheight) / 2;
            int width = popupWindow.getWidth();
            int height = popupWindow.getHeight();
            popupWindow.showAsDropDown(view, viewWidth / 2 - (width / 2), -(leftOffset + viewheight + height + 3));
            tv_nums.setTextColor(SkinCompatResources.getColor(mContext, R.color.tk_page_num_select));
            page_iv_arrow.setImageResource(R.drawable.tk_icon_up_default);
            listView.setSelection(currentNum - 2);
        }
    }


    public void dismisspop() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    class PageNumAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return nums;
        }

        @Override
        public Object getItem(int position) {
            return nums;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.tk_item_page_list_tv, null, false);
                holder = new ViewHolder();
                holder.tv_num = (TextView) convertView.findViewById(R.id.page_list_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_num.setText(String.valueOf(position + 1));
            if ((position + 1) == currentNum) {
                holder.tv_num.setTextColor(SkinCompatResources.getColor(mContext, R.color.tk_page_num_select));
            } else {
                holder.tv_num.setTextColor(mContext.getResources().getColor(R.color.white));
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.setNum(position + 1, mShowPageBean);
                        dismisspop();
                    }
                }
            });


            return convertView;
        }

        class ViewHolder {
            TextView tv_num;
        }
    }

}
