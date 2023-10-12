package com.crm.util.http;


import com.crm.util.JSONUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体类转化Map工具
 *
 * @author kongzhimin
 */
public class HttpParamsMapUtil {
    /**
     * 实体类转化操作：将java实体对象转换为map数据类型
     *
     * @param params {@link Object} 实体对象
     * @return {@link Map<String,String>}
     */
    public static Map<String, String> excuct(Object params) {
        if(null == params) {
            return new HashMap<String, String>();
        }
        Class<? extends Object> class1 = params.getClass();
        Field[] declaredFields = class1.getDeclaredFields();
        Map<String, String> result = new HashMap<String, String>();
        for(Field field : declaredFields) {
            String fieldName = field.getName();
            result.put(fieldName, getParamValue(fieldName, params));
        }
        return result;
    }

    /**
     * 获取字段value
     *
     * @param fieldName
     * @param params
     * @return
     */
    private static String getParamValue(String fieldName, Object params) {
        try {
            Method method = params.getClass().getMethod("get" + toUpperCase4Index(fieldName), new Class[]{});
            Object value = method.invoke(params, new Object[]{});
            return JSONUtil.toJSONString(value);
        } catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 首字母大写
     *
     * @param string
     * @return
     */
    public static String toUpperCase4Index(String string) {
        char[] methodName = string.toCharArray();
        methodName[0] = toUpperCase(methodName[0]);
        return String.valueOf(methodName);
    }

    /**
     * 字符转成大写
     *
     * @param chars
     * @return
     */
    public static char toUpperCase(char chars) {
        if(97 <= chars && chars <= 122) {
            chars ^= 32;
        }
        return chars;
    }

}
