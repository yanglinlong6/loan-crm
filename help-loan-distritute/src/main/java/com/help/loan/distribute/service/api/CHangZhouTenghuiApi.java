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

import java.util.Date;

/**
 * 常州腾汇商务信息咨询有限公司
 */
//@Component("apiSender_20110")
class CHangZhouTenghuiApi implements ApiSender{

    private static final Logger LOG = LoggerFactory.getLogger(CHangZhouTenghuiApi.class);

    private static final String checkUrl = "http://thoa.18021805678.com/index.php/api/check_repeat";

    private static final String sendUrl = "http://thoa.18021805678.com/index.php/api/haohan";


    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po, select);
        } catch (Exception e) {
            LOG.error("[常州腾汇]推送异常：{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[常州腾汇]推送未知异常：" + e.getMessage()));
            return new SendResult(false, "[常州腾汇]推送异常:" + e.getMessage());
        }
    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("tel", MD5Util.getMd5String(po.getMobile()));
        String result = HttpUtil.postForJSON(checkUrl,data); // {"status":true,"code":0}
        LOG.info("[常州腾汇]撞库验证结果:{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        if(1 == resultJSON.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[常州腾汇]撞库重复：" +result));
            return new SendResult(false,"[常州腾汇]撞库重复");
        }
        data.clear();
        isHaveAptitude(po);

        data.put("cname",po.getName());
        data.put("tel",po.getMobile());
        data.put("age", (null == po.getAge() || po.getAge() < 25)?25:po.getAge());
        data.put("ed", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("fc",JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("cc",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("wld",0);
        data.put("gjj",JudgeUtil.contain(po.getPublicFund(),"有，")?1:0);
        data.put("bd",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("zmf",0);
        data.put("sr",JudgeUtil.in(po.getGetwayIncome(),1,2)?"1000-10000":0);
        data.put("city","江苏,"+(po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity()));
        data.put("remarks",getContent(po));
        result = HttpUtil.postFormForObject(sendUrl,data);
        LOG.info("[常州腾汇]推送结果:{}",result);
        resultJSON = JSONUtil.toJSON(result);
        String msg = resultJSON.getString("message");
        int code = resultJSON.getIntValue("code");
        if(0 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[常州腾汇]推送成功：" +msg));
            return new SendResult(true,"[常州腾汇]推送成功:"+msg);
        }
        if(2 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[常州腾汇]推送重复：" +msg));
            return new SendResult(false,"[常州腾汇]推送重复:"+msg);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[常州腾汇]推送失败：" +msg));
        return new SendResult(false,"[常州腾汇]推送失败:"+msg);
    }
//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍技术测试09");
//        po.setMobile("13059692920");
//        po.setCity("徐州市");
//        po.setLoanAmount("50");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setCreateBy("house");
//        po.setHouseExtension(5);
//        po.setUpdateDate(new Date());
//        ApiSender api = new CHangZhouTenghuiApi();
//        System.out.println(api.send(po,null));
//    }
}
