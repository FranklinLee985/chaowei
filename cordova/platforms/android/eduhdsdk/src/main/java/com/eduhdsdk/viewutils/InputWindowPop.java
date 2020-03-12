package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.FaceGVAdapter;
import com.eduhdsdk.entity.ChatData;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.tools.FullScreenTools;
import com.eduhdsdk.tools.KeyBoardUtil;
import com.eduhdsdk.tools.SoftKeyBoardListener;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.ui.BasePopupWindow;
import com.eduhdsdk.ui.TKBaseActivity;
import com.talkcloud.room.TKRoomManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/4/23/023.
 */

public class InputWindowPop {

    private Activity activity;
    private PopupWindow popupWindow;
    private EditText edt_input_small_window;
    private List<String> staticFacesList;
    private ArrayList<ChatData> msgList = new ArrayList<ChatData>();
    private ArrayList<ChatData> chatList;

    //缓存聊天框中输入的内容，每次发送消息后置空
    private String edt_input_content = "";
    //默认用户可以发言
    private boolean is_forbid_chat = false;

    private View smallContentView;

    public InputWindowPop(Activity activity, ArrayList<ChatData> chatList) {
        this.activity = activity;
        this.chatList = chatList;
        initStaticFaces();

        SoftKeyBoardListener.setListener(activity, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //键盘显示
            }

            @Override
            public void keyBoardHide(int height) {
                //键盘隐藏
            }
        });
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

    //判断弹框弹出时，用户的点击是在底部控件的内部还是外部
    boolean isInView = true;

    public void showInputPopupWindow(final int width, final int height, final View view,
                                     final View cb_view, int webwidth, boolean padLeft, InputSelectImageListener listener) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.tk_layout_input_pop, null);
        ScreenScale.scaleView(contentView, "InputWindowPop");

        popupWindow = new BasePopupWindow(activity);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        if (TKRoomManager.getInstance().getMySelf().properties.containsKey("disablechat")) {
            is_forbid_chat = (boolean) TKRoomManager.getInstance().getMySelf().properties.get("disablechat");
        }

        popupWindow.setContentView(contentView);

        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int[] reb_wb_board = new int[2];
        view.getLocationInWindow(reb_wb_board);

        //popupwindow基于屏幕左上角位移到给定view中心的偏移量
        int x = 0;
        if (padLeft) {
            x = Math.abs(view.getWidth() - popupWindow.getWidth()) / 2 + FullScreenTools.getStatusBarHeight(activity) + webwidth;
        } else {
            x = Math.abs(view.getWidth() - popupWindow.getWidth()) / 2 + webwidth;
        }
        int y = Math.abs(reb_wb_board[1] + view.getHeight() / 2 - popupWindow.getHeight() / 2);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);

        if (TKRoomManager.getInstance().getMySelf().role != -1) {
            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isInView = Tools.isInView(event, cb_view);
                    return false;
                }
            });
        }

        showBoardPopupWindow(view, true, listener);
    }

    private void delete() {
        if (edt_input_small_window.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(edt_input_small_window.getText());
            int iCursorStart = Selection.getSelectionStart(edt_input_small_window.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "[em_1]";
                        ((Editable) edt_input_small_window.getText()).delete(iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        ((Editable) edt_input_small_window.getText()).delete(iCursorEnd - 1, iCursorEnd);
                    }
                } else {
                    ((Editable) edt_input_small_window.getText()).delete(iCursorStart, iCursorEnd);
                }
            }
        }
    }

    private boolean isDeletePng(int cursor) {
        String st = "[em_1]";
        String content = edt_input_small_window.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(),
                    content.length());
            String regex = "(\\[em_)\\d{1}(\\])";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    private void insert(CharSequence text) {
        if (edt_input_small_window == null) {
            return;
        }
        int iCursorStart = Selection.getSelectionStart((edt_input_small_window.getText()));
        int iCursorEnd = Selection.getSelectionEnd((edt_input_small_window.getText()));
        if (iCursorStart != iCursorEnd) {
            ((Editable) edt_input_small_window.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((edt_input_small_window.getText()));
        ((Editable) edt_input_small_window.getText()).insert(iCursor, text);
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            String[] splitText = png.split("\\.");
            String tempText = "[" + splitText[0] + "]";
            Bitmap bitmap = BitmapFactory.decodeStream(activity.getAssets().open("face/" + png));
            Drawable drawable = new BitmapDrawable(bitmap);
            drawable.setBounds(0, 0, edt_input_small_window.getLayoutParams().height / 2,
                    edt_input_small_window.getLayoutParams().height / 2);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
//            MyIm imageSpan = new MyIm(activity, bitmap);
            sb.append(tempText);
            sb.setSpan(span, sb.length() - tempText.length(), sb.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    private SpannableStringBuilder getFaceMixed(String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\[em_)\\d{1}(\\])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String png = tempText.substring("[".length(), tempText.length() - "]".length()) + ".png";
                Bitmap bitmap = BitmapFactory.decodeStream(activity.getAssets().open("face/" + png));
                Drawable drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0, 0, edt_input_small_window.getLayoutParams().height / 2,
                        edt_input_small_window.getLayoutParams().height / 2);
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
//                MyIm imageSpan = new MyIm(activity, bitmap);
                sb.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb;
    }

    public void dismissPopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private PopupWindow chat_input_popup;

    public void dismissInputPopupWindow() {
        if (chat_input_popup != null) {
            chat_input_popup.dismiss();
        }
    }

    /**
     * 显示软键盘上方一小条输入栏
     *
     * @param view
     * @param is_show_board true 显示键盘   false 显示表情
     */
    private void showBoardPopupWindow(final View view, boolean is_show_board, final InputSelectImageListener listener) {

        if (chat_input_popup != null && chat_input_popup.isShowing()) {
            chat_input_popup.dismiss();
            chat_input_popup = null;
        }

        smallContentView = LayoutInflater.from(activity).inflate(R.layout.tk_layout_chat_message_edit_input, null);
        ScreenScale.scaleView(smallContentView, "ChatInput");

        chat_input_popup = new PopupWindow(activity);
        chat_input_popup.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        chat_input_popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        chat_input_popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        TextView txt_send = (TextView) smallContentView.findViewById(R.id.txt_send);
        final ImageView iv_chat = (ImageView) smallContentView.findViewById(R.id.iv_chat);
        final ImageView iv_broad = (ImageView) smallContentView.findViewById(R.id.iv_broad);
        final ImageView iv_photo = (ImageView) smallContentView.findViewById(R.id.iv_photo);
        edt_input_small_window = (EditText) smallContentView.findViewById(R.id.edt_input_chat);
        //解决部分机型获取不到焦点问题
        edt_input_small_window.setFocusable(true);
        edt_input_small_window.setFocusableInTouchMode(true);
        if (RoomControler.isChatAllowSendImage()){
            iv_photo.setVisibility(View.VISIBLE);
        }

        final GridView chart_face_gv = (GridView) smallContentView.findViewById(R.id.chart_face_gv);

        FaceGVAdapter mGvAdapter = new FaceGVAdapter(staticFacesList, activity);
        chart_face_gv.setAdapter(mGvAdapter);
        chart_face_gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        chart_face_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position == 41) {
                        delete();
                    }
                    if (position < 8) {
                        insert(getFace(staticFacesList.get(position)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.photoClickListener(TKBaseActivity.CHAT_SELECT_IMAGE);
            }
        });


        iv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示表情的时候，设置不可获取焦点，否则会出现表情框与键盘同时存在的情况
                edt_input_small_window.setFocusable(false);
                chart_face_gv.setVisibility(View.VISIBLE);
                iv_chat.setVisibility(View.GONE);
                iv_broad.setVisibility(View.VISIBLE);
                KeyBoardUtil.hideKeyBoard(activity, edt_input_small_window);
            }
        });

        iv_broad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户想要弹出键盘的时候，让edittext继续获得焦点
                edt_input_small_window.setFocusableInTouchMode(true);
                chart_face_gv.setVisibility(View.GONE);
                iv_chat.setVisibility(View.VISIBLE);
                iv_broad.setVisibility(View.GONE);
                KeyBoardUtil.showKeyBoard(activity, edt_input_small_window);
            }
        });

        txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_forbid_chat) {
                    return;
                }
                String msg = edt_input_small_window.getText().toString().trim();

                if (msg != null && !msg.isEmpty()) {
                    boolean isSend = false;
                    if (chatList.size() > 0) {
                        msgList.clear();
                        for (int x = 0; x < chatList.size(); x++) {
                            msgList.add(chatList.get(x));
                        }
                        Collections.sort(msgList, Collections.reverseOrder());
                        for (int x = 0; x < msgList.size(); x++) {
                            if (msgList.get(x).getUser() != null) {
                                if (msgList.get(x).getUser().peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) &&
                                        msgList.get(x).getMessage() != null) {
                                    if (msgList.get(x).getMessage().equals(msg) && System.currentTimeMillis() - msgList.get(x).getMsgTime() <= 10 * 60) {
                                        isSend = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    edt_input_small_window.setText("");
                    edt_input_content = "";
                    if (isSend) {
                        Toast.makeText(activity, activity.getString(R.string.chat_hint), Toast.LENGTH_SHORT).show();
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(System.currentTimeMillis()));
                        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

                        Map<String, Object> msgMap = new HashMap<String, Object>();
                        msgMap.put("type", 0);
                        msgMap.put("time", time);
                        TKRoomManager.getInstance().sendMessage(msg, "__all", msgMap);
                    }
                    chat_input_popup.dismiss();
                }
                dismissPopupWindow();
            }
        });

        edt_input_small_window.setText(getFaceMixed(edt_input_content));
        edt_input_small_window.setSelection(edt_input_small_window.getText().length());

        chat_input_popup.setContentView(smallContentView);

        chat_input_popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        chat_input_popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        chat_input_popup.setFocusable(true);
        chat_input_popup.setOutsideTouchable(false);
        chat_input_popup.setTouchable(true);

        chat_input_popup.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        chat_input_popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                edt_input_content = edt_input_small_window.getText().toString();

                if (SoftKeyBoardListener.isSoftShowing(activity)) {
                    KeyBoardUtil.hideKeyBoard(activity, edt_input_small_window);
                }
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }

            }
        });

        if (!is_show_board) {
            //显示表情的时候，设置不可获取焦点，否则会出现表情框与键盘同时存在的情况
            edt_input_small_window.setFocusable(false);
            chart_face_gv.setVisibility(View.VISIBLE);
            iv_chat.setVisibility(View.GONE);
            iv_broad.setVisibility(View.VISIBLE);
            KeyBoardUtil.hideKeyBoard(activity, edt_input_small_window);
        } else {
            KeyBoardUtil.showKeyBoard(activity, edt_input_small_window);
        }
    }

    /**
     * 聊天选择图片回调
     */
    public interface InputSelectImageListener {
        /**
         * 打开相册
         */
        void photoClickListener(int type);
    }
}
