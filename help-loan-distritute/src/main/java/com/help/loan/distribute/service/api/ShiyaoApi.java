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

/**
 * 上海世曜信息科技
 */
@Component("apiSender_10074")
public class ShiyaoApi implements ApiSender {
    private static final Logger log = LoggerFactory.getLogger(ShiyaoApi.class);

    private static final String key = "DbzTJ2txzXiaoLia";

    private static final String sendUrl = "http://dbz.huidaikeji.com/api/api/addwhh";

    private static final String code = "dfkjwhhtg03";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            log.error("【世曜信息科技】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【世曜信息科技】分发异常:"+e.getMessage()));
            return new SendResult(false,"【世曜信息科技】分发异常："+e.getMessage());
        }

    }

    public SendResult send2(UserAptitudePO po, UserDTO select) throws Exception {

        JSONObject data = new JSONObject();
        data.put("code",code);
        data.put("name",po.getName());
        data.put("phone",Long.valueOf(po.getMobile()));
        data.put("money", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("age",(po.getAge() == null || po.getAge()<=0)?30:po.getAge());
        data.put("sex",(po.getGender()==null || po.getGender() ==0)?0:1);
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car", JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("insurance", JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("fund", po.getPublicFund().contains("有，")?1:0);
        data.put("social", 0);
        data.put("credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("webank",0);
        data.put("tax",JudgeUtil.in(po.getCompany(),1)?1:0);
        data.put("work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("city",po.getCity());
        data.put("flag","dfkjwhh");

        String encryptData = AESUtil.java_openssl_encrypt(data.toJSONString(),key);

        data.clear();

        data.put("data",encryptData);

        String result  = HttpUtil.postForJSON(sendUrl,data);
        JSONObject json = JSONUtil.toJSON(result);
        if(500 == json.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【世曜信息科技】分发成功"+result));
            return new SendResult(true,"【世曜信息科技】:"+result);
        }
        String msg = json.getString("msg");
        if("电话号码重复".equals(msg)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【世曜信息】重复"+result));
            return new SendResult(false,"【世曜信息科技】"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【世曜信息】失败"+result));
        return new SendResult(false,"【世曜信息科技】"+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("赵先生");
//        po.setMobile("18605257124");
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
//        ShiyaoApi api = new ShiyaoApi();
//        System.out.println(api.send(po,null));
//    }
}
