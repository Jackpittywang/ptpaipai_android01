package com.sunnybear.library.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Android Toast 封装
 * Created by guchenkai on 2015/11/6.
 */
public final class ToastUtils {
    public static void showToastLong(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_LONG);
    }

    public static void showToastShort(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToastShort(Context context, int strRes) {
        showToast(context, context.getString(strRes), Toast.LENGTH_SHORT);
    }

    public static void showToastLong(Context context, int strRes) {
        showToast(context, context.getString(strRes), Toast.LENGTH_LONG);
    }

    public static void showToast(Context context, String msg, int duration) {
        Toast.makeText(context, msg, duration).show();
    }
}
