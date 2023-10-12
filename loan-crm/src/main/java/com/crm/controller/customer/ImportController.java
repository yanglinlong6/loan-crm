package com.crm.controller.customer;

import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.customer.CustomerService;
import com.crm.service.customer.ImportService;
import com.crm.service.customer.model.CustomerBO;
import com.crm.service.customer.model.CustomerImportBO;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;
import java.util.Date;

/**
 * 进件接口
 */
@RestController
public class ImportController {

    @Autowired
    private ImportService importService;

    @Autowired
    private CustomerService customerService;




    /**
     * 全部进件客户
     * @param pageBO
     * @return
     */
    @PostMapping("/import/customer/page")
    @ResponseBody
    public ResultVO allImportCustomer(@RequestBody() PageBO<CustomerBO> pageBO){
        if(null == pageBO.getParamObject()){
            pageBO.setParamObject(new CustomerBO());
        }
        CustomerBO paramObject = pageBO.getParamObject();
        if(StringUtils.isBlank(paramObject.getStartDate())){
            paramObject.setStartDate(
                    DateUtil.computeMonthDay(new Date(),1,DateUtil.yyyymmdd) + CrmConstant.Date.START
            );
        }else{
            paramObject.setStartDate(DateUtil.cumputeStartDate(paramObject.getStartDate()));
        }
        if(StringUtils.isBlank(paramObject.getEndDate())){
            String endDate = DateUtil.computeMonthDay(new Date(),31,DateUtil.yyyymmdd)+CrmConstant.Date.END;
            paramObject.setEndDate(endDate);
        }else{
            paramObject.setEndDate(DateUtil.computeEndDate(paramObject.getEndDate()));
        }
        OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();
        paramObject.setOrgId(employeeBO.getOrgId());
        byte type = employeeBO.getRole().getType();
        if(CrmConstant.Role.Type.SHOP == type){
            paramObject.setShopId(employeeBO.getShopId());
        }
        if(CrmConstant.Role.Type.TEAM == type){
            paramObject.setTeamId(employeeBO.getTeamId());
        }
        pageBO.setParamObject(paramObject);
        customerService.getImportCustomerPage(pageBO);
        return ResultVO.success("获取全部进件客户列表成功",pageBO.getDataList());
    }



    /**
     * 我的进件
     * @param pageBO
     * @return
     */
    @PostMapping("/import/page")
    @ResponseBody
    public ResultVO page(@RequestBody(required = false) PageBO<CustomerImportBO> pageBO){
        if(null == pageBO)
            return ResultVO.fail("我的进件-缺少分页参数",pageBO);
        CustomerImportBO bo = pageBO.getParamObject();
        if(null == bo)
            bo = new CustomerImportBO();
        bo.setEmployeeId(LoginUtil.getLoginEmployee().getId());
        pageBO.setParamObject(bo);
        importService.getPage(pageBO);
        return ResultVO.success("我的进件-分页成功",pageBO);
    }

    /**
     * 新增进件
     * @param bo CustomerImportBO
     * @return ResultVO
     */
    @PostMapping("/import/add")
    @ResponseBody
    public ResultVO addImport(@RequestBody(required = false)CustomerImportBO bo){
        importService.addImport(bo);
        return ResultVO.success("我的进件-新增进件成功",null);
    }

    /**
     * 修改进件
     * @param bo CustomerImportBO
     * @return ResultVO
     */
    @PostMapping("/import/update")
    @ResponseBody
    public ResultVO updateImport(@RequestBody(required = false)CustomerImportBO bo){
        importService.updateImport(bo);
        return ResultVO.success("我的进件-修改进件成功",null);
    }



    /**
     * 全部进件
     * @param pageBO
     * @return
     */
    @PostMapping("/import/all/page")
    @ResponseBody
    public ResultVO allImportPage(@RequestBody(required = false) PageBO<CustomerImportBO> pageBO){
        if(null == pageBO)
            return ResultVO.fail("我的进件-缺少分页参数",pageBO);
        importService.getPage(pageBO);
        return ResultVO.success("进件管理-分页成功",pageBO);
    }

}
