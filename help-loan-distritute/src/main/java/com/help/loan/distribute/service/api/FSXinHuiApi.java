package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
 * 佛山鑫辉
 */
@Component("apiSender_20080")
public class FSXinHuiApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(FSXinHuiApi.class);

    private static final String sendUrl = "http://www.fsxhxx.com/ajax/api.ashx?cmd=crmCustom_add_procChannel&qdbh=2022329&qdcode=Bzpyq973xYW";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("[佛山鑫辉]推送异常:{}",e.getMessage(),e);
            String message = "[佛山鑫辉]推送异常:"+e.getMessage();
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,message));
            return new SendResult(false,message);
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("name","Bz");
        data.put("khmc",po.getName());
        data.put("sj",po.getMobile());
        data.put("nl",po.getAge());
        data.put("xb", JudgeUtil.in(po.getGender(),1)?"男":"女");
        data.put("dkje", LoanAmountUtil.transform(po.getLoanAmount()));
        if(JudgeUtil.contain(po.getChannel(),"ttt","shengbei")){
            data.put("khly","头条");
        }else if(JudgeUtil.contain(po.getChannel(),"baidu")){
            data.put("khly","百度");
        }else{
            data.put("khly","朋友圈");
        }
        data.put("ywfc",JudgeUtil.in(po.getHouse(),1,2)?"本地房":"无房");
        data.put("carqk",JudgeUtil.in(po.getCar(),1,2)?"有车":"无车");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            data.put("ywsb","有社保");
            data.put("gzdf","有代发");
        }else{
            data.put("ywsb","无社保");
            data.put("gzdf","无代发");
        }

        data.put("gjj",JudgeUtil.in(po.getPublicFund(),"有，")?"有公积金":"无公积金");
        data.put("xykqk",JudgeUtil.in(po.getCreditCard(),1,2)?"有信用卡":"无信用卡");
        data.put("bdqk",JudgeUtil.in(po.getInsurance(),1,2)?"有保单":"无保单");
        data.put("qyns",JudgeUtil.in(po.getCompany(),1)?"有企税":"无企税");

        // {"ResponseID":0,"Message":"操作成功！","Data":{"id":"188773"}}
        String result = HttpUtil.postForJSON(sendUrl,data);
        LOG.info("[佛山鑫辉]推送结果:{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        int responseId = resultJSON.getIntValue("ResponseID");
        String message = "[佛山鑫辉]推送结果:"+resultJSON.getString("Message");
        if(0 == responseId){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,message));
            return new SendResult(true,message);
        }
        if(1 == responseId && JudgeUtil.contain(resultJSON.getString("Message"),"数据撞库")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,message));
            return new SendResult(false,message);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,message));
        return new SendResult(false,message);
    }

//    public static void main(String[] args) {
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试请忽略");
//        po.setMobile("15214191689");
//        po.setCity("合肥市");
//        po.setAge(32);
//        po.setGender(1);
//        po.setLoanAmount("30000");
//        po.setCompany(1);
//        po.setPublicFund("无");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setChannel("5937-xx");
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setUpdateDate(new Date());
//        ApiSender api = new FSXinHuiApi();
//        System.out.println(api.send(po, null));
//    }
}
