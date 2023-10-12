package com.daofen.crm.controller.login;

import com.daofen.crm.base.ResultVO;
import com.daofen.crm.config.AppContextUtil;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.service.counselor.CounselorService;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.counselor.model.CompanyCounselorPO;
import com.daofen.crm.service.sms.AliyunSms;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * 登录模块
 */
@RestController
public class LoginController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private CounselorService counselorService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    private static final String VERIFICATION = "verification";
    
    public static final String VERIFICATION_PREFIX = "verification_";

//    @RequestMapping("/login/send/sms/common")
//    @ResponseBody
//    public ResultVO sendSmsWithCommon(@PathParam("mobile") String mobile){
//        if(StringUtils.isBlank(mobile) || mobile.length() != 11){
//            return this.failed("请输入正确的手机号码",null);
//        }
//        String code = stringRedisTemplate.opsForValue().get(mobile);
//        if(StringUtils.isBlank(code)){
//            code = LoginUtil.generateMobileCode(mobile);
//            if(new AliyunSms().sendCode(mobile,code,"SMS_182875033")){ // 阿里发送短信验证码待实现
//                stringRedisTemplate.opsForValue().set(mobile,code,10*60, TimeUnit.SECONDS);
//                return this.success("短信验证码已发送，10分钟内有效");
//            }
//            return this.failed("手机验证法发送失败,请联系您的商务顾问",null);
//        }
//        return this.success("手机验证码已发送,10分钟之内有效,无需重新发送");
//    }

    /**
     * 发送短信验证
     * @param mobile 手机号码
     * @return
     */
    @RequestMapping("/login/send/sms")
    @ResponseBody
    public ResultVO sendSms(HttpServletRequest request,
            HttpServletResponse response,@PathParam("mobile") String mobile){
        if(StringUtils.isBlank(mobile) || mobile.length() != 11){
            return this.failed("请输入正确的手机号码",null);
        }
        CompanyCounselorBO counselorBO = counselorService.getCounselorByMobile(mobile);
        if(null == counselorBO){
            return this.failed("该账户不存在，请联系公司管理员",null);
        }
        String code = stringRedisTemplate.opsForValue().get(mobile);
        if(StringUtils.isBlank(code)){
            code = LoginUtil.generateMobileCode(mobile);
            if(verification(request)||counselorBO.getRole().getType()<=1||counselorBO.getRole().getType()==6) {
            	if(new AliyunSms().sendCode(mobile,code)){ // 阿里发送短信验证码待实现
                    stringRedisTemplate.opsForValue().set(mobile,code,10*60, TimeUnit.SECONDS);
                    return this.success("短信验证码已发送，10分钟内有效");
                }
            }else {
            	CompanyCounselorPO selectAdmin = counselorService.selectAdmin(counselorBO.getCompanyId());
            	String substring = mobile.substring(mobile.length()-4, mobile.length());
            	if(new AliyunSms().sendCode(selectAdmin.getMobile(),substring+"_"+code)){ // 阿里发送短信验证码待实现
                    stringRedisTemplate.opsForValue().set(mobile,code,10*60, TimeUnit.SECONDS);
                    return this.success("首次登录，短信验证码已发送贵公司管理员："+selectAdmin.getMobile()+"，10分钟内有效");
                }
            }
            return this.failed("手机验证法发送失败,请联系您的商务顾问",null);
        }
        return this.success("手机验证码已发送,10分钟之内有效,无需重新发送");
    }

    /**
     * 短信验证码验证登录
     * @param mobile 手机号码
     * @param smsCode 短信验证码
     * @return ResultVO
     */
    @RequestMapping("/login/check")
    @ResponseBody
    public ResultVO login(HttpServletRequest request,
                          HttpServletResponse response,
                          @PathParam("mobile") String mobile,
                          @PathParam("smsCode")String smsCode){
        if(StringUtils.isBlank(mobile) || mobile.length() != 11)
            return this.failed("请输入正确的手机号码",null);
        if(StringUtils.isBlank(smsCode))
            return this.failed("请输入正确的手机验证码",null);

        String token = LoginUtil.getLoginCookie(request);
        if(StringUtils.isNotBlank(token) && StringUtils.isNotBlank(stringRedisTemplate.opsForValue().get(token)) ){
            LOG.info("请勿重新登录:{}-{}",mobile,token);
            return this.failed("请勿重新登录",null);
        }
        if(!smsCode.equals(stringRedisTemplate.opsForValue().get(mobile)))
            return this.failed("请输入正确的手机验证码",null);
        // 根据手机号码查询顾问信息，以及权限信息，并缓存到redis中,缓存一天
        CompanyCounselorBO counselorBO = counselorService.getCounselorByMobile(mobile);
        if(null == counselorBO){
            return this.failed("该账户不存在，请联系公司管理员",null);
        }
        // 写入登录状态到cookie中
        LoginUtil.writeCookie(response,counselorBO);
        if(!verification(request)) {
        	writeCookie(request,response,counselorBO);
        }
        return this.success();
    }

    /**
     * 退出登录
     * @param request HttpServletRequest
     * @return ResultVO
     */
    @RequestMapping("/login/out")
    @ResponseBody
    public ResultVO loginOut(HttpServletRequest request){

        String token = LoginUtil.getLoginCookie(request);
        if(StringUtils.isBlank(token))
            return this.success();
        stringRedisTemplate.opsForValue().getOperations().delete(token);
        return this.success();
    }
    
    public static boolean verification(HttpServletRequest request) {
    	String verificationCookie = getVerificationCookie(request);
    	if(StringUtils.isBlank(verificationCookie)) {
    		return false;
    	}
    	String string = AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().get(verificationCookie+request.getHeader("user-agent"));
    	if(StringUtils.isBlank(string)) {
    		return false;
    	}
    	return true;
    }


    public static void writeCookie(HttpServletRequest request,HttpServletResponse response,CompanyCounselorBO counselorBO){
        String token = generateToken(counselorBO);
        LOG.info("用户:[{}],token:[{}]",counselorBO.getMobile(),token);
        AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().set(token+request.getHeader("user-agent"),"11111111111111111111111111");
        Cookie cookie = new Cookie(VERIFICATION, token);
        cookie.setValue(token);
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
    }
    
    /**
     * 生成token
     * @return 返货生成的token字符串
     */
    public static String generateToken(CompanyCounselorBO counselorBO){
        String uuid = UUID.randomUUID().toString().toLowerCase().replaceAll("-","");
        return VERIFICATION_PREFIX+counselorBO.getCompanyId()+"_"+counselorBO.getMobile()+"_"+uuid;
    }
    
    public static String getVerificationCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(null == cookies || cookies.length <=0)
            return null;
        for(Cookie cookie : cookies){
            if(VERIFICATION.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

}
