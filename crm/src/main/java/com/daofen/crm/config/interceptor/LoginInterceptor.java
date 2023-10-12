package com.daofen.crm.config.interceptor;

import com.daofen.crm.base.CrmException;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.config.AppContextUtil;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.counselor.model.PermissionPO;
import com.daofen.crm.utils.CollectionUtil;
import com.daofen.crm.utils.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoginInterceptor.class);

    private static final String[] exclude = {
            "/crm/order/bank/add",// 新增银行
            "/crm/customer/list/get", // 客户列表接口
            "/crm/customer/media/receive",//机构获客接口
            "/crm/customer/repeat/check",//机构除重接口
            "/crm/company/all", // 所有公司列表
            "/crm/shop/by/companyId", // 城市分公司下的门店类列表
            "/crm/team/by/companyId/shopId", // 门店级联查询列表
            "/crm/counselor/permission/list", // 客户权限列表
            "/crm/counselor/team/list", // 团队级联查询列表
            "/crm/role/list/with/company",
            "/crm/customer/my/uncontacted/num/get", //
            "/crm/customer/meida/receive", // 接收客户数据
            "/crm/order/bank/list/get" ,// 订单银行列表
            "/crm/permission/list",
            "/crm/login/send/sms/common",
            "/crm/customer/new/get",
            "/crm/customer/public/pool/list/get",
            "/crm/counselor/permission/list",
            "/crm/customer/team/list/get",
            "/crm/customer/matrix/get",
            "/crm/customer/matrix/replace",
            "/crm/leave",
            "/crm/join",
            "/crm/push",
            "/crm/memorandum/add",
            "/crm/memorandum/del",
            "/crm/customer/sse/push",
            "/crm/customer/sse/send",
            "/crm/customer/team/uncontacted/num/get",
            "/crm/call/account"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String method = request.getMethod();
//        if("OPTIONS".equals(method)){
//            LOG.info("LoginInterceptor 拦截:[{}],直接返回true",method);
//            return true;
//        }
        String uri = request.getRequestURI();
        String token = LoginUtil.getLoginCookie(request);
        LOG.info("LoginInterceptor 拦截:[{}],token:[{}]",uri,token);
        if(StringUtils.isBlank(token)){
            if(isExclude(uri)){ // 是否排除该请求,则跳过改请求，否则提示用户登录
                return true;
            }
            throw new CrmException(ResultVO.ResultCode.LOGIN,"未登录,请登陆！",null);
        }
        String value = AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().get(token);

        if(StringUtils.isBlank(value) || !JSONUtil.isJsonString(value)){
            throw new CrmException(ResultVO.ResultCode.LOGIN,"登录已失效,请重新登陆！",null);
        }
        CompanyCounselorBO counselorBO = JSONUtil.toJavaBean(value, CompanyCounselorBO.class);
        if(null == counselorBO){
            throw new CrmException(ResultVO.ResultCode.LOGIN,"非法用户,请正确手机登陆！",null);
        }
        //判断用户是否有该请求权限
        if(isExclude(uri)){ // 是否排除该请求
            LoginUtil.setLoginThreadLocal(counselorBO); // 将登陆信息加入到线程共享对象中
            return true;
        }
//        LOG.info("开始验证权限：token:{},uri:{}",token,uri);
        List<PermissionPO> permissions = counselorBO.getRole().getPermissions();
        if(CollectionUtil.isEmpty(permissions)){
            throw new CrmException(ResultVO.ResultCode.NO_AUTH,"抱歉,您没有该功能权限,如需要操作该功能,请联系贵司管理员【空】",null);
        }
        for(PermissionPO  permission : permissions){
            String userUri= permission.getUri();
            LOG.debug("权限验证：比较：{}--{}",uri,userUri);
            if(uri.equals(userUri)){
                LoginUtil.setLoginThreadLocal(counselorBO);
                return true;
            }
        }
        throw new CrmException(ResultVO.ResultCode.NO_AUTH,"抱歉,您没有该功能权限,如需要操作该功能,请联系贵司管理员",null);
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
                LOG.info("exclude uri-->{},is:{}",uri,true);
                return true;
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LoginUtil.removeLoginThreadLocal();
    }
}
