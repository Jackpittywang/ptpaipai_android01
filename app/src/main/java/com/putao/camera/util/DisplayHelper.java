package com.putao.camera.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

public class DisplayHelper {
    public static DisplayMetrics metrics;
    public static int rootViewWidth = 0;
    public static int rootViewHeight = 0;

    /**
     * App启动时初始化
     */
    public static void init(Context context) {
        metrics = context.getResources().getDisplayMetrics();
    }

    /**
     * [简要描述]: Display Density [详细描述]:
     */
    public static float getDensity() {
        return metrics.density;
    }

    /**
     * 屏幕密度DPI
     */
    public static int getDensityDpi() {
        return metrics.densityDpi;
    }

    /**
     * [简要描述]: 获取屏幕宽度（像素值） [详细描述]:
     */
    public static int getScreenWidth() {
        return metrics.widthPixels;
    }

    /**
     * [简要描述]: 获取屏幕高度（像素值） [详细描述]:
     */
    public static int getScreenHeight() {
        return metrics.heightPixels;
    }

    public static int dipTopx(float dpValue) {
        final float scale = getDensity();
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getValueByDensity(int value) {
        return (int) (value * metrics.density / 2);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int pxTodip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> localClass = Class.forName("com.android.internal.R$dimen");
            Object localObject = localClass.newInstance();
            int j = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
            int k = context.getResources().getDimensionPixelSize(j);
            Loger.d("statusbar height:" + k);
            return k;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return 0;
    }

    public static int[] getScreenCenter() {
        return new int[]{getScreenWidth() / 2, getScreenHeight() / 2};
    }

    /**
     * 获取屏幕宽度和高度，单位为px
     *
     * @param context
     * @return
     */
    public static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        Loger.i("Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
        return new Point(w_screen, h_screen);
    }

    /**
     * 获取屏幕长宽比
     *
     * @param context
     * @return
     */
    public static float getScreenRate(Context context) {
        Point P = getScreenMetrics(context);
        float H = P.y;
        float W = P.x;
        return (H / W);
    }

    public static int getRootViewWidth() {
        return rootViewWidth;
    }

    public static int getRootViewHeight() {
        return rootViewWidth;
    }


//    public static getScreenDpiInfo()
//    {
//        switch (metrics.densityDpi)
//        {
//
//        }
//    }
}
