package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * 杭州联优
 */
@Component("apiSender_20090")
public class HZLianyouApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(HZLianyouApi.class);

    private static final String sendUrl = "http://103.74.173.186:6688/addons/skycaiji.index/do?model=kehumodel";


    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[杭州联优]推送异常:"+e.getMessage()));
            return new SendResult(false,"[杭州联优]推送异常:"+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("xingming",po.getName());
        data.put("shouji",po.getMobile());
        data.put("sqje",po.getLoanAmount());
        data.put("bendiren","不详");
        data.put("gsd","不详");
        data.put("xueli","不详");
        data.put("fangchan", JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        data.put("chechan",JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("baoxian",JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        data.put("shebao",JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("gongjijin",JudgeUtil.contain(po.getPublicFund(),"有，")?"有":"无");
        data.put("kaipiao","无");
        data.put("apiKey","sd4F9DS8d3w963g");

        String result = restTemplate.postForObject(sendUrl,data,String.class);

        LOG.info("[杭州联优]推送结果:{}",result);

        JSONObject resultJSON = JSONUtil.toJSON(result);

        String msg = HttpUtil.convertUnicodeToCh(resultJSON.getString("msg"));
        resultJSON.put("msg",msg);

        if(1 == resultJSON.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[杭州联优]推送成功:"+resultJSON.toJSONString()));
            return new SendResult(true,"[杭州联优]推送成功:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[杭州联优]推送失败:"+resultJSON.toJSONString()));
        return new SendResult(false,"[杭州联优]推送失败:"+resultJSON.toJSONString());
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("海测试6");
//        po.setMobile("13671948331");
//        po.setCity("杭州市");
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
//        ApiSender api = new HZLianyouApi();
//        System.out.println(api.send(po,null));
//    }

}
