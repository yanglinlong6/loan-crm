package com.loan.wechat.docking.controller;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.loan.wechat.common.DateUtil;
import com.loan.wechat.common.JudgeUtil;
import com.loan.wechat.user.UserAptitudePO;
import com.loan.wechat.user.UserAptitudeService;
import com.sun.org.apache.bcel.internal.generic.DADD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.common.Result;
import com.loan.wechat.docking.cache.IWechatCache;
import com.loan.wechat.docking.cache.MarketingCache;
import com.loan.wechat.docking.entity.WechatDTO;
import com.loan.wechat.docking.dao.MarketDao;
import com.loan.wechat.entrances.service.EntrancesService;
import com.loan.wechat.entrances.util.HttpEXProxy;
/**
 * 	腾讯广告授权TOKEN控制器
 * @author kongzhimin
 *
 */
@RestController
public class TencentMarketingContr {
	
	@Autowired
	private EntrancesService entrancesService;
	
	@Autowired
	private MarketDao marketDao;

	@Autowired
	private UserAptitudeService userAptitudeService;
	
	private static Logger logger = LoggerFactory.getLogger(TencentMarketingContr.class);
	
	/**
	 * 推广商授权
	 * @param authorizationCode 授权CODE
	 * @return
	 */
	@RequestMapping("/tencent/marketing/token/authorization")
	public Result authorizationAccessToken(@RequestParam String authorizationCode)
	{	
		return Result.success(MarketingCache.authorizationAccessToken(authorizationCode));
	}
	/**
	 * 获取授权TOKEN
	 * @return
	 */
	@RequestMapping("/tencent/marketing/token/get")
	public String getAccessToken(@RequestParam String accountid,HttpServletResponse response)
	{	
		return MarketingCache.getMarketingToken(accountid);
	}
	
	/**
	 * 	刷新授权TOKEN
	 * @return
	 */
	@RequestMapping("/tencent/marketing/token/refresh")
	public Result refreshAccessToken()
	{	
		MarketingCache.refreshAccessToken();
		return Result.success();
	}
	
	@RequestMapping("/tencent/marketing/add")
	public Result addMarket(@RequestParam(required = false) String accountId,@RequestParam String urlBase64,@RequestParam String clickId,HttpServletRequest req) {
		String url = new String(Base64Utils.decodeFromString(urlBase64));
		logger.info(String.format("ocpm 反馈 accountId = %s ,urlBase64 = %s ,clickId = %s", accountId,urlBase64,clickId));
		if(StringUtils.isEmpty(accountId)) {
			accountId = "15111987";
		}
		JSONObject set = entrancesService.getSet(accountId);
		if(set==null) {
			return Result.success();
		}
		JSONObject total = new JSONObject();
		JSONArray actions = new JSONArray();
		Integer setId = set.getInteger("setId");
		String token = set.getString("domain2");
		total.put("account_id", accountId);
		total.put("user_action_set_id", setId);
		String nonce = UUID.randomUUID().toString().replace("-", "");
		Long time =System.currentTimeMillis()/1000;
		JSONObject action = new JSONObject();
		action.put("url", url);
		action.put("action_time",time );
		action.put("action_type", "RESERVATION");
		JSONObject trace = new JSONObject();
		trace.put("click_id", clickId);
		action.put("trace", trace);
		actions.add(action);
		
		total.put("actions", actions);
		String doexcute = new HttpEXProxy() {
			@Override
			public String excute() {
				return HttpUtil.postForObject(String.format("https://api.e.qq.com/v1.1/user_actions/add?access_token=%s&timestamp=%s&nonce=%s", token,time,nonce), JSON.toJSONString(total));
			}
		}.doexcute();
		logger.info("腾讯广告返回："+doexcute);
		return Result.success();
	}
	
	@RequestMapping("/wechat/marketing/add")
	public Result addWXMarket(@RequestParam String wechatId,@RequestParam String urlBase64,@RequestParam String clickId,HttpServletRequest req) {
		String url = new String(Base64Utils.decodeFromString(urlBase64));
		WechatDTO wechatDTOByWechatId = new WechatDTO();
		try {
			wechatDTOByWechatId = IWechatCache.getWechatDTOByDomain2(req.getServerName());
		}catch (Exception e) {
			// TODO: handle exception
			wechatDTOByWechatId = IWechatCache.getWechatDTOByWechatId(wechatId);
		}
		JSONObject total = new JSONObject();
		JSONArray actions = new JSONArray();
		Integer setId = entrancesService.getSetId(wechatDTOByWechatId.getWechatId());
		logger.info(String.format("ocpm 反馈 wechatId = %s ,urlBase64 = %s ,clickId = %s ,setid = %s", wechatId,urlBase64,clickId,setId));
		Long time =System.currentTimeMillis()/1000;
		JSONObject action = new JSONObject();
		action.put("user_action_set_id", setId);
		action.put("url", url);
		action.put("action_time",time );
		action.put("action_type", "RESERVATION");
		JSONObject trace = new JSONObject();
		trace.put("click_id", clickId);
		action.put("trace", trace);
		actions.add(action);
		
		JSONObject action2 = new JSONObject();
		action2.put("user_action_set_id", setId);
		action2.put("url", url);
		action2.put("action_time", time);
		action2.put("action_type", "CONFIRM_EFFECTIVE_LEADS");
		JSONObject trace2 = new JSONObject();
		trace2.put("click_id", clickId);
		action2.put("trace", trace2);
		JSONObject actionParams = new JSONObject();
		actionParams.put("leads_type","RESERVE");
		action2.put("action_param", actionParams);
		actions.add(action2);
		
		total.put("actions", actions);
		String token = wechatDTOByWechatId.getToken();
		String doexcute = new HttpEXProxy() {
			@Override
			public String excute() {
				return HttpUtil.postForObject(String.format("https://api.weixin.qq.com/marketing/user_actions/add?version=v1.0&access_token=%s", token), JSON.toJSONString(total));
			}
		}.doexcute();
		logger.info("微信返回："+doexcute);
		return Result.success();
	}
	
	@RequestMapping("/wechat/marketing/add2")
	public Result addWXMarket2(@RequestParam String urlBase64,@RequestParam String clickId,HttpServletRequest req) {
		String url = new String(Base64Utils.decodeFromString(urlBase64));
		JSONObject total = new JSONObject();
		JSONArray actions = new JSONArray();
		Integer setId = 1110049802;
		logger.info(String.format("外部 ocpm 反馈 urlBase64 = %s ,clickId = %s ,setid = %s", urlBase64,clickId,setId));
		Long time =System.currentTimeMillis()/1000;
		JSONObject action = new JSONObject();
		action.put("user_action_set_id", setId);
		action.put("url", url);
		action.put("action_time",time );
		action.put("action_type", "RESERVATION");
		JSONObject trace = new JSONObject();
		trace.put("click_id", clickId);
		action.put("trace", trace);
		actions.add(action);
		
		JSONObject action2 = new JSONObject();
		action2.put("user_action_set_id", setId);
		action2.put("url", url);
		action2.put("action_time", time);
		action2.put("action_type", "CONFIRM_EFFECTIVE_LEADS");
		JSONObject trace2 = new JSONObject();
		trace2.put("click_id", clickId);
		action2.put("trace", trace2);
		JSONObject actionParams = new JSONObject();
		actionParams.put("leads_type","RESERVE");
		action2.put("action_param", actionParams);
		actions.add(action2);
		
		total.put("actions", actions);
		String doexcute = new HttpEXProxy() {
			@Override
			public String excute() {
				return HttpUtil.postForObject(String.format("https://api.weixin.qq.com/marketing/user_actions/add?version=v1.0&access_token=%s",HttpUtil.getForObject("http://nmkj.xyz:8080/api/yuToken")), JSON.toJSONString(total));
			}
		}.doexcute();
		logger.info("微信返回："+doexcute);
		return Result.success();
	}

	/**
	 *
	 * @param accountId 账户id
	 * @param channel 渠道
	 * @param urlBase64 整个链接地址Base64编码后的字符串
	 * @param clickId 点击id  腾讯广告:用来区分是哪个计划中的广告
	 * @param req HttpServletRequest请求对象
	 * @return 返回记录上报是否成功
	 */
	@RequestMapping("/marketing/add")
	public Result market(@RequestParam(required = false) String accountId,@RequestParam(required = false) String channel,@RequestParam String urlBase64,@RequestParam(required = false) String clickId,HttpServletRequest req) throws UnsupportedEncodingException {
		String url = new String(Base64Utils.decodeFromString(urlBase64));
		String account = StringUtils.isEmpty(accountId)?getParam(url,"accountId"):accountId;
		logger.info("[收到埋点]accountId-{},channel-{},clickId-{},url-{}",accountId,channel,clickId,url);
		JSONObject obj = new JSONObject();
		obj.put("channel", channel);
		obj.put("url", url);
		obj.put("click", StringUtils.isEmpty(clickId)?"":clickId);
		obj.put("status", 0);
		obj.put("account", account);

		if(StringUtils.isEmpty(channel)){
			channel = getParam(url,"channel");
		}
		//表示头条渠道
		if(JudgeUtil.contains(channel,"ttt")){
			String click  = getParam(url,"clickid");
			if(!StringUtils.isEmpty(click)){
				click = click.replaceAll("#/","").replaceAll("#/?","");
			}
			obj.put("type", 2); // 2-表示头条
			obj.put("click",click);
			Long add = marketDao.add(obj);
			logger.info("[保存埋点]头条: {}",obj.toJSONString());
			return Result.success(obj.getString("id"));
		}


		// channel包含baidu  表示百度媒体
		if(JudgeUtil.contains(channel,"baidu")){
			account = URLDecoder.decode(account,"UTF-8");
			if(account.startsWith("%")){
				account = URLDecoder.decode(account,"UTF-8");
			}
			logger.info("百度:{}",account);
			obj.put("account", account);
			obj.put("type", 3); // 3-表示百度
			Long add = marketDao.add(obj);
			logger.info("[保存埋点]百度: {}",obj.toJSONString());
			return Result.success(obj.getString("id"));
		}

		// 支付
		if(StringUtils.isEmpty(clickId)) {
			if("111".equals(accountId)) {  //微信支付后上报么?
				obj.put("type", 1);
				marketDao.add(obj);
			}
			return Result.success(0);
		}

		if(StringUtils.isEmpty(accountId)){
			accountId = getParam(url,"accountId");
			obj.put("account", account);
		}

		if(StringUtils.isEmpty(accountId)) {
			WechatDTO wechatDTOByWechatId = null;
			try {
				wechatDTOByWechatId = IWechatCache.getWechatDTOByDomain2(req.getServerName());
			}catch (Exception e) {
				return Result.success(0);
			}
			obj.put("type", 0);
			obj.put("account", wechatDTOByWechatId.getWechatId());
		}else {
			obj.put("type", 1);
			obj.put("account", accountId);
		}
		logger.info("[保存埋点]: {}",obj.toJSONString());
		Long add = marketDao.add(obj);
		return Result.success(obj.getString("id"));
	}

//	/**
//	 * zhongdai 数据上报
//	 * @param accountId  公众号原始id
//	 * @param channel 渠道
//	 * @param urlBase64 请求链接编码
//	 * @param clickId 点击id
//	 * @param req
//	 * @return
//	 */
//	@RequestMapping("/marketing/reporting2")
//	public Result marketing(@RequestParam(required = false) String accountId,@RequestParam(required = false) String channel,@RequestParam String urlBase64,@RequestParam(required = false) String clickId,HttpServletRequest req) {
//		logger.info("zhongdai: accountId-{},channel-{},clientId-{},url-{}",accountId,channel,clickId,urlBase64);
//		String doexcute = new HttpEXProxy() {
//			@Override
//			public String excute() {
//				return HttpUtil.getForObject(String.format("https://h5.bcloan.cn/server/apis/center/wechat/marketing/add?urlBase64=%s&clickId=%s&wechatId=%s", urlBase64,clickId,accountId));
//			}
//		}.doexcute();
//		logger.info("微信返回:-->"+"："+doexcute);
//		return Result.success(doexcute);
//	}
	
	@RequestMapping("/marketing/reporting")
	public Result reporting(@RequestParam String mId) throws UnsupportedEncodingException {
		logger.info("上报ID ："+mId);
		if("0".equals(mId)) {
			return Result.success();
		}
		List<JSONObject> byId = marketDao.getById(mId);
		if(byId.isEmpty()) {
			return Result.success(Result.SUCCESS_CODE,"上报id空的",null);
		}
		JSONObject jsonObject = byId.get(0);
		int status = jsonObject.getIntValue("status");
		if(status==1||status==2) {
			return Result.success(Result.SUCCESS_CODE,"已上报"+status,null);
		}
		// 限制异地比率上报
		String url = jsonObject.getString("url");
		String channel = getParam(url,"channel");
		if(StringUtils.isEmpty(channel)){
			logger.info("上报ID:{},渠道：{}【是空的】",mId,channel);
			return reporting(jsonObject);
		}
		String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
		String startDate = date + " 00:00:00";
		String endDate = date + " 23:59:59";
		int totalCount = userAptitudeService.getChannelCountByLevel(channel, startDate,endDate,null);//查询当天城市总数量
		int levelCount = userAptitudeService.getChannelCountByLevel(channel, startDate,endDate,99);//99异地
		double rate = 0.00d;
		if(totalCount != 0 && levelCount != 0)
			rate = BigDecimal.valueOf(levelCount).divide(BigDecimal.valueOf(totalCount),2,BigDecimal.ROUND_HALF_UP).doubleValue();
		UserAptitudePO userAptitude = userAptitudeService.getChannelUserAptitudePO(channel,startDate,endDate);
		logger.info("[数据上报异地判断]渠道:{},总数量:{},异地数量:{},异地比率：{},是否异地：{}]",channel,totalCount,levelCount,rate,userAptitude.getInCity());
		if(rate >= 0.25d && userAptitude.getLevel().intValue() == 99 &&  totalCount > 10){ // 超出异地占比，并且是异地客户
			return Result.success(Result.SUCCESS_CODE,"您已成功提交[异地]",null);
		}
		return reporting(jsonObject);
	}

	private Result reporting(JSONObject jsonObject){
		int intValue = jsonObject.getIntValue("type");
		// 0-表示微信
		if(intValue==0) {
			WechatDTO wechatDTOByWechatId = new WechatDTO();
			try {
				wechatDTOByWechatId = IWechatCache.getWechatDTOByWechatId(jsonObject.getString("account"));
			}catch (Exception e) {
				// TODO: handle exception
				return Result.success();
			}
			JSONObject total = new JSONObject();
			JSONArray actions = new JSONArray();
			Integer setId = entrancesService.getSetId(wechatDTOByWechatId.getWechatId());
			logger.info(String.format("ocpm 反馈 wechatId = %s ,url = %s ,clickId = %s ,setid = %s", jsonObject.getString("account"),jsonObject.getString("url"),jsonObject.getString("click"),setId));
			Long time =System.currentTimeMillis()/1000;
			JSONObject action = new JSONObject();
			action.put("user_action_set_id", setId);
			action.put("url", jsonObject.getString("url"));
			action.put("action_time",time );
			action.put("action_type", "RESERVATION");
			JSONObject trace = new JSONObject();
			trace.put("click_id", jsonObject.getString("click"));
			action.put("trace", trace);
			actions.add(action);

			total.put("actions", actions);
			String token = wechatDTOByWechatId.getToken();
			String doexcute = new HttpEXProxy() {
				@Override
				public String excute() {
					return HttpUtil.postForObject(String.format("https://api.weixin.qq.com/marketing/user_actions/add?version=v1.0&access_token=%s", token), JSON.toJSONString(total));
				}
			}.doexcute();
			logger.info("微信返回:"+jsonObject.getString("account")+"-->"+"："+doexcute);
		}else if(intValue==1) { // 1-表示广点通
			JSONObject set = entrancesService.getSet(jsonObject.getString("account"));
			if(set==null) {
				return Result.success();
			}
			JSONObject total = new JSONObject();
			JSONArray actions = new JSONArray();
			Integer setId = set.getInteger("setId");
			String token = set.getString("domain2");
			total.put("account_id", jsonObject.getString("account"));
			total.put("user_action_set_id", setId);
			String nonce = UUID.randomUUID().toString().replace("-", "");
			Long time =System.currentTimeMillis()/1000;
			JSONObject action = new JSONObject();
			action.put("url", jsonObject.getString("url"));
			action.put("action_time",time );
			action.put("action_type", "RESERVATION");
			JSONObject trace = new JSONObject();
			trace.put("click_id", jsonObject.getString("click"));
			action.put("trace", trace);
			actions.add(action);

			logger.info(String.format("ocpm 反馈 account = %s ,url = %s ,clickId = %s ,setid = %s", jsonObject.getString("account"),jsonObject.getString("url"),jsonObject.getString("click"),setId));

			total.put("actions", actions);
			String doexcute = new HttpEXProxy() {
				@Override
				public String excute() {
					return HttpUtil.postForObject(String.format("https://api.e.qq.com/v1.1/user_actions/add?access_token=%s&timestamp=%s&nonce=%s", token,time,nonce), JSON.toJSONString(total));
				}
			}.doexcute();
			logger.info("腾讯广告返回："+doexcute);
		}else if(intValue == 2){ //表示头条
			addTTMarketing2(jsonObject.getString("url"));
		}else if(intValue == 3){ // 百度媒体
			// 获取token
			String account = jsonObject.getString("account");
			JSONObject set = entrancesService.getSet(account);
			if(null == set){
				return Result.fail(Result.FAIL_CODE,"百度上报失败["+account+"]未配置");
			}
			String token = set.getString("domain2");
			JSONObject data = new JSONObject();
			data.put("token",token);

			JSONObject newType = new JSONObject();
			newType.put("logidUrl",jsonObject.getString("url"));
			newType.put("newType",3);

			JSONArray conversionTypes = new JSONArray();
			conversionTypes.add(newType);

			data.put("conversionTypes",conversionTypes);

			String doexcute = new HttpEXProxy() {
				@Override
				public String excute() {
					return HttpUtil.postForObject("https://ocpc.baidu.com/ocpcapi/api/uploadConvertData", data);
				}
			}.doexcute();
			logger.info("百度广告返回："+doexcute+"【"+jsonObject.getString("url")+"】");
		}
		jsonObject.put("status", 1);
		marketDao.update(jsonObject);
		return Result.success();
	}


	/**
	 * 自动上报定时任务
	 */
	@Scheduled(fixedRate = 60*1000)
	public void reporting() {
		List<JSONObject> byChannel = marketDao.getByChannel();
		if(byChannel!=null && !byChannel.isEmpty()) {
			Random r = new Random();
			for(JSONObject obj:byChannel) {
				int nextInt = r.nextInt(100);
				if(obj.getIntValue("ratio")>=nextInt) {
					logger.info("埋点自助上报："+obj.getString("id"));
					int intValue = obj.getIntValue("type");
					if(intValue==0) {
						WechatDTO wechatDTOByWechatId = new WechatDTO();
						try {
							 wechatDTOByWechatId = IWechatCache.getWechatDTOByWechatId(obj.getString("account"));
						}catch (Exception e) {
							continue;
						}
						JSONObject total = new JSONObject();
						JSONArray actions = new JSONArray();
						Integer setId = entrancesService.getSetId(wechatDTOByWechatId.getWechatId());
						logger.info(String.format("ocpm 反馈 wechatId = %s ,url = %s ,clickId = %s ,setid = %s", obj.getString("account"),obj.getString("url"),obj.getString("click"),setId));
						Long time =System.currentTimeMillis()/1000;
						JSONObject action = new JSONObject();
						action.put("user_action_set_id", setId);
						action.put("url", obj.getString("url"));
						action.put("action_time",time );
						action.put("action_type", "RESERVATION");
						JSONObject trace = new JSONObject();
						trace.put("click_id", obj.getString("click"));
						action.put("trace", trace);
						actions.add(action);
						total.put("actions", actions);
						String token = wechatDTOByWechatId.getToken();
						String doexcute = new HttpEXProxy() {
							@Override
							public String excute() {
								return HttpUtil.postForObject(String.format("https://api.weixin.qq.com/marketing/user_actions/add?version=v1.0&access_token=%s", token), JSON.toJSONString(total));
							}
						}.doexcute();
						logger.info("微信返回："+doexcute);
					}else {
						JSONObject set = entrancesService.getSet(obj.getString("account"));
						if(set!=null) {
							JSONObject total = new JSONObject();
							JSONArray actions = new JSONArray();
							Integer setId = set.getInteger("setId");
							String token = set.getString("domain2");
							total.put("account_id", obj.getString("account"));
							total.put("user_action_set_id", setId);
							String nonce = UUID.randomUUID().toString().replace("-", "");
							Long time =System.currentTimeMillis()/1000;
							JSONObject action = new JSONObject();
							action.put("url", obj.getString("url"));
							action.put("action_time",time );
							action.put("action_type", "RESERVATION");
							JSONObject trace = new JSONObject();
							trace.put("click_id", obj.getString("click"));
							action.put("trace", trace);
							actions.add(action);
							
							logger.info(String.format("ocpm 反馈 account = %s ,url = %s ,clickId = %s ,setid = %s", obj.getString("account"),obj.getString("url"),obj.getString("click"),setId));
							
							total.put("actions", actions);
							String doexcute = new HttpEXProxy() {
								@Override
								public String excute() {
									return HttpUtil.postForObject(String.format("https://api.e.qq.com/v1.1/user_actions/add?access_token=%s&timestamp=%s&nonce=%s", token,time,nonce), JSON.toJSONString(total));
								}
							}.doexcute();
							logger.info("腾讯广告返回："+doexcute);
						}
					}
					obj.put("status", 2);
					marketDao.update(obj);
				}else {
					obj.put("status", 3);
					marketDao.update(obj);
				}
			}
		}
	}


	/**
	 * 头条上报
	 * @param urlBase64
	 * @return
	 */
	@RequestMapping("/tt/marketing/add")
	public Result addTTMarketing(@RequestParam String urlBase64) {
		String url = new String(Base64Utils.decodeFromString(urlBase64));
		logger.info(String.format("外部 ocpm 反馈 urlBase64 = %s ", url));
		String encode = "";
		try {
			encode = URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.info("urlencode err");
			return Result.fail();
		}
		String format = String.format("https://ad.toutiao.com/track/activate/?link=%s&event_type=%s",encode,"3");
		String doexcute = new HttpEXProxy() {
			@Override
			public String excute() {
				return HttpUtil.getForObject(format);
			}
		}.doexcute();
		logger.info("头条返回："+doexcute);
		return Result.success();
	}

	private Result addTTMarketing2(@RequestParam String urlBase64){
		logger.info(String.format("头条 ocpm 反馈 urlBase64 = %s ", urlBase64));
		String clickid = getParam(urlBase64,"clickid");
		try {
			if(!StringUtils.isEmpty(clickid) && !clickid.contains("__CLICKID__")){
				if(clickid.endsWith("#/?")){
					clickid = clickid.replaceAll("#/?","");
				}
				if(clickid.endsWith("#/")){
					clickid = clickid.replaceAll("#/","");
				}
				logger.info("头条上报[callback]："+clickid);
				JSONObject ad = new JSONObject();
				ad.put("callback",clickid);

				JSONObject context = new JSONObject();
				context.put("ad",ad);

				JSONObject data = new JSONObject();
				data.put("event_type","active_register");
				data.put("context",context);
				data.put("timestamp",System.currentTimeMillis());

				String result = HttpUtil.postForObject("https://analytics.oceanengine.com/api/v2/conversion",data);
				logger.info("头条返回[new]："+result);
				return Result.success();
			}else{
				String  encode = URLEncoder.encode(urlBase64, "UTF-8");
				String format = String.format("https://ad.toutiao.com/track/activate/?link=%s&event_type=%s",encode,"1");
				String doexcute = new HttpEXProxy() {
					@Override
					public String excute() {
						return HttpUtil.getForObject(format);
					}
				}.doexcute();
				logger.info("头条返回[old]："+doexcute);
				return Result.success();
			}
		} catch (UnsupportedEncodingException e) {
			logger.info("urlencode err");
			return Result.fail();
		}
	}

	/**
	 * 从请求url地址中解析参数
	 * http://gdt.fxsk100.com/?wx_aid=4435743888&tid=4435744014&gdt_vid=wx0yodfzmbzi2lyw00&wx_traceid=wx0yodfzmbzi2lyw00#/?city=nantong&accountId=19440078&channel=0078-v21-nt-1114-3-1002
	 * @param url url请求地址
	 * @param paramName 参数名称
	 * @return String  参数值
	 */
	public static String getParam(String url, String paramName){
		try{
			if(StringUtils.isEmpty(url))
				return null;
			String[] array = url.split("\\?");
			if(null == array || array.length<=0){
				return null;
			}
			for(String str : array){
				if(!str.contains(paramName+"=")){
					continue;
				}
				String[] paramArray = str.split("&");
				if(null == paramArray || paramArray.length<=0){
					continue;
				}
				for(String param : paramArray){
					if(!param.contains(paramName)){
						continue;
					}
					String[] cityArray = param.split("=");
					return cityArray[1];
				}
			}
			return null;
		}catch (Exception e){
			logger.error("解析请求链接参数异常：{}-{}",e.getMessage(),e);
			return null;
		}

	}


	public static void main(String[] args) throws UnsupportedEncodingException {
//		String str = "%E5%BE%B7%E5%8A%A1%E5%8F%91%E9%A1%BE";
//		System.out.println(URLDecoder.decode(str,"UTF-8"));
		String url = "http://credit.bangzheng100.com/?adid=1717226415200260&clickid=EIO46PPuuYYDGMfHsOTl9ccEIOfb4OPl9e8DMAw4AUIiMjAyMTExMjMyMTM5NTAwMTAxNTExNDUxNzEwRjFFQjcwOEgBkAEA&creativeid=1717226415201283&creativetype=5#/?accountId=1717201043031054&channel=ttt-credit-001";
		String clickid = getParam(url,"clickid");

		String sendUrl = "https://analytics.oceanengine.com/api/v2/conversion";
		JSONObject data = new JSONObject();
		data.put("event_type","form");

		JSONObject ad = new JSONObject();
		ad.put("callback",clickid);

		JSONObject context = new JSONObject();
		context.put("ad",ad);

		data.put("context",context);
		data.put("timestamp",System.currentTimeMillis());

		String result = HttpUtil.postForObject(sendUrl,data);
		System.out.println(result);


	}

	
}
