package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.REBIND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 甲方：北京企创未来金融服务外包有限公司
 * 通讯地址：朝阳区华普国际大厦1109
 * 业务联系人：禹松
 * 联系电话：13811537599
 * 电子邮箱：332958540@qq.com
 *
 *
 */
@Component("apiSender_20061")
public class QiChuangApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(QiChuangApi.class);

    private static final String clientId = "cf5qiROhWznTb";

    private static final String secret = "3OAv1WM9B28M04sO7kxL0Jr04A1tt3n4";

    private static final String token = "51wwXh7yc8ox5498yHtel69aylKOk8fBMF786cZ4x4S";

    private static final String tokenUrl = "https://qw-openapi-tx.dustess.com/auth/v1/access_token/token";

    private static final String sendUrl = "https://qw-openapi-tx.dustess.com/customer/v1/batchAddCustomer?accessToken=%s";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {

        try{
            return sendResult(po,select);
        }catch (Exception e){
            LOG.info("【北京企创】异常：{}-{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京企创】分发未知异常："+e.getMessage()));
            return new SendResult(false,"【北京企创】异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        JSONObject data = new JSONObject();
        data.put("clientId",clientId);
        data.put("clientSecret",secret);
        String result = HttpUtil.postForJSON(tokenUrl,data);
        LOG.info("【北京企创】获取token结果：{}",result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        String accessToken = null;
        if(0 == jsonObject.getIntValue("code")){
            accessToken = jsonObject.getJSONObject("data").getString("accessToken");
        }
        if(StringUtils.isBlank(token)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京企创】分发未知异常："+result));
            return new SendResult(false,"【北京企创】获取token失败："+result);
        }

        isHaveAptitude(po);

        data.clear();
        data.put("remark",po.getName());
        JSONArray mobiles = new JSONArray();
        JSONObject mobile = new JSONObject();
        mobile.put("display","手机");
        mobile.put("type","mobile");
        mobile.put("tel",po.getMobile());
        mobiles.add(mobile);
        data.put("mobiles",mobiles);
        data.put("source","头条");
        data.put("prov_city",po.getCity());
        data.put("birthday","");
        data.put("age",po.getAge());
        data.put("gender",po.getGender());
        data.put("description",getContent(po));
        data.put("nationality","中国");
        data.put("pool","732bd68a-33b5-11ec-99f4-0e65904555db");// A5+

        JSONArray array = new JSONArray();
        array.add(data);

        JSONObject customerList = new JSONObject();
        customerList.put("customer_list",array);
        String url = String.format(sendUrl,accessToken);
        result = HttpUtil.postForJSON(url,customerList);
        LOG.info("【北京企创】发送结果：{}",result);
        jsonObject = JSONUtil.toJSON(result);
        JSONArray successArray = jsonObject.getJSONObject("data").getJSONArray("success");
        if(jsonObject.getBooleanValue("success") && (null != successArray && successArray.size() >0)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【北京企创】发送成功："+result));
            return new SendResult(true,"[北京企创]发送成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京企创】发送失败："+result));
        return new SendResult(false,"[北京企创]发送失败："+result);
    }

//    public static void main(String[] args){
//
////        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzQ5NjY5NzkxMTUzMTQyMTIsImlhdCI6MTYzNDk1OTE3OTExNTMxNDIxMiwiaWQiOiJXMDAwMDAwMDQwNzQifQ.EUgM1K6Dn-2xrsShnbydl9T8YcCO6WRahbbYHXkMsr0";
////        String url = "https://qw-openapi-tx.dustess.com/customer/v1/getCustomerPoolList?accessToken="+token;
////        String result = HttpUtil.postForJSON(url,new JSONObject());
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("伍三人测试请忽略");
//        po.setMobile("13632965533");
//        po.setCity("北京市");
//        po.setLoanAmount("《3-10万》");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setAge(33);
//        po.setChannel("tt0012");
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//
//        ApiSender apiSender = new QiChuangApi();
//        apiSender.send(po,null);
//
//    }
}
