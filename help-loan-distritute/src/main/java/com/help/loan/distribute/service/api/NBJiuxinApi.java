package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ec.v2.utlis.Md5Util;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 宁波玖欣客信息科技有限公司
 */
@Component("apiSender_20117")
public class NBJiuxinApi implements ApiSender {

    private static final String checkUrl = "http://121.40.173.17:8811/admin/pushApi/check_mobile";

    private static final String sendUrl = "http://121.40.173.17:8811/admin/pushApi/import_tttg";

    private static final int file_id = 42;

    private static final String promotion = "BZ";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            LOGGER.error("[宁波玖欣]推送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[宁波玖欣]推送异常："+e.getMessage()));
            return new SendResult(false,"[杭州正佰融]推送异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("mobile", Md5Util.encryptMd5(po.getMobile()));
        String result = HttpUtil.postFormForObject(checkUrl,data);
        LOGGER.info("[宁波玖欣]撞库结果:{}",result);
        if(0 != JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[宁波玖欣]推送重复："+result));
            return new SendResult(false,"[宁波玖欣]推送重复:"+result);
        }
        isHaveAptitude(po);
        data.clear();

        data.put("file_id",file_id);
        data.put("promotion",promotion);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("age",null == po.getAge()?0:po.getAge());
        data.put("sex",po.getGender());
        data.put("city",po.getCity());
        data.put("is_house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("is_car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("is_company",po.getCompany() ==1 ? 1:0);
        data.put("is_credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("is_insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("is_social",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("is_fund",po.getPublicFund().contains("有，")?1:0);
        data.put("is_work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("is_tax",0);
        data.put("webank",0);
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()).toString());
        data.put("content",getContent(po));
        result = HttpUtil.postFormForObject(sendUrl,data);
        LOGGER.info("[宁波玖欣]推送结果：{}",result);
        if(0 == JSONUtil.toJSON(result).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[宁波玖欣]推送成功:"+result));
            return new SendResult(true,"[宁波玖欣]推送成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[宁波玖欣]推送失败:"+result));
        return new SendResult(false,"[宁波玖欣]推送失败："+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试1");
//        po.setMobile("13631965238");
//        po.setCity("宁波市");
//        po.setLoanAmount("5000000");
//        po.setCompany(1);
//        po.setPublicFund("公积金有，");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setChannel("zxf-ttt-ningbo");
//        po.setUpdateDate(new Date());
//        ApiSender api = new NBJiuxinApi();
//        System.out.println(api.send(po,null));
//
//    }
}
