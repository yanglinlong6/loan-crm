package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 *  企鹅:apiSender_20020
 */
@Component("apiSender_20020")
public class NJQieIIApi implements ApiSender {
    private static Logger LOG = LoggerFactory.getLogger(NJQieIIApi.class);


    private static final String checkUrl = "http://110.42.64.194:8081/Admin/UserIncomeApi/comparePhone";

    private static final String sendUrl = "http://110.42.64.194:8081/Admin/UserIncomeApi/addUser";

    private static final String source = "wq-01";

//    private static final String URL = "http://101.37.76.253:8009/customer/import";
//    private static final String  vender_name = "bzwy";
//    private static final String appid = "20014";
//    private static final String appkey = "ZWMZgZ7K4PLlCls2JB0xPeGhmfr6fXve";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            return send2(po,select);
        }catch (Exception e){
            LOG.error("[企峨]推送异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[企峨]推送异常"));
            return new SendResult(false,""+e.getMessage());
        }
    }

    private SendResult send2(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("phone", MD5Util.getMd5String(po.getMobile()));
        String response = HttpUtil.postFormData(checkUrl,data);
        LOG.info("[企峨]撞库结果:{}",response);
        if(!"200".equals(JSONUtil.toJSON(response).getString("status"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[企峨]撞库重复:"+response));
            return new SendResult(false,"[企峨]撞库重复:"+response);
        }
        data.clear();
        data.put("qdName",source);
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("price",LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("house",po.getHouse());
        data.put("car",po.getCar());
        data.put("insurance",po.getInsurance());
        data.put("fund",JudgeUtil.contain(po.getPublicFund(),"有，")?1:0);
        data.put("socital",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("xyk",po.getCreditCard());
        data.put("wld",0);
        data.put("qyns",0);
        data.put("city",po.getCity());
        data.put("source",source);
        data.put("age",po.getAge());
        data.put("content",getContent(po));

        response = HttpUtil.postFormData(sendUrl,data);
        LOG.info("[企峨]推送结果:{}",response);
        JSONObject jsonObject = JSONUtil.toJSON(response);
        int status = jsonObject.getIntValue("status");
        String mes = UnicodeUtil.toCN(jsonObject.getString("mes"));
        if(0  == status){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[企峨]推送成功:"+mes));
            return new SendResult(true,"[企峨]推送成功:"+mes);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[企峨]推送失败:"+mes));
        return new SendResult(false,"[企峨]推送失败:"+mes);
    }



    public static void main(String[] args){
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("3e491487931543d7ab1f088e254f1812");
        po.setName("伍散人测试");
        po.setHouse(1);
        po.setCity("南京市");
        po.setCompany(1);
        po.setGetwayIncome(1);
        po.setInsurance(1);
        po.setLoanAmount("《3万以下》");
        po.setMobile("18229491955");
        po.setOccupation(1);
        po.setPublicFund("有，个人月缴500元");
        po.setCreditCard(1);
        po.setCar(1);
        po.setAge(25);
        po.setChannel("moerlong-ttt-30");
        po.setUpdateDate(new Date());
        ApiSender api = new NJQieIIApi();
        System.out.println(api.send(po,null));

//        System.out.println(MD5Util.getMd5String("19947571204"));
    }
}
