package com.eduhdsdk.viewutils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.eduhdsdk.R;
import com.eduhdsdk.ui.holder.VideoItem;
import com.eduhdsdk.ui.holder.VideoItemToMany;

import org.tkwebrtc.RendererCommon;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/22/022.
 */

public class VideoTtemLayoutUtils {

   /* *//***
     * @param movedVideoItems
     * @param rel_wb_container     13
     *//*
    public static void screenThirteen(ArrayList<VideoItem> movedVideoItems, RelativeLayout rel_wb_container, double nameLabelHeight) {
        ArrayList<VideoItem> withOutTeacherList = new ArrayList<VideoItem>();
        VideoItem teacherItem = null;
        for (int i = 0; i < movedVideoItems.size(); i++) {
            if (movedVideoItems.get(i).role != 0) {
                withOutTeacherList.add(movedVideoItems.get(i));
            } else {
                teacherItem = movedVideoItems.get(i);
            }
        }

        if (teacherItem != null) {
           *//* LayoutSizeUilts.videoSize(teacherItem, rel_wb_container, 4, 3, nameLabelHeight);*//*
            RelativeLayout.LayoutParams teacherParam = (RelativeLayout.LayoutParams) teacherItem.parent.getLayoutParams();
            teacherParam.bottomMargin = rel_wb_container.getHeight() / 3;
            teacherParam.leftMargin = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4 * 2 + rel_wb_container.getLeft();
            teacherParam.topMargin = 0;
            teacherParam.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            teacherParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            teacherItem.parent.setTranslationX(0);
            teacherItem.parent.setTranslationY(0);
            teacherItem.parent.setLayoutParams(teacherParam);
        }

        for (int x = 0; x < withOutTeacherList.size(); x++) {
            VideoItem it = withOutTeacherList.get(x);
            if (x < 4 || x > 7) {
                *//*LayoutSizeUilts.videoSize(it, rel_wb_container, 4, 3, nameLabelHeight);*//*
            } else {
                LayoutSizeUilts.videoThirteenSize(it, rel_wb_container, 5, 3, nameLabelHeight);
            }
            RelativeLayout.LayoutParams studentParam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
            studentParam.bottomMargin = 0;
            studentParam.topMargin = 0;
            studentParam.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            studentParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (x < 4) {
                studentParam.bottomMargin = rel_wb_container.getHeight() / 3 * 2;
                studentParam.leftMargin = rel_wb_container.getLeft() + rel_wb_container.getWidth() / 4 * x;
            } else if (x > 3 && x < 8) {
                studentParam.bottomMargin = rel_wb_container.getHeight() / 3;
                if (x > 5) {
                    studentParam.leftMargin = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4 * (x - 4) + rel_wb_container.getLeft() + rel_wb_container.getWidth() / 4;
                } else {
                    studentParam.leftMargin = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4 * (x - 4) + rel_wb_container.getLeft();

                }
            } else if (x > 7) {
                studentParam.bottomMargin = 0;
                studentParam.leftMargin = rel_wb_container.getWidth() / 4 * (x - 8) + rel_wb_container.getLeft();
            }
            it.parent.setTranslationX(0);
            it.parent.setTranslationY(0);
            it.parent.setLayoutParams(studentParam);
        }
    }

    *//***
     * @param movedVideoItems
     * @param rel_wb_container    8-12
     *//*
    public static void screenEightToTwelve(ArrayList<VideoItem> movedVideoItems, RelativeLayout rel_wb_container, double nameLabelHeight) {
        for (int x = 0; x < movedVideoItems.size(); x++) {
            VideoItem it = movedVideoItems.get(x);
            if (movedVideoItems.size() < 10) {
                if (movedVideoItems.size() == 8 && x < 2) {
                    *//*LayoutSizeUilts.videoSize(it, rel_wb_container, 2, 3, nameLabelHeight);*//*
                } else {
                    LayoutSizeUilts.videoSize(it, rel_wb_container, 3, 3, nameLabelHeight);
                }
            } else if (movedVideoItems.size() == 10) {
                if (x < 6) {
                    LayoutSizeUilts.videoSize(it, rel_wb_container, 3, 3, nameLabelHeight);
                } else {
                    LayoutSizeUilts.videoSize(it, rel_wb_container, 4, 3, nameLabelHeight);
                }
            } else if (movedVideoItems.size() > 10) {
                if (x < 3 && movedVideoItems.size() == 11) {
                    LayoutSizeUilts.videoSize(it, rel_wb_container, 3, 3, nameLabelHeight);
                } else {
                    LayoutSizeUilts.videoSize(it, rel_wb_container, 4, 3, nameLabelHeight);
                }
            }

            RelativeLayout.LayoutParams studentParam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
            studentParam.bottomMargin = 0;
            studentParam.topMargin = 0;
            studentParam.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            studentParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            if (movedVideoItems.size() == 8) {
                if (x < 2) {
                    studentParam.bottomMargin = rel_wb_container.getHeight() / 3 * 2;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 2 * x + rel_wb_container.getLeft();
                } else if (x > 1 && x < 5) {
                    studentParam.bottomMargin = rel_wb_container.getHeight() / 3;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 3 * (x - 2) + rel_wb_container.getLeft();
                } else {
                    studentParam.bottomMargin = 0;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 3 * (x - 5) + rel_wb_container.getLeft();
                }
            }

            if (movedVideoItems.size() > 8 && movedVideoItems.size() < 11) {
                if (x < 3) {
                    studentParam.bottomMargin = rel_wb_container.getHeight() / 3 * 2;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 3 * x + rel_wb_container.getLeft();
                } else if (x > 2 && x < 6) {
                    studentParam.bottomMargin = rel_wb_container.getHeight() / 3;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 3 * (x - 3) + rel_wb_container.getLeft();
                } else {
                    studentParam.bottomMargin = 0;
                    if (movedVideoItems.size() == 9) {
                        studentParam.leftMargin = rel_wb_container.getWidth() / 3 * (x - 6) + rel_wb_container.getLeft();
                    } else {
                        studentParam.leftMargin = rel_wb_container.getWidth() / 4 * (x - 6) + rel_wb_container.getLeft();
                    }
                }
            }

            if (movedVideoItems.size() == 11) {
                if (x < 3) {
                    studentParam.bottomMargin = rel_wb_container.getHeight() / 3 * 2;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 3 * x + rel_wb_container.getLeft();
                } else if (x > 2 && x < 7) {
                    studentParam.bottomMargin = rel_wb_container.getHeight() / 3;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 4 * (x - 3) + rel_wb_container.getLeft();
                } else {
                    studentParam.bottomMargin = 0;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 4 * (x - 7) + rel_wb_container.getLeft();
                }
            }

            if (movedVideoItems.size() == 12) {
                if (x < 4) {
                    studentParam.bottomMargin = rel_wb_container.getHeight() / 3 * 2;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 4 * x + rel_wb_container.getLeft();
                } else if (x > 3 && x < 8) {
                    studentParam.bottomMargin = rel_wb_container.getHeight() / 3;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 4 * (x - 4) + rel_wb_container.getLeft();
                } else {
                    studentParam.bottomMargin = 0;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 4 * (x - 8) + rel_wb_container.getLeft();
                }
            }
            it.parent.setTranslationX(0);
            it.parent.setTranslationY(0);
            it.parent.setLayoutParams(studentParam);
        }
    }

    *//***
     * @param movedVideoItems
     * @param rel_wb_container    7
     *//*
    public static void screenSeven(ArrayList<VideoItem> movedVideoItems, RelativeLayout rel_wb_container, double nameLabelHeight) {
        ArrayList<VideoItem> withOutTeacherList = new ArrayList<VideoItem>();
        VideoItem teacherItem = null;
        for (int i = 0; i < movedVideoItems.size(); i++) {
            if (movedVideoItems.get(i).role != 0) {
                withOutTeacherList.add(movedVideoItems.get(i));
            } else {
                teacherItem = movedVideoItems.get(i);
            }
        }
        if (teacherItem != null) {
            LayoutSizeUilts.videoSize(teacherItem, rel_wb_container, 3, 2, nameLabelHeight);
            RelativeLayout.LayoutParams teacherParam = (RelativeLayout.LayoutParams) teacherItem.parent.getLayoutParams();
            teacherParam.bottomMargin = rel_wb_container.getHeight() / 2;
            teacherParam.leftMargin = rel_wb_container.getWidth() / 3 + rel_wb_container.getLeft();
            teacherParam.topMargin = 0;
            teacherParam.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            teacherParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            teacherItem.parent.setTranslationX(0);
            teacherItem.parent.setTranslationY(0);
            teacherItem.parent.setLayoutParams(teacherParam);
        }

        for (int x = 0; x < withOutTeacherList.size(); x++) {
            VideoItem it = withOutTeacherList.get(x);
            if (withOutTeacherList.size() == 7) {
                if (x < 3) {
                    LayoutSizeUilts.videoSize(it, rel_wb_container, 3, 2, nameLabelHeight);
                } else {
                    LayoutSizeUilts.videoSize(it, rel_wb_container, 4, 2, nameLabelHeight);
                }
            } else {
                if (x < 2) {
                    LayoutSizeUilts.videoSize(teacherItem, rel_wb_container, 3, 2, nameLabelHeight);
                } else {
                    LayoutSizeUilts.videoSize(it, rel_wb_container, 4, 2, nameLabelHeight);
                }
            }

            RelativeLayout.LayoutParams studentParam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
            studentParam.bottomMargin = 0;
            studentParam.topMargin = 0;
            studentParam.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            studentParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (x == 0) {
                studentParam.bottomMargin = rel_wb_container.getHeight() / 2;
                studentParam.leftMargin = rel_wb_container.getLeft();
            } else if (x == 1) {
                studentParam.bottomMargin = rel_wb_container.getHeight() / 2;
                if (withOutTeacherList.size() == 7) {
                    studentParam.leftMargin = rel_wb_container.getWidth() / 3 + rel_wb_container.getLeft();
                } else {
                    studentParam.leftMargin = rel_wb_container.getWidth() / 3 * 2 + rel_wb_container.getLeft();
                }
            } else if (x == 2) {
                if (withOutTeacherList.size() == 7) {
                    studentParam.bottomMargin = rel_wb_container.getHeight() / 2;
                    studentParam.leftMargin = rel_wb_container.getWidth() / 3 * 2 + rel_wb_container.getLeft();
                } else {
                    studentParam.leftMargin = rel_wb_container.getLeft();
                }
            } else if (x == 3) {
                if (withOutTeacherList.size() == 7) {
                    studentParam.leftMargin = rel_wb_container.getLeft();
                } else {
                    studentParam.leftMargin = rel_wb_container.getWidth() / 4 + rel_wb_container.getLeft();
                }
            } else if (x == 4) {
                if (withOutTeacherList.size() == 7) {
                    studentParam.leftMargin = rel_wb_container.getWidth() / 4 + rel_wb_container.getLeft();
                } else {
                    studentParam.leftMargin = rel_wb_container.getWidth() / 4 * 2 + rel_wb_container.getLeft();
                }
            } else if (x == 5) {
                if (withOutTeacherList.size() == 7) {
                    studentParam.leftMargin = rel_wb_container.getWidth() / 4 * 2 + rel_wb_container.getLeft();
                } else {
                    studentParam.leftMargin = rel_wb_container.getWidth() / 4 * 3 + rel_wb_container.getLeft();
                }
            } else if (x == 6) {
                studentParam.leftMargin = rel_wb_container.getWidth() / 4 * 3 + rel_wb_container.getLeft();
            }
            it.parent.setTranslationX(0);
            it.parent.setTranslationY(0);
            it.parent.setLayoutParams(studentParam);
        }
    }

    *//***
     * @param movedVideoItems
     * @param rel_wb_container  3到6
     *//*
    public static void screenThreeToSixth(ArrayList<VideoItem> movedVideoItems, RelativeLayout rel_wb_container, double nameLabelHeight) {
        for (int x = 0; x < movedVideoItems.size(); x++) {
            VideoItem it = movedVideoItems.get(x);
            if (movedVideoItems.size() == 3 && x == 0) {
                LayoutSizeUilts.videoSize(it, rel_wb_container, 2, 1, nameLabelHeight);
            } else {
                if (movedVideoItems.size() % 2 == 0) {
                    LayoutSizeUilts.videoSize(it, rel_wb_container, (int) movedVideoItems.size() / 2, 2, nameLabelHeight);
                } else {
                    if (movedVideoItems.size() == 5 && x < 2) {
                        LayoutSizeUilts.videoSize(it, rel_wb_container, 2, 2, nameLabelHeight);
                    } else {
                        LayoutSizeUilts.videoSize(it, rel_wb_container, (int) (movedVideoItems.size() / 2 + 1), 2, nameLabelHeight);
                    }
                }
            }
            RelativeLayout.LayoutParams studentParam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
            studentParam.bottomMargin = 0;
            studentParam.topMargin = 0;
            studentParam.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            studentParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (x >= movedVideoItems.size() / 2) {
                if (x != 1) {
                    studentParam.bottomMargin = rel_wb_container.getHeight() / 2;
                    if (movedVideoItems.size() > 4) {
                        studentParam.leftMargin = rel_wb_container.getWidth() / 3 * (x - movedVideoItems.size() / 2) + rel_wb_container.getLeft();
                    } else {
                        studentParam.leftMargin = rel_wb_container.getWidth() / 2 * (x - movedVideoItems.size() / 2) + rel_wb_container.getLeft();
                    }
                } else {
                    studentParam.leftMargin = rel_wb_container.getWidth() / 2 + rel_wb_container.getLeft();
                }
            } else {
                if (movedVideoItems.size() > 5) {
                    studentParam.leftMargin = rel_wb_container.getWidth() / 3 * x + rel_wb_container.getLeft();
                } else {
                    studentParam.leftMargin = rel_wb_container.getWidth() / 2 * x + rel_wb_container.getLeft();
                }
            }
            it.parent.setTranslationX(0);
            it.parent.setTranslationY(0);
            it.parent.setLayoutParams(studentParam);
        }
    }*/

    /***
     * @param movedVideoItems
     * @param rel_wb_container   小于3
     */
    public static void screenLessThree(ArrayList<VideoItemToMany> movedVideoItems, RelativeLayout rel_wb_container, double nameLabelHeight) {
        for (int i = 0; i < movedVideoItems.size(); i++) {
            VideoItemToMany it = movedVideoItems.get(i);
            int top = rel_wb_container.getTop();
            LayoutSizeUilts.videoSize(it, rel_wb_container, movedVideoItems.size(), 1, nameLabelHeight);
            RelativeLayout.LayoutParams studentParam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
            studentParam.bottomMargin = -top;
            studentParam.topMargin = top;
            studentParam.leftMargin = 0;
            studentParam.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            it.parent.setLayoutParams(studentParam);
            it.bg_video_back.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }
}
