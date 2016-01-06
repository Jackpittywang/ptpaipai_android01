
package com.putao.camera.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

/**
 * 该类用于读写SharedPreferences区数据
 *
 * @author yanglun
 */
public class SharedPreferencesHelper {
    public static String readStringValue(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(key, "");
        try {
            String valuedecode = TextUtils.isEmpty(value) ? value : decode(value);
            Loger.d("readStringValue+2++value:" + value + ",valuedecode:" + valuedecode);
            return valuedecode;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String readStringValue(Context context, String key, String defaultStr) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(key, defaultStr);
        try {
            String valuedecode = TextUtils.isEmpty(value) ? value : decode(value);
            Loger.d("readStringValue+2++value:" + value + ",valuedecode:" + valuedecode);
            return valuedecode;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultStr;
        }
    }

    public static int readIntValue(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, 0);
    }

    public static int readIntValue(Context context, String key, int def) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, def);
    }

    public static Long readLongValue(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(key, 0);
    }

    public static boolean readBooleanValue(Context context, String key, boolean defValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, defValue);
    }

    public static void saveIntValue(Context context, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void saveStringValue(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, TextUtils.isEmpty(value) ? value : encode(value));
        editor.commit();
    }

    public static void saveLongValue(Context context, String key, Long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }


    public static void saveBooleanValue(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * base64加密
     *
     * @param encodeStr
     * @return
     */
    public static String encode(String encodeStr) {
        return Base64.encodeToString(encodeStr.getBytes(), Base64.DEFAULT);
    }

    /**
     * base64解密
     *
     * @param decodeStr
     * @return
     */
    public static String decode(String encodeStr) {
        return new String(Base64.decode(encodeStr, Base64.DEFAULT));
    }
}