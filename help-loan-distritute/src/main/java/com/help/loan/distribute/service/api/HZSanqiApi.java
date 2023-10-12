package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.CustomerPO;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import com.help.loan.distribute.util.DESUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 三旗广汇信息咨询有限公司-房抵
 */
@Component("apiSender_20085")
public class HZSanqiApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(HZSanqiApi.class);

    private static final String URL = "http://crm.dewufagu.com/crm/import/customer";

    private static String KEY = "3961f54ad2a0479d9c421d36662a20d8";

    private static Long channelId = 59l;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("【惠州三旗-房抵】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【惠州三旗-房抵】分发异常:"+e.getMessage()));
            return new SendResult(false,"【惠州三旗-房抵】分发异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        CustomerPO customerPO = new CustomerPO();
        customerPO.setName(po.getName()+getLoanType(po.getType()));
        customerPO.setMobile(po.getMobile());
        customerPO.setChannel(channelId);

        String channel = po.getChannel();
        String media;
        if(StringUtils.isBlank(channel)){
            media = "朋友圈";
        }else{
            if(channel.contains("ttt")){
                media ="头条";
            }else if(channel.contains("baidu")){
                media = "百度";
            }else{
                media = "朋友圈";
            }
        }
        customerPO.setMedia(media);
        customerPO.setCity(po.getCity());
        customerPO.setAge(po.getAge());
        customerPO.setSex(po.getGender().byteValue());
        customerPO.setNeed(LoanAmountUtil.transform(po.getLoanAmount()).toString());

        if(po.getPublicFund().contains("有，"))
            customerPO.setField2("有");
        else customerPO.setField2("无");

        if(JudgeUtil.in(po.getHouse(),1,2)){
            customerPO.setField3("有");
        }else customerPO.setField3("无");

        if(JudgeUtil.in(po.getCar(),1,2)){
            customerPO.setField4("有");
        }else customerPO.setField4("无");

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            customerPO.setField5("有");
        }else  customerPO.setField5("无");

        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            customerPO.setField6("有");
            customerPO.setField9("有");
        }else {
            customerPO.setField6("无");
            customerPO.setField9("无");
        }

        if(JudgeUtil.in(po.getCompany(),1)){
            customerPO.setField7("有");
        }else customerPO.setField7("无");
        customerPO.setRemark(getContent(po,media,channel));

        String content = DESUtil.encrypt(KEY,customerPO.toString());

        JSONObject data = new JSONObject();
        data.put("channelId",channelId);
        data.put("data",content);
        String result = HttpUtil.postForJSON(URL,data);
        JSONObject json = JSONUtil.toJSON(result);
        LOG.info("【惠州三旗-房抵】发送结果：{}",result);
        int code = json.getIntValue("code");
        if(200 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【惠州三旗-房抵】分发成功："+result));
            return new SendResult(true,"【惠州三旗-房抵】分发成功："+result);
        }else if(601 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【惠州三旗-房抵】发送重复："+result));
            return new SendResult(false,"【惠州三旗-房抵】发送重复："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【惠州三旗-房抵】发送失败："+result));
        return new SendResult(false,"【惠州三旗-房抵】发送失败："+result);
    }

    public String getContent(UserAptitudePO po,String media,String channel){
        StringBuffer content = new StringBuffer();
        content.append("媒体：").append(media);
        content.append(";").append("贷款金额：").append(LoanAmountUtil.transform(po.getLoanAmount()));
        if(po.getPublicFund().contains("有，"))
            content.append(";").append("有300-800公积金");
        if(JudgeUtil.in(po.getHouse(),1,2))
            content.append(";").append("有本地商品房");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2))
            content.append(";").append("有银行代发工资");
        if(JudgeUtil.in(po.getCar(),1,2))
            content.append(";").append("有车");
        if(JudgeUtil.in(po.getInsurance(),1,2))
            content.append(";").append("有商业保单");
        if(1 == po.getCompany())
            content.append(";").append("有公司营业执照");
        return content.toString();
    }

}
