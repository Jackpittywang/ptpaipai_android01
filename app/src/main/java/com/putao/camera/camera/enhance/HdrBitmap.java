package com.putao.camera.camera.enhance;

import android.graphics.Bitmap;

import com.putao.camera.base.BaseItem;

/**
 * Created by jidongdong on 15/5/20.
 */
public class HdrBitmap extends BaseItem {
    private Bitmap mBitmap;
    private int[] rgbapix;

    public HdrBitmap(Bitmap bitmap) {
        mBitmap = Bitmap.createBitmap(bitmap).copy(Bitmap.Config.ARGB_8888, true);
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        rgbapix = new int[width * height];
        mBitmap.getPixels(rgbapix, 0, width, 0, 0, width, height);
//        for (int i = 0; i < rgbapix.length; i++) {
//            int pixel = rgbapix[i];
//            rgbapix[i] = (pixel << 8) | ((pixel >> 24) & 0xFF);
//        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int[] getRgbaArray() {
        return rgbapix;
    }
}
