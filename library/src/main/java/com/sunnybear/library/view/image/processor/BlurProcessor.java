package com.sunnybear.library.view.image.processor;

import android.content.Context;
import android.graphics.Bitmap;

import com.sunnybear.library.view.image.FastBlur;

/**
 * 高斯模糊处理器
 * Created by guchenkai on 2016/1/12.
 */
public class BlurProcessor implements ProcessorInterface {
    private final int DEFAULT_BLUR_RADIUS = 25;
    private int radius = 0;

    public BlurProcessor() {
    }

    public BlurProcessor(int radius) {
        this.radius = radius;
    }

    @Override
    public void process(Context context, Bitmap bitmap) {
        FastBlur.applyOriginal(context, bitmap, radius != 0 ? radius : DEFAULT_BLUR_RADIUS);
    }
}
