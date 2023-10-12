package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ChengduApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(ChengduApi.class);

    private static final String sendUrl="http://zhudai.17jiedai.com.cn/public/index.php/api/goods/Dfscience";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【成都】分发异常:"+e.getMessage()));
            return new SendResult(false,"成都分发异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("username",po.getName());
        data.put("phone",po.getMobile());
        data.put("money",po.getLoanAmount());
        data.put("location",po.getCity());
        data.put("remark",getInfo(po));
        data.put("luodi",13);
        data.put("time", DateUtil.to10());

        String result = HttpUtil.postForJSON(sendUrl,data);
        log.info("【成都】分发结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        if(200 == json.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【成都】分发成功:"+result));
            return new SendResult(true,"【成都】分发成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【成都】分发失败:"+result));
        return new SendResult(false,"【成都】分发失败："+result);
    }

    private java.lang.String getInfo(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getCompany() ==1){
            memo.append("有营业执照").append(",");
        }
        if(po.getPublicFund().contains("有，")){
            memo.append("有公积金").append("，");
        }else memo.append("无公积金").append("，");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            memo.append("有银行代发工资").append(",");
        }
        if(JudgeUtil.in(po.getHouse(),1,2)){
            memo.append("有商品房").append(",");
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            memo.append("有车").append(",");
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            memo.append("有商业寿险保单").append(",");
        }
        if(null == po.getAge() || po.getAge() <=30){
            memo.append("年龄：30岁").append(",");
        }else{
            memo.append("年龄：").append(po.getAge()).append("岁,");
        }
        return memo.toString();
    }

//    public static void main(String[] args){
//
//        ChengduApi api = new ChengduApi();
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("f4f8ccab843d43a4b0a221e534fbed99");
//        po.setCar(4);
//        po.setHouse(2);
//        po.setCity("成都市");
//        po.setCompany(1);
//        po.setGetwayIncome(1);
//        po.setInsurance(2);
//        po.setLoanAmount("《3-5万》");
//        po.setMobile("13632965538");
//        po.setName("测试三");
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴300-500元");
//        po.setCreditCard(1);
//        po.setCar(1);
//
//        SendResult result = api.send(po,null);
//        System.out.println(JSONUtil.toJsonString(result));
//    }

}
