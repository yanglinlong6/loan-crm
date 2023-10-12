package com.crm.config;

import com.crm.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        String[] exclude = {
                "/crm/login",
                "/crm/login/out",
                "/crm/import/customer",
                "/crm/distribute/customer",
                "/crm/import/customer/accounting"
        };
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(exclude);
    }
}
