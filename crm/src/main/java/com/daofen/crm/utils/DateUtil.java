package com.daofen.crm.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    private static final String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 按照给定格式转换给定Date对象
     *
     * @param date    Date日期对象
     * @param pattern 日期转换格式，比如：yyyy-MM-dd HH:mm:ss
     * @return 格式化日期对象
     */
    public static String formatToString(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 获取当前日期是星期几
     *
     * @param date 可以为空，如果不为空则计算给定日期对象是星期几，如果为空，则计算当前日期是星期几
     * @return String 星期几，比如：星期日，星期一，星期二
     */
    public static String getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        int index = 0;
        if(null != date) {
            calendar.setTime(date);
            index = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        } else {
            index = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return weeks[index];
    }


    public static int to10() {
        Long currentTime = System.currentTimeMillis();
        return Integer.valueOf(currentTime.toString().substring(0, 10));
    }


//    public static void main(String[] args){
//
//        System.out.println(DateUtil.to10());
//
//    }

}
