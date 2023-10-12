package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.common.utils.http.HttpClientUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;


/**
 * 北京众京商务咨询服务有限公司: apiSender_10118
 */
@Component("apiSender_10118")
public class SHZhongjingApi implements  ApiSender{

    private static final Logger log = LoggerFactory.getLogger(SHZhongjingApi.class);

    private static final String sendUrl = "http://120.53.228.36/api/member/receive_list";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[上海众京]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海众京】分发未知异常："+e.getMessage()));
            return new SendResult(false,"[上海众京]分发异常:"+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws IOException {
        isHaveAptitude(po);
        if(org.apache.commons.lang3.StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isEmpty(po.getName())) {
                if(StringUtils.isEmpty(parse.get("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try{
            HttpPost post = new HttpPost(sendUrl);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            List paramList = new ArrayList<>();
            paramList.add(new BasicNameValuePair("data[leads_name]", po.getName()));
            paramList.add(new BasicNameValuePair("data[leads_tel]",po.getMobile()));
            paramList.add(new BasicNameValuePair("data[leads_area]",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity()));
            paramList.add(new BasicNameValuePair("data[is_home]", JudgeUtil.in(po.getHouse(),1,2)?"1":"0"));
            paramList.add(new BasicNameValuePair("data[is_car]", (JudgeUtil.in(po.getCar(),1,2)?1:0)+""));
            paramList.add(new BasicNameValuePair("data[is_baoxian]", JudgeUtil.in(po.getInsurance(),1,2)?"1":"0"));
            paramList.add(new BasicNameValuePair("data[is_gjj]", po.getPublicFund().contains("有，")?"1":"0"));
            paramList.add(new BasicNameValuePair("data[is_sb]", "0"));
            paramList.add(new BasicNameValuePair("data[is_wld]", "0"));
            paramList.add(new BasicNameValuePair("data[money]", LoanAmountUtil.transformToWan(po.getLoanAmount())));
            paramList.add(new BasicNameValuePair("data[is_dkgj]", JudgeUtil.in(po.getGetwayIncome(),1,2)?"1":"0"));
            post.setEntity(new UrlEncodedFormEntity(paramList, "utf-8"));
            //{"code":200,"msg":true}
            String result = EntityUtils.toString(httpclient.execute(post).getEntity(), "utf-8");
            log.info("[北京众京]分发结果{}",result);
            JSONObject json = JSONUtil.toJSON(result);
            if(200 == json.getIntValue("code")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海众京】http表单分发成功："+result));
                return new SendResult(true,"[北京众京]http表单分发成功:"+result);
            }
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海众京】http表单分发失败："+result));
            return new SendResult(false,"[北京众京]http表单分发失败:"+result);
        }catch (Exception e){
            log.error("[北京众京]:分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海众京】http分发异常："+e.getMessage()));
            return new SendResult(false,"[北京众京]http分发异常:"+e.getMessage());
        }finally {
            httpclient.close();
            if(null != response){
                response.close();
            }
        }

    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("张先森测试3");
//        po.setMobile("13632965529");
//        po.setCity("上海市");
//        po.setLoanAmount("5000000");
//        po.setCompany(0);
//        po.setPublicFund("公积金有，");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(0);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new SHZhongjingApi();
//        System.out.println(api.send(po,null));
//    }


}
