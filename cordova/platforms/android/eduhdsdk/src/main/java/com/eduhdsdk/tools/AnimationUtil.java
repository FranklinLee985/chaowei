package com.eduhdsdk.tools;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.R;

/**
 * Created by Administrator on 2018/11/20/020.  动画工具类
 */

public class AnimationUtil {

    private static  AnimationUtil mInstance = null;
    private Context mContext;

    private AnimationUtil(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static  AnimationUtil getInstance(Context context) {
        synchronized (AnimationUtil.class) {
            if (mInstance == null) {
                mInstance = new AnimationUtil(context);
            }
            return mInstance;
        }
    }

    /**
     * 向上显示控制条
     *
     * @param view
     */
    public void moveUpView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY",
                120 * ScreenScale.getWidthScale(), 0);
        animator.setDuration(1000);
        animator.start();
    }

    /**
     * 向下隐藏控制条
     *
     * @param view
     */
    public void backView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY",
                0, 120 * ScreenScale.getWidthScale());
        animator.setDuration(300);
        animator.start();
    }

    /**
     * @param view 身份向下隐藏控制条
     */
    public void roleBackView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0);
        animator.setDuration(300);
        animator.start();
    }

    /**
     * @param view 身份向上显示控制条
     */
    public void rolemoveUpView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -100);
        animator.setDuration(300);
        animator.start();
    }

    /***
     *
     * @param view
     * @param context   聊天列表隐藏动画
     */
    public void hideChatLists(final View view, Context context) {
        if (view.getVisibility() == View.VISIBLE) {
            AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(context, R.anim.tk_chatpophide_anim);
            view.startAnimation(alphaAnimation);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setEnabled(false);
                    view.setVisibility(View.GONE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    /***
     *
     * @param view
     * @param context   控件隐藏动画
     */
    public void hideViewAniamtion(final View view, Context context) {
        if (view.getVisibility() == View.VISIBLE) {
            AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.tk_chat_button_hide_anim);
            view.startAnimation(animationSet);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
}
