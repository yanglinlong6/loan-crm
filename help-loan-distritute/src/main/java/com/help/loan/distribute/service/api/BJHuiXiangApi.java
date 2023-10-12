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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 北京惠享时代 apiSender_20048
 */
@Component("apiSender_20048")
public class BJHuiXiangApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(BJHuiXiangApi.class);

    private static final String checkUrl = "http://8.140.139.68/api/customer/check_mobile";

    private static final String sendUrl = "http://8.140.139.68/api/customer/import_data";

    private static final int file_id = 41;

    private static final int file_id_tt = 71;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【北京惠享时代】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京惠享时代】推送异常:"+e.getMessage()));
            return new SendResult(false,"【北京惠享时代】推送异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        String md5 = MD5Util.getMd5String(po.getMobile());
        JSONObject data = new JSONObject();
        data.put("mobile",md5);

        String result = HttpUtil.postForJSON(checkUrl,data);
        log.info("【北京惠享时代】验证重复:{}",result);
        if(0 != JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【北京惠享时代】推送重复:"+result));
            return new SendResult(false,"【北京惠享时代】推送重复:"+result);
        }

        isHaveAptitude(po);
        data.clear();
//        if(StringUtils.isNotBlank(po.getChannel())  && po.getChannel().toLowerCase().contains("tt")){
//            data.put("file_id",file_id_tt);
//        }else{
//            data.put("file_id",file_id);
//        }
        data.put("file_id",file_id_tt);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("age",null == po.getAge()?0:po.getAge());
        data.put("sex",po.getGender());
        data.put("city",po.getCity());
        data.put("is_house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("is_car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("is_company",po.getCompany() ==1 ? 1:0);
        data.put("is_credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("is_insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("is_social",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("is_fund",po.getPublicFund().contains("有，")?1:0);
        data.put("is_work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("is_tax",0);
        data.put("webank",0);
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()).toString());
        data.put("wish_remarks",getContent(po));
        result = HttpUtil.postForJSON(sendUrl,data);
        log.info("【北京惠享时代】推送结果：{}",result);
        if(0 == JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【北京惠享时代】推送成功:"+result));
            return new SendResult(true,"【北京惠享时代】分发成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京惠享时代】推送失败:"+result));
        return new SendResult(false,"【北京惠享时代】推送失败："+result);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3f031229fbc3492ab507ad30fabe73a8");
//        po.setName("伍测试请忽略1");
//        po.setMobile("13632965530");
//        po.setCity("北京市");
//        po.setLoanAmount("300000");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setChannel("ttt");
//        po.setUpdateDate(new Date());
//        po.setGender(1);
//        BJHuiXiangApi api = new BJHuiXiangApi();
//        System.out.println(api.send(po,null));
//    }

}
