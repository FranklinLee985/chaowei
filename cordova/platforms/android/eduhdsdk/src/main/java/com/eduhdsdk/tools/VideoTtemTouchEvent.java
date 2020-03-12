package com.eduhdsdk.tools;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.classroomsdk.utils.KeyBoardUtil;
import com.eduhdsdk.entity.MoveVideoInfo;
import com.eduhdsdk.message.SendingSignalling;
import com.eduhdsdk.toolcase.LayoutPopupWindow;
import com.eduhdsdk.ui.OneToManyActivity;
import com.eduhdsdk.ui.holder.VideoItemToMany;
import com.eduhdsdk.viewutils.LayoutZoomOrIn;
import com.eduhdsdk.viewutils.OneToManyFreeLayoutUtil;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2019/4/11/011.
 * 一对多 視頻的Touch事件
 */
public class VideoTtemTouchEvent {

    private static int NONE = 0;
    private static int mode = NONE; // 当前的事件
    private static int DRAG = 1; // 拖动中
    private static int ZOOM = 2; // 缩放中
    private static float beforeLenght, afterLenght; // 两触点距离
    public static int mLayoutState = LayoutPopupWindow.LAYOUT_NORMAL;

    /***
     *  事件处理
     */
    public static void eventProcess(final ArrayList<VideoItemToMany> videoItems, final VideoItemToMany videoItem,
                                    final RelativeLayout rel_students, final View v_students, final RelativeLayout rel_wb, final double printWidth,
                                    final double nameLabelHeight, final double printHeight, final Map<String, MoveVideoInfo> stuMoveInfoMap,
                                    final ArrayList<String> screenID, final Activity activity) {
        final float tabbarH = KeyBoardUtil.dp2px(activity, 40); //activity 顶部tabbar的高度
        videoItem.parent.setOnTouchListener(new View.OnTouchListener() {

            private float eventY; //触点据控件上方的y轴高度
            private float eventX;//触点据控件上方的X轴高度

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mLayoutState == LayoutPopupWindow.LAYOUT_DOUBLE || mLayoutState == LayoutPopupWindow.LAYOUT_VIDEO) {
                    return false;
                }
                if (!TKRoomManager.getInstance().getMySelf().canDraw || videoItem.isSplitScreen) {
                    return false;
                }

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mode = DRAG;
                        eventX = event.getX();
                        eventY = event.getY();
                        if (!videoItem.canMove) {
                            return false;
                        }
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        mode = ZOOM;
                        beforeLenght = Tools.spacing(event);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (mode == ZOOM) {
                            afterLenght = Tools.spacing(event);
                            float gapLenght = afterLenght - beforeLenght;
                            if (Math.abs(gapLenght) > 5f) {
                                float scale = afterLenght / beforeLenght;
                                LayoutZoomOrIn.zoomMouldVideoItem(videoItem, scale,
                                        rel_students, v_students, rel_wb);
                                beforeLenght = afterLenght;
                            }
                        } else if (mode == DRAG) {
                            if (!videoItem.canMove || (TKRoomManager.getInstance().getMySelf().role == 2 && !videoItem.isMoved)) {//能移动  学生在有画笔权限并且被老师移动到白板区，学生才能拖动。否则就return
                                return false;
                            }
                            if (event.getRawX() - eventX >= 0 &&
                                    event.getRawX() + (videoItem.parent.getWidth() - eventX) <= Tools.getScreenWidth(activity) &&
                                    event.getRawY() - eventY - tabbarH >= 0 &&
                                    event.getRawY() + (videoItem.parent.getHeight() - eventY) <= Tools.getScreenHeight(activity)) {

                                if (videoItem.parent.getWidth() < printWidth || videoItem.parent.getHeight() < printHeight) {
                                    OneToManyFreeLayoutUtil.getInstance().doLayout((int) printWidth, (int) printHeight, videoItem, -1, -1);
                                }
                                //当是学生不能把视频拉回视频区
                                if (!(TKRoomManager.getInstance().getMySelf().role == 2 &&
                                        v_students.getVisibility() == View.VISIBLE &&
                                        event.getRawY() - videoItem.parent.getHeight() <= v_students.getBottom())) {
                                    LayoutZoomOrIn.layoutVideo(videoItem, (int) (event.getRawX() - eventX),
                                            (int) (event.getRawY() - eventY - tabbarH));
                                    /*  LayoutZoomOrIn.layoutVideo(videoItem, (int) (event.getRawX() - videoItem.parent.getWidth() / 2),
                                            (int) (event.getRawY() - videoItem.parent.getHeight()));*/
                                }
                            }
                        }

                        break;

                    case MotionEvent.ACTION_UP:

                        videoItem.parent.setAlpha(1f);
                        if (mode == DRAG) {
                            if (!videoItem.canMove) {
                                return false;
                            }
                            videoItem.canMove = false;

                            if (videoItem.parent.getTop() >= rel_wb.getTop()) {

                                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                                    RoomUser roomUser = TKRoomManager.getInstance().getUser(videoItem.peerid);
                                    if (!stuMoveInfoMap.containsKey(roomUser.peerId) && !videoItem.isMoved) {
                                        if (roomUser.getPublishState() == 2) {
                                            TKRoomManager.getInstance().changeUserProperty(roomUser.peerId, "__all",
                                                    "publishstate", 3);
                                        } else if (roomUser.getPublishState() == 4) {
                                            TKRoomManager.getInstance().changeUserProperty(roomUser.peerId, "__all",
                                                    "publishstate", 1);
                                        }
                                    }
                                }
                                videoItem.isMoved = true;
                            } else {
                                videoItem.isMoved = false;
                            }

                            if (screenID.size() > 0 && !screenID.contains(videoItem.peerid) && (videoItem.isMoved || videoItem.isSplitScreen)) {
                                screenID.add(videoItem.peerid);
                            }

                            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                                SendingSignalling.getInstance().sendStudentMove(videoItems,
                                        rel_students, v_students);
                            }

                        } else if (mode == ZOOM) {
                            if (videoItem.parent.getTop() < rel_wb.getTop()) {
                                RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) videoItem.parent.getLayoutParams();
                                relparam.topMargin = rel_wb.getTop();
                                videoItem.parent.setLayoutParams(relparam);
                            }

                            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                                SendingSignalling.getInstance().sendScaleVideoItem(videoItems, true, printHeight);
                                SendingSignalling.getInstance().sendStudentMove(videoItems, rel_students, v_students);
                            }
                            videoItem.canMove = false;
                        }
                        mode = NONE;
                        ((OneToManyActivity) activity).do1vsnStudentVideoLayout();
                        break;
                }
                return true;
            }
        });
    }
}
