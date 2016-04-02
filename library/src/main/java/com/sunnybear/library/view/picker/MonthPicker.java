package com.sunnybear.library.view.picker;

import android.app.Activity;

import com.sunnybear.library.view.picker.util.DateUtils;


/**
 * 月份选择器
 *
 * Created By guchenkai
 */
public class MonthPicker extends OptionPicker {

    public MonthPicker(Activity activity) {
        super(activity, new String[]{});
        for (int i = 1; i <= 12; i++) {
            options.add(DateUtils.fillZore(i));
        }
    }
}