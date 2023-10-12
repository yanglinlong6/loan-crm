package com.crm.controller.login;

import com.crm.common.CrmConstant;
import com.crm.common.ResultVO;
import com.crm.controller.api.WebSocket;
import com.crm.service.employee.dao.OrgEmployeePOMapper;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.service.role.RoleService;
import com.crm.util.AppContextUtil;
import com.crm.util.JSONUtil;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LoginUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LoginUtil.class);

    private static final String TOKEN = "crm_token";
    
    public static final String TOKEN_VALUE_PREFIX = "crm_fanxin_";

    private static final ThreadLocal<OrgEmployeeBO> loginThreadLocal = new ThreadLocal<>();


    public static void setLoginThreadLocal(OrgEmployeeBO employeeBO){
        loginThreadLocal.set(employeeBO);
    }

    public static OrgEmployeeBO getLoginEmployee(){
        return loginThreadLocal.get();
    }

    public static void removeLoginThreadLocal(){
        loginThreadLocal.remove();
    }


    /**
     * 生成token
     * @return 返货生成的token字符串
     */
    public static String generateToken(OrgEmployeeBO orgEmployee){

        return TOKEN_VALUE_PREFIX+orgEmployee.getOrgId()+"_"+orgEmployee.getPhone()+"_"+System.currentTimeMillis();
    }

    /**
     * 登录成功后，写登录token到cookie中
     */
    public static String writeCookie(HttpServletResponse response,OrgEmployeeBO orgEmployee){
        String token = generateToken(orgEmployee);
        LOG.info("用户:[{}],token:[{}]",orgEmployee.getPhone(),token);
        AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().set(token,orgEmployee.toString(),10,TimeUnit.HOURS);
        Cookie cookie = new Cookie(TOKEN, token);
        cookie.setValue(token);
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
        return token;
    }


    public static String readCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(null == cookies || cookies.length <=0)
            return null;
        for(Cookie cookie : cookies){
            if(TOKEN.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }


    public static void loginOut(HttpServletRequest request){
        StringRedisTemplate stringRedisTemplate =  AppContextUtil.getBean(StringRedisTemplate.class);
        String token = LoginUtil.readCookie(request);
        if(StringUtils.isBlank(token))
            return;
        String value = stringRedisTemplate.opsForValue().get(token);
        if(StringUtils.isBlank(value)){
            return;
        }
        LOG.info("退出登陆:{}-{}",token,stringRedisTemplate.opsForValue().getOperations().delete(token));
        String mobile = token.split("_")[2];
        if(WebSocket.socketMap.containsKey(mobile)){
            WebSocket webSocket = WebSocket.socketMap.get(mobile);
            webSocket.onClose(webSocket.getSession(),mobile);
        }
    }


    public static void updateLoginRedis(Long orgId,String phone){
        if(null == orgId){
            return;
        }
        //更新缓存
        // 获取机构下已经登录的账号
        StringRedisTemplate stringRedisTemplate =  AppContextUtil.getBean(StringRedisTemplate.class);
        String key = null;
        if(StringUtils.isBlank(phone)){
            key = LoginUtil.TOKEN_VALUE_PREFIX+orgId+"_*";
        }else{
            key = LoginUtil.TOKEN_VALUE_PREFIX+orgId+"_"+phone+"_*";
        }
        Set<String> keys = stringRedisTemplate.opsForValue().getOperations().keys(key);
        if(ListUtil.isEmpty(keys)){
            LOG.info("刷新员工缓存:用户【{}】未登陆",key);
            return;
        }
        stringRedisTemplate.delete(keys);
    }

    /**
     * 判断登陆员工是否是管理元
     * @param bo OrgEmployeeBO
     * @return boolean true-是   false-否
     */
    public static boolean isAdmin(OrgEmployeeBO bo){
        if( bo.getRoleId().longValue() == CrmConstant.Role.ADMIN ||  bo.getRole().getType().byteValue() == CrmConstant.Role.Type.ADMIN){
            return true;
        }
        return false;
    }


    /**
     * 判断机构休息模式开关
     * @param orgId 机构id
     * @return Byte: 1-开启, 0-关闭
     */
    public static Byte judgeSwitch(Long orgId){
        if(null == orgId || orgId <= 0L){
            return CrmConstant.Switch.NO;
        }
        StringRedisTemplate redisTemplate = AppContextUtil.getBean(StringRedisTemplate.class);
        if(redisTemplate.opsForHash().hasKey(CrmConstant.Switch.KEY,orgId.toString())){
            Object value = redisTemplate.opsForHash().get(CrmConstant.Switch.KEY,orgId.toString());
            // 表示还没有设置开关状态
            if(null == value){
                redisTemplate.opsForHash().put(CrmConstant.Switch.KEY,orgId.toString(),CrmConstant.Switch.NO);
                return CrmConstant.Switch.NO;
            }
            // 返回开关状态
            return Byte.valueOf(value.toString());
        }
        // 到这里表示还没有设置开关状态, 直接返回关闭状态
        return CrmConstant.Switch.NO;
    }



}
