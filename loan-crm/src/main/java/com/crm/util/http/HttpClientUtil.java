package com.crm.util.http;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {


    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
    /**
     * 路由后连接数
     */
    private static final Integer MAX_PER_ROUTE = 2000;
    /**
     * 最大连接数
     */
    private static final Integer MAX_TOTAL = 2000;

    /**
     * 毫秒
     */
    private static final Integer waitTime = 2000;//

    /**
     * 默认字符编码
     */
    private static String defineCharset = "UTF-8";
    /**
     * client全局变量
     */
    private static CloseableHttpClient httpClient;


    private static HttpHeaders headersJson;

    private static RestTemplate restTemplate;

    /**
     * client全局变量初始化静态方法块，先于构造方法执行
     */
    static {
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        SSLConnectionSocketFactory socketFactory = null;
        try {
            sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            socketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
        } catch(Exception e) {
            log.error("初始化Http SSL异常:{}",e.getMessage(),e);
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", socketFactory).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();


        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(100000);//ms
        factory.setConnectTimeout(150000);//ms


        headersJson = new HttpHeaders();
        headersJson.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
        headersJson.add("Accept", "application/json;charset=UTF-8");

        restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    /**
     * Get请求
     * @param url  请求url地址
     * @param params  请求实体对象
     * @return String
     */
    public static String doGet(String url,Object params){
        return doGet(url,params,waitTime,null);
    }


    public static String doGet(String url,Object params,int waitTime,Map<String,String> header){
        HttpGet get = new HttpGet(appendUrlParams(url, params));
        get.setConfig(RequestConfig.custom().setSocketTimeout(waitTime<=0?waitTime:waitTime).setConnectTimeout(waitTime<=0?waitTime:waitTime).build());
        setHttpHeader(get, header);
        return excuteHttpClient(get);
    }


    public static String doPostForm(String url,Object params,String charset) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        List paramList = new ArrayList<>();
        try{
            JSONObject json = JSONUtil.toJSON(params);
            Iterator<String> iterator = json.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                paramList.add(new BasicNameValuePair(key, json.get(key).toString()));
            }
            post.setEntity(new UrlEncodedFormEntity(paramList, StringUtils.isBlank(charset)?defineCharset:charset));
            return excuteHttpClient(post);
        }catch (UnsupportedEncodingException e){
            throw new CrmException(CrmConstant.ResultCode.FAIL,e.getMessage(),e);
        }catch (Exception e ){
            log.error("请求地址:{},构建表单数据异常:{}",url,e.getMessage(),e);
            throw new CrmException(CrmConstant.ResultCode.FAIL,e.getMessage(),e);
        }
    }

    /**
     *
     * @param url
     * @param params
     * @param headers 可以为空,默认是: application/json;charset=UTF-8
     * @return String
     */
    public static String doPostJson(String url,Object params,HttpHeaders headers){
        HttpEntity<String> requestEntity = new HttpEntity<>(JSONUtil.toJSONString(params), null == headers?headersJson:headers);
        String result = restTemplate.postForObject(url, requestEntity, String.class);
        if(log.isDebugEnabled()) {
            log.debug("url <" + url + "> 参数<" + JSONUtil.toJSONString(params) + "> 结果<" + result + ">");
        }
        return result;
    }

    /**
     * 请求方法执行：HttpClient执行http方法，比如：post，get，put等等
     *
     * @param method {@link HttpRequestBase}
     * @return 返回执行http方法结果
     */
    private static String excuteHttpClient(HttpRequestBase method) {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(method);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), defineCharset);
                if(log.isDebugEnabled()) {
                    log.debug("请求地址【{}】，正常返回信息【{}】", method.getURI(), result);
                }
                return result;
            } else {
                String content = EntityUtils.toString(response.getEntity());
                log.error("请求地址【{}】，异常信息【{}】", content, defineCharset);
                return "Http请求异常:" + response.getStatusLine().getStatusCode() + "--" + content;
            }
        } catch(ClientProtocolException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch(IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                method.releaseConnection();
                if(response != null) {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                }
            } catch(IOException e) {
                log.error("请求地址【{}】，异常信息【{}】",method.getURI(), e.getMessage());
            }
        }
        return null;
    }







    /**
     * 请求头设置
     *
     * @param method     {@link HttpRequestBase} HttpPost，HttpGet等是HttpRequestBase的子类
     * @param httpHeader 消息头：map数据类型
     */
    private static void setHttpHeader(HttpRequestBase method, Map<String, String> httpHeader) {
        if(httpHeader == null) {
            return;
        }
        Iterator<String> iterator = httpHeader.keySet().iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            method.addHeader(key, httpHeader.get(key));
        }
    }

    /**
     * Get请求,拼接参数
     * @param url
     * @param params
     * @return
     */
    private static String appendUrlParams(String url, Object params) {
        if(null == params){
            return url;
        }
        if(params instanceof  String && !JSONUtil.isJsonString(params.toString())){
            return  url;
        }
        JSONObject json = JSONUtil.toJSON(params);
        StringBuilder builder = null;
        Iterator<String> iterator = JSONUtil.toJSON(params).keySet().iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            System.out.println(key);
            if(builder == null) {
                builder = new StringBuilder(url);
                builder.append("?");
                builder.append(key);
                builder.append("=");
                builder.append(json.get(key));
            } else {
                builder.append("&");
                builder.append(key);
                builder.append("=");
                builder.append(json.get(key));
            }
        }
        return builder == null ? url : builder.toString();
    }


}
