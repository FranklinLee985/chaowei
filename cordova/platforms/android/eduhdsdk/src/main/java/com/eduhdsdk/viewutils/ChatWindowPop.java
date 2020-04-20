package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.ChatListAdapter;
import com.eduhdsdk.entity.ChatData;
import com.eduhdsdk.interfaces.TranslateCallback;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.tools.SoftKeyBoardListener;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.tools.Translate;
import com.eduhdsdk.ui.BasePopupWindow;
import com.talkcloud.room.TKRoomManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/23/023.
 */

public class ChatWindowPop implements TranslateCallback {

    private Activity activity;
    private ChatPopupWindowClick chatPopupWindowClick;
    private PopupWindow popupWindow;
    private List<String> staticFacesList;
    private ChatListAdapter chlistAdapter;
    private ListView lv_chat;


    public ChatWindowPop(Activity activity, ArrayList<ChatData> chatList) {
        this.activity = activity;
        chlistAdapter = new ChatListAdapter(chatList, activity);
        initStaticFaces();
        Translate.getInstance().setCallback(this);
    }

    private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            staticFacesList.clear();
            String[] faces = activity.getAssets().list("face");
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setChatPopupWindowClick(ChatWindowPop.ChatPopupWindowClick chatPopupWindowClick) {
        this.chatPopupWindowClick = chatPopupWindowClick;
    }

    public ChatListAdapter getChatListAdapter() {
        return this.chlistAdapter;
    }

    //判断弹框弹出时，用户的点击是在底部控件的内部还是外部
    boolean isInView = true;

    public void showChatPopupWindow(final int width, final int height, final View view, final View cb_view, int leftMargin) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.tk_layout_chat_pop, null);
        ScreenScale.scaleView(contentView, "ChatWindowPop");

        popupWindow = new BasePopupWindow(activity);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);

        lv_chat = (ListView) contentView.findViewById(R.id.lv_chat);

        lv_chat.setAdapter(chlistAdapter);
        lv_chat.setSelection(chlistAdapter.getCount() - 1);

        popupWindow.setContentView(contentView);

        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);

        popupWindow.setAnimationStyle(R.style.chat_pop_anim);

        int[] reb_wb_board = new int[2];
        view.getLocationOnScreen(reb_wb_board);

        //popupwindow基于屏幕左下角给定view的偏移量
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, leftMargin, reb_wb_board[1] - height);

        if (TKRoomManager.getInstance().getMySelf().role != -1) {
            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isInView = Tools.isInView(event, cb_view);
                    return false;
                }
            });
        }
    }

    public boolean popupIsShow() {
        if (popupWindow == null) {
            return false;
        } else {
            return popupWindow.isShowing();
        }
    }

    public void dismissPopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onResult(final int index, final String result) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (RoomSession.chatList.size() > index) {
                    RoomSession.chatList.get(index).setTrans(true);
                    RoomSession.chatList.get(index).setTrans(result);
                    chlistAdapter.setArrayList(RoomSession.chatList);
                    lv_chat.setSelection(index);
                }
            }
        });
    }

    /**
     * 定义popupwindow的接口，通过接口和activity进行通信
     */
    public interface ChatPopupWindowClick {
        void close_chat_window();
    }
}
