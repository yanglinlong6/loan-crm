package com.help.loan.distribute.service.api.utils;

import org.springframework.util.StringUtils;

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

    public static boolean in(Byte currentValue, Byte... values) {
        if(null == values || values.length <= 0 || null == currentValue) {
            return false;
        }
        for(Byte value : values) {
            if(null == value)
                continue;
            if(currentValue.byteValue() == value.byteValue()) {
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
        if(null == values || values.length <= 0 || null == currentValue) {
            return false;
        }
        for(String value : values) {
            if(currentValue.equals(value)) {
                return true;
            }
        }
        return false;
    }




    public static boolean contain(String currentString,String... values ){
        if(StringUtils.isEmpty(currentString))
            return false;
        if(null == values || values.length == 0){
            return false;
        }
        for(String value : values){
            if(currentString.contains(value)){
                return true;
            }
        }
        return false;
    }

    public static boolean startWith(String currentValue, String... values) {
        if(null == values || values.length <= 0 || null == currentValue) {
            return false;
        }
        for(String value : values) {
            if(currentValue.startsWith(value)) {
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
