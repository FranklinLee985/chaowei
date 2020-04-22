package com.eduhdsdk.toolcase;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eduhdsdk.BuildVars;
import com.eduhdsdk.adapter.AnswerAdapter;
import com.eduhdsdk.adapter.AnswerDetailsAdapter;
import com.classroomsdk.bean.AnswerBean;
import com.classroomsdk.bean.AnswerDetailsBean;
import com.classroomsdk.common.RoomControler;
import com.classroomsdk.http.HttpHelp;
import com.classroomsdk.http.ResponseCallBack;
import com.classroomsdk.manage.WBSession;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.room.RoomOperation;
import com.eduhdsdk.tools.PopupWindowTools;
import com.classroomsdk.utils.Tools;
import com.classroomsdk.viewUi.TimerChronometer;
import com.eduhdsdk.interfaces.ShowingPopupWindowInterface;
import com.eduhdsdk.tools.MovePopupwindowTouchListener;
import com.eduhdsdk.ui.FixItemListView;
import com.eduhdsdk.viewutils.PlayBackSeekPopupWindow;
import com.loopj.android.http.RequestParams;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.eduhdsdk.toolcase.ToolsPopupWindow.TOOLS_DATIQI;

/**
 * 答题器
 * Created by YF on 2018/12/27 0027
 */
public class AnswerPopupWindow implements View.OnClickListener, AnswerAdapter.CheckBoxCallBack, View.OnTouchListener, MovePopupwindowTouchListener.onMoveListener {
    private static AnswerPopupWindow instance;

    private JSONArray optionalAnswers;//提供给用户可选的选项
    private Map actions;//记录上提交的答案
    private JSONArray rightOptions;
    //    private String quesID;//问题的id
    private boolean isChangeAnswer = false;//是否修改答案

    private Activity mActivity;

    private PopupWindow popupWindow;
    private View contentView;
    private TextView answer_title;
    private ImageView close;
    private TextView answer_btn;
    private TextView answer_stu_btn;
    private TextView answer_right;
    private RelativeLayout rl_commit;

    //=================one 选择答案（老师开始答题）======================
    private RelativeLayout answer_one;
    private TextView answer_add;
    private TextView answer_delete;
    private TextView answer_my;
    private GridView answer_gv;
    //=================one 选择答案（老师开始答题）======================

    //=================two 答题统计结果======================
    private LinearLayout answer_two;
    private TextView answer_count;
    private TextView answer_time_hint;
    private TimerChronometer answer_time;
    private TextView answer_detail;

    private LinearLayout answer_ll_content;

    private LinearLayout answer_ll_a;
    private LinearLayout answer_ll_b;
    private LinearLayout answer_ll_c;
    private LinearLayout answer_ll_d;

    private LinearLayout answer_rl_a;
    private LinearLayout answer_rl_b;
    private LinearLayout answer_rl_c;
    private LinearLayout answer_rl_d;
    private LinearLayout answer_rl_e;
    private LinearLayout answer_rl_f;
    private LinearLayout answer_rl_g;
    private LinearLayout answer_rl_h;

    private SeekBar answer_sbar_a;
    private SeekBar answer_sbar_b;
    private SeekBar answer_sbar_c;
    private SeekBar answer_sbar_d;
    private SeekBar answer_sbar_e;
    private SeekBar answer_sbar_f;
    private SeekBar answer_sbar_g;
    private SeekBar answer_sbar_h;

    private TextView answer_tv_a;
    private TextView answer_tv_b;
    private TextView answer_tv_c;
    private TextView answer_tv_d;
    private TextView answer_tv_e;
    private TextView answer_tv_f;
    private TextView answer_tv_g;
    private TextView answer_tv_h;

    private TextView answer_tv_a_number;
    private TextView answer_tv_b_number;
    private TextView answer_tv_c_number;
    private TextView answer_tv_d_number;
    private TextView answer_tv_e_number;
    private TextView answer_tv_f_number;
    private TextView answer_tv_g_number;
    private TextView answer_tv_h_number;
    //=================two 答题统计结果======================

    //=================three 答题详情======================
    private FixItemListView listView;
    private LinearLayout answer_ll_details;
    private AnswerDetailsAdapter answerDetailsAdapter;
    private ImageView answer_details_img_left;
    private ImageView answer_details_img_right;
    private TextView answer_details_tv_page;
    private TextView answer_details_tv_pagecount;
    private TextView answer_public;
    private TextView amswer_nodata;

    private List<AnswerDetailsBean> detailsBeanList;//详情数据

    private int page = 1;//当前页数
    private int countTotal = 1;//总页数

    //=================three 答题详情======================
    //  true 为详情  false 为统计
    private boolean isDetail = true;
    //是否有选择答案 true 有  false 没有
    private boolean isAnswer = false;
    private int answer_start = 00; //开始答题
    private int answer_end = 11;   //结束答题
    private int answer_restart = 22;  //重新开始
    private int answer_button = 00;

    private AnswerAdapter answerAdapter;
    private List<AnswerBean> list = new ArrayList<>();
    private static final int MAX = 8; //最多8个答案
    private static final int MIN = 2;//最少2个答案
    private int num = 4;  //一列是4个答案
    private String[] names = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"};


    private MovePopupwindowTouchListener movePopupwindowTouchListener;//拖动
    private ShowingPopupWindowInterface showingPopupWindowInterface;//显示的回调

    private int answerCount = 0;//答题的总人数 老师用到的
    private String quesID;//老师发送信令，开始答题，结束答题，需要保持一致

    //回放时记录计时器的值
    private long mRecordTime;

    private View mRootView;
    private boolean isShow = false;
    private double moveX, movieY;
    private int offsetX, offsetY;
    private boolean isHaiping;
    private StringBuffer lastAnswer;

    public static synchronized AnswerPopupWindow getInstance() {
        if (instance == null) {
            instance = new AnswerPopupWindow();
        }
        return instance;
    }

    public void resetInstance() {
        instance = null;
    }


    public void setmACtivity(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 初始化popupwindow
     */
    public void initPopupWindow() {
        contentView = LayoutInflater.from(mActivity).inflate(R.layout.tk_layout_tools_answer, null);
//        ScreenScale.scaleView(contentView,"AnswerPopupWindow");
        answer_title = contentView.findViewById(R.id.answer_title);
        close = contentView.findViewById(R.id.tools_img_close);
        answer_btn = contentView.findViewById(R.id.answer_btn);
        rl_commit = contentView.findViewById(R.id.rl_commit);
        answer_stu_btn = contentView.findViewById(R.id.answer_stu_btn);
        answer_public = contentView.findViewById(R.id.answer_public);
        answer_my = contentView.findViewById(R.id.answer_my);
        answer_right = contentView.findViewById(R.id.answer_right);
        //身份为巡课的时候隐藏"点击字母预设正确答案"，但是公布答案后显示答案
        if (Constant.USERROLE_LASSPATROL == TKRoomManager.getInstance().getMySelf().role) {
            answer_right.setText("");
        } else {
            answer_right.setText(mActivity.getString(R.string.answer_correct));
        }
        answer_btn.setOnClickListener(this);
        answer_stu_btn.setOnClickListener(this);
        answer_public.setOnClickListener(this);
        //=================one======================
        answer_one = contentView.findViewById(R.id.answer_one);
        answer_add = answer_one.findViewById(R.id.answer_add);
        answer_delete = answer_one.findViewById(R.id.answer_delete);
        answer_gv = answer_one.findViewById(R.id.answer_gv);
        initData();
        if (answerAdapter == null) {
            answerAdapter = new AnswerAdapter(mActivity);
        }
        answerAdapter.setData(list);
        answer_gv.setAdapter(answerAdapter);
        answerAdapter.setCallBack(this);

        close.setOnClickListener(this);
        answer_add.setOnClickListener(this);
        answer_delete.setOnClickListener(this);
        //=================one======================
        //=================two======================
        answer_two = contentView.findViewById(R.id.answer_two);
        answer_count = answer_two.findViewById(R.id.answer_count);
        answer_time_hint = answer_two.findViewById(R.id.answer_time_hint);
        answer_time = answer_two.findViewById(R.id.answer_time);
        answer_detail = answer_two.findViewById(R.id.answer_detail);
        answer_detail.setOnClickListener(this);

        answer_ll_content = answer_two.findViewById(R.id.answer_ll_content);

        answer_ll_a = answer_two.findViewById(R.id.answer_ll_a);
        answer_ll_b = answer_two.findViewById(R.id.answer_ll_b);
        answer_ll_c = answer_two.findViewById(R.id.answer_ll_c);
        answer_ll_d = answer_two.findViewById(R.id.answer_ll_d);

        answer_rl_a = answer_two.findViewById(R.id.answer_rl_a);
        answer_rl_b = answer_two.findViewById(R.id.answer_rl_b);
        answer_rl_c = answer_two.findViewById(R.id.answer_rl_c);
        answer_rl_d = answer_two.findViewById(R.id.answer_rl_d);
        answer_rl_e = answer_two.findViewById(R.id.answer_rl_e);
        answer_rl_f = answer_two.findViewById(R.id.answer_rl_f);
        answer_rl_g = answer_two.findViewById(R.id.answer_rl_g);
        answer_rl_h = answer_two.findViewById(R.id.answer_rl_h);


        answer_sbar_a = answer_two.findViewById(R.id.answer_sbar_a);
        answer_sbar_b = answer_two.findViewById(R.id.answer_sbar_b);
        answer_sbar_c = answer_two.findViewById(R.id.answer_sbar_c);
        answer_sbar_d = answer_two.findViewById(R.id.answer_sbar_d);
        answer_sbar_e = answer_two.findViewById(R.id.answer_sbar_e);
        answer_sbar_f = answer_two.findViewById(R.id.answer_sbar_f);
        answer_sbar_g = answer_two.findViewById(R.id.answer_sbar_g);
        answer_sbar_h = answer_two.findViewById(R.id.answer_sbar_h);

        answer_sbar_a.setOnTouchListener(this);
        answer_sbar_b.setOnTouchListener(this);
        answer_sbar_c.setOnTouchListener(this);
        answer_sbar_d.setOnTouchListener(this);
        answer_sbar_e.setOnTouchListener(this);
        answer_sbar_f.setOnTouchListener(this);
        answer_sbar_g.setOnTouchListener(this);
        answer_sbar_h.setOnTouchListener(this);


        answer_tv_a = answer_two.findViewById(R.id.answer_tv_a);
        answer_tv_b = answer_two.findViewById(R.id.answer_tv_b);
        answer_tv_c = answer_two.findViewById(R.id.answer_tv_c);
        answer_tv_d = answer_two.findViewById(R.id.answer_tv_d);
        answer_tv_e = answer_two.findViewById(R.id.answer_tv_e);
        answer_tv_f = answer_two.findViewById(R.id.answer_tv_f);
        answer_tv_g = answer_two.findViewById(R.id.answer_tv_g);
        answer_tv_h = answer_two.findViewById(R.id.answer_tv_h);


        answer_tv_a_number = answer_two.findViewById(R.id.answer_tv_a_number);
        answer_tv_b_number = answer_two.findViewById(R.id.answer_tv_b_number);
        answer_tv_c_number = answer_two.findViewById(R.id.answer_tv_c_number);
        answer_tv_d_number = answer_two.findViewById(R.id.answer_tv_d_number);
        answer_tv_e_number = answer_two.findViewById(R.id.answer_tv_e_number);
        answer_tv_f_number = answer_two.findViewById(R.id.answer_tv_f_number);
        answer_tv_g_number = answer_two.findViewById(R.id.answer_tv_g_number);
        answer_tv_h_number = answer_two.findViewById(R.id.answer_tv_h_number);

        //=================two======================

        //=================three======================
        answer_ll_details = answer_two.findViewById(R.id.answer_ll_details);
        listView = answer_two.findViewById(R.id.answer_details_lv);
        answer_details_img_left = answer_two.findViewById(R.id.answer_details_img_left);
        answer_details_img_right = answer_two.findViewById(R.id.answer_details_img_right);
        answer_details_tv_page = answer_two.findViewById(R.id.answer_details_tv_page);
        answer_details_tv_pagecount = answer_two.findViewById(R.id.answer_details_tv_pagecount);
        amswer_nodata = answer_two.findViewById(R.id.amswer_nodata);
        answer_details_img_left.setOnClickListener(this);
        answer_details_img_right.setOnClickListener(this);

        if (answerDetailsAdapter == null) {
            answerDetailsAdapter = new AnswerDetailsAdapter(mActivity);
        }
        listView.setAdapter(answerDetailsAdapter);
        listView.setEmptyView(amswer_nodata);

        //=================three======================

        answer_btn.setText(mActivity.getString(R.string.answer_start));

        if (popupWindow == null) {
            popupWindow = new PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        popupWindow.setContentView(contentView);
        // 设置PopupWindow的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        popupWindow.setOutsideTouchable(false);
        // 设置PopupWindow是否能响应点击事件
        popupWindow.setTouchable(true);
        contentView.setTag(TOOLS_DATIQI);
        if (movePopupwindowTouchListener == null) {
            movePopupwindowTouchListener = new MovePopupwindowTouchListener(popupWindow, mActivity);
            movePopupwindowTouchListener.setOnMoveListener(this);
            movePopupwindowTouchListener.setType(TOOLS_DATIQI);
        }
        contentView.setOnTouchListener(movePopupwindowTouchListener);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                answerAdapter.setCheckable(true);
                isDetail = true;
                ToolsPopupWindow.getInstance().setAnswerBtnReset();
            }
        });
    }

    public void setVisibility(int visibility) {
        if (popupWindow != null) {
            if (visibility == View.GONE) {
                popupWindow.dismiss();
                popupWindow.setTouchable(false);
            } else if (visibility == View.VISIBLE) {
                if (mRootView != null && isShow) {
                    showPopupWindow(mRootView);
                    if (moveX != 0 || movieY != 0) {
                        movePopupWindow(mRootView, moveX, movieY, isHaiping);
                    }
                    if (offsetX != 0 || offsetY != 0) {
                        PopupWindowTools.movePopupWindow(popupWindow, offsetX, offsetY);
                    }
                }
                popupWindow.setTouchable(true);
            }
            popupWindow.update();
        }
    }

    public void initData() {
        list.clear();
        for (int i = 0; i < num; i++) {
            list.add(new AnswerBean(names[i], false));
        }
    }

    public int getGridHeight() {
        if (answerAdapter.getCount() > 4) {
            return answerAdapter.getItemHeight() + answer_gv.getVerticalSpacing();
        }
        return 0;
    }

    /**
     * 数据,入口
     */
    public void setStartData(JSONObject jsdata, long ts) {
        if (jsdata != null) {
            optionalAnswers = new JSONArray();
            try {
                quesID = jsdata.getString("quesID");

                optionalAnswers = jsdata.getJSONArray("options");
                if (optionalAnswers != null) {
                    if (optionalAnswers.length() > 0) {
                        num = optionalAnswers.length();
                        initData();
                        answerAdapter.setData(list);
                    }
                }

                String rightAnswer = mActivity.getString(R.string.answer_right);
                rightOptions = jsdata.getJSONArray("rightOptions");
                if (rightOptions != null) {
                    if (rightOptions.length() > 0) {
                        for (int x = 0; x < rightOptions.length(); x++) {
                            int a = (int) rightOptions.get(x);
                            if (x == 0) {
                                rightAnswer += names[a];
                            } else {
                                rightAnswer += "," + names[a];
                            }
                        }
                    }
                }
                if (Constant.USERROLE_LASSPATROL == TKRoomManager.getInstance().getMySelf().role) {
                    rl_commit.setVisibility(View.VISIBLE);
                    answer_btn.setVisibility(View.GONE);
                }
                answer_right.setText(rightAnswer);
                answer_count.setText(mActivity.getString(R.string.answer_number) + mActivity.getString(R.string.answer_peoples));

                if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_STUDENT) {//如果不是学生，显示的布局应该是toTwo
                    answer_btn.setText(mActivity.getString(R.string.answer_end));
                    setTWO(ts);
                    answer_button = answer_end;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 结束答题
     * 数据,入口
     */
    public void setEndData(JSONObject jsdata, boolean inListPub) {
        if (jsdata != null) {
            try {
                JSONObject state = jsdata.getJSONObject("state");
                JSONArray result = state.getJSONArray("result");
                quesID = jsdata.getString("quesID");
                if (inListPub) {
                    num = result.length();
                    initData();
                    answerAdapter.setData(list);
                }
                if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
                    setTWO();
                    isDetail = true;
                    answer_detail.setText(mActivity.getString(R.string.answer_details));
                    answer_ll_content.setVisibility(View.VISIBLE);
                    answer_ll_details.setVisibility(View.GONE);

                    answer_my.setVisibility(View.VISIBLE);
                    answer_my.setText(lastAnswer == null ? getAnswer(answerAdapter.getList()) : lastAnswer.toString());
                    answer_btn.setVisibility(View.GONE);
                    answer_right.setVisibility(View.GONE);
                    answer_add.setVisibility(View.VISIBLE);
                    answer_delete.setVisibility(View.VISIBLE);
                    answer_public.setVisibility(View.GONE);
                    answer_stu_btn.setVisibility(View.GONE);
                    answer_my.setVisibility(View.VISIBLE);

                } else {
                    stopTime();
                    getCountHandler.removeCallbacks(getCountRunnable);
                    setTWO();
                    answer_button = answer_restart;
                    if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {
                        answer_public.setVisibility(View.GONE);
                        answer_btn.setVisibility(View.GONE);
                    }
                }

                String rightAnswer = mActivity.getString(R.string.answer_right);
                JSONArray rightOptions = state.getJSONArray("rightOptions");
                if (rightOptions != null) {
                    if (rightOptions.length() > 0) {
                        for (int x = 0; x < rightOptions.length(); x++) {
                            int a = (int) rightOptions.get(x);
                            if (x == 0) {
                                rightAnswer += names[a];
                            } else {
                                rightAnswer += "," + names[a];
                            }
                        }
                    }
                }
                answer_right.setText(rightAnswer);

                int max = state.getInt("resultNum");
                int ansTime = state.getInt("ansTime");
//                answer_time.setBase(SystemClock.elapsedRealtime()-ansTime*1000);
                answer_time.setText(Tools.formatTime(ansTime * 1000));
//                answer_time.setBase(ansTime);
                answer_count.setText(mActivity.getString(R.string.answer_number) +
                        max + mActivity.getString(R.string.answer_people));
                setSeeBarMax(max);
                if (result != null) {
                    setSeeBarProgress(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     *   答题器,公布答案
     */
    public void publishResultOperation(boolean hasPub) {
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
            if (hasPub) {
                answer_right.setVisibility(View.VISIBLE);
            } else {
                answer_right.setVisibility(View.GONE);
            }
        } else {
            if (RoomControler.isShowEndOfTheAnswerCard()) {
//                String answer = answer_right.getText().toString();
//                answer_right.setText(answer + "(" + mActivity.getString(R.string.answer_published) + ")");
                if (hasPub) {
                    answer_public.setText(mActivity.getString(R.string.answer_published));
                    answer_public.setEnabled(false);
                } else {
                    answer_public.setText(mActivity.getString(R.string.answer_publish));
                    answer_public.setEnabled(true);
                }

            }
        }
    }

    /**
     * 老师获取学生提交的答案
     *
     * @param jsonObject
     */
    public void setQuestionCount(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                answerCount = jsonObject.getInt("answerCount");
                int totalUsers = jsonObject.getInt("totalUsers");
                int correctAnswers = jsonObject.getInt("correctAnswers");
                answer_count.setText(mActivity.getString(R.string.answer_number) + ":" + answerCount
                        + mActivity.getString(R.string.answer_people));
                setSeeBarMax(0);
                initAnswerNumber();
                initAnswerSeeBar();
                setSeeBarMax(answerCount);
                JSONObject values = jsonObject.getJSONObject("values");
                Iterator<String> it = values.keys();
                while (it.hasNext()) {
                    // 获得key
                    String key = it.next();
                    int i = Integer.parseInt(key);
                    String value = values.getString(key);
                    int number = Integer.parseInt(value);
                    if (i == 0) {
                        answer_sbar_a.setProgress(number);
                        answer_tv_a_number.setText(number + " " + mActivity.getString(R.string.answer_people));
                    } else if (i == 1) {
                        answer_sbar_b.setProgress(number);
                        answer_tv_b_number.setText(number + " " + mActivity.getString(R.string.answer_people));
                    } else if (i == 2) {
                        answer_sbar_c.setProgress(number);
                        answer_tv_c_number.setText(number + " " + mActivity.getString(R.string.answer_people));
                    } else if (i == 3) {
                        answer_sbar_d.setProgress(number);
                        answer_tv_d_number.setText(number + " " + mActivity.getString(R.string.answer_people));
                    } else if (i == 4) {
                        answer_sbar_e.setProgress(number);
                        answer_tv_e_number.setText(number + " " + mActivity.getString(R.string.answer_people));
                    } else if (i == 5) {
                        answer_sbar_f.setProgress(number);
                        answer_tv_f_number.setText(number + " " + mActivity.getString(R.string.answer_people));
                    } else if (i == 6) {
                        answer_sbar_g.setProgress(number);
                        answer_tv_g_number.setText(number + " " + mActivity.getString(R.string.answer_people));
                    } else if (i == 7) {
                        answer_sbar_h.setProgress(number);
                        answer_tv_h_number.setText(number + " " + mActivity.getString(R.string.answer_people));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     *    添加答案
     */
    public void addNum() {
        List list = answerAdapter.getList();
        if (num + 1 <= MAX) {
            ++num;
            if (list.size() > 0) {
                list.add(new AnswerBean(names[list.size()], false));
                answerAdapter.notifyDataSetChanged();
            }
            if (num > MIN) {
                answer_delete.setBackgroundResource(R.drawable.tk_popup_window_text_bg);
            }
            if (num < MAX) {
                answer_add.setBackgroundResource(R.drawable.tk_popup_window_text_bg);
            } else {
                answer_add.setBackgroundResource(R.drawable.tk_popup_window_text_bg_default);
            }
        } else {
            answer_add.setBackgroundResource(R.drawable.tk_popup_window_text_bg_default);
        }
    }

    /***
     *   减少答案
     */
    public void deleteNum() {
        List list = answerAdapter.getList();
        if (num - 1 >= MIN) {
            --num;
            list.remove(list.size() - 1);
            answerAdapter.notifyDataSetChanged();
            answer_right.setText(getAnswer(answerAdapter.getList()));
            if (num > MIN) {
                answer_delete.setBackgroundResource(R.drawable.tk_popup_window_text_bg);
            } else {
                answer_delete.setBackgroundResource(R.drawable.tk_popup_window_text_bg_default);
            }
            if (num < MAX) {
                answer_add.setBackgroundResource(R.drawable.tk_popup_window_text_bg);
            }
        } else {
            answer_delete.setBackgroundResource(R.drawable.tk_popup_window_text_bg_default);
        }
    }

    public void showPopupWindow(View rootView) {
        mRootView = rootView;
        isShow = true;
        if (contentView == null) {
            initPopupWindow();
        }

        if (popupWindow.isShowing()) {
            return;
        }
        isRole();
        if (showingPopupWindowInterface != null) {
            showingPopupWindowInterface.popupWindowShowing(TOOLS_DATIQI);
        }
        movePopupwindowTouchListener.setView(rootView);
        if (answer_gv != null) {
            answer_gv.setAdapter(answerAdapter);
        }
        if (WBSession.roomtype == 0) {//0是一对一教室
            popupWindow.setWidth(rootView.getMeasuredWidth() / 2);
            answerAdapter.setWidth(rootView.getMeasuredWidth() / 2 - 20 * 3 - 30 * 2);//popupwindow的宽度-间距的距离-gridview的边距

            doLayout(rootView.getMeasuredWidth() / 2);
        } else {
            popupWindow.setWidth(rootView.getMeasuredWidth() / 5 * 2);
            answerAdapter.setWidth(rootView.getMeasuredWidth() / 5 * 2 - 20 * 3 - 30 * 2);//popupwindow的宽度-间距的距离-gridview的边距

            doLayout(rootView.getMeasuredWidth() / 5 * 2);
        }


        int[] location = new int[2];
        rootView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        int pw = width - popupWindow.getWidth();
        int ph = height - popupWindow.getHeight();
        if (mActivity != null && !mActivity.isFinishing()) {
            popupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, x + pw / 2, y + ph / 4);
        }
        if (RoomInfo.getInstance().getRoomType() == 0) {
            if (LayoutPopupWindow.getInstance().layoutState == 3) {
                instance.setVisibility(View.GONE);
            }
        } else {
            if (LayoutPopupWindow.getInstance().layoutState != 1) {
                instance.setVisibility(View.GONE);
            }
        }
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public void dismiss() {
        isShow = false;
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                clear();
                popupWindow.dismiss();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (TKRoomManager.getInstance().getMySelf().role == -1) {
            return;
        }
        int id = v.getId();
        if (id == R.id.tools_img_close) {  //关闭答题卡
            closeAnswerPopup();
        } else if (id == R.id.answer_add) { // 添加答案
            addNum();
        } else if (id == R.id.answer_delete) {  // 减少答案
            deleteNum();
        } else if (id == R.id.answer_btn) {
            isAnswer();
            if (answer_button == answer_start) {  //开始答题
                if (isAnswer) {//选择过答案
                    answer_btn.setText(mActivity.getString(R.string.answer_end));
                    setTWO();
                    if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                        startTime(0);
                    }
                    sendStart();
                    answer_button = answer_end;
                } else {
                    if (mActivity != null) {
                        answer_right.setText(mActivity.getString(R.string.answer_lastone));
                    }
                }

            } else if (answer_button == answer_end) {  // 结束答题
                stopTime();
                sendEnd();
                answer_button = answer_restart;
            } else if (answer_button == answer_restart) {  //重新开始答题
                clear();
                answer_one.setVisibility(View.VISIBLE);
                answer_two.setVisibility(View.GONE);
                answer_ll_content.setVisibility(View.VISIBLE);
                answer_ll_details.setVisibility(View.GONE);
                JSONObject jsonRoomInfo = TKRoomManager.getInstance().getRoomProperties();
                String serial = jsonRoomInfo.optString("serial");
                TKRoomManager.getInstance().delMsg("Question", "Question_" + serial, "__all",
                        new HashMap<String, Object>());
                JSONObject data = new JSONObject();
                try {
                    data.put("action", "open");
                    TKRoomManager.getInstance().pubMsg("Question", "Question_" + serial, "__all",
                            data.toString(), true, "ClassBegin", null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                answer_btn.setText(mActivity.getString(R.string.answer_start));
                answer_right.setText(mActivity.getString(R.string.answer_correct));
//                setTWO();
//                sendStart();
                answer_button = answer_start;
            }
            /**
             * 如果身份为巡课，隐藏开始答题按钮
             */
            if (Constant.USERROLE_LASSPATROL == TKRoomManager.getInstance().getMySelf().role) {
                rl_commit.setVisibility(View.GONE);
            }
        } else if (id == R.id.answer_detail) {  //  详情和统计
            if (isDetail) {
                answer_detail.setText(mActivity.getString(R.string.answer_statistics));
                answer_ll_content.setVisibility(View.GONE);
                answer_ll_details.setVisibility(View.VISIBLE);
                setRightBackGround();
                setLeftBackGround();
                getDetailsData();
            } else {
                answer_detail.setText(mActivity.getString(R.string.answer_details));
                answer_ll_content.setVisibility(View.VISIBLE);
                answer_ll_details.setVisibility(View.GONE);
            }
            isDetail = !isDetail;
        } else if (id == R.id.answer_details_img_left) { // 左翻页
            if (page - 1 <= countTotal && countTotal - 1 > 0) {
                setLeftBackGround();
                getDetailsData();
            }
        } else if (id == R.id.answer_details_img_right) {  //  右翻页
            if (page + 1 <= countTotal) {
                setRightBackGround();
                getDetailsData();
            }
        } else if (id == R.id.answer_public) {//公布答案
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("result", getQuestionCount());
                jsonObject.put("resultNum", 1);
                jsonObject.put("ansTime", PopupWindowTools.getChronometerSeconds(answer_time));
                jsonObject.put("rightOptions", rightOptions);
                jsonObject.put("hasPub", true);
                TKRoomManager.getInstance().pubMsg("PublishResult", "PublishResult",
                        "__all", jsonObject.toString(), true, "ClassBegin", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.answer_stu_btn) {//学生提交答案
            studentCommitAnswer();
        }
    }

    /***
     *   学生提交答案
     */
    private void studentCommitAnswer() {
        if (mActivity.getString(R.string.answer_commit).equals(answer_stu_btn.getText().toString())) {
            /**
             * 判断修改答案的时候是否选中了答案
             */
            int answerSize = 0;
            for (int i = 0; i < answerAdapter.getList().size(); i++) {
                boolean a = answerAdapter.getList().get(i).isChecked();
                if (!a) {
                    answerSize++;
                }
            }
            //当学生未选择答案的时候不提交答案
            if (answerSize == answerAdapter.getList().size()) {
                return;
            }
            /**
             * 记录学生提交的答案，本地保存，解决我的答案为空
             */
            if (lastAnswer == null)
                lastAnswer = new StringBuffer();
            lastAnswer.setLength(0);
            lastAnswer.append(mActivity.getString(R.string.answer_my));
            for (int i = 0; i < answerAdapter.getList().size(); i++) {
                boolean boo = answerAdapter.getList().get(i).isChecked();
                if (boo) {
                    lastAnswer.append(answerAdapter.getList().get(i).getName());
                }
            }
            JSONObject data = new JSONObject();
            JSONObject actionsJson = new JSONObject();
            JSONArray options = new JSONArray();
            try {
                for (int i = 0; i < answerAdapter.getList().size(); i++) {
                    options.put(new JSONObject().put("hasChose", answerAdapter.getList().get(i).isChecked()));
                    if (actions == null) {
                        if (answerAdapter.getList().get(i).isChecked()) {
                            actionsJson.put("" + i, 1);
                        }
                    } else {
                        if (actions.containsKey(i + "")) {
                            if (answerAdapter.getList().get(i).isChecked()) {
//                                    actionsJson.put("" + i, 1);
                                actionsJson.remove("" + i);
                            } else {
                                actionsJson.put("" + i, -1);
                            }
                        } else {
                            if (answerAdapter.getList().get(i).isChecked()) {
                                actionsJson.put("" + i, 1);
                            }
                        }
                    }
                }
                data.put("options", options);
                data.put("actions", actionsJson);
                if (isChangeAnswer) {
                    data.put("modify", 1);
                } else {
                    data.put("modify", 0);
                }
                data.put("stuName", TKRoomManager.getInstance().getMySelf().nickName);

                data.put("quesID", quesID);
                data.put("isRight", isRight() == true ? 1 : 0);
                HashMap<String, Object> attrMap = new HashMap<>();
                attrMap.put("type", "count");
                attrMap.put("write2DB", true);


                if (isChangeAnswer) {
                    attrMap.put("modify", 1);
                } else {
                    attrMap.put("modify", 0);
                }

                attrMap.put("actions", actionsJson);
                JSONObject jsonRoomInfo = TKRoomManager.getInstance().getRoomProperties();
                String serial = jsonRoomInfo.optString("serial");
                TKRoomManager.getInstance().pubMsg("AnswerCommit", "Question_" + serial,
                        "__none", data.toString(), false, "Question", null, attrMap);

                answer_stu_btn.setText(mActivity.getString(R.string.answer_modify));
                isChangeAnswer = false;
                answerAdapter.setCheckable(false);

                if (actions == null) {
                    actions = new HashMap<String, Integer>();
                } else {
                    actions.clear();
                }
                for (int i = 0; i < answerAdapter.getList().size(); i++) {
                    if (answerAdapter.getList().get(i).isChecked()) {
                        actions.put("" + i, -1);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            answer_stu_btn.setText(mActivity.getString(R.string.answer_commit));
            isChangeAnswer = true;
            for (int i = 0; i < answerAdapter.getList().size(); i++) {
                answerAdapter.getList().get(i).setChecked(false);
            }
            answerAdapter.setCheckable(true);
        }
    }

    /***
     *   关闭答题卡
     */
    private void closeAnswerPopup() {
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
            dismiss();
            JSONObject jsonRoomInfo = TKRoomManager.getInstance().getRoomProperties();
            String serial = jsonRoomInfo.optString("serial");
            TKRoomManager.getInstance().delMsg("Question", "Question_" + serial,
                    "__all", new HashMap<String, Object>());
            TKRoomManager.getInstance().delMsg("PublishResult", "PublishResult",
                    "__all", "");
        }
    }


    /**
     * 判断用户身份，显示相对应的布局
     */
    public void isRole() {
        if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {//学生
            close.setVisibility(View.GONE);
            answer_my.setVisibility(View.GONE);
            answer_btn.setVisibility(View.GONE);
            answer_right.setVisibility(View.GONE);
            answer_add.setVisibility(View.GONE);
            answer_delete.setVisibility(View.GONE);
            answer_public.setVisibility(View.GONE);
            answer_stu_btn.setVisibility(View.VISIBLE);
        } else if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {//巡课
            close.setVisibility(View.GONE);
            answer_add.setVisibility(View.GONE);
            answer_delete.setVisibility(View.GONE);
            /**
             * 如果身份为巡课，隐藏开始答题按钮
             */
            rl_commit.setVisibility(View.GONE);
        }
    }

    /**
     * 发布答题
     */
    public void setTWO() {
        answer_one.setVisibility(View.GONE);
        answer_two.setVisibility(View.VISIBLE);
        answerCount();
        initAnswerNumber();

    }

    /**
     * 发布答题
     *
     * @param ts 信令的发送时间
     */
    public void setTWO(long ts) {
        answer_one.setVisibility(View.GONE);
        answer_two.setVisibility(View.VISIBLE);
        answerCount();
        initAnswerNumber();
        if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_STUDENT) {
            startTime(ts);
            getCountHandler.postDelayed(getCountRunnable, 1000);
        }
    }

    /**
     * 计时开始
     */
    public void startTime(long ts) {
        if (ts == 0) {
            answer_time.setBase(SystemClock.elapsedRealtime());//计时器清零
            int hour = (int) ((SystemClock.elapsedRealtime() - answer_time.getBase()) / 1000 / 60);
            answer_time.setFormat("0" + String.valueOf(hour) + ":%s");
            answer_time.start();
        } else {
            if (RoomOperation.serviceTime - ts > 0) {
                long t = RoomOperation.serviceTime - ts;
                answer_time.setBase(SystemClock.elapsedRealtime() - (t * 1000));//如果t》0，说明是后进收到的信令，需要把之前计时的时间算上。
                int hour = (int) ((SystemClock.elapsedRealtime() - answer_time.getBase()) / 1000 / 60 / 60);
                answer_time.setFormat("0" + String.valueOf(hour) + ":%s");
                answer_time.start();
            } else {
                answer_time.setBase(SystemClock.elapsedRealtime());//计时器清零
                int hour = (int) ((SystemClock.elapsedRealtime() - answer_time.getBase()) / 1000 / 60);
                answer_time.setFormat("0" + String.valueOf(hour) + ":%s");
                answer_time.start();
            }
        }

    }

    /**
     * 停止计时
     */
    public void stopTime() {
        if (RoomControler.isShowEndOfTheAnswerCard()) {
            answer_public.setVisibility(View.VISIBLE);
            answer_public.setText(mActivity.getString(R.string.answer_publish));
            answer_public.setEnabled(true);
        }
        answer_time.stop();
        answer_btn.setText(mActivity.getString(R.string.answer_restart));
//        answer_public.setVisibility(View.VISIBLE);
    }

    /**
     * 统计 显示多少个答案
     */
    public void answerCount() {
        int count = answerAdapter.getCount();
        if (count == 2) {
            answer_rl_e.setVisibility(View.GONE);
            answer_rl_f.setVisibility(View.GONE);
            answer_ll_c.setVisibility(View.GONE);
            answer_ll_d.setVisibility(View.GONE);
        } else if (count == 3) {
            answer_rl_e.setVisibility(View.GONE);
            answer_rl_f.setVisibility(View.GONE);
            answer_rl_g.setVisibility(View.GONE);
            answer_ll_d.setVisibility(View.GONE);
        } else if (count == 4) {
            answer_rl_e.setVisibility(View.GONE);
            answer_rl_f.setVisibility(View.GONE);
            answer_rl_g.setVisibility(View.GONE);
            answer_rl_h.setVisibility(View.GONE);
        } else if (count == 5) {
            answer_rl_f.setVisibility(View.INVISIBLE);
            answer_rl_g.setVisibility(View.INVISIBLE);
            answer_rl_h.setVisibility(View.INVISIBLE);
        } else if (count == 6) {
            answer_rl_g.setVisibility(View.INVISIBLE);
            answer_rl_h.setVisibility(View.INVISIBLE);
        } else if (count == 7) {
            answer_rl_h.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取正确答案
     */
    public String getAnswer(List<AnswerBean> list) {
        if (list != null) {
            boolean b = false;
            int s = 0;
            StringBuffer sb = new StringBuffer();
            if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
                sb.append(mActivity.getString(R.string.answer_my));
            } else {
                sb.append(mActivity.getString(R.string.answer_right));
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isChecked()) {
                    if (s == 0) {
                        sb.append("" + list.get(i).getName());
                    } else {
                        sb.append("," + list.get(i).getName());
                    }
                    b = true;
                    s++;
                }
            }
            if (b) {
                return sb.toString();
            } else {
                if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
                    return mActivity.getString(R.string.answer_my);
                } else {
                    return mActivity.getString(R.string.answer_correct);
                }
            }
        } else {
            return null;
        }
    }

    public boolean isRight() {
        boolean b = false;
        if (rightOptions != null) {
            int count = 0;
            for (int i = 0; i < answerAdapter.getList().size(); i++) {
                if (list.get(i).isChecked()) {
                    ++count;
                }
            }
            if (rightOptions.length() == count && count > 0 && rightOptions.length() > 0) {
                for (int i = 0; i < rightOptions.length(); i++) {
                    try {
                        int answer = rightOptions.getInt(i);
                        if (answerAdapter.getList().get(answer).isChecked()) {
                            b = true;
                        } else {
                            b = false;
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                b = false;
            }
        }
        return b;
    }

    public void isAnswer() {
        List<AnswerBean> list = answerAdapter.getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isChecked()) {
                isAnswer = true;
            }
        }
    }

    /**
     * 重新开始，要清空之前的数据记录
     */
    public void clear() {
        lastAnswer=null;
        num = 4;
        answer_add.setBackgroundResource(R.drawable.tk_popup_window_text_bg);
        answer_delete.setBackgroundResource(R.drawable.tk_popup_window_text_bg);
        initData();
        answerAdapter.setData(list);
        isAnswer();

        answer_one.setVisibility(View.VISIBLE);
        answer_two.setVisibility(View.GONE);

        answer_btn.setText(mActivity.getString(R.string.answer_start));
        answer_detail.setText(mActivity.getString(R.string.answer_details));
        answer_ll_content.setVisibility(View.VISIBLE);
        answer_ll_details.setVisibility(View.GONE);
        answer_stu_btn.setText(mActivity.getString(R.string.answer_commit));
        answer_public.setVisibility(View.GONE);

        answer_my.setVisibility(View.GONE);
        answer_btn.setVisibility(View.VISIBLE);
        answer_right.setVisibility(View.VISIBLE);
        //身份为巡课的时候隐藏"点击字母预设正确答案"，但是公布答案后显示答案
        if (Constant.USERROLE_LASSPATROL == TKRoomManager.getInstance().getMySelf().role) {
            answer_right.setText("");
        } else {
            answer_right.setText(mActivity.getString(R.string.answer_correct));
        }
        answer_add.setVisibility(View.VISIBLE);
        answer_delete.setVisibility(View.VISIBLE);
        answer_stu_btn.setVisibility(View.GONE);

        initAnswerCount();
        initAnswerNumber();
        initAnswerSeeBar();
        answerDetailsAdapter.clear();

        answer_button = answer_start;
        isChangeAnswer = false;
        isAnswer = false;
        actions = null;

        getCountHandler.removeCallbacks(getCountRunnable);
    }

    public void initAnswerCount() {
        answer_ll_a.setVisibility(View.VISIBLE);
        answer_ll_b.setVisibility(View.VISIBLE);
        answer_ll_c.setVisibility(View.VISIBLE);
        answer_ll_d.setVisibility(View.VISIBLE);

        answer_rl_a.setVisibility(View.VISIBLE);
        answer_rl_b.setVisibility(View.VISIBLE);
        answer_rl_c.setVisibility(View.VISIBLE);
        answer_rl_d.setVisibility(View.VISIBLE);
        answer_rl_e.setVisibility(View.VISIBLE);
        answer_rl_f.setVisibility(View.VISIBLE);
        answer_rl_g.setVisibility(View.VISIBLE);
        answer_rl_h.setVisibility(View.VISIBLE);

        setSeeBarMax(0);
    }

    public void setSeeBarMax(int max) {
        answer_sbar_a.setMax(max);
        answer_sbar_b.setMax(max);
        answer_sbar_c.setMax(max);
        answer_sbar_d.setMax(max);
        answer_sbar_e.setMax(max);
        answer_sbar_f.setMax(max);
        answer_sbar_g.setMax(max);
        answer_sbar_h.setMax(max);
    }

    /**
     * 初始化统计条后面的人数
     */
    public void initAnswerNumber() {
        answer_tv_a_number.setText(mActivity.getString(R.string.answer_peoples));
        answer_tv_b_number.setText(mActivity.getString(R.string.answer_peoples));
        answer_tv_c_number.setText(mActivity.getString(R.string.answer_peoples));
        answer_tv_d_number.setText(mActivity.getString(R.string.answer_peoples));
        answer_tv_e_number.setText(mActivity.getString(R.string.answer_peoples));
        answer_tv_f_number.setText(mActivity.getString(R.string.answer_peoples));
        answer_tv_g_number.setText(mActivity.getString(R.string.answer_peoples));
        answer_tv_h_number.setText(mActivity.getString(R.string.answer_peoples));
    }

    /**
     * 初始化统计条
     */
    public void initAnswerSeeBar() {
        answer_sbar_a.setProgress(0);
        answer_sbar_b.setProgress(0);
        answer_sbar_c.setProgress(0);
        answer_sbar_d.setProgress(0);
        answer_sbar_e.setProgress(0);
        answer_sbar_f.setProgress(0);
        answer_sbar_g.setProgress(0);
        answer_sbar_h.setProgress(0);
    }

    /**
     * 老师结束答题，需要发送的答题结果
     */
    public JSONArray getQuestionCount() {
        JSONArray result = new JSONArray();
        for (int i = 0; i < answerAdapter.getList().size(); i++) {
            if (i == 0) {
                result.put(answer_sbar_a.getProgress());
            } else if (i == 1) {
                result.put(answer_sbar_b.getProgress());
            } else if (i == 2) {
                result.put(answer_sbar_c.getProgress());
            } else if (i == 3) {
                result.put(answer_sbar_d.getProgress());
            } else if (i == 4) {
                result.put(answer_sbar_e.getProgress());
            } else if (i == 5) {
                result.put(answer_sbar_f.getProgress());
            } else if (i == 6) {
                result.put(answer_sbar_g.getProgress());
            } else if (i == 7) {
                result.put(answer_sbar_h.getProgress());
            }
        }

        return result;
    }

    public void setSeeBarProgress(JSONArray result) {
        try {
            for (int i = 0; i < result.length(); i++) {
                int number = result.getInt(i);
                if (i == 0) {
                    answer_sbar_a.setProgress(number);
                    answer_tv_a_number.setText(number + mActivity.getString(R.string.answer_people));
                } else if (i == 1) {
                    answer_sbar_b.setProgress(number);
                    answer_tv_b_number.setText(number + mActivity.getString(R.string.answer_people));
                } else if (i == 2) {
                    answer_sbar_c.setProgress(number);
                    answer_tv_c_number.setText(number + mActivity.getString(R.string.answer_people));
                } else if (i == 3) {
                    answer_sbar_d.setProgress(number);
                    answer_tv_d_number.setText(number + mActivity.getString(R.string.answer_people));
                } else if (i == 4) {
                    answer_sbar_e.setProgress(number);
                    answer_tv_e_number.setText(number + mActivity.getString(R.string.answer_people));
                } else if (i == 5) {
                    answer_sbar_f.setProgress(number);
                    answer_tv_f_number.setText(number + mActivity.getString(R.string.answer_people));
                } else if (i == 6) {
                    answer_sbar_g.setProgress(number);
                    answer_tv_g_number.setText(number + mActivity.getString(R.string.answer_people));
                } else if (i == 7) {
                    answer_sbar_h.setProgress(number);
                    answer_tv_h_number.setText(number + mActivity.getString(R.string.answer_people));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布答题 信令
     */
    public void sendStart() {
        JSONObject jsonRoomInfo = TKRoomManager.getInstance().getRoomProperties();
        String serial = jsonRoomInfo.optString("serial");
        JSONObject data = new JSONObject();
        JSONArray options = new JSONArray();
        JSONArray rightOptions = new JSONArray();
        JSONArray result = new JSONArray();
        try {
            for (int i = 0; i < answerAdapter.getList().size(); i++) {
                options.put(new JSONObject().put("hasChose", answerAdapter.getList().get(i).isChecked()));
                if (answerAdapter.getList().get(i).isChecked()) {
                    rightOptions.put(i);
                }
                result.put(0);
            }
            JSONObject state = new JSONObject();
            state.put("show", true);
            state.put("questionState", "RUNNING");

            /**
             *  detail    : 'DETAIL',       // 统计详情
             option    : 'OPTION',       // 选项界面
             statistics: 'STATISTICS',   // 统计界面
             current   : 'CURRENT',      // 维持当前页面不变
             */
            JSONObject page = new JSONObject();
            page.put("index", "STATISTICS");//
            page.put("data", new JSONObject());

            state.put("page", page);
            state.put("options", options);
            state.put("result", result);
            state.put("hasPub", false);
            state.put("ansTime", 0);
            state.put("resultNum", 0);
            state.put("rightOptions", rightOptions);
            state.put("detailData", new JSONArray());
            state.put("resizeInfo", new JSONObject().put("width", 5.2).put("height", 3.26));
            state.put("detailPageInfo", new JSONObject().put("current", 1).put("total", 1));
            state.put("hintShow", false);
            state.put("role", TKRoomManager.getInstance().getMySelf().role);

            JSONObject owner = new JSONObject();
            owner.put("id", TKRoomManager.getInstance().getMySelf().peerId);
            owner.put("watchStatus", 0);
            owner.put("role", TKRoomManager.getInstance().getMySelf().role);
            owner.put("nickname", TKRoomManager.getInstance().getMySelf().nickName);
            owner.put("publishstate", TKRoomManager.getInstance().getMySelf().publishState);
            owner.put("hasvideo", TKRoomManager.getInstance().getMySelf().hasVideo);
            owner.put("hasaudio", TKRoomManager.getInstance().getMySelf().hasAudio);
            owner.put("raisehand", false);
            owner.put("giftnumber", 0);
            owner.put("candraw", TKRoomManager.getInstance().getMySelf().canDraw);
            owner.put("disablevideo", TKRoomManager.getInstance().getMySelf().disablevideo);
            owner.put("disableaudio", TKRoomManager.getInstance().getMySelf().disableaudio);
            String primaryColor = "#00000000";
            if (TKRoomManager.getInstance().getMySelf().properties.containsKey("primaryColor")) {
                primaryColor = (String) TKRoomManager.getInstance().getMySelf().properties.get("primaryColor");
            }
            owner.put("pointerstate", false);
            owner.put("disablechat", false);
            owner.put("primaryColor", primaryColor);
            owner.put("systemversion", "");
            owner.put("version", "");
            owner.put("appType", "");
            owner.put("volume", 100);
            owner.put("codeVersion", "");
            owner.put("servername", TKRoomManager.getInstance().getRoomProperties().getString("servername"));
            owner.put("tk_ip", "");
            owner.put("tk_area", "");
            owner.put("tk_carrier", "");

            state.put("owner", owner);

            JSONObject event = new JSONObject();
            event.put("type", "roompubmsg");
            event.put("message", new JSONObject());

            state.put("event", event);


            data.put("options", options);
            data.put("action", "start");
            data.put("rightOptions", rightOptions);
            quesID = "ques_" + System.currentTimeMillis();
            data.put("quesID", quesID);
            data.put("state", state);
            HashMap<String, Object> attrMap = new HashMap<>();
            attrMap.put("userId", TKRoomManager.getInstance().getMySelf().peerId);
            attrMap.put("roomId", serial);
            attrMap.put("answerId", quesID);
            attrMap.put("role", TKRoomManager.getInstance().getMySelf().role);
            attrMap.put("write2DB", true);
            TKRoomManager.getInstance().pubMsg("Question", "Question_" + serial, "__all", data.toString(), true, "ClassBegin", null, attrMap);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 结束答题 信令
     */
    public void sendEnd() {
        JSONObject jsonRoomInfo = TKRoomManager.getInstance().getRoomProperties();
        String serial = jsonRoomInfo.optString("serial");
        JSONObject data = new JSONObject();
        JSONArray options = new JSONArray();
        try {
            for (int i = 0; i < answerAdapter.getList().size(); i++) {
                options.put(new JSONObject().put("hasChose", answerAdapter.getList().get(i).isChecked()));
            }

            JSONObject state = new JSONObject();
            state.put("show", true);
            state.put("questionState", "FINISHED");

            JSONObject page = new JSONObject();
            page.put("index", "STATISTICS");
            page.put("data", new JSONObject());

            state.put("page", page);
            state.put("options", options);
            state.put("result", getQuestionCount());
            state.put("hasPub", false);
            state.put("ansTime", PopupWindowTools.getChronometerSeconds(answer_time));
            state.put("resultNum", answerCount);
            state.put("rightOptions", rightOptions);
            state.put("detailData", new JSONArray());
            state.put("resizeInfo", new JSONObject().put("width", 5.2).put("height", 3.26));
            state.put("detailPageInfo", new JSONObject().put("current", 1).put("total", 1));
            state.put("hintShow", false);
            state.put("role", TKRoomManager.getInstance().getMySelf().role);

            JSONObject owner = new JSONObject();
            owner.put("id", TKRoomManager.getInstance().getMySelf().peerId);
            owner.put("watchStatus", 0);
            owner.put("role", TKRoomManager.getInstance().getMySelf().role);
            owner.put("nickname", TKRoomManager.getInstance().getMySelf().nickName);
            owner.put("publishstate", TKRoomManager.getInstance().getMySelf().publishState);
            owner.put("hasvideo", TKRoomManager.getInstance().getMySelf().hasVideo);
            owner.put("hasaudio", TKRoomManager.getInstance().getMySelf().hasAudio);
            owner.put("raisehand", false);
            owner.put("giftnumber", 0);
            owner.put("candraw", TKRoomManager.getInstance().getMySelf().canDraw);
            owner.put("disablevideo", TKRoomManager.getInstance().getMySelf().disablevideo);
            owner.put("disableaudio", TKRoomManager.getInstance().getMySelf().disableaudio);
            String primaryColor = "#00000000";
            if (TKRoomManager.getInstance().getMySelf().properties.containsKey("primaryColor")) {
                primaryColor = (String) TKRoomManager.getInstance().getMySelf().properties.get("primaryColor");
            }
            owner.put("pointerstate", false);
            owner.put("disablechat", false);
            owner.put("primaryColor", primaryColor);
            owner.put("systemversion", "");
            owner.put("version", "");
            owner.put("appType", "");
            owner.put("volume", 100);
            owner.put("codeVersion", "");
            owner.put("servername", TKRoomManager.getInstance().getRoomProperties().getString("servername"));
            owner.put("tk_ip", "");
            owner.put("tk_area", "");
            owner.put("tk_carrier", "");

            state.put("owner", owner);

            JSONObject event = new JSONObject();
            event.put("type", "roompubmsg");
            event.put("message", new JSONObject());

            state.put("event", event);


            data.put("options", options);
            data.put("action", "end");
            data.put("rightOptions", rightOptions);
            data.put("quesID", quesID);
            data.put("state", state);
            TKRoomManager.getInstance().pubMsg("Question", "Question_" + serial, "__all", data.toString(), true, "ClassBegin", null);


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", getQuestionCount());
            jsonObject.put("resultNum", 1);
            jsonObject.put("ansTime", PopupWindowTools.getChronometerSeconds(answer_time));
            jsonObject.put("rightOptions", rightOptions);

            TKRoomManager.getInstance().pubMsg("PublishResult", "PublishResult",
                    "__all", jsonObject.toString(), true, "ClassBegin", null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取详情页的数据
     */
    public void getDetailsData() {
        JSONObject jsonRoomInfo = TKRoomManager.getInstance().getRoomProperties();
        final String serial = jsonRoomInfo.optString("serial");
        String url = BuildVars.REQUEST_HEADER + WBSession.host + ":" + WBSession.port + "/ClientAPI/simplifyAnswer";
        RequestParams params = new RequestParams();
        params.put("id", quesID);
        params.put("page", page);
        params.put("serial", serial);

        HttpHelp.getInstance().post(url, params, new ResponseCallBack() {
            @Override
            public void success(int statusCode, JSONObject response) {
                try {
                    int nRet = response.getInt("result");
                    if (nRet == 0) {
                        JSONArray data = response.getJSONArray("data");
                        JSONObject pageinfo = response.getJSONObject("pageinfo");
                        page = Integer.parseInt(pageinfo.getString("page"));
                        int count = pageinfo.getInt("count");

                        countTotal = count % 20 == 0 ? (count / 20) : (count / 20) + 1;

                        answer_details_tv_page.setText(page + "");
                        answer_details_tv_pagecount.setText("/" + countTotal);
//                        setRightBackGround();
//                        setLeftBackGround();
                        if (data != null) {
                            if (data.length() > 0) {
                                if (detailsBeanList == null) {
                                    detailsBeanList = new ArrayList<>();
                                } else {
                                    detailsBeanList.clear();
                                }
                                for (int i = 0; i < data.length(); i++) {
                                    AnswerDetailsBean bean = new AnswerDetailsBean();
                                    JSONObject jsonObject = (JSONObject) data.get(i);
                                    String studentname = jsonObject.getString("studentname");
                                    String timestr = jsonObject.getString("timestr");
                                    int time = jsonObject.getInt("time");
                                    bean.setNickname(studentname);
                                    bean.setTime(timestr);
                                    JSONArray options = jsonObject.getJSONArray("options");

                                    StringBuffer sb = new StringBuffer();
                                    int s = 0;
                                    for (int j = 0; j < options.length(); j++) {
                                        if (options.getInt(j) == 0) {
                                            if (s == 0) {
                                                sb.append("A");
                                            } else {
                                                sb.append(",A");
                                            }
                                        } else if (options.getInt(j) == 1) {
                                            if (s == 0) {
                                                sb.append("B");
                                            } else {
                                                sb.append(",B");
                                            }
                                        } else if (options.getInt(j) == 2) {
                                            if (s == 0) {
                                                sb.append("C");
                                            } else {
                                                sb.append(",C");
                                            }
                                        } else if (options.getInt(j) == 3) {
                                            if (s == 0) {
                                                sb.append("D");
                                            } else {
                                                sb.append(",D");
                                            }
                                        } else if (options.getInt(j) == 4) {
                                            if (s == 0) {
                                                sb.append("E");
                                            } else {
                                                sb.append(",E");
                                            }
                                        } else if (options.getInt(j) == 5) {
                                            if (s == 0) {
                                                sb.append("F");
                                            } else {
                                                sb.append(",F");
                                            }
                                        } else if (options.getInt(j) == 6) {
                                            if (s == 0) {
                                                sb.append("G");
                                            } else {
                                                sb.append(",G");
                                            }
                                        } else if (options.getInt(j) == 7) {
                                            if (s == 0) {
                                                sb.append("H");
                                            } else {
                                                sb.append(",H");
                                            }
                                        }
                                        s++;
                                    }
                                    bean.setAnswer(sb.toString());
                                    detailsBeanList.add(bean);
                                }
                                answerDetailsAdapter.setData(detailsBeanList);
                                listView.setFixItemCount(4);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(int statusCode, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    /**
     * 设置详情页，右分页按钮的状态
     */
    public void setRightBackGround() {
        if (page + 1 <= countTotal) {
            ++page;
            if (page == 1) {
                answer_details_img_left.setBackgroundResource(R.drawable.tk_common_icon_left_disable);
            } else {
                answer_details_img_left.setBackgroundResource(R.drawable.tk_common_icon_left);
            }

            if (countTotal == 1 || page == countTotal) {
                answer_details_img_right.setBackgroundResource(R.drawable.tk_common_icon_right_disable);
            } else {
                answer_details_img_right.setBackgroundResource(R.drawable.tk_common_icon_right);
            }
        } else {
            if (page == 1) {
                answer_details_img_left.setBackgroundResource(R.drawable.tk_common_icon_left_disable);
            }
            answer_details_img_right.setBackgroundResource(R.drawable.tk_common_icon_right_disable);
        }

        answer_details_tv_page.setText(page + "");
        answer_details_tv_pagecount.setText("/" + countTotal);
    }

    /**
     * 设置详情页，左分页按钮的状态
     */
    public void setLeftBackGround() {
        if (page - 1 <= countTotal && page - 1 > 0) {
            --page;
            if (page == 1) {
                answer_details_img_left.setBackgroundResource(R.drawable.tk_common_icon_left_disable);
            } else {
                answer_details_img_left.setBackgroundResource(R.drawable.tk_common_icon_left);
            }

            if (countTotal == 1 || page == countTotal) {
                answer_details_img_right.setBackgroundResource(R.drawable.tk_common_icon_right_disable);
            } else {
                answer_details_img_right.setBackgroundResource(R.drawable.tk_common_icon_right);
            }
        } else {
            answer_details_img_left.setBackgroundResource(R.drawable.tk_common_icon_left_disable);
            if (countTotal == 1) {
                answer_details_img_right.setBackgroundResource(R.drawable.tk_common_icon_right_disable);
            } else {
                answer_details_img_right.setBackgroundResource(R.drawable.tk_common_icon_right);
            }
        }
        answer_details_tv_page.setText(page + "");
        answer_details_tv_pagecount.setText("/" + countTotal);
    }

    @Override
    public void checkedChangeCallBack(CompoundButton buttonView, boolean isChecked) {
        answer_right.setText(getAnswer(answerAdapter.getList()));
    }

    /**
     * 拖动位置
     *
     * @param rootView
     * @param moveX
     * @param movieY
     */
    public void movePopupWindow(View rootView, double moveX, double movieY, boolean isHaiping) {
        mRootView = rootView;
        this.moveX = moveX;
        this.movieY = movieY;
        this.isHaiping = isHaiping;
        PopupWindowTools.movePopupWindow(popupWindow, rootView, moveX, movieY, isHaiping);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    public void doLayout(int width) {
        answer_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (width * 0.038));

        float textSize = (float) (width * 0.24 * 0.4 / 3);
        RelativeLayout.LayoutParams btnlayoutParams = (RelativeLayout.LayoutParams) answer_btn.getLayoutParams();
        btnlayoutParams.width = (int) (width * 0.24);
        btnlayoutParams.height = (int) (width * 0.24 * 0.4);
        answer_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 3);
        answer_btn.setLayoutParams(btnlayoutParams);

        RelativeLayout.LayoutParams stuBtnlayoutParams = (RelativeLayout.LayoutParams) answer_stu_btn.getLayoutParams();
        stuBtnlayoutParams.width = (int) (width * 0.24);
        stuBtnlayoutParams.height = (int) (width * 0.24 * 0.4);
        answer_stu_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 3);
        answer_stu_btn.setLayoutParams(stuBtnlayoutParams);

        answer_add.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 3);
        answer_delete.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 3);
        answer_my.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 3);
        answer_right.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 3);
        answer_public.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 3);

        answer_count.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_time_hint.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_detail.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);

        answer_tv_a.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_b.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_c.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_d.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_e.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_f.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_g.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_h.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);

        answer_tv_a_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_b_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_c_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_d_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_e_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_f_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_g_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);
        answer_tv_h_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 5);

        amswer_nodata.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 4);
        answerDetailsAdapter.setTextSize(textSize - 4);
    }

    private Handler getCountHandler = new Handler();
    private Runnable getCountRunnable = new Runnable() {
        @Override
        public void run() {
            getCountHandler.postDelayed(this, 1000);
            getCount();
        }
    };

    /**
     * 老师每秒钟都要发送一个获取答案的信令
     */
    public void getCount() {
        JSONObject jsonRoomInfo = TKRoomManager.getInstance().getRoomProperties();
        if (jsonRoomInfo != null) {
            String serial = jsonRoomInfo.optString("serial");
            HashMap<String, Object> attrMap = new HashMap<>();
            attrMap.put("type", "getCount");
            TKRoomManager.getInstance().pubMsg("GetQuestionCount",
                    "Question_" + serial, TKRoomManager.getInstance().getMySelf().peerId,
                    new JSONObject().toString(), false, "Question",
                    null, attrMap);
        }
    }

    /**
     * 注册显示popupwindow的回调
     *
     * @param showingPopupWindowInterface
     */
    public void setShowingPopupWindowInterface(ShowingPopupWindowInterface showingPopupWindowInterface) {
        this.showingPopupWindowInterface = showingPopupWindowInterface;
    }

    public void setPlayBackSeekPopupWindow(PlayBackSeekPopupWindow window) {
        if (window != null) {
            window.setPlayBackListener(new PlayBackSeekPopupWindow.PlayBackListener() {
                @Override
                public void playingState(boolean state) {
                    if (TKRoomManager.getInstance().getMySelf().role == -1) {
                        if (state) {
                            if (mRecordTime != 0) {
                                answer_time.setBase(answer_time.getBase() + (SystemClock.elapsedRealtime() - mRecordTime));
                            } else {
                                answer_time.setBase(SystemClock.elapsedRealtime());
                            }
                            answer_time.start();
                        } else {
                            answer_time.stop();
                            mRecordTime = SystemClock.elapsedRealtime();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onMove(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
