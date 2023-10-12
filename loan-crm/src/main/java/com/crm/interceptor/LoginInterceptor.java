package com.crm.interceptor;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.controller.login.LoginUtil;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.util.AppContextUtil;
import com.crm.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoginInterceptor.class);

    private static final String[] exclude = {
            "/crm/login",
            "/crm/login/out",
            "/crm/import/customer",
            "/crm/import/customer/accounting",
            "/crm/distribute/customer",
            "/crm/error",
            "/crm/esign/receive",//e签宝回调路径
            "/crm/org/dengji"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if("OPTIONS".equals(method)){
            LOG.info("LoginInterceptor 拦截:[{}],直接返回true",method);
            return true;
        }
        String uri = request.getRequestURI();
        LOG.info("请求地址：{}",uri);
        if(isExclude(uri)){ // 是否跳过该请求
            return true;
        }
        String token = LoginUtil.readCookie(request);
        LOG.info("LoginInterceptor 拦截:[{}],token:[{}]",uri,token);
        if(StringUtils.isBlank(token)){
            throw new CrmException(CrmConstant.ResultCode.NOT_AUTH,"未登录,请登陆！",null);
        }
        String value = AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().get(token);
        if(StringUtils.isBlank(value) || !JSONUtil.isJsonString(value)){
            throw new CrmException(CrmConstant.ResultCode.NOT_AUTH,"登录已失效,请重新登陆！",null);
        }
        OrgEmployeeBO employeeBO = JSONUtil.toJavaBean(value, OrgEmployeeBO.class);
        if(null == employeeBO){
            throw new CrmException(CrmConstant.ResultCode.NOT_AUTH,"非法用户,请正确手机登陆！",null);
        }
        // 到这里表示已经登陆
        LoginUtil.setLoginThreadLocal(employeeBO);
        return true;
    }

    /**
     * 如果包含在需要排除的数组中，则返回true，否则返回false
     * @param uri 当前请求的uri地址
     * @return boolean  如果包含在需要排除的数组中，则返回true，否则返回false
     */
    private boolean isExclude(String uri){
        if(StringUtils.isBlank(uri))
            return false;
        for(String string : exclude){
            if(string.equals(uri)){
                return true;
            }
        }
        LOG.info("exclude uri-->{},is:{}",uri,false);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LoginUtil.removeLoginThreadLocal();
    }
}
