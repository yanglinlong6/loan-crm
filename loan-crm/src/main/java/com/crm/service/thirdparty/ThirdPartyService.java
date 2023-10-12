package com.crm.service.thirdparty;

import com.crm.common.PageBO;
import com.crm.service.thirdparty.model.OrgThirdPartyPO;

import java.util.List;

/**
 * 第三方合作公司service接口
 */
public interface ThirdPartyService {

    void getThirdPartyOrgByPage(PageBO<OrgThirdPartyPO> pageBO);

    void addThirdPartyOrg(OrgThirdPartyPO po);

    void updateThirdPartyOrg(OrgThirdPartyPO po);

    /**
     * 产品全部第三方合作机构
     * @param orgId 归属机构id
     * @param city 城市
     * @param productId 产品id
     * @return List<OrgThirdPartyPO>
     */
    List<OrgThirdPartyPO> selectAllThirdPartyOrg(Long orgId,String city,Long productId);

}
