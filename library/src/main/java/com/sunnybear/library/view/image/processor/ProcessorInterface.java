package com.sunnybear.library.view.image.processor;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 图片处理接口
 * Created by guchenkai on 2015/11/17.
 */
public interface ProcessorInterface {

    void process(Context context, Bitmap bitmap);
}
