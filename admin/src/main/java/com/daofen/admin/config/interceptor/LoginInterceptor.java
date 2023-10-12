package com.daofen.admin.config.interceptor;

import com.daofen.admin.basic.AdminException;
import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.config.AppContextUtil;
import com.daofen.admin.service.user.model.UserBO;
import com.daofen.admin.utils.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws AdminException {
        String uri = request.getRequestURI();
        if(request.getMethod().equals("OPTIONS")){
            log.info("请求连接：{}，是跨域请求。",uri);
            return true;
        }
        String token = LoginUtil.getLoginCookie(request);
        log.info("LoginInterceptor 拦截:[{}],token:[{}]",uri,token);
        if(StringUtils.isBlank(token)){
            log.error("未登录，请先登录授权，谢谢");
            throw new AdminException(ResultCode.LOGIN_FAIL);
        }
        Object value = AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().get(token);
        if(null == value || !JSONUtil.isJsonString(value.toString())){
            log.error("token失效，请先登录授权，谢谢");
            throw new AdminException(ResultCode.LOGIN_FAIL);
        }
        LoginUtil.setLoginCache(JSONUtil.toJavaBean(value.toString(), UserBO.class));
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws AdminException {
        LoginUtil.delLoginCache();
    }
}
