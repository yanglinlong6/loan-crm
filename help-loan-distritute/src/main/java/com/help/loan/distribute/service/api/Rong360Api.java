package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.api.utils.Rong360Util;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 融360: apiSender_10107
 */
@Component("apiSender_10107")
public class Rong360Api implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(Rong360Api.class);

    private static final String sendUrl="http://m.rong360.com/apiapplynew/channeldianxiaoapi/apply?source=daofenkeji";

    private static final String secret_key="JX1sP71oU0s8l0Kwc5bbEtXQgW0zZg12";

    private static final String key = "5OK6kmMu2Zs2r1ru";

    private static final String iv = "xzXm8sdhnv9zZMQk";

    private static final String channel="daofenkeji";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[融360分发异常]分发一场异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[融360]"+e.getMessage()));
            return new SendResult(false,"[融360分发异常]:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws Exception {

        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("mobile", Rong360Util.strEncodBase64(key,iv,po.getMobile()) );
        data.put("realName",po.getName());
        String city = po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity();
        data.put("city",city);
        data.put("channel",channel);
        data.put("loanValue", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("loanTime",12);
        data.put("idNum","");
        String access_token = MD5Util.getMd5String(po.getName()+data.getString("mobile")+city+channel+""+LoanAmountUtil.transform(po.getLoanAmount())+12+secret_key);
        data.put("access_token", access_token);
        String result = HttpUtil.postForJSON(sendUrl,data); // {"code":0,"msg":"success","data":[]}
        log.info("[融360]分发结果:{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        int code = resultJSON.getIntValue("code");
        if(0==code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[融360]"+result));
            return new SendResult(true,"[融360]分发结果:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[融360]"+result));
        return new SendResult(true,"[融360]分发结果:"+"[融360]"+result);
    }
}
