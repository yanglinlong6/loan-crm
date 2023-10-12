package com.crm.controller;

import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.cache.model.CacheConfigPO;
import com.crm.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.faces.annotation.RequestMap;
import java.util.List;

@RestController
public class SystemConfigController {

    @Autowired
    CacheConfigService cacheConfigService;

    @RequestMapping("/sys/config/get")
    @RequestMap
    private ResultVO getConfig(){
        List<CacheConfigPO> list = cacheConfigService.getConfig(LoginUtil.getLoginEmployee().getOrgId()) ;
        return ResultVO.success("获取机构系统配置成功",list);
    }

    @RequestMapping("/sys/config/update")
    @RequestMap
    private ResultVO updateConfig(List<CacheConfigPO> list){
        if(ListUtil.isEmpty(list)){
            return ResultVO.success("获取机构系统配置成功",list);
        }
        for(CacheConfigPO po : list){
            po.setKey(LoginUtil.getLoginEmployee().getOrgId().toString());
            cacheConfigService.updateConfig(po);
        }
        cacheConfigService.getAllCacheConfig();
        return ResultVO.success("获取机构系统配置成功",list);
    }


}
