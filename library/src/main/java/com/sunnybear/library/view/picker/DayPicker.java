package com.sunnybear.library.view.picker;

import android.app.Activity;

import com.sunnybear.library.view.picker.util.DateUtils;

/**
 * 天数选择器
 * Created By guchenkai
 */
public class DayPicker extends OptionPicker {

    public DayPicker(Activity activity, int year, int month) {
        super(activity, new String[]{});
        //需要根据年份及月份动态计算天数
        int maxDays = DateUtils.calculateDaysInMonth(year, month);
        for (int i = 1; i <= maxDays; i++) {
            options.add(DateUtils.fillZore(i));
        }
    }
}