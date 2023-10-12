package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.HttpUtilForJava;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.http.HttpHeaders;
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

import javax.print.DocFlavor;
import java.util.*;

/**
 * 上海得丰
 */
@Component("apiSender_20030")
public class DeFengApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(DeFengApi.class);

    private static final String sendUrl = "http://qw.weihuo6.com/index/index/fun_get_from_bd";

    private static final String source = "邦正头条";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[得丰]分发异常:{}-{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[得丰]分发异常:"+e.getMessage()));
            return new SendResult(false,"[得丰]分发失败:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try{
            HttpPost post = new HttpPost(sendUrl);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            List paramList = new ArrayList<>();
            paramList.add(new BasicNameValuePair("name", po.getName()));
            paramList.add(new BasicNameValuePair("mobile",po.getMobile()));
            paramList.add(new BasicNameValuePair("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity()));
            paramList.add(new BasicNameValuePair("load", LoanAmountUtil.transformToWan(po.getLoanAmount())));

            paramList.add(new BasicNameValuePair("has_house", JudgeUtil.in(po.getHouse(),1,2)?"1":"0"));
            paramList.add(new BasicNameValuePair("has_car", (JudgeUtil.in(po.getCar(),1,2)?1:0)+""));
            paramList.add(new BasicNameValuePair("has_insurance", JudgeUtil.in(po.getInsurance(),1,2)?"1":"0"));
            paramList.add(new BasicNameValuePair("has_gjj", po.getPublicFund().contains("有，")?"1":"0"));
            paramList.add(new BasicNameValuePair("has_sb", JudgeUtil.in(po.getGetwayIncome(),1,2)?"1":"0"));
            paramList.add(new BasicNameValuePair("has_company", JudgeUtil.in(po.getCompany(),1,2)?"1":"0"));
            paramList.add(new BasicNameValuePair("has_salary", JudgeUtil.in(po.getGetwayIncome(),1,2)?"1":"0"));
            paramList.add(new BasicNameValuePair("has_credit", JudgeUtil.in(po.getCreditCard(),1,2)?"1":"0"));
            paramList.add(new BasicNameValuePair("source", source));
            post.setEntity(new UrlEncodedFormEntity(paramList, "utf-8"));
            //{"code":200,"msg":{"city":"\u4e0a\u6d77\u5e02","pro":"\u4e0a\u6d77\u5e02"}}  {"city":"上海市","pro":"上海市"}
            // {"code":300,"msg":"\u649e\u5e93"} 撞库
            String result = EntityUtils.toString(httpclient.execute(post).getEntity(), "utf-8");
            JSONObject json = JSONUtil.toJSON(result);
            int code = json.getIntValue("code");
            String msg = json.getString("msg");
            log.info("[得丰]分发结果,code-{},msg-{}",code,msg);
            if(200 == code){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[得丰]分发成功:"+msg));
                return new SendResult(true,"[得丰]分发成功:"+msg);
            }
            if(300 == code){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[得丰]分发重复:"+msg));
                return new SendResult(false,"[得丰]分发重复:"+msg);
            }
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[得丰]分发失败:"+msg));
            return new SendResult(false,"[得丰]分发失败:"+msg);
        }catch (Exception e){
            log.error("[得丰]分发异常:{}-{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[得丰]分发异常:"+e.getMessage()));
            return new SendResult(false,"[得丰]分发失败:"+e.getMessage());
        }finally {
            try{
                if(null != response)
                    response.close();
                httpclient.close();
            }catch (Exception e){

            }
        }

    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试3");
//        po.setMobile("136329655323");
//        po.setCity("上海市");
//        po.setLoanAmount("50000");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new DeFengApi();
//        System.out.println(api.send(po,null));
//    }
}
