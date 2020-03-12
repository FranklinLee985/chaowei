package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.eduhdsdk.R;
import com.eduhdsdk.tools.PhotoUtils;
import com.eduhdsdk.ui.OneToOneActivity;
import com.talkcloud.room.TKRoomManager;

/**
 * 上传图片的popupwindow
 * Created by YF on 2018/12/25 0025.
 */

public class UploadPhotoPopupWindowUtils {

    private PopupWindow popupWindowPhoto;
    private static UploadPhotoPopupWindowUtils windowUtils;
    private UploadPhotoPopupWindowClick click;

    public static UploadPhotoPopupWindowUtils getInstance() {
        synchronized (UploadPhotoPopupWindowUtils.class) {
            if (windowUtils == null) {
                windowUtils = new UploadPhotoPopupWindowUtils();
            }
            return windowUtils;
        }
    }

    public void resetInstance() {
        windowUtils = null;
    }

    public void showPopupWindow(final Activity activity, final CheckBox cb_choose_photo, final UploadPhotoPopupWindowClick click) {
        if (click != null) {
            this.click = click;
        }
        View contentView = LayoutInflater.from(activity).inflate(R.layout.tk_popup_photo_control, null);
        LinearLayout ll_popu_camera = (LinearLayout) contentView.findViewById(R.id.ll_popu_camera);
        LinearLayout ll_popu_selectphoto = (LinearLayout) contentView.findViewById(R.id.ll_popu_selectphoto);
        if (popupWindowPhoto == null) {
            popupWindowPhoto = new PopupWindow(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        popupWindowPhoto.setContentView(contentView);
        ll_popu_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click != null) {
                    click.cameraClickListener();
                }
                popupWindowPhoto.dismiss();
            }
        });
        ll_popu_selectphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click != null) {
                    click.photoClickListener();
                }
                popupWindowPhoto.dismiss();
            }
        });
        popupWindowPhoto.setBackgroundDrawable(new BitmapDrawable());
        popupWindowPhoto.setFocusable(true);
        popupWindowPhoto.setOutsideTouchable(true);

        int[] width_and_height = new int[2];
        cb_choose_photo.getLocationInWindow(width_and_height);

        popupWindowPhoto.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (cb_choose_photo != null) {
                    cb_choose_photo.setChecked(false);
                }
            }
        });

        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xPos = cb_choose_photo.getWidth() / 2 - contentView.getMeasuredWidth() / 2;
        popupWindowPhoto.showAsDropDown(cb_choose_photo, xPos, 0, Gravity.BOTTOM);
    }

    public void setDismiss() {
        if (popupWindowPhoto != null) {
            if (popupWindowPhoto.isShowing()) {
                popupWindowPhoto.dismiss();
            }
        }
    }

    /**
     * 点击事件的接口
     */
    public interface UploadPhotoPopupWindowClick {
        /**
         * 打开相机
         */
        void cameraClickListener();

        /**
         * 打开相册
         */
        void photoClickListener();
    }
}
