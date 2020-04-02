package com.eduhdsdk.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eduhdsdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/25.
 */

public class SendGiftAdapter extends BaseAdapter {

    private Activity activity;
    private List<Picture> pictures = new ArrayList<Picture>();

    public SendGiftAdapter(Activity activity, List<String> images) {
        super();
        this.activity = activity;
        for (int i = 0; i < images.size(); i++) {
            Picture picture = new Picture(images.get(i));
            pictures.add(picture);
        }
    }

    @Override
    public int getCount() {
        if (null != pictures) {
            return pictures.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.tk_layout_send_gift_item, null);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.iv_send_gift_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picture picture = pictures.get(position);

        if (activity != null && !activity.isDestroyed()) {
            Glide.with(activity).asDrawable()
                    .load(picture.getImageUrl())
                    .into(viewHolder.image);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public ImageView image;
    }

    class Picture {
        private String image_url;

        public Picture(String image_url) {
            this.image_url = image_url;
        }

        public String getImageUrl() {
            return image_url;
        }
    }

}
