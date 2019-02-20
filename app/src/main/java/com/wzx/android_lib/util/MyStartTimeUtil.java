package com.wzx.android_lib.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangzixu on 2019/2/20.
 * 用来计算各个时间点起始点的工具类
 */
public class MyStartTimeUtil {
    public static void test() {
        // TODO Auto-generated method stub
        Log.d("MyStartTimeUtil", "当前时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        Log.d("MyStartTimeUtil", "当天0点时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getTodayStartTime())));
        Log.d("MyStartTimeUtil", "当天24点时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getTodayEndTime())));

        Log.d("MyStartTimeUtil", "本周开始0点时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getWeekStartTime())));
        Log.d("MyStartTimeUtil", "本周结束24点时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getWeekEndTime())));

        Log.d("MyStartTimeUtil", "本月开始点时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getMonthStartTime())));
        Log.d("MyStartTimeUtil", "本月结束点时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getMonthEndTime())));

        Log.d("MyStartTimeUtil", "本季度开始点时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getQuarterStartTime())));
        Log.d("MyStartTimeUtil", "本季度结束点时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getQuarterEndTime())));

        Log.d("MyStartTimeUtil", "本年开始点时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getYearStartTime())));
        Log.d("MyStartTimeUtil", "本年结束点时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(getYearEndTime())));
    }

    // 获得当天0点时间
    public static long getTodayStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // 获得昨天0点时间
    public static long getYesterdayStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTodayStartTime() - 3600 * 24 * 1000);
        return cal.getTimeInMillis();
    }

    // 获得当天近7天时间
    public static long getWeekFromNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTodayStartTime() - 3600 * 24 * 1000 * 7);
        return cal.getTimeInMillis();
    }

    // 获得当天24点的时间戳
    public static long getTodayEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // 获得本周一0点时间
    public static long getWeekStartTime() {
        Calendar cal = Calendar.getInstance();
        // cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        // int firstDayOfWeek = cal.getFirstDayOfWeek();
        // Log.d("MyStartTimeUtil", "firstDayOfWeek = " + firstDayOfWeek + ", cal.getActualMinimum(Calendar.DAY_OF_WEEK) " + cal.getActualMinimum(Calendar.DAY_OF_WEEK));
        // cal.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // 获得本周日24点时间戳, 本周结束时间点
    public static long getWeekEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getWeekStartTime());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTimeInMillis();
    }

    // 获得本月第一天0点时间
    public static long getMonthStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // 获得本月最后一天24点时间, 本月结束时间
    public static long getMonthEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 当前季度的开始时间
     * @return
     */
    public static long getQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 4);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now.getTime();
    }

    /**
     * 当前季度的结束时间，即2012-03-31 23:59:59
     * @return
     */
    public static long getQuarterEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getQuarterStartTime());
        cal.add(Calendar.MONTH, 3);
        return cal.getTimeInMillis();
    }


    public static long getYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getYearEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getYearStartTime());
        cal.add(Calendar.YEAR, 1);
        return cal.getTimeInMillis();
    }

    public static long getLastYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getYearStartTime());
        cal.add(Calendar.YEAR, -1);
        return cal.getTimeInMillis();
    }
}
