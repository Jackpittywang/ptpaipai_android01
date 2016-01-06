
package com.putao.camera.bean;

import java.util.Calendar;

public class SolarDay {
    private int year;
    private int month;
    private int day;

    public SolarDay(Calendar calendar) {
        if (null != calendar) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * 得到公历节日
     *
     * @return
     */
    public String solarFestival() {
        Festival ftv = new Festival();
        return ftv.showSFtv(month, day);
    }
}
