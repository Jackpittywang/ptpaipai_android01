package com.sunnybear.library.view.picker;

import android.app.Activity;

/**
 * 性别
 *
 * @since 2015/12/15
 * Created By guchenkai
 */
public class SexPicker extends OptionPicker {

    public SexPicker(Activity activity) {
        super(activity, new String[]{
                "男",
                "女",
        });
    }

    /**
     * 仅仅提供男和女来选择
     */
    public void onlyMaleAndFemale() {
        options.remove(options.size() - 1);
    }
}
