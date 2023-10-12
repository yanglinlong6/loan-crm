package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component("apiSender_10032")
public class NiuSanApi implements ApiSender {

    private static Log logger = LogFactory.getLog(NiuSanApi.class);

    private static String URL = "https://www.juzhi168.com/index.php?r=api/callback";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            return  send2(po,select);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,e.getMessage()));
            return new SendResult(false,"【牛散】发送失败："+e.getMessage());
        }
    }


    private SendResult send2(UserAptitudePO po, UserDTO select){

        if(null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isEmpty(po.getName())) {
                if(StringUtils.isEmpty(parse.get("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("amount", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("applyTime", DateUtil.formatToString(new Date(),DateUtil.yyyyMMddHHmmss2));
        data.put("city",po.getCity());
        String income = "";
        if(po.getGetwayIncome() == 1){
            income = ""+(8000*12);
        }else if (po.getGetwayIncome() == 2){
            income = ""+(5000*12);
        }
        data.put("income",income);

        if(po.getPublicFund().contains("有，")){
            data.put("hasCreditCard",1);
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("hasBd",1);
        }
        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("hasHouse",1);
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("hasCar",1);
        }
        data.put("fromAccount","DF");
        data.put("fromPlatform","BD");

        String response = HttpUtil.postForJSON(URL,data);
        if(JSONUtil.isJsonString(response)){
            JSONObject responseJson = JSONUtil.toJSON(response);
            if(200 == responseJson.getIntValue("statusCode")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【牛散】发送成功："+response));
                return new SendResult(true,"【牛散】发送成功："+response);
            }else if(300 == responseJson.getIntValue("statusCode") && "申请人手机号码已经被提交过".equals(responseJson.getString("message"))){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【牛散】发送失败："+response));
                return new SendResult(false,"【牛散】发送失败："+response);
            }
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【牛散】发送失败："+response));
            return new SendResult(false,"【牛散】发送失败："+response);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【牛散】发送失败："+response));
        return new SendResult(false,"【牛散】发送失败："+response);
    }

}
