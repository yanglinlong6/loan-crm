package com.loan.cps.dao;


import com.loan.cps.service.accounting.AccountingUserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AccountingUserDao {


    int insertAccountUser(AccountingUserPO accountingUserPO);

    int updateAccountingUserById(AccountingUserPO accountingUserPO);

    /**
     * 查询未发送成功的客户列表
     * @param count 查询的数量
     * @param status 状态
     * @return List<AccountingUserPO>
     */
    List<AccountingUserPO> getAccountingUserWithNotSend(@Param("count") int count, @Param("status")Byte status);
}