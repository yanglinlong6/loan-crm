package com.crm.controller.api;

import com.crm.common.CrmConstant;
import com.crm.common.ResultVO;
import com.crm.service.api.ApiService;
import com.crm.service.api.model.ImportBO;
import com.crm.service.employee.model.OrgEmployeePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 客户入库api
 */
@RestController
public class CustomerApiController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerApiController.class);

    @Autowired
    ApiService apiService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @PostMapping("/import/customer")
    public ResultVO add(@RequestBody() ImportBO importBO){
        LOG.info("入库新客户:{}",importBO.toString());
        apiService.importCustomer(importBO);
        return ResultVO.success("客户入库成功",null);
    }

    /**财会客户导入*/
    @PostMapping("/import/customer/accounting")
    public ResultVO addAccountingCustomer(@RequestBody() ImportBO importBO){
        OrgEmployeePO po = apiService.importAccountingCustomer(importBO);
        return ResultVO.success("客户入库成功",null == po ? null:po.getWechat()); // 返回微信号
    }



    @GetMapping("/distribute/customer")
    public ResultVO distribute(@RequestParam Long orgId){
        String key = CrmConstant.Config.Distribute.DIS + orgId;
        if(stringRedisTemplate.hasKey(key)){
            return ResultVO.success("机构["+orgId+"]正在分配中",null);
        }
        try{
            stringRedisTemplate.opsForValue().set(key,"1",2, TimeUnit.MINUTES);
            apiService.distributeCustomer(orgId);
            return ResultVO.success("机构["+orgId+"]自动分配调用成功",null);
        }catch (Exception e){
            LOG.error("【机构{}】分配定时任务异常：{}",orgId,e.getMessage(),e);
            return ResultVO.fail("机构："+orgId+"，分发异常："+e.getMessage(),null);
        }finally {
            stringRedisTemplate.opsForValue().getOperations().delete(key);
        }

    }
}
