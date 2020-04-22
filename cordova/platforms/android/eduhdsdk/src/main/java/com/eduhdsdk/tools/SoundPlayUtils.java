package com.eduhdsdk.tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.classroomsdk.http.DownLoadCallBack;
import com.classroomsdk.http.HttpHelp;
import com.eduhdsdk.BuildVars;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.Trophy;
import com.eduhdsdk.room.RoomInfo;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/11/3.
 */

public class SoundPlayUtils {

    // SoundPool对象
    public static SoundPool mSoundPlayer;
    public static SoundPlayUtils soundPlayUtils;
    static String mHost;
    static int mPort;
    static String MP3File;
    static List<Trophy> trophyList;
    static int postion = 0;

    static List<String> mp3Urls = new ArrayList<>();


    //根据文件的名字记录在load中的position位置
    static Map<String, Integer> position_data = new HashMap<String, Integer>();


    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }
        if (mSoundPlayer == null) {
            mSoundPlayer = new SoundPool(10, AudioManager.STREAM_VOICE_CALL, 0);
        }
        /*mSoundPlayer.unload(1);*/
       /* mSoundPlayer.release();*/
        if ("null".equals(RoomInfo.getInstance().get_MP3Url()) ||TextUtils.isEmpty(RoomInfo.getInstance().get_MP3Url())) {
            mSoundPlayer.load(context.getApplicationContext(), R.raw.tk_trophy_tones, 1);
        } else {
            mSoundPlayer.load(MP3File, 1);
        }
        return soundPlayUtils;
    }

    public static void release() {
        if (soundPlayUtils != null && mSoundPlayer != null) {
            mSoundPlayer.release();
            mSoundPlayer = null;
            soundPlayUtils = null;
            mp3Urls.clear();
            deleteFile(MP3File);
        }
    }

    /**
     * 播放声音
     */
    public static void play(String file_name) {
        if (mSoundPlayer != null) {
            if (mp3Urls.size() > 0 && position_data.containsKey(file_name)) {
                int a = position_data.get(file_name);
                if (a > 0) {
                    mSoundPlayer.play(a, 1, 1, 0, 0, 1);
                }
            } else {
                mSoundPlayer.play(1, 1, 1, 0, 0, 1);
            }
        }
    }

    public static void loadMP3(String host, int port, final Context context) {

        mHost = host;
        mPort = port;

        MP3File = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cupRingtone" +
                RoomInfo.getInstance().get_MP3Url().substring(RoomInfo.getInstance().get_MP3Url().lastIndexOf("."));

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "SD卡没有使用权限", Toast.LENGTH_SHORT).show();
        }

        final File file = new File(MP3File);
        if (file.exists()) {
            file.delete();
        }

        if (!"null".equals(RoomInfo.getInstance().get_MP3Url()) && !TextUtils.isEmpty(RoomInfo.getInstance().get_MP3Url()) && !TextUtils.isEmpty(host)) {
            String url = BuildVars.REQUEST_HEADER + mHost + ":" + mPort + RoomInfo.getInstance().get_MP3Url();
            String[] allowedContentTypes = new String[]{".*"};
            HttpHelp.getInstance().downLoad(url, allowedContentTypes, new DownLoadCallBack() {
                @Override
                public void onSuccess(int statusCode, byte[] bytes) {
                    if (statusCode == 200) {
                        BufferedOutputStream bos = null;
                        FileOutputStream fos = null;
                        java.io.File localFile = null;
                        try {
                            localFile = new File(MP3File);
                            if (!localFile.exists()) {
                                localFile.getParentFile().mkdirs();
                            }
                            fos = new FileOutputStream(localFile);
                            bos = new BufferedOutputStream(fos);
                            bos.write(bytes);
                            init(context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (bos != null) {
                                try {
                                    bos.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, byte[] bytes, Throwable throwable) {

                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {

                }
            });

        }
    }

    public static void loadTrophy(String host, int port, Context context) {

        mHost = host;
        mPort = port;
        postion = 0;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "SD卡没有使用权限", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(host) || RoomInfo.getInstance().getTrophyList() == null
                || RoomInfo.getInstance().getTrophyList().size() == 0) {
            return;
        }
        trophyList = RoomInfo.getInstance().getTrophyList();
        downMP3File(postion);
    }

    public static void initSoundPlay() {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }
        if (mSoundPlayer == null) {
            mSoundPlayer = new SoundPool(10, AudioManager.STREAM_VOICE_CALL, 0);
        }
        /*mSoundPlayer.release();*/
        if (mp3Urls.size() > 0) {

            position_data.clear();
            for (int x = 0; x < mp3Urls.size(); x++) {
                if (!TextUtils.isEmpty(mp3Urls.get(x))) {
                    int location = x + 1;
                    String[] data = null;
                    if (mp3Urls.get(x).contains("upload1")) {
                        data = mp3Urls.get(x).split("upload1");
                        if (data != null && data.length > 1) {
                            position_data.put(("/upload1" + data[1]), location);
                        }
                    } else if (mp3Urls.get(x).contains("upload0")) {
                        data = mp3Urls.get(x).split("upload0");
                        if (data != null && data.length > 1) {
                            position_data.put(("/upload0" + data[1]), location);
                        }
                    } else if (mp3Urls.get(x).contains("cospath")) {
                        data = mp3Urls.get(x).split("cospath");
                        if (data != null && data.length > 1) {
                            position_data.put(("/cospath" + data[1]), location);
                        }
                    }

                   /* String[] data = mp3Urls.get(x).split("upload1");*/
                   /* if (data.length == 1) {
                        position_data.put(("/upload1" + data[0]), location);
                    } else {
                        position_data.put(("/upload1" + data[1]), location);
                    }*/
                   /* position_data.put(("/upload1" + data[1]), location);*/
                    mSoundPlayer.load(mp3Urls.get(x), location);
                }
            }
        }
    }

    public static void downMP3File(final int index) {

        if (index > trophyList.size() - 1) {
            if (mp3Urls.size() > 0) {
                initSoundPlay();
            }
            return;
        }

        final String MP3File = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                "Trophyvoice" + "/" + index + trophyList.get(index).getTrophyvoice();
        String existMP3File = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                "Trophyvoice" + "/" + index + "/upload1";

        deleteDirectory(existMP3File);

        final File file = new File(MP3File);
        if (file.exists()) {
            file.delete();
        }

        String mp3Url = BuildVars.REQUEST_HEADER + mHost + ":" + mPort + trophyList.get(index).getTrophyvoice();

//        String[] allowedContentTypes = new String[] {"audio/x-wav","application/octet-stream", "image/jpeg", "image/png", "image/gif"};
        String[] allowedContentTypes = new String[]{".*"};
        HttpHelp.getInstance().downLoad(mp3Url, allowedContentTypes, new DownLoadCallBack() {
            @Override
            public void onSuccess(int statusCode, byte[] bytes) {
                if (statusCode == 200) {
                    BufferedOutputStream bos = null;
                    FileOutputStream fos = null;
                    java.io.File localFile = null;
                    try {
                        localFile = new File(MP3File);
                        if (!localFile.exists()) {
                            localFile.getParentFile().mkdirs();
                        }
                        fos = new FileOutputStream(localFile);
                        bos = new BufferedOutputStream(fos);
                        bos.write(bytes);
                        mp3Urls.add(MP3File);
                        downTrophyimg(index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (bos != null) {
                            try {
                                bos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, byte[] bytes, Throwable throwable) {
                downMP3File(index);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });

    }


    public static void downTrophyimg(final int index) {

        if (trophyList.size() < index || trophyList.size() == 0) {
            return;
        }

        final String imgFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                "Trophyimg" + "/" + index + trophyList.get(index).getTrophyimg();

        String existMP3File = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                "Trophyimg" + "/" + index + "/upload1";

        deleteDirectory(existMP3File);

        final File file = new File(imgFile);
        if (file.exists()) {
            file.delete();
        }

        String imgUrl = BuildVars.REQUEST_HEADER + mHost + ":" + mPort + trophyList.get(index).getTrophyimg();
        String[] allowedContentTypes = new String[]{".*"};
        HttpHelp.getInstance().downLoad(imgUrl, allowedContentTypes, new DownLoadCallBack() {
            @Override
            public void onSuccess(int statusCode, byte[] bytes) {
                if (statusCode == 200) {
                    BufferedOutputStream bos = null;
                    FileOutputStream fos = null;
                    java.io.File localFile = null;
                    try {
                        localFile = new File(imgFile);
                        if (!localFile.exists()) {
                            localFile.getParentFile().mkdirs();
                        }
                        fos = new FileOutputStream(localFile);
                        bos = new BufferedOutputStream(fos);
                        bos.write(bytes);
                        downTrophyIcon(index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (bos != null) {
                            try {
                                bos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, byte[] bytes, Throwable throwable) {
                downTrophyimg(index);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });

    }

    public static void downTrophyIcon(final int index) {

        if (trophyList.size() < index || trophyList.size() == 0) {
            return;
        }

        final String iconFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                "TrophyIcon" + "/" + index + trophyList.get(index).getTrophyIcon();

        String existMP3File = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                "TrophyIcon" + "/" + index + "/upload1";

        deleteDirectory(existMP3File);

        final File file = new File(iconFile);
        if (file.exists()) {
            file.delete();
        }

        String iconUrl = BuildVars.REQUEST_HEADER + mHost + ":" + mPort + trophyList.get(index).getTrophyIcon();
        String[] allowedContentTypes = new String[]{".*"};
        HttpHelp.getInstance().downLoad(iconUrl, allowedContentTypes, new DownLoadCallBack() {
            @Override
            public void onSuccess(int statusCode, byte[] bytes) {
                if (statusCode == 200) {
                    BufferedOutputStream bos = null;
                    FileOutputStream fos = null;
                    java.io.File localFile = null;
                    try {
                        localFile = new File(iconFile);
                        if (!localFile.exists()) {
                            localFile.getParentFile().mkdirs();
                        }
                        fos = new FileOutputStream(localFile);
                        bos = new BufferedOutputStream(fos);
                        bos.write(bytes);
                        postion++;
                        downMP3File(postion);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (bos != null) {
                            try {
                                bos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, byte[] bytes, Throwable throwable) {
                downTrophyIcon(index);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });

    }

    public static void releaseTrophy() {
        if (soundPlayUtils != null && mSoundPlayer != null) {
            mSoundPlayer.release();
            mSoundPlayer = null;
            soundPlayUtils = null;
            mp3Urls.clear();

            if (RoomInfo.getInstance().getTrophyList() == null || RoomInfo.getInstance().getTrophyList().size() == 0) {
                return;
            }

            for (int x = 0; x < RoomInfo.getInstance().getTrophyList().size(); x++) {

                String MP3File = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        "Trophyvoice" + x + trophyList.get(x).getTrophyvoice();
                String imgFile = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + "Trophyimg" + x + trophyList.get(x).getTrophyimg();
                String iconFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        "TrophyIcon" + x + trophyList.get(x).getTrophyIcon();

                deleteFile(MP3File);
                deleteFile(imgFile);
                deleteFile(iconFile);
            }
        }
    }

    public static void deleteFile(String fileName) {
        if (fileName != null && !TextUtils.isEmpty(fileName)) {
            File file = new File(fileName);
            if (file != null && file.isFile() && file.exists()) {
                file.delete();
            }
        }
    }

    public static boolean deleteFileSuc(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                // 删除子文件
                if (files[i].isFile()) {
                    flag = deleteFileSuc(files[i].getAbsolutePath());
                    if (!flag)
                        break;
                }
                // 删除子目录
                else if (files[i].isDirectory()) {
                    flag = deleteDirectory(files[i]
                            .getAbsolutePath());
                    if (!flag)
                        break;
                }
            }
        }

        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
}
