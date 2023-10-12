package com.help.loan.distribute.service.api.process;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kongzhimin
 */
@Component
public class NodeManager implements InitializingBean {

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

    public static final Integer FINISH5 = 999;

    public static final Integer FINISH3 = 998;

    public static final Integer ONE_HOUS_URGE = 1000;

    public static final Integer SECOND_HOUS_URGE = 1001;

    public static final Integer FIVE_MIN_URGE = 1002;


    /**
     * 枚举Map集 保存对应消息类型处理器
     */
    private Map<Integer, Node> em = new HashMap<Integer, Node>();

    /**
     * 获取对应消息类型处理器
     *
     * @param msgType 消息类型
     * @return
     */
    public Node getNode(Integer nodeid) {
        return em.get(nodeid);
    }

    public Map<Integer, Node> getEm() {
        return em;
    }

    public void setEm(Map<Integer, Node> em) {
        this.em = em;
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
//		em.put(WORK, new WorkNode());
//		em.put(HOUSE, new HouseNode());
//		em.put(HOUSE_STATE, new HouseStateNode());
//		em.put(CAR, new CarNode());
    }

}
