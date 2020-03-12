package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.eduhdsdk.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018/4/20.
 */

public class PlaySpeedPopupWindowUtils {


    private Activity activity;

    PopupWindow popupWindowPlaySpeed;
    List<String> data;


    private ListView listView;
    private PlaySpeedClick playSpeedClick;
    public PlaySpeedPopupWindowUtils(Activity activity) {
        this.activity = activity;
    }

    public void setPlaySpeedClick(PlaySpeedClick playSpeedClick){
        this.playSpeedClick = playSpeedClick;
    }

    public void showPlaySpeedPopupWindow(View view, int width, int height) {

        //防止用户多次点击，出现多个popupwindow的情况
        if(popupWindowPlaySpeed!=null){
            if(popupWindowPlaySpeed.isShowing()){
                popupWindowPlaySpeed.dismiss();
                return;
            }
        }

        View contentView = LayoutInflater.from(activity).inflate(R.layout.tk_popup_play_speed_layout, null);

        data = new ArrayList<String>();

        for(int i=1;i<7;i++){
            data.add((0.25+0.25*i)+"X");
        }


        listView = (ListView) contentView.findViewById(R.id.lv_play_speed);
        listView.setAdapter(new ArrayAdapter<String>(activity,R.layout.tk_layout_play_speed_item,data));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(playSpeedClick!=null){
                    playSpeedClick.playSpeedClick(data.get(position));
                }
            }
        });


        popupWindowPlaySpeed = new PopupWindow(width, height);
        popupWindowPlaySpeed.setContentView(contentView);

        popupWindowPlaySpeed.setBackgroundDrawable(new BitmapDrawable());
        popupWindowPlaySpeed.setFocusable(false);
        popupWindowPlaySpeed.setOutsideTouchable(false);
//        popupWindowPageNumber.showAtLocation(view, Gravity.CENTER, 0, 0);
        //设置popupwindow在底部页数布局的正上方
        popupWindowPlaySpeed.showAsDropDown(view,-Math.abs((int)(width-view.getWidth())/2),0,Gravity.TOP);
    }

    public void dismissPopupWindow() {
        if (popupWindowPlaySpeed != null) {
            popupWindowPlaySpeed.dismiss();
        }
    }

    /**
     * 定义popupwindow的接口，通过接口和activity进行通信
     */
    public interface PlaySpeedClick {
        void playSpeedClick(String play_speed);
    }
}
