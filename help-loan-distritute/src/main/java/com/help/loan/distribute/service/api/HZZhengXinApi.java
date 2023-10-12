package com.help.loan.distribute.service.api;

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

/**
 * 杭州正鑫：杭州正鑫科技信息技术有限公司
 */
@Component("apiSender_10093")
public class HZZhengXinApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(HZZhengXinApi.class);

    private static final String checkUrl = "http://zhexiangdai.com/api/customer/check_mobile";

    private static final String sendUrl = "http://zhexiangdai.com/api/customer/import_cdbg";

    private static final int file_id = 7;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【杭州正鑫】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【杭州正鑫】分发异常:"+e.getMessage()));
            return new SendResult(false,"【杭州正鑫】分发异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        String md5 = MD5Util.getMd5String(po.getMobile());
        JSONObject data = new JSONObject();
        data.put("mobile",md5);

        String result = HttpUtil.postForJSON(checkUrl,data);
        log.info("【杭州正鑫】验证重复:{}",result);
        if(0 != JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【杭州正鑫】分发重复:"+result));
            return new SendResult(false,"【杭州正鑫】验证重复:"+result);
        }

        data.clear();
        data.put("file_id",file_id);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("age",po.getAge());
        data.put("sex",po.getGender());
        data.put("city",po.getCity());
        data.put("is_house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("is_car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("is_company",po.getCompany() ==1 ? 1:0);
        data.put("is_credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("is_insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("is_social",0);
        data.put("is_fund",po.getPublicFund().contains("有，")?1:0);
        data.put("is_work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("is_tax",0);
        data.put("webank",0);
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()));
        result = HttpUtil.postForJSON(sendUrl,data);
        log.info("【杭州正鑫】分发结果：{}",result);
        if(0 == JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【杭州正鑫】分发成功:"+result));
            return new SendResult(true,"【杭州正鑫】分发成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【杭州正鑫】分发失败:"+result));
        return new SendResult(false,"【杭州正鑫】分发失败："+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("海测试3");
//        po.setMobile("13671948207");
//        po.setCity("南京市");
//        po.setLoanAmount("《3-10万》");
//        po.setGender(1);
//        po.setAge(35);
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setUpdateDate(new Date());
//        HZYuyeApi api = new HZYuyeApi();
//        System.out.println(api.send(po,null));
//    }
}
