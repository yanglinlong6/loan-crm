package com.crm.service.employee;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.customer.CustomerService;
import com.crm.service.employee.dao.OrgEmployeePOMapper;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.service.org.OrgService;
import com.crm.service.org.ShopService;
import com.crm.service.org.TeamService;
import com.crm.service.role.RoleService;
import com.crm.service.role.model.RoleBO;
import com.crm.util.JSONUtil;
import com.crm.util.JudgeUtil;
import com.crm.util.MD5Util;
import com.ec.v2.utlis.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    OrgService orgService;

    @Autowired
    RoleService roleService;

    @Autowired
    TeamService teamService;

    @Autowired
    ShopService shopService;

    @Autowired
    OrgEmployeePOMapper employeeMapper;

    @Autowired
    CustomerService customerService;


//    @Autowired
//    StringRedisTemplate stringRedisTemplate;


    @Override
    public OrgEmployeeBO login(String phone, String password) {
        LOG.info("客户登录账户:{}-{}",phone,password);
        if(StringUtils.isBlank(phone) || StringUtils.isBlank(password)){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"登录手机或者密码错误！");
        }
        OrgEmployeePO employeePO = employeeMapper.selectEmployeeByPhone(phone);
        if(null == employeePO){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"手机用户不存在，请联系公司管理员");
        }
        String md5 = MD5Util.getMd5String(password);
        if(!Objects.equals(md5, employeePO.getPassword())){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"登录手机或者密码错误！");
        }
        //到这里表示用户存在，则后去用户权限(如果用户是管理员，则获取所有权限)
        OrgEmployeeBO bo = JSONUtil.toJavaBean(employeePO.toString(),OrgEmployeeBO.class);
        if(null != bo){
            bo.setRole(roleService.getRoleBO(employeePO.getRoleId()));
            bo.setOrg(orgService.getOrgById(bo.getOrgId()));
        }
        return bo;
    }

    @Override
    public void getEmployeePage(PageBO<OrgEmployeeBO> page) {
        if(null == page){
            return;
        }
        if(page.getParamObject()  == null){
            OrgEmployeeBO employee = new OrgEmployeeBO();
            employee.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
            page.setParamObject(employee);
        }else{
            page.getParamObject().setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        }
        page.setSize(20);
        page.setDataList(employeeMapper.selectEmployeeByPage(page));
        int totalCount = employeeMapper.selectEmployeeCountByPage(page);
        page.setTotalCount(totalCount);
        if(totalCount == 0){
            page.setPageCount(0);
        }else if(totalCount%page.getSize() == 0){
            page.setPageCount(totalCount/page.getSize());
        }else
            page.setPageCount(totalCount/page.getSize()+1);
    }

    @Override
    public void addEmployee(OrgEmployeePO employeePO) {
        if(null == employeePO){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"未填写员工信息");
        }
        if(null == employeePO.getShopId()){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"请选择门店");
        }
        Long admin = Long.valueOf(CrmConstant.Role.ADMIN);
        if(!JudgeUtil.in(employeePO.getRoleId(),admin) && null == shopService.getShop(employeePO.getShopId())){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"门店不存在！");
        }
        if(null != employeeMapper.selectEmployeeByPhone(employeePO.getPhone())){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"手机号码已经存在！");
        }
        if(null != employeeMapper.selectEmployee(LoginUtil.getLoginEmployee().getOrgId(),employeePO.getName())){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"姓名已经存在！");
        }
        if(StringUtils.isBlank(employeePO.getPassword())){
            employeePO.setPassword(MD5Util.getMd5String("123456"));
        }else{
            employeePO.setPassword(MD5Util.getMd5String(employeePO.getPassword()));
        }
        // 如果是是管理员 或者 店长  则不需要设置团队
        RoleBO role = roleService.getRoleBO(employeePO.getRoleId());
        if(null != role && JudgeUtil.in(role.getType(),CrmConstant.Role.Type.SHOP,CrmConstant.Role.Type.ADMIN)){
            employeePO.setTeamId(0L);
        }
        if(null == employeePO.getOrgId())
            employeePO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        employeePO.setCreateBy(LoginUtil.getLoginEmployee().getName());
        employeePO.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        employeeMapper.insertEmployee(employeePO);
    }

    @Transactional
    @Override
    public void updateEmployee(OrgEmployeePO employeePO) {
        if(null == employeePO){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"未填写员工信息");
        }
        Long orgId = LoginUtil.getLoginEmployee().getOrgId();
        if(!employeePO.isUpdateAction()){
            if(null == employeePO.getShopId() || null == employeePO.getTeamId()){
                throw  new CrmException(CrmConstant.ResultCode.FAIL,"未选择门店或者团队");
            }
            if(null == shopService.getShop(employeePO.getShopId())){
                throw  new CrmException(CrmConstant.ResultCode.FAIL,"门店不存在！");
            }
            if(null == teamService.getTeam(employeePO.getTeamId())){
                throw  new CrmException(CrmConstant.ResultCode.FAIL,"团队不存在！");
            }
            // 如果是是管理员 或者 店长  则不需要设置团队
            RoleBO role = roleService.getRoleBO(employeePO.getRoleId());
            if(null != role && JudgeUtil.in(role.getType(),CrmConstant.Role.Type.ADMIN)){
                employeePO.setTeamId(null);
            }
            // 设置密码
            if(StringUtils.isNotBlank(employeePO.getPassword()))
                employeePO.setPassword(MD5Util.getMd5String(employeePO.getPassword()));
            employeePO.setOrgId(orgId);
            employeePO.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        }
        OrgEmployeePO old = employeeMapper.selectEmployeeById(employeePO.getId());
        if(null == old){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"员工不存在！");
        }
        // 如果员工修改了门店 或者团队 则将名下的客户也迁移获取
//        if(!JudgeUtil.in(employeePO.getShopId(),old.getShopId()) || !JudgeUtil.in(employeePO.getTeamId(),old.getTeamId())){
//            employeePO.setOrgId(orgId);
//            customerService.updateCustomerByEmployee(employeePO);
//            employeeMapper.updateEmployee(employeePO);
//        }else
//            employeeMapper.updateEmployee(employeePO);
        employeeMapper.updateEmployee(employeePO);
        LoginUtil.updateLoginRedis(orgId,employeePO.getPhone());
        return;
    }

    @Override
    public void updateEmployeePassword(Long id, String password) {
        if (null == id || id <=0)
            return;
        OrgEmployeePO po = new OrgEmployeePO();
        po.setId(id);
        po.setPassword(Md5Util.encryptMd5(password));
        employeeMapper.updateEmployee(po);
    }

    @Override
    public OrgEmployeePO getEmployeeById(Long id) {
        if(null == id || id <= 0)
            return null;
        return employeeMapper.selectEmployeeById(id);
    }

    @Override
    public void delEmployee(Long id) {
        if(null == id || id <= 0)
            return;
        employeeMapper.deleteEmployeeById(id);
    }

    @Override
    public OrgEmployeePO getAdminEmployee(Long orgId) {
        return employeeMapper.selectAdminEmployee(orgId);
    }

    @Override
    public List<OrgEmployeePO> getAllEmployee(Long orgId, Long shopId, Long teamId) {
        if(null == orgId)
            orgId = LoginUtil.getLoginEmployee().getOrgId();
        return employeeMapper.selectOrgEmployeeList(orgId,shopId,teamId);
    }


}
