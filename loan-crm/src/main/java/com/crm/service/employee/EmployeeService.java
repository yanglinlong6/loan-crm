package com.crm.service.employee;

import com.crm.common.PageBO;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.employee.model.OrgEmployeePO;

import java.util.List;

public interface EmployeeService {

    /**
     * 登陆
     * @param phone  电话号码
     * @param password  密码
     * @return OrgEmployeeBO
     */
    OrgEmployeeBO login(String phone, String password);


    void getEmployeePage(PageBO<OrgEmployeeBO> pageBO);

    void addEmployee(OrgEmployeePO employeePO);

    void updateEmployee(OrgEmployeePO employeePO);

    void updateEmployeePassword(Long id,String password);

    OrgEmployeePO getEmployeeById(Long id);

    void delEmployee(Long id);

    /**
     * 查询机构管理员账号
     * @param orgId 机构id
     * @return
     */
    OrgEmployeePO getAdminEmployee(Long orgId);

    /**
     * 机构id必填，默认只查询机构下的员工列表
     * shopId不为空，则查询机构下，门店下的员工id
     * teamId不为空，则查询机构，门店，团队下的员工id
     * @param orgId 机构id
     * @param shopId 门店id
     * @param teamId 团队id
     * @return List<OrgEmployeePO> 员工列表
     *
     */
    List<OrgEmployeePO> getAllEmployee(Long orgId,Long shopId,Long teamId);

}
