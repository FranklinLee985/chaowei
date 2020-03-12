package com.eduhdsdk.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.classroomsdk.bean.AnswerDetailsBean;
import com.eduhdsdk.R;

import java.util.List;

/**
 * 答题器详情的adapter
 * Created by YF on 2018/12/28 0028.
 */

public class AnswerDetailsAdapter extends BaseAdapter {

    private List<AnswerDetailsBean> list;
    private Context activity;
    private float textSize = 0;

    public AnswerDetailsAdapter(Context activity) {
        this.activity = activity;
    }

    public void setData(List<AnswerDetailsBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void clear() {
        if (list != null) {
            list.clear();
        }
        notifyDataSetChanged();
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.tk_item_answer_details, null, false);
            viewHolder = new ViewHolder();
            viewHolder.amswer_item_details_name = convertView.findViewById(R.id.amswer_item_details_name);
            viewHolder.amswer_item_details_answers = convertView.findViewById(R.id.amswer_item_details_answers);
            viewHolder.amswer_item_details_time = convertView.findViewById(R.id.amswer_item_details_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.amswer_item_details_name.setText(list.get(position).getNickname());
        viewHolder.amswer_item_details_answers.setText(activity.getString(R.string.answer_selections) + list.get(position).getAnswer());
        viewHolder.amswer_item_details_time.setText(list.get(position).getTime());
        if (textSize > 0) {
            viewHolder.amswer_item_details_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            viewHolder.amswer_item_details_answers.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            viewHolder.amswer_item_details_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        return convertView;
    }

    class ViewHolder {
        TextView amswer_item_details_name;
        TextView amswer_item_details_answers;
        TextView amswer_item_details_time;
    }
}
