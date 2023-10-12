package com.crm.service.thirdparty.dao;

import com.crm.common.PageBO;
import com.crm.service.thirdparty.model.OrgThirdPartyPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 第三方合作机构数据查询映射类
 */
@Component
@Mapper
public interface OrgThirdPartyMapper {

    /**
     * 第三方合作机构分页查询列表
     * @param partyPOPageBO PageBO<OrgThirdPartyPO>
     * @return List<OrgThirdPartyPO>
     */
    List<OrgThirdPartyPO> selectThirdPartyOrgByPage(PageBO<OrgThirdPartyPO> partyPOPageBO);

    /**
     * 第三方合作机构分页查询总数量
     * @param partyPOPageBO PageBO<OrgThirdPartyPO>
     * @return 总数量
     */
    Integer selectThirdPartyOrgByPageCount(PageBO<OrgThirdPartyPO> partyPOPageBO);

    int insertThirdPartyOrg(OrgThirdPartyPO record);

    OrgThirdPartyPO selectThirdPartyOrgById(@Param("id") Long id);

    List<OrgThirdPartyPO> selectThirdPartyOrgByOrgId(@Param("orgId")Long OrgId);

    List<OrgThirdPartyPO> selectThirdPartyOrg(@Param("orgId")Long orgId,@Param("city")String city,@Param("productId")Long productId);

    int updateThirdPartyOrgById(OrgThirdPartyPO orgThirdParty);
}