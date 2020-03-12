package com.eduhdsdk.viewutils;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eduhdsdk.R;
import com.eduhdsdk.comparator.PeerIDComparator;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.ui.holder.OneToManyRootHolder;
import com.eduhdsdk.ui.holder.VideoItemToMany;
import com.talkcloud.room.TKRoomManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OnetoManyLayoutUtil {


    /**
     * 主讲视频 隐藏白板区域，放大视频区域
     *
     * @param mRootHolder
     */
    public static void hideView(OneToManyRootHolder mRootHolder, int screenValueWidth, int screenValueHeight) {
        if (mRootHolder != null) {
            //隐藏白板
//            RelativeLayout.LayoutParams wbParams = (RelativeLayout.LayoutParams) mRootHolder.rel_wb_container.getLayoutParams();
//            wbParams.height = 0;
//            wbParams.width = 0;
//            mRootHolder.rel_wb_container.setLayoutParams(wbParams);
//            mRootHolder.rel_wb.setLayoutParams(wbParams);
//            mRootHolder.v_students.setLayoutParams(wbParams);

            mRootHolder.rel_wb_container.setVisibility(View.INVISIBLE);
            mRootHolder.rel_wb.setVisibility(View.INVISIBLE);
            mRootHolder.v_students.setVisibility(View.INVISIBLE);
            //占位图
            mRootHolder.speak_rl_zw.setVisibility(View.GONE);
            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                //隐藏课件库
                mRootHolder.cb_file_person_media_list.setVisibility(View.GONE);
                //隐藏工具箱
                mRootHolder.cb_tool_case.setVisibility(View.GONE);
            } else if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
                mRootHolder.cb_choose_photo.setVisibility(View.GONE);
            }

            //视频区域
            RelativeLayout.LayoutParams videoParams = (RelativeLayout.LayoutParams) mRootHolder.rel_students.getLayoutParams();
            videoParams.width = screenValueWidth;
            videoParams.height = screenValueHeight;
            mRootHolder.rel_students.setLayoutParams(videoParams);

            mRootHolder.v_students.setVisibility(View.GONE);

        }
    }

    /**
     * 主讲视频 隐藏白板区域，放大视频区域
     *
     * @param mRootHolder
     */
    public static void ShowView(OneToManyRootHolder mRootHolder) {
        if (mRootHolder != null) {
            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                //显示课件库
                mRootHolder.cb_file_person_media_list.setVisibility(View.VISIBLE);
                if (RoomSession.isClassBegin) {
                    //显示工具箱
                    mRootHolder.cb_tool_case.setVisibility(View.VISIBLE);
                }
            } else if (TKRoomManager.getInstance().getMySelf().role == 2) {
                if (TKRoomManager.getInstance().getMySelf().canDraw) {
                    mRootHolder.cb_choose_photo.setVisibility(View.VISIBLE);
                }
            }

            mRootHolder.speak_rl_zw.setVisibility(View.GONE);
            mRootHolder.rel_wb_container.setVisibility(View.VISIBLE);
            mRootHolder.rel_wb.setVisibility(View.VISIBLE);
            mRootHolder.v_students.setVisibility(View.VISIBLE);

            //视频区域
//            RelativeLayout.LayoutParams videoParams = (RelativeLayout.LayoutParams) mRootHolder.rel_students.getLayoutParams();
//            videoParams.width = screenValueWidth;
//            videoParams.height = screenValueHeight;
//            mRootHolder.rel_students.setLayoutParams(videoParams);

        }
    }


    /**
     * 常规布局
     *
     * @param notMoveVideoItems 上台未移动视频
     * @param mScreenValueWidth 屏幕宽
     * @param wid_ratio         视频比例
     * @param hid_ratio
     */
    public static void routineDoLayout(ArrayList<VideoItemToMany> notMoveVideoItems, int mScreenValueWidth, int wid_ratio, int hid_ratio) {
        boolean isContainTeacher = false;
        //循环 判断未移动的列表中是否有老师
        for (int i = 0; i < notMoveVideoItems.size(); i++) {
            if (notMoveVideoItems.get(i).role == 0) {
                isContainTeacher = true;
                break;
            }
        }

        //按peerid的升序排序视频列表
        if (RoomControler.isStudentVideoSequence()) {
            getSortPlayingList(notMoveVideoItems);
        }

        //老师视频的宽
        int videoWidth = 0;
        //老师视频的高
        int videoHeight = 0;

        //学生视频宽
        int stuVideoWidth = 0;
        //学生视频高
        int stuvideoHeight = 0;

        int mDutyNumPopuper = notMoveVideoItems.size();
        if (mDutyNumPopuper == 0) {
            return;
        }

        //老师在台上并且未移动
        if (isContainTeacher) {
            //(屏幕宽 - 8条边框宽) / 7路
            videoWidth = (mScreenValueWidth - 8 * 8) / 7;
            videoHeight = videoWidth * hid_ratio / wid_ratio;

            //如果上台人数小于7位(包含老师)
            if (mDutyNumPopuper <= 7) {
                stuVideoWidth = videoWidth;
                stuvideoHeight = videoHeight;
                //如果上台人数大于7路 并且小于13路 （包含老师）
            } else if (mDutyNumPopuper > 7 && mDutyNumPopuper <= 12) {
                //((屏幕宽 - 老师视频宽 - 老师边框宽) - (边框宽 * 边框条数) ) / 学生人数 = 学生视频宽
                stuVideoWidth = (mScreenValueWidth - videoWidth - (8 * (mDutyNumPopuper + 1))) / (mDutyNumPopuper - 1);
                stuvideoHeight = videoHeight;
                //如果上台人数大于13路 小于25路 （包含老师）
            } else if (mDutyNumPopuper > 12 && mDutyNumPopuper <= 25) {
                //(屏幕宽 - 老师视频宽 - 所有边框宽) / 学生人数
                stuVideoWidth = (mScreenValueWidth - videoWidth - (8 * (13 + 1))) / (13 - 1);
                stuvideoHeight = videoHeight / 2 - 4;
            }
        } else {
            //如果上台人数小于7位
            if (mDutyNumPopuper <= 7) {
                //同上
                stuVideoWidth = (mScreenValueWidth - 8 * 8) / 7;
                stuvideoHeight = stuVideoWidth * hid_ratio / wid_ratio;
                //如果上台人数大于7路 并且小于14路
            } else if (mDutyNumPopuper > 7 && mDutyNumPopuper <= 13) {
                //(屏幕宽 - 边框宽) / 学生人数 = 学生视频宽
                stuVideoWidth = (mScreenValueWidth - (8 * (mDutyNumPopuper + 1))) / mDutyNumPopuper;
                //高度为7路视频的高
                stuvideoHeight = (mScreenValueWidth - 8 * 8) / 7 * hid_ratio / wid_ratio;
                //如果上台人数大于14路 小于28路 （不包含老师）
            } else if (mDutyNumPopuper > 13 && mDutyNumPopuper <= 24) {
                //（屏幕宽 - 边框宽） / 学生
                stuVideoWidth = (mScreenValueWidth - (8 * (14 + 1))) / 14;
                //7路视频的高的一半 预留4
                stuvideoHeight = (mScreenValueWidth - 8 * 8) / 7 * hid_ratio / wid_ratio / 2 - 4;
            }
        }

        //重新布局在顶部视频框
        for (int i = 0; i < notMoveVideoItems.size(); i++) {
            //老师取老师宽高，学生取学生
            boolean isTeacher = notMoveVideoItems.get(i).role == 0 ? true : false;
            int sufWidth = isTeacher ? videoWidth : stuVideoWidth;
            int sufheight = isTeacher ? videoHeight : stuvideoHeight;
            //视频框根布局
            RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).parent.getLayoutParams();
            layout.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layout.removeRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layout.width = sufWidth;
            layout.height = sufheight;
            //视频双击时会移除控件据顶部，再次布局时添加
            int leftmargin = 0;
            //边框 默认为8 除了在7路视频下计算，其他所有路视频下统一8
            int firstLeftMargin = 8;
            //是否包含老师
            if (isContainTeacher) {
                //边框
                // int firstLeftMargin = (mScreenValueWidth - 8 * (notMoveVideoItems.size() - 1) - layout.width * notMoveVideoItems.size()) / 2;
                if (mDutyNumPopuper <= 7) {
                    //距离左边
                    leftmargin = 8 * (i + 1) + (mScreenValueWidth - 8 * 8) / 7 * i;
                    firstLeftMargin = (mScreenValueWidth - 8 * (notMoveVideoItems.size() - 1) - layout.width * notMoveVideoItems.size()) / 2;
                    if (i == 0) {
                        layout.leftMargin = firstLeftMargin;
                    } else {
                        layout.leftMargin = leftmargin + firstLeftMargin - 8;
                    }
                } else if (mDutyNumPopuper > 7 && mDutyNumPopuper <= 12) {
                    //距离左边
                    leftmargin = 8 * (i + 1) + (mScreenValueWidth - videoWidth - (8 * (notMoveVideoItems.size() + 1))) / (notMoveVideoItems.size() - 1) * (i == 0 ? 0 : i - 1);
                    if (i == 0) {
                        layout.leftMargin = firstLeftMargin;
                    } else {
                        layout.leftMargin = videoWidth + leftmargin;
                    }
                } else if (mDutyNumPopuper > 12 && mDutyNumPopuper <= 25) {
                    if (i == 0) {
                        layout.leftMargin = firstLeftMargin;
                    } else if (i > 0 && i <= 12) {
                        leftmargin = 8 * (i + 1) + (mScreenValueWidth - videoWidth - (8 * (13 + 1))) / (13 - 1) * (i == 0 ? 0 : i - 1);
                        layout.leftMargin = videoWidth + leftmargin;
                    } else if (i > 12) {
                        leftmargin = 8 * (i - 12 + 1) + (mScreenValueWidth - videoWidth - (8 * (13 + 1))) / (13 - 1) * (i - 13);
                        layout.leftMargin = videoWidth + leftmargin;
                        layout.topMargin = 8 + sufheight + 4;
                    }
                }
            } else {
                //如果上台人数小于7位
                if (mDutyNumPopuper <= 7) {
                    //距离左边
                    leftmargin = 8 * (i + 1) + (mScreenValueWidth - 8 * 8) / 7 * i;
                    firstLeftMargin = (mScreenValueWidth - 8 * (notMoveVideoItems.size() - 1) - layout.width * notMoveVideoItems.size()) / 2;
                    if (i == 0) {
                        layout.leftMargin = firstLeftMargin;
                    } else {
                        layout.leftMargin = leftmargin + firstLeftMargin - 8;
                    }
                } else if (mDutyNumPopuper > 7 && mDutyNumPopuper <= 13) {
                    leftmargin = 8 * (i + 1) + (mScreenValueWidth - (8 * (notMoveVideoItems.size() + 1))) / notMoveVideoItems.size() * i;
                    if (i == 0) {
                        layout.leftMargin = firstLeftMargin;
                    } else {
                        layout.leftMargin = leftmargin;
                    }
                } else if (mDutyNumPopuper > 13 && mDutyNumPopuper <= 24) {
                    if (i == 0) {
                        layout.leftMargin = firstLeftMargin;
                    } else if (i > 0 && i <= 14) {
                        leftmargin = 8 * (i + 1) + (mScreenValueWidth - (8 * (14 + 1))) / 14 * i;
                        layout.leftMargin = leftmargin;
                    } else if (i > 14) {
                        leftmargin = 8 * (i - 14 + 1) + (mScreenValueWidth - (8 * (14 + 1))) / 14 * (i - 14);
                        layout.leftMargin = leftmargin;
                        layout.topMargin = 8 + sufheight + 4;
                    }
                }
            }

            if (isContainTeacher) {
                if (i <= 12) {
                    layout.topMargin = 8;
                }
            } else {
                if (i <= 13) {
                    layout.topMargin = 8;
                }
            }
            notMoveVideoItems.get(i).parent.setLayoutParams(layout);

            //视频框
            RelativeLayout.LayoutParams linparam = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).rel_video_label.getLayoutParams();
            linparam.width = sufWidth;
            linparam.height = sufheight;
            notMoveVideoItems.get(i).rel_video_label.setLayoutParams(linparam);

            //在视频双击时候会隐藏底部条，在回来时显示
            if (notMoveVideoItems.get(i).lin_name_label.getVisibility() == View.INVISIBLE || notMoveVideoItems.get(i).lin_name_label.getVisibility() == View.GONE) {
                notMoveVideoItems.get(i).lin_name_label.setVisibility(View.VISIBLE);
            }

            //底部布局
            RelativeLayout.LayoutParams stu_name = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).lin_name_label.getLayoutParams();
            stu_name.width = sufWidth;
            stu_name.height = (int) (sufheight * ((double) 40 / (double) 250));
            notMoveVideoItems.get(i).lin_name_label.setLayoutParams(stu_name);

            if (mDutyNumPopuper > 7 && mDutyNumPopuper <= 12) {
                notMoveVideoItems.get(i).lin_name_label.setBackgroundResource(0);
            } else {
                notMoveVideoItems.get(i).lin_name_label.setBackgroundResource(R.drawable.tk_jianbian);
            }

            //视频的 SurfaceView
            if (notMoveVideoItems.get(i).sf_video == null)
                continue;
            RelativeLayout.LayoutParams sf_video_layoutParams = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).sf_video.getLayoutParams();
            RelativeLayout.LayoutParams re_background = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).re_background.getLayoutParams();
            if (isContainTeacher) {
                if (i == 0) {
                    sf_video_layoutParams.width = sufWidth;
                    sf_video_layoutParams.height = sufheight;
                    re_background.width = sufWidth;
                    re_background.height = sufheight;
                } else {
                    sf_video_layoutParams.width = sufWidth;
                    sf_video_layoutParams.height = sufWidth / 4 * 3;
                    re_background.width = sufWidth;
                    re_background.height = sufWidth / 4 * 3;
                }

            } else {
                sf_video_layoutParams.width = sufWidth;
                sf_video_layoutParams.height = sufWidth / wid_ratio * hid_ratio;
                re_background.width = sufWidth;
                re_background.height = sufWidth / wid_ratio * hid_ratio;
            }
            sf_video_layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            notMoveVideoItems.get(i).sf_video.setLayoutParams(sf_video_layoutParams);

            RelativeLayout.LayoutParams bgVideoBackLayoutParams = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).bg_video_back.getLayoutParams();
            bgVideoBackLayoutParams.width = sf_video_layoutParams.width;
            bgVideoBackLayoutParams.height = sf_video_layoutParams.height;
            notMoveVideoItems.get(i).bg_video_back.setLayoutParams(bgVideoBackLayoutParams);

            RelativeLayout.LayoutParams imgVideoBackLayoutParams = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).img_video_back.getLayoutParams();
            imgVideoBackLayoutParams.width = sf_video_layoutParams.width;
            imgVideoBackLayoutParams.height = sf_video_layoutParams.height;
            notMoveVideoItems.get(i).img_video_back.setLayoutParams(imgVideoBackLayoutParams);

            //话筒图标
            LinearLayout.LayoutParams imc1 = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).img_mic.getLayoutParams();
            imc1.height = (int) (sufheight * ((double) 40 / (double) 250));
            imc1.width = (int) (sufheight * ((double) 40 / (double) 250));
            notMoveVideoItems.get(i).img_mic.setLayoutParams(imc1);

            //音量进度条
            LinearLayout.LayoutParams volume1 = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).volume.getLayoutParams();
            volume1.height = (int) (sufheight * ((double) 40 / (double) 250));
            volume1.width = (int) (sufheight * ((double) 55 / (double) 250));
            notMoveVideoItems.get(i).volume.setLayoutParams(volume1);

            if (notMoveVideoItems.get(i).role == 2) {
                if (isContainTeacher) {
                    if (notMoveVideoItems.size() > 13) {
                        FrameLayout.LayoutParams txt_gift_par = (FrameLayout.LayoutParams) notMoveVideoItems.get(i).txt_gift_num.getLayoutParams();
                        txt_gift_par.height = 0;
                        notMoveVideoItems.get(i).txt_gift_num.setLayoutParams(txt_gift_par);
                        notMoveVideoItems.get(i).txt_gift_num.setPadding((int) (sf_video_layoutParams.height * ((double) 40 / (double) 250)) + 5, 0, (int) (linparam.height * ((double) 40 / (double) 250)) / 3, 0);

                        FrameLayout.LayoutParams icon_gif_par = (FrameLayout.LayoutParams) notMoveVideoItems.get(i).icon_gif.getLayoutParams();
                        icon_gif_par.height = 0;
                        icon_gif_par.width = 0;
                        notMoveVideoItems.get(i).icon_gif.setLayoutParams(icon_gif_par);

                        //话筒图标
                        LinearLayout.LayoutParams imc = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).img_mic.getLayoutParams();
                        imc.height = 0;
                        imc.width = 0;
                        notMoveVideoItems.get(i).img_mic.setLayoutParams(imc);

                        //音量进度条
                        LinearLayout.LayoutParams volume = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).volume.getLayoutParams();
                        volume.height = 0;
                        volume.width = 0;
                        notMoveVideoItems.get(i).volume.setLayoutParams(volume);
                    } else {
                        FrameLayout.LayoutParams txt_gift_par = (FrameLayout.LayoutParams) notMoveVideoItems.get(i).txt_gift_num.getLayoutParams();
                        txt_gift_par.height = (int) (sf_video_layoutParams.height * ((double) 40 / (double) 250) / 4 * 3);
                        notMoveVideoItems.get(i).txt_gift_num.setLayoutParams(txt_gift_par);
                        notMoveVideoItems.get(i).txt_gift_num.setPadding((int) (sf_video_layoutParams.height * ((double) 40 / (double) 250)) + 5, 0, (int) (linparam.height * ((double) 40 / (double) 250)) / 3, 0);

                        FrameLayout.LayoutParams icon_gif_par = (FrameLayout.LayoutParams) notMoveVideoItems.get(i).icon_gif.getLayoutParams();
                        icon_gif_par.height = (int) (sf_video_layoutParams.height * ((double) 40 / (double) 250));
                        icon_gif_par.width = (int) (sf_video_layoutParams.height * ((double) 40 / (double) 250));
                        notMoveVideoItems.get(i).icon_gif.setLayoutParams(icon_gif_par);

                        //话筒图标
                        LinearLayout.LayoutParams imc = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).img_mic.getLayoutParams();
                        imc.height = (int) (sufheight * ((double) 40 / (double) 250));
                        imc.width = (int) (sufheight * ((double) 40 / (double) 250));
                        notMoveVideoItems.get(i).img_mic.setLayoutParams(imc);

                        //音量进度条
                        LinearLayout.LayoutParams volume = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).volume.getLayoutParams();
                        volume.height = (int) (sufheight * ((double) 40 / (double) 250));
                        volume.width = (int) (sufheight * ((double) 55 / (double) 250));
                        notMoveVideoItems.get(i).volume.setLayoutParams(volume);
                    }
                } else {
                    if (notMoveVideoItems.size() > 12) {
                        FrameLayout.LayoutParams txt_gift_par = (FrameLayout.LayoutParams) notMoveVideoItems.get(i).txt_gift_num.getLayoutParams();
                        txt_gift_par.height = 0;
                        notMoveVideoItems.get(i).txt_gift_num.setLayoutParams(txt_gift_par);
                        notMoveVideoItems.get(i).txt_gift_num.setPadding((int) (sf_video_layoutParams.height * ((double) 40 / (double) 250)) + 5, 0, (int) (linparam.height * ((double) 40 / (double) 250)) / 3, 0);

                        FrameLayout.LayoutParams icon_gif_par = (FrameLayout.LayoutParams) notMoveVideoItems.get(i).icon_gif.getLayoutParams();
                        icon_gif_par.height = 0;
                        icon_gif_par.width = 0;
                        notMoveVideoItems.get(i).icon_gif.setLayoutParams(icon_gif_par);

                        //话筒图标
                        LinearLayout.LayoutParams imc = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).img_mic.getLayoutParams();
                        imc.height = 0;
                        imc.width = 0;
                        notMoveVideoItems.get(i).img_mic.setLayoutParams(imc);

                        //音量进度条
                        LinearLayout.LayoutParams volume = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).volume.getLayoutParams();
                        volume.height = 0;
                        volume.width = 0;
                        notMoveVideoItems.get(i).volume.setLayoutParams(volume);

                    } else {
                        FrameLayout.LayoutParams txt_gift_par = (FrameLayout.LayoutParams) notMoveVideoItems.get(i).txt_gift_num.getLayoutParams();
                        txt_gift_par.height = (int) (sf_video_layoutParams.height * ((double) 40 / (double) 250) / 4 * 3);
                        notMoveVideoItems.get(i).txt_gift_num.setLayoutParams(txt_gift_par);
                        notMoveVideoItems.get(i).txt_gift_num.setPadding((int) (sf_video_layoutParams.height * ((double) 40 / (double) 250)) + 5, 0, (int) (linparam.height * ((double) 40 / (double) 250)) / 3, 0);

                        FrameLayout.LayoutParams icon_gif_par = (FrameLayout.LayoutParams) notMoveVideoItems.get(i).icon_gif.getLayoutParams();
                        icon_gif_par.height = (int) (sf_video_layoutParams.height * ((double) 40 / (double) 250));
                        icon_gif_par.width = (int) (sf_video_layoutParams.height * ((double) 40 / (double) 250));
                        notMoveVideoItems.get(i).icon_gif.setLayoutParams(icon_gif_par);

                        //话筒图标
                        LinearLayout.LayoutParams imc = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).img_mic.getLayoutParams();
                        imc.height = (int) (sufheight * ((double) 40 / (double) 250));
                        imc.width = (int) (sufheight * ((double) 40 / (double) 250));
                        notMoveVideoItems.get(i).img_mic.setLayoutParams(imc);

                        //音量进度条
                        LinearLayout.LayoutParams volume = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).volume.getLayoutParams();
                        volume.height = (int) (sufheight * ((double) 40 / (double) 250));
                        volume.width = (int) (sufheight * ((double) 55 / (double) 250));
                        notMoveVideoItems.get(i).volume.setLayoutParams(volume);
                    }
                }
            }

            //画笔背景图标
            LinearLayout.LayoutParams bg_img_pen_params = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).bg_img_pen.getLayoutParams();
            bg_img_pen_params.height = (int) (sf_video_layoutParams.height * ((double) 40 / (double) 250));
            bg_img_pen_params.width = (int) (sf_video_layoutParams.height * ((double) 40 / (double) 250));
            notMoveVideoItems.get(i).bg_img_pen.setLayoutParams(bg_img_pen_params);
            //画笔图标
            RelativeLayout.LayoutParams img_pen_params = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).img_pen.getLayoutParams();
            img_pen_params.height = (int) (bg_img_pen_params.height * ((double) 7 / (double) 10));
            img_pen_params.width = (int) (bg_img_pen_params.height * ((double) 7 / (double) 10));
            //img_pen_params.setMargins(volume.width, 0, 0, 0);
            notMoveVideoItems.get(i).img_pen.setLayoutParams(img_pen_params);

            //教室用户昵称
            LinearLayout.LayoutParams txt_name_par = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).txt_name.getLayoutParams();
            txt_name_par.height = LinearLayout.LayoutParams.MATCH_PARENT;
            txt_name_par.leftMargin = 4;
            txt_name_par.gravity = Gravity.CENTER_VERTICAL;
            notMoveVideoItems.get(i).txt_name.setLayoutParams(txt_name_par);

            LinearLayout.LayoutParams img_hand = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).img_hand.getLayoutParams();
            img_hand.height = (int) (sf_video_layoutParams.height * ((double) 40 / (double) 250));
            img_hand.width = (int) (sf_video_layoutParams.height * ((double) 40 / (double) 250));
            notMoveVideoItems.get(i).img_hand.setLayoutParams(img_hand);
        }
    }

    /**
     * 主讲视频
     */
    public static void speakVideoDoLayout(ArrayList<VideoItemToMany> notMoveVideoItems, int mScreenValueWidth, int mScreenvalueHighter, int wid_ratio, int hid_ratio, OneToManyRootHolder mRootHolder, int printWidth, int printHeight, String pID) {
        if (notMoveVideoItems == null) {
            return;
        }
        //是否包含老师
        boolean isContainTeacher = false;
        //主讲是否包含上台人员id
        boolean isContainPid = false;
        //循环 判断未移动的列表中是否有老师
        for (int i = 0; i < notMoveVideoItems.size(); i++) {
            if (notMoveVideoItems.get(i).role == 0) {
                isContainTeacher = true;
            }
            if (notMoveVideoItems.get(i).peerid.equals(pID) || pID.equals("only")) {
                isContainPid = true;
            }
        }

        //按peerid的升序排序视频列表
        if (RoomControler.isStudentVideoSequence()) {
            getSortPlayingList(notMoveVideoItems);
            for (int x = 0; x < notMoveVideoItems.size(); x++) {
                if (notMoveVideoItems.get(x).peerid.equals(pID)) {
                    Collections.swap(notMoveVideoItems, 0, x);
                }
            }
        }

        //主视频区域宽
        int mainvideowidth = mScreenValueWidth * 2 / 3;
        //次视频区域宽
        int secondvideowidth = mScreenValueWidth - mainvideowidth;
        //主视频video宽
        int itemmainvideowidth = mainvideowidth - 16;
        //主视频video高
        int itemmainvideohight = itemmainvideowidth * hid_ratio / wid_ratio;
        int itemmainLeft = 8;
        int itemmainTop = 0;

        if (itemmainvideohight > mScreenvalueHighter) {
            itemmainvideohight = mScreenvalueHighter - 16;
            itemmainvideowidth = itemmainvideohight * wid_ratio / hid_ratio;
            itemmainLeft = (mainvideowidth - itemmainvideowidth) / 2;
            itemmainTop = (mScreenvalueHighter - itemmainvideohight) / 2;
        } else {
            itemmainTop = (mScreenvalueHighter - itemmainvideohight) / 2;
        }

        //次视频video宽
        int itemsecondwidth;
        //次视频video高
        int itemsecondhight;
        //几列
        int num = 1;

        int studentCount = notMoveVideoItems.size();
        if (isContainTeacher && isContainPid) {
            studentCount = notMoveVideoItems.size() - 1;
        }

        while (true) {
            itemsecondwidth = (secondvideowidth - 8 * num) / num;
            itemsecondhight = itemsecondwidth * hid_ratio / wid_ratio;
            int itemTotal = (itemsecondhight * studentCount + (studentCount * 8) + 8) / num;
            if ((notMoveVideoItems.size() - 1) % 2 != 0 || !isContainPid) {
                itemTotal = itemTotal + (itemsecondhight / 2) + 4;
            }
            if (itemTotal < mScreenvalueHighter) {
                break;
            }
            num++;
        }

        //如果有老师
        if (isContainTeacher && isContainPid) {
            mRootHolder.speak_rl_zw.setVisibility(View.GONE);
            for (int i = 0; i < notMoveVideoItems.size(); i++) {
                int sufWidth = i == 0 ? itemmainvideowidth : itemsecondwidth;
                int sufheight = i == 0 ? itemmainvideohight : itemsecondhight;
                //视频框根布局
                RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).parent.getLayoutParams();
                layout.height = sufheight;
                layout.width = sufWidth;
                layout.removeRule(RelativeLayout.CENTER_IN_PARENT);
                layout.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layout.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                if (i == 0) {
                    if (notMoveVideoItems.size() == 1) {
                        /*layout.leftMargin = (mScreenValueWidth - mainvideowidth) / 2;*/
                        layout.leftMargin = 0;
                        layout.topMargin = 0;
                        layout.addRule(RelativeLayout.CENTER_IN_PARENT);
                    } else {
                        layout.leftMargin = itemmainLeft;
                        layout.topMargin = itemmainTop;
                    }
                } else {
                    //商
                    int c = i / num;
                    //余
                    int a = i % num;
                    //距离左边
                    int left = mainvideowidth + (sufWidth + 8) * (a - 1);
                    int top = 8 + (sufheight + 8) * c;
                    if (a == 0) {
                        a = num;
                        left = mainvideowidth + (sufWidth + 8) * (a - 1);
                        top = 8 + (sufheight + 8) * (c - 1);
                    }
                    layout.leftMargin = left;
                    layout.topMargin = top;
                }
                notMoveVideoItems.get(i).parent.setLayoutParams(layout);
                //布局
                videoLayout(notMoveVideoItems.get(i), sufWidth, sufheight, printWidth, printHeight);
            }
        } else {
            //占位图
            mRootHolder.speak_rl_zw.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams speakImg = (RelativeLayout.LayoutParams) mRootHolder.speak_rl_zw.getLayoutParams();
            speakImg.height = itemmainvideohight;
            speakImg.width = itemmainvideowidth;
            if (notMoveVideoItems.size() == 0) {
                speakImg.leftMargin = (mScreenValueWidth - mainvideowidth) / 2;
            } else {
                speakImg.leftMargin = itemmainLeft;
            }
            speakImg.topMargin = itemmainTop;
            mRootHolder.speak_rl_zw.setLayoutParams(speakImg);

            for (int i = 1; i <= notMoveVideoItems.size(); i++) {
                int sufWidth = itemsecondwidth;
                int sufheight = itemsecondhight;
                //视频框根布局
                RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i - 1).parent.getLayoutParams();
                layout.width = sufWidth;
                layout.height = sufheight;
                //商
                int c = i / num;
                //余
                int a = i % num;
                //距离左边
                int left = mainvideowidth + (sufWidth + 8) * (a - 1);
                int top = 8 + (sufheight + 8) * c;
                if (a == 0) {
                    a = num;
                    left = mainvideowidth + (sufWidth + 8) * (a - 1);
                    top = 8 + (sufheight + 8) * (c - 1);
                }
                layout.leftMargin = left;
                layout.topMargin = top;
                layout.removeRule(RelativeLayout.CENTER_IN_PARENT);
                layout.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                notMoveVideoItems.get(i - 1).parent.setLayoutParams(layout);
                //布局
                videoLayout(notMoveVideoItems.get(i - 1), sufWidth, sufheight, printWidth, printHeight);
            }
        }
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
     * 视频框大小调整
     *
     * @param videoItem 视频控件
     * @param sufWidth  宽
     * @param sufheight 高
     */
    public static void videoLayout(VideoItemToMany videoItem, int sufWidth, int sufheight, int printWidth, int printHeight) {

        //视频框
        RelativeLayout.LayoutParams linparam = (RelativeLayout.LayoutParams) videoItem.rel_video_label.getLayoutParams();
        linparam.width = sufWidth;
        linparam.height = sufheight;
        videoItem.rel_video_label.setLayoutParams(linparam);

        ViewGroup.LayoutParams backparm = videoItem.re_background.getLayoutParams();
        backparm.width = sufWidth;
        backparm.height = sufheight;
        videoItem.re_background.setLayoutParams(backparm);

        //视频的 SurfaceView
        RelativeLayout.LayoutParams sf_video_layoutParams = (RelativeLayout.LayoutParams) videoItem.sf_video.getLayoutParams();
        sf_video_layoutParams.width = sufWidth;
        sf_video_layoutParams.height = sufheight;
        videoItem.sf_video.setLayoutParams(sf_video_layoutParams);
        videoItem.re_background.setLayoutParams(sf_video_layoutParams);

        RelativeLayout.LayoutParams bgVideoBackLayoutParams = (RelativeLayout.LayoutParams) videoItem.bg_video_back.getLayoutParams();
        bgVideoBackLayoutParams.width = sufWidth;
        bgVideoBackLayoutParams.height = sufheight;
        videoItem.bg_video_back.setLayoutParams(bgVideoBackLayoutParams);

        RelativeLayout.LayoutParams imgVideoBackLayoutParams = (RelativeLayout.LayoutParams) videoItem.img_video_back.getLayoutParams();
        imgVideoBackLayoutParams.width = sufWidth;
        imgVideoBackLayoutParams.height = sufheight;
        videoItem.img_video_back.setLayoutParams(imgVideoBackLayoutParams);

        //以上布局使用计算大小布局  以下布局以默认视频框布局
        if (sufheight > printHeight || sufWidth > printWidth) {
            sufheight = printHeight;
        }

        //底部布局
        RelativeLayout.LayoutParams stu_name = (RelativeLayout.LayoutParams) videoItem.lin_name_label.getLayoutParams();
        stu_name.width = sf_video_layoutParams.width;
        stu_name.height = (int) (sufheight * ((double) 40 / (double) 250));
        videoItem.lin_name_label.setLayoutParams(stu_name);

        //话筒图标
        LinearLayout.LayoutParams imc1 = (LinearLayout.LayoutParams) videoItem.img_mic.getLayoutParams();
        imc1.height = (int) (sufheight * ((double) 40 / (double) 250));
        imc1.width = (int) (sufheight * ((double) 40 / (double) 250));
        videoItem.img_mic.setLayoutParams(imc1);

        //音量进度条
        LinearLayout.LayoutParams volume1 = (LinearLayout.LayoutParams) videoItem.volume.getLayoutParams();
        volume1.height = (int) (sufheight * ((double) 40 / (double) 250));
        volume1.width = (int) (sufheight * ((double) 55 / (double) 250));
        videoItem.volume.setLayoutParams(volume1);

        //奖杯数
        FrameLayout.LayoutParams txt_gift_par = (FrameLayout.LayoutParams) videoItem.txt_gift_num.getLayoutParams();
        txt_gift_par.height = (int) (sufheight * ((double) 40 / (double) 250) / 4 * 3);
        videoItem.txt_gift_num.setLayoutParams(txt_gift_par);
        videoItem.txt_gift_num.setPadding((int) (sufheight * ((double) 40 / (double) 250)) + 5, 0, (int) (sufheight * ((double) 40 / (double) 250)) / 3, 0);
        //奖杯图标
        FrameLayout.LayoutParams icon_gif_par = (FrameLayout.LayoutParams) videoItem.icon_gif.getLayoutParams();
        icon_gif_par.height = (int) (sufheight * ((double) 40 / (double) 250));
        icon_gif_par.width = (int) (sufheight * ((double) 40 / (double) 250));
        videoItem.icon_gif.setLayoutParams(icon_gif_par);

        //画笔图标
        LinearLayout.LayoutParams bg_img_pen_params = (LinearLayout.LayoutParams) videoItem.bg_img_pen.getLayoutParams();
        bg_img_pen_params.height = (int) (sufheight * ((double) 40 / (double) 250));
        bg_img_pen_params.width = (int) (sufheight * ((double) 40 / (double) 250));
        videoItem.bg_img_pen.setLayoutParams(bg_img_pen_params);
        RelativeLayout.LayoutParams img_pen_params = (RelativeLayout.LayoutParams) videoItem.img_pen.getLayoutParams();
        img_pen_params.height = (int) (bg_img_pen_params.height * ((double) 7 / (double) 10));
        img_pen_params.width = (int) (bg_img_pen_params.width * ((double) 7 / (double) 10));
        //img_pen_params.setMargins(volume.width, 0, 0, 0);
        videoItem.img_pen.setLayoutParams(img_pen_params);

        //教室用户昵称
        LinearLayout.LayoutParams txt_name_par = (LinearLayout.LayoutParams) videoItem.txt_name.getLayoutParams();
        txt_name_par.height = (int) (sufheight * ((double) 40 / (double) 250));
        txt_name_par.leftMargin = 4;
        txt_name_par.gravity = Gravity.CENTER_VERTICAL;
        videoItem.txt_name.setLayoutParams(txt_name_par);
        //举手图标
        LinearLayout.LayoutParams img_hand = (LinearLayout.LayoutParams) videoItem.img_hand.getLayoutParams();
        img_hand.height = (int) (sufheight * ((double) 40 / (double) 250));
        img_hand.width = (int) (sufheight * ((double) 40 / (double) 250));
        videoItem.img_hand.setLayoutParams(img_hand);
    }
}
