package com.eduhdsdk.toolcase;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.classroomsdk.Config;
import com.classroomsdk.bean.SmallPaintBean;
import com.classroomsdk.bean.StudentListBean;
import com.classroomsdk.common.SmallPaint;
import com.classroomsdk.common.ToolsType;
import com.classroomsdk.interfaces.EditTextInputControl;
import com.classroomsdk.interfaces.SmallBoardInterface;
import com.classroomsdk.manage.SharePadMgr;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.UserAdapter;
import com.eduhdsdk.entity.Constant;
import com.eduhdsdk.interfaces.ShowingPopupWindowInterface;
import com.eduhdsdk.room.RoomInfo;
import com.eduhdsdk.tools.CustomRecyclerView;
import com.eduhdsdk.tools.MovePopupwindowTouchListener;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.eduhdsdk.toolcase.ToolsPopupWindow.TOOLS_XIAOBAIBAN;

/***
 *    小白板
 */
public class SmallWhiteBoardPopupWindow implements SmallBoardInterface, EditTextInputControl {

    private Activity mContext;
    private PopupWindow popupWindow;

    //关闭
    private ImageView iv_close;
    //小白板
    private SmallPaint smallPaint;
    //小白板顶层
    private SmallPaint smallPaint_top;
    private RelativeLayout rl_buttom;
    //笔
    private ImageView smallPen;
    //文字
    private ImageView smallFont;
    //橡皮
    private ImageView smallEraser;
    //分发
    private Button smallDispatcher;
    //工具父布局
    private LinearLayout small_paint_types;
    private LinearLayout small_top_bar;
    private CustomRecyclerView mRecyclerview;

    private RelativeLayout rl_paint;
    //笔
    private ToolsFontPopupWindow mToolsPenPopupWindow;
    //橡皮
    private ToolsEraserPopupWindow mToolsEraserPopupWindow;
    //文字
    private ToolsFontPopupWindow mToolsFontPopupWindow;
    private View view;
    //顶部学生导航adapter
    private UserAdapter adapter;

    private SmallPaintBean mSmallPaintBean;
    private List<StudentListBean> adapterlist = new ArrayList<>();

    private boolean isHaiping = false;

    private MovePopupwindowTouchListener movePopupwindowTouchListener;//拖动
    private ShowingPopupWindowInterface showingPopupWindowInterface;//显示的回调
    public EditText paintPadLocationEditText;
    private View contentView;

    public SmallWhiteBoardPopupWindow(Activity context) {
        this.mContext = context;
        SharePadMgr.getInstance().setOnuserjoinClick(this);
    }

    public void initPopwindow(View view) {
        if (popupWindow != null) return;

        RoomUser user = TKRoomManager.getInstance().getMySelf();

        if (user == null) return;

        adapter = new UserAdapter(mContext);

        this.view = view;
        contentView = LayoutInflater.from(mContext).inflate(R.layout.tk_item_small_whiteboard, null, false);
        iv_close = contentView.findViewById(R.id.small_close);
        rl_paint = contentView.findViewById(R.id.rl_paint);
        smallPaint = contentView.findViewById(R.id.smallPaint);
        smallPaint_top = contentView.findViewById(R.id.smallPaint_top);
        smallPen = contentView.findViewById(R.id.small_pen);
        smallFont = contentView.findViewById(R.id.small_font);
        smallEraser = contentView.findViewById(R.id.small_eraser);
        smallDispatcher = contentView.findViewById(R.id.small_dispatcher);
        small_paint_types = contentView.findViewById(R.id.small_paint_types);
        small_top_bar = contentView.findViewById(R.id.small_top_bar);
        mRecyclerview = contentView.findViewById(R.id.small_recyclerview);

        rl_buttom = contentView.findViewById(R.id.rl_buttom);
        RelativeLayout close_ll = contentView.findViewById(R.id.close_ll);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(OrientationHelper.HORIZONTAL);
        mRecyclerview.setLayoutManager(manager);
        mRecyclerview.setAdapter(adapter);
        //如果是老师 显示关闭和分发
        if (user.role == 0) {
            iv_close.setVisibility(View.VISIBLE);
            smallDispatcher.setVisibility(View.VISIBLE);
        }


        smallPaint.setPadMgr(SharePadMgr.getInstance());
        smallPaint.setContext(mContext);
        smallPaint.setDrawShow(false);
        smallPaint.initInputPop((Activity) mContext, view);
        smallPaint.setSoundEffectsEnabled(false);
        smallPaint.setClickable(true);

        smallPaint_top.setPadMgr(SharePadMgr.getInstance());
        smallPaint_top.setContext(mContext);
        smallPaint_top.setDrawShow(true);
        smallPaint_top.initInputPop((Activity) mContext, view);
        smallPaint_top.setSoundEffectsEnabled(false);
        smallPaint_top.setClickable(true);
        smallPaint_top.setmEditTextInputControl(this);

        initPop();
        iv_close.setOnClickListener(clickListener);
        smallPen.setOnClickListener(clickListener);
        smallFont.setOnClickListener(clickListener);
        smallEraser.setOnClickListener(clickListener);
        smallDispatcher.setOnClickListener(clickListener);

        if (popupWindow == null) {
            popupWindow = new PopupWindow(mContext);
        }
        popupWindow.setContentView(contentView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(false);

        contentView.setTag(TOOLS_XIAOBAIBAN);
        if (movePopupwindowTouchListener == null) {
            movePopupwindowTouchListener = new MovePopupwindowTouchListener(popupWindow, mContext);
        }
        movePopupwindowTouchListener.setView(view);
        contentView.setOnTouchListener(movePopupwindowTouchListener);
//        pop高
        int popheight = view.getHeight() * 600 / 710;
        //白板高
        double wbheightdouble = popheight * 1.0 / 10 * 7.5;
        int wbheight = (int) wbheightdouble;
        //白板宽
        int wbwidth = wbheight / 9 * 16;
        //pop宽
        int popwidth = wbwidth + 20;

        /*//白板
        LinearLayout.LayoutParams smallpaint_ll = (LinearLayout.LayoutParams) smallPaint.getLayoutParams();
        smallpaint_ll.height = wbheight;
        smallpaint_ll.width = wbwidth;
        smallPaint.setLayoutParams(smallpaint_ll);*/

        //白板
        LinearLayout.LayoutParams smallpaint_ll = (LinearLayout.LayoutParams) rl_paint.getLayoutParams();
        smallpaint_ll.height = wbheight;
        smallpaint_ll.width = wbwidth;
        rl_paint.setLayoutParams(smallpaint_ll);

        //top高
        LinearLayout.LayoutParams small_top_bar_ll = (LinearLayout.LayoutParams) small_top_bar.getLayoutParams();
        small_top_bar_ll.height = (popheight - wbheight) / 2;
        small_top_bar_ll.width = popwidth;
        small_top_bar.setLayoutParams(small_top_bar_ll);

        //Recyclerview展示宽高
        int leftoffset = (popwidth - wbwidth) / 2;
        //recyclerview
        LinearLayout.LayoutParams mRecyclerview_ll = (LinearLayout.LayoutParams) mRecyclerview.getLayoutParams();
        mRecyclerview_ll.height = small_top_bar_ll.height * 4 / 5;
        mRecyclerview_ll.width = (popwidth - 20) / 10 * 8;
        mRecyclerview_ll.leftMargin = leftoffset;
        adapter.setmRecyclerWidth(mRecyclerview_ll.width - leftoffset, mRecyclerview_ll.height);
        mRecyclerview.setLayoutParams(mRecyclerview_ll);

        //关闭按钮大小
        LinearLayout.LayoutParams close_ll_ll = (LinearLayout.LayoutParams) close_ll.getLayoutParams();
        close_ll_ll.height = small_top_bar_ll.height;
        close_ll_ll.width = (popwidth - 20) / 10 * 2;
        close_ll.setLayoutParams(close_ll_ll);


        //buttom高
        LinearLayout.LayoutParams rl_buttom_ll = (LinearLayout.LayoutParams) rl_buttom.getLayoutParams();
        rl_buttom_ll.height = (popheight - wbheight) / 2;
        rl_buttom_ll.width = popwidth;
        rl_buttom.setLayoutParams(rl_buttom_ll);

        //笔
        LinearLayout.LayoutParams smallPen_ll = (LinearLayout.LayoutParams) smallPen.getLayoutParams();
        smallPen_ll.height = rl_buttom_ll.height * 2 / 4;
        smallPen_ll.width = smallPen_ll.height * 3;
        smallPen.setLayoutParams(smallPen_ll);
        //文字
        LinearLayout.LayoutParams smallFont_ll = (LinearLayout.LayoutParams) smallFont.getLayoutParams();
        smallFont_ll.height = rl_buttom_ll.height * 2 / 4;
        smallFont_ll.width = smallFont_ll.height * 3;
        smallFont.setLayoutParams(smallFont_ll);
        //橡皮
        LinearLayout.LayoutParams smallEraser_ll = (LinearLayout.LayoutParams) smallEraser.getLayoutParams();
        smallEraser_ll.height = rl_buttom_ll.height * 2 / 4;
        smallEraser_ll.width = smallEraser_ll.height * 3;
        smallEraser.setLayoutParams(smallEraser_ll);
        //分发
        RelativeLayout.LayoutParams smallDispatcher_rl = (RelativeLayout.LayoutParams) smallDispatcher.getLayoutParams();
        smallDispatcher_rl.height = rl_buttom_ll.height * 5 / 6;
        smallDispatcher_rl.width = (int) (smallDispatcher_rl.height * 3.56);
        smallDispatcher.setLayoutParams(smallDispatcher_rl);

        popupWindow.setHeight(popheight);
        popupWindow.setWidth(popwidth);
        if (showingPopupWindowInterface != null) {
            showingPopupWindowInterface.popupWindowShowing(TOOLS_XIAOBAIBAN);
        }
        int[] location = new int[2]; //0 300
        view.getLocationInWindow(location);
        int x = (view.getWidth() - popupWindow.getWidth()) / 2 + location[0];
        //居中向上偏移15
        int y = (view.getHeight() - popupWindow.getHeight()) / 2 + location[1] - 15;
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
        if (RoomInfo.getInstance().getRoomType() == 0) {
            if (LayoutPopupWindow.getInstance().layoutState == 3) {
                setVisibility(View.GONE);
            }
        } else {
            if (LayoutPopupWindow.getInstance().layoutState != 1) {
                setVisibility(View.GONE);
            }
        }
        //设置默认画笔和颜色
        smallPen.setSelected(true);
        smallPaint_top.setToolsType(ToolsType.pen);
        if (user.role == 0) {
            smallPaint_top.setmToolsPenColor(Color.parseColor(Config.mColor[5]));
            mToolsPenPopupWindow.colorSelectorView.setmSelectIndex(5);
        } else {
            smallPaint_top.setmToolsPenColor(Color.parseColor(Config.mColor[0]));
            mToolsPenPopupWindow.colorSelectorView.setmSelectIndex(0);
        }
    }

    public void setVisibility(int visibility) {
        if (contentView != null && popupWindow != null) {
            contentView.setVisibility(visibility);
            if (visibility == View.GONE) {
                popupWindow.setTouchable(false);
            } else if (visibility == View.VISIBLE) {
                popupWindow.setTouchable(true);
            }
            popupWindow.update();
        }
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    /**
     * 收到关闭信令再关闭
     */
    public void ClosePopPen() {
        if (popupWindow != null) {
            mSmallPaintBean = null;
            adapterlist.clear();
            popupWindow.dismiss();
            popupWindow = null;
            mToolsEraserPopupWindow.dismisspop();
            mToolsFontPopupWindow.dismisspop();
            mToolsPenPopupWindow.dismisspop();
        }
    }

    private void initPop() {
        //笔
        mToolsPenPopupWindow = new ToolsFontPopupWindow(mContext, false);
        mToolsPenPopupWindow.SetOnToolsListener(new ToolsFontPopupWindow.onToolsPenListener() {
            @Override
            public void SelectedColor(int color) {
                smallPaint_top.setmToolsPenColor(color);
            }

            @Override
            public void SeekBarProgress(int progress) {
                smallPaint_top.setmToolsPenProgress(progress);
            }
        });
        //文字
        mToolsFontPopupWindow = new ToolsFontPopupWindow(mContext, false);
        mToolsFontPopupWindow.SetOnToolsListener(new ToolsFontPopupWindow.onToolsPenListener() {
            @Override
            public void SelectedColor(int color) {
                smallPaint_top.setmToolsFontColor(color);
            }

            @Override
            public void SeekBarProgress(int progress) {
                smallPaint_top.setmToolsFontSize(progress);
            }
        });

        //橡皮
        mToolsEraserPopupWindow = new ToolsEraserPopupWindow(mContext, false);
        mToolsEraserPopupWindow.SetonToolsListener(new ToolsEraserPopupWindow.onToolsListener() {
            @Override
            public void SeekBarSize(int size) {
                smallPaint_top.setmToolsEraserWidth(size);
            }
        });
    }

    //点击事件
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TKRoomManager.getInstance().getMySelf().role == -1) {
                return;
            }
            int id = v.getId();
            //关闭
            if (id == R.id.small_close) {
                if (popupWindow.isShowing()) {
                    Map<String, Object> prepareing = new HashMap<>();
                    TKRoomManager.getInstance().delMsg("BlackBoard_new", "BlackBoard_new", "__all", new JSONObject(prepareing).toString());
                }
            } else if (id == R.id.small_pen) {
                if (mToolsPenPopupWindow != null) {
                    smallPen.setSelected(true);
                    smallFont.setSelected(false);
                    smallEraser.setSelected(false);
                    smallPaint_top.setToolsType(ToolsType.pen);
                    mToolsPenPopupWindow.showPopPenSmall(view, smallPen, isHaiping);
                }
            } else if (id == R.id.small_font) {
                if (mToolsFontPopupWindow != null) {
                    smallPen.setSelected(false);
                    smallFont.setSelected(true);
                    smallEraser.setSelected(false);
                    smallPaint_top.setToolsType(ToolsType.font);
                    mToolsFontPopupWindow.showPopPenSmall(view, smallFont, isHaiping);
                }
            } else if (id == R.id.small_eraser) {
                if (mToolsEraserPopupWindow != null) {
                    smallPen.setSelected(false);
                    smallFont.setSelected(false);
                    smallEraser.setSelected(true);
                    smallPaint_top.setToolsType(ToolsType.eraser);
                    mToolsEraserPopupWindow.showPopPenSmall(view, smallEraser, isHaiping);
                }
                //分发
            } else if (id == R.id.small_dispatcher) {
                //信令
                if (mSmallPaintBean != null) {
                    if (TKRoomManager.getInstance().getMySelf() != null && TKRoomManager.getInstance().getMySelf().role == 0) {
                        //准备状态 按钮显示分发
                        if (mSmallPaintBean.getBlackBoardState().equals("_prepareing")) {
                            Map<String, Object> prepareing = new HashMap<>();
                            prepareing.put("blackBoardState", "_dispenseed");
                            prepareing.put("currentTapKey", mSmallPaintBean.getCurrentTapKey());
                            prepareing.put("currentTapPage", 1);
                            TKRoomManager.getInstance().pubMsg("BlackBoard_new", "BlackBoard_new", "__all", new JSONObject(prepareing).toString(), true, "ClassBegin", "");
                        } else if (mSmallPaintBean.getBlackBoardState().equals("_dispenseed")) {
                            //如果是分发状态  按钮显示分发  状态点击回收
                            Map<String, Object> prepareing = new HashMap<>();
                            prepareing.put("blackBoardState", "_recycle");
                            prepareing.put("currentTapKey", mSmallPaintBean.getCurrentTapKey());
                            prepareing.put("currentTapPage", 1);
                            TKRoomManager.getInstance().pubMsg("BlackBoard_new", "BlackBoard_new", "__all", new JSONObject(prepareing).toString(), true, "ClassBegin", "");
                        } else if (mSmallPaintBean.getBlackBoardState().equals("_recycle")) {
                            //如果是回收状态  按钮显示回收  状态点击 再次分发
                            Map<String, Object> prepareing = new HashMap<>();
                            prepareing.put("blackBoardState", "_againDispenseed");
                            prepareing.put("currentTapKey", mSmallPaintBean.getCurrentTapKey());
                            prepareing.put("currentTapPage", 1);
                            TKRoomManager.getInstance().pubMsg("BlackBoard_new", "BlackBoard_new", "__all", new JSONObject(prepareing).toString(), true, "ClassBegin", "");
                        } else if (mSmallPaintBean.getBlackBoardState().equals("_againDispenseed")) {
                            //如果是再次分发状态  按钮显示再次分发  状态点击回收
                            Map<String, Object> prepareing = new HashMap<>();
                            prepareing.put("blackBoardState", "_dispenseed");
                            prepareing.put("currentTapKey", mSmallPaintBean.getCurrentTapKey());
                            prepareing.put("currentTapPage", 1);
                            TKRoomManager.getInstance().pubMsg("BlackBoard_new", "BlackBoard_new", "__all", new JSONObject(prepareing).toString(), true, "ClassBegin", "");
                        }
                    }
                }
            }
        }
    };


    @Override
    public void setTeacher(StudentListBean bean) {
        if (SharePadMgr.getInstance().mTeacherUser != null) {
            adapterlist.clear();
            adapterlist.add(bean);
            if (adapter != null && adapterlist.size() > 0) {
                adapter.SetData(adapterlist);
            }
        }
    }

    /**
     * 用户进入
     *
     * @param listBeans
     */
    @Override
    public void setStudents(List<StudentListBean> listBeans) {
        //是否是大并发教室 ，如果是 过滤只显示上台人数
        if (SharePadMgr.getInstance().isBigRoom) {
            if (listBeans != null && listBeans.size() > 0) {
                Iterator<StudentListBean> listBeanIterator = listBeans.iterator();
                while (listBeanIterator.hasNext()) {
                    StudentListBean studentListBean = listBeanIterator.next();
                    //如果在大并发状态下  Publishstate 为空 或者==0的这种 从集合中删除，现在不确定Publishstate参数 在IOS和pc什么时候发，如有问题，再次查看
                    if (studentListBean.getPublishstate() == null || studentListBean.getPublishstate() == 0) {
                        listBeanIterator.remove();
                    }
                }
            }
        }
        //升序
        Collections.sort(listBeans, new Comparator<StudentListBean>() {
            @Override
            public int compare(StudentListBean o1, StudentListBean o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        adapterlist.clear();
        //确保老师必须添加
        if (SharePadMgr.getInstance().mTeacherUser != null) {
            adapterlist.add(new StudentListBean("blackBoardCommon", "老师", 0));
        }
        adapterlist.addAll(listBeans);

        //选项及分页
        if (smallPaint != null) {
            smallPaint.post(new Runnable() {
                @Override
                public void run() {
                    if (adapterlist.size() > 0) {
                        if (adapter != null) {
                            adapter.SetData(adapterlist);
                        }
                    }
                }
            });
        }

    }

    /**
     * 当前状态及选中框
     */
    @Override
    public void setStatus(SmallPaintBean smallPaintBean) {
        this.mSmallPaintBean = smallPaintBean;
        if (mSmallPaintBean != null) {
            //准备状态 老师显示
            if (smallPaintBean.getBlackBoardState().equals("_prepareing")) {
                //是老师显示
                if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_STUDENT) {
                    ToolCaseMgr.getInstance().showSmallWhiteBoard();
                }
                if (popupWindow != null) {
                    if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {//巡课
                        rl_buttom.setVisibility(View.GONE);
                    } else {
                        smallDispatcher.setText(R.string.whiteboard_small_send);
                    }
                }
                adapterlist.clear();
                //分发状态 所有人显示pop 老师可查看学生
            } else if (smallPaintBean.getBlackBoardState().equals("_dispenseed")) {
                //如果是大并发 只显示上台
                if (SharePadMgr.getInstance().isBigRoom) {
                    if (TKRoomManager.getInstance().getMySelf().publishState > 0) {
                        ToolCaseMgr.getInstance().showSmallWhiteBoard();
                    }
                } else {
                    ToolCaseMgr.getInstance().showSmallWhiteBoard();
                }
                if (popupWindow != null) {

                    if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER ||
                            TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_STUDENT) {
                        small_paint_types.setVisibility(View.VISIBLE);
                    } else {
                        small_paint_types.setVisibility(View.INVISIBLE);
                    }


                    smallDispatcher.setText(R.string.whiteboard_small_take_back);
                    if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_STUDENT) {
                        mRecyclerview.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerview.setVisibility(View.INVISIBLE);
                    }
                }
                //回收状态 所有人显示pop 学生不可花 老师可点击学生花
            } else if (smallPaintBean.getBlackBoardState().equals("_recycle")) {
                //如果是大并发 只显示上台
                if (SharePadMgr.getInstance().isBigRoom) {
                    if (TKRoomManager.getInstance().getMySelf().publishState > 0) {
                        ToolCaseMgr.getInstance().showSmallWhiteBoard();
                    }
                } else {
                    ToolCaseMgr.getInstance().showSmallWhiteBoard();
                }
                if (popupWindow != null && popupWindow.getContentView() != null) {
                    smallDispatcher.setText(R.string.whiteboard_small_send_again);
                    mRecyclerview.setVisibility(View.VISIBLE);
                    if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_TEACHER) {
                        small_paint_types.setVisibility(View.VISIBLE);
                    } else {
                        small_paint_types.setVisibility(View.INVISIBLE);
                    }
                }
                //再次分发状态  所有人显示pop 老师显示学生  老师不可画学生画笔  可查看
            } else if (smallPaintBean.getBlackBoardState().equals("_againDispenseed")) {
                //如果是大并发 只显示上台
                if (SharePadMgr.getInstance().isBigRoom) {
                    if (TKRoomManager.getInstance().getMySelf().publishState > 0) {
                        ToolCaseMgr.getInstance().showSmallWhiteBoard();
                    }
                } else {
                    ToolCaseMgr.getInstance().showSmallWhiteBoard();
                }
                if (popupWindow != null && popupWindow.getContentView() != null) {
                    if (TKRoomManager.getInstance().getMySelf().role == Constant.USERROLE_LASSPATROL) {
                        small_paint_types.setVisibility(View.INVISIBLE);
                    } else {
                        small_paint_types.setVisibility(View.VISIBLE);
                    }
                    smallDispatcher.setText(R.string.whiteboard_small_take_back);
                    if (TKRoomManager.getInstance().getMySelf().role != Constant.USERROLE_STUDENT) {
                        mRecyclerview.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerview.setVisibility(View.INVISIBLE);
                    }
                }
            }

            //选项及分页
            if (smallPaint != null) {
                smallPaint.post(new Runnable() {
                    @Override
                    public void run() {
                        if (adapterlist.size() > 0) {
                            if (adapter != null) {
                                adapter.SetData(adapterlist);
                            }
                        }
                    }
                });
            }


        }
    }


    public void setHaiping(boolean haiping) {
        isHaiping = haiping;
    }

    public void setShowingPopupWindowInterface(ShowingPopupWindowInterface showingPopupWindowInterface) {
        this.showingPopupWindowInterface = showingPopupWindowInterface;
    }

    @Override
    public void showTextInput(float x, float y, int textSize, int textColor) {
        paintPadLocationEditText = new EditText(mContext);
        paintPadLocationEditText.setTextColor(textColor);
        paintPadLocationEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        paintPadLocationEditText.setPadding(0, 0, 0, 0);
        paintPadLocationEditText.setBackground(mContext.getResources().getDrawable(com.classroomsdk.R.drawable.tk_paintpad_ed_bg));
        paintPadLocationEditText.setMaxWidth((int) (rl_paint.getMeasuredWidth() - x));
        paintPadLocationEditText.setMinWidth(30);
        paintPadLocationEditText.setCursorVisible(false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = (int) x;
        layoutParams.topMargin = (int) y;
        paintPadLocationEditText.setLayoutParams(layoutParams);
        rl_paint.addView(paintPadLocationEditText);
    }

    @Override
    public void changeTextInput(String text) {
        if (paintPadLocationEditText != null) {
            paintPadLocationEditText.setText(text);
        }
    }

    @Override
    public void removeEditText() {
        if (paintPadLocationEditText != null) {
            rl_paint.removeView(paintPadLocationEditText);
            paintPadLocationEditText = null;
        }
    }
}
