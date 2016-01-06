package com.putao.camera.util;

import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimalHelper {
    public static void setAnim(Activity activity, final View view, final View shopCart, final int[] start_location,
                               final AnimationListener animationListener) {
        //        LinearLayout anim_mask_layout = createAnimLayout(activity);
        //        anim_mask_layout.addView(view);
        //        final View view = addViewToAnimLayout(anim_mask_layout, v, start_location);
        AnimationSet set = new AnimationSet(false);
        ScaleAnimation scaleout = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleout.setFillAfter(true);
        set.addAnimation(scaleout);
        ScaleAnimation scalein = new ScaleAnimation(1.4f, 1.0f, 1.4f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scalein.setStartOffset(300);
        scalein.setFillAfter(true);
        set.addAnimation(scalein);
        set.setDuration(300);
        scalein.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setTranslateAnim(view, shopCart, start_location, animationListener);
            }
        });
        view.startAnimation(set);
    }

    public static void setTranslateAnim(View view, View shopCart, int[] start_location, AnimationListener animationListener) {
        int[] end_location = new int[2];
        shopCart.getLocationInWindow(end_location);
        int endX = end_location[0] + shopCart.getMeasuredWidth() / 2 - start_location[0];
        int endY = end_location[1] - start_location[1];
        TranslateAnimation translateAnimation = new TranslateAnimation(0, endX, 0, endY);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(700);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        alphaAnimation.setStartOffset(300);
        alphaAnimation.setDuration(700);
        alphaAnimation.setFillAfter(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.4f, 1.0f, 0.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setStartOffset(300);
        scaleAnimation.setDuration(700);
        scaleAnimation.setFillAfter(true);
        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(alphaAnimation);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimation);
        view.startAnimation(set);
        set.setAnimationListener(animationListener);
    }
}
