package com.loan.cps.controller;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.*;
import com.loan.cps.dao.UserAptitudeDao;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.service.cache.CacheService;
import com.loan.cps.service.city.CityService;
import com.loan.cps.service.city.model.CityBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 抵押贷款控制器
 */
@RestController
public class MortgageController {


    private static final Logger log = LoggerFactory.getLogger(MortgageController.class);

    @Autowired
    private CityService cityService;

    @Autowired
    CacheService cacheService;

    @Autowired
    private UserAptitudeDao userAptitudeDao;

    @Autowired
    private RedisTemplate<String, Object> template;

    @RequestMapping("/city")
    @ResponseBody
    public R getCity(@RequestParam(value = "level", required = false, defaultValue = "2") Byte level){
        R r = R.ok();
        CityBO cityBO = cityService.getCityForTree(level);
        r.setData(cityBO.getChildList());
        String value = cacheService.getValue(CPSConstant.Redis.Advertising.FIELD,CPSConstant.Redis.Advertising.CITY_MORTGAGE);
        r.setMsg(value);
        return r;
    }

//    @RequestMapping(value = "/mortgage/code/send")
//    public R send(@RequestParam String mobile) {
//        if(!CustController.checkMobile(mobile)) {
//            return R.fail("1","手机号格式错误");
//        }
//
//        String redisCode = (String)template.opsForValue().get(CustController.MOBILE_CODE_REDIS_KEY_PREFIX+mobile);
//        if(redisCode!=null) {
//            return R.fail("1","验证码已发送,1小时内请勿重复提交");
//        }
//        String key = CustController.MOBILE_CODE_COUNT_REDIS_KEY_PREFIX+mobile;
//        Integer count = (Integer)template.opsForValue().get(key);
//        if(count==null) {
//            count = 0;
//        }
//        if(count>=3) {
//            return R.fail("1","验证码发送频繁，请一小时后使用");
//        }
//        count = count+1;
//
//        template.opsForValue().set(key, count, 1, TimeUnit.HOURS);
//        String randomCode = CustController.getRandomCode();
//        AliyunSms sms = new AliyunSms();
//        if(sms.sendCode(mobile, randomCode)) {
//            template.opsForValue().set(CustController.MOBILE_CODE_REDIS_KEY_PREFIX+mobile, randomCode, 1, TimeUnit.HOURS);
//        }else {
//            return R.fail();
//        }
//        return R.ok("发送成功");
//    }

//    @RequestMapping(value = "/mortgage/code/check")
//    public R send(@RequestParam String mobile,@RequestParam String code,@RequestParam String channel) {
//        if(!CustController.checkMobile(mobile)) {
//            return R.fail("1","手机号格式错误");
//        }
//        if(StringUtils.isBlank(code))
//            return R.fail("1","请输入短信验证码");
//        String redisCode = (String)template.opsForValue().get(CustController.MOBILE_CODE_REDIS_KEY_PREFIX+mobile);
//        if(redisCode == null ) {
//            return R.fail("1","验证码已过期，请重新发送");
//        }
//        if(code.equals(redisCode)) {
//            // 预先保存手机号码
//            return R.ok();
//        }
//        return R.fail("1","验证码错误");
//    }


    @PostMapping("/apply/mortgage/car")
    @ResponseBody
    public R applyCar(HttpServletRequest req, HttpServletResponse res,@RequestBody() UserAptitudePO po,@RequestParam String code){
        R r = R.ok();

        String redisCode = (String)template.opsForValue().get(CustController.MOBILE_CODE_REDIS_KEY_PREFIX+po.getMobile());
        if (!JudgeUtil.in(code,"010101",redisCode)){
            return R.fail("1","验证码错误");
        }
        if(StringUtils.isBlank(po.getChannel()))
            return R.fail("1","缺少渠道");
        if(StringUtils.isBlank(po.getName()))
            return R.fail("1","请输入姓名");
        if(StringUtils.isBlank(po.getMobile()))
            return R.fail("1","请输入手机号码");
        if(StringUtils.isBlank(po.getCity()))
            return R.fail("1","请选择城市");
        if(StringUtils.isBlank(po.getLoanAmount()) || Double.valueOf(po.getLoanAmount()) >=1000d || Double.valueOf(po.getLoanAmount()) <= 0d)
            return R.fail("1","请输入需求金额(单位:万元)");
        if(!JudgeUtil.in(po.getCar(),1,2,5))
            return R.fail("1","请选择车辆类型");
        if(null == po.getCarPrice() || po.getCarPrice()<=0 || po.getCarPrice() >= 10000d)
            return R.fail("1","请输入正确裸车价,单位:万元");

        po.setMd5(MD5Util.getMd5String(po.getMobile()));
        UserAptitudePO byMD5 = userAptitudeDao.getByMobileMD5(po.getMd5());
        if(null != byMD5){
            return R.fail("1","您的申请已提交,请勿重复申请",false);
        }
        AptitudeUtils.setIpCity(po,req);
        JSONObject andCheck = MobileLocationUtil.getAndCheck(po.getMobile());
        if(andCheck!=null)
            po.setMobileLocation(andCheck.getString("City"));
        po.setUserId(UUID.randomUUID().toString().replace("-", ""));
        po.setCreateBy(CPSConstant.CreateBy.CAR);
        po.setType(CPSConstant.ProductType.Car);
        po.setLevel(4);
        userAptitudeDao.add(po);
        return r;

    }


    @PostMapping("/apply/mortgage/house")
    @ResponseBody
    public R applyHouse(HttpServletRequest req, HttpServletResponse res,@RequestBody() UserAptitudePO po,@RequestParam String code){
        R r = R.ok();
        String redisCode = (String)template.opsForValue().get(CustController.MOBILE_CODE_REDIS_KEY_PREFIX+po.getMobile());
        if(redisCode == null )
            return R.fail("1","验证码已过期，请重新发送");

        if(!code.equals(redisCode))
            return R.fail("1","验证码错误");
        if(StringUtils.isBlank(po.getChannel()))
            return R.fail("1","缺少渠道");
        if(StringUtils.isBlank(po.getName()))
            return R.fail("1","请输入姓名");
        if(StringUtils.isBlank(po.getMobile()))
            return R.fail("1","请输入手机号码");
        if(StringUtils.isBlank(po.getCity()))
            return R.fail("1","请输入城市");
        if(StringUtils.isBlank(po.getLoanAmount()) || Double.valueOf(po.getLoanAmount()) >=1000d || Double.valueOf(po.getLoanAmount()) <= 0)
            return R.fail("1","请输入需求金额(单位:万元)");
        if(!JudgeUtil.in(po.getHouse(),1,2))
            return R.fail("1","请选择房产情况");
        if(!JudgeUtil.in(po.getHouseExtension(),5,6,7,8))
            return R.fail("1","请选择房屋类型");
        if(null == po.getCarPrice() || po.getCarPrice()<=0 || po.getCarPrice() >= 10000d)
            return R.fail("1","请输入房产价格,单位:万元");
        po.setMd5(MD5Util.getMd5String(po.getMobile()));
        UserAptitudePO byMD5 = userAptitudeDao.getByMobileMD5(po.getMd5());
        if(null != byMD5){
            r.setMsg("您的申请已提交,请勿重复申请");
            return r;
        }
        AptitudeUtils.setIpCity(po,req);
        JSONObject andCheck = MobileLocationUtil.getAndCheck(po.getMobile());
        if(andCheck!=null)
            po.setMobileLocation(andCheck.getString("City"));
        po.setUserId(UUID.randomUUID().toString().replace("-", ""));
        po.setCreateBy(CPSConstant.CreateBy.HOUSE);
        po.setLevel(4);
        po.setType(CPSConstant.ProductType.House);
        userAptitudeDao.add(po);
        return r;
    }


    private ReentrantLock lock = new ReentrantLock();

    @PostMapping("/apply/mortgage/fund")
    @ResponseBody
    public R applyFund(HttpServletRequest req, HttpServletResponse res,@RequestBody() UserAptitudePO po,@RequestParam String code){
        if(AptitudeUtils.checkAlreadyApply(req)) {
            return R.fail("1","您的申请已提交，请勿重复提交");
        }
        R r = R.ok();
        String redisCode = (String)template.opsForValue().get(CustController.MOBILE_CODE_REDIS_KEY_PREFIX+po.getMobile());
        if(!code.equals(redisCode)){
            return R.fail("1","验证码错误");
        }
        if(StringUtils.isBlank(po.getChannel()))
            return R.fail("1","缺少渠道");
        if(StringUtils.isBlank(po.getName()))
            return R.fail("1","请输入姓名");
        if(StringUtils.isBlank(po.getMobile()))
            return R.fail("1","请输入手机号码");
        if(StringUtils.isBlank(po.getCity()))
            return R.fail("1","请输入城市");
        if(StringUtils.isBlank(po.getLoanAmount()) || Double.valueOf(po.getLoanAmount()) <= 0)
            return R.fail("1","请输入需求金额");
        if(StringUtils.isBlank(po.getPublicFund()))
            return R.fail("1","输入公积金月缴金额");
        if(!JudgeUtil.in(po.getAptitude(),"1至6月","6个月至2年","2年以上"))
            return R.fail("1","请选择正确的公积金缴交时长");

        String md5 = MD5Util.getMd5String(po.getMobile());

        po.setPublicFund("有，"+po.getPublicFund()+";"+po.getAptitude());
        po.setCar(0);
        po.setCompany(0);
        po.setInsurance(0);
        po.setGetwayIncome(0);
        po.setHouse(0);
        po.setMd5(md5);

        synchronized (this){
            UserAptitudePO byMD5 = userAptitudeDao.getByMobileMD5(md5);
            if(null != byMD5){
                return R.fail(R.FAIL,"您的申请已提交,请勿重复申请",false);
            }
            AptitudeUtils.setIpCity(po,req);
            JSONObject andCheck = MobileLocationUtil.getAndCheck(po.getMobile());
            if(andCheck!=null)
                po.setMobileLocation(andCheck.getString("City"));
            po.setUserId(UUID.randomUUID().toString().replace("-", ""));
            po.setCreateBy(CPSConstant.CreateBy.Fund);
            po.setLevel(4);
            po.setType(CPSConstant.ProductType.Fund);
            userAptitudeDao.add(po);
            if(R.SUC.equals(r.getCode())) {
                AptitudeUtils.setAlreadyApplyCookie(res);
            }
            return r;
        }
    }


}
