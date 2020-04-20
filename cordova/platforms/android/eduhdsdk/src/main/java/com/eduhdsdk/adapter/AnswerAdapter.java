package com.eduhdsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.classroomsdk.bean.AnswerBean;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.talkcloud.room.TKRoomManager;

import java.util.List;

/**
 * 答题器的答案个数的adapter
 * Created by YF on 2018/12/28 0028.
 */

public class AnswerAdapter extends BaseAdapter {

    private List<AnswerBean> list;
    private Context activity;
    private CheckBoxCallBack callBack;

    private int width;//popupwindow的宽
    //是否可以选择答案
    private boolean isCheckable = true;

    private int itemHeight;

    public AnswerAdapter(Context activity) {
        this.activity = activity;
    }

    public void setData(List<AnswerBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<AnswerBean> getList() {
        return list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    public int getItemHeight() {
        return itemHeight;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.tk_item_answer, null, false);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = convertView.findViewById(R.id.cb);
            convertView.setTag(viewHolder);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.checkBox.getLayoutParams();
            layoutParams.height = width / 5;
            layoutParams.width = width / 5;
            viewHolder.checkBox.setLayoutParams(layoutParams);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkBox.setTag(position);
        viewHolder.checkBox.setEnabled(isCheckable);

        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL || TKRoomManager.getInstance().getMySelf().role == -1) {
            viewHolder.checkBox.setEnabled(false);
        }

        if (position == 0) {
            viewHolder.checkBox.setBackgroundResource(R.drawable.tk_selector_answer_a);
        } else if (position == 1) {
            viewHolder.checkBox.setBackgroundResource(R.drawable.tk_selector_answer_b);
        } else if (position == 2) {
            viewHolder.checkBox.setBackgroundResource(R.drawable.tk_selector_answer_c);
        } else if (position == 3) {
            viewHolder.checkBox.setBackgroundResource(R.drawable.tk_selector_answer_d);
        } else if (position == 4) {
            viewHolder.checkBox.setBackgroundResource(R.drawable.tk_selector_answer_e);
        } else if (position == 5) {
            viewHolder.checkBox.setBackgroundResource(R.drawable.tk_selector_answer_f);
        } else if (position == 6) {
            viewHolder.checkBox.setBackgroundResource(R.drawable.tk_selector_answer_g);
        } else if (position == 7) {
            viewHolder.checkBox.setBackgroundResource(R.drawable.tk_selector_answer_h);
        }

        final AnswerBean bean = list.get(position);
        if (bean != null) {
            if (bean.isChecked()) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }
        }

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_LASSPATROL) {
                    int po = (int) buttonView.getTag();
                    if (isChecked) {
                        list.get(po).setChecked(true);
                    } else {
                        list.get(po).setChecked(false);
                    }
                    if (callBack != null) {
                        callBack.checkedChangeCallBack(buttonView, isChecked);
                    }
                }
            }
        });

        itemHeight = convertView.getMeasuredHeight();
        return convertView;
    }

    class ViewHolder {
        private CheckBox checkBox;
    }

    public void setCallBack(CheckBoxCallBack callBack) {
        this.callBack = callBack;
    }

    public interface CheckBoxCallBack {

        void checkedChangeCallBack(CompoundButton buttonView, boolean isChecked);
    }

    //设置是否可以选择答案
    public void setCheckable(boolean checkable) {
        isCheckable = checkable;
        notifyDataSetChanged();
    }
}
