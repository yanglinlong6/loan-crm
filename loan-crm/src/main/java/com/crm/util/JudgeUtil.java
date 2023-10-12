package com.crm.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 判断工具类
 */
public class JudgeUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JudgeUtil.class);

    public static boolean in(Byte currentValue, Byte... values) {
        if(null == values || values.length <= 0 || null == currentValue) {
            return false;
        }
        for(Byte value : values) {
            if(currentValue.intValue() == value.intValue()) {
                return true;
            }
        }
        return false;
    }

    public static boolean in(Long currentValue, Byte... values) {
        if(null == values || values.length <= 0 || null == currentValue) {
            return false;
        }
        for(Byte value : values) {
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

    public static boolean in(Long currentValue, Long... values) {
        if(null == values || values.length <= 0 || null == currentValue) {
            return false;
        }
        for(Long value : values) {
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

    /**
     * 判断字符串是否包含字符
     * @param currentValue 当前字符串
     * @param values 判断参数,是否包含这些字符
     * @return String  返回包含的字符
     */
    public static String contain(String currentValue, String... values) {
        if(StringUtils.isBlank(currentValue) || null == values || values.length <= 0){
            return null;
        }
        for(String value : values){
            if(currentValue.contains(value)){
                return value;
            }
        }
        return null;
    }

    /**
     * 判断字符串是否包含字符
     * @param currentValue 当前字符串
     * @param values 判断参数,是否包含这些字符
     * @return String  返回包含的字符
     */
    public static boolean contain2(String currentValue, String... values) {
        if(StringUtils.isBlank(currentValue) || null == values || values.length <= 0){
            return false;
        }
        for(String value : values){
            if(currentValue.contains(value)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字段字符串以某些字符结尾:字符:是或者的关系
     * @param str 字符你
     * @param values 被包含的值
     * @return boolean true-包含, false-不包含
     */
    public static boolean endWith(String str,String...values){
        if(StringUtils.isBlank(str) || null == values || values.length == 0 ){
            return false;
        }
        boolean endWith=false;
        for(String value : values ){
            endWith = str.endsWith(value);
            if(endWith)
                return endWith;
        }
        return endWith;
    }


    public static boolean isNumber(Object o){
        if(null == o){
            return false;
        }
        String s = o.toString();
        try {
            if(s.contains(".")){
                Double.valueOf(s);
            }else {
                Long.valueOf(s);
            }
            return true;
        }catch (Exception e){
            LOG.error("判断对象是否是数字:{}[非数字]",s);
            return false;
        }
    }


//    public static void main(String[] args){
//
//        System.out.println(JudgeUtil.in("1","0","2","3"));
//    }

}
