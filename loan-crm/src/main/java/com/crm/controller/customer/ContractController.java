package com.crm.controller.customer;

import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.customer.ContractService;
import com.crm.service.customer.model.CustomerContractBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 签约列表接口
 */
@Controller
public class ContractController {

    /**
     * 签约合同service
     */
    @Autowired
    ContractService contractService;
    /**
     * 客户管理：签约列表
     * @param page PageBO<CustomerContractBO>
     * @return ResultVO
     */
    @PostMapping("/customer/contract")
    @ResponseBody
    public ResultVO getContractPage(@RequestBody(required = false) PageBO<CustomerContractBO> page){
        if(null == page){
            return ResultVO.fail("我的客户-我的签约-无分页查询",null);
        }
        CustomerContractBO bo = page.getParamObject();
        if(null == bo)
            bo = new CustomerContractBO();
        bo.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        contractService.getPage(page);
        return ResultVO.success("我的客户-我的签约-分页查询成功",page);
    }


}
