package com.classroomsdk.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.classroomsdk.R;
import com.classroomsdk.bean.CaptureImg;
import com.classroomsdk.common.PaintPad;
import com.classroomsdk.common.RoomControler;
import com.classroomsdk.common.ToolsFormType;
import com.classroomsdk.common.ToolsPenType;
import com.classroomsdk.common.ToolsType;
import com.classroomsdk.interfaces.CaptureImgInterface;
import com.classroomsdk.interfaces.EditTextInputControl;
import com.classroomsdk.interfaces.FragmentUserVisibleHint;
import com.classroomsdk.interfaces.PaintPadActionUp;
import com.classroomsdk.manage.SharePadMgr;
import com.classroomsdk.popupwindow.ScreenShotPopWindow;
import com.talkcloud.room.RoomUser;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import skin.support.annotation.Skinable;

@Skinable
public class FaceShareFragment extends Fragment implements PaintPadActionUp, EditTextInputControl {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    /* private RelativeLayout share_paintpad_ll;*/
    private RelativeLayout share_paintpad_fl;

    private Runnable mRunnable;

    //底部接受数据层
    private PaintPad PaintPad_1;
    //顶部绘制层
    private PaintPad PaintPad_Top;

    //画板的宽高
    private int mFaceShareWidth, mFaceShareHeight;
    private View view;

    @SuppressLint("HandlerLeak")
    private Handler myhandler = new Handler();
    private ArrayList txtList = new ArrayList();

    private boolean visibilityTop = false;
    //是否刘海屏
    public boolean isHaiping;


    //存储所有截图pop
    public Map<String, ScreenShotPopWindow> screenShotPopWindowMap = new HashMap<>();
    //缓存需要下载的截图
    public Map<String, JSONObject> mDownloadCapture = new HashMap<>();
    public EditText paintPadLocationEditText;

    private FragmentUserVisibleHint fragmentUserVisibleHint;

    public FaceShareFragment() {
        // Required empty public constructor
    }

    public static FaceShareFragment newInstance(String param1, String param2) {
        FaceShareFragment fragment = new FaceShareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (fragmentUserVisibleHint != null) {
            fragmentUserVisibleHint.setUserVisibleHint();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tk_fragment_face_share, container, false);
        initview(view);
        return view;
    }

    private void initview(final View view) {
        share_paintpad_fl = view.findViewById(R.id.share_paintpad_fl);
        /* share_paintpad_ll = view.findViewById(R.id.share_paintpad_ll);*/
        PaintPad_1 = (PaintPad) view.findViewById(R.id.PaintPad_1);
        PaintPad_1.setmPaintPadActionUp(this);
        PaintPad_Top = (PaintPad) view.findViewById(R.id.PaintPad_Top);

        SharePadMgr.getInstance().setContext(getActivity());
        SharePadMgr.getInstance().setCaptureImgListener(captureImgInterface);

        PaintPad_1.setPadMgr(SharePadMgr.getInstance());
        PaintPad_1.setContext(getActivity());
        PaintPad_1.initInputPop(getActivity());
        PaintPad_1.setDrawShow(false);
        PaintPad_1.setSoundEffectsEnabled(false);
        PaintPad_1.setClickable(true);

        PaintPad_Top.setPadMgr(SharePadMgr.getInstance());
        PaintPad_Top.setDrawShow(true);
        PaintPad_Top.setContext(getActivity());
        PaintPad_Top.initInputPop(getActivity());
        PaintPad_Top.setSoundEffectsEnabled(false);
        PaintPad_Top.setClickable(true);
        PaintPad_Top.setmEditTextInputControl(this);


        PaintPad_Top.setPaintPadMoveInterface(new PaintPad.PaintPadMoveInterface() {
            @Override
            public void onTouchMove(float dx, float dy) {
                PaintPad_1.SyncOffset(dx, dy);
            }
        });
    }

    public void setVisibility(int visibility) {
        for (String key : screenShotPopWindowMap.keySet()) {
            ScreenShotPopWindow popupWindow = screenShotPopWindowMap.get(key);
            popupWindow.setVisibility(visibility);
        }
    }

    //设置fragmnet显示回调
    public void setFragmentUserVisibleHint(FragmentUserVisibleHint fragmentUserVisibleHint) {
        this.fragmentUserVisibleHint = fragmentUserVisibleHint;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        SharePadMgr.getInstance().resetSharePadMgr();
    }

    /**
     * 发送更改画笔类型
     *
     * @param b true=鼠标
     */
    public void sendToolType(boolean b) {
        JSONObject data = new JSONObject();
        try {
            data.put("sourceInstanceId", "default");
            data.put("selectMouse", b);
            TKRoomManager.getInstance().pubMsg("whiteboardMarkTool", "whiteboardMarkTool",
                    "__all", data.toString(), true, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 截图
     */
    CaptureImgInterface captureImgInterface = new CaptureImgInterface() {

        //开始下载
        @Override
        public void startDownload(CaptureImg captureImg, String file) {
            String key = "CaptureImg_" + captureImg.getCaptureImgInfo().getFileid();
            mDownloadCapture.put(key, new JSONObject());
        }

        //截图
        @Override
        public void setScreenShot(final CaptureImg captureImg, final String file) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String key = "CaptureImg_" + captureImg.getCaptureImgInfo().getFileid();
                    //如果fileid一样对话，就remove之前的，只保存最新的
                    if (screenShotPopWindowMap.containsKey(key)) {
                        screenShotPopWindowMap.get(key).dismissScreen();
                        screenShotPopWindowMap.remove(key);
                    }
                    //每次截图都创建一个新的popwindow
                    ScreenShotPopWindow screenShotPopWindow = new ScreenShotPopWindow(getActivity(), captureImg, file);
                    screenShotPopWindow.initPop(view, visibilityTop);
                    //保存pop
                    screenShotPopWindowMap.put(key, screenShotPopWindow);

                    if (mDownloadCapture != null && mDownloadCapture.size() > 0) {
                        for (String id : mDownloadCapture.keySet()) {
                            JSONObject jsonObject = mDownloadCapture.get(id);
                            String id1 = jsonObject.optString("id");
                            double percentLeft = jsonObject.optDouble("percentLeft");
                            double percentTop = jsonObject.optDouble("percentTop");
                            boolean isDrag = jsonObject.optBoolean("isDrag");
                            double scale = jsonObject.optDouble("scale");
                            if (scale != 0) {
                                SetCaptureImgSize(id1, scale);
                            }
                            if (percentLeft != 0 || percentTop != 0) {
                                SetCaptureImgDrag(id1, percentLeft, percentTop, isDrag);
                            }
                        }
                        mDownloadCapture.clear();
                    }
                }
            });
        }

        //刪除popwindow
        @Override
        public void dissmisScreenPop(final String captureImgID) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //根据信令id找到相应的pop dismiss并从map中移除
                    if (screenShotPopWindowMap != null && screenShotPopWindowMap.containsKey(captureImgID)) {
                        SharePadMgr.getInstance().removeOnScreenChangeListener(screenShotPopWindowMap.get(captureImgID).mScreenPaint.getmCaptureImg());
                        SharePadMgr.getInstance().removeOnTopScreenChangeListener(screenShotPopWindowMap.get(captureImgID).mScreenPaintTop.getmCaptureImg());
                        screenShotPopWindowMap.get(captureImgID).dismissScreen();
                        screenShotPopWindowMap.remove(captureImgID);
                        mDownloadCapture.remove(captureImgID);
                    }
                }
            });
        }


        @Override
        public void SetCaptureImgDrag(final String id, final double percentLeft, final double percentTop, final boolean isDrag) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!id.contains("_")) return;
                    String[] str = id.split("_");
                    String Mapid = "CaptureImg_" + str[1];
                    if (mDownloadCapture.containsKey(Mapid)) {
                        try {
                            JSONObject jsonObject = mDownloadCapture.get(Mapid);
                            jsonObject.put("id", id);
                            jsonObject.put("percentLeft", percentLeft);
                            jsonObject.put("percentTop", percentTop);
                            jsonObject.put("isDrag", isDrag);
                            mDownloadCapture.put(Mapid, jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //根据图片id找到对应pop 更新pop位置
                    if (screenShotPopWindowMap != null && screenShotPopWindowMap.containsKey(Mapid)) {
                        //是否拖动
                        if (isDrag) {
                            screenShotPopWindowMap.get(Mapid).movePopupWindow(view, percentLeft, percentTop, isHaiping);
                        }
                    }
                }
            });
        }

        @Override
        public void SetCaptureImgSize(final String id, final double scale) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!id.contains("_")) return;
                    String[] str = id.split("_");
                    String Mapid = "CaptureImg_" + str[1];
                    if (mDownloadCapture.containsKey(Mapid)) {
                        try {
                            JSONObject jsonObject = mDownloadCapture.get(Mapid);
                            jsonObject.put("id", id);
                            jsonObject.put("scale", scale);
                            mDownloadCapture.put(Mapid, jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //跟新对应pop大小 同时更新画布大小
                    if (screenShotPopWindowMap != null && screenShotPopWindowMap.containsKey(Mapid)) {
                        //是否拖动
                        screenShotPopWindowMap.get(Mapid).UpdatePopSize(scale);
                    }
                }
            });
        }

        @Override
        public synchronized void ClearAllPop() {
            if (screenShotPopWindowMap != null && screenShotPopWindowMap.size() > 0) {
                for (String key : screenShotPopWindowMap.keySet()) {
                    ScreenShotPopWindow shotPopWindow = screenShotPopWindowMap.get(key);
                    if (shotPopWindow != null) {
                        shotPopWindow.dismissScreen();
                    }
                }
//                screenShotPopWindowMap.clear();
            }
            mDownloadCapture.clear();
        }
    };


    /**
     * 重置放大缩小的比例
     */
    public float resetLargeOrSmallView(boolean isTop) {
        if (isTop) {
            return PaintPad_Top.LargeOrSmallView(1.0f);
        } else {
            return PaintPad_1.LargeOrSmallView(1.0f);
        }
    }


    @Override
    public void drawActionUp(PointF stopPoint, String fromId) {
        //画板抬起笔回调
        if (RoomControler.isShowWriteUpTheName() && !TextUtils.isEmpty(fromId) && !fromId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
            RoomUser user = TKRoomManager.getInstance().getUser(fromId);
            if (user != null) {
                TextView textView = new TextView(getContext());
                textView.setTextColor(Color.BLACK);
                textView.setText(user.nickName);
                textView.setTextSize(10);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                int left = (view.getMeasuredWidth() - PaintPad_1.getMeasuredWidth()) / 2;
                int top = (view.getMeasuredHeight() - PaintPad_1.getMeasuredHeight()) / 2;

                layoutParams.setMargins((int) (stopPoint.x + left), (int) (stopPoint.y + top), 0, 0);
                textView.setLayoutParams(layoutParams);
                textView.setTag(3);
                txtList.add(textView);
                share_paintpad_fl.addView(textView);
                if (mRunnable == null) {
                    mRunnable = new MyRunnable();
                    myhandler.postDelayed(mRunnable, 1000);
                }
            }
        }
    }


    //文本输入框显示
    @Override
    public void showTextInput(float x, float y, int textSize, int textColor) {
        paintPadLocationEditText = new EditText(getContext());
        paintPadLocationEditText.setTextColor(textColor);
        paintPadLocationEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        paintPadLocationEditText.setPadding(0, 0, 0, 0);
        paintPadLocationEditText.setBackground(getResources().getDrawable(R.drawable.tk_paintpad_ed_bg));
        paintPadLocationEditText.setMaxWidth((int) (share_paintpad_fl.getMeasuredWidth() - x));
        paintPadLocationEditText.setMinWidth(30);
        paintPadLocationEditText.setCursorVisible(false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = (int) (x + PaintPad_Top.getLeft());
        layoutParams.topMargin = (int) y;
        paintPadLocationEditText.setLayoutParams(layoutParams);
        share_paintpad_fl.addView(paintPadLocationEditText);
    }

    //修改文本框内容
    @Override
    public void changeTextInput(String text) {
        if (paintPadLocationEditText != null) {
            paintPadLocationEditText.setText(text);
        }
    }

    @Override
    public void removeEditText() {
        if (paintPadLocationEditText != null) {
            share_paintpad_fl.removeView(paintPadLocationEditText);
            paintPadLocationEditText = null;
        }
    }


    /**
     * 设置白板大小
     */
    public void transmitFaceShareSize(int width, int height) {
        mFaceShareWidth = width;
        mFaceShareHeight = height;
        setParams(width, height);
    }

    /**
     * 修改画布是否全屏
     *
     * @param fullScreen
     */
    public void setFaceShareFullScreen(boolean fullScreen) {
        if (fullScreen) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int wid = dm.widthPixels;
            int hid = dm.heightPixels;
            int resultW = wid;
            int resultH = hid;
            if (mFaceShareHeight <= 0 || mFaceShareWidth <= 0) {
                setParams(resultW, resultH);
            }
            if (mFaceShareHeight != 0 && hid != 0) {
                if (mFaceShareWidth / mFaceShareHeight > wid / hid) {
                    resultH = wid * mFaceShareHeight / mFaceShareWidth;
                } else {
                    resultW = hid * mFaceShareWidth / mFaceShareHeight;
                }
            }
            setParams(resultW, resultH);
        } else {
            setParams(mFaceShareWidth, mFaceShareHeight);
        }

        PaintPad_1.LargeOrSmallView();
        PaintPad_Top.LargeOrSmallView();
    }

    private void setParams(int width, int height) {
        PaintPad_1.setDrawSize(width, height);
        PaintPad_Top.setDrawSize(width, height);
    }


    //画笔显示名字的倒计时
    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = txtList.size() - 1; i >= 0; i--) {
                TextView txt = (TextView) txtList.get(i);
                int time = (int) txt.getTag();
                time--;
                txt.setTag(time);
                if (time == 0) {
                    share_paintpad_fl.removeView(txt);
                    txtList.remove(txt);
                }
            }
            myhandler.postDelayed(this, 1000);
        }
    }

    @Override
    public void onDestroy() {
        myhandler.removeCallbacks(mRunnable);
        mRunnable = null;
        myhandler = null;
        super.onDestroy();
    }

    //设置是否是刘海屏
    public void setLiuHaiping(boolean isHaiping) {
        this.isHaiping = isHaiping;
    }


    //#######################画笔属性及方法开始##############################


    /**
     * 设置工具画笔类型
     *
     * @param type
     */
    public void setToolsType(ToolsType type) {
        if (PaintPad_1 != null) {
            PaintPad_1.setToolsType(type);
        }
        if (PaintPad_Top != null) {
            PaintPad_Top.setToolsType(type);
        }
    }

    /**
     * 设置顶层画笔显示还是隐藏
     *
     * @param visibilityTop true 显示 false 隐藏
     */
    public void setVisibilityTop(boolean visibilityTop) {
        this.visibilityTop = visibilityTop;
        if (PaintPad_Top != null) {
            if (visibilityTop) {
                PaintPad_Top.setVisibility(View.VISIBLE);
            } else {
                PaintPad_Top.setVisibility(View.INVISIBLE);
            }
        }
        if (screenShotPopWindowMap != null) {

            Iterator<Map.Entry<String, ScreenShotPopWindow>> iterator = screenShotPopWindowMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ScreenShotPopWindow> entry = iterator.next();
                ScreenShotPopWindow shotPopWindow = entry.getValue();
                if (shotPopWindow != null) {
                    shotPopWindow.setvisibilityTop(visibilityTop);
                }
            }
        }
    }

    /**
     * 设置画笔是否隐藏
     *
     * @param isHideDraw true不显示  false显示
     */
    public void setHideDraw(boolean isHideDraw) {
        if (PaintPad_Top != null && PaintPad_1 != null) {
            PaintPad_Top.setHideDraw(isHideDraw);
            PaintPad_1.setHideDraw(isHideDraw);
        }
        if (screenShotPopWindowMap != null) {
            for (Map.Entry<String, ScreenShotPopWindow> entry : screenShotPopWindowMap.entrySet()) {
                ScreenShotPopWindow shotPopWindow = entry.getValue();
                if (shotPopWindow != null) {
                    shotPopWindow.setHideDraw(isHideDraw);
                }
            }
        }
    }


    /**
     * 设置画笔颜色
     *
     * @param color
     */
    public void setmToolsPenColor(int color) {
        if (PaintPad_Top != null) {
            PaintPad_Top.setmToolsPenColor(color);
        }
    }

    /**
     * 设置画笔类型
     *
     * @param penType 钢笔 荧光笔 直线 箭头
     */
    public void setmToolsPenType(ToolsPenType penType) {
        if (PaintPad_Top != null) {
            PaintPad_Top.setmToolsPenType(penType);
        }
    }

    /**
     * 设置画笔大小
     *
     * @param size 1 - 100
     */
    public void setmToolsPenProgress(int size) {
        if (PaintPad_Top != null) {
            PaintPad_Top.setmToolsPenProgress(size);
        }
    }

    /**
     * 设置文字颜色
     *
     * @param color
     */
    public void setmToolsFontColor(int color) {
        if (PaintPad_Top != null) {
            PaintPad_Top.setmToolsFontColor(color);
        }
    }

    /**
     * 设置文字大小
     *
     * @param size
     */
    public void setmToolsFontSize(int size) {
        if (PaintPad_Top != null) {
            PaintPad_Top.setmToolsFontSize(size);
        }
    }

    /**
     * 形状颜色
     *
     * @param color
     */
    public void setmToolsFormColor(int color) {
        if (PaintPad_Top != null) {
            PaintPad_Top.setmToolsFormColor(color);
        }
    }

    /**
     * 设置形状类型
     *
     * @param toolsFormType 空心/实心矩形  空心/实心圆
     */
    public void setmToolsFormType(ToolsFormType toolsFormType) {
        if (PaintPad_Top != null) {
            PaintPad_Top.setmToolsFormType(toolsFormType);
        }
    }

    /**
     * 设置形状颜色
     *
     * @param size
     */
    public void setmToolsFormWidth(int size) {
        if (PaintPad_Top != null) {
            PaintPad_Top.setmToolsFormWidth(size);
        }
    }

    /**
     * 设置橡皮擦大小
     *
     * @param size
     */
    public void setmToolsEraserWidth(int size) {
        if (PaintPad_1 != null) {
            PaintPad_1.setmToolsEraserWidth(size);
        }
    }

    //#######################画笔属性设置结束##############################


    //##################翻页工具条设置##########################################

    /**
     * @param visibility
     */
    public void setVisibilityRemark(boolean visibility) {

    }


    /**
     * 放大缩小白板 画布
     *
     * @param isLargeOrSmall
     * @param isTop          顶部画布还是底部画布
     * @return
     */
    public float getLargeOrSmallScale(boolean isLargeOrSmall, boolean isTop) {
        float scale = 0;
        if (isTop) {
            if (PaintPad_Top != null) {
                scale = PaintPad_Top.LargeOrSmallView(isLargeOrSmall);
            }
        } else {
            if (PaintPad_1 != null) {
                scale = PaintPad_1.LargeOrSmallView(isLargeOrSmall);
            }
        }
        return scale;
    }
    //##################翻页工具条结束##########################################
}
