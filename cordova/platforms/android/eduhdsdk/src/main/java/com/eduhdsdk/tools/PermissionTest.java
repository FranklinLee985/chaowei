package com.eduhdsdk.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.tkwebrtc.voiceengine.WebRtcAudioUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/20/020.
 */

public class PermissionTest {

    /**
     * 判断相机
     *
     * @param context
     * @return
     */
    public static boolean isCameraUseable(Context context) {
        boolean hasCamera = false;
        PackageManager pm = context.getPackageManager();
        hasCamera = pm.hasSystemFeature("android.hardware.camera") || pm.hasSystemFeature("android.hardware.camera.front") || Build.VERSION.SDK_INT < 9 || Camera.getNumberOfCameras() > 0;
        if (!hasCamera) {
            return hasCamera;
        } else {
            hasCamera = canOpenCamera();
            return hasCamera;
        }
    }


    private static boolean canOpenCamera() {
        boolean has = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open(0);
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception var3) {
            has = false;
        }
        if (mCamera != null) {
            mCamera.setPreviewCallback((Camera.PreviewCallback) null);
            mCamera.release();
            mCamera = null;
        }
        return has;
    }

    /**
     * 音频检测
     *
     * @param context
     * @return true 有
     */
    public static boolean checkAudioPermission(Context context) {
        boolean has = false;
        if (Build.BRAND.equals("SMARTISAN")) {
            has = smartisanRecord();
        } else if (Build.VERSION.SDK_INT < 21) {
            has = WebRtcAudioUtils.hasPermission(context, "android.permission.RECORD_AUDIO");
        } else if (ContextCompat.checkSelfPermission(context, "android.permission.RECORD_AUDIO") != 0) {
            has = false;
        } else {
            has = true;
        }

        return has;
    }

    private static boolean smartisanRecord() {
        int audioSource = MediaRecorder.AudioSource.MIC;
        int sampleRateInHz = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSizeInBytes = 0;

        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try {
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }

        //            if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
        //                //如果短时间内频繁检测，会造成audioRecord还未销毁完成，此时检测会返回RECORDSTATE_STOPPED状态，再去read，会读到0的size，所以此时默认权限通过
        //                return true;
        //            }

        byte[] bytes = new byte[1024];
        int readSize = audioRecord.read(bytes, 0, 1024);
        if (readSize == AudioRecord.ERROR_INVALID_OPERATION || readSize <= 0) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        return true;
    }


    public static final int STATE_RECORDING = -1;
    public static final int STATE_NO_PERMISSION = -2;
    public static final int STATE_SUCCESS = 1;

    /**
     * 用于检测录音权限是禁用还是允许状态
     *
     * @return 返回1表示权限是允许状态，返回-2表示权限是禁用状态
     * @author ZhuJian
     */
    public static int getRecordState() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int minBuffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 100));
            short[] point = new short[minBuffer];
            int readSize = 0;
            try {
                audioRecord.startRecording();//检测是否可以进入初始化状态
            } catch (Exception e) {
                if (audioRecord != null) {
                    audioRecord.release();
                    audioRecord = null;
                }
                return STATE_NO_PERMISSION;
            }
            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
             //6.0以下机型都会返回状态，故使用时需要判断bulid版本
              //检测是否在录音中
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
                return STATE_RECORDING;
            } else {//检测是否可以获取录音结果
                readSize = audioRecord.read(point, 0, point.length);
                if (readSize <= 0) {
                    if (audioRecord != null) {
                        audioRecord.stop();
                        audioRecord.release();
                        audioRecord = null;
                    }
                    return STATE_NO_PERMISSION;
                } else {
                    if (audioRecord != null) {
                        audioRecord.stop();
                        audioRecord.release();
                        audioRecord = null;
                    }
                    return STATE_SUCCESS;
                }
            }
        } else {
            return STATE_SUCCESS;
        }
    }

    public static void requestPermission(Activity context, int requestCode) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.READ_PHONE_STATE};
        List<String> mPermissionList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permission);
                }
            }
            if (mPermissionList.size() > 0) {
                ActivityCompat.requestPermissions(context, mPermissionList.toArray(new String[mPermissionList.size()]), requestCode);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Camera mCamera = null;
                try {
                    mCamera = Camera.open(0);
                    Camera.Parameters mParameters = mCamera.getParameters();
                    mCamera.setParameters(mParameters);
                    mCamera.startPreview();
                } catch (Exception var3) {
                    var3.printStackTrace();
                }
                if (mCamera != null) {
                    mCamera.setPreviewCallback((Camera.PreviewCallback) null);
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
            }
        }
    }
}
