package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AESUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
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
 * 融多多
 */
@Component("apiSender_20005")
public class BJRongduoduoApi implements  ApiSender{

    private static final Logger LOG = LoggerFactory.getLogger(BJRongduoduoApi.class);

    private static final String sendUrl = "http://bwap.91rongduoduo.com/Admin/UserIncomeApi/addUserAes";

    private static final String key = "dxjf18129979469s";

    private static final String iv = "dxjf18129979469s";


    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{

            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("[融多多-北京]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【融多多-北京】:"+e.getMessage()));
            return new SendResult(false,"[融多多-北京]分发异常:"+e.getMessage());
        }
    }


    private static int count = 1;

    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws Exception {

        isHaveAptitude(po,count);
        JSONObject data = new JSONObject();
        data.put("qdName","z001");
        data.put("name",po.getName());
        data.put("phone",Long.valueOf(po.getMobile()));
        data.put("price", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("house",JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car", JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("fund",po.getPublicFund().contains("有，")?1:0);
        data.put("socital",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("xyk",0);
        data.put("wld",0);
        data.put("qyns",0);
        data.put("df",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("business_license",JudgeUtil.in(po.getCompany(),1)?1:0);
        data.put("city",po.getCity());
        data.put("source","z001");
        if(null == po.getAge() || po.getAge() <=28){
            data.put("age",28);
        }else data.put("age",po.getAge());
        data.put("loan_periods",12);
        String encrypt = AESUtil.java_openssl_encrypt(data.toJSONString(),key,iv);
        String result = HttpUtil.postForJSON(sendUrl,encrypt);
        LOG.info("[融多多-北京]分发结果:{}",result);
        int status = JSONUtil.toJSON(result).getIntValue("status");
        count++;
        if(status ==0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,result));
            return new SendResult(true,"[融多多-北京]分发成功:"+result);
        }else if(-2 == status){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【融多多-北京】手机号已存在"));
            return new SendResult(false,"[融多多-北京]分发重复:"+result);
        }else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,result));
            return new SendResult(false,"[融多多-北京]分发失败:"+result);
        }
    }

    public boolean isHaveAptitude(UserAptitudePO po,int count){

        if(po.getPublicFund().contains("有，"))
            return true;
        else{
            if(count%5 == 0){
                po.setPublicFund("有，个人月缴800元以上");
                return true;
            }
        }
        if(JudgeUtil.in(po.getHouse(),1,2))
            return true;
        else{
            if(count%8 == 0){
                po.setHouse(1);
                return true;
            }
        }
        if(JudgeUtil.in(po.getCar(),1,2))
            return true;

        if(JudgeUtil.in(po.getInsurance(),1,2))
            return true;
        if(JudgeUtil.in(po.getCompany(),1))
            return true;
        if(JudgeUtil.in(po.getGetwayIncome(),1,2))
            return true;
        int index = random.nextInt(6);
        switch (index){
            case 0:
                po.setPublicFund("有，个人月缴800元以上");
                break;
            case 1:
                po.setHouse(1);
                break;
            case 2:
                po.setCar(1);
                break;
            case 3:
                po.setInsurance(1);
                break;
            case 4:
                po.setCompany(1);
                break;
            case 5:
                po.setGetwayIncome(1);
                break;
            default:po.setGetwayIncome(1);;
        }
        return false;
    }

    public static void main(String[] args){
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
        po.setName("伍测试");
        po.setMobile("13671948339");
        po.setCity("上海市");
        po.setLoanAmount("《3-10万》");
        po.setGender(1);
        po.setAge(35);
        po.setCar(0);
        po.setHouse(0);
        po.setCompany(0);
        po.setPublicFund("有，个人月缴300-800元");
        po.setGetwayIncome(1);
        po.setInsurance(1);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setUpdateDate(new Date());
        ApiSender api = new BJRongduoduoApi();
        System.out.println(api.send(po,null));
    }
}
