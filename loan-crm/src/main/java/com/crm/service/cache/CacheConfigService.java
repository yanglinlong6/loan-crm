package com.crm.service.cache;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crm.service.cache.model.CacheConfigPO;
import com.crm.util.JudgeUtil;
import org.springframework.util.StringUtils;

import java.util.List;

public interface CacheConfigService {

    void getAllCacheConfig();

    void getAllCacheConfig(String orgId);

    String getCacheConfigValue(String field, String key);

    List<CacheConfigPO> getConfig(Long orgId);

    void addConfig(CacheConfigPO po);

    void updateConfig(CacheConfigPO po);


    /**
     * 解析客户状态
     * @param value 0-新客户,1-已分配,2-跟进中,3-有意向,4-已上门,5-已签约,6-进件回款,7-完成
     * @return JSONArray
     */
    default JSONArray parseCustomerProcess(String value){
        if(StringUtils.isEmpty(value))
            return null;
        String[] array = value.split(",");
        JSONArray data = new JSONArray();
        for(String str : array){
            String[] param = str.split("-");
            JSONObject jsonData = new JSONObject();
            jsonData.put("status",param[0]);
            jsonData.put("desc",param[1]);
            data.add(jsonData);
        }
        return data;
    }

    default Byte parseCustomerProcess(String value, String processDesc){
        if(StringUtils.isEmpty(value))
            return null;
        String[] array = value.split(",");
        Byte process = null;
        for(String str : array){
            String[] param = str.split("-");
            String desc = param[1];
            if(desc.contains(processDesc)){
                process = Byte.valueOf(param[0]);
                return process;
            }
        }
        return process;
    }

}
