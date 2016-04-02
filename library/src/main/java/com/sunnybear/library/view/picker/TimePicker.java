package com.sunnybear.library.view.picker;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunnybear.library.view.picker.util.DateUtils;
import com.sunnybear.library.view.picker.widget.WheelView;

import java.util.ArrayList;

/**
 * 时间选择器
 * <p/>
 * Created By guchenkai
 */
public class TimePicker extends WheelPicker {
    private OnTimePickListener onTimePickListener;
    private String hourLabel = "时", minuteLabel = "分";
    private String selectedHour = "", selectedMinute = "";

    public TimePicker(Activity activity) {
        super(activity);
    }

    public void setLabel(String hourLabel, String minuteLabel) {
        this.hourLabel = hourLabel;
        this.minuteLabel = minuteLabel;
    }

    public void setOnTimePickListener(OnTimePickListener listener) {
        this.onTimePickListener = listener;
    }

    @Override
    protected View initContentView() {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        WheelView hourView = new WheelView(activity);
        hourView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        hourView.setTextSize(textSize);
        hourView.setTextColor(textColorNormal, textColorFocus);
        hourView.setLineVisible(lineVisible);
        hourView.setLineColor(lineColor);
        layout.addView(hourView);
        TextView hourTextView = new TextView(activity);
        hourTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        hourTextView.setTextSize(textSize);
        hourTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(hourLabel)) {
            hourTextView.setText(hourLabel);
        }
        layout.addView(hourTextView);
        WheelView minuteView = new WheelView(activity);
        minuteView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        minuteView.setTextSize(textSize);
        minuteView.setTextColor(textColorNormal, textColorFocus);
        minuteView.setLineVisible(lineVisible);
        minuteView.setLineColor(lineColor);
        minuteView.setOffset(offset);
        layout.addView(minuteView);
        TextView minuteTextView = new TextView(activity);
        minuteTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        minuteTextView.setTextSize(textSize);
        minuteTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(minuteLabel)) {
            minuteTextView.setText(minuteLabel);
        }
        layout.addView(minuteTextView);
        ArrayList<String> hours = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            hours.add(DateUtils.fillZore(i));
        }
        hourView.setItems(hours);
        ArrayList<String> minutes = new ArrayList<String>();
        for (int i = 0; i < 60; i++) {
            minutes.add(DateUtils.fillZore(i));
        }
        minuteView.setItems(minutes);
        hourView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                selectedHour = item;
            }
        });
        minuteView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                selectedMinute = item;
            }
        });
        return layout;
    }

    @Override
    protected void setContentViewAfter(View contentView) {
        super.setContentViewAfter(contentView);
        super.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public void onConfirm() {
                if (onTimePickListener != null) {
                    onTimePickListener.onTimePicked(selectedHour, selectedMinute);
                }
            }
        });
    }

    public interface OnTimePickListener {

        void onTimePicked(String hour, String minute);

    }
}
