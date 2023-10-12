package com.crm.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
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
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * httpClient请求工具类
 *
 * @author kongzhimin
 */
public class HttpClientProxy {

    /**
     * 路由后连接数
     */
    private static final Integer MAX_PER_ROUTE = 2000;
    /**
     * 最大连接数
     */
    private static final Integer MAX_TOTAL = 2000;

    private static final Logger log = LoggerFactory.getLogger(HttpClientProxy.class);
    /**
     * 默认字符编码
     */
    private static String CHARSET = "UTF-8";
    /**
     * client全局变量
     */
    private static CloseableHttpClient httpClient;

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
            e.printStackTrace();
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", socketFactory).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    /**
     * HTTP get请求重载
     *
     * @param url    请求地址
     * @param object 实体对象
     * @return String 返回get请求结果
     */
    public static String doGet(String url, Object object) {
        return doGet(url, HttpParamsMapUtil.excuct(object));
    }

    /**
     * HTTP get请求重载
     *
     * @param url    请求地址
     * @param params 请求参数:map数据类型
     * @return String 返回get请求结果
     */
    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, params, null, null);
    }

    /**
     * HTTP get请求重载
     *
     * @param url     请求地址
     * @param params  请求参数:map数据类型
     * @param charset 字符编码
     * @param maxWait 等到响应超时时间
     * @return String 返回get请求结果
     */
    public static String doGet(String url, Map<String, String> params, String charset, Integer maxWait) {
        return doGet(url, params, charset, maxWait, null);
    }

    /**
     * HTTP get请求
     *
     * @param url        请求地址
     * @param params     请求参数:map数据类型
     * @param charset    字符编码
     * @param maxWait    等到响应超时时间
     * @param httpHeader 消息头：map数据类型
     * @return String 返回请求结果
     */
    public static String doGet(String url, Map<String, String> params, String charset, Integer maxWait, Map<String, String> httpHeader) {
        return excuteHttpClient(defineHttpGet(url, params, charset, maxWait, httpHeader));
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
                String result = EntityUtils.toString(response.getEntity(), CHARSET);
                if(log.isDebugEnabled()) {
                    log.debug("请求地址【{}】，正常返回信息【{}】", method.getURI(), result);
                }
                return result;
            } else {
                String content = EntityUtils.toString(response.getEntity());
                log.error("请求地址【{}】，异常信息【{}】", content, CHARSET);
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
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * get请求方法对象封装
     *
     * @param url        请求地址
     * @param params     请求参数：map数据类型
     * @param charset    字符编码
     * @param maxWait    等待响应超时时间
     * @param httpHeader 请求头
     * @return {@code HttpGet}  get请求对象
     */
    private static HttpGet defineHttpGet(String url, Map<String, String> params, String charset, Integer maxWait, Map<String, String> httpHeader) {
        HttpGet get = new HttpGet(appendUrlParams(url, params));
        get.setConfig(getRequestConfig(maxWait));
        setHttpHeader(get, httpHeader);
        return get;
    }

    /**
     * get请求url拼接
     *
     * @param url    请求url地址
     * @param params 请求参数:map数据类型
     * @return 返回拼接好的带参数的url地址，用于get请求
     */
    private static String appendUrlParams(String url, Map<String, String> params) {
        if(CollectionUtils.isEmpty(params))
            return url;
        StringBuilder builder = null;
        Iterator<String> iterator = params.keySet().iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            System.out.println(key);
            if(builder == null) {
                builder = new StringBuilder(url);
                builder.append("?");
                builder.append(key);
                builder.append("=");
                builder.append(params.get(key));
            } else {
                builder.append("&");
                builder.append(key);
                builder.append("=");
                builder.append(params.get(key));
            }
        }
        return builder == null ? url : builder.toString();
    }

    /**
     * request超时时间设置
     *
     * @param maxWait 等待超时时间，可自定义
     * @return {@link RequestConfig}
     */
    private static RequestConfig getRequestConfig(Integer maxWait) {
        Integer waitTime = maxWait == null ? 2000 : maxWait;
        return RequestConfig.custom().setSocketTimeout(waitTime).setConnectTimeout(waitTime).build();
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

    public static String doPost(String url, String params, String charset, Integer maxWait, Map<String, String> httpHeader) {
        return excuteHttpClient(defineHttpPost(url, pakegeStringEntity(params, charset == null ? CHARSET : charset), charset, maxWait, httpHeader));
    }

    /**
     * post请求方案封装
     *
     * @param url
     * @param charset    字符编码格式
     * @param maxWait    等待超时时间，可自定义
     * @param httpHeader 消息头
     * @return HttpPost {@link HttpPost} 返回构建好的HttpPost对象
     */
    private static HttpPost defineHttpPost(String url, HttpEntity entity, String charset, Integer maxWait, Map<String, String> httpHeader) {
        HttpPost post = new HttpPost(url);
        post.setConfig(getRequestConfig(maxWait));
        post.setEntity(entity);
        setHttpHeader(post, httpHeader);
        return post;
    }

    /**
     * entity参数封装
     *
     * @param params
     * @param charset
     * @return
     */
    private static StringEntity pakegeStringEntity(String params, String charset) {
        return new StringEntity(params, charset);
    }

    public static String doPost(String url, Map<String, File> params, Map<String, String> map, String charset, Integer maxWait, Map<String, String> httpHeader) {
        return excuteHttpClient(defineHttpPost(url, pakegeHttpFileEntity(params, map, charset == null ? CHARSET : charset), charset, maxWait, httpHeader));
    }

    /**
     * entity参数封装
     *
     * @param charset 字符编码格式
     * @return {@link UrlEncodedFormEntity}
     */
    private static HttpEntity pakegeHttpFileEntity(Map<String, File> Map, Map<String, String> paramsMap, String charset) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置浏览器兼容模式
        if(Map != null) {
            Iterator<String> iterator = Map.keySet().iterator();
            while(iterator.hasNext()) {
                String next = iterator.next();
                builder.addBinaryBody(next, Map.get(next));// 设置请求参数
            }
        }
        if(paramsMap != null) {
            Iterator<String> iterator = paramsMap.keySet().iterator();
            while(iterator.hasNext()) {
                String next = iterator.next();
                builder.addTextBody(next, paramsMap.get(next));// 设置请求参数
            }
        }
        HttpEntity entity = builder.build();// 生成 HTTP POST 实体
        return entity;
    }

    public static String doPost(String url, String content, ContentType contentType, String charset, Integer maxWait, Map<String, String> httpHeader) {
        return excuteHttpClient(defineHttpPost(url, packageHttpEntity(content, contentType), charset, maxWait, httpHeader));
    }

    private static ByteArrayEntity packageHttpEntity(String content, ContentType contentType) {
        ByteArrayEntity entity = new ByteArrayEntity(content.getBytes(), contentType);
        return entity;
    }

    /**
     * 执行post请求重载方法:默认等待响应超时时间2秒
     *
     * @param url    请求地址
     * @param object 实体对象
     * @return String 返回请求结果
     */
    public static String doPost(String url, Object object) {
        return doPost(url, HttpParamsMapUtil.excuct(object));
    }

    /**
     * 执行post请求重载方法:默认等待响应超时时间2秒
     *
     * @param url    请求地址
     * @param params 请求参数:map数据类型
     * @return String 返回请求结果
     */
    public static String doPost(String url, Map<String, Object> params) {
        return doPost(url, params, null, null);
    }

    /**
     * 执行post请求重载方法
     *
     * @param url     请求地址
     * @param params  请求参数:map数据类型
     * @param charset 字符编码
     * @param maxWait 等到响应超时时间
     * @return String 返回请求结果
     */
    public static String doPost(String url, Map<String, Object> params, String charset, Integer maxWait) {
        return doPost(url, params, charset, maxWait, null);
    }

    /**
     * 执行post请求
     *
     * @param url        请求地址
     * @param params     请求参数:map数据类型
     * @param charset    字符编码
     * @param maxWait    等到响应超时时间
     * @param httpHeader 消息头：map数据类型
     * @return String 返回请求结果
     */
    public static String doPost(String url, Map<String, Object> params, String charset, Integer maxWait, Map<String, String> httpHeader) {
        return excuteHttpClient(defineHttpPost(url, packageHttpEntity(params, charset == null ? CHARSET : charset), charset, maxWait, httpHeader));
    }

    public static String doPostWithObject(String url, Map<String, String> params, String charset, Integer maxWait, Map<String, String> httpHeader) {
        return excuteHttpClient(defineHttpPost(url, packageHttpEntityWithObject(params, charset == null ? CHARSET : charset), charset, maxWait, httpHeader));
    }

    private static HttpEntity packageHttpEntityWithObject(Map<String, String> params, String charset) {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(Charset.forName(charset));
//        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
        for(String key : params.keySet()) {
            builder.addTextBody(key, params.get(key));
        }
        return builder.build();
    }

    /**
     * entity参数封装
     *
     * @param params  post请求参数：map数据类型
     * @param charset 字符编码格式
     * @return {@link UrlEncodedFormEntity}
     */
    private static UrlEncodedFormEntity packageHttpEntity(Map<String, Object> params, String charset) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        for(String key : params.keySet()) {
            formparams.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        UrlEncodedFormEntity uefEntity = null;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, charset);
        } catch(UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return uefEntity;
    }


    /**
     *
     * @param url 网络文件地址
     * @return 成功返回本地文件名称
     * @throws IOException
     */
    public static boolean download(String url,String  outPath) {
        try{
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            int size = FileCopyUtils.copy(is,new FileOutputStream(new File(outPath)));
            return true;
        }catch (Exception e){
            log.error("下载网络文件失败:{}",e.getMessage(),e);
            return false;
        }
    }
}