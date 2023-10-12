package com.crm.controller.thirdparty;

import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.customer.CustomerService;
import com.crm.service.customer.model.CustomerBO;
import com.crm.service.thirdparty.ThirdPartyService;
import com.crm.service.thirdparty.model.OrgThirdPartyPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 第三方合作机构接口控制器
 */
@RestController
public class ThirdPartyOrgController {

    private static final Logger LOG = LoggerFactory.getLogger(ThirdPartyOrgController.class);

    @Autowired
    ThirdPartyService thirdPartyService;

    @Autowired
    CustomerService customerService;

    @PostMapping("/third/party/page")
    @ResponseBody
    public ResultVO page(@RequestBody()PageBO<OrgThirdPartyPO> pageBO){
        if(null == pageBO){
            pageBO = new PageBO<>();
        }
        thirdPartyService.getThirdPartyOrgByPage(pageBO);
        return ResultVO.success("第三方合作机构分页成功",pageBO);
    }


    @PostMapping("/third/party/add")
    @ResponseBody
    public ResultVO addThirdPartyOrg(@RequestBody()OrgThirdPartyPO orgThirdPartyPO){
        thirdPartyService.addThirdPartyOrg(orgThirdPartyPO);
        return ResultVO.success("第三方合作机构新增成功",null);
    }

    @PostMapping("/third/party/update")
    @ResponseBody
    public ResultVO updateThirdPartyOrg(@RequestBody()OrgThirdPartyPO orgThirdPartyPO){
        thirdPartyService.updateThirdPartyOrg(orgThirdPartyPO);
        return ResultVO.success("第三方合作机构编辑成功",null);
    }

    @GetMapping("/third/party/all")
    @ResponseBody
    public ResultVO allThirdPartyOrg(@RequestParam(value = "city",required = false) String city, @RequestParam(value = "productId",required = false)Long productId){
        return ResultVO.success("查询第三方合作机构成功",thirdPartyService.selectAllThirdPartyOrg(LoginUtil.getLoginEmployee().getOrgId(),city,productId));
    }


    @PostMapping("/third/party/customer/page")
    @ResponseBody
    public ResultVO joinCustomer(@RequestBody() PageBO<CustomerBO> pageBO) {
        LOG.info("获取第三方合作客户分页列表成功:{}",pageBO);
        if (null == pageBO) {
            return ResultVO.success("第三方合作机构客户分页列表成功",pageBO);
        }
        CustomerBO customer = pageBO.getParamObject();
        if(null == customer){
            customer = new CustomerBO();
        }
        customer.setQueryThirdParty(true);
        customerService.getCustomerPage(pageBO);
        pageBO.setParamObject(null);
        return ResultVO.success("第三方合作机构客户分页列表成功",pageBO);
    }



}
