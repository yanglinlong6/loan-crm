package com.daofen.crm.service.counselor.dao;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.counselor.model.RolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RoleMapper {

    List<RolePO> selectRolePage(PageVO<RolePO> pageVO);

    int selectRolePageCount(PageVO<RolePO> pageVO);

    int deleteRoleById(Long id);

    int insertRole(RolePO record);

    RolePO selectRoleById(Long id);

    RolePO selectRoleByCompanyIdAndName(@Param("companyIds") String companyIds, @Param("name")String name);

    int updateRole(RolePO record);

    /**
     *
     * @param companyId 机构id
     * @return List<RolePO>
     */
    List<RolePO> selectRoleList(Long companyId);
}