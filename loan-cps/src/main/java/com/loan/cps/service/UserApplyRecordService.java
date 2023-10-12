package com.loan.cps.service;


import java.util.UUID;

import com.loan.cps.entity.UserApplyRecordPO;

/**
 * 用户申请记录service接口
 */
public interface UserApplyRecordService {


    /**
     * 随机生成applyId(用户申请记录id)
     *
     * @return String 用户申请记录id
     * @auth zhangqiuping 2019-09-30
     */
    default String generateApplyId() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * 新增用户盛情记录
     * @param userApplyRecord UserApplyRecordPO
     * @auth zhangqiuping 2019-09-30
     */
    boolean addApplyRecord(UserApplyRecordPO userApplyRecord);


    /**
     * 根据申请记录id获取单条记录数据
     * @param applyId 申请记录id
     * @return UserApplyRecordPO
     * @auth zhangqiuping 2019-09-30
     */
    UserApplyRecordPO getByApplyId(String applyId);


    /**
     * 获取产品的申请总数量
     * @param lenderId 产品id
     * @return
     */
    Integer getApplyNumByLenderId(String lenderId);


}
