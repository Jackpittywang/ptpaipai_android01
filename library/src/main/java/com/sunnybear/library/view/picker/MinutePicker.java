package com.sunnybear.library.view.picker;

import android.app.Activity;

import com.sunnybear.library.view.picker.util.DateUtils;

/**
 * 分钟选择器
 * <p/>
 * Created By guchenkai
 */
public class MinutePicker extends OptionPicker {

    public MinutePicker(Activity activity) {
        super(activity, new String[]{});
        for (int i = 0; i < 60; i++) {
            options.add(DateUtils.fillZore(i));
        }
    }
}