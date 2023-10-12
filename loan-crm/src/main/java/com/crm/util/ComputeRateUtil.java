package com.crm.util;

import java.math.BigDecimal;

/**
 * 计算比率工具类
 */
public class ComputeRateUtil {

    private static final String model = "(%s)%s%";
    public static String computeRate(Integer up, Integer down){
        if(null == up || up.intValue() <= 0){
            return "(0)0%";
        }
        if(null == down || down.intValue() <= 0){
            return "(0)0%";
        }
        if(down.intValue() > up.intValue()){
            return "("+up+")"+100+"%";
        }
        double value = BigDecimal.valueOf(up).divide(BigDecimal.valueOf(down),4,BigDecimal.ROUND_HALF_UP).doubleValue()*100;
        return  "("+up+")"+value+"%";
    }
}
