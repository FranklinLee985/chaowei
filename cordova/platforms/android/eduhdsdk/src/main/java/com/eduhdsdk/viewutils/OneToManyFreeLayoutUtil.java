package com.eduhdsdk.viewutils;

import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eduhdsdk.comparator.PeerIDComparator;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.ui.holder.VideoItemToMany;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * date 2019/4/12
 * version
 * describe 一对多自由視頻布局
 *
 * @author hxd
 */
public class OneToManyFreeLayoutUtil {

    private static OneToManyFreeLayoutUtil Instance;

    public static OneToManyFreeLayoutUtil getInstance() {
        if (Instance == null) {
            synchronized (OneToManyFreeLayoutUtil.class) {
                if (Instance == null) {
                    Instance = new OneToManyFreeLayoutUtil();
                }
            }
        }
        return Instance;

    }

    public void resetInstance() {
        Instance = null;
    }

    private int mScreenValueWidth, mScreenValueHeight, wid_ratio, hid_ratio;
    private ArrayList<VideoItemToMany> notMoveVideoItems;
    //视频宽高，视频间隔，整体视频布局距离上 / 左 距离
    private int videoWidth, videoHeight, videoDivider = 8, layoutTopMargin, layoutLeftMargin;
    //行数 列数
    private int rowNumber = 1, columnNumber = 1;

    //刘海屏 高度
    private int mHeightStatusBar;

    /**
     * 主讲视频
     */
    public void freeVideoDoLayout(ArrayList<VideoItemToMany> videoItems, int screenValueWidth, int screenValueHeight, int heightStatusBar, int w_ratio, int h_ratio) {
        this.notMoveVideoItems = videoItems;
        this.mScreenValueWidth = screenValueWidth - 16;
        this.mScreenValueHeight = screenValueHeight - 16;
        this.mHeightStatusBar = heightStatusBar;
        this.wid_ratio = w_ratio;
        this.hid_ratio = h_ratio;

        //按peerid的升序排序视频列表
        if (RoomControler.isStudentVideoSequence()) {
            getSortPlayingList(notMoveVideoItems);
        }

        //计算布局
        calculationLayout();
        //设置布局
        setLayout();
    }

    /**
     * 对视频用户ID:peerId升序排序
     */
    public static void getSortPlayingList(ArrayList<VideoItemToMany> notMoveVideoItems) {
        //        getPlayingList();//调用一下最新的数据
        if (notMoveVideoItems != null && notMoveVideoItems.size() > 0) {
            List<VideoItemToMany> roomUserList = Collections.synchronizedList(new ArrayList<VideoItemToMany>());
            VideoItemToMany videoItem = null;
            for (VideoItemToMany videoItems : notMoveVideoItems) {
                if (videoItems.role == 0) {
                    videoItem = videoItems;
                } else {
                    roomUserList.add(videoItems);
                }
            }
            PeerIDComparator peerIDComparator = new PeerIDComparator();
            peerIDComparator.setisUp(true);
            Collections.sort(roomUserList, peerIDComparator);
            if (videoItem != null) {
                roomUserList.add(0, videoItem);
            }
            if (roomUserList != null && roomUserList.size() > 0) {
                notMoveVideoItems.clear();
                notMoveVideoItems.addAll(roomUserList);
                roomUserList.clear();
            }
        }
    }

    /**
     * 计算布局
     */
    private void calculationLayout() {
        if (notMoveVideoItems.size() > 0) {
            double line = notMoveVideoItems.size() > 9 ? 4 : notMoveVideoItems.size() + 2 == 5 ? 2 : (notMoveVideoItems.size() + 2) / 3;
            double column = Math.ceil(notMoveVideoItems.size() / line);
            rowNumber = (int) line;
            columnNumber = (int) column;
        }
        calculationSize();
    }

    /**
     * 设置布局
     */
    private void setLayout() {
        if (notMoveVideoItems.size() == 3) {
            doLayout(notMoveVideoItems.get(0), layoutTopMargin, (mScreenValueWidth - videoWidth) / 2);
            doLayout(notMoveVideoItems.get(1), layoutTopMargin + videoHeight + videoDivider, layoutLeftMargin);
            doLayout(notMoveVideoItems.get(2), layoutTopMargin + videoHeight + videoDivider, layoutLeftMargin + videoWidth + videoDivider);

        } else if (notMoveVideoItems.size() == 5) {
            int firstRowLeftMargin = (mScreenValueWidth - videoWidth * 2 - videoDivider) / 2;
            doLayout(notMoveVideoItems.get(0), layoutTopMargin, firstRowLeftMargin);
            doLayout(notMoveVideoItems.get(1), layoutTopMargin, firstRowLeftMargin + videoWidth + videoDivider);
            for (int i = 2; i < 5; i++) {
                doLayout(notMoveVideoItems.get(i), layoutTopMargin + videoHeight + videoDivider, layoutLeftMargin + (videoWidth + videoDivider) * (i - 2));
            }

        } else {
            doLayout(0, notMoveVideoItems.size());
        }
    }

    private void doLayout(int start, int end) {
        for (int i = start; i < end; i++) {
            //当前行数
            int curRow = (i + 1) / columnNumber;
            //余数
            int remainder = (i + 1) % columnNumber;

            if (remainder == 0) {
                remainder = columnNumber;
                curRow--;
            }
            doLayout(notMoveVideoItems.get(i), layoutTopMargin + (videoHeight + videoDivider) * curRow, layoutLeftMargin + (videoWidth + videoDivider) * (remainder - 1));
        }
    }

    private void calculationSize() {
        videoHeight = (mScreenValueHeight - videoDivider * (rowNumber - 1)) / rowNumber;
        videoWidth = videoHeight * wid_ratio / hid_ratio;

        if ((videoWidth * columnNumber + videoDivider * (columnNumber - 1)) > mScreenValueWidth) {
            videoWidth = (mScreenValueWidth - videoDivider * (columnNumber - 1)) / columnNumber;
            videoHeight = videoWidth * hid_ratio / wid_ratio;
        }

        layoutTopMargin = (mScreenValueHeight + 16 - videoHeight * rowNumber - videoDivider * (rowNumber - 1)) / 2;
        layoutLeftMargin = (mScreenValueWidth + 16 - videoWidth * columnNumber - videoDivider * (columnNumber - 1)) / 2;
    }

    private void doLayout(VideoItemToMany videoItemToMany, int topMargin, int leftMargin) {
        doLayout(videoWidth, videoHeight, videoItemToMany, topMargin, leftMargin);
    }


    public void doLayout(int videoWidth, int videoHeight, VideoItemToMany videoItemToMany, int topMargin, int leftMargin) {
        //视频框根布局
        RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) videoItemToMany.parent.getLayoutParams();
        layout.width = videoWidth;
        layout.height = videoHeight;
        layout.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.removeRule(RelativeLayout.CENTER_IN_PARENT);
        if (topMargin >= 0) {
            layout.topMargin = topMargin;
        }
        if (leftMargin >= 0) {
            layout.leftMargin = leftMargin;
        }
        videoItemToMany.parent.setLayoutParams(layout);

        //视频框
        RelativeLayout.LayoutParams linparam = (RelativeLayout.LayoutParams) videoItemToMany.rel_video_label.getLayoutParams();
        linparam.width = videoWidth;
        linparam.height = videoHeight;
        videoItemToMany.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_video_layoutParams = (RelativeLayout.LayoutParams) videoItemToMany.sf_video.getLayoutParams();
        sf_video_layoutParams.width = videoWidth;
        sf_video_layoutParams.height = videoHeight;
        videoItemToMany.sf_video.setLayoutParams(sf_video_layoutParams);

        RelativeLayout.LayoutParams bgVideoBackLayoutParams = (RelativeLayout.LayoutParams) videoItemToMany.bg_video_back.getLayoutParams();
        bgVideoBackLayoutParams.width = videoWidth;
        bgVideoBackLayoutParams.height = videoHeight;
        videoItemToMany.bg_video_back.setLayoutParams(bgVideoBackLayoutParams);

        RelativeLayout.LayoutParams imgVideoBackLayoutParams = (RelativeLayout.LayoutParams) videoItemToMany.img_video_back.getLayoutParams();
        imgVideoBackLayoutParams.width = videoWidth;
        imgVideoBackLayoutParams.height = videoHeight;
        videoItemToMany.img_video_back.setLayoutParams(imgVideoBackLayoutParams);

        //底部布局
        RelativeLayout.LayoutParams stu_name = (RelativeLayout.LayoutParams) videoItemToMany.lin_name_label.getLayoutParams();
        stu_name.width = videoWidth;
        stu_name.height = 40;
        videoItemToMany.lin_name_label.setLayoutParams(stu_name);

        //话筒图标
        LinearLayout.LayoutParams imc1 = (LinearLayout.LayoutParams) videoItemToMany.img_mic.getLayoutParams();
        imc1.height = stu_name.height;
        imc1.width = stu_name.height;
        videoItemToMany.img_mic.setLayoutParams(imc1);

        //音量进度条
        LinearLayout.LayoutParams volume1 = (LinearLayout.LayoutParams) videoItemToMany.volume.getLayoutParams();
        volume1.height = stu_name.height;
        volume1.width = stu_name.height * 55 / 40;
        videoItemToMany.volume.setLayoutParams(volume1);

        //画笔图标
        LinearLayout.LayoutParams bg_img_pen_params = (LinearLayout.LayoutParams) videoItemToMany.bg_img_pen.getLayoutParams();
        bg_img_pen_params.height = stu_name.height;
        bg_img_pen_params.width = stu_name.height;
        videoItemToMany.bg_img_pen.setLayoutParams(bg_img_pen_params);
        RelativeLayout.LayoutParams img_pen_params = (RelativeLayout.LayoutParams) videoItemToMany.img_pen.getLayoutParams();
        img_pen_params.height = (int) (bg_img_pen_params.height * ((double) 7 / (double) 10));
        img_pen_params.width = (int) (bg_img_pen_params.height * ((double) 7 / (double) 10));
        videoItemToMany.img_pen.setLayoutParams(img_pen_params);

        //教室用户昵称
        LinearLayout.LayoutParams txt_name_par = (LinearLayout.LayoutParams) videoItemToMany.txt_name.getLayoutParams();
        txt_name_par.height = LinearLayout.LayoutParams.MATCH_PARENT;
        txt_name_par.leftMargin = 4;
        txt_name_par.gravity = Gravity.CENTER_VERTICAL;
        videoItemToMany.txt_name.setLayoutParams(txt_name_par);

        //举手图标
        LinearLayout.LayoutParams img_hand = (LinearLayout.LayoutParams) videoItemToMany.img_hand.getLayoutParams();
        img_hand.height = stu_name.height;
        img_hand.width = stu_name.height;
        videoItemToMany.img_hand.setLayoutParams(img_hand);

        //奖杯数量
        FrameLayout.LayoutParams txt_gift_par = (FrameLayout.LayoutParams) videoItemToMany.txt_gift_num.getLayoutParams();
        txt_gift_par.height = (int) (stu_name.height / 4 * 3);
        videoItemToMany.txt_gift_num.setLayoutParams(txt_gift_par);
        videoItemToMany.txt_gift_num.setPadding(stu_name.height + 5, 0, (int) stu_name.height / 3, 0);

        //奖杯图标
        FrameLayout.LayoutParams icon_gif_par = (FrameLayout.LayoutParams) videoItemToMany.icon_gif.getLayoutParams();
        icon_gif_par.height = stu_name.height;
        icon_gif_par.width = stu_name.height;
        videoItemToMany.icon_gif.setLayoutParams(icon_gif_par);
    }
}
