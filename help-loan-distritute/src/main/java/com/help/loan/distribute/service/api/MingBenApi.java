package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.LoanDistributeException;
import com.help.loan.distribute.common.ResultCode;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.config.AppContextUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 铭奔：10073
 */
@Component("apiSender_10073")
public class MingBenApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(MingBenApi.class);

    private static final String CORPID = "bcb0da1b7e81d73d3d4288690b7e96c2";

    private static final String APP_KEY = "ae825caafd21ac69d52f0ce264c81228";

    private static final String APP_SECRET = "b44ab81be965708310e517f01084d16a";

    private static final String USER_ID = "4977674740847308933";

    private static final String CODE_URL = "https://api.eteams.cn/oauth2/authorize?corpid=%s&response_type=code&state=a";

    private static final String ACCESS_TOKEN_URL = "https://api.eteams.cn/oauth2/access_token";

    private static final String CREATE_URL = "https://api.eteams.cn/crm/v2/create";


    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            log.error("【铭奔】分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【铭奔】分发异常:"+e.getMessage()));
            return new SendResult(false,"【铭奔】分发异常："+e.getMessage());
        }
    }


    public SendResult send2(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);

        Map<String,Object> data = new HashMap<>();
        data.put("access_token",getAccessToken(ACCESS_TOKEN_URL,APP_KEY,APP_SECRET));
        data.put("userid",USER_ID);
        data.put("module","customer");

        JSONObject entity = new JSONObject();
        entity.put("name",po.getName());

        JSONObject manager = new JSONObject();
        manager.put("name","陈恩乐");
        entity.put("manager",manager);

        JSONObject customerStatus = new JSONObject();
        customerStatus.put("name","0星");
        entity.put("customerStatus",customerStatus);

        JSONObject customerType = new JSONObject();
        customerType.put("name","道分渠道客户");
        entity.put("customerType",customerType);

        JSONObject customerIndustry = new JSONObject();
        customerIndustry.put("name","销售");
        entity.put("customerIndustry",customerIndustry);

        JSONObject customerRegion = new JSONObject();
        String city = po.getCity();
        customerRegion.put("name",city.endsWith("市")?city.substring(0,city.length()-1):city);
        entity.put("customerRegion",customerRegion);

        entity.put("customerFax","");
        entity.put("customerEmail","");
        entity.put("customerZipCode","");
        entity.put("customerTelephone",po.getMobile());
        entity.put("customerWebsite","");
        entity.put("customerAddress",po.getCity());
        entity.put("customerDescription",getDesc(po));

        data.put("entity",entity);

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        String response = HttpClientProxy.doPost(CREATE_URL, data, "UTF-8", 3000, httpHeader);
        log.info("【铭奔】创建客户结果：{}",response);
        JSONObject message = JSONUtil.toJSON(response).getJSONObject("message");
        String errcode = message.getString("errcode");
        if("0".equals(errcode)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【铭奔】分发成功:"+message.toJSONString()));
            return new SendResult(true,"【铭奔】分发成功："+message.toJSONString());
        }
        if(JudgeUtil.in(errcode,"200007","200008","300001","300002")){
            data.put("access_token",getAccessToken(ACCESS_TOKEN_URL,APP_KEY,APP_SECRET));
            response = HttpClientProxy.doPost(CREATE_URL, data, "UTF-8", 3000, httpHeader);
            log.info("【铭奔】第二次创建客户结果：{}",response);
            message = JSONUtil.toJSON(response).getJSONObject("message");
            errcode = message.getString("errcode");
            if("0".equals(errcode)){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【铭奔】分发成功:"+message.toJSONString()));
                return new SendResult(true,"【铭奔】分发成功："+response);
            }
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【铭奔】分发失败:"+message.toJSONString()));
        return new SendResult(true,"【铭奔】分发失败："+response);
    }

    private String getDesc(UserAptitudePO po){
        StringBuffer content = new StringBuffer();
        if(po.getPublicFund().contains("有，"))
            content.append(po.getPublicFund());
        if(JudgeUtil.in(po.getHouse(),1,2))
            content.append("，").append("有本地商品房");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2))
            content.append("，").append("有银行代发工资");
        if(JudgeUtil.in(po.getCar(),1,2))
            content.append("，").append("有车");
        if(JudgeUtil.in(po.getInsurance(),1,2))
            content.append("，").append("有商业保单");
        if(1 == po.getCompany())
            content.append("，").append("有公司营业执照");
        return content.toString();
    }

    private synchronized String getAccessToken(String url,String appKey,String appSecret){

        StringRedisTemplate stringRedisTemplate = AppContextUtil.get().getBean(StringRedisTemplate.class);
        String accessToken = stringRedisTemplate.opsForValue().get("distribute__token_mingben");
        if(StringUtils.isNotBlank(accessToken))
            return accessToken;
        //第一步：获取code
        String response = HttpUtil.getForObject(String.format(CODE_URL,CORPID));
        log.info("【铭奔】获取code结果：{}",response);
        JSONObject json = JSONUtil.toJSON(response);
        if(!"0".equals(json.getString("errcode"))){
            throw  new LoanDistributeException(ResultCode.FAILED,"【铭奔】获取code结果:"+response);
        }
        String code = json.getString("code");


        Map<String,Object> data = new HashMap<>();
        data.put("app_key",appKey);
        data.put("app_secret",appSecret);
        data.put("grant_type", "authorization_code");
        data.put("code", code);

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        response = HttpClientProxy.doPost(url, data, "UTF-8", 3000, httpHeader);
        log.info("【铭奔】获取AccessToken结果：{}",response);
        json = JSONUtil.toJSON(response);
        if(!"0".equals(json.getString("errcode"))){
            throw  new LoanDistributeException(ResultCode.FAILED,"【铭奔】获取AccessToken失败："+response);
        }
        accessToken = json.getString("accessToken");
        stringRedisTemplate.opsForValue().set("distribute__token_mingben",accessToken,7200, TimeUnit.MICROSECONDS);
        return accessToken;
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("赵先生");
//        po.setMobile("18605257123");
//        po.setCity("上海市");
//        po.setLoanAmount("《10-30万》");
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setCar(0);
//        po.setHouse(2);
//        po.setInsurance(2);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setUpdateDate(new Date());
//        MingBenApi api = new MingBenApi();
//        System.out.println(api.send(po,null));
//    }

//    public static void main(String[] args){
//
//        String accountId = "T00000017865";
//        String date  = DateUtil.formatToString(new Date(),"yyyyMMddHHmmss");
//        String auth = Base64Util.encode(accountId+":"+ date);
//        System.out.println(auth);
//        System.out.println(MD5Util.getMd5String(accountId+"929925e0-e5ab-11ea-a2a4-c5f98b93c7e6"+date).toUpperCase());
//    }

}
