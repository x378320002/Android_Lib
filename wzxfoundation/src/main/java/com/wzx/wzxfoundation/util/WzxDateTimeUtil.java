package com.wzx.wzxfoundation.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wangzixu on 2017/8/21.
 */
public class WzxDateTimeUtil {
    public static final long MINUTE_IN_SECOND = 60;
    public static final long HOUR_IN_SECOND = MINUTE_IN_SECOND * 60;
    public static final long DAY_IN_SECOND = HOUR_IN_SECOND * 24;
    public static final long WEEK_IN_SECOND = DAY_IN_SECOND * 7;
    public static final long MONTH_IN_SECOND = DAY_IN_SECOND * 30;
    public static final long YEAR_IN_SECOND = MONTH_IN_SECOND * 12;

    public static String getCurrentSimpleData() {
        // HH:mm:ss
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(new Date());
    }

    public static String getCurrentLongTime() {
        // HH:mm:ss
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formatter.format(new Date());
    }

    /**
     * 转换日期 转换为更为人性化的时间，单位秒
     * @return
     */
    public static String translateDate(long time) {
        Calendar today = Calendar.getInstance();    //今天
        long curTime = today.getTimeInMillis()/1000; //当前时间
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        long todayStartTime = today.getTimeInMillis() / 1000;
        if (time >= todayStartTime) { //说明是今天
            long d = curTime - time;
            if (d <= 0) {
                return "刚刚";
            } else if (d <= MINUTE_IN_SECOND) {
                return d + "秒";
            } else if (d <= HOUR_IN_SECOND) {
                long m = d / MINUTE_IN_SECOND;
                return m + "分钟";
            } else {
                long h = d / HOUR_IN_SECOND;
                return h + "小时";
            }
        } else {
            long delta = todayStartTime - time;
            if (delta < DAY_IN_SECOND) {
                return "1天前";
            } else if (delta <= WEEK_IN_SECOND) {
                int i = (int) (delta/DAY_IN_SECOND);
                return i + "天前";
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(time * 1000);
                String dateStr = dateFormat.format(date);
                return dateStr;
            }
        }
    }

    /**
     * 显示模式, xx周, xx天, xx小时, xx分钟
     * @param time
     * @return
     */
    public static String transTimeForMessageV3(long time) {
        Calendar today = Calendar.getInstance();    //今天
        long curTime = today.getTimeInMillis()/1000;

        long delta = curTime - time;
//        if (delta >= YEAR_IN_SECOND) {
//            long l = delta / YEAR_IN_SECOND;
//            return l + "年";
//        } else if (delta >= MONTH_IN_SECOND) {
//            long l = delta / MONTH_IN_SECOND;
//            return l + "月";
//        } else
        if (delta >= WEEK_IN_SECOND) {
            long l = delta / WEEK_IN_SECOND;
            return l + "周";
        } else if (delta >= DAY_IN_SECOND) {
            long l = delta / DAY_IN_SECOND;
            return l + "天";
        } else if (delta >= HOUR_IN_SECOND) {
            long l = delta / HOUR_IN_SECOND;
            return l + "小时";
        } else {
            long l = delta / MINUTE_IN_SECOND;
            if (l <= 0) {
                return delta + "秒";
            }
            return l + "分钟";
        }
    }
}
