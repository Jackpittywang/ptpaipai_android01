package com.putao.camera.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.putao.camera.bean.LunarDay;
import com.putao.camera.bean.SolarDay;

/**
 * 计算农历节日、公历节日
 *
 * @author CLEVO
 * @modify jidongdong 1015/01/23
 * <p/>
 * 添加关于日期的相关计算方法
 */
public class DateUtil {
    public static String getFestival(long timeInMills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMills);
        LunarDay lunar = new LunarDay(calendar);
        SolarDay solar = new SolarDay(calendar);
        String temp = "";
        temp += (lunar.lunarFestival().trim().length() != 0) ? lunar.lunarFestival().trim() : "";
        if ((temp.length() != 0) && (solar.solarFestival().trim().length() != 0)) {
            temp += " ";
        }
        temp += (solar.solarFestival().trim().length() != 0) ? solar.solarFestival().trim() : "";
        return temp;
    }

    /**
     * 两个时间之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDays(String date1, String date2) {
        if (StringHelper.isEmpty(date1) || StringHelper.isEmpty(date1))
            return 0;
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int day = (int) (Math.abs(date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000));
        return day;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    /**
     * 得到现在时间
     *
     * @return 字符串 yyyyMMdd HHmmss
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 毫秒数
     *
     * @param time
     * @return
     */
    public static String getDate(long time) {
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time * 1000L));
    }

    /**
     * 获取xxxx-xx-xx的日
     *
     * @param d
     * @return
     */
    public static int getDay(Date d) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        return now.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取月份，1-12月
     *
     * @param d
     * @return
     */
    public static int getMonth(Date d) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        return now.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取19xx,20xx形式的年
     *
     * @param d
     * @return
     */
    public static int getYear(Date d) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        return now.get(Calendar.YEAR);
    }

    /**
     * 获取时期对象
     *
     * @param strDate
     * @return
     */
    public static Date getDate(String strDate) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(strDate);
            return date;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 获取星期
     *
     * @param Date
     * @return
     */
    public static String getWeekSting(Date d) {
        String weeks[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        return weeks[now.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShortAfterDays(int days) {
        Date currentTime = new Date();
        Date newDate = new Date(currentTime.getTime() + days * 24 * 60 * 60 * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(newDate);
        return dateString;
    }


}
