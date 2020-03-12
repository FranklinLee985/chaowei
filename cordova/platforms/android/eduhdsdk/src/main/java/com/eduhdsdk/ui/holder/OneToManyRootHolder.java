package com.eduhdsdk.ui.holder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.classroomsdk.viewUi.DownloadProgressView;
import com.eduhdsdk.R;

/**
 * date 2018/11/19
 * version
 * describe 一对多View持有类
 *
 * @author hxd
 */
public class OneToManyRootHolder extends TKBaseRootHolder {

    public OneToManyRootHolder(View rootView) {
        this.mRootView = rootView;
        findView();
    }

    //视频容器与白板容器 间隔
    public View side_view;
    //******   顶部视频框区域
    public RelativeLayout rel_students;
    //单个视频框layout
    public View v_students;
    //******   白板 视频播放 layout
    public RelativeLayout rel_parent;
    //兼容弹出框，写的一个和白板高度相同的view
    public RelativeLayout rel_wb;
    //白板 layout
    public RelativeLayout rel_wb_container;

    public DownloadProgressView fl_downloadProgress;
    public FrameLayout wb_protogenesis;
    //主讲视频占位图
    public RelativeLayout speak_rl_zw;
    @Override
    protected void findView() {
        super.findView();
        //视频容器与白板容器 间隔
        side_view = mRootView.findViewById(R.id.side_view);

        //******   顶部视频框区域speak_rl_zhanwei
        rel_students = (RelativeLayout) mRootView.findViewById(R.id.rel_students);
        //单个视频框layout
        v_students = (View) mRootView.findViewById(R.id.v_student);

        //******   白板 视频播放 layout
        rel_parent = (RelativeLayout) mRootView.findViewById(R.id.rel_parent);
        //兼容弹出框，写的一个和白板高度相同的view
        rel_wb = (RelativeLayout) mRootView.findViewById(R.id.rel_wb);
        //白板 layout
        rel_wb_container = (RelativeLayout) mRootView.findViewById(R.id.rel_wb_container);

        //小白板
        wb_protogenesis = mRootView.findViewById(R.id.wb_protogenesis);

        fl_downloadProgress = mRootView.findViewById(R.id.fl_downloadprogress);

        speak_rl_zw = mRootView.findViewById(R.id.speak_rl_zhanwei);
    }

}
