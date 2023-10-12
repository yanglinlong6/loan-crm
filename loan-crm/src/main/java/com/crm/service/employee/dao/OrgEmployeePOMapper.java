package com.crm.service.employee.dao;


import com.crm.common.PageBO;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.employee.model.OrgEmployeePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface OrgEmployeePOMapper {

    List<OrgEmployeeBO> selectEmployeeByPage(PageBO<OrgEmployeeBO> page);

    int selectEmployeeCountByPage(PageBO<OrgEmployeeBO> page);

    int deleteEmployeeById(Long id);

    int insertEmployee(OrgEmployeePO employeePO);

    OrgEmployeePO selectEmployeeById(Long id);

    OrgEmployeePO selectAdminEmployee(@Param("orgId")Long orgId);


    OrgEmployeePO selectEmployeeByPhone(@Param("phone")String phone);

    OrgEmployeePO selectEmployee(@Param("orgId") Long orgId, @Param("name") String name);

    List<OrgEmployeePO> selectOrgEmployeeList(@Param("orgId") Long orgId,@Param("shopId") Long shopId,@Param("teamId") Long teamId);

    int updateEmployee(OrgEmployeePO employeePO);

}