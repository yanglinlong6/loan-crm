package com.loan.wechat.entrances.processor;

import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.loan.wechat.common.HttpUtil;
import com.loan.wechat.entrances.constant.WechatConstant;
import com.loan.wechat.entrances.service.EntrancesService;
import com.loan.wechat.entrances.util.HttpEXProxy;
import com.loan.wechat.login.entity.UserDTO;

public class UnsubProcessor implements Processor{

	@Override
	public void process(Map<String, String> processRequestXml, EntrancesService entrancesService) {
		UserDTO selUserByOpenid = entrancesService.selUserByUserid(processRequestXml.get("FromUserName"));
		if(selUserByOpenid==null) {
			throw new RuntimeException("err");
		}
		selUserByOpenid.setState(WechatConstant.UNSUB_STATE);
		selUserByOpenid.setUnfollowTime(new Date());
		selUserByOpenid.setCreateBy(WechatConstant.CREATE_NAME);
		selUserByOpenid.setUpdateDate(new Date());
		entrancesService.updateBindUser(selUserByOpenid);
		new HttpEXProxy() {
			@Override
			public String excute() {
				return HttpUtil.postForObject("http://"+processRequestXml.get("domain2")+"/dialogue/node/end", JSON.toJSONString(processRequestXml));
			}
		}.doexcute();
	}

}
