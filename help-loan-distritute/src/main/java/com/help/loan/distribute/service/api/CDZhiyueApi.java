package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 四川智跃天成商务信息咨询有限公司
 */
@Component("apiSender_20118")
public class CDZhiyueApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(CDZhiyueApi.class);

    private static final String checkUrl = "https://www.zytc666.com/api/work/check";

    private static final String sendUrl = "https://www.zytc666.com/api/work/import";

    private static final int channelId = 61;

    private static final String secret = "bc01fd78de51210b8783f0f4a85bd90d";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
           return sendResult(po,select);
        }catch (Exception e){
            LOG.error("[四川智跃]推送异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[四川智跃]推送未知异常："+e.getMessage()));
            return new SendResult(false,"[四川智跃]推送异常:{}"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("code", MD5Util.getMd5String(po.getMobile()));
        String result = HttpUtil.postFormForObject(checkUrl,data); // {"code":1,"msg":"成功","time":"1668500699","data":null}
        LOG.info("[四川智跃]撞库结果:{}",result);
        if(1 != JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[四川智跃]推送重复："+result));
            return new SendResult(false,"[四川智跃]推送重复:{}"+result);
        }
        data.clear();
        data.put("channel_id",channelId);
        data.put("name",po.getName());
        data.put("phone_number",po.getMobile());
        data.put("loan_amount", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("token",MD5Util.getMd5String(po.getMobile()+secret));
        data.put("sex",po.getGender());
        data.put("city",po.getCity());
        data.put("house",po.getHouse());
        data.put("car",po.getCar());
        data.put("house_fund", JudgeUtil.contain(po.getPublicFund(),"有，")?1:0);
        data.put("social_insurance",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("life_insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("salary_method",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("remark",getContent(po));
        result = HttpUtil.postFormForObject(sendUrl,data);
        LOG.info("[四川智跃]推送结果:{}",result);
        if(1 == JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[四川智跃]推送成功："+result));
            return new SendResult(true,"[四川智跃]推送成功:{}"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[四川智跃]推送失败："+result));
        return new SendResult(false,"[四川智跃]推送失败:{}"+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试1");
//        po.setMobile("13632965541");
//        po.setCity("成都市");
//        po.setLoanAmount("50000");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(32);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new CDZhiyueApi();
//        System.out.println(api.send(po,null));
//    }
}
