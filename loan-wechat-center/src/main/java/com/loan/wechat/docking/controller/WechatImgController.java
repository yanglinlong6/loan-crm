package com.loan.wechat.docking.controller;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.loan.wechat.docking.cache.IWechatCache;
import com.loan.wechat.docking.entity.WechatDTO;
import com.loan.wechat.docking.util.WeixinImgUtil;

import cn.hutool.core.util.NumberUtil;


@RestController
public class WechatImgController {
	
	/**
	 * 调测日志记录器。
	 */
	private static final Logger logger = LoggerFactory.getLogger(WechatImgController.class);
	
	/**
	 * 	获取用户信息
	 * @param openId
	 * @return
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	@RequestMapping("/util/img/get")
	@ResponseBody
	public void getTwoDimensionalCodeImg(HttpServletRequest req,HttpServletResponse res) throws NumberFormatException, IOException
	{	
		String serverName = req.getServerName();
		String qrcode = req.getParameter("qrcode");
		if(serverName.startsWith("localhost")) {
			serverName = "zhang.591jq.cn";//测试使用
		}
		String wxtype = req.getParameter("wxtype");//公众号定位信息
		WechatDTO wechatDTOByType = null;
		if(!StringUtils.isEmpty(wxtype)&&NumberUtil.isInteger(wxtype)) {
			wechatDTOByType = IWechatCache.getWechatDTOByType(Integer.valueOf(wxtype));//单域名投放
		}else {
			wechatDTOByType = IWechatCache.getWechatDTOByDomain2(serverName);
		}
		String expire = req.getParameter("expire");
		String type = req.getParameter("type");
		checkParams(qrcode,expire,type);
		try {
			WeixinImgUtil.qrcode(Long.valueOf(expire), qrcode,Integer.valueOf(type) ,wechatDTOByType.getToken() , res.getOutputStream());
		} catch (Exception e) {
			logger.error("qrcode = " +qrcode +" wxtype= "+wxtype +" domain2 = " +serverName + " ip = "+getIpAddr(req));
			throw e;
		}
		
	}
	
	public static String getIpAddr(HttpServletRequest request)
	{
		String ip = request.getHeader("x-forwarded-for");
		logger.debug("x-forwarded-for--->" + ip);
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("Proxy-Client-IP");
			logger.debug("Proxy-Client-IP--->" + ip);
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("WL-Proxy-Client-IP");
			logger.debug("WL-Proxy-Client-IP--->" + ip);
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getRemoteAddr();
			logger.debug("RemoteAddr--->" + ip);
		}
		if(StringUtils.isEmpty(ip))
		{
			return ip;
		}
		String[] ips = ip.split(",");
		return ips[0];
	}
	
	/**
	 * 	获取用户信息
	 * @param openId
	 * @return
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */	
	@RequestMapping("/util/wechat/name/get")
	public String getWechatName(@RequestParam Integer wxtype)
	{	
		return IWechatCache.getWechatDTOByType(Integer.valueOf(wxtype)).getWechat();
	}
	
	private void checkParams(String qrcode,String expire,String type) {
		if(StringUtils.isEmpty(qrcode)||StringUtils.isEmpty(type)) {
			throw new RuntimeException("param err");
		}
		if(!NumberUtil.isInteger(expire)) {
			throw new RuntimeException("param err");
		}
		if(!Pattern.matches("[1,2,3,4]", type)) {
			throw new RuntimeException("param err");
		}
	}
	
}
