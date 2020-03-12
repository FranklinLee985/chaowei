package com.classroomsdk.common;

import android.text.TextUtils;

public class RoomControler {

    public static String chairmanControl = "1111111001101111101000010000000000000000";

    /***
     * 是否自动下课
     */
    // && TextUtils.isEmpty(TKRoomManager.recordfilepath)
    public static boolean isAutoClassDissMiss() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 7) {
            return chairmanControl.charAt(7) == '1';
        } else {
            return false;
        }
    }

    /***
     * 上课后是否自动上台
     */
    public static boolean isAutomaticUp() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 23) {
            return chairmanControl.charAt(23) == '1';
        } else {
            return false;
        }
    }

    /***
     * 是否自动上课
     */
    public static boolean isAutoClassBegin() {

        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 32) {
            return chairmanControl.charAt(32) == '1';
        } else {
            return false;
        }
    }

    /***
     * 允许学生自己控制音视频
     */
    public static boolean isAllowStudentControlAV() {

        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 33) {
            return chairmanControl.charAt(33) == '1';
        } else {
            return false;
        }
    }

    /***
     * 不显示上下课按钮
     */
    public static boolean isShowClassBeginButton() {

        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 34) {
            return chairmanControl.charAt(34) == '1';
        } else {
            return false;
        }
    }

    /***
     * 是否有画笔权限
     * @return
     */
    public static boolean isAutoHasDraw() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 37) {
            return chairmanControl.charAt(37) == '1';
        } else {
            return false;
        }
    }

    /***
     * 是否允许助教开启音视频
     */
    public static boolean isShowAssistantAV() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 36) {
            return chairmanControl.charAt(36) == '1';
        } else {
            return false;
        }
    }

    /***
     * 学生是否可以翻页
     * @return
     */
    public static boolean isStudentCanTurnPage() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 38) {
            return chairmanControl.charAt(38) == '1';
        } else {
            return false;
        }
    }

    /***
     * 上课前是否发布音视频
     */
    public static boolean isReleasedBeforeClass() {

        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 41) {
            return chairmanControl.charAt(41) == '1';
        } else {
            return false;
        }
    }

    /***
     * 下课后是否不离开课堂
     */
    public static boolean isNotLeaveAfterClass() {

        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 47) {
            return chairmanControl.charAt(47) == '1';
        } else {
            return false;
        }
    }

    /***
     * 是否有视频标注
     */
    public static boolean isShowVideoWhiteBoard() {

        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 48) {
            return chairmanControl.charAt(48) == '1';
        } else {
            return false;
        }
    }

    /***
     * 是否不自动关闭视频播放器
     */
    //&& TextUtils.isEmpty(TKRoomManager.recordfilepath)
    public static boolean isNotCloseVideoPlayer() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 52) {
            return chairmanControl.charAt(52) == '1';
        } else {
            return false;
        }
    }

    /***
     * 是否有文档分类
     */
    public static boolean isDocumentClassification() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 56) {
            return chairmanControl.charAt(56) == '1';
        } else {
            return false;
        }
    }

    /***
     * 是否有时间节点退出教室
     */
    public static boolean haveTimeQuitClassroomAfterClass() {

        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 71) {
            return chairmanControl.charAt(71) == '1';
        } else {
            return false;
        }
    }

    /***
     * 是否自定义奖杯
     */
    public static boolean isCustomTrophy() {

        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 44) {
            return chairmanControl.charAt(44) == '1';
        } else {
            return false;
        }
    }

    /***
     * 是否自定义白板底色
     */
    public static boolean isCustomizeWhiteboard() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 81) {
            return chairmanControl.charAt(81) == '1';
        } else {
            return false;
        }
    }

    /***
     *
     * @return 巡课取消点击下课按钮
     */
    public static boolean patrollerCanClassDismiss() {
        if (chairmanControl != null && chairmanControl.length() > 78) {
            return chairmanControl.charAt(78) == '1';
        } else {
            return false;
        }
    }

    /***
     *
     * @return 课件全屏同步 （课件或MP4全屏后video放置右下角(画中画效果)）
     */
    public static boolean isFullScreenVideo() {
        if (chairmanControl != null && chairmanControl.length() > 50) {
            return chairmanControl.charAt(50) == '1';
        } else {
            return false;
        }
    }

    /***
     *  学生视频顺序统一 （ture 有顺序）
     */
    public static boolean isStudentVideoSequence() {

        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 116) {
            return chairmanControl.charAt(116) == '1';
        } else {
            return false;
        }
    }

    /***
     *   只显示老师和自己视频 （true是）
     */
    public static boolean isOnlyShowTeachersAndVideos() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 119) {
            return chairmanControl.charAt(119) == '1';
        } else {
            return false;
        }
    }

    /***
     *   是否是中日翻译（true是）
     */
    public static boolean isChineseJapaneseTranslation() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 122) {
            return chairmanControl.charAt(122) == '1';
        } else {
            return false;
        }
    }


    /***
     *   是否拥有答题器
     */
    public static boolean isHasAnswerMachine() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 61) {
            return chairmanControl.charAt(61) == '1';
        } else {
            return false;
        }
    }

    /***
     *   是否拥有转盘
     */
    public static boolean isHasTurntable() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 62) {
            return chairmanControl.charAt(62) == '1';
        } else {
            return false;
        }
    }

    /***
     *   是否拥有计时器
     */
    public static boolean isHasTimer() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 63) {
            return chairmanControl.charAt(63) == '1';
        } else {
            return false;
        }
    }

    /***
     *   是否拥有抢答器
     */
    public static boolean isHasResponderAnswer() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 64) {
            return chairmanControl.charAt(64) == '1';
        } else {
            return false;
        }
    }

    /***
     *   是否拥有小白板
     */
    public static boolean isHasWhiteBoard() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 65) {
            return chairmanControl.charAt(65) == '1';
        } else {
            return false;
        }
    }

    /***
     *   是否显示撤回、恢复、删除
     */
    public static boolean isShowDeletion() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 68) {
            return chairmanControl.charAt(68) == '1';
        } else {
            return false;
        }
    }

    /***
     *   是否显示课件备注
     */
    public static boolean isShowCoursewareRemarks() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 43) {
            return chairmanControl.charAt(43) == '1';
        } else {
            return false;
        }
    }

    /***
     *   不显示字体字号  （开启后画笔栏不可选择字体、字号，仅可选择颜色）
     */
    public static boolean isShowFontSize() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 115) {
            return chairmanControl.charAt(115) == '1';
        } else {
            return false;
        }
    }

    /***
     *   开启配置项后，在白板进行涂鸦的用户会在右下角显示用户昵称3秒，
     *   3秒后昵称消失。且自己无法看到自己的昵称显示，仅显示其他用户昵称
     */
    public static boolean isShowWriteUpTheName() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 58) {
            return chairmanControl.charAt(58) == '1';
        } else {
            return false;
        }
    }

    /***
     *   开启后画笔栏不显示形状工具
     */
    public static boolean isHiddenShapeTool() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 114) {
            return chairmanControl.charAt(114) == '1';
        } else {
            return false;
        }
    }

    /***
     *   涂鸦工具隐藏鼠标：开启后画笔栏不显示鼠标
     */
    public static boolean isHiddenMouse() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 113) {
            return chairmanControl.charAt(113) == '1';
        } else {
            return false;
        }
    }

    /***
     *  动态PPT隐藏翻页按钮：开启后在播放动态PPT时，学生隐藏翻页按钮
     * @return
     */
    public static boolean isHiddenPageFlipButton() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 112) {
            return chairmanControl.charAt(112) == '1';
        } else {
            return false;
        }
    }


    /***
     *   答题卡结束展示答案(答题结束是否显示公布答案按钮)
     */
    public static boolean isShowEndOfTheAnswerCard() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 42) {
            return chairmanControl.charAt(42) == '1';
        } else {
            return false;
        }
    }

    /***
     *   是否有课件备注
     */
    public static boolean isHasCoursewareNotes() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 43) {
            return chairmanControl.charAt(43) == '1';
        } else {
            return false;
        }
    }

    /***
     * 选中小箭头时是否隐藏画笔  true：不隐藏  fales:隐藏  （ 画笔穿透）
     */
    public static boolean isHideDrawPath() {
        if (!TextUtils.isEmpty(chairmanControl) && chairmanControl.length() > 131) {
            return chairmanControl.charAt(131) == '1';
        } else {
            return false;
        }
    }
}
