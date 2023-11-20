package com.daofen.admin.service.org.dao;

import com.daofen.admin.basic.PageVO;
import com.daofen.admin.service.org.model.CityNeedPO;
import com.daofen.admin.service.org.model.OrgAptitudePO;
import com.daofen.admin.service.org.model.OrgPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 */
@Mapper
public interface OrgDao {

    List<OrgPO> selectOrgListByPage(PageVO<OrgPO> page);

    Integer selectOrgListCountByPage(PageVO<OrgPO> page);

    /**
     * 根据机构名称查询机构信息对象
     *
     * @param orgName 机构名称
     * @return OrgPO
     */
    OrgPO selectOrgByName(OrgPO orgName);

    /**
     * 根据机构id查询机构对象信息
     *
     * @param orgId
     * @return OrgPO
     */
    OrgPO selectOrgByOrgId(Long orgId);

    List<OrgPO> selectAllOrg(@Param("orgName") String orgName);

    Long selectMaxOrg();

    void insertOrg(OrgPO orgPO);

    void updateOrg(OrgPO orgPO);

    List<CityNeedPO> selectCityNeed(@Param("startDate") String startDate, @Param("endDate") String endDate);


    List<OrgAptitudePO> selectOrgAptitudePage(PageVO<OrgAptitudePO> pageVO);

    Integer selectOrgAptitudePageCount(PageVO<OrgAptitudePO> pageVO);

    List<OrgAptitudePO> selectOrgAptitudeByOrgId(Long orgId);

    void insertOrgAptitude(OrgAptitudePO orgAptitudePO);

    void updateOrgAptitude(OrgAptitudePO orgAptitudePO);

    void changeStatus(@Param("id") Long id, @Param("status") Integer status);

    void changeUseLegacyFlag(@Param("id") Long id, @Param("useLegacyFlag") Integer useLegacyFlag);
}
