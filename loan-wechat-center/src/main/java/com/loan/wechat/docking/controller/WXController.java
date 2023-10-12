package com.loan.wechat.docking.controller;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.RedisClient;
import com.loan.wechat.common.Result;
import com.loan.wechat.docking.cache.IWechatCache;
import com.loan.wechat.docking.cache.WechatListReplace;
import com.loan.wechat.docking.constant.WXConstants;
import com.loan.wechat.docking.dao.WechatBaseDataDao;
import com.loan.wechat.docking.entity.WechatDTO;
import com.loan.wechat.docking.util.WXCenterJointUtil;
import com.loan.wechat.login.dao.WechatUserBindDao;


/**
 * 微信对接中心控制器
 * @author kongzhimin
 *
 */
@RestController
public class WXController {
	
	@Autowired
	private WechatListReplace wechatListReplace;
	
	@Autowired
	private WechatBaseDataDao wechatBaseDataDao;
	
	@Autowired
	private WechatUserBindDao wechatUserBindDao;
	
	private static final Log logger = LogFactory.getLog(WXController.class);
	
	/**
	 * 获取token
	 * @param domain2 公众号二级域名
	 * @param wechatId 公众号原始ID
	 * @return
	 */
	@RequestMapping("/wxcenter/token/get")
	public Result getTokenByDomain(@RequestParam(required=false) String domain2,@RequestParam(required=false) String wechatId,@RequestParam(required=false) String userid)
	{	
		
		JSONObject resultView = new JSONObject();
		String openid = null;
		if(!StringUtils.isEmpty(userid)) {
			openid = wechatUserBindDao.selUserByUserid(userid).getOpenid();
		}
		resultView.put("token", getToken(wechatId,domain2,openid));
		return Result.success(resultView);
	}
	
	/**
	 * 获取token
	 * @param domain2 公众号二级域名
	 * @param wechatId 公众号原始ID
	 * @return
	 */
	@RequestMapping("/wxcenter/token/get2")
	public String getWechatToken(@RequestParam(required=false) String domain2,@RequestParam(required=false) String wechatId,@RequestParam(required=false) String userid) 
	{
		String openid = null;
		if(!StringUtils.isEmpty(userid)) {
			openid = wechatUserBindDao.selUserByUserid(userid).getOpenid();
		}
		return getToken( wechatId, domain2, openid);
	}
	
	/**
	 * 获取公众号信息
	 * @param domain2 公众号二级域名
	 * @param wechatId 公众号原始ID
	 * @return
	 */
	@RequestMapping("/wxcenter/wechat/get")
	public Result getWechatByWechatId(@RequestParam(required=false) String domain2,@RequestParam(required=false) String wechatId,@RequestParam(required=false) String userid)
	{	
		String openid = null;
		if(!StringUtils.isEmpty(userid)) {
			openid = wechatUserBindDao.selUserByUserid(userid).getOpenid();
		}
		return Result.success(getWechatDTO(wechatId,domain2,openid));
	}
	
	/**
	 * 刷新公众号列表缓存
	 * @param 
	 * @return
	 */
	@RequestMapping("/wxcenter/wechat/list/replace")
	public Result replaceWechatList()
	{	
		wechatListReplace.replaceWechatList();
		return Result.success();
	}
	
	/**
	 * 发送微信模版消息
	 * @param wechatId  公众号原始ID
	 * @param domain2  公众号二级域名
	 * @return
	 */
	@RequestMapping("/wxcenter/sendTempMsg")
	public JSONObject sendTempMsg(@RequestBody JSONObject dto,@RequestParam(required=false) String wechatId,@RequestParam(required=false) String domain2)
	{	
		String openid = wechatUserBindDao.selUserByUserid(dto.getString("touser")).getOpenid();
		dto.put("touser", openid);
		return JSONObject.parseObject(WXCenterJointUtil.sendTempMsg(getToken(wechatId,domain2,openid), dto));
	}
	
	/**
	 * 发送微信模版消息(批量)
	 * @param wechatId  公众号原始ID
	 * @param domain2  公众号二级域名
	 * @return
	 */
	@RequestMapping("/wxcenter/sendTempMsgList")
	public JSONArray sendTempMsg(@RequestBody List<JSONObject> list,@RequestParam(required=false) String wechatId,@RequestParam(required=false) String domain2)
	{	
		JSONArray result = new JSONArray();
		for(JSONObject dto:list) {
			String openid = wechatUserBindDao.selUserByUserid(dto.getString("touser")).getOpenid();
			dto.put("touser", openid);
			result.add(JSONObject.parseObject(WXCenterJointUtil.sendTempMsg(getToken(wechatId,domain2,openid), dto)));
		}
		return result;
	}
	
	
	/**
	 * 发送微信客服消息
	 * @param wechatId  公众号原始ID
	 * @param domain2  公众号二级域名
	 * @return
	 */
	@RequestMapping("/wxcenter/sendCustMsg")
	public JSONObject sendCustMsg(@RequestBody JSONObject object,@RequestParam(required=false) String wechatId,@RequestParam(required=false) String domain2)
	{	
		String openid = wechatUserBindDao.selUserByUserid(object.getString("touser")).getOpenid();
		object.put("touser", openid);
		return JSONObject.parseObject( WXCenterJointUtil.sendCustMsg(getToken(wechatId,domain2,openid), object));
	}
	
	/**
	 * 发送微信客服消息   （批量）
	 * @param wechatId  公众号原始ID
	 * @param domain2  公众号二级域名
	 * @return
	 */
	@RequestMapping("/wxcenter/sendCustMsgList")
	public JSONArray sendCustMsgList(@RequestBody JSONArray array,@RequestParam(required=false) String wechatId,@RequestParam(required=false) String domain2)
	{	
		JSONArray result = new JSONArray();
		for(int i = 0;i<array.size();i++) {
			JSONObject object = array.getJSONObject(i);
			String openid = wechatUserBindDao.selUserByUserid(object.getString("touser")).getOpenid();
			object.put("touser", openid);
			result.add(JSONObject.parseObject( WXCenterJointUtil.sendCustMsg(getToken(wechatId,domain2,openid), object)));
		}
		return result;
	}
	
	/**
	 * 微信公众号页面授权登录接口
	 * @param code 微信页面授权CODE
	 * @param domain2  公众号二级域名
	 * @return
	 */
	@RequestMapping("/wxcenter/code/htmlLogin")
	public JSONObject htmlLogin(@RequestParam String code,@RequestParam String domain2)
	{	
		if(StringUtils.isEmpty(domain2)) {
			throw new RuntimeException("缺少参数");
		}
		WechatDTO wechatDTOByDomain2 = IWechatCache.getWechatDTOByDomain2(domain2);
		if(wechatDTOByDomain2 == null) {
			throw new RuntimeException("缺少参数");
		}
		String userLogin = WXCenterJointUtil.toUserLogin(code, wechatDTOByDomain2.getAppId(), wechatDTOByDomain2.getAppSecret());
		JSONObject parseObject = JSON.parseObject(userLogin);
		if(StringUtils.isEmpty(parseObject.getString("access_token"))) {
			return parseObject;
		}
		return JSONObject.parseObject(WXCenterJointUtil.getSNSUserInfo(parseObject.getString("access_token"), parseObject.getString("openid")));
	}
	
	@RequestMapping("/wxcenter/userinfo/get1")
	public JSONObject getUserInfo1(@RequestParam String userid,@RequestParam(required=false) String wechatId,@RequestParam(required=false) String domain2)
	{	
		String openid = wechatUserBindDao.selUserByUserid(userid).getOpenid();
		return JSONObject.parseObject(WXCenterJointUtil.getCgiBinUserInfo(getToken(wechatId,domain2,openid), openid));
	}
	
	/**
	 * 微信公众号关注用户信息获取接口
	 * @param wechatId  公众号原始ID
	 * @param domain2  公众号二级域名
	 * @return
	 */
	@RequestMapping("/wxcenter/userinfo/get")
	public JSONObject getUserInfo(@RequestParam String userid,@RequestParam(required=false) String wechatId,@RequestParam(required=false) String domain2)
	{	
		String userinfo = RedisClient.get(WXConstants.WXRedis.REDIS_CACHE_USER_INFO+userid);
		JSONObject obj = null;
		if(!StringUtils.isEmpty(userinfo)) {
			obj = JSON.parseObject(userinfo);
			RedisClient.expire(WXConstants.WXRedis.REDIS_CACHE_USER_INFO+userid, 1800l);
		}else {
			String openid = wechatUserBindDao.selUserByUserid(userid).getOpenid();
			obj = JSONObject.parseObject(WXCenterJointUtil.getCgiBinUserInfo(getToken(wechatId,domain2,openid), openid));
			if(obj == null ||StringUtils.isEmpty(obj.getString("openid"))) {
				return obj;
			}
			RedisClient.setex(WXConstants.WXRedis.REDIS_CACHE_USER_INFO+userid, JSON.toJSONString(obj), 60*30l);
		}
		return obj;
	}
	
	/**
	 * 微信公众号列表获取
	 * @return
	 */
	@RequestMapping("/wxcenter/wechat/list/get")
	public Result getAllWechatsList()
	{	
		return Result.success(wechatBaseDataDao.getAllWechatsList());
	}
	
	/**
	 * 微信公众号关注用户信息获取接口
	 * @return
	 */
	@RequestMapping("/wxcenter/check")
	public String serviceCheck()
	{	
		Map<String, String> map = System.getenv(); 
        String computerName = map.get("COMPUTERNAME");// 获取计算机名 
        InetAddress addr=null; 
        try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        String ip = addr.getHostAddress();
		return computerName+":"+ip;
	}
	
	
	/**
	 * 获取token,适应公众号原始ID，适应二级域名配置
	 * @param wechatId 公众号原始ID
	 * @param domain2  公众号二级域名配置
	 * @return
	 */
	private String getToken(String wechatId,String domain2,String openId) {
		if(isAllEmpty(wechatId,domain2,openId)) {
			throw new RuntimeException("缺少参数");
		}
		return getWechatDTO(wechatId,domain2,openId).getToken();
	}
	
	private WechatDTO getWechatDTO(String wechatId,String domain2,String openId) {
		if(!StringUtils.isEmpty(wechatId)) {
			return IWechatCache.getWechatDTOByWechatId(wechatId);
		}else if(!StringUtils.isEmpty(domain2)) {
			return IWechatCache.getWechatDTOByDomain2(domain2);
		}else if(!StringUtils.isEmpty(openId)){
			return IWechatCache.getWechatDTOByOpenId(openId);
		}else {
			throw new RuntimeException("param err");
		}
	}
	
	/**
	 * 检查参数是否全部为空
	 * @param strings
	 * @return
	 */
	private boolean isAllEmpty(String...strings) {
		boolean check = true;
		for(String s:strings) {
			if(!StringUtils.isEmpty(s)) {
				check = false;
			}
		}
		return check;
	}
	
}
