	package com.loan.wechat.docking.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.HttpUtil;
/**
 * 
 * @author kongzhimin
 *
 */
public class WeixinImgUtil {
	
	/**
	 * 二维码ticket获取接口
	 */
	private static final String getTicket = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
	/**
	 * 二维码图片获取接口
	 */
	private static final String createQrcode = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";
	/**
	 * 临时二维码 数字参数
	 */
	public static final int TEMPORARY_NUM = 1;
	/**
	 * 临时二维码 字符串参数
	 */
	public static final int TEMPORARY_STR = 2;
	/**
	 * 永久二维码 数字参数
	 */
	public static final int LIMIT_NUM = 3;
	/**
	 * 永久二维码 字符串参数
	 */
	public static final int LIMIT_STR = 4;
	
	/**
	 * 封装二维码ticket获取参数
	 * @param expire_seconds  二维码有限时间 仅临时使用
	 * @param qrcodeParam 二维码携带参数
	 * @param type 二维码类型
	 * @return
	 */
	private static String getTicketParams(long expire_seconds,Object qrcodeParam,int type){
		JSONObject params = new JSONObject();
		if(type == TEMPORARY_NUM|| type ==TEMPORARY_STR) {
			params.put("expire_seconds", expire_seconds);
			if(type == TEMPORARY_NUM) {
				params.put("action_name", "QR_SCENE");
			}else {
				params.put("action_name", "QR_STR_SCENE");
			}
		}else if(type == LIMIT_NUM|| type ==LIMIT_STR){
			if(type == LIMIT_NUM) {
				params.put("action_name", "QR_LIMIT_SCENE");
			}else {
				params.put("action_name", "QR_LIMIT_STR_SCENE");
			}
		}
		JSONObject action_info = new JSONObject();
		JSONObject scene = new JSONObject();
		if(type == TEMPORARY_NUM||type == LIMIT_NUM) {
			scene.put("scene_id", qrcodeParam);
		}else {
			scene.put("scene_str", qrcodeParam);
		}
		action_info.put("scene", scene);
		params.put("action_info", action_info);
		return params.toJSONString();
	}
	
	/**
	 * http请求处理 
	 * @param url get请求url
	 * @param stream 输出流
	 */
	public static void httpImgExecute(String url, OutputStream stream) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		try {
			response = httpclient.execute(httpGet);
			entity = response.getEntity();
			InputStream inStream = entity.getContent();
			int b = inStream.read();
			while (b != -1) {
				stream.write(b);
				b = inStream.read();
			}
			stream.flush();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				stream.close();
				EntityUtils.consume(entity);
				response.close();
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}
	
	/**
	 * 拼接二维码获取接口url
	 * @param expire_seconds 二维码有限时间 仅临时使用
	 * @param qrcodeParam 二维码携带参数
	 * @param type 二维码类型
	 * @return
	 */
	private static String getQrcodeUrl(long expire_seconds,Object qrcodeParam,int type,String token) {
		return String.format(createQrcode, getTicket(expire_seconds,qrcodeParam,type,token));
	}
	
	/**
	 * 获取二维码验证ticket
	 * @param expire_seconds 二维码有限时间 仅临时使用
	 * @param qrcodeParam 二维码携带参数
	 * @param type 二维码类型
	 * @return
	 */
	private static String getTicket(long expire_seconds,Object qrcodeParam,int type,String token) {
		return JSON.parseObject(HttpUtil.postForObject(getTicketUrl(token),  getTicketParams(expire_seconds,qrcodeParam,type))).getString("ticket");
	}
	
	/**
	 * 拼接二维码验证ticket获取url
	 * @return
	 */
	private static String getTicketUrl(String token) {
		return String.format(getTicket, token);
	}
	
	/**
	 * 获取公众号二维码图片
	 * @param expire_seconds 二维码有限时间 仅临时使用
	 * @param qrcode 二维码携带参数
	 * @param type 二维码类型
	 * @param stream 输出流
	 */
	public static void qrcode(long expire_seconds,Object qrcode,int type,String token,OutputStream stream) {
		httpImgExecute(getQrcodeUrl(expire_seconds,qrcode,type,token),stream);
	}
	
	public static void main1(String[] args) {
		long aa= System.currentTimeMillis()/1000l;
		JSONObject o = new JSONObject();
		o.put("type", "WEB");
		o.put("account_id", 10884533);
		o.put("name", "cofrv");
		o.put("description", "new visitors");
		Map<String,String> map = new HashMap<String,String>();
		map.put("Content-Type", "application/json");
		String doPost = HttpUtil.postForObject("https://api.e.qq.com/v1.1/user_action_sets/add?access_token="+"4189e7f5c2a70d6096f0c0bf3ee18caf"+"&timestamp="+aa+"&nonce=nonce",o);
		System.out.println(doPost);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
//		String doGet = HttpClientProxy.doGet("http://10.5.11.75/wechat/wxcenter/wechat/list/get", null);
//		JSONArray jsonArray = JSON.parseObject(doGet).getJSONArray("datas");
		JSONObject o = new JSONObject();
		o.put("type", "WEB");
		o.put("name", "cofrv");
		o.put("description", "new visitors");
		Map<String,String> map = new HashMap<String,String>();
		map.put("Content-Type", "application/json");
//		for(int i =0;i<jsonArray.size();i++) {
//			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String doPost = HttpUtil.postForObject("https://api.weixin.qq.com/marketing/user_action_sets/add?version=v1.0&access_token="+HttpUtil.getForObject("http://10.5.11.75/wechat/wxcenter/token/get2?wechatId="+"gh_f883a8c05dd5"),o);
			System.out.println(doPost);
//			System.out.println(jsonObject);
			System.out.println("-------------------------------------------");
//		}
	}
	
}
