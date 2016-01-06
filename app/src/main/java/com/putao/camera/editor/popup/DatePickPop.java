
package com.putao.camera.editor.popup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.putao.camera.R;

/**
 * 时间选择器
 *
 * @author CLEVO
 */
public class DatePickPop extends PopupWindow {
    private Context context;
    private LayoutInflater inflater;
    private View contentV;
    private TextView timeTV;
    private DatePicker datePicker;
    private TimePicker timPicker;
    private Button cancleBtn;
    private Button setBtn;
    private long timeInMillis;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private String sdfPattern = "yyyy-MM-dd";
    private boolean allowAdvance;
    private OnDateSelectListener onDateSelectListener;

    /**
     * @param context
     * @param pattern
     * @param allowAdvance
     */
    public DatePickPop(Context context) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.contentV = inflater.inflate(R.layout.view_pick_time, null);
        this.timeTV = (TextView) this.contentV.findViewById(R.id.textview1);
        this.datePicker = (DatePicker) this.contentV.findViewById(R.id.datepicker1);
        this.setBtn = (Button) this.contentV.findViewById(R.id.btn1);
        this.cancleBtn = (Button) this.contentV.findViewById(R.id.btn2);
        this.cancleBtn.setOnClickListener(cancleClick);
        this.setBtn.setOnClickListener(setClick);
        this.contentV.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        this.setContentView(contentV);
        /*this.setAnimationStyle(R.style.popuStyle);*/
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.xmlbg_pop));
        setPattern("yyyy-MM-dd");
    }

    /**
     * 允许选择时间超过当前时间
     *
     * @param allowAdvance
     */
    public void setAlowAdvance(boolean allowAdvance) {
        this.allowAdvance = allowAdvance;
    }

    /**
     * 设置日期时间的格式
     *
     * @param pattern
     */
    public void setPattern(String pattern) {
        this.sdfPattern = pattern.toString();
        this.sdf = new SimpleDateFormat(sdfPattern, Locale.getDefault());
        this.calendar = Calendar.getInstance(Locale.getDefault());
        // 初始化日期选择器
        this.datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), dateChangedListener);
        /*// 初始化时间选择器
        this.timPicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        this.timPicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        this.timPicker.setOnTimeChangedListener(timeChangedListener);
        // 设置日期时间显示方式
        this.timPicker.setIs24HourView(true);*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            datePicker.setCalendarViewShown(false);
        }
    }

    public void setTime(long timeInMillis) {
        this.timeInMillis = timeInMillis;
        calendar.setTimeInMillis(this.timeInMillis);
        // 更新界面
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        /*timPicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timPicker.setCurrentMinute(calendar.get(Calendar.MINUTE));*/
        updateTime();
    }

    public void setOnDateSelectListener(OnDateSelectListener onDateSelectListener) {
        this.onDateSelectListener = onDateSelectListener;
    }

    /**
     * 当前正在发生的时间
     *
     * @return
     */
    private long getCurrentTime() {
        Calendar calendarTemp = Calendar.getInstance(Locale.getDefault());
        return calendarTemp.getTimeInMillis();
    }

    private void notifyChange() {
        if (null == sdfPattern) {
            sdfPattern = "yyyy-MM-dd";
        }
        if (null == sdf) {
            sdf = new SimpleDateFormat(sdfPattern, Locale.getDefault());
        }
        if (null == calendar) {
            calendar = Calendar.getInstance(Locale.getDefault());
        }
        // 日期时间选择器上的时间
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        // 界面更新
        updateTime();
    }

    /**
     * 更新時間，显示在界面上
     */
    private void updateTime() {
        timeInMillis = calendar.getTimeInMillis();
        timeTV.setText(sdf.format(calendar.getTime())
                + weekday(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
    }

    /**
     * 时间回滚
     *
     * @param timeInMillis
     */
    private void timeRollback(long timeInMillis) {
        calendar.setTimeInMillis(timeInMillis);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        /*timPicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timPicker.setCurrentMinute(calendar.get(Calendar.MINUTE));*/
    }

    public boolean isLeapYear(int year) {
        boolean flag = false;
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public String weekday(int year, int month, int day) {
        long amount = 0;
        for (int i = 1; i < year; i++) {
            if (isLeapYear(i))
                amount++;
        }
        amount += 365 * (year - 1);
        for (int i = 1; i < month; i++) {
            amount += daysofMonth(year, i);
        }
        amount += day;
        int week = (int) (amount % 7);
        String w = "";
        switch (week) {
            case 0:
                w = "星期日";
                break;
            case 1:
                w = "星期一";
                break;
            case 2:
                w = "星期二";
                break;
            case 3:
                w = "星期三";
                break;
            case 4:
                w = "星期四";
                break;
            case 5:
                w = "星期五";
                break;
            case 6:
                w = "星期六";
                break;
        }
        return w;
    }

    private int daysofMonth(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (isLeapYear(year))
                    return 29;
                else
                    return 28;
            default:
                return 0;
        }
    }

    private OnClickListener cancleClick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            if (null != onDateSelectListener) {
                onDateSelectListener.onCancle();
            }
            dismiss();
        }
    };
    private OnClickListener setClick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            if (null != onDateSelectListener) {
                onDateSelectListener.onSet(timeInMillis);
            }
            dismiss();
        }
    };
    private OnTimeChangedListener timeChangedListener = new OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
            notifyChange();
        }
    };
    private OnDateChangedListener dateChangedListener = new OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            notifyChange();
        }
    };

    public interface OnDateSelectListener {
        public void onCancle();

        public void onSet(long timeInMillis);
    }
}
