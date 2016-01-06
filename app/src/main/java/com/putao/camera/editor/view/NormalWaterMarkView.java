package com.putao.camera.editor.view;

import android.content.Context;
import android.graphics.Bitmap;

import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.Loger;

/**
 * Created by ji dong dong on 15/1/15.
 */
public class NormalWaterMarkView extends WaterMarkView {
    public NormalWaterMarkView(Context context, Bitmap watermark) {
        super(context, watermark);
    }

    public NormalWaterMarkView(Context context, Bitmap watermark, boolean isCanRemove) {
        super(context, watermark, isCanRemove);
    }


    @Override
    void WaterMarkClicked(float x, float y) {
        Loger.d("other area clicked");
    }

    @Override
    protected void setBitSizeScale(float scale) {
        super.setBitSizeScale(DisplayHelper.getDensity() / 2);
    }
}
