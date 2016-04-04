package com.putao.mtlib.util;

import android.util.Log;

public class PTLoger {
	public final static String TAG = "ptl";

	public static void d(String message) {
		Log.d(TAG, message);
	}

	public static void v(String message) {
		Log.v(TAG, message);
	}

	public static void i(String message) {
		Log.i(TAG, message);
	}

	public static void e(String message) {
		Log.e(TAG, message);
	}

	public static void w(String message) {
		Log.w(TAG, message);
	}
}
