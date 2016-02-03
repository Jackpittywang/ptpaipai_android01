package com.putao.common.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

	private static final String TAG = "FileUtil";
	private static final String PARENT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PutaoCamera";

	/**
	 * view shot
	 * @return Bitmap
	 */
	private Bitmap getViewShot (View view) {

		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		view.destroyDrawingCache();
		return bitmap;
	}

	/**
	 * save bitmap to sdCard
	 * @param bitmap	src
	 * @param fileName	sample:"xxx.png"
	 */
	public static void saveBitmap(Bitmap bitmap, String fileName) {

		try {

			Log.w(TAG, "PARENT_PATH = " + PARENT_PATH);
			File directory = new File(PARENT_PATH);
			if (!directory.exists()) directory.mkdirs();

			FileOutputStream fos = new FileOutputStream(new File(PARENT_PATH + File.separator + fileName));
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
