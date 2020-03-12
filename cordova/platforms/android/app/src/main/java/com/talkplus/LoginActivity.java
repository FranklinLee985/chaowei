package com.talkplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eduhdsdk.BuildVars;
import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.interfaces.JoinmeetingCallBack;
import com.eduhdsdk.interfaces.MeetingNotify;
import com.eduhdsdk.room.RoomClient;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.room.RoomVariable;
import com.eduhdsdk.tools.AnimationUtil;
import com.eduhdsdk.tools.KeyBoardUtil;
import com.eduhdsdk.tools.LogCrashesUtil;
import com.eduhdsdk.tools.PermissionTest;
import com.eduhdsdk.tools.ResourceSetManage;
import com.eduhdsdk.tools.SkinTool;
//import com.talkcloud.plus.R;
import io.framework7.classroom.R;
import com.talkcloud.room.TKRoomManager;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashMap;
import java.util.Map;
import android.util.Log;

public class LoginActivity extends Activity implements OnClickListener, JoinmeetingCallBack, MeetingNotify, View.OnFocusChangeListener {

    private EditText edt_meetingid;
    private EditText edt_nickname;
    private Button txt_joinmeeting;
    private TextView txt_version, tv_role, tv_cancel;
    private String meetingid;
    private RelativeLayout re_role;
    private LinearLayout linearLayout;
    private TextView cb_student, cb_teacher, cb_lass_patrol;
    private String strnickname, class_identity;
    private Map<String, Object> map = null;
    private boolean isDiaShow = false;
    private int REQUEST_CODED = 4;
    private SharedPreferences sp = null;
    private PopupWindow popupWindow;
    //错误提示弹窗
    private PopupWindow error_tip_popupWindow;
    private PopupWindow error_popupWindow;

    private RelativeLayout re_loading;
    private ImageView loadingImageView;
    private String userRole = "2";
    private boolean playBackToast;
	private static final String TAG="LoginActivity";  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate Enter."); 
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.tk_activity_login);

        sp = getSharedPreferences("classroom", MODE_PRIVATE);

        re_loading = (RelativeLayout) findViewById(R.id.loading_gif);

        ScreenScale.scaleView(re_loading, "login_activity");

        loadingImageView = (ImageView) re_loading.findViewById(R.id.loadingImageView);

        //加载不同的loading图
        SkinTool.getmInstance().setLoadingSkin(this, loadingImageView);

        edt_meetingid = (EditText) findViewById(R.id.edt_meetingid);
        edt_nickname = (EditText) findViewById(R.id.edt_nickname);
        tv_role = (TextView) findViewById(R.id.tv_role);
        re_role = (RelativeLayout) findViewById(R.id.re_role);

        txt_joinmeeting = (Button) findViewById(R.id.txt_joinmeeting);
        txt_version = (TextView) findViewById(R.id.txt_version_num);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        txt_joinmeeting.setOnClickListener(this);
        re_role.setOnClickListener(this);

        edt_meetingid.setOnClickListener(this);
        edt_nickname.setOnClickListener(this);
        edt_meetingid.setOnFocusChangeListener(this);
        edt_nickname.setOnFocusChangeListener(this);
		
		Log.i(TAG, "before handleIntentemm ."); 
        handleIntentemm(getIntent());
		Log.i(TAG, "end handleIntentemm ."); 
        txt_version.setText(LoginUtils.getVersion());

        initNameAndPassWord();

        /*//测试切换网络
        LoginUtils.testNet(txt_version, this);*/
        ResourceSetManage.getInstance().setAppName(R.string.app_name);
        ResourceSetManage.getInstance().setAppLogo(R.drawable.tk_logo);
        //注册RoomClient
        RoomClient.getInstance().regiestInterface(this, this);
        //日志异常捕获
        LogCrashesUtil.getInstance().checkForCrashes(this, BuildVars.HOCKEY_APP_HASH);
        CrashReport.initCrashReport(getApplicationContext(), "de6cb21afa", false);
        //权限检查
        PermissionTest.requestPermission(this, REQUEST_CODED);
		Log.i(TAG, "onCreate end."); 
    }

    private void initNameAndPassWord() {
        SharedPreferences preferences = getSharedPreferences("RoomNuberAndNick", Context.MODE_PRIVATE);
        String roomNumber = preferences.getString("RoomNumber", null);
        String nick = preferences.getString("Nick", null);
        String role = preferences.getString("Role", null);
        if (!TextUtils.isEmpty(roomNumber)) {
            edt_meetingid.setText(roomNumber);
            edt_meetingid.setSelection(edt_meetingid.getText().length());
        }

        if (!TextUtils.isEmpty(nick)) {
            edt_nickname.setText(nick);
        }
        if (!TextUtils.isEmpty(role)) {
            switch (role) {
                case "0":
                    tv_role.setText(getString(R.string.teacher));
                    break;
                case "2":
                    tv_role.setText(getString(R.string.student));
                    break;
                case "4":
                    tv_role.setText(getString(R.string.lass_patrol));
                    break;
                default:
                    tv_role.setText(getString(R.string.student));
                    break;
            }
        } else {
            //默认显示学生
            tv_role.setText(getResources().getString(R.string.student));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        re_loading.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        //自动更新检测
        AutoUpdateUtil.getInstance().checkForUpdates(this);
        super.onResume();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.txt_joinmeeting) {
            if (!checkEmpty()) {
                txt_joinmeeting.setClickable(false);
                txt_joinmeeting.setSelected(true);
                strnickname = edt_nickname.getText().toString().trim();
                meetingid = edt_meetingid.getText().toString().trim();
                class_identity = tv_role.getText().toString().trim();

                if (class_identity.equals(getString(R.string.student))) {
                    SkinTool.getmInstance().setLoadingSkin(this, loadingImageView);
                    re_loading.setVisibility(View.VISIBLE);
                }

                map = new HashMap<String, Object>();
                map.put("host", RoomClient.webServer);
                map.put("port", 80);
                map.put("serial", meetingid);
                map.put("nickname", strnickname);

                //新加皮肤字段   1 PC   2 Android   3 IOS
                map.put("clientType", "2");

                if (sp.contains("servername")) {
                    map.put("servername", sp.getString("servername", ""));
                }
                if (class_identity.equals(getString(R.string.teacher))) {
                    map.put("userrole", 0);
                    userRole = "0";
                } else if (class_identity.equals(getString(R.string.student))) {
                    map.put("userrole", 2);
                    userRole = "2";
                } else {
                    map.put("userrole", 4);
                    userRole = "4";
                }

                saveRoomNumberAndNick();
                RoomClient.getInstance().joinRoom(LoginActivity.this, map);
            }
        } else if (v.getId() == R.id.re_role) {
            KeyBoardUtil.hideInputMethod(this);
            AnimationUtil.getInstance(this).rolemoveUpView(linearLayout);
            showCoursePopupWindow();
        } else if (v.getId() == R.id.tv_cancel) {
            AnimationUtil.getInstance(this).roleBackView(linearLayout);
            dissPopupWindow();
        } else if (v.getId() == R.id.cb_student) {
            cb_student.setTextColor(getResources().getColor(R.color.white));
            cb_teacher.setTextColor(getResources().getColor(R.color.color_choose_role));
            cb_lass_patrol.setTextColor(getResources().getColor(R.color.color_choose_role));

            cb_student.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud_checked));
            cb_teacher.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud));
            cb_lass_patrol.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud));

            tv_role.setText(getResources().getString(R.string.student));
            AnimationUtil.getInstance(this).roleBackView(linearLayout);
            dissPopupWindow();
        } else if (v.getId() == R.id.cb_teacher) {
            cb_teacher.setTextColor(getResources().getColor(R.color.white));
            cb_student.setTextColor(getResources().getColor(R.color.color_choose_role));
            cb_lass_patrol.setTextColor(getResources().getColor(R.color.color_choose_role));

            cb_teacher.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud_checked));
            cb_student.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud));
            cb_lass_patrol.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud));

            tv_role.setText(getResources().getString(R.string.teacher));
            AnimationUtil.getInstance(this).roleBackView(linearLayout);
            dissPopupWindow();
        } else if (v.getId() == R.id.cb_lass_patrol) {

            cb_lass_patrol.setTextColor(getResources().getColor(R.color.white));
            cb_student.setTextColor(getResources().getColor(R.color.color_choose_role));
            cb_teacher.setTextColor(getResources().getColor(R.color.color_choose_role));

            cb_lass_patrol.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud_checked));
            cb_student.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud));
            cb_teacher.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud));

            tv_role.setText(getResources().getString(R.string.lass_patrol));
            AnimationUtil.getInstance(this).roleBackView(linearLayout);
            dissPopupWindow();
        } else if (v.getId() == R.id.edt_meetingid) {
            edt_meetingid.setBackgroundResource(R.drawable.tk_rounded_edittext_selected);
        } else if (v.getId() == R.id.edt_nickname) {
            edt_nickname.setBackgroundResource(R.drawable.tk_rounded_edittext_selected);
        }
    }

    private void saveRoomNumberAndNick() {
        SharedPreferences preferences = getSharedPreferences("RoomNuberAndNick", Context.MODE_PRIVATE);
        SharedPreferences.Editor numberEditor = preferences.edit();
        numberEditor.putString("RoomNumber", meetingid);
        numberEditor.putString("Nick", strnickname);
        numberEditor.putString("Role", userRole);
        numberEditor.commit();
    }

    boolean isEmpty;

    private boolean checkEmpty() {
        isEmpty = false;
        if (edt_nickname.getText().toString().trim().isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.nickname_not_empty));
            isEmpty = true;
        }
        if (edt_meetingid.getText().toString().trim().isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.classroomnumber_not_empty));
            isEmpty = true;
        }
        if (tv_role.getText().toString().trim().isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.login_choose_user_role));
            isEmpty = true;
        }
        return isEmpty;
    }

    /**
     * @param activity
     * @param nTipID
     * @param mid
     * @param type     0 是正常逻辑   1 密码错误    根据这个控制布局的错误按钮是否显示以及字体的颜色
     */
    public void inputMeetingPassward(final Activity activity, int nTipID,
                                     final String mid, int type) {

        View contentView = LayoutInflater.from(activity).inflate(R.layout.tk_meeting_password, null);
        error_popupWindow = new PopupWindow(activity);

        error_popupWindow.setWidth(KeyBoardUtil.dp2px(LoginActivity.this, 300f));
        error_popupWindow.setHeight(KeyBoardUtil.dp2px(LoginActivity.this, 240f));

        final EditText etpsd = (EditText) contentView.findViewById(R.id.et_psd);
        TextView tv_room_need_pwd = (TextView) contentView.findViewById(R.id.tv_room_need_pwd);
        ImageView icon_error = (ImageView) contentView.findViewById(R.id.icon_error);
        CheckBox cb_control_pwd = (CheckBox) contentView.findViewById(R.id.cb_control_pwd);

        cb_control_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etpsd.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    etpsd.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                } else {
                    etpsd.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etpsd.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                }
                etpsd.setSelection(etpsd.getEditableText().length());
            }
        });

        tv_room_need_pwd.setText(getString(nTipID));
        if (type == 0) {    //正常
            icon_error.setVisibility(View.GONE);
        } else if (type == 1) {   //密码错误
            icon_error.setVisibility(View.VISIBLE);
            tv_room_need_pwd.setTextColor(Color.RED);
        }

        contentView.findViewById(R.id.iv_close_popup).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (error_popupWindow != null) {
                    error_popupWindow.dismiss();
                    isDiaShow = false;
                }
            }
        });

        contentView.findViewById(R.id.room_needs_pwd_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String meetingpwd = etpsd.getText().toString();
                if (TextUtils.isEmpty(meetingpwd)) {
                    ToastUtils.showToast(LoginActivity.this, getString(R.string.password_not_empty));
                    return;
                }
                SkinTool.getmInstance().setLoadingSkin(LoginActivity.this, loadingImageView);
                re_loading.setVisibility(View.VISIBLE);

                if (map != null) {
                    map.put("password", meetingpwd);
                    RoomClient.getInstance().joinRoom(LoginActivity.this, map);
                    KeyBoardUtil.hideKeyBoard(LoginActivity.this, etpsd);
                }
                error_popupWindow.dismiss();
                isDiaShow = false;
            }
        });

        error_popupWindow.setContentView(contentView);
        error_popupWindow.setFocusable(true);
        error_popupWindow.setTouchable(true);
        error_popupWindow.setOutsideTouchable(false);
        error_popupWindow.setBackgroundDrawable(new BitmapDrawable());
        error_popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        isDiaShow = true;
        if (!activity.isFinishing()) {
            error_popupWindow.showAtLocation(contentView, Gravity.CENTER, 0, 0);
        }
        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        error_popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                txt_joinmeeting.setClickable(true);
                txt_joinmeeting.setSelected(false);

                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }


    public void errorTipDialog(final Activity activity, String errorTip) {

        boolean res = false;
        if (activity == null) {
            res = false;
        } else {
            if (activity.isFinishing() || activity.isDestroyed()) {
                res = false;
            } else {
                res = true;
            }
        }

        if (res) {
            if (error_tip_popupWindow != null && error_tip_popupWindow.isShowing()) {
                return;
            }
            View contentView = LayoutInflater.from(activity).inflate(R.layout.tk_layout_login_error_dialog, null);

            if (error_tip_popupWindow == null) {
                error_tip_popupWindow = new PopupWindow(activity);
            }

            error_tip_popupWindow.setWidth(KeyBoardUtil.dp2px(LoginActivity.this, 300f));
            error_tip_popupWindow.setHeight(KeyBoardUtil.dp2px(LoginActivity.this, 150));

            final EditText etpsd = (EditText) contentView.findViewById(R.id.et_psd);
            TextView tv_room_need_pwd = (TextView) contentView.findViewById(R.id.tv_room_need_pwd);
            tv_room_need_pwd.setText(errorTip);

            contentView.findViewById(R.id.room_needs_pwd_ok).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    error_tip_popupWindow.dismiss();
                    isDiaShow = false;
                }
            });

            error_tip_popupWindow.setContentView(contentView);

            error_tip_popupWindow.setFocusable(true);
            error_tip_popupWindow.setTouchable(true);
            error_tip_popupWindow.setOutsideTouchable(false);
            error_tip_popupWindow.setBackgroundDrawable(new BitmapDrawable());
            error_tip_popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            isDiaShow = true;

            if (activity.isFinishing() || activity.isDestroyed() || getWindow() == null) {
                return;
            }

            error_tip_popupWindow.showAtLocation(contentView, Gravity.CENTER, 0, 0);
            //产生背景变暗效果
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.4f;
            getWindow().setAttributes(lp);

            error_tip_popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1f;
                    getWindow().setAttributes(lp);
                }
            });
        }
    }

    @Override
    public void callBack(int nRet) {
        if (nRet == 0) {

        } else if (nRet == 100) {

            // 踢人重置  登陆按钮
            if (re_loading != null) {
                re_loading.setVisibility(View.GONE);
            }

        } else if (nRet == 101) {
            errorTipDialog(this, getString(R.string.checkmeeting_error_5005));
        } else if (nRet == 4008) {
            inputMeetingPassward(this, R.string.checkmeeting_error_4008, meetingid, 1);
        } else if (nRet == 4110) {
            inputMeetingPassward(this, R.string.checkmeeting_error_4110, meetingid, 0);
        } else if (nRet == 4007) {
            errorTipDialog(this, getString(R.string.checkmeeting_error_4007));
        } else if (nRet == 3001) {
            errorTipDialog(this, getString(R.string.checkmeeting_error_3001));
        } else if (nRet == 3002) {
            errorTipDialog(this, getString(R.string.checkmeeting_error_3002));
        } else if (nRet == 3003) {
            errorTipDialog(this, getString(R.string.checkmeeting_error_3003));
        } else if (nRet == 4109) {
            errorTipDialog(this, getString(R.string.checkmeeting_error_4109));
        } else if (nRet == 4103) {
            errorTipDialog(this, getString(R.string.checkmeeting_error_4103));
        } else if (nRet == 4012) {
            inputMeetingPassward(this, R.string.checkmeeting_error_4008, meetingid, 1);
        } else if (nRet == 4112) {
            errorTipDialog(this, getString(R.string.checkmeeting_error_4112));
        } else if (nRet == 4113) {
            errorTipDialog(this, getString(R.string.checkmeeting_error_4113));
        } else {
            if (nRet == -1 || nRet == 3 || nRet == 11 || nRet == 1502) {
                errorTipDialog(this, getString(R.string.WaitingForNetwork));

            } else {
                errorTipDialog(this, getString(R.string.WaitingForNetwork) + "(" + nRet + ")");
            }
        }

        txt_joinmeeting.setClickable(true);
        txt_joinmeeting.setSelected(false);

        if (re_loading != null) {
            re_loading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onKickOut(int res) {
        if (res == RoomVariable.Kickout_Repeat) {
            Toast.makeText(this, getString(R.string.kick_out_tip), Toast.LENGTH_LONG).show();
        }

        if (res == RoomVariable.Kickout_ChairmanKickout) {
            Toast.makeText(this, getString(R.string.chairman_kick_out), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onWarning(int code) {
        if (code == 1) {
//			showExitDialog("视频打开失败请前往设置设置权限");
        }
        if (code == 2) {
//			showExitDialog("视频打开失败请前往设置设置权限");
        }
    }

    @Override
    public void onClassBegin() {
        if (TKRoomManager.getInstance().getMySelf().role == -1 && !playBackToast) {
            return;
        }
        playBackToast = false;
        Toast.makeText(this, getString(R.string.class_started), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClassDismiss() {
        Toast.makeText(this, getString(R.string.class_closeed), Toast.LENGTH_LONG).show();
    }

    private void handleIntentemm(Intent intent) {
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
				 Log.i(TAG, "handleIntentemm " + uri); 
                String url = uri.toString();
                if (url.startsWith("enterroomnew://")) {
					 Toast.makeText(this, "enterroomnew", Toast.LENGTH_LONG).show();
					
                    String temp = url.substring(url.indexOf("?") + 1);
                    String[] temps = temp.split("&");
                    Map<String, Object> tempMap = new HashMap<String, Object>();
                    for (int i = 0; i < temps.length; i++) {
                        String[] t = temps[i].split("=");
                        if (t.length > 1) {
                            tempMap.put(t[0], t[1]);
                        }
                    }
                    if (tempMap.containsKey("host")) {
                        String servername = (String) tempMap.get("host");
                        servername = servername.substring(0, servername.indexOf("."));
                        tempMap.put("servername", servername);
                        if (sp.contains("servername")) {
                            tempMap.put("servername", sp.getString("servername", ""));
                        }
                    }
                    if (tempMap.containsKey("path")) {
                        String tempPath = "http://" + tempMap.get("path");
                        tempMap.put("path", tempPath);
						Log.i(TAG, "call joinPlayBackRoom "); 
                        RoomClient.getInstance().joinPlayBackRoom(LoginActivity.this, temp);
                        playBackToast = true;
                    } else {
                        tempMap.put("port", 80);
						Log.i(TAG, "call joinRoom "); 
                        RoomClient.getInstance().joinRoom(LoginActivity.this, temp);
                    }
                }
				else
                {
                    if (url.startsWith("chaoweiclass://")) {
                        Toast.makeText(this, "chaoweiclass", Toast.LENGTH_LONG).show();

                        String temp = url.substring(url.indexOf("?") + 1);
                        String[] temps = temp.split("&");
                        Map<String, Object> tempMap = new HashMap<String, Object>();
                        for (int i = 0; i < temps.length; i++) {
                            String[] t = temps[i].split("=");
                            if (t.length > 1) {
                                tempMap.put(t[0], t[1]);
                            }
                        }
                        if (tempMap.containsKey("host")) {
                            String servername = (String) tempMap.get("host");
                            servername = servername.substring(0, servername.indexOf("."));
                            tempMap.put("servername", servername);
                            if (sp.contains("servername")) {
                                tempMap.put("servername", sp.getString("servername", ""));
                            }
                        }
                        /*
                        if (tempMap.containsKey("path")) {
                            String tempPath = "http://" + tempMap.get("path");
                            tempMap.put("path", tempPath);
                            RoomClient.getInstance().joinPlayBackRoom(LoginActivity.this, temp);
                            playBackToast = true;
                        } else {
                            tempMap.put("port", 80);
                            RoomClient.getInstance().joinRoom(LoginActivity.this, temp);
                        }*/
						Log.i(TAG, "call joinRoomEx "); 
                        RoomClient.getInstance().joinRoomEx(LoginActivity.this, temp);
                    }
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntentemm(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isDiaShow) {
                return super.onKeyDown(keyCode, event);
            } else {
                finish();
                return true;//return true;拦截事件传递,从而屏蔽back键。
            }
        }
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            finish();
            return true;//同理
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showCoursePopupWindow() {
        edt_meetingid.setBackgroundResource(R.drawable.tk_rounded_edittext);
        edt_nickname.setBackgroundResource(R.drawable.tk_rounded_edittext);
        re_role.setBackgroundResource(R.drawable.tk_rounded_edittext_selected);
        View contentView = LayoutInflater.from(this).inflate(R.layout.tk_layout_choose_role, null);
        tv_cancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        cb_student = (TextView) contentView.findViewById(R.id.cb_student);
        cb_teacher = (TextView) contentView.findViewById(R.id.cb_teacher);
        cb_lass_patrol = (TextView) contentView.findViewById(R.id.cb_lass_patrol);

        tv_cancel.setOnClickListener(this);
        cb_lass_patrol.setOnClickListener(this);
        cb_teacher.setOnClickListener(this);
        cb_student.setOnClickListener(this);

        String role = tv_role.getText().toString().trim();
        if (!TextUtils.isEmpty(role)) {
            if (role.equals(getString(R.string.teacher))) {
                cb_teacher.setTextAppearance(LoginActivity.this, R.style.pop_choose_role_checked);
                cb_student.setTextAppearance(LoginActivity.this, R.style.pop_choose_role_no_checked);
                cb_lass_patrol.setTextAppearance(LoginActivity.this, R.style.pop_choose_role_no_checked);

                cb_teacher.setBackgroundResource(R.drawable.tk_shape_role_backgroud_checked);
                cb_student.setBackgroundResource(R.drawable.tk_shape_role_backgroud);
                cb_lass_patrol.setBackgroundResource(R.drawable.tk_shape_role_backgroud);

            } else if (role.equals(getString(R.string.student))) {

                cb_student.setTextAppearance(LoginActivity.this, R.style.pop_choose_role_checked);
                cb_teacher.setTextAppearance(LoginActivity.this, R.style.pop_choose_role_no_checked);
                cb_lass_patrol.setTextAppearance(LoginActivity.this, R.style.pop_choose_role_no_checked);

                cb_student.setBackgroundResource(R.drawable.tk_shape_role_backgroud_checked);
                cb_teacher.setBackgroundResource(R.drawable.tk_shape_role_backgroud);
                cb_lass_patrol.setBackgroundResource(R.drawable.tk_shape_role_backgroud);
            } else {
                cb_lass_patrol.setTextAppearance(LoginActivity.this, R.style.pop_choose_role_checked);
                cb_teacher.setTextAppearance(LoginActivity.this, R.style.pop_choose_role_no_checked);
                cb_student.setTextAppearance(LoginActivity.this, R.style.pop_choose_role_no_checked);

                cb_lass_patrol.setBackgroundResource(R.drawable.tk_shape_role_backgroud_checked);
                cb_teacher.setBackgroundResource(R.drawable.tk_shape_role_backgroud);
                cb_student.setBackgroundResource(R.drawable.tk_shape_role_backgroud);
            }
        } else {
            //默认选择一个学生身份
            cb_student.setTextColor(getResources().getColor(R.color.white));
            cb_teacher.setTextColor(getResources().getColor(R.color.color_choose_role));
            cb_lass_patrol.setTextColor(getResources().getColor(R.color.color_choose_role));

            cb_student.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud_checked));
            cb_teacher.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud));
            cb_lass_patrol.setBackground(getResources().getDrawable(R.drawable.tk_shape_role_backgroud));

            tv_role.setText(getResources().getString(R.string.student));
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (popupWindow == null) {
            popupWindow = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (dm.heightPixels * 0.22));
        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                re_role.setBackgroundResource(R.drawable.tk_rounded_edittext);
                popupWindow = null;
                AnimationUtil.getInstance(LoginActivity.this).roleBackView(linearLayout);
            }
        });

        popupWindow.setContentView(contentView);
        popupWindow.setAnimationStyle(R.style.anim_PopupWindowt);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(findViewById(R.id.re_main), Gravity.BOTTOM, 0, 0);
    }

    private void dissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edt_meetingid:
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.tk_rounded_edittext_selected);
                } else {
                    v.setBackgroundResource(R.drawable.tk_rounded_edittext);
                }
                break;

            case R.id.edt_nickname:
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.tk_rounded_edittext_selected);
                } else {
                    v.setBackgroundResource(R.drawable.tk_rounded_edittext);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (error_popupWindow != null) {
            error_popupWindow.dismiss();
            error_popupWindow = null;
        }

        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        LogCrashesUtil.getInstance().resetInstance();
        RoomClient.getInstance().resetInstance();
        RoomSession.getInstance().resetInstance();
        AutoUpdateUtil.getInstance().resetInstance();
    }
}