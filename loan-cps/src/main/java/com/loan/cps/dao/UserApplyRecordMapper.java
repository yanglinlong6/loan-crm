package com.loan.cps.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.loan.cps.entity.UserApplyRecordPO;

import java.util.List;

@Mapper
public interface UserApplyRecordMapper {

    /**
     * 增加用户申请记录
     * @param record UserApplyRecordPO
     * @return
     */
    int insertApplyRecord(UserApplyRecordPO record);

    UserApplyRecordPO selectByApplyId(String applyId);

    /**
     * 查询产品的申请数量
     * @param lenderId 产品id
     * @return Integer  产品的申请数量
     */
    Integer selectApplyNumByLenderId(String lenderId);

}