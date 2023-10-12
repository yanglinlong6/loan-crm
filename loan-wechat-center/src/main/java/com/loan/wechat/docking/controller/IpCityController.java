package com.loan.wechat.docking.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.json.JSONUtil;
import com.loan.wechat.user.UserAptitudePO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.common.Result;

@RestController
public class IpCityController {
	
	public static final String URL = "http://ip.ws.126.net/ipquery?ip=";
	
	private static Log logger = LogFactory.getLog(IpCityController.class);

	private static final Logger LOG = LoggerFactory.getLogger(IpCityController.class);

	private static final String TEMPLATE = "http://apis.juhe.cn/ip/ipNewV3?ip=%s&key=d4dc4468ba619be3e2fd944a9c1caaf9";

	@RequestMapping(value = "/common/getIp")
	public Result ipCity(HttpServletRequest request) {
		JSONObject cc = new JSONObject();
		cc.put("city", setIpCity(null,request));
		return Result.success(cc);
	}

	public String setIpCity(UserAptitudePO po, HttpServletRequest request) {
		try {
			String url = String.format(TEMPLATE,getIpAddress(request));
			String forObject = HttpUtil.getForObject(url);
			LOG.info("[聚合]获取ip城市结果:{}",forObject);
			JSONObject jsonObject = JSONObject.parseObject(forObject);
			String resultcode = jsonObject.getString("resultcode");
			if("200".equals(resultcode)){
				String city = jsonObject.getJSONObject("result").getString("City");
				if (!city.endsWith("市")){
					city = city+"市";
				}
				if(null != po){
					po.setIpLocation(city);
				}
				return city;
			}else{
				forObject = HttpUtil.getForObject("http://ip.ws.126.net/ipquery?ip="+getIpAddress(request));
				String city = getCity(forObject);
				if(StringUtils.isEmpty(city)) {
					String forObject2 = HttpUtil.getForObject("http://whois.pconline.com.cn/ipJson.jsp?ip="+getIpAddress(request)+"&json=true");
					JSONObject parseObject = JSON.parseObject(forObject2);
					city = parseObject.getString("city");
				}
				if(null != po){
					po.setIpLocation(city);
				}
				return city;
			}
		} catch (Exception e) {
			LOG.error("获取ip城市异常:{}",e.getMessage(),e);
			return null;
		}
	}
	
	private String getIpAddress(HttpServletRequest request) {
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
	
	private String getCity(String result) {
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
