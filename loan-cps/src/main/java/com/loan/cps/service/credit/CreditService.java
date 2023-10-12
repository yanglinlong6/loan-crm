package com.loan.cps.service.credit;

import com.loan.cps.common.R;

import java.util.List;

/**
 * 信用逾期service
 */
public interface CreditService {

    R addCreditUser(CreditUserPO creditUserPO);

    R addCreditUser2(CreditUserPO po);

    R updateCreditUser(CreditUserPO creditUserPO);

    /**
     * 发送信用逾期客户
     * @param creditUserPO
     * @return boolean  true-成功,false-失败
     */
    boolean sendCreditUser(CreditUserPO creditUserPO);

    /**
     * 获取信用预取客户列表
     * @param count 数量
     * @param status 状态: 0-初始,1-成功,2-失败
     * @return List<CreditUserPO>
     */
    List<CreditUserPO> getCreditUserList(Integer count,Byte status);
}
