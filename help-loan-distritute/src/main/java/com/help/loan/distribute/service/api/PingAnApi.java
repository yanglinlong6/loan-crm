package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.LoanDistributeException;
import com.help.loan.distribute.common.ResultCode;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.PingAnTransport;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 平安：apiSender_10021
 */
@Component("apiSender_10021")
public class PingAnApi implements ApiSender{

    private static final Logger LOG = LoggerFactory.getLogger(PingAnApi.class);

    private static final String ClientId = "P_BANG_ZHENG_WEI_YE";

    private static final String ClientPassword = "hA1FP62n";

    private static final String AccessTokenRedisKey = "apiSender_10021_access_token";

    /**获取访问token的url地址*/
    private static String Access_Token_Url = "http://api.pingan.com.cn/oauth/oauth2/access_token";

    private static String Regist_Url = "http://api.pingan.com.cn/open/appsvr/channel/rsploan/unifyForTheCustomerV2.do?access_token=%s&request_id=%s";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            return send2(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【平安好贷-车主贷】分发异常："+e.getMessage()));
            LOG.error("[平安好贷-车主贷]分发异常：手机-{}，异常信息：{}",po.getMobile(),e.getMessage(),e);
            return new SendResult(false,"[平安好贷-车主贷]分发异常："+e.getMessage());
        }
    }

    public SendResult send2(UserAptitudePO po, UserDTO select) throws Exception {

        if(null != select){
            JSONObject user = JSON.parseObject(WechatCenterUtil.getUserInfo(po.getUserId(), "", ""));
            if(select != null){
                if(StringUtils.isBlank(po.getName())
                        && StringUtils.isNotBlank(user.getString("openid"))) {
                    po.setName(EmojiFilter.filterEmoji(user.getString("nickname"),po.getUserId()));
                }
            }
        }
        if(StringUtils.isBlank(po.getName())) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[平安好贷-车主贷]手机:{}，该用户name字段是空的，并且也取不到微信昵称"));
            return new SendResult(false, "[平安好贷-车主贷]手机:{}，该用户name字段是空的，并且也取不到微信昵称");
        }
        Map<String,String> data = new HashMap<>();
        data.put("pageType","3");
        data.put("isDirectForm","N");
        data.put("custSrc","2");
        if(StringUtils.isBlank(po.getCity())){
            String city = MobileLocationUtil.getCity(po.getMobile());
            data.put("city",city);
        }else data.put("city",po.getCity());
        data.put("name",po.getName());
        data.put("teleNum",po.getMobile());
        data.put("applyDate", DateUtil.formatToString(new Date(),DateUtil.yyyyMMddHHmmss2));
        data.put("mediaSourceId","ZTXYD-appqrqt-bzwy-001");
        data.put("campaignCode","BZWY_HUOKE_0001");
        data.put("campaignName","BZWY外部API获客");
        if(JudgeUtil.in(po.getCreditCard(),1,2)) // 信用卡
            data.put("hasCreditCard","Y");
        if(JudgeUtil.in(po.getHouse(),2))// 是否有房贷
            data.put("mortgage","Y");
        if(JudgeUtil.in(po.getCar(),2))// 是否有车贷
            data.put("hasCarLoan","Y");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)) // 收入方式
            data.put("payoffType","0");
        String post = PingAnTransport.encodeForm(data);
        JSONObject paramMap=new JSONObject();
        paramMap.put("cipher",post);
        String registUrl = String.format(Regist_Url,getCacheAccessToken(),System.currentTimeMillis());
        LOG.info("【平安新一贷&车主贷】参数：{}",JSONUtil.toJsonString(data));
        String result = HttpUtil.postForJSON(registUrl,paramMap);
        //{"ret":"0","msg":"","requestId":"1583303120661","data":{"errorCode":"071","resultCode":"02","errorMsg":"拥有信用卡的年限只能是中文字符"}}
        //{"ret":"0","msg":"","requestId":"1583303470320","data":{"idApplyInfoLog":"9F0749C843916A18E0532F1F210ABCE4","dropResult":"Y","isDirectFlg":"N","checkResult":"","resultCode":"0","accelerateResult":"N","isClean":"N"}}
        LOG.info("[平安金服]用户：{}，分发结果：{}",po.getMobile(),result);
        if(!JSONUtil.isJsonString(result))
            return new SendResult(false,"[平安好贷-车主贷]分发结果："+result);
        JSONObject resultJson = JSONUtil.toJSON(result);
        if("0".equals(resultJson.getString("ret")) && "0".equals(resultJson.getJSONObject("data").get("resultCode"))){
            if(null != select){
                String url = "http://htsj.daofen100.com/dialogue/lender/recommend?uuid=" + po.getUserId();
                String recommendResult = HttpUtil.getForObject(url);
                LOG.info("[东方融资网]推网贷：{}",recommendResult);
            }
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【平安好贷-车主贷】发送成功："+result));
            return new SendResult(true,"[平安好贷-车主贷]分发成功:"+result);
        }else if("13012".equals(resultJson.getString("ret"))){
            String secondUrl = String.format(Regist_Url,getAccessToken(),System.currentTimeMillis());
            JSONObject second = JSONUtil.toJSON(HttpUtil.postForJSON(registUrl,paramMap));
            if("0".equals(second.getString("ret")) && "0".equals(second.getJSONObject("data").get("resultCode"))){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【平安好贷-车主贷】发送成功："+result));
                return new SendResult(true,"[平安好贷-车主贷]分发成功:"+result);
            }
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【平安好贷-车主贷】分发失败："+result));
        return new SendResult(false,"[平安好贷-车主贷]分发结果："+result);
    }


    private String getCacheAccessToken(){
        String accessToken = stringRedisTemplate.opsForValue().get(AccessTokenRedisKey);
        if(StringUtils.isBlank(accessToken)){
            return getAccessToken();
        }else
            return accessToken;
    }

    private synchronized String getAccessToken(){
        StringBuffer url = new StringBuffer();
        url.append(Access_Token_Url);
        url.append("?client_id=" + ClientId);
        url.append("&grant_type=client_credentials");
        url.append("&client_secret=" + ClientPassword);
        String response = HttpUtil.getForObject(url.toString());
        LOG.info("[平安金服]获取AccessToken结果：{}",response);
        if(JSONUtil.isJsonString(response)){
            JSONObject resJson = JSONUtil.toJSON(response);
            String accessToken = resJson.getJSONObject("data").getString("access_token");
            stringRedisTemplate.opsForValue().set(AccessTokenRedisKey,accessToken,29, TimeUnit.DAYS);
            return accessToken;
        }
        throw new LoanDistributeException(ResultCode.FAILED,"[平安好贷-车主贷]获取AccessToken异常");
    }




    public static void main(String[] args) throws NoSuchPaddingException, UnsupportedEncodingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

//        StringBuffer url = new StringBuffer();
//        url.append(ACCESS_TOKEN_URL);
//        url.append("?client_id=" + ClientId);
//        url.append("&grant_type=client_credentials");
//        url.append("&client_secret=" + ClientPassword);
//        String response = HttpUtil.getForObject(url.toString());
//        JSONObject resJson = JSONUtil.toJSON(response);
//        String accessToken = resJson.getJSONObject("data").getString("access_token");
//        System.out.println(accessToken);

//        String ClientId = "P_DOLPHIN100";
//        String ClientPassword = "ap8w7f1W";
//        String AccessTokenRedisKey = "apiSender_10021_access_token";
//        String registUrl = String.format(Regist_Url,"5A00AF4229E8427B992988DFEB1C0970",System.currentTimeMillis());
//        Map<String,String> paramap = new HashMap<>();
//        paramap.put("pageType","3");
//        paramap.put("isDirectForm","N");
//        paramap.put("custSrc","2");
//        paramap.put("city","深圳市");
//        paramap.put("name","孔先森测试三");
//        paramap.put("teleNum","13632966663");
//        paramap.put("applyDate", DateUtil.formatToString(new Date(),DateUtil.yyyyMMddHHmmss2));
//        paramap.put("mediaSourceId","ZTXYD-appqrqt-bzwy-001");
//        paramap.put("campaignCode","BZWY_HUOKE_0001");
//        paramap.put("campaignName","BZWY外部API获客");
//        // 信用卡
//        paramap.put("hasCreditCard","Y");
//        // 是否有房贷
//        paramap.put("mortgage","Y");
//        // 是否有车贷
//        paramap.put("hasCarLoan","Y");
//        // 收入方式
//        paramap.put("payoffType","0");
//
//        String post = PingAnTransport.encodeForm(paramap);
//        JSONObject paraMap=new JSONObject();
//        paraMap.put("cipher",post);
//
//        String result = HttpUtil.postForObject(registUrl,paraMap);
//        System.out.println(result);

    }


}
