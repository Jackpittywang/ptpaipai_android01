
package com.putao.camera.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ActivityHelper {
    public static void showInputKeyboard(Context context, View view) {
        if (null != view) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    public static void hideInputKeyboard(Context context, View view) {
        if (null != view) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * @param context
     * @param clazz
     */
    public static void startActivity(Activity activity, Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(activity, clazz);
        activity.startActivity(intent);
    }

    /**
     * @param context
     * @param clazz
     * @param bundle
     */
    public static void startActivity(Activity activity, Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(activity, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
    }

    /**
     * @param context
     * @param clazz
     * @param bundle
     */
    public static void startActivity(Activity activity, Class<?> clazz, Bundle bundle, boolean isAnimation) {
        Intent intent = new Intent();
        intent.setClass(activity, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
        if (isAnimation) {
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /**
     * @param activity
     * @param intent
     * @param resultCode
     */
    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * @param activity
     * @param clazz
     * @param bundle
     * @param resultCode
     */
    public static void startActivityForResult(Activity activity, Class<?> clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * @param activity
     * @param resultCode
     */
    public static void startActivityForResult(Activity activity, Class<?> clazz, int requestCode) {
        startActivityForResult(activity, clazz, null, requestCode);
    }
}
