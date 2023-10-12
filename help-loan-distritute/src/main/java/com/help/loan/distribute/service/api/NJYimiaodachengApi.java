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
 * 南京一秒达成信用信息咨询有限公司
 */
@Component("apiSender_20111")
class NJYimiaodachengApi implements ApiSender{

    private static final Logger LOG = LoggerFactory.getLogger(NJYimiaodachengApi.class);

    private static final String checkUrl = "https://openapi.kehu51.com/v1/openapi/583036/%s";

    private static final String sendUrl = "https://openapi.kehu51.com/v1/openapi/583036/01P03R686vb85a";


    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po, select);
        } catch (Exception e) {
            LOG.error("[南京一秒达成]推送异常：{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[南京一秒达成]推送未知异常：" + e.getMessage()));
            return new SendResult(false, "[南京一秒达成]推送异常:" + e.getMessage());
        }
    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        JSONObject data = new JSONObject();
//        data.p

        data.clear();
        isHaveAptitude(po);
        data.put("CusName",po.getName());
        data.put("Customize2", (null == po.getAge() || po.getAge() < 25)?"25":po.getAge().toString());
        data.put("MobilePhone",po.getMobile());
        data.put("sex",JudgeUtil.in(po.getGender(),1)?"男":"女");
//        data.put("custom_date_1",new Date());
        data.put("SourceID",parseAccount(po.getChannel()));
        data.put("WorkPhone", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("Customize1",getContent(po));
        data.put("StateID","");
        String result = HttpUtil.postForJSON(sendUrl,data);
        LOG.info("[南京一秒达成]推送结果:{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        String msg = resultJSON.getString("msg");
        int code = resultJSON.getIntValue("code");
        if(600000 == code){
            try{
                Thread.sleep(5000);
            }catch (Exception e){ }
            String url = String.format(checkUrl,resultJSON.getString("requestCode"));
            result = HttpUtil.getForObject(url);
            LOG.info("[南京一秒达成]推送查询结果:{}",result);
            JSONObject jsonObject = JSONUtil.toJSON(result);
            int state = jsonObject.getIntValue("state");
            msg = jsonObject.getString("msg");
            if(0 == state){
                try{
                    Thread.sleep(5000);
                }catch (Exception e){ }
                result = HttpUtil.getForObject(url);
                state = jsonObject.getIntValue("state");
                msg = jsonObject.getString("msg");
            }
            if(1 == state){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[南京一秒达成]推送成功：" +msg));
                return new SendResult(true,"[南京一秒达成]推送成功:"+result);
            }
            if(JudgeUtil.contain(msg,"手机号重复")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[南京一秒达成]推送重复：" +msg));
                return new SendResult(false,"[南京一秒达成]推送成功:"+result);
            }
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[南京一秒达成]推送成功：" +msg));
            return new SendResult(false,"[南京一秒达成]推送成功:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[南京一秒达成]推送失败：" +result));
        return new SendResult(false,"[南京一秒达成]推送失败:"+result);
    }
//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试17");
//        po.setMobile("13059692917");
//        po.setCity("常州市");
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
//        ApiSender api = new NJYimiaodachengApi();
//        System.out.println(api.send(po,null));
//    }
}
