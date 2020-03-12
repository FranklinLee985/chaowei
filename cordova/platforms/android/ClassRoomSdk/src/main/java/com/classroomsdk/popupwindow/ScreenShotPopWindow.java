package com.classroomsdk.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.classroomsdk.R;
import com.classroomsdk.bean.CaptureImg;
import com.classroomsdk.common.GlobalToolsType;
import com.classroomsdk.common.ScreenPaint;
import com.classroomsdk.common.ToolsType;
import com.classroomsdk.interfaces.EditTextInputControl;
import com.classroomsdk.manage.SharePadMgr;
import com.classroomsdk.tools.ScreenScale;
import com.classroomsdk.utils.FullScreenTools;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ScreenShotPopWindow implements EditTextInputControl {

    private Context mContext;
    private CaptureImg mCaptureImg;
    private String mFile;
    private View rootView;

    private int orgX, orgY;
    private int offsetX, offsetY;

    private int popwidth;
    private int popheight;

    private boolean huawei, oppo, voio;
    private View contentView;
    private FrameLayout fl_layout;
    public EditText paintPadLocationEditText;

    public ScreenShotPopWindow(Context context, CaptureImg captureImg, String file) {
        this.mContext = context;
        this.mCaptureImg = captureImg;
        this.mFile = file;
        huawei = FullScreenTools.hasNotchInScreen(context);
        oppo = FullScreenTools.hasNotchInOppo(context);
        voio = FullScreenTools.hasNotchInScreenAtVoio(context);
    }

    public ScreenPaint mScreenPaint;
    public ScreenPaint mScreenPaintTop;
    private PopupWindow popupWindow;
    private Bitmap btImage;

    //初始化pop
    public void initPop(View view, boolean visibilityTop) {
        //如果截图类 和 下载的图片都为空 不去创建popwindow
        if (mCaptureImg == null && mFile == null) return;
        this.rootView = view;
        Bitmap bt = getBitMapImage(mFile);
        if (bt != null && !bt.isRecycled()) {
            //控件实例化
            contentView = LayoutInflater.from(mContext).inflate(R.layout.tk_item_screenshot, null, false);
            fl_layout = contentView.findViewById(R.id.fl_layout);
            mScreenPaint = contentView.findViewById(R.id.screenpaint_1);
            //绑定画布管理类
            mScreenPaint.setPadMgr(SharePadMgr.getInstance());
            mScreenPaint.setContext(mContext);
            //标识此画布是底层画布还是顶层画布 false为底层
            mScreenPaint.setDrawShow(false);
            //初始化文字输入
            mScreenPaint.initInputPop((Activity) mContext, view);
            mScreenPaint.setSoundEffectsEnabled(false);
            //设置下载好图片
            mScreenPaint.setBitmap(bt);
            //设置图片类
            mScreenPaint.setCapture(mCaptureImg);
            mScreenPaint.setClickable(true);

            //同上
            mScreenPaintTop = contentView.findViewById(R.id.screenpaint_top);
            mScreenPaintTop.setPadMgr(SharePadMgr.getInstance());
            mScreenPaintTop.setContext(mContext);
            mScreenPaintTop.setDrawShow(true);
            mScreenPaintTop.initInputPop((Activity) mContext, view);
            mScreenPaintTop.setSoundEffectsEnabled(false);
            mScreenPaintTop.setEditTextInputControl(this);
            mScreenPaintTop.setBitmap(bt);
            mScreenPaintTop.setCapture(mCaptureImg);
            mScreenPaintTop.setClickable(true);


//            contentview.setOnTouchListener(onTouchListener);

            //图片宽高就是pop宽高
            int width = bt.getWidth();
            int height = bt.getHeight();
            double score = width * 1.0 / height;
            if (width > view.getWidth()) {
                width = view.getWidth();
                double dbheight = width / score;
                height = (int) dbheight;
            }
            if (height > view.getHeight()) {
                height = view.getHeight();
                double dbwidth = height * score;
                width = (int) dbwidth;
            }

            popupWindow = new PopupWindow(mContext);
            popupWindow.setWidth(width);
            popupWindow.setHeight(height);
            this.popwidth = width;
            this.popheight = height;

            popupWindow.setContentView(contentView);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(false);


            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (GlobalToolsType.global_type != ToolsType.defaule) {
                        return false;
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            orgX = (int) event.getX();
                            orgY = (int) event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            offsetX = (int) event.getRawX() - orgX;
                            offsetY = (int) event.getRawY() - orgY;

                            int popupWindowHeight = popupWindow.getHeight();
                            int popupWindowWidth = popupWindow.getWidth();
                            if (popupWindowHeight <= 0) {
                                popupWindow.getContentView().measure(0, 0);
                                popupWindowHeight = popupWindow.getContentView().getMeasuredHeight();
                            }
                            if (popupWindowWidth <= 0) {
                                popupWindow.getContentView().measure(0, 0);
                                popupWindowWidth = popupWindow.getContentView().getMeasuredWidth();
                            }

                            int[] location = new int[2];
                            rootView.getLocationOnScreen(location);

                            int topEdge = location[1];
                            int bottomEdge = topEdge + rootView.getMeasuredHeight() - popupWindowHeight;

                            int rightEdge = 0;
                            int leftEdge = 0;
                            if (huawei || oppo || voio) {
                                leftEdge = ScreenScale.getStatusBarHeight();
                                rightEdge = rootView.getMeasuredWidth() - popupWindowWidth + ScreenScale.getStatusBarHeight();
                            } else {
                                rightEdge = rootView.getMeasuredWidth() - popupWindowWidth;
                            }

                            if (offsetY < topEdge) {
                                offsetY = topEdge;
                            }

                            if (offsetY >= bottomEdge) {
                                offsetY = bottomEdge;
                            }

                            if (offsetX >= rightEdge) {
                                offsetX = rightEdge;
                            }

                            if (offsetX <= leftEdge) {
                                offsetX = leftEdge;
                            }

                            popupWindow.update(offsetX, offsetY, -1, -1, true);
                            break;
                        case MotionEvent.ACTION_UP:
                            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                                sendXY(event.getRawX(), event.getRawY(), event.getX(), event.getY());
                            }
                            break;
                    }
                    return true;
                }
            });
            //拖动监听


            int[] location = new int[2]; //0 300
            view.getLocationInWindow(location);

            int x = (view.getWidth() - popupWindow.getWidth()) / 2 + location[0];
            //居中向上偏移15
            int y = (view.getHeight() - popupWindow.getHeight()) / 2 + location[1];
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
            setvisibilityTop(visibilityTop);

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

    public void dismissScreen() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    /**
     * 顶层画布显示或隐藏
     */
    public void setvisibilityTop(boolean visibilityTop) {
        if (popupWindow != null && contentView != null) {
            if (visibilityTop) {
                mScreenPaintTop.setVisibility(View.VISIBLE);
            } else {
                mScreenPaintTop.setVisibility(View.GONE);
            }
        }
    }

    public Bitmap getBitMapImage(String file) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        BitmapFactory.decodeFile(file, opts);
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;

        int nScale = imageHeight * imageWidth / (1920 * 1080);

        if (nScale > 1) {
            opts.inSampleSize = nScale;
        }

        opts.inPreferredConfig = Bitmap.Config.RGB_565;

        opts.inJustDecodeBounds = false;

        if (btImage != null) {
            btImage.recycle();
            System.gc();
            btImage = null;
        }

        btImage = BitmapFactory.decodeFile(file, opts);

        if (btImage != null) {
            return btImage;
        }
        return null;
    }

    /**
     * 更新pop大小
     *
     * @param scale 係數
     */
    public void UpdatePopSize(double scale) {
        if (popupWindow != null) {
            double w = popwidth * 1.0 * scale;
            double h = popheight * 1.0 * scale;
            if (w > rootView.getWidth()) {
                w = rootView.getWidth();
                double dbscale = w / popwidth;
                h = dbscale * popheight;
            }
            if (h > rootView.getHeight()) {
                h = rootView.getHeight();
                double dbscale = h / popheight;
                w = dbscale * popwidth;
            }
            popupWindow.update((int) w, (int) h);
            if (mScreenPaint != null) {
                mScreenPaint.setDrawSize(popwidth, popheight);
                mScreenPaint.LargeOrSmallView((float) scale);
            }
            if (mScreenPaintTop != null) {
                mScreenPaintTop.setDrawSize(popwidth, popheight);
                mScreenPaintTop.LargeOrSmallView((float) scale);
            }
        }
    }

    /**
     * PC拖动响应
     *
     * @param rootView
     * @param moveX
     * @param movieY
     * @param isHaiping
     */
    public void movePopupWindow(View rootView, double moveX, double movieY, boolean isHaiping) {
        if (rootView == null) {
            return;
        }
        int popupWindowHeight = popupWindow.getHeight();
        int popupWindowWidth = popupWindow.getWidth();
        if (popupWindowHeight <= 0 || popupWindowWidth <= 0) {
            popupWindow.getContentView().measure(0, 0);
            popupWindowHeight = popupWindow.getContentView().getMeasuredHeight();
            popupWindowWidth = popupWindow.getContentView().getMeasuredWidth();
        }

        int[] location = new int[2];
        rootView.getLocationInWindow(location);
        int x = 0;
        if (huawei || oppo || voio) {
            x = location[0] - ScreenScale.getStatusBarHeight();
        } else {
            x = location[0];
        }
        int y = location[1];
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        int pw = width - popupWindow.getWidth();
        int ph = height - popupWindowHeight;
        int offsetX = (int) (x + pw * moveX);
        int offsetY = (int) (y + ph * movieY);

        if (offsetX >= width - popupWindowWidth) {
            if (huawei || oppo || voio) {
                offsetX = width - popupWindowWidth + ScreenScale.getStatusBarHeight();
            } else {
                offsetX = width - popupWindowWidth;
            }
        }

        if (huawei || oppo || voio) {
            if (offsetX == 0) {
                offsetX = ScreenScale.getStatusBarHeight();
            }
        }

        if (offsetY >= ScreenScale.getScreenHeight() - popupWindowHeight) {
            offsetY = ScreenScale.getScreenHeight() - popupWindowHeight;
        }

        if (popupWindow.isShowing()) {
            popupWindow.update(offsetX, offsetY, -1, -1, true);
        }
    }


    public void sendXY(float rawX, float rawY, float x1, float y1) {
        int[] location = new int[2];
        rootView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        int pw = width - popupWindow.getWidth();
        int ph = height - popupWindow.getHeight();
        double percentLeft = (rawX - x - x1) / pw;
        double percentTop = (rawY - y - y1) / ph;

        try {
            JSONObject data = new JSONObject();
            data.put("id", "captureImg_" + mCaptureImg.getCaptureImgInfo().getFileid());

            JSONObject position = new JSONObject();
            position.put("percentLeft", percentLeft);
            position.put("percentTop", percentTop);
            position.put("isDrag", true);
            data.put("position", position);
            TKRoomManager.getInstance().pubMsg("CaptureImgDrag", "CaptureImgDrag_" + mCaptureImg.getCaptureImgInfo().getFileid(), "__allExceptSender", data.toString(), false, "CaptureImg_" + mCaptureImg.getCaptureImgInfo().getFileid(), null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showTextInput(float x, float y, int textSize, int textColor) {
        paintPadLocationEditText = new EditText(mContext);
        paintPadLocationEditText.setTextColor(textColor);
        paintPadLocationEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        paintPadLocationEditText.setPadding(0, 0, 0, 0);
        paintPadLocationEditText.setBackground(mContext.getResources().getDrawable(com.classroomsdk.R.drawable.tk_paintpad_ed_bg));
        paintPadLocationEditText.setMaxWidth((int) (popwidth - x));
        paintPadLocationEditText.setMinWidth(30);
        paintPadLocationEditText.setCursorVisible(false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = (int) x;
        layoutParams.topMargin = (int) y;
        paintPadLocationEditText.setLayoutParams(layoutParams);
        fl_layout.addView(paintPadLocationEditText);
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
            fl_layout.removeView(paintPadLocationEditText);
            paintPadLocationEditText = null;
        }
    }

    public void setHideDraw(boolean isHideDraw) {
        mScreenPaint.setHideDraw(isHideDraw);
        mScreenPaintTop.setHideDraw(isHideDraw);
    }
}
