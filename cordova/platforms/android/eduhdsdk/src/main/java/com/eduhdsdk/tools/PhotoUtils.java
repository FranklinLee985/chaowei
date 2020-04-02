package com.eduhdsdk.tools;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.utils.UploadFile;
import com.eduhdsdk.R;
import com.eduhdsdk.room.RoomSession;
import com.eduhdsdk.ui.TKBaseActivity;
import com.talkcloud.room.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/20/020.
 */

public class PhotoUtils {

    public static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    public static final int ALBUM_IMAGE = 2; //相册
    public static File tempFile;
    public static Uri imageUri;

    public static void openCamera(Activity activity) {
        int currentapiVersion = Build.VERSION.SDK_INT;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (PersonInfo_ImageUtils.hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    public static void openAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, PhotoUtils.ALBUM_IMAGE);
    }

    //上传拍照图片
    public static void uploadCaremaImage(Uri uri, Activity activity, int resultCode, Intent data) {
        if (resultCode == activity.RESULT_CANCELED) {
            return;
        }
        if (resultCode == PHOTO_REQUEST_CAREMA) {
            if (data != null) {
                uri = data.getData();
            } else {
                uri = PhotoUtils.imageUri;
            }
            if (!TextUtils.isEmpty(uri.toString())) {
                try {
                    String path = PersonInfo_ImageUtils.scaleAndSaveImage(PersonInfo_ImageUtils.getRealFilePath(activity,
                            PersonInfo_ImageUtils.getFileUri(uri, activity)), 800, 800, activity);
                    String serial = TKRoomManager.getInstance().getRoomProperties().getString("serial");
                    WhiteBoradConfig.getsInstance().uploadRoomFile(serial, path, RoomSession.isClassBegin, 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //选择上传图片
    public static void uploadAlbumImage(Activity activity, Intent data, int selectImageType) {
        String imagePath = null;
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                imagePath = PersonInfo_ImageUtils.getImageAfterKitKat(data, activity);
            } else {
                imagePath = PersonInfo_ImageUtils.getImageBeforeKitKat(data, activity);
            }
            if (!TextUtils.isEmpty(imagePath)) {
                String path = PersonInfo_ImageUtils.scaleAndSaveImage(imagePath, 800, 800, activity);
                String serial = TKRoomManager.getInstance().getRoomProperties().getString("serial");
                if (selectImageType == TKBaseActivity.CHAT_SELECT_IMAGE) {
                    uploadImage(path, serial);
                } else {
                    WhiteBoradConfig.getsInstance().uploadRoomFile(serial, path, RoomSession.isClassBegin, 1);
                }
            } else {
                Toast.makeText(activity, activity.getString(R.string.tips_image), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void uploadImage(String path, String roomID) {
        String url = "http://" + WhiteBoradConfig.getsInstance().getFileServierUrl() + ":" +
                WhiteBoradConfig.getsInstance().getFileServierPort() + "/ClientAPI/" + "uploaddocument";
        UploadFile uf = new UploadFile();
        uf.UploadOperation(url);
        uf.packageFile(path, roomID, TKRoomManager.getInstance().getMySelf().peerId,
                TKRoomManager.getInstance().getMySelf().nickName, 0);
        uf.start();
        uf.setOnUploadListener(new UploadFile.onUploadListener() {
            @Override
            public void onUploadSuccess(JSONObject object) {
                String msg = object.optString("swfpath");
                String cospath = object.optString("cospath");

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(System.currentTimeMillis()));
                String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("type", 0);
                msgMap.put("time", time);
                msgMap.put("msgtype", "onlyimg");
                msgMap.put("cospath", cospath);
                TKRoomManager.getInstance().sendMessage(msg, "__all", msgMap);
            }
        });
    }
}
