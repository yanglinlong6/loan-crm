package com.crm.service.channel;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.service.cache.CacheConfigService;
import com.crm.util.AppContextUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 机构媒体服务工具累
 */
public class MediaService {

    public static String[] getAll(String orgId){
        CacheConfigService cacheConfigService = AppContextUtil.getBean(CacheConfigService.class);
        String value = cacheConfigService.getCacheConfigValue(CrmConstant.Config.MEDIA, orgId);
        if(StringUtils.isBlank(value))
            throw new CrmException(CrmConstant.ResultCode.EX,"【媒体】没有配置，请联系系统技术人员");
        return value.split(",");
    }

}
