package com.help.loan.distribute.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static RestTemplate restTemplate;

    private static HttpHeaders headersJson;

    private static HttpHeaders headersForm;

    private static HttpHeaders formData;

    private static  final HttpClient client = HttpClients.createDefault();


    static {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(100000);//ms
        factory.setConnectTimeout(150000);//ms

        headersForm = new HttpHeaders();
        headersForm.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headersForm.add("Accept", "application/json;charset=UTF-8");

        headersJson = new HttpHeaders();
        headersJson.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
        headersJson.add("Accept", "application/json;charset=UTF-8");

        formData = new HttpHeaders();
        formData.setContentType(MediaType.MULTIPART_FORM_DATA);

        restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }


    /**
     * post请求:默认请求内容是application/json;charset=UTF-8
     * @param url 请求url地址
     * @param parameter JSONObject
     * @return 返回请求响应结果:String
     */
    public static String postForJSON(String url, JSONObject parameter) {
        HttpEntity<String> requestEntity = new HttpEntity<>(parameter.toJSONString(), headersJson);
        String result = restTemplate.postForObject(url, requestEntity, String.class);
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + url + "> 参数<" + parameter + "> 结果<" + result + ">");
        }
        return result;
    }

    /**
     * post请求:默认请求内容是application/json;charset=UTF-8
     * @param url 请求url地址
     * @param parameter JSONObject
     * @return 返回请求响应结果:String
     */
    public static String postForObject(String url, JSONObject parameter) {
        try{
            HttpEntity<String> requestEntity = new HttpEntity<>(parameter.toJSONString(), headersJson);
            String result = restTemplate.postForObject(url, requestEntity, String.class);
            if(logger.isDebugEnabled()) {
                logger.debug("url <" + url + "> 结果<" + result + ">");
            }
            return result;
        }catch (Exception e){
            return e.getMessage();
        }
    }

    /**
     * post请求:默认请求内容是application/json;charset=UTF-8
     * @param url 请求url地址
     * @param jsonSting
     * @return 返回请求响应结果:String
     */
    public static String postForJSON(String url, String jsonSting) {
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonSting, headersJson);
        String result = restTemplate.postForObject(url, requestEntity, String.class);
        try {
            Map<String, Object> resultMap = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
            });
            result = JSON.toJSONString(resultMap);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + url + "> 参数<" + jsonSting + "> 结果<" + result + ">");
        }
        return result;
    }

    /**
     *
     * @param url
     * @param parameter
     * @param headers
     * @return
     */
    public static String postForJSON(String url, JSONObject parameter, HttpHeaders headers) {
        HttpEntity<String> requestEntity = new HttpEntity<>(parameter.toJSONString(), headers);
        String result = restTemplate.postForObject(url, requestEntity, String.class);
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + url + "> 参数<" + parameter + "> 结果<" + result + ">");
        }
        return result;
    }



    public static String postForFormUrlencoded(String Url, LinkedMultiValueMap<String, Object> parameter) {
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameter, headersForm);
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

    public static String postFormForObject(String url, JSONObject parameter) {

        LinkedMultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        if(null != parameter && !parameter.isEmpty()){
            Iterator<String> it = parameter.keySet().iterator();
            while (it.hasNext()){
                String key = it.next();
                paramMap.add(key,parameter.get(key));
            }
        }
        return postFormForObject(url,paramMap);
    }

    public static String postFormForObject(String url, LinkedMultiValueMap<String, Object> parameter) {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameter);
        String result = restTemplate.postForObject(url, requestEntity, String.class);
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + url + "> 参数<" + parameter + "> 结果<" + result + ">");
        }
        return result;
    }


    public static String getForObject(String url) {
        try{
            String result = restTemplate.getForObject(url, String.class).trim();
            if(logger.isDebugEnabled()) {
                logger.debug("url <" + url + "> 结果<" + result + ">");
            }
            return result;
        }catch (Exception e){
            logger.error("{}--->请求异常:{}",url,e.getMessage(),e);
            return null;
        }

    }

    public static String postFormData(String url,Object data){
        try{
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            JSONObject jsonObject = JSONUtil.toJSON(data);
            Iterator<String> iterator = jsonObject.keySet().iterator();
            ContentType contentType = ContentType.MULTIPART_FORM_DATA;
            contentType = contentType.withCharset("UTF-8");
            while(iterator.hasNext()){
                String key = iterator.next();
                Object value = jsonObject.get(key);
                if(null == value){
                    value = "";
                }
                entityBuilder.addTextBody(key, value.toString(),contentType);
            }
            org.apache.http.HttpEntity entity = entityBuilder.setCharset(Charset.forName("UTF-8")).build();
            HttpPost post = new HttpPost(url);
            post.setEntity(entity);
            String response = EntityUtils.toString(client.execute(post).getEntity());
            return response;
        }catch (Exception e){
            logger.error("form-data异常：{}",e.getMessage(),e);
            return new JSONObject().put("form-data异常",e.getMessage()).toString();
        }
    }

    public static String postFormData(String url,LinkedMultiValueMap<String, Object> parameter){
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameter, formData);
        String result = restTemplate.postForObject(url, requestEntity, String.class);
        try {
            Map<String, Object> resultMap = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
            });
            result = JSON.toJSONString(resultMap);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + url + "> 参数<" + parameter + "> 结果<" + result + ">");
        }
        return result;
    }

    public static void getWithNotResult(String url) {
        if(logger.isDebugEnabled()) {
            logger.debug("url <" + url + ">");
        }
        restTemplate.getForObject(url, Object.class);
    }


    public static String convertUnicodeToCh(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\w{4}))");
        Matcher matcher = pattern.matcher(str);
        // 迭代，将str中的所有unicode转换为正常字符
        while (matcher.find()) {
            String unicodeFull = matcher.group(1); // 匹配出的每个字的unicode，⽐如\u67e5
            String unicodeNum = matcher.group(2); // 匹配出每个字的数字，⽐如\u67e5，会匹配出67e5
            // 将匹配出的数字按照16进制转换为10进制，转换为char类型，就是对应的正常字符了
            char singleChar = (char) Integer.parseInt(unicodeNum, 16);
            // 替换原始字符串中的unicode码
            str = str.replace(unicodeFull, singleChar + "");
        }
        return str;
    }
}
