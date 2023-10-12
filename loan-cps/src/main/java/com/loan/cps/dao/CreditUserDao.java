package com.loan.cps.dao;


import com.loan.cps.service.credit.CreditUserPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CreditUserDao {

    CreditUserPO selectCreditUserByMobile(String mobile);

    int insertCreditUser(CreditUserPO creditUser);

    int updateCreditUser(CreditUserPO creditUser);
}