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

/**
 * 上海邦芒信息科技有限公司: apiSender_20011 :15988199461
 */
@Component("apiSender_20011")
public class SHBangMangApi  implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SHBangMangApi.class);

    private static final String checkUrl = "http://crm.91zhouzhuan.com/api?key=u8Siuc21&md5_tel=";

    private static final String sendUrl = "http://crm.91zhouzhuan.com/api?key=u8Siuc21";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【上海邦芒】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海邦芒】分发异常:"+e.getMessage()));
            return new SendResult(false,"【上海邦芒】分发异常："+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        String result = HttpUtil.getForObject(checkUrl+MD5Util.getMd5String(po.getMobile()));
        log.info("【上海邦芒】撞库结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        int checkCode = json.getIntValue("success");
        String message= json.getString("msg");
        if(1 != checkCode ){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海邦芒】分发重复:"+result));
            new SendResult(false,"【上海邦芒】撞库重复："+message);
        }
        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("tel",Long.valueOf(po.getMobile()));
        data.put("name",po.getName());
        data.put("need_quota", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("age",po.getAge());
        data.put("sex",po.getGender());
        data.put("car", JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        data.put("funds", po.getPublicFund().contains("有，")?"有":"无");
        data.put("insurance", JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        data.put("wages", JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");

        result = HttpUtil.postForJSON(sendUrl,data);
        log.info("【上海邦芒】分发结果：{}",result);
        JSONObject resultJson = JSONUtil.toJSON(result);
        if(resultJson.getIntValue("success") == 1){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海邦芒】分发成功:"+result));
            return new SendResult(true,"【上海邦芒】分发成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海邦芒】分发失败:"+result));
        return new SendResult(false,"【上海邦芒】分发失败："+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("刘永军测试");
//        po.setMobile("15988199463");
//        po.setCity("上海市");
//        po.setLoanAmount("50000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(1);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        SHBangMangApi api = new SHBangMangApi();
//        System.out.println(api.send(po,null));
//    }
}
