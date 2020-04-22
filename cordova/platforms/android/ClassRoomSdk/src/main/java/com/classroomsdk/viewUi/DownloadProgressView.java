package com.classroomsdk.viewUi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.classroomsdk.R;
import com.classroomsdk.tools.ScreenScale;

import java.util.Locale;

/**
 * Created by zhe on 2019-04-26.
 * 下载进度条
 */
public class DownloadProgressView extends FrameLayout {

    private Context mContext;

    private ProgressBar mDownloadProgress;
    private TextView mDownloadText;
    private Button mJumpOver;
    private JumpOverClieck jumpOverClieck;

    public DownloadProgressView(@NonNull Context context) {
        this(context, null);
    }

    public DownloadProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        View mRootView = LayoutInflater.from(mContext).inflate(R.layout.tk_download_progress_view, null);
        mDownloadProgress = mRootView.findViewById(R.id.tk_dp_progress);
        mDownloadText = mRootView.findViewById(R.id.tk_dp_text);
        mJumpOver = mRootView.findViewById(R.id.but_jump_over);
        mJumpOver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jumpOverClieck != null) {
                    jumpOverClieck.jumpover();
                }
            }
        });
        ScreenScale.scaleView(mRootView, "DownloadProgressView");
        addView(mRootView);
    }

    public synchronized void setProgress(String text, int progress) {
        mDownloadText.setText(String.format(Locale.getDefault(), "%s  %d%%", text, progress));
        mDownloadProgress.setProgress(progress);
    }

    public void setJumpOverClieck(JumpOverClieck jumpOverClieck) {
        this.jumpOverClieck = jumpOverClieck;
    }

    public interface JumpOverClieck {
        void jumpover();
    }

}
