package com.crm.util;

import com.crm.common.CrmConstant;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.DATA_CONVERSION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String yyyymmdd = "yyyy-MM-dd";

    public static final String yyyymmdd2 = "yyyy年MM月dd日";

    private static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 计算两个日期之间相差多少天
     * @param start Date开始日期
     * @param end   Date结束日期
     * @return
     */
    public static int computeDayDifference(Date start,Date end){
        try{
            long startLong = start.getTime();
            long endLong = end.getTime();
            int days = (int) ((endLong - startLong) / (1000 * 60 * 60 * 24));
            return days;
        }catch (Exception e){
            log.error("计算两个日期之间相差多少天异常：{}-{}-{}",start,end,e.getMessage(),e);
            return 0;
        }
    }

    /**
     * 格式化当前日期
     * @param format 日期格式
     * @return 格式化后的额日期
     */
    public static String getCurrentDate(String format){
        if(StringUtils.isBlank(format)){
            format = yyyyMMddHHmmss;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 计算开始日期：
     * @param startDate 开始日期：2021-05-30
     * @return 例如：2021-05-30 00:00:00
     */
    public static String cumputeStartDate(String startDate) {
        if(StringUtils.isBlank(startDate)){
            startDate = computeMonthDay(new Date(),1,yyyymmdd);
        }
        if(startDate.endsWith(CrmConstant.Date.START))
            return startDate;
        return startDate +CrmConstant.Date.START;
    }

    /**
     * 计算结束日期
     * @param endDate 结算日期：2021-05-31
     * @return 例如：2021-05-30 23:59:59
     */
    public static String computeEndDate(String endDate){
        if(StringUtils.isBlank(endDate)){
            endDate = getCurrentDate(yyyymmdd);
        }
        if(endDate.endsWith(CrmConstant.Date.END))
            return endDate;
        return endDate +CrmConstant.Date.END;
    }

    /**
     * 计算当前星期中 星期几的日期
     * @param date Date
     * @param weekDay  星期几(1-星期日,2-星期一,3-星期二 以此类推)
     * @param formatStr 日期格式
     * @return String 格式化后的日期
     */
    public static String computeWeekDay(Date date,int weekDay,String formatStr){
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK,weekDay);
        String importDate = format.format(cal.getTime());
        return importDate;
    }

    public static String computeMonthDay(Date date,int day,String formatStr){
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime((date == null ? new Date():date));
        cal.set(Calendar.DAY_OF_MONTH,day);
        String importDate = format.format(cal.getTime());
        return importDate;
    }


}
