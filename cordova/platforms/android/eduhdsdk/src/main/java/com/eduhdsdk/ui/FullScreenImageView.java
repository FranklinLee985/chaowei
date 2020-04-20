package com.eduhdsdk.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

/**
 * Created by zhe on 2019-01-15.
 * 全屏ImageView
 */
public class FullScreenImageView {

    private Context mContext;
    private RelativeLayout mRootView;
    private FrameLayout mFrameLayout;
    private ImageView mImageView;

    public FullScreenImageView(Context context, RelativeLayout rootView) {
        mContext = context;
        mRootView = rootView;
        mFrameLayout = new FrameLayout(mContext);
        mFrameLayout.setBackgroundColor(Color.parseColor("#70000000"));

        mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setAdjustViewBounds(true);

        mFrameLayout.addView(mImageView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mImageView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        mImageView.setLayoutParams(params);

        mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    public void show(String image) {
        mRootView.removeView(mFrameLayout);
        mRootView.addView(mFrameLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        Glide.with(mContext).asDrawable()
                .load(image)
                .into(mImageView);
    }

    private void hide() {
        if (mFrameLayout == null || mRootView == null) {
            return;
        }
        mRootView.removeView(mFrameLayout);
    }

}
