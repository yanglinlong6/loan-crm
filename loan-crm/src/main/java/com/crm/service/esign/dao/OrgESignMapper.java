package com.crm.service.esign.dao;

import com.crm.service.esign.model.OrgESignPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface OrgESignMapper {


    OrgESignPO selectOrgESignById(@Param("id") Long id);

    OrgESignPO selectOrgESignByOrgId(@Param("orgId")Long orgId);

    int deleteOrgESignById(@Param("id")Long id);

    int deleteOrgESignByOrgId(@Param("orgId")Long orgId);

    int insertOrgESign(OrgESignPO record);

    void updateOrgESign(OrgESignPO orgESignPO);

}