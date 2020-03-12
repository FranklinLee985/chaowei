package com.classroomsdk.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.classroomsdk.R;

/**
 * 播放声音的工具类
 * Created by YF on 2019/1/10 0010.
 */

public class SoundPlayUtils {
    private static SoundPlayUtils instance;

    // SoundPool对象
    public static SoundPool mSoundPlayer;

    public static synchronized SoundPlayUtils getInstance() {
        if (instance == null) {
            instance = new SoundPlayUtils();
        }
        return instance;
    }

    public void resetInstance() {
        mSoundPlayer = null;
        instance = null;
    }

    public void init(Context context) {
        if (mSoundPlayer == null) {
            mSoundPlayer = new SoundPool(10, AudioManager.STREAM_VOICE_CALL, 0);
        }
        mSoundPlayer.load(context.getApplicationContext(), R.raw.tk_timer_default, 1);
    }

    /**
     * 播放声音
     */
    public void play() {
        if (mSoundPlayer != null) {
            mSoundPlayer.play(1, 1, 1, 0, 0, 1);
        }
    }
}
