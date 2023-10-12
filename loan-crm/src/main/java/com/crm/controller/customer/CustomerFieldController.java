package com.crm.controller.customer;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crm.common.CrmConstant;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.customer.CustomerService;
import com.crm.service.employee.model.OrgEmployeeBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户字符配置接口
 */
@RestController
public class CustomerFieldController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CacheConfigService cacheConfigService;

    @PostMapping("/customer/field/list")
    public ResultVO list(){
        return ResultVO.success("获取字段说明列表成功",customerService.getAllField(LoginUtil.getLoginEmployee().getOrgId()));
    }


    /**
     * 客户状态
     * @return ResultVO
     */
    @PostMapping("/customer/status")
    public ResultVO customerStatus(){
        OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();
        String value = cacheConfigService.getCacheConfigValue(CrmConstant.Config.CUSTOMER_STATUS_KEY,employeeBO.getOrgId().toString());
        if(StringUtils.isEmpty(value)){
            ResultVO.fail("未配置客户状态，请联系管理员", null);
        }
        String[] array = value.split(",");
        JSONArray data = new JSONArray();
        for(String str : array){
            String[] param = str.split("-");
            JSONObject jsonData = new JSONObject();
            jsonData.put("status",param[0]);
            jsonData.put("desc",param[1]);
            data.add(jsonData);
        }
        if(data.isEmpty()){
            ResultVO.fail("未配置客户状态，请联系管理员", null);
        }
        return ResultVO.success("客户状态",data);
    }

}
