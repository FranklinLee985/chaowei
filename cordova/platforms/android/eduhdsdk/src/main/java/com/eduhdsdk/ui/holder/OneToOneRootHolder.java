package com.eduhdsdk.ui.holder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.classroomsdk.viewUi.DownloadProgressView;
import com.eduhdsdk.R;
import com.eduhdsdk.tools.ShowTrophyUtil;

/**
 * date 2018/11/16
 * version
 * describe 一对一View持有类
 *
 * @author hxd
 */
public class OneToOneRootHolder extends TKBaseRootHolder {


    public OneToOneRootHolder(View rootView) {
        this.mRootView = rootView;
        findView();
    }


    //加载Web的layout
    public RelativeLayout rl_web;
    public RelativeLayout ll_wb_container;
    //****************************************   老师 学生 视频框容器
    public RelativeLayout lin_menu;
    //视频布局 视频交互按钮
    public RelativeLayout rel_video_change;
    public ImageView iv_video_change;

    public RelativeLayout rlmain;
    public FrameLayout wb_protogenesis;

    public DownloadProgressView fl_downloadProgress;

    @Override
    protected void findView() {
        super.findView();

        //加载Web的layout
        rl_web = (RelativeLayout) mRootView.findViewById(R.id.rl_web);
        ll_wb_container = (RelativeLayout) mRootView.findViewById(R.id.ll_wb_container);

        //****************************************   老师 学生 视频框容器
        lin_menu = (RelativeLayout) mRootView.findViewById(R.id.lin_menu);
        rel_video_change = (RelativeLayout) mRootView.findViewById(R.id.rel_video_change);
        iv_video_change = (ImageView) mRootView.findViewById(R.id.iv_video_change);

        fl_downloadProgress = mRootView.findViewById(R.id.fl_downloadprogress);

        //小白板
        wb_protogenesis = mRootView.findViewById(R.id.wb_protogenesis);
    }

}
