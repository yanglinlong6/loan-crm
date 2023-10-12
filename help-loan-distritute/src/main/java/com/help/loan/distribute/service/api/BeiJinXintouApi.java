package com.help.loan.distribute.service.api;

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

import java.util.Date;

/**
 * 北京鑫投金融服务外包有限公司
 */
//@Component("apiSender_10082")
public class BeiJinXintouApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(BeiJinXintouApi.class);

    private static final String checkUrl = "http://121.40.192.174:6801/erp/api/ifRepeat";

    private static final String sendUrl = "http://121.40.192.174:6801/erp/api/addclue";

    private static final String appid = "ms+VXiyglhMTRw";

    private static final String appkey = "npSUD3+mkxBtOhWe1Z0qpcM";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return send2(po,select);
        }catch (Exception e){
            log.error("【北京鑫投】分发未知异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京鑫投】分发未知异常："+e.getMessage()));
            return new SendResult(false,"【北京鑫投】分发异常："+e.getMessage());
        }

    }

    private SendResult send2(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("Appid",appid);
        data.put("Appkey",appkey);
        data.put("Userphone",po.getMobile());
        String checkResult = HttpUtil.postForJSON(checkUrl,data);
        log.info("【北京鑫投】验证手机号码结果：{}"+checkResult);
        int code = JSONUtil.toJSON(checkResult).getIntValue("result");
        String message = JSONUtil.toJSON(checkResult).getString("message");
        if(400 != code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【北京鑫投】分发重复："+message));
            return new SendResult(false,"【北京鑫投】手机号码重复："+checkResult);
        }
        isHaveAptitude(po);
        data.clear();
        data.put("Appid",appid);
        data.put("Appkey",appkey);
        data.put("Userphone",po.getMobile());
        data.put("Username",po.getName());
        data.put("Sex",po.getGender());
        data.put("City",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("Channel","朋友圈银行端");
        data.put("ChannelInfo","易贷中心");
        data.put("Money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("Info",getInfo(po));
        System.out.println(data.toJSONString());
        String sendResult = HttpUtil.postForJSON(sendUrl,data);
        log.info("【北京鑫投】分发结果：{}",sendResult);
       code = JSONUtil.toJSON(sendResult).getIntValue("result");
        if(200 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【北京鑫投】分发成功："+message));
            return new SendResult(true,"【北京鑫投】分发成功："+sendResult);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京鑫投】分发失败："+message));
        return new SendResult(false,"【北京鑫投】分发失败："+sendResult);

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
        if(null == po.getAge()){
            memo.append("年龄：30岁").append(",");
        }else{
            memo.append("年龄：").append(po.getAge()).append("岁,");
        }
        memo.append("申请金额：").append(po.getLoanAmount());
        return memo.toString();
    }


}
