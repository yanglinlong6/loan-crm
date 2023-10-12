package com.loan.wechat.entrances.service;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.docking.cache.IWechatCache;
import com.loan.wechat.docking.entity.WechatDTO;
import com.loan.wechat.entrances.constant.WechatConstant;
import com.loan.wechat.entrances.processor.EventTypeEnum;
import com.loan.wechat.entrances.processor.MsgTypeEnum;
import com.loan.wechat.entrances.util.HttpEXProxy;
import com.loan.wechat.login.entity.UserDTO;


/**
 * 
 * @author kongzhimin
 *
 */
@Component
public class IEntrancesFacade implements EntrancesFacade{
	
	private static Log logger = LogFactory.getLog(IEntrancesFacade.class);
	
	@Autowired
	private EntrancesService entrancesService;
	
	/**
	 * 接收处理微信推送消息
	 */
	@Override
	@Async
	public String xmlMsgReceive(Map<String, String> wechatMsg) {
		logger.info("微信事件推送消息:" + JSON.toJSONString(wechatMsg));
		UserDTO selUserByOpenid = entrancesService.selUserByOpenid(wechatMsg.get("FromUserName"));
		if(selUserByOpenid!=null &&!StringUtils.isEmpty(selUserByOpenid.getUserId())) {
			if (EventTypeEnum.SUBSCRIBE.getEventType().equals(wechatMsg.get("Event"))) {
				selUserByOpenid.setState(WechatConstant.SUB_STATE);
				selUserByOpenid.setSubCount(selUserByOpenid.getSubCount()+1);
				selUserByOpenid.setJobState(0);
				selUserByOpenid.setFollowTime(new Date());
				selUserByOpenid.setUpdateDate(new Date());
				selUserByOpenid.setUpdateBy(WechatConstant.CREATE_NAME);
				entrancesService.updateBindUser(selUserByOpenid);
			}
		}else {
			selUserByOpenid = getUserDTO(wechatMsg);
			entrancesService.bindUser(selUserByOpenid);
		}
		wechatMsg.put("subCount", selUserByOpenid.getSubCount().toString());
		wechatMsg.put("FromUserName", selUserByOpenid.getUserId());
		entrancesService.addWechatLog(wechatMsg);
		MsgTypeEnum.getProcessor(wechatMsg.get("MsgType")).process(wechatMsg, entrancesService);
		return "";
	}
	
	private UserDTO getUserDTO(Map<String, String> processRequestXml) {
		UserDTO dto = new UserDTO();
		dto.setOpenid(processRequestXml.get("FromUserName"));
		dto.setState(WechatConstant.SUB_STATE);
		dto.setType(WechatConstant.WECHAT_USER_TYPE);
		dto.setUnionId("");
		dto.setSubCount(1);
		dto.setUserId(createUUID());
		WechatDTO wechatDTOByWechatId = IWechatCache.getWechatDTOByWechatId(processRequestXml.get("ToUserName"));
		dto.setWxType(wechatDTOByWechatId.getWxType());
		dto.setFollowTime(new Date());
		dto.setCreateBy(WechatConstant.CREATE_NAME);
		dto.setCreateDate(new Date());
		dto.setMedia(getMediaId(processRequestXml));
		if(1==wechatDTOByWechatId.getAppType()) {
			dto.setJobState(100);
		}else {
			dto.setJobState(100);
		}
		return dto;
	}
	
	private String createUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	
	private Integer getMediaId(Map<String, String> processRequestXml) {
		String ticket = processRequestXml.get("Ticket");
		int media = 0;
		// 同步关注用户到微信绑定表
		if(!StringUtils.isEmpty(ticket))
		{
			String eventKey = processRequestXml.get("EventKey");
			if(!StringUtils.isEmpty(eventKey) && eventKey.contains("_"))
			{
				String[] array = eventKey.split("_");
				if(array.length == 2)
				{
					if(array[1].startsWith("wxcf")) {
						String[] split = array[1].split("-");
						if(split.length==4) {
							WechatDTO wechatDTOByWechatId = IWechatCache.getWechatDTOByDomain2(processRequestXml.get("domain2"));
							//-------------微信数据上报开始
							JSONObject total = new JSONObject();
							JSONArray actions = new JSONArray();
							Integer setId = entrancesService.getSetId(processRequestXml.get("ToUserName"));
							String url = new String(Base64.getDecoder().decode(split[3]));
							Long time =System.currentTimeMillis()/1000;
							JSONObject action = new JSONObject();
							action.put("user_action_set_id", setId);
							action.put("url", url);
							action.put("action_time",time );
							action.put("action_type", "RESERVATION");
							JSONObject trace = new JSONObject();
							trace.put("click_id", split[2]);
							action.put("trace", trace);
							actions.add(action);
							
							JSONObject action2 = new JSONObject();
							action2.put("user_action_set_id", setId);
							action2.put("url", url);
							action2.put("action_time", time);
							action2.put("action_type", "CONFIRM_EFFECTIVE_LEADS");
							JSONObject trace2 = new JSONObject();
							trace2.put("click_id", split[2]);
							action2.put("trace", trace2);
							JSONObject actionParams = new JSONObject();
							actionParams.put("leads_type","RESERVE");
							action2.put("action_param", actionParams);
							actions.add(action2);
							
							total.put("actions", actions);
							String doexcute = new HttpEXProxy() {
								@Override
								public String excute() {
									return HttpUtil.postForObject(String.format("https://api.weixin.qq.com/marketing/user_actions/add?version=v1.0&access_token=%s", wechatDTOByWechatId.getToken()), total);
								}
							}.doexcute();
							logger.info("微信返回："+doexcute);
							//--------微信数据上报结束
							array[1] = split[1];
						}
					}
					if(array[1].startsWith("tmtf")) {
						String[] split = array[1].split("-");
						if(split.length==5) {
							Integer setId = entrancesService.getSetId(split[3]);
							String url = new String(Base64.getDecoder().decode(split[4]));
							String string = UUID.randomUUID().toString().replace("-", "");
							Long time= System.currentTimeMillis()/1000;
							JSONObject total = new JSONObject();
							total.put("account_id", Integer.valueOf(split[3]));
							
							JSONArray actions = new JSONArray();
							JSONObject action = new JSONObject();
							action.put("user_action_set_id",setId);
							action.put("url",url );
							action.put("action_time", time);
							action.put("action_type", "RESERVATION");
							JSONObject trace = new JSONObject();
							trace.put("click_id", split[2]);
							action.put("trace", trace);
							action.put("outer_action_id", string);
							actions.add(action);
							
							JSONObject action1 = new JSONObject();
							action1.put("user_action_set_id", setId);
							action1.put("url", url);
							action1.put("action_time", time);
							action1.put("action_type", "CONFIRM_EFFECTIVE_LEADS");
							JSONObject trace1 = new JSONObject();
							trace1.put("click_id", split[2]);
							action1.put("trace", trace1);
							action1.put("outer_action_id", string);
							JSONObject actionParams = new JSONObject();
							actionParams.put("leads_type","RESERVE");
							action1.put("action_param", actionParams);
							actions.add(action1);
							
							total.put("actions", actions);
							String doexcute = new HttpEXProxy() {
								@Override
								public String excute() {
									return HttpUtil.postForObject("http://"+processRequestXml.get("domain")+"/", JSON.toJSONString(processRequestXml));
								}
							}.doexcute();
							logger.info("腾讯广告返回："+doexcute);
							array[1] = split[1];
						}
					}
					if(isNumeric(array[1]))
					{
						media = Integer.parseInt(array[1]);
					}
				}
			}
		}
		return media;
	}
	
	//只做是否为纯数字
    public static boolean isNumeric(String str) {
        if (StringUtils.isEmpty(str)){
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
