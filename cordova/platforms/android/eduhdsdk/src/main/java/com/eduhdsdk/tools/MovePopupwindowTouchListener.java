package com.eduhdsdk.tools;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.classroomsdk.tools.ScreenScale;
import com.classroomsdk.utils.FullScreenTools;
import com.eduhdsdk.toolcase.AnswerPopupWindow;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.eduhdsdk.toolcase.ToolsPopupWindow.TOOLS_DATIQI;
import static com.eduhdsdk.toolcase.ToolsPopupWindow.TOOLS_JISHIQI;
import static com.eduhdsdk.toolcase.ToolsPopupWindow.TOOLS_QIANGDA;
import static com.eduhdsdk.toolcase.ToolsPopupWindow.TOOLS_ZHUANPAN;

/**
 * 移动popupwindow的实现类
 * Created by Administrator on 2018/12/29 0029.
 */

public class MovePopupwindowTouchListener implements View.OnTouchListener {

    private PopupWindow popupWindow;
    private int orgX, orgY;
    private int offsetX, offsetY;
    private View rootView;
    private boolean huawei, oppo, voio;
    private Context mActivity;
    private int type = 0;
    private onMoveListener mOnMoveListener;


    public MovePopupwindowTouchListener(PopupWindow popupWindow, Context activity) {
        this.popupWindow = popupWindow;
        this.mActivity = activity;
        huawei = FullScreenTools.hasNotchInScreen(mActivity);
        oppo = FullScreenTools.hasNotchInOppo(mActivity);
        voio = FullScreenTools.hasNotchInScreenAtVoio(mActivity);
    }

    /**
     * popupwindow的依附view
     *
     * @param rootView
     */
    public void setView(View rootView) {
        this.rootView = rootView;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (TKRoomManager.getInstance().getMySelf().role == -1) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                orgX = (int) event.getX();
                orgY = (int) event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = (int) (event.getRawX() - orgX);
                offsetY = (int) (event.getRawY() - orgY);

                int popupWindowHeight = popupWindow.getHeight();
                int popupWindowWidth = popupWindow.getWidth();
                if (popupWindowHeight <= 0 || popupWindowWidth <= 0) {
                    popupWindow.getContentView().measure(0, 0);
                    popupWindowHeight = popupWindow.getContentView().getMeasuredHeight();
                    popupWindowWidth = popupWindow.getContentView().getMeasuredWidth();
                }

                if (type == TOOLS_DATIQI) {
                    popupWindowHeight += AnswerPopupWindow.getInstance().getGridHeight();
                }


                int[] location = new int[2];
                rootView.getLocationOnScreen(location);

                int topEdge = location[1];
                int bottomEdge = topEdge + rootView.getMeasuredHeight() - popupWindowHeight;
                int leftEdge = 0;
                if (huawei || oppo || voio) {
                    leftEdge = ScreenScale.getStatusBarHeight();
                }

                int rightEdge = rootView.getMeasuredWidth() - popupWindowWidth;

                if (offsetY < topEdge) {
                    offsetY = topEdge;
                }

                if (offsetY >= bottomEdge) {
                    offsetY = bottomEdge;
                }

                if (offsetX <= leftEdge) {
                    offsetX = leftEdge;
                }
                if (offsetX >= rightEdge) {
                    offsetX = rightEdge;
                }

                popupWindow.update(offsetX, offsetY, -1, -1, true);
                break;
            case MotionEvent.ACTION_UP:
                if (mOnMoveListener != null) {
                    mOnMoveListener.onMove(offsetX, offsetY);
                }
//                if (TKRoomManager.getInstance().getMySelf().role == 0) {
//                    int type = (int) v.getTag();
//                    sendXY(v, event.getRawX(), event.getRawY(), type);
//                }
                break;
        }
        return true;
    }

    public void sendXY(View view, float rawX, float rawY, int type) {
        int[] location = new int[2];
        rootView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        int pw = width - popupWindow.getWidth();
        int ph = height - popupWindow.getHeight();
        double percentLeft = (rawX - x) / pw;
        double percentTop = (rawY - y) / ph;

        try {
            JSONObject data = new JSONObject();
            data.put("percentLeft", percentLeft);
            data.put("percentTop", percentTop);
            if (type > 0) {
                if (type == TOOLS_ZHUANPAN) {
                    TKRoomManager.getInstance().pubMsg("DialDrag", "DialDrag", "__all", data.toString(), false, null, null);
                } else if (type == TOOLS_DATIQI) {
                    TKRoomManager.getInstance().pubMsg("AnswerDrag", "AnswerDrag", "__all", data.toString(), false, null, null);
                } else if (type == TOOLS_JISHIQI) {
                    TKRoomManager.getInstance().pubMsg("TimerDrag", "TimerDrag", "__all", data.toString(), false, null, null);
                } else if (type == TOOLS_QIANGDA) {
                    TKRoomManager.getInstance().pubMsg("ResponderDrag", "ResponderDrag", "__all", data.toString(), false, null, null);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setOnMoveListener(onMoveListener onMoveListener) {
        this.mOnMoveListener = onMoveListener;
    }

    public interface onMoveListener {
        void onMove(int offsetX, int offsetY);
    }
}

