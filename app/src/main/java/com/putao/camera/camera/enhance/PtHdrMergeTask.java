package com.putao.camera.camera.enhance;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.putao.camera.JNIFUN;

import java.util.List;

/**
 * Created by jidongdong on 15/5/20.
 */
public class PtHdrMergeTask extends AsyncTask<Void, Void, int[]> {
    int mwidth, mheight;
    private List<HdrBitmap> mHdrBitmaps;
    PtHdrMergeListener mPtHdrMergeListener;

    public interface PtHdrMergeListener {
        void merged(Bitmap bitmap);
    }

    public PtHdrMergeTask(List<HdrBitmap> bitmaps, int width, int height, PtHdrMergeListener listener) {
        mwidth = width;
        mheight = height;
        mHdrBitmaps = bitmaps;
        mPtHdrMergeListener = listener;
    }

    @Override
    protected int[] doInBackground(Void... params) {
//        Loger.d("bitmap size:" + mHdrBitmaps.size());
        int[] expovaule = new int[]{2, 1, 4};
        return JNIFUN.PTHDRImageMerge(mHdrBitmaps.get(0).getRgbaArray(), mHdrBitmaps.get(1).getRgbaArray(), mHdrBitmaps.get(2).getRgbaArray(), expovaule, mwidth, mheight);
    }

    @Override
    protected void onPostExecute(int[] ints) {
        super.onPostExecute(ints);
        Bitmap bitmap = null;
        if (ints != null) {
            bitmap = Bitmap.createBitmap(mwidth, mheight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(ints, 0, mwidth, 0, 0, mwidth, mheight);
        }
        if (mPtHdrMergeListener != null) {
            mPtHdrMergeListener.merged(bitmap);
        }
    }
}
