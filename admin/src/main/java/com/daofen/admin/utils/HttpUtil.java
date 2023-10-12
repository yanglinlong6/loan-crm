package com.daofen.admin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static RestTemplate restTemplate;

    private static HttpHeaders headersForm;

    private static HttpHeaders headersJson;

    static {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(100000);//ms
        factory.setConnectTimeout(150000);//ms
        headersForm = new HttpHeaders();
        headersForm.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headersForm.add("Accept", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headersJson = new HttpHeaders();
        headersJson.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headersJson.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public static String postForObject(String Url, JSONObject parameter) {
        HttpEntity<String> requestEntity = new HttpEntity<>(parameter.toJSONString(), headersJson);
        String result = restTemplate.postForObject(Url, requestEntity, String.class);
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + Url + "> 参数<" + parameter + "> 结果<" + result + ">");
        }
        return result;
    }

    public static String postForObject(String Url, String jsonSting) {
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonSting, headersJson);
        String result = restTemplate.postForObject(Url, requestEntity, String.class);
        try {
            Map<String, Object> resultMap = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
            });
            result = JSON.toJSONString(resultMap);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + Url + "> 参数<" + jsonSting + "> 结果<" + result + ">");
        }
        return result;
    }

    public static String postForObject(String Url, LinkedMultiValueMap<String, String> parameter) {
        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameter, headersForm);
        String result = restTemplate.postForObject(Url, requestEntity, String.class);
        try {
            Map<String, Object> resultMap = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
            });
            result = JSON.toJSONString(resultMap);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + Url + "> 参数<" + parameter + "> 结果<" + result + ">");
        }
        return result;
    }

    public static String postFormForObject(String Url, LinkedMultiValueMap<String, Object> parameter) {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameter);
        String result = restTemplate.postForObject(Url, requestEntity, String.class);
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + Url + "> 参数<" + parameter + "> 结果<" + result + ">");
        }
        return result;
    }

    public static String getForObject(String Url) {
        String result = restTemplate.getForObject(Url, String.class).trim();
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + Url + "> 结果<" + result + ">");
        }
        return result;
    }

    public static void getWithNotResult(String url) {
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + url + ">");
        }
        restTemplate.getForObject(url, Object.class);
    }
}
