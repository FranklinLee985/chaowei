package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.MemberListAdapter;
import com.eduhdsdk.room.RoomOperation;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.tools.KeyBoardUtil;
import com.eduhdsdk.tools.SoftKeyBoardListener;
import com.eduhdsdk.tools.Tools;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/4/20.
 * 花名册
 */

public class MemberListPopupWindowUtils implements View.OnClickListener {

    private Activity activity;
    private PopupWindow popupWindowMemberList, munber_input_popup;
    private ArrayList<RoomUser> memberList;
    private MemberListAdapter memberListAdapter;
    private TextView tv_popup_title;

    private ListView lv_student_name_data;
    private EditText edt_input_munber;
    private TextView et_number, tv_number_total;
    private CloseMemberListWindow popup_click;
    private ImageView im_to_left, im_to_right;

    public MemberListPopupWindowUtils(Activity activity, ArrayList<RoomUser> memberList) {
        this.activity = activity;
        this.memberList = memberList;
        memberListAdapter = new MemberListAdapter(activity, memberList);
    }

    public MemberListAdapter getMemberListAdapter() {
        return this.memberListAdapter;
    }

    public void setPopupWindowClick(CloseMemberListWindow popup_click) {
        this.popup_click = popup_click;
    }

    //判断弹框弹出时，用户的点击是在底部控件的内部还是外部
    boolean isInView = true;

    /**
     * 弹出popupwindow
     *
     * @param view
     * @param cb_view
     * @param width
     * @param height
     */
    public void showMemberListPopupWindow(final View view, final View cb_view, int width, int height) {
        if (popupWindowMemberList != null) {
            popupWindowMemberList.showAtLocation(view, Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
            return;
        }

        View contentView = null;
        contentView = LayoutInflater.from(activity).inflate(R.layout.tk_layout_member_list_popupwindow, null);

        ScreenScale.scaleView(contentView, "MemberListAdapter");

        tv_popup_title = (TextView) contentView.findViewById(R.id.tv_popup_title);

        lv_student_name_data = (ListView) contentView.findViewById(R.id.lv_student_name_data);
        LinearLayout ll_student_member_tab = (LinearLayout) contentView.findViewById(R.id.ll_student_member_tab);
        et_number = (TextView) contentView.findViewById(R.id.et_number);
        im_to_left = (ImageView) contentView.findViewById(R.id.im_to_left);
        im_to_right = (ImageView) contentView.findViewById(R.id.im_to_right);
        tv_number_total = (TextView) contentView.findViewById(R.id.tv_number_total);

        if (TKRoomManager.getInstance().getMySelf().role == 4) {    //巡课 隐藏花名册功能栏
            ll_student_member_tab.setVisibility(View.INVISIBLE);
        }
        contentView.findViewById(R.id.iv_popup_close).setOnClickListener(this);
        contentView.findViewById(R.id.im_to_left).setOnClickListener(this);
        contentView.findViewById(R.id.im_to_right).setOnClickListener(this);

        setTiteNumber(RoomSession.memberList.size());

        setArrowStatus();

        popupWindowMemberList = new PopupWindow(width, height);
        popupWindowMemberList.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindowMemberList.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindowMemberList.setContentView(contentView);

        lv_student_name_data.setAdapter(memberListAdapter);

        popupWindowMemberList.setBackgroundDrawable(new BitmapDrawable());
        popupWindowMemberList.setFocusable(false);
        popupWindowMemberList.setOutsideTouchable(true);

        popupWindowMemberList.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popup_click != null) {
                    popup_click.close_member_list_window();
                }
            }
        });

        popupWindowMemberList.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isInView = Tools.isInView(event, cb_view);
                return false;
            }
        });

        tv_popup_title.setText(activity.getString(R.string.userlist) + "（" + memberList.size() + "）");

        //设置显示动画
        popupWindowMemberList.setAnimationStyle(R.style.three_popup_animation);
        popupWindowMemberList.showAtLocation(view, Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
    }

    public void dismissPopupWindow() {
        if (popupWindowMemberList != null) {
            popupWindowMemberList.dismiss();
        }
    }

    public boolean isShowing() {
        if (popupWindowMemberList != null) {
            return popupWindowMemberList.isShowing();
        } else {
            return false;
        }
    }

    public void setTiteNumber(int number) {
        if (tv_popup_title != null) {
            tv_popup_title.setText(activity.getString(R.string.userlist) + "（" + number + "）");
        }
        if (tv_number_total != null && number != 0) {
            int total = 1;
            if (number % 15 == 0) {
                total = number / 15;
            } else {
                total = number / 15 + 1;
            }
            tv_number_total.setText(total + "");
        }
        if (im_to_right != null) {
            setArrowStatus();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_popup_close) {
            if (popupWindowMemberList != null) {
                popupWindowMemberList.dismiss();
                if (popup_click != null) {
                    popup_click.close_member_list_window();
                }
            }
        } else if (v.getId() == R.id.im_to_left) {
            String chooseMember = et_number.getText().toString();
            if (TextUtils.isEmpty(chooseMember)) {
                return;
            }
            int member = Integer.parseInt(chooseMember);
            if (member <= 1) {
                return;
            }

            int[] roles = {1, 2};
            int start = (member - 2) * 15;
            int max = (member - 1) * 15 - 1;
            RoomOperation.start = start;
            RoomOperation.max = max;

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ts", "asc");
            hashMap.put("role", "asc");
            TKRoomManager.getInstance().getRoomUsers(roles, start, max, null, hashMap);

            et_number.setText(String.valueOf(member - 1));
            setArrowStatus();
        } else if (v.getId() == R.id.im_to_right) {
            String chooseMember = et_number.getText().toString();
            if (TextUtils.isEmpty(chooseMember)) {
                return;
            }
            int member = Integer.parseInt(chooseMember);

            int[] roles = {1, 2};
            int start = member * 15;
            int max = (member + 1) * 15 - 1;
            RoomOperation.start = start;
            RoomOperation.max = max;

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ts", "asc");
            hashMap.put("role", "asc");
            TKRoomManager.getInstance().getRoomUsers(roles, start, max, null, hashMap);

            et_number.setText(String.valueOf(member + 1));
            setArrowStatus();
        }
    }

    /**
     * 修改左右箭头状态
     */
    private void setArrowStatus() {
        if (et_number == null || im_to_left == null || im_to_right == null || tv_number_total == null) {
            return;
        }
        String numberStr = et_number.getText().toString();
        if (TextUtils.isEmpty(numberStr)) {
            return;
        }
        int number = Integer.parseInt(numberStr);
        String totalNumberStr = tv_number_total.getText().toString();
        if (TextUtils.isEmpty(totalNumberStr)) {
            return;
        }
        int totalNumber = Integer.parseInt(totalNumberStr);

        if (number <= 1) {
            im_to_left.setClickable(false);
            im_to_left.setImageResource(R.drawable.tk_munber_common_icon_left_dis);
        } else {
            im_to_left.setClickable(true);
            im_to_left.setImageResource(R.drawable.tk_munber_common_icon_left);
        }

        if (totalNumber == 1) {
            im_to_right.setClickable(false);
            im_to_right.setImageResource(R.drawable.tk_munber_common_icon_right_dis);
        } else {
            if (number == totalNumber) {
                im_to_right.setClickable(false);
                im_to_right.setImageResource(R.drawable.tk_munber_common_icon_right_dis);
            } else {
                im_to_right.setClickable(true);
                im_to_right.setImageResource(R.drawable.tk_munber_common_icon_right);
            }
        }
    }

    public interface CloseMemberListWindow {
        void close_member_list_window();
    }
}
