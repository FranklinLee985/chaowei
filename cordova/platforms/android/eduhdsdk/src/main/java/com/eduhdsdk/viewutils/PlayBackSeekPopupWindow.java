package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.classroomsdk.manage.WhiteBoradConfig;
import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;
import com.eduhdsdk.tools.Tools;
import com.talkcloud.room.TKPlayBackManager;

import skin.support.annotation.Skinable;

/**
 * 回放进度条
 */
@Skinable
public class PlayBackSeekPopupWindow {

    private PopupWindow popupWindow;

    //******   回放进度控制layout
    public LinearLayout rel_play_back;
    //播放暂停图标
    public ImageView img_play_back;
    //播放回放进度条
    public SeekBar sek_play_back;
    //播放时长 / 总时长
    public TextView txt_play_back_time;

    private PlaybackControlUtils playbackControlUtils;//回放进度条的显示隐藏工具类
    private double postionPlayBack = 0.0;
    private boolean isPlayBackPlay = true;
    private boolean isEnd = false;
    private long currenttime;
    private long connectLostCurrenttime;
    //回放开始时间和结束时间
    public long startPlayBackTime, endPlayBackTime;

    private Activity mActivity;
    public View contentView;
    View mRootView;
    int ph;
    private PlayBackListener mPlayBackListener;

    public PlayBackSeekPopupWindow(Activity activity, View rootView) {
        this.mActivity = activity;
        this.mRootView = rootView;
        initPopupWindow();
    }

    /**
     * 初始化popupwindow
     */
    public void initPopupWindow() {
        contentView = LayoutInflater.from(mActivity).inflate(R.layout.tk_layout_play_back_seek, null);

        //******   回放进度控制layout
        rel_play_back = (LinearLayout) contentView.findViewById(R.id.rel_play_back);
        //播放暂停图标
        img_play_back = (ImageView) rel_play_back.findViewById(R.id.img_play_back);
        //播放回放进度条
        sek_play_back = (SeekBar) rel_play_back.findViewById(R.id.sek_play_back);
        //播放时长 / 总时长
        txt_play_back_time = (TextView) rel_play_back.findViewById(R.id.txt_play_back_time);

        //回放相关监听
        bindPlayBackListener();

        if (popupWindow == null) {
            popupWindow = new PopupWindow(ScreenScale.getScreenWidth() - 80, 110);
        }
        popupWindow.setContentView(contentView);
        // 设置PopupWindow的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        popupWindow.setOutsideTouchable(false);
        // 设置PopupWindow是否能响应点击事件
        popupWindow.setTouchable(true);

        int[] location = new int[2];
        mRootView.getLocationOnScreen(location);
        ph = -popupWindow.getHeight();

    }

    public void connectLost() {
        playbackControlUtils.connectLost();
        connectLostCurrenttime = currenttime;
    }

    /**
     * 回放相关监听
     */
    private void bindPlayBackListener() {
        rel_play_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                playbackControlUtils.startHideTimer(rel_play_back);
                return true;
            }
        });

        sek_play_back.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            boolean isfromUser = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    this.progress = progress;
                    isfromUser = fromUser;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                playbackControlUtils.startHideTimer(rel_play_back);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playbackControlUtils.startHideTimer(rel_play_back);
                postionPlayBack = progress / 100;
                long pos = (long) (((double) progress / 100) * (endPlayBackTime - startPlayBackTime) + startPlayBackTime);

                img_play_back.setImageResource(R.drawable.tk_btn_pause_normal);
                TKPlayBackManager.getInstance().seekPlayback(pos);
                TKPlayBackManager.getInstance().resumePlayBack();
                isPlayBackPlay = true;

                isEnd = false;
            }
        });

        img_play_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayBackPlay) {
                    playbackControlUtils.startHideTimer(rel_play_back);
                    TKPlayBackManager.getInstance().pausePlayback();
                    img_play_back.setImageResource(R.drawable.tk_btn_play_normal);
                } else {
                    playbackControlUtils.startHideTimer(rel_play_back);
                    if (isEnd) {
                        TKPlayBackManager.getInstance().seekPlayback(startPlayBackTime);
                        TKPlayBackManager.getInstance().resumePlayBack();
                        currenttime = startPlayBackTime;
                        img_play_back.setImageResource(R.drawable.tk_btn_pause_normal);
                        isEnd = false;
                    } else {
                        TKPlayBackManager.getInstance().resumePlayBack();
                        img_play_back.setImageResource(R.drawable.tk_btn_pause_normal);
                    }
                }
                isPlayBackPlay = !isPlayBackPlay;
                if (mPlayBackListener != null) {
                    mPlayBackListener.playingState(isPlayBackPlay);
                }
                WhiteBoradConfig.getsInstance().playbackPlayAndPauseController(isPlayBackPlay);
            }
        });

    }

    public void startTimer(PlaybackControlUtils controlUtils) {
        //回放进度条的显示隐藏工具类,初始化
        playbackControlUtils = controlUtils;

        playbackControlUtils.startHideTimer(rel_play_back);
        showPopupWindow();
    }

    public void onPlayBackEnd() {
        postionPlayBack = 0.0;
        img_play_back.setImageResource(R.drawable.tk_btn_play_normal);
        sek_play_back.setProgress(0);
        isPlayBackPlay = false;
        isEnd = true;
    }

    /***
     *    回放起止时间回调
     * @param startTime     开始时间
     * @param endTime       结束时间
     */
    public void onPlayBackDuration(long startTime, long endTime) {
        this.startPlayBackTime = startTime;
        this.endPlayBackTime = endTime;
        if (connectLostCurrenttime != 0) {
            TKPlayBackManager.getInstance().seekPlayback(connectLostCurrenttime);
            TKPlayBackManager.getInstance().resumePlayBack();
        }
    }

    public void onPlayBackUpdateTime(long backTimePos) {
        this.currenttime = backTimePos;
        double pos = (double) (currenttime - startPlayBackTime) / (double) (endPlayBackTime - startPlayBackTime);
        if (pos < postionPlayBack) {
            pos = postionPlayBack;
            long postionTime = (long) (((double) postionPlayBack) * (endPlayBackTime - startPlayBackTime) + startPlayBackTime);
            TKPlayBackManager.getInstance().seekPlayback(postionTime);
        } else {
            postionPlayBack = pos;
        }
        sek_play_back.setProgress((int) (pos * 100));

        String strcur = Tools.secToTime(currenttime - startPlayBackTime);
        String strda = Tools.secToTime(endPlayBackTime - startPlayBackTime);
        txt_play_back_time.setText(strcur + "/" + strda);
    }

    public void onPlayBackClearAll() {
        postionPlayBack = 0.0;

    }

    public void showPopupWindow() {
        if (mActivity != null && !mActivity.isFinishing() && popupWindow != null && !mActivity.getFragmentManager().isDestroyed()) {
            popupWindow.showAtLocation(mRootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ph);
        }
    }

    public void dismiss() {
        if (mActivity != null && !mActivity.isFinishing() && popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public void setPlayBackListener(PlayBackListener listener) {
        this.mPlayBackListener = listener;
    }

    public interface PlayBackListener {
        void playingState(boolean state);
    }

}
