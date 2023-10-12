package com.crm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.entity.ByteArrayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
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

    public static void putForObject(String url, byte[] param,HttpHeaders httpHeaders) throws URISyntaxException {
        HttpEntity<ByteArrayEntity> requestEntity = new HttpEntity<>(new ByteArrayEntity(param),httpHeaders);
        restTemplate.put(new URI(url),requestEntity);
    }
    
    public static String postForObject(String Url, JSONObject parameter){
         HttpEntity<String> requestEntity = new HttpEntity<>(parameter.toJSONString(), headersJson);
         String result = restTemplate.postForObject(Url,requestEntity,String.class);
         if(logger.isDebugEnabled()){
             logger.debug("url <"+ Url +"> 参数<" + parameter + "> 结果<"+ result +">");
         }
         return result;
    }

    public static String postForObject(String Url, JSONObject parameter,HttpHeaders httpHeaders){
        HttpEntity<String> requestEntity = new HttpEntity<>(parameter.toJSONString(), null == httpHeaders?headersJson:httpHeaders);
        String result = restTemplate.postForObject(Url,requestEntity,String.class);
        if(logger.isDebugEnabled()){
            logger.debug("url <"+ Url +"> 参数<" + parameter + "> 结果<"+ result +">");
        }
        return result;
    }

    public static String postForObject(String Url, String jsonSting){
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonSting, headersJson);
        String result = restTemplate.postForObject(Url,requestEntity,String.class);
        try {
            Map<String, Object> resultMap = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
            });
            result = JSON.toJSONString(resultMap);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        if(logger.isDebugEnabled()){
            logger.debug("url <"+ Url +"> 参数<" + jsonSting + "> 结果<"+ result +">");
        }
        return  result;
    }

    public static String postForObject(String Url, LinkedMultiValueMap<String, String> parameter){
        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameter, headersForm);
        String result = restTemplate.postForObject(Url, requestEntity, String.class);
        try {
            Map<String, Object> resultMap = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
            });
            result = JSON.toJSONString(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(logger.isDebugEnabled()){
            logger.debug("url <"+ Url +"> 参数<" + parameter + "> 结果<"+ result +">");
        }
        return result;
    }

    public static String postFormForObject(String Url, LinkedMultiValueMap<String, Object> parameter,HttpHeaders httpHeaders){
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameter,httpHeaders);
        String result = restTemplate.postForObject(Url,requestEntity,String.class);
        if(logger.isDebugEnabled()){
            logger.debug("url <"+ Url +"> 参数<" + parameter + "> 结果<"+ result +">");
        }
        return result;
    }

    public static String postFormForObject(String Url, LinkedMultiValueMap<String, Object> parameter){
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameter);
        String result = restTemplate.postForObject(Url,requestEntity,String.class);
        if(logger.isDebugEnabled()){
            logger.debug("url <"+ Url +"> 参数<" + parameter + "> 结果<"+ result +">");
        }
        return result;
    }






    public static String getForObject(String Url){
        String result = restTemplate.getForObject(Url,String.class).trim();
        if(logger.isDebugEnabled()){
            logger.debug("url <"+ Url +"> 结果<"+ result +">");
        }
        return result;
    }

    public static void getWithNotResult(String url) {
        if(logger.isDebugEnabled()){
            logger.debug("url <"+ url +">");
        }
        restTemplate.getForObject(url, Object.class);
    }
    
    public static String getForObject(String url,HttpHeaders headersForm) {
    	HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(null, headersForm);
    	ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
    	return exchange.getBody();
    }



//    public static void main(String[] args) throws IOException {
//        String fileName = HttpClientProxy.download("http://crm.dewufagu.com/contract/1_1_262a880ece3f4dada453c3eadba99d6a-德务客户协议.pdf",
//                "C:\\dev\\bangzheng\\重庆通融\\loan-crm\\target\\ttt.pdf");
//    }
}
