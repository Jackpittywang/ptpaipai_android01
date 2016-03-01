package com.putao.camera.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.putao.camera.constants.PuTaoConstants;

public class CommonUtils {

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),

                PuTaoConstants.PAIAPI_PHOTOS_FOLDER);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("PAIAPI_PHOTOS_FOLDER", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + "_" + (int) (Math.random() * 10) + (int) (Math.random() * 10) + ".jpg");

        return mediaFile;
    }

    public static File getOutputVideoFile() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),

                PuTaoConstants.PAIAPI_PHOTOS_FOLDER);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("PAIAPI_PHOTOS_FOLDER", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + "_" + (int) (Math.random() * 10) + (int) (Math.random() * 10) + ".mp4");

        return mediaFile;
    }


    public static Dialog dialog(Context context, String message, String b1text,
                                String b2text, OnClickListener... listeners) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        return builder.show();
    }

    /**
     * base64加密
     *
     * @param encodeStr
     * @return
     */
    public static String encode(String encodeStr) {
        return new String(Base64.encode(encodeStr.getBytes(), Base64.DEFAULT));
    }

    /**
     * base64解密
     *
     * @param encodeStr
     * @return
     */
    public static String decode(String encodeStr) {
        return new String(Base64.decode(encodeStr.getBytes(), Base64.DEFAULT));
    }


    public static String parseTime(Object time) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        if (time instanceof Long) {
            date = new Date((Long) time);
        } else if (time instanceof String) {
            try {
                date = fmt.parse(String.valueOf(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return fmt.format(date);
    }

    public static String parseTime(Object time, String type) {
        DateFormat fmt = new SimpleDateFormat(type);
        Date date = null;
        if (time instanceof Long) {
            date = new Date((Long) time);
        } else if (time instanceof String) {
            try {
                date = fmt.parse(String.valueOf(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return fmt.format(date);
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断存储卡是否挂载
     *
     * @return
     */
    public static boolean isExternalStorageMounted() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
