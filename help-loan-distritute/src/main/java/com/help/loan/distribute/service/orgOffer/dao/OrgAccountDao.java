package com.help.loan.distribute.service.orgOffer.dao;

import com.help.loan.distribute.service.orgOffer.model.OrgAccountPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface OrgAccountDao {


    OrgAccountPO selectByOrgId(@Param("orgId") Long orgId);

    void updateByOrgId(OrgAccountPO orgAccountPO);


}
