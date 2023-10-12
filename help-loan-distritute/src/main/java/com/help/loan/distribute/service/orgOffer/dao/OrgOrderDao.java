package com.help.loan.distribute.service.orgOffer.dao;

import com.help.loan.distribute.service.orgOffer.model.OrgOrderPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 机构预付订单记录
 */
@Component
@Mapper
public interface OrgOrderDao {


    List<OrgOrderPO> selectAllByOrgId(@Param("orgId") Long orgId, @Param("startDate")String startDate);



}
