package com.crm.controller.customer;

import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.behind.BehindCustomerService;
import com.crm.service.behind.model.CustomerBehindBO;
import com.crm.service.behind.model.CustomerBehindRemarkPO;
import com.crm.service.customer.model.ContractProductPO;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class BehindCustomerController {

    @Autowired
    private BehindCustomerService behindCustomerService;

    /**
     * 我的客户
     * @param pageBO
     * @return
     */
    @PostMapping("/behind/my/page")
    public ResultVO myCustomer(@RequestBody() PageBO<CustomerBehindBO> pageBO){
        CustomerBehindBO paramObject = pageBO.getParamObject();
        if(null == paramObject)
            paramObject = new CustomerBehindBO();
        OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();
        paramObject.setEmployeeId(employeeBO.getId());
        behindCustomerService.getPage(pageBO,employeeBO);
        return ResultVO.success("我分配客户分页成功",pageBO);
    }

    /**
     * 全部客户
     * @param pageBO
     * @return
     */
    @PostMapping("/behind/all/page")
    public ResultVO allCustomer(@RequestBody()PageBO<CustomerBehindBO> pageBO){
        OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();
        if(JudgeUtil.in(employeeBO.getRole().getType(), CrmConstant.Role.Type.COMMON,CrmConstant.Role.Type.CHANNEL))
            return ResultVO.fail("您没有权限查看【全部客户】，请联系管理员",null);
        CustomerBehindBO paramObject = pageBO.getParamObject();
        if(null == paramObject)
            paramObject = new CustomerBehindBO();
        paramObject.setOrgId(employeeBO.getOrgId());
        if(CrmConstant.Role.Type.SHOP == employeeBO.getRole().getType().byteValue()){
            paramObject.setShopId(employeeBO.getShopId());
        }
        if(CrmConstant.Role.Type.TEAM == employeeBO.getRole().getType().byteValue()){
            paramObject.setTeamId(employeeBO.getTeamId());
        }
        if(StringUtils.isBlank(paramObject.getStartDate())){
            paramObject.setStartDate("2021-01-01"); // 默认查询全部客户
        }
        pageBO.setParamObject(paramObject);
        behindCustomerService.getPage(pageBO,employeeBO);
        return ResultVO.success("待分配客户分页成功",pageBO);
    }

    /**
     * 待分配客户
     * @param pageBO PageBO<CustomerBehindBO>
     * @return ResultVO
     */
    @PostMapping("/behind/wait/page")
    public ResultVO waitDistributeCustomer(@RequestBody()PageBO<CustomerBehindBO> pageBO){
        OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();
        if(JudgeUtil.in(employeeBO.getRole().getType(), CrmConstant.Role.Type.TEAM,CrmConstant.Role.Type.COMMON,CrmConstant.Role.Type.CHANNEL)){
            return ResultVO.fail("您没有权限查看【待分配】客户，请联系管理员",null);
        }
        CustomerBehindBO paramObject = pageBO.getParamObject();
        if(null == paramObject)
            paramObject = new CustomerBehindBO();
        paramObject.setProcess(Byte.valueOf(CrmConstant.Customer.init)); // 查询待分配客户
        behindCustomerService.getPage(pageBO,employeeBO);
        return ResultVO.success("待分配客户分页成功",pageBO);
    }


    /**
     * 分配客户
     * @param bo CustomerBehindBO
     * @return ResultVO
     */
    @PostMapping("/behind/dis")
    public ResultVO disCustomer(@RequestBody()CustomerBehindBO bo){

        return behindCustomerService.disCustomer(bo);
    }

    @PostMapping("/behind/add/remark")
    public ResultVO addRemark(@RequestBody() CustomerBehindRemarkPO po){
        return behindCustomerService.addCustomerRemark(po);
    }


    @PostMapping("/behind/update/product/process")
    public ResultVO updatProductProcess(@RequestBody() ContractProductPO po){
        return behindCustomerService.updateProductProcess(po);
    }

    @GetMapping("/behind/get/product/process")
    public ResultVO getContractProduct(@RequestParam("id") Long id){
        return ResultVO.success("",behindCustomerService.getProduct(id));
    }



}
