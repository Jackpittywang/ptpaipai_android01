package com.sunnybear.library.view.picker;

import android.app.Activity;
import android.view.View;

import com.sunnybear.library.view.picker.popup.ConfirmPopup;
import com.sunnybear.library.view.picker.widget.WheelView;


/**
 * 滑轮选择器
 *
 * @since 2015/12/22
 * Created By guchenkai
 */
public abstract class WheelPicker extends ConfirmPopup<View> {
    protected int textSize = WheelView.TEXT_SIZE;
    protected int textColorNormal = WheelView.TEXT_COLOR_NORMAL;
    protected int textColorFocus = WheelView.TEXT_COLOR_FOCUS;
    protected int lineColor = WheelView.LINE_COLOR;
    protected boolean lineVisible = true;
    protected int offset = WheelView.OFF_SET;

    public WheelPicker(Activity activity) {
        super(activity);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTextColor(int textColorFocus, int textColorNormal) {
        this.textColorFocus = textColorFocus;
        this.textColorNormal = textColorNormal;
    }

    public void setTextColor(int textColor) {
        this.textColorFocus = textColor;
    }

    public void setLineVisible(boolean lineVisible) {
        this.lineVisible = lineVisible;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
