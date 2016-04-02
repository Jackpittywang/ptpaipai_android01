package com.sunnybear.library.view.picker;

import android.app.Activity;

/**
 * 生肖选择器
 *
 * @since 2015/12/15
 * Created By guchenkai
 */
public class ChineseZodiacPicker extends OptionPicker {

    public ChineseZodiacPicker(Activity activity) {
        super(activity, new String[]{
                "鼠",
                "牛",
                "虎",
                "兔",
                "龙",
                "蛇",
                "马",
                "羊",
                "猴",
                "鸡",
                "狗",
                "猪",
        });
    }

}
