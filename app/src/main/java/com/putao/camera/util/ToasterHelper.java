
package com.putao.camera.util;

import java.text.MessageFormat;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.putao.camera.R;

public class ToasterHelper {
    public static final boolean NO_IMG = true;
    public static final int IMG_ALERT = android.R.drawable.ic_dialog_alert;
    public static final int IMG_INFO = android.R.drawable.ic_dialog_info;
    public static Toast mToast;
    private static final Object lock = new Object();

    private static void show(final Activity activity, final int resId, final int imgResId, final int duration) {
        if (activity == null)
            return;
        final Context context = activity.getApplication();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = getDefaultToastView(context, resId, imgResId);
                showToast(duration, context, view);
            }
        });
    }

    private static void show(final Activity activity, final String message, final int imgResId, final int duration) {
        if (activity == null)
            return;
        if (TextUtils.isEmpty(message))
            return;
        final Context context = activity.getApplication();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = getDefaultToastView(context, message, imgResId);
                showToast(duration, context, view);
            }
        });
    }

    private static void show(final Context context, final String message, final int imgResId, final int duration) {
        if (TextUtils.isEmpty(message))
            return;
        View view = getDefaultToastView(context, message, imgResId);
        showToast(duration, context, view);
    }

    public static View getDefaultToastView(final Context context, final Object message, final int imgResId) {
        if (message == null)
            return null;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toaster, null);
        TextView mTextView = (TextView) view.findViewById(R.id.toast_text);
        ImageView mImageView = (ImageView) view.findViewById(R.id.toast_img);
        if (NO_IMG) {
            mImageView.setVisibility(View.GONE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageResource(imgResId);
        }
        if (message instanceof String) {
            mTextView.setText(message.toString());
        } else {
            mTextView.setText(Integer.parseInt(message.toString()));
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mTextView.setMaxWidth(dm.widthPixels - 100);
        return view;
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param resId
     */
    public static void showLong(final Activity activity, int resId, int imgResId) {
        show(activity, resId, imgResId, Toast.LENGTH_LONG);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param resId
     */
    public static void showShort(final Activity activity, final int resId, int imgResId) {
        show(activity, resId, imgResId, Toast.LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param message
     */
    public static void showLong(final Activity activity, final String message, int imgResId) {
        show(activity, message, imgResId, Toast.LENGTH_LONG);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param message
     */
    public static void showShort(final Activity activity, final String message, int imgResId) {
        show(activity, message, imgResId, Toast.LENGTH_SHORT);
    }

    public static void showToast(final Context context, final String message, int imgResId) {
        show(context, message, imgResId, Toast.LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param message
     * @param args
     */
    public static void showLong(final Activity activity, final String message, final int imgResId, final Object... args) {
        String formatted = MessageFormat.format(message, args);
        show(activity, formatted, imgResId, Toast.LENGTH_LONG);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param message
     * @param args
     */
    public static void showShort(final Activity activity, final String message, final int imgResId, final Object... args) {
        String formatted = MessageFormat.format(message, args);
        show(activity, formatted, imgResId, Toast.LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param resId
     * @param args
     */
    public static void showLong(final Activity activity, final int resId, final int imgResId, final Object... args) {
        if (activity == null)
            return;
        String message = activity.getString(resId);
        showLong(activity, message, imgResId, args);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param resId
     * @param args
     */
    public static void showShort(final Activity activity, final int resId, final int imgResId, final Object... args) {
        if (activity == null)
            return;
        String message = activity.getString(resId);
        showShort(activity, message, imgResId, args);
    }

    public static void showToast(final int duration, final Context context, View view) {
        synchronized (lock) {
            if (mToast == null) {
                mToast = new Toast(context);
            }

            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(duration);
            mToast.setView(view);
            mToast.show();
        }
    }

    /**
     * 显示toast
     * @param context
     * @param msg
     */
    public static void show(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
