package com.help.loan.distribute.config;

import com.help.loan.distribute.service.api.utils.MainCity;
import com.help.loan.distribute.service.cache.CacheService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ApplicationContextConfig implements ApplicationContextAware {


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        AppContextUtil.set(applicationContext);

        CacheService cacheService = applicationContext.getBean(CacheService.class);
        cacheService.getAll();

        String value  = cacheService.getValue("advertising","city");
        if(StringUtils.isEmpty(value))
            MainCity.CITY = value;

    }
}
