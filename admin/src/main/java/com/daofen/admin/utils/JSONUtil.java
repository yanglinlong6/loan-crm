package com.daofen.admin.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * JSON工具类
 */
public class JSONUtil {


    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final Logger LOG = LoggerFactory.getLogger(JSONUtil.class);


    /**
     * 处理对象输出带换行空格格式的JSON字符串，用于记录日志。
     * { "code" : "", "desc" : "" } 格式化带换行型字符串
     *
     * @param o {@link Object} 待格式化的数据对象
     * @return {@link String} 格式化后的字符串
     * @author zhangqiuping
     */
    public static String format(Object o) {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentArraysWith(new DefaultIndenter());
        try {
            return objectMapper.writer(printer).writeValueAsString(o);
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON转对象
     *
     * @param json json字符串
     * @param type java类类型
     * @return <T>T
     */
    public static <T> T toJavaBean(String json, Class<T> type) {
        try {
            if(StringUtils.isBlank(json) || null == type) {
                return null;
            }
            return JSONObject.toJavaObject(JSONObject.parseObject(json), type);
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }

    }

    /**
     * json字符串转化为list
     *
     * @param <T>
     * @param content
     * @return <T>List<T>
     * @throws IOException
     */
    public static <T> List<T> toJavaBeanList(String content, Class<T> clazz) {
        try {
            JSONArray jsonArray = JSONArray.parseArray(content);
            return jsonArray.toJavaList(clazz);
        } catch(Exception e) {
            throw new RuntimeException("json to list error.", e);
        }
    }

    /**
     * 将数据对象转换为JSONArray对象
     *
     * @param o {@link Object} 待转换的数据对象
     * @return {@link JSONArray}
     * @author zhangqiuping
     */
    public static JSONArray toJsonArray(Object o) {
        if(null == o)
            return null;
        if(!isJsonString(o.toString())) {
            return null;
        }
        return JSONArray.parseArray(toString(o));
    }

    /**
     * 判断字符串是否是json格式的字符串
     *
     * @param string json格式字符串
     * @return boolean true(是json格式的字符串)，false(不是json格式的字符串)
     */
    public static boolean isJsonString(String string) {
        if(StringUtils.isBlank(string)) {
            return false;
        }
        return JSONObject.isValid(string);
    }

    /**
     * 转json字符串
     *
     * @param o {@link Object} 数据对象
     * @return String
     * @author zhangqiuping
     */
    public static String toString(Object o) {
        return JSONObject.toJSONString(o);
    }

    /**
     * 对象转JSON
     *
     * @param value
     * @return
     */
    public static JSONObject toJSON(Object value) {
        return JSONObject.parseObject(value.toString());
    }

}
