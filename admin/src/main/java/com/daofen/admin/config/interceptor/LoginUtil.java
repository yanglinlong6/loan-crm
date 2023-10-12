package com.daofen.admin.config.interceptor;

import com.daofen.admin.config.AppContextUtil;
import com.daofen.admin.service.user.model.UserBO;
import com.daofen.admin.service.user.model.UserPO;
import com.daofen.admin.utils.CollectionUtil;
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

    private static final String TOKEN_NAME = "admin_login_token";

    private static final String LOGIN_PREFIX = "admin_login_";

    private static final ThreadLocal<UserBO> loginCache = new ThreadLocal<UserBO>();





    /**
     * 登录成功后，写登录token到cookie中
     */
    public static String writeLoginCookie(HttpServletResponse response, UserBO user){
        delLoginRedis(user);//删除之前登录token
        String token = generateToken(user);//生成token
        LOG.info("用户:[{}],token:[{}]",user.getUsername(),token);
        Cookie cookie = new Cookie(TOKEN_NAME, token);
        cookie.setValue(token);
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
        // 响应完成之后写入redis缓存
        AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().set(token,user.toString(),12*60*60, TimeUnit.SECONDS);
        return token;
    }

    private static void delLoginRedis(UserBO user){
        String loginPrefix = LOGIN_PREFIX+user.getUsername();
        StringRedisTemplate redisTemplate = AppContextUtil.getBean(StringRedisTemplate.class);
        Set<String> keys = redisTemplate.keys(loginPrefix+"*");
        if(CollectionUtil.isEmpty(keys)){
            return;
        }
        for (String key : keys){
            redisTemplate.delete(key);
        }
    }


    /**
     * 生成token
     * @return 返货生成的token字符串
     */
    private static String generateToken(UserPO user){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return LOGIN_PREFIX+user.getUsername()+"_"+uuid;
    }

    public static String getLoginCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String token=null;
        if(null == cookies || cookies.length <=0){
            token = request.getHeader("Access-Token");
            return token;
        }
        for(Cookie cookie : cookies){
            if(TOKEN_NAME.equals(cookie.getName())){
                token = cookie.getValue();
                break;
            }
        }
        LOG.info("token:{}",token);
        if(StringUtils.isBlank(token))
            token = request.getHeader("Access-Token");
        return token;
    }

    /**设置线程共享：用户登录信息*/
    public static void setLoginCache(UserBO userBO){ loginCache.set(userBO); }
    /**获取线程共享：用户登录信息*/
    public static UserBO getLoginCache(){return loginCache.get();}
    /**清理线程共享：：用户登录信息*/
    public static void delLoginCache(){loginCache.remove();}







}
