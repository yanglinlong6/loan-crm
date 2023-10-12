package com.loan.cps.controller;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.HttpUtil;
import com.loan.cps.common.JSONUtil;
import com.loan.cps.common.JudgeUtil;
import com.loan.cps.common.MobileLocationUtil;
import com.loan.cps.entity.UserAptitudePO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 客户参数工具类
 */
public class AptitudeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AptitudeUtils.class);
    private static final String TEMPLATE = "http://apis.juhe.cn/ip/ipNewV3?ip=%s&key=5983e9500fb67b899e63404d0fe28c4e";

    /**
     * 设置ip城市
     * @param request HttpServletRequest
     */
    public static String getIpCity(HttpServletRequest request) {
//        String city = null;
//        try {
//            String forObject = HttpUtil.getForObject("http://ip.ws.126.net/ipquery?ip="+getIpAddress(request));
//            city = getCity(forObject);
//            if(StringUtils.isEmpty(city)) {
//                String forObject2 = HttpUtil.getForObject("http://whois.pconline.com.cn/ipJson.jsp?ip="+getIpAddress(request)+"&json=true");
//                JSONObject parseObject = JSON.parseObject(forObject2);
//                city = parseObject.getString("city");
//            }
//            return setIpCity(null,request);
//        } catch (Exception e) {
//            LOG.error("获取ip城市异常:{}",e.getMessage(),e);
//            return city;
//        }
        return setIpCity(null,request);
    }

    public static String setIpCity(UserAptitudePO po,HttpServletRequest request) {
        try {
            if(null == request){
                po.setIpLocation("");
                return null;
            }
            String url = String.format(TEMPLATE,getIpAddress(request));
            String forObject = HttpUtil.getForObject(url);
            JSONObject jsonObject = JSONUtil.toJSON(forObject);
            String resultcode = jsonObject.getString("resultcode");
            if("200".equals(resultcode)){
                String city = jsonObject.getJSONObject("result").getString("City");
                if(StringUtils.isBlank(city)){
                    city = getIpCity(request);
                }
                if (!city.endsWith("市")){
                    city = city+"市";
                }
                if(null != po){
                    po.setIpLocation(city);
                }
                return city;
            }
            if(null != po){
                po.setIpLocation("");
            }
            return "";
        } catch (Exception e) {
            LOG.error("获取ip城市异常:{}",e.getMessage(),e);
            return null;
        }
    }

    public static String getIpCity(String ip) {
        try {
            if(StringUtils.isBlank(ip)){
                return null;
            }
            String url = String.format(TEMPLATE,ip);
            String forObject = HttpUtil.getForObject(url);
            JSONObject jsonObject = JSONUtil.toJSON(forObject);
            String resultcode = jsonObject.getString("resultcode");
            String city="";
            if("200".equals(resultcode)){
                city = jsonObject.getJSONObject("result").getString("City");
            }
            if(StringUtils.isNotBlank(city) && !city.endsWith("市")){
                city += "市";
            }
            return city;
        } catch (Exception e) {
            LOG.error("[getIpCity]获取ip城市异常:{}",e.getMessage(),e);
            return null;
        }
    }




    /**
     * 设置手机归属地
     * @param po UserAptitudePO
     */
    public static String setMobileCity(UserAptitudePO po){
        try{
            JSONObject andCheck = MobileLocationUtil.getAndCheck(po.getMobile());
            if(andCheck != null && andCheck.containsKey("City")) {
                String city = andCheck.getString("City");
                if(null != po)
                    po.setMobileLocation(city);
                return  city;
            }
            return null;
        }catch (Exception e){
            LOG.error("获取手机归属地异常:{}",e.getMessage(),e);
            return null;
        }
    }

    /**
     * 验证手机号码格式
     * @param mobile 手机号码
     * @return true-格式正确 false-格式错误
     */
    public static boolean checkMoblie(String mobile) {
        String regex = "^((13[0-9])|(14[0-9])|(16[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";
        boolean m = false;
        if(mobile.length()==11) {
            m = Pattern.matches(regex, mobile);
        }
        return m;
    }

    public static String getMobileCity(String mobile){
        try{
            JSONObject andCheck = MobileLocationUtil.getAndCheck(mobile);
            LOG.info("[getMobileCity]获取手机归属地:{}--{}",mobile,andCheck);
            if(andCheck != null && andCheck.containsKey("City")) {
                return  andCheck.getString("City");
            }
            return null;
        }catch (Exception e){
            LOG.error("获取手机归属地异常:{}",e.getMessage(),e);
            return null;
        }
    }


    /**
     * 设置百度参数
     * @param po UserAptitudePO
     */
    public static void setBaiduAptitude(UserAptitudePO po){
        po.setChannel(po.getChannel()+"-"+po.getUcid()+"-"+po.getClueId());
        if(JudgeUtil.in(po.getSex(),"男")){
            po.setGender(1);
        }else{
            po.setGender(0);
        }
        if(null == po.getAge() || po.getAge() <22){
            po.setAge(25);
        }else if(po.getAge() >= 60){
            po.setAge(60);
        }
        if(null != po.getAmount() && po.getAmount().doubleValue() > 50000){
            po.setLoanAmount(po.getAmount().toString());
        }
        if(StringUtils.isBlank(po.getLoanAmount())){
            po.setLoanAmount("50000");
        }
        if(StringUtils.isNotBlank(po.getAptitude())){
            String[] array = po.getAptitude().replace("，",",").split(",");
            for(String str : array){
                if(str.contains("公积金")){
                    po.setPublicFund("有，个人月缴300-800元");
                }
                if(str.contains("房")){
                    po.setHouse(1);
                }
                if(str.contains("车")){
                    po.setCar(1);
                }
                if(str.contains("企业") || str.contains("营业执照")){
                    po.setHouse(1);
                }
                if(str.contains("保单")){
                    po.setInsurance(1);
                }
                if(str.contains("代发") || str.contains("银行") || str.contains("工资")){
                    po.setGetwayIncome(1);
                }
            }
        }
        if(StringUtils.isBlank(po.getPublicFund()))
            po.setPublicFund("无");
        if(null == po.getHouse() || po.getHouse() < 0)
            po.setHouse(0);
        if(null == po.getCar() || po.getCar() < 0)
            po.setCar(0);
        if(null == po.getGetwayIncome() || po.getGetwayIncome() < 0)
            po.setGetwayIncome(0);
        if(null == po.getInsurance() || po.getInsurance() < 0)
            po.setInsurance(0);
        if(null == po.getCompany() || po.getCompany() < 0)
            po.setCompany(0);
    }


    public static final String MOBILE_COOKIE = "USER_APPLY_ALREADY";

    /**
     * 验证是否是再次申请
     * @param req HttpServletRequest
     * @return boolean true-表示已申请  false-表示第一次申请
     */
    public static boolean checkAlreadyApply(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if(cookies == null) {
            return false;
        }
        for(Cookie c:cookies) {
            if(c.getName().equals(MOBILE_COOKIE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 页面防多次申请 写入cookie
     * @param res HttpServletResponse
     */
    public static  void setAlreadyApplyCookie(HttpServletResponse res) {
        Cookie c = new Cookie(MOBILE_COOKIE, UUID.randomUUID().toString());
        c.setPath("/");
        c.setMaxAge(24*60*60);
        res.addCookie(c);
    }


    public static String getIpAddress(HttpServletRequest request) {
        if(null == request){
            return null;
        }
        String ip= request.getHeader("x-forwarded-for");
        if(ip !=null) {
            String[] split = ip.split(",");
            ip = split[0];
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if("127.0.0.1".equals(ip)||"0:0:0:0:0:0:0:1".equals(ip)){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip= inet.getHostAddress();
            }
        }
        return ip;
    }

    public static String getCity(String result) {
        try {
            String[] split = result.split("=");
            for(String s:split) {
                boolean contains = s.contains("市");
                if(contains) {
                    String replaceAll = s.replaceAll("[^\u4E00-\u9FA5]", "");
                    String substring = replaceAll.substring(0, replaceAll.indexOf("市")+1);
                    return substring;
                }
            }
        } catch (Exception e) {

        }
        return "";
    }
}
