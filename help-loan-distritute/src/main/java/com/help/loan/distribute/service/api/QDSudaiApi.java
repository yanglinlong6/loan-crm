package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.DistributeConstant;
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

@Component("apiSender_20091")
public class QDSudaiApi implements ApiSender{

    private static final String sendUrl = "http://api.xingyezhongxin.com:8181/home/adCommonSave?company=4&channel=7&channel_account=10&uuid=qd-toutiao-1";

    private static final String checkUrl = "http://api.xingyezhongxin.com:8181/home/checkRepeatCustomer?company=4&mobile_md5=";

    private static final Logger LOG = LoggerFactory.getLogger(QDSudaiApi.class);

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("[金泰速贷]推送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[金泰速贷]推送异常:"+e.getMessage()));
            return new SendResult(false,"[金泰速贷]推送异常:"+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        String url = checkUrl + MD5Util.getMd5String(po.getMobile());
        String checkResult = HttpUtil.getForObject(url);
        LOG.info("[金泰速贷]撞库结果:{}",checkResult);
        if(null == checkResult){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[金泰速贷]推送失败:"+checkResult));
            return new SendResult(false,"[金泰速贷]推送失败:"+checkResult);
        }
        int code = JSONUtil.toJSON(checkResult).getIntValue("code");
        if(0 != code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[金泰速贷]推送重复:"+checkResult));
            return new SendResult(false,"[金泰速贷]推送重复:"+checkResult);
        }

        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("sex",po.getGender());
        data.put("loan_limit", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("room", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("accumulation",JudgeUtil.contain(po.getPublicFund(),"有，")?1:0);
        data.put("social",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("policy",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        if(JudgeUtil.in(po.getCompany(),1)){
            data.put("business",1);
            data.put("enterprise",1);
        }else{
            data.put("business",0);
            data.put("enterprise",0);
        }
        data.put("business",JudgeUtil.in(po.getCompany(),1)?1:0);
        data.put("issued",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        LOG.info("[金泰速贷]推送详情:{}",data.toJSONString());
        String result = restTemplate.postForObject(sendUrl,data,String.class);
        LOG.info("[金泰速贷]推送结果:{}",result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        code = jsonObject.getIntValue("code");
        if(0 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[金泰速贷]推送成功:"+result));
            return  new SendResult(true,"[金泰速贷]推送成功:"+result);
        }else if(-1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[金泰速贷]推送重复:"+result));
            return  new SendResult(false,"[金泰速贷]推送重复:"+result);
        }else
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[金泰速贷]推送失败:"+result));
        return  new SendResult(false,"[金泰速贷]推送失败:"+result);
    }

    public static void main(String[] args) {
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("伍测试请忽略");
        po.setMobile("15898823777");
        po.setCity("青岛市");
        po.setAge(32);
        po.setGender(2);
        po.setLoanAmount("30000");
        po.setCompany(0);
        po.setPublicFund("无");
        po.setCar(0);
        po.setHouse(0);
        po.setInsurance(0);
        po.setGetwayIncome(1);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setChannel("moerlong-ttt-30");
        po.setType(DistributeConstant.LoanType.CREDIT);
        po.setUpdateDate(new Date());
        ApiSender api = new QDSudaiApi();
        System.out.println(api.send(po, null));
    }
}
