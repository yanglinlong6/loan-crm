package com.loan.cps.common;

import org.apache.commons.lang3.StringUtils;

/**
 * 判断工具类
 */
public class JudgeUtil {

    /**
     * 判断currentValue 是否在values数组中
     *
     * @param currentValue 当前需要验证的值
     * @param values       当前需要验证的是否在该数组中
     * @return values是否包含currentValue
     */
    public static boolean in(Integer currentValue, Integer... values) {
        if(null == values || values.length <= 0 || null == currentValue) {
            return false;
        }
        for(Integer value : values) {
            if(currentValue.intValue() == value.intValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断currentValue 是否在values数组中
     *
     * @param currentValue 当前需要验证的值
     * @param values       当前需要验证的是否在该数组中
     * @return values是否包含currentValue
     */
    public static boolean in(String currentValue, String... values) {
        if(null == values || values.length <= 0 || StringUtils.isBlank(currentValue)) {
            return false;
        }
        for(String value : values) {
            if(currentValue.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String currentValue,String... values){
        if(null == values || values.length <= 0 || StringUtils.isBlank(currentValue)) {
            return false;
        }
        for(String value : values) {
            if(currentValue.contains(value)) {
                return true;
            }
        }
        return false;
    }


//    public static void main(String[] args){
//
//        System.out.println(JudgeUtil.in("1","0","2","3"));
//    }

}
