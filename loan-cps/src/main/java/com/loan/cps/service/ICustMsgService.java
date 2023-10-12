package com.loan.cps.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.WXConstants;
import com.loan.cps.dao.CustMsgDao;

@Component
public class ICustMsgService implements CustMsgService{
	
	@Autowired
	private CustMsgDao custMsgDao;
	
	@Override
	public List<JSONObject> getNodeMsg(Integer nodeId) {
		List<JSONObject> nodeMsgList = custMsgDao.getNodeMsgList(nodeId);
		for(JSONObject custMsg:nodeMsgList) {
			if(WXConstants.MsgConstants.WX_CUST_MSG_TYPE_MENU.equals(custMsg.getString("msg_type"))) {
				List<JSONObject> custMsgMenuList = custMsgDao.getCustMsgMenuList(custMsg.getString("msg_id"));
				custMsg.put("list", JSON.parseArray(JSON.toJSONString(custMsgMenuList)));
			}
		}
		return nodeMsgList;
	}

}
