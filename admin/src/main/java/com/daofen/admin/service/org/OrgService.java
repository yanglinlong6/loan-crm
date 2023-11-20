package com.daofen.admin.service.org;

import com.daofen.admin.basic.PageVO;
import com.daofen.admin.service.org.model.CityNeedPO;
import com.daofen.admin.service.org.model.OrgAptitudePO;
import com.daofen.admin.service.org.model.OrgPO;

import java.util.List;

/**
 * 机构service接口
 */
public interface OrgService {

    /**
     * 机构分页列表
     *
     * @param page PageVO<OrgPO>
     */
    void orgPage(PageVO<OrgPO> page);

    List<OrgPO> getAllOrg(String orgName);

    /**
     * 新增机构
     *
     * @param orgPO OrgPO
     */
    void addOrg(OrgPO orgPO);

    /**
     * 更新机构
     *
     * @param orgPO OrgPO
     */
    void updateOrg(OrgPO orgPO);

    OrgPO getOrg(Long orgId);

    List<CityNeedPO> getCityNeed(String startDate, String endDate);

    /**
     * 机构配量分页列表
     *
     * @param page PageVO<OrgAptitudePO>
     */
    void orgAptitudePage(PageVO<OrgAptitudePO> page);

    /**
     * 新增机构配量信息
     *
     * @param orgAptitudePO
     */
    void addOrgAptitude(OrgAptitudePO orgAptitudePO);

    /**
     * 修改机构配量信息
     *
     * @param orgAptitudePO
     */
    void updateOrgAptitude(OrgAptitudePO orgAptitudePO);

    void changeStatus(OrgAptitudePO orgAptitudePO);

    /**
     * 是否使用昨日遗留标志
     *
     * @param orgAptitudePO
     */
    void changeUseLegacyFlag(OrgAptitudePO orgAptitudePO);

    /**
     * 删除组织规则
     *
     * @param orgAptitudePO
     */
    void deleteOrgAptitude(OrgAptitudePO orgAptitudePO);

    void deleteOrg(OrgPO orgPO);
}
