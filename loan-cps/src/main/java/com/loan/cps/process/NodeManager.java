package com.loan.cps.process;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.HttpUtil;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.entity.Session;
/**
 * @author kongzhimin
 *
 */
@Component
public class NodeManager implements InitializingBean{
	
	public static final Integer WELCOME = 0;
	
	public static final Integer FUND = 1;
	
	public static final Integer FIXED_ASSETS = 2;
	
	public static final Integer COMPANY = 3;
	
	public static final Integer INSURANCE = 4;
	
	public static final Integer ZHIMA = 5;
	
	public static final Integer MOBILE = 6;
	
	public static final Integer MOBILE_LOCATION = 7;
	
	public static final Integer LOCATION = 8;
	
	public static final Integer NAME = 9;
	
	public static final Integer WAGES = 10;
	
	public static final Integer WORK = 11;
	
	public static final Integer HOUSE = 12;
	
	public static final Integer HOUSE_STATE = 13;
	
	public static final Integer CAR = 14;
	
	public static final Integer CARD = 15;
	
	public static final Integer CAR2 = 16;
	
	public static final Integer DF_WELCOME = 17;
	
	public static final Integer AGREEMENT = 18;
	
	public static final Integer SURNAME = 19;
	
	public static final Integer FINISH5 = 999;
	
	public static final Integer FINISH3 = 998;
	
	public static final Integer FINISH4 = 997;
	
	public static final Integer FINISH2 = 996;
	
	public static final Integer FINISH = 995;
	
	public static final Integer ONE_HOUS_URGE = 1000;
	
	public static final Integer SECOND_HOUS_URGE = 1001;
	
	public static final Integer FIVE_MIN_URGE = 1002;
	
	public static final Integer REPLACE = 1003;
	
	
	/**
	 * 枚举Map集 保存对应消息类型处理器
	 */
	private static  Map<Integer,Node> em = new HashMap<Integer,Node>();
	
	/**
	 * 获取对应消息类型处理器
	 * @param msgType 消息类型
	 * @return
	 */
	public static  Node getNode(Integer nodeid) {
		return em.get(nodeid);
	}
	
	public static  Map<Integer, Node> getEm() {
		return em;
	}
	
	public static void setEm(Map<Integer, Node> e) {
		em = e;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		em.put(WELCOME, new WelcomeNode());
		em.put(FUND, new FundNode());
		em.put(FIXED_ASSETS, new FixedAssetsNode());
		em.put(COMPANY, new CompanyNode());
		em.put(INSURANCE, new InsuranceNode());
		em.put(ZHIMA, new ZhimaNode());
		em.put(MOBILE, new MobileNode());
		em.put(MOBILE_LOCATION, new MobileLocationNode());
		em.put(LOCATION, new LocationNode());
		em.put(FINISH5, new Finish5Node());
		em.put(ONE_HOUS_URGE, new UrgeMsgNode());
		em.put(SECOND_HOUS_URGE, new UrgeMsgNode2());
		em.put(FIVE_MIN_URGE, new UrgeMsgNode3());
		em.put(FINISH3, new Finish3Node());
		em.put(NAME, new NameNode());
		em.put(WAGES, new WagesNode());
		em.put(CARD, new CardNode());
		em.put(CAR2, new CarNode2());
		em.put(HOUSE, new HouseNode());
		em.put(HOUSE_STATE, new HouseStateNode());
		em.put(CAR, new CarNode());
		em.put(WORK, new WorkNode());
		em.put(DF_WELCOME, new DFWelcomeNode());
		em.put(FINISH4, new Finish4Node());
		em.put(FINISH2, new Finish2Node());
		em.put(FINISH, new FinishNode());
		em.put(AGREEMENT, new AgreementNode());
		em.put(REPLACE, new ReplaceQueMsgNode());
		em.put(SURNAME, new SurnameNode());
	}
	
	public static NodeResult paseMsgMenu(String menuid,NodeResult result,Session session) {
		if(System.currentTimeMillis()-session.getDown()>5*60*1000) {
			return result;
		}
		Collection<Node> values = em.values();
		for(Node node:values) {
			JSONObject msgMenu = node.getMsgMenu(menuid);
			if(msgMenu!=null) {
				JSONObject clone = (JSONObject) msgMenu.clone();
				clone.put("city", session.getCity());
				clone.put("userId", session.getUserId());
				result = node.parseQualification(clone, result);
				WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(NodeManager.getNode(NodeManager.REPLACE).getConfig().getQuestionMsg(), session.getUserId()), "", session.getDomain2());
				if(clone.getInteger("choose")==1&&session.getFinish()==null&&!(NodeManager.MOBILE.equals(session.getNodeId())
						||NodeManager.NAME.equals(session.getNodeId())||NodeManager.MOBILE_LOCATION.equals(session.getNodeId())||NodeManager.LOCATION.equals(session.getNodeId())||NodeManager.WORK.equals(session.getNodeId())||NodeManager.SURNAME.equals(session.getNodeId()))) {
					NodeManager.getNode(node.getConfig().getRightNodeId()).sendQuestion(session);
				}else if(session.getNodeId()<900) {
					NodeManager.getNode(session.getNodeId()).sendQuestion(session);
				}
			} 
		}
		return result;
	}
	
}
