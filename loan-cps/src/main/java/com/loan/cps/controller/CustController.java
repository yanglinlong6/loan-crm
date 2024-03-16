package com.loan.cps.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.*;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.service.UserAptitudeService;
import com.loan.cps.service.cache.CacheService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


@RestController
public class CustController {

    private static final Logger LOG = LoggerFactory.getLogger(CustController.class);

    @Autowired
    UserAptitudeService aptService;

    @Autowired
    RedisTemplate<String, Object> template;

    public static final String MOBILE_CODE_REDIS_KEY_PREFIX = "mobile_";

    public static final String MOBILE_CODE_COUNT_REDIS_KEY_PREFIX = "mobile_count_";


    private static final Log logger = LogFactory.getLog(CustController.class);

    public static Map<String, String> FIELD_MAPPING = new HashMap<String, String>();

    static {
        FIELD_MAPPING.put("姓名", "name");
        FIELD_MAPPING.put("手机号", "mobile");
        FIELD_MAPPING.put("城市", "city");
        FIELD_MAPPING.put("借款金额", "loanAmount");
        FIELD_MAPPING.put("有营业执照", "company");
        FIELD_MAPPING.put("有公积金", "publicFund");
        FIELD_MAPPING.put("有车", "car");
        FIELD_MAPPING.put("有商品房", "house");
        FIELD_MAPPING.put("有保单", "insurance");
        FIELD_MAPPING.put("有打卡工资", "getwayIncome");
        FIELD_MAPPING.put("年龄", "age");
        FIELD_MAPPING.put("性别", "gender");
    }

    @Autowired
    CacheService cacheService;

    @RequestMapping(value = "/customer/city")
    @ResponseBody
    public R getCity() {
        String value = cacheService.getValue(CPSConstant.Redis.Advertising.FIELD, CPSConstant.Redis.Advertising.CITY);
        R ok = R.ok();
        if (StringUtils.isBlank(value)) return ok;
        ok.setData(value.split(","));
        return ok;
    }

    @RequestMapping(value = "/customer/one/city")
    @ResponseBody
    public R getOneCity(@RequestParam("city") String city) {
        if (StringUtils.isBlank(city)) return R.fail("city名不能为空");
        // 通过英文转中文
        R ok = R.ok();
        String cnCity = cacheService.getValueByEnCity(city);
        if (StringUtils.isBlank(cnCity)) return ok;
        ok.setData(cnCity);
        return ok;
    }

    /**
     * 投放城市,手机归属地城市,ip所在地城市  返回三个城市
     *
     * @param city
     * @return
     */
    @RequestMapping({"/customer/three/city"})
    @ResponseBody
    public R getThreeCity(@RequestParam("city") String city, @RequestParam("mobile") String mobile, HttpServletRequest req) {
        List<String> cityList = new ArrayList<>();
        String value = null;
        if (StringUtils.isNotBlank(city)) {
            value = this.cacheService.getValue(CPSConstant.Redis.Advertising.ONE_CITY, city);
            LOG.info("获取城市-{}-{}", city, value);
            cityList.add(value);
        }
        // ip所在城市
        String ipCity = AptitudeUtils.setIpCity(null, req);
        if (StringUtils.isNotBlank(ipCity)) {
            ipCity = ipCity.endsWith("市") ? ipCity : ipCity + "市";
            if (!cityList.contains(ipCity)) cityList.add(ipCity);
        }
        // 如果投放城市和ip所在城市相同,则直接返回,表示不是异地
        if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(ipCity) && value.equals(ipCity)) {
            return R.ok("suc", cityList.toArray(new String[cityList.size()]));
        }
        //手机归属城市
        String mobileCity = AptitudeUtils.getMobileCity(mobile);
        if (StringUtils.isNotBlank(mobileCity)) {
            mobileCity = mobileCity.endsWith("市") ? mobileCity : mobileCity + "市";
            if (!cityList.contains(mobileCity)) cityList.add(mobileCity);
        }
        return R.ok("suc", cityList.toArray(new String[cityList.size()]));
    }

    /**
     * 热门城市
     *
     * @param cityCode 热门城市编码
     * @param req      HttpServletRequest
     * @return R
     */
    @RequestMapping({"/customer/hot/city"})
    @ResponseBody
    public R getHotCity(@RequestParam("cityCode") String cityCode, HttpServletRequest req) {
        List<String> hotCity = new ArrayList<>();
        if (StringUtils.isBlank(cityCode)) {
            return R.fail("缺少city参数");
        }
        String ipCity = AptitudeUtils.setIpCity(null, req);
        LOG.info("获取[ip]城市-{}-{}", cityCode, ipCity);
        if (StringUtils.isNotBlank(ipCity)) {
            ipCity = ipCity.endsWith("市") ? ipCity : ipCity + "市";
            hotCity.add(ipCity);
        }

        String adCity = this.cacheService.getValue(CPSConstant.Redis.Advertising.FIELD, cityCode);
        LOG.info("获取热门城市-key:{}-value:{}", cityCode, adCity);
        if (StringUtils.isBlank(adCity)) {
            return R.fail("热门城市:" + cityCode + "没有配置");
        }
        String[] array = adCity.split(",");
        for (String city : array) {
            if (StringUtils.isBlank(city) || !city.endsWith("市")) continue;
            if (hotCity.contains(city)) continue;
            hotCity.add(city);
        }
        return R.ok("suc", hotCity.toArray(new String[hotCity.size()]));
    }


    @RequestMapping(value = "/customer/apply")
    public R apply(@RequestBody UserAptitudePO po, @RequestParam String code, HttpServletRequest req, HttpServletResponse res) {
        logger.info("user add = " + JSON.toJSONString(po));
        R ok = R.ok();
        if (AptitudeUtils.checkAlreadyApply(req)) {
            return R.fail("1", "您的申请已提交，请勿重复提交");
        }
        try {
            String redisCode = (String) template.opsForValue().get(MOBILE_CODE_REDIS_KEY_PREFIX + po.getMobile());
            if (redisCode == null) {
                return R.fail("1", "验证码已过期，请重新发送");
            }
            if (!code.equals(redisCode)) {
                return R.fail("1", "验证码错误");
            }
            if (StringUtils.isBlank(po.getMobile())) {
                return R.fail("1", "请输入真实手机号码");
            }
            AptitudeUtils.setIpCity(po, req);
            AptitudeUtils.setMobileCity(po);
            po.setType(CPSConstant.ProductType.Credit);
            String mobileMD5 = MD5Util.getMd5String(po.getMobile()).intern();
            synchronized (mobileMD5) {
                po.setMd5(mobileMD5);
                ok = aptService.add(po);
            }

        } catch (Exception e) {
            ok.setCode("1");
            ok.setMsg(e.getMessage());
        }
        if ("0".equals(ok.getCode()) || "2".equals(ok.getCode())) {
            AptitudeUtils.setAlreadyApplyCookie(res);
        }
        return ok;
    }


    public static final String[] channels = {"tt001", "bd001"};

    public static final String citys = "'上海市','杭州市','南京市','北京市','武汉市','苏州市','南通市','深圳市','长沙市','天津市','徐州市','重庆市','成都市'";

    @RequestMapping(value = "/customer/tt/apply")
    public R ttApply(@RequestBody UserAptitudePO po) {
        logger.info("user add = " + JSON.toJSONString(po));
        R ok = R.ok();
        try {
            if (!JudgeUtil.in(po.getChannel(), channels)) {
                ok.setCode("1");
                ok.setMsg("非法请求");
                return ok;
            }
            int amount = Integer.valueOf(po.getLoanAmount());
            if (amount < 50000 || amount > 10000000) {
                ok.setCode("1");
                ok.setMsg("非法请求:贷款金额小于50000 或者 大于10000000");
                return ok;
            }
            if (null == po.getAge() || po.getAge() < 25) {
                ok.setCode("1");
                ok.setMsg("非法请求:年龄错误");
                return ok;
            }
            if (!citys.contains(po.getCity())) {
                ok.setCode("1");
                ok.setMsg("非法请求:该城市未开放");
                return ok;
            }
            if (!JudgeUtil.in(Integer.valueOf(po.getPublicFund()), 1, 2)) {
                ok.setCode("1");
                ok.setMsg("非法请求:公积金错误");
                return ok;
            }
            if (!JudgeUtil.in(po.getCar(), 1, 2)) {
                ok.setCode("1");
                ok.setMsg("非法请求:车产错误");
                return ok;
            }
            if (!JudgeUtil.in(po.getHouse(), 1, 2)) {
                ok.setCode("1");
                ok.setMsg("非法请求:房产错误");
                return ok;
            }
            if (!JudgeUtil.in(po.getInsurance(), 1, 2)) {
                ok.setCode("1");
                ok.setMsg("非法请求:商业保险错误");
                return ok;
            }
            if (!JudgeUtil.in(po.getGetwayIncome(), 1, 2)) {
                ok.setCode("1");
                ok.setMsg("非法请求:银行代发错误");
                return ok;
            }
            if (!JudgeUtil.in(po.getCompany(), 1, 2)) {
                ok.setCode("1");
                ok.setMsg("非法请求:营业执照错误");
                return ok;
            }
            JSONObject andCheck = MobileLocationUtil.getAndCheck(po.getMobile());
            if (andCheck != null) {
                po.setMobileLocation(andCheck.getString("City"));
            }
            ok = aptService.add(po);
        } catch (Exception e) {
            ok.setCode("1");
            ok.setMsg(e.getMessage());
        }

        return ok;
    }

    @RequestMapping(value = "/tencent/customer/apply")
    public R tencentApply(@RequestBody JSONObject obj1) {
        JSONObject obj = obj1.getJSONObject("data");
        logger.info("tencent user add = " + obj.toJSONString());
        String string = obj.getString("account_id");
        if (StringUtils.isEmpty(string) || !Pattern.matches("\\d+", string)) {
            return R.fail();
        }
        String string2 = obj.getString("url");
        if (StringUtils.isEmpty(string2)) {
            return R.fail();
        }
        String[] split2 = string2.split("\\?");
        String[] split = split2[0].split("/");
        UserAptitudePO packing = packing(obj);
        try {
            packing.setChannel(split[split.length - 1]);
            if (packing.getAge() == null || packing.getAge() < 22 || packing.getAge() > 55) {
                packing.setAge(33);
            }
            JSONObject andCheck = MobileLocationUtil.getAndCheck(packing.getMobile());
            if (andCheck != null) {
                packing.setMobileLocation(andCheck.getString("City"));
            }
            aptService.add(packing);
        } catch (Exception e) {
            logger.info("tencent user add err ");
            e.printStackTrace();
        }
        return R.fail("200", "suc");
    }

    private static UserAptitudePO packing(JSONObject obj) {
        JSONArray jsonArray = obj.getJSONArray("data");
        JSONObject o = new JSONObject();
        o.put("company", 2);
        o.put("publicFund", 2);
        o.put("car", 2);
        o.put("house", 2);
        o.put("insurance", 2);
        o.put("getwayIncome", 2);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String string = jsonObject.getString("label");
            if (string.contains("完善信息")) {
                String qua = jsonObject.getString("value");
                if (!StringUtils.isEmpty(qua)) {
                    String[] split2 = qua.split("\\|");
                    for (String q : split2) {
                        o.put(FIELD_MAPPING.get(q), 1);
                    }
                }
            } else if (string.contains("性别")) {
                int gender = 0;
                if (jsonObject.getString("value").contains("男")) {
                    gender = 1;
                } else {
                    gender = 2;
                }
                o.put(FIELD_MAPPING.get(string), gender);
            } else {
                o.put(FIELD_MAPPING.get(string), jsonObject.getString("value"));
            }
        }
        return JSON.toJavaObject(o, UserAptitudePO.class);
    }

    @RequestMapping(value = "/customer/code/send")
    public R send(@RequestParam String mobile, HttpServletRequest request) {
        String domain2 = request.getServerName();
        logger.info("请求域名:" + domain2);
        if (!checkMobile(mobile)) {
            return R.fail("1", "手机号格式错误");
        }
        String redisCode = (String) template.opsForValue().get(MOBILE_CODE_REDIS_KEY_PREFIX + mobile);
        if (redisCode != null) {
            return R.fail("1", "请使用上次发送的验证,1小时内有效");
        }
        Integer count = (Integer) template.opsForValue().get(MOBILE_CODE_COUNT_REDIS_KEY_PREFIX + mobile);
        if (count == null) {
            count = 0;
        }
        if (count >= 3) {
            return R.fail("1", "请使用上次发送的验证,1小时内有效");
        }
        count = count + 1;
        template.opsForValue().set(MOBILE_CODE_COUNT_REDIS_KEY_PREFIX + mobile, count, 1, TimeUnit.HOURS);
        String randomCode = getRandomCode();
//        SmsApi api = new YunSMS();
//		SmsApi api = new WDSms();
        AliyunSms api = new AliyunSms();
        if (api.sendCode(mobile, randomCode)) {
            template.opsForValue().set(MOBILE_CODE_REDIS_KEY_PREFIX + mobile, randomCode, 1, TimeUnit.HOURS);
        } else {
            return R.fail();
        }
        return R.ok("短信发送成功,发送由延迟,请注意查收");
    }

    @RequestMapping(value = "/customer/code/check")
    public R send(@RequestParam String mobile, @RequestParam String code) {
        if (!checkMobile(mobile)) {
            return R.fail("1", "手机号格式错误");
        }
        String redisCode = (String) template.opsForValue().get(MOBILE_CODE_REDIS_KEY_PREFIX + mobile);
        if (redisCode == null) {
            return R.fail("1", "验证码已过期，请重新发送");
        }
        if (code.equals(redisCode)) {
            return R.ok();
        }
        return R.fail("1", "验证码错误");
    }

    @RequestMapping(value = "/mobile/city/get")
    public JSONObject getMobileCity(@RequestParam String mobile) {
        return MobileLocationUtil.getAndCheck(mobile);
    }

    @RequestMapping(value = "/mobile/check")
    public JSONObject getMobileCheck(@RequestParam String mobile) {
        return MobileLocationUtil.getAndCheck(mobile);
    }

    public static boolean checkMobile(String mobile) {
        String regex = "^((13[0-9])|(14[0-9])|(16[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";
        boolean m = false;
        if (mobile.length() == 11) {
            m = Pattern.matches(regex, mobile);
        }
        return m;
    }

    public static String getRandomCode() {
        Random a = new Random();
        Integer nextInt = a.nextInt(10000);
        String string = nextInt.toString();
        if (string.length() < 4) {
            int length = string.length();
            for (int i = 0; i < 4 - length; i++) {
                string = "0" + string;
            }
        }
        return string;
    }


//	public static final String URL = "http://ip.ws.126.net/ipquery?ip=";

//	public static JSONObject ipCity(HttpServletRequest request) {
////		String forObject = HttpUtil.getForObject("http://whois.pconline.com.cn/ipJson.jsp?ip="+getIpAddress(request)+"&json=true");
//		try {
//			String forObject = HttpUtil.getForObject(URL+AptitudeUtils.getIpAddress(request));
//			JSONObject cc = new JSONObject();
//			String city = getCity(forObject);
//			if(StringUtils.isEmpty(city)) {
//				String forObject2 = HttpUtil.getForObject("http://whois.pconline.com.cn/ipJson.jsp?ip="+AptitudeUtils.getIpAddress(request)+"&json=true");
//				JSONObject parseObject = JSON.parseObject(forObject2);
//				city = parseObject.getString("city");
//			}
//			cc.put("city", city);
//			return cc;
//		} catch (Exception e) {
//			logger.info("查询ip归属地异常："+e.getMessage(),e);
//		}
//		return new JSONObject();
//	}

//	private static String getIpAddress(HttpServletRequest request) {
//        String ip= request.getHeader("x-forwarded-for");
//        if(ip !=null) {
//        	 String[] split = ip.split(",");
//             ip = split[0];
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("Proxy-Client-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_CLIENT_IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//            if("127.0.0.1".equals(ip)||"0:0:0:0:0:0:0:1".equals(ip)){
//                //根据网卡取本机配置的IP
//                 InetAddress inet=null;
//                 try {
//                     inet = InetAddress.getLocalHost();
//                 } catch (UnknownHostException e) {
//                     e.printStackTrace();
//                 }
//                     ip= inet.getHostAddress();
//            }
//        }
//        return ip;
//    }

    private static String getCity(String result) {
        try {
            String[] split = result.split("=");
            for (String s : split) {
                boolean contains = s.contains("市");
                if (contains) {
                    String replaceAll = s.replaceAll("[^\u4E00-\u9FA5]", "");
                    String substring = replaceAll.substring(0, replaceAll.indexOf("市") + 1);
                    return substring;
                }
            }
        } catch (Exception e) {

        }
        return "";
    }
}
