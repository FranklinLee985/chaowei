package com.eduhdsdk.tools;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.classroomsdk.tools.ScreenScale;
import com.eduhdsdk.BuildVars;
import com.eduhdsdk.R;
import com.eduhdsdk.room.RoomControler;
import com.eduhdsdk.room.RoomVariable;
import com.eduhdsdk.ui.holder.VideoItem;

import org.tkwebrtc.SurfaceViewRenderer;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2019/4/11/011.
 * <p>
 * 显示奖杯工具类
 */
public class ShowTrophyUtil {

    public static boolean is_gif = false;

    /***
     *   一对多奖杯意图
     * @param sf_video
     * @param map
     * @param mContext
     * @param wid
     * @param hid
     * @param rel_parent
     */
    public static void showManyTrophyIntention(SurfaceViewRenderer sf_video, Map<String, Object> map, Context mContext,
                                               int wid, int hid, RelativeLayout rel_parent) {

        final ImageView img_gift = new ImageView(mContext);
        img_gift.setScaleType(ImageView.ScaleType.FIT_XY);
        img_gift.setAdjustViewBounds(true);

        final GifImageView iv_gif = new GifImageView(mContext);
        iv_gif.setScaleType(ImageView.ScaleType.FIT_XY);
        iv_gif.setAdjustViewBounds(true);

        if (RoomControler.isCustomTrophy() && map.containsKey("giftinfo")) {
            //自定义奖杯
            try {
                final Map<String, Object> gift_data = (HashMap<String, Object>) map.get("giftinfo");
                String url = gift_data.get("trophyimg").toString();
                is_gif = url.endsWith(".gif");

                SoundPlayUtils.play(gift_data.get("trophyvoice").toString());
                String img_url = BuildVars.REQUEST_HEADER + RoomVariable.host + ":" + RoomVariable.port + url;

                if (is_gif) {
                    Glide.with(mContext).asGif().load(img_url).into(iv_gif);
                } else {
                    Glide.with(mContext).asDrawable().load(img_url).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            img_gift.setImageDrawable(resource);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //默认动画
            is_gif = false;
            SoundPlayUtils.play("");
            img_gift.setImageResource(R.drawable.tk_ico_gift);
        }
        setManyAnimal(is_gif, img_gift, iv_gif, sf_video, wid, hid, rel_parent, mContext);
    }

    /***
     *    一对多奖杯动画
     * @param is_gif
     * @param img_gift
     * @param iv_gif
     * @param sf_video
     * @param wid
     * @param hid
     * @param rel_parent
     * @param mContext
     */
    public static void setManyAnimal(final boolean is_gif, final ImageView img_gift,
                                     final GifImageView iv_gif, SurfaceViewRenderer sf_video,
                                     int wid, int hid, RelativeLayout rel_parent, Context mContext) {

        int size = ScreenScale.getScreenHeight() / 4;
        RelativeLayout.LayoutParams relparam = new RelativeLayout.LayoutParams(size, size);
        relparam.addRule(RelativeLayout.CENTER_IN_PARENT);
        relparam.height = size;
        relparam.width = size;

        if (is_gif) {
            iv_gif.setLayoutParams(relparam);
            rel_parent.addView(iv_gif);
        } else {
            img_gift.setLayoutParams(relparam);
            rel_parent.addView(img_gift);
        }

        // 当前sf_video 相对于屏幕的位置
        int[] loca2 = new int[2];
        sf_video.getLocationInWindow(loca2);

        //rel_parent 相对于屏幕的位置
        int[] loca3 = new int[2];
        rel_parent.getLocationInWindow(loca3);

        //原点是父布局（rel_parent）的中心点

        float dx = loca2[0] - wid / 2 + sf_video.getWidth() / 2;

        // 视频框相对于父布局（rel_parent）的 y 坐标 ：sf_hid - loca3[1]
        //父布局（rel_parent）高的一半 ：(hid - loca3[1]) / 2
        float dy = loca2[1] - loca3[1] - (hid - loca3[1]) / 2 + sf_video.getHeight() / 2;

        ObjectAnimator smlTobigXScale = null;
        ObjectAnimator smlTobigYScale = null;
        ObjectAnimator bigToSmlXScale = null;
        ObjectAnimator bigToSmlYScale = null;
        ObjectAnimator translateX = null;
        ObjectAnimator translateY = null;

        if (is_gif) {
            smlTobigXScale = ObjectAnimator.ofFloat(iv_gif, "scaleX", 1.0f, 3.0f);
            smlTobigYScale = ObjectAnimator.ofFloat(iv_gif, "scaleY", 1.0f, 3.0f);
            bigToSmlXScale = ObjectAnimator.ofFloat(iv_gif, "scaleX", 3.0f, 0.0f);
            bigToSmlYScale = ObjectAnimator.ofFloat(iv_gif, "scaleY", 3.0f, 0.0f);
            translateX = ObjectAnimator.ofFloat(iv_gif, "translationX", 0.0f, dx);
            translateY = ObjectAnimator.ofFloat(iv_gif, "translationY", 0.0f, dy);

        } else {
            smlTobigXScale = ObjectAnimator.ofFloat(img_gift, "scaleX", 1.0f, 3.0f);
            smlTobigYScale = ObjectAnimator.ofFloat(img_gift, "scaleY", 1.0f, 3.0f);
            bigToSmlXScale = ObjectAnimator.ofFloat(img_gift, "scaleX", 3.0f, 0.0f);
            bigToSmlYScale = ObjectAnimator.ofFloat(img_gift, "scaleY", 3.0f, 0.0f);
            translateX = ObjectAnimator.ofFloat(img_gift, "translationX", 0.0f, dx);
            translateY = ObjectAnimator.ofFloat(img_gift, "translationY", 0.0f, dy);
        }
        AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.play(smlTobigXScale).with(smlTobigYScale);
        scaleSet.setDuration(1000);
        scaleSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet scaleAndTranSet = new AnimatorSet();
        scaleAndTranSet.playTogether(bigToSmlXScale, bigToSmlYScale, translateX, translateY);
        scaleAndTranSet.setDuration(2000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleSet).before(scaleAndTranSet);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (is_gif) {
                    iv_gif.clearAnimation();
                    iv_gif.setVisibility(View.GONE);
                } else {
                    img_gift.clearAnimation();
                    img_gift.setVisibility(View.GONE);

                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /***
     *    一对一奖杯意图
     * @param item
     * @param map
     * @param mContext
     * @param rl_web
     * @param rel_tool_bar
     */
    public static void showOneTrophyIntention(final VideoItem item, Map<String, Object> map, Context mContext,
                                              RelativeLayout rl_web, RelativeLayout rel_tool_bar) {

        final ImageView img_gift = new ImageView(mContext);
        img_gift.setScaleType(ImageView.ScaleType.FIT_XY);
        img_gift.setAdjustViewBounds(true);

        final GifImageView iv_gif = new GifImageView(mContext);
        iv_gif.setScaleType(ImageView.ScaleType.FIT_XY);
        iv_gif.setAdjustViewBounds(true);


        if (RoomControler.isCustomTrophy() && map.containsKey("giftinfo")) {
            //自定义奖杯
            try {
                final Map<String, Object> gift_data = (HashMap<String, Object>) map.get("giftinfo");
                String url = gift_data.get("trophyimg").toString();
                is_gif = url.endsWith(".gif");

                SoundPlayUtils.play(gift_data.get("trophyvoice").toString());
                String img_url = BuildVars.REQUEST_HEADER + RoomVariable.host + ":" + RoomVariable.port + url;

                if (is_gif) {
                    Glide.with(mContext).asGif().load(img_url).into(iv_gif);
                } else {
                    Glide.with(mContext).asDrawable().load(img_url).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            img_gift.setImageDrawable(resource);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //默认动画
            is_gif = false;
            SoundPlayUtils.play("");
            img_gift.setImageResource(R.drawable.tk_ico_gift);
        }
        setOneAnimal(is_gif, img_gift, iv_gif, item.sf_video, rl_web, rel_tool_bar);
    }

    /***
     *   一对一 奖杯动画
     * @param is_gif
     * @param img_gift
     * @param iv_gif
     * @param sf_video
     * @param rl_web
     * @param rel_tool_bar
     */
    public static void setOneAnimal(final boolean is_gif, final ImageView img_gift, final GifImageView iv_gif,
                                    SurfaceViewRenderer sf_video, RelativeLayout rl_web, RelativeLayout rel_tool_bar) {

        int size = ScreenScale.getScreenHeight() / 4;
        RelativeLayout.LayoutParams relparam = new RelativeLayout.LayoutParams(size, size);
        relparam.addRule(RelativeLayout.CENTER_IN_PARENT);
        relparam.height = size;
        relparam.width = size;
        if (is_gif) {
            iv_gif.setLayoutParams(relparam);
            rl_web.addView(iv_gif);
        } else {
            img_gift.setLayoutParams(relparam);
            rl_web.addView(img_gift);
        }

        //视频框相对于屏幕的坐标
        int[] loca2 = new int[2];
        sf_video.getLocationInWindow(loca2);
        float sf_wid = loca2[0];
        float sf_hid = loca2[1];

        float gx = 0, gy = 0;
        if (is_gif) {
            gx = rl_web.getWidth() / 2 - iv_gif.getWidth();
            gy = rl_web.getHeight() / 2 - iv_gif.getHeight();
        } else {
            gx = rl_web.getWidth() / 2 - img_gift.getWidth();
            gy = rl_web.getHeight() / 2 - img_gift.getHeight();
        }

        float dx = sf_wid - gx + sf_video.getWidth() / 2;
        float dy = sf_hid - gy - rel_tool_bar.getHeight() + sf_video.getHeight() / 2;

        ObjectAnimator smlTobigXScale = null;
        ObjectAnimator smlTobigYScale = null;
        ObjectAnimator bigToSmlXScale = null;
        ObjectAnimator bigToSmlYScale = null;
        ObjectAnimator translateX = null;
        ObjectAnimator translateY = null;
        if (is_gif) {
            smlTobigXScale = ObjectAnimator.ofFloat(iv_gif, "scaleX", 1.0f, 2.0f);
            smlTobigYScale = ObjectAnimator.ofFloat(iv_gif, "scaleY", 1.0f, 2.0f);
            bigToSmlXScale = ObjectAnimator.ofFloat(iv_gif, "scaleX", 2.0f, 0.0f);
            bigToSmlYScale = ObjectAnimator.ofFloat(iv_gif, "scaleY", 2.0f, 0.0f);
            translateX = ObjectAnimator.ofFloat(iv_gif, "translationX", 0.0f, dx);
            translateY = ObjectAnimator.ofFloat(iv_gif, "translationY", 0.0f, dy);
        } else {
            smlTobigXScale = ObjectAnimator.ofFloat(img_gift, "scaleX", 1.0f, 2.0f);
            smlTobigYScale = ObjectAnimator.ofFloat(img_gift, "scaleY", 1.0f, 2.0f);
            bigToSmlXScale = ObjectAnimator.ofFloat(img_gift, "scaleX", 2.0f, 0.0f);
            bigToSmlYScale = ObjectAnimator.ofFloat(img_gift, "scaleY", 2.0f, 0.0f);
            translateX = ObjectAnimator.ofFloat(img_gift, "translationX", 0.0f, dx);
            translateY = ObjectAnimator.ofFloat(img_gift, "translationY", 0.0f, dy);
        }
        AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.play(smlTobigXScale).with(smlTobigYScale);
        scaleSet.setDuration(1500);

        scaleSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet scaleAndTranSet = new AnimatorSet();
        scaleAndTranSet.playTogether(bigToSmlXScale, bigToSmlYScale, translateX, translateY);
        scaleAndTranSet.setDuration(2000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleSet).before(scaleAndTranSet);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (is_gif) {
                    iv_gif.clearAnimation();
                    iv_gif.setVisibility(View.GONE);
                } else {
                    img_gift.clearAnimation();
                    img_gift.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
