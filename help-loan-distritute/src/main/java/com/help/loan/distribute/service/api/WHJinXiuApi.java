package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
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

/**
 * 武汉锦绣数科
 */
@Component("apiSender_20028")
public class WHJinXiuApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(WHJinXiuApi.class);

    private static final String checkUrl = "https://jiekou.miaodaimao.com/sem/check_mobile.html";

    private static final String sendUrl = "https://jiekou.miaodaimao.com/sem/loan_do.html";

    private static final String source = "AO1bzwy";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【武汉-锦绣数科】发送异常：{},{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【武汉-锦绣数科】分发异常:"+e.getMessage()));
            return new SendResult(false,"【武汉-锦绣数科】发送异常："+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("mobile", MD5Util.getMd5String(po.getMobile()));
        String city = po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity();
        data.put("city",city);
        data.put("source",source);
        //{"code":"100","msg":"\u5ba2\u6237\u4e0d\u5b58\u5728\uff01"}
        String checkResult = HttpUtil.postForJSON(checkUrl,data);
        log.info("【武汉-锦绣数科】撞库结果:{}",checkResult);
        JSONObject json = JSONUtil.toJSON(checkResult);
        String code = json.getString("code");
        String msg = json.getString("msg");
        if("101".equals(code)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【武汉-锦绣数科】分发重复:"+msg));
            return new SendResult(false,"【武汉-锦绣数科】客户已存在："+msg);
        }
        data.clear();
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("city",city);
        data.put("source",source);
        data.put("ip","127.0.0.1");
        data.put("age",po.getAge());
        data.put("sex",po.getGender() ==1?"男":"女");
        data.put("car", JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("job", JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        data.put("baodan_is", JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        data.put("money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("shebao", JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");

        data.put("gongjijin", po.getPublicFund().contains("有，")?"有":"无");
        data.put("isbankpay", JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("credit_card", JudgeUtil.in(po.getCreditCard(),1,2)?"有":"无");
        if(po.getChannel().startsWith("tt")){
            data.put("meiti", "头条");
        }else{
            data.put("meiti", "朋友圈");
        }
        data.put("time", DateUtil.to10());
        data.put("weili",0);
        data.put("zhima",0);
        //success
        String result = HttpUtil.postForJSON(sendUrl,data);
        if("success".equals(result)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【武汉-锦绣数科】发送成功:"+msg));
            return new SendResult(true,"【武汉-锦绣数科】发送成功："+result);
        }else if("3".equals(result)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【武汉-锦绣数科】分发重复:"+msg));
            return new SendResult(false,"【武汉-锦绣数科】发送重复："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【武汉-锦绣数科】发送失败:"+msg));
        return new SendResult(false,"【武汉-锦绣数科】发送失败："+result);
    }

}
