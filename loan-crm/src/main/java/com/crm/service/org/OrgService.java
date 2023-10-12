package com.crm.service.org;

import com.crm.service.org.model.OrgPO;
import com.crm.service.org.model.OrgRegisterPO;

/**
 * 机构service接口
 */
public interface OrgService {


    OrgPO getOrgByDomain2(String domain2);

    OrgPO getOrgById(Long id);

    OrgPO addOrg(OrgPO orgPO);

    void updateOrg(OrgPO orgPO);

    /**
     * 增加客户上门登记记录
     * @param registerPO OrgRegisterPO
     */
    void addRegister(OrgRegisterPO registerPO);
}
