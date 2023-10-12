package com.crm.service.org.dao;


import com.crm.service.org.model.OrgPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface OrgPOMapper {

    /**
     * 根据机构访问的二级域名,获取机构配置信息
     * @param domain2  二级域名
     * @return OrgPO
     * @auth zhangqiuping
     */
    OrgPO selectOrgByDomain2(@Param("domain2") String domain2);

    /**
     * 根据机构id,查询机构信息
     * @param id 机构id
     * @return OrgPO
     * @auth zhangqiuping
     */
    OrgPO selectOrgById(@Param("id") Long id);

    int insertOrg(OrgPO orgPO);

    int updateOrg(OrgPO orgPO);
}