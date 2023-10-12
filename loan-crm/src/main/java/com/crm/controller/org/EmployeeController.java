package com.crm.controller.org;

import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.customer.CustomerService;
import com.crm.service.employee.EmployeeService;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.service.org.OrgService;
import com.crm.service.role.RoleService;
import com.crm.service.role.model.RoleBO;
import com.crm.util.JSONUtil;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import com.ec.v2.utlis.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    EmployeeService employeeService;

    @Autowired
    private OrgService orgService;


    @PostMapping("/employee/page")
    @ResponseBody
    public ResultVO page(@RequestBody()PageBO<OrgEmployeeBO> page){
        employeeService.getEmployeePage(page);
        return ResultVO.success("员工分页列表",page);
    }

    @PostMapping("/employee/list")
    @ResponseBody
    public ResultVO list(@RequestParam(value = "shopId",required = false) Long shopId,@RequestParam(value = "teamId",required = false)Long teamId){
        OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();
        if(null == shopId || shopId.intValue() <= 0){
            shopId = employeeBO.getShopId();
        }
        if(null == teamId || teamId.intValue() <= 0){
            teamId = employeeBO.getTeamId();
        }
        List<OrgEmployeePO> dataList = employeeService.getAllEmployee(employeeBO.getOrgId(),shopId,teamId);
        return ResultVO.success("员工分页列表",dataList);
    }

    @PostMapping("/employee/add")
    @ResponseBody
    public ResultVO add(@RequestBody()OrgEmployeePO employeePO){
        employeeService.addEmployee(employeePO);
        return ResultVO.success("新增员工成功",employeePO);
    }


    @Autowired
    RoleService roleService;

    @PostMapping("/employee/update")
    @ResponseBody
    public ResultVO update(@RequestBody()OrgEmployeePO employeePO){
        RoleBO roleBO = roleService.getRoleBO(employeePO.getRoleId());
        if(JudgeUtil.in(employeePO.getRoleId(),CrmConstant.Role.ADMIN) ||    JudgeUtil.in(roleBO.getType(),CrmConstant.Role.Type.ADMIN)){
            return ResultVO.fail("管理员不能被修改",employeePO);
        }
        employeeService.updateEmployee(employeePO);
        return ResultVO.success("更新员工成功",employeePO);
    }

    @PostMapping("/employee/update/action")
    @ResponseBody
    public ResultVO updateAction(@RequestBody(required = false) OrgEmployeeBO orgEmployeeBO){
        if(null == orgEmployeeBO || ListUtil.isEmpty(orgEmployeeBO.getIdList())){
            return ResultVO.fail("缺少请求参数",null);
        }
        OrgEmployeePO po = JSONUtil.toJavaBean(orgEmployeeBO.toString(),OrgEmployeePO.class);
        List<Long> idList = orgEmployeeBO.getIdList();
        for(Long id : idList){
            po.setId(id);
            po.setUpdateAction(true);
            employeeService.updateEmployee(po);
        }
        return ResultVO.success("更新行为成功",null);
    }

    @GetMapping("/employee/update/password")
    @ResponseBody
    public ResultVO updatePassword(@RequestParam("password1")String password1,@RequestParam("password2")String password2){
        if(StringUtils.isBlank(password1) || StringUtils.isBlank(password2))
            return ResultVO.fail("修改密码：新密码不能为空",null);
        if(!password1.equals(password2))
            return ResultVO.fail("修改密码：两次密码不相同",null);
        if(password1.length() <6 || password1.length() > 12)
            return ResultVO.fail("修改密码：密码长度6-12个字符",null);
        employeeService.updateEmployeePassword(LoginUtil.getLoginEmployee().getId(),password1);
        return ResultVO.success("修改密码成功",null);
    }


    @Autowired
    CustomerService customerService;

    @RequestMapping("/employee/state")
    @ResponseBody
    @Transactional
    public ResultVO employeeState(@RequestParam("id")Long id){
        log.info("离职并回收员工客户:{}",id );

        OrgEmployeePO employeePO = employeeService.getEmployeeById(id);
        if(null == employeePO){
            return ResultVO.fail("员工不存在",null);
        }
        customerService.updateCustomerByEmployee(employeePO);

        employeePO.setStatus(CrmConstant.Employee.Status.NO);
        employeeService.updateEmployee(employeePO);

        return ResultVO.success("离职并回收员工客户成功",null);
    }

    @RequestMapping("/employee/del")
    @ResponseBody
    public ResultVO delEmployee(@RequestParam("id")Long id){
        log.info("删除员工:{}",id );
        OrgEmployeePO employeePO = employeeService.getEmployeeById(id);
        if(null == employeePO){
            return ResultVO.fail("员工不存在",null);
        }
        if(CrmConstant.Role.ADMIN == employeePO.getRoleId()){
            return ResultVO.fail("管理不能删除!",null);
        }
        if(CrmConstant.Employee.Status.NO != employeePO.getStatus().byteValue()){
            return ResultVO.fail("员工未离职",null);
        }
        employeeService.delEmployee(id);
        return ResultVO.success("删除员工成功",null);
    }

}
