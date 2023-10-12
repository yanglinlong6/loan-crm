package com.daofen.crm.controller.login;

import com.daofen.crm.config.AppContextUtil;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LoginUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LoginUtil.class);

    private static final String TOKEN = "crm_token";
    
    public static final String LOGIN_PREFIX = "crm_login_";
    
    private static final String[] randomArray = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

    private static final Random random = new Random();

    private static final ThreadLocal<CompanyCounselorBO> loginThreadLocal = new ThreadLocal<>();


    public static void setLoginThreadLocal(CompanyCounselorBO counselorBO){
        loginThreadLocal.set(counselorBO);
    }

    public static CompanyCounselorBO getLoginUser(){
        return loginThreadLocal.get();
    }

    public static void removeLoginThreadLocal(){
        loginThreadLocal.remove();
    }

    /**
     * 生成4位短信验证码
     * @param mobile 手机号码
     * @return 返回生成的4位短信验证码
     */
    public static String generateMobileCode(String mobile){
        StringBuffer code = new StringBuffer();
        for(int i=0;i<4;i++){
            int index = random.nextInt(36);
            code.append(randomArray[index]);
        }
        LOG.info("生成手机验证码：{}--{}",mobile,code);
        return code.toString();
    }

    /**
     * 生成token
     * @return 返货生成的token字符串
     */
    public static String generateToken(CompanyCounselorBO counselorBO){
        String uuid = UUID.randomUUID().toString().toLowerCase().replaceAll("-","");
        return LOGIN_PREFIX+counselorBO.getCompanyId()+"_"+counselorBO.getMobile()+"_"+uuid;
    }

    public static String generateToken(CompanyCounselorBO counselorBO,String uuid){
        return LOGIN_PREFIX+counselorBO.getCompanyId()+"_"+counselorBO.getMobile()+"_"+uuid;
    }

    /**
     * 登录成功后，写登录token到cookie中
     */
    public static void writeCookie(HttpServletResponse response,CompanyCounselorBO counselorBO){
        String token = generateToken(counselorBO);
        LOG.info("用户:[{}],token:[{}]",counselorBO.getMobile(),token);
        AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().set(token,counselorBO.toString(),12*60*60,TimeUnit.SECONDS);
        Cookie cookie = new Cookie(TOKEN, token);
        cookie.setValue(token);
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
    }


    public static String getLoginCookie(HttpServletRequest request){
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



}
