package com.classroomsdk.custom;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.classroomsdk.R;
import com.classroomsdk.interfaces.OnSendClickListener;
import com.classroomsdk.tools.ScreenScale;
import com.classroomsdk.utils.KeyBoardUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/4/23/023.
 */

public class WhiteInputPop {

    private Activity activity;
    private EditText edt_input_small_window;
    private List<String> staticFacesList;


    //缓存聊天框中输入的内容，每次发送消息后置空
    private String edt_input_content = "";
    //默认用户可以发言
    private boolean is_forbid_chat = false;

    private View smallContentView;

    public OnSendClickListener onSendClickListener;

    public WhiteInputPop(Activity activity) {
        this.activity = activity;
        initStaticFaces();
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
                drawable.setBounds(0, 0, 50, 50);
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
//                MyIm imageSpan = new MyIm(activity, bitmap);
                sb.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb;
    }

    public PopupWindow chat_input_popup;

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
    public void showBoardPopupWindow(final View view, boolean is_show_board, final OnSendClickListener onSendClickListener) {

        if (chat_input_popup != null && chat_input_popup.isShowing()) {
            chat_input_popup.dismiss();
            chat_input_popup = null;
        }

        smallContentView = LayoutInflater.from(activity).inflate(R.layout.tk_edit_input, null);

        ScreenScale.scaleView(smallContentView, "showBoardPopupWindow");

        chat_input_popup = new PopupWindow(activity);
        chat_input_popup.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        chat_input_popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        chat_input_popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        TextView txt_send = (TextView) smallContentView.findViewById(R.id.txt_send);

        final ImageView iv_broad = (ImageView) smallContentView.findViewById(R.id.iv_broad);
        edt_input_small_window = (EditText) smallContentView.findViewById(R.id.edt_input_chat);
        //解决部分机型获取不到焦点问题
        edt_input_small_window.setFocusable(true);
        edt_input_small_window.setFocusableInTouchMode(true);

        iv_broad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户想要弹出键盘的时候，让edittext继续获得焦点
                edt_input_small_window.setFocusableInTouchMode(true);
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
                    edt_input_small_window.setText("");
                    edt_input_content = "";
                    if (onSendClickListener != null) {
                        onSendClickListener.ShowText(msg);
                    }
                    chat_input_popup.dismiss();
                }
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

        if (!activity.isFinishing()) {
            chat_input_popup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }

        chat_input_popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                edt_input_content = edt_input_small_window.getText().toString();
                if (SoftKeyBoardListenerWb.isSoftShowing(activity)) {
                    KeyBoardUtil.hideKeyBoard(activity, edt_input_small_window);
                }
            }
        });

        if (!is_show_board) {
            //显示表情的时候，设置不可获取焦点，否则会出现表情框与键盘同时存在的情况
            edt_input_small_window.setFocusable(false);
            iv_broad.setVisibility(View.VISIBLE);
            KeyBoardUtil.hideKeyBoard(activity, edt_input_small_window);
        } else {
            KeyBoardUtil.showKeyBoard(activity, edt_input_small_window);
        }

        edt_input_small_window.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (onSendClickListener != null) {
                    String msg = edt_input_small_window.getText().toString().trim();
                    onSendClickListener.textChange(msg);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
