package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ec.v2.utlis.Md5Util;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.city.CityService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import com.help.loan.distribute.util.DisConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
 *  成都简易蓉
 */
@Component("apiSender_20052")
public class CDJianYiRongHouseApi implements ApiSender {
    private static final Logger log = LoggerFactory.getLogger(CDJianYiRongHouseApi.class);

    private static final String sendUrl="https://www.huaxiazhonglian.com/api/xuchao/user";

    private static final String secret = "YVbfSKQNgKQAMM4JRUK2yoQhD88aLq";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Autowired
    private CityService cityService;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            SendResult sendResult;
            switch (po.getCreateBy()){
                case DisConstant.User.Type.HOUSE:
                    sendResult = sendResultWithHouse(po,select);
                    break;
                default: sendResult = new SendResult(false,"不是房抵客户："+po.getCreateBy()); break;
            }
            return sendResult;
        }catch (Exception e){
            log.error("[成都简易蓉]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[成都简易蓉]分发异常:"+e.getMessage()));
            return new SendResult(false,"[成都简易蓉]分发异常"+e.getMessage());
        }

    }

    private SendResult sendResultWithHouse(UserAptitudePO po, UserDTO select){

        long createtime = DateUtil.to10();
        String token = Md5Util.encryptMd5(po.getMobile()+createtime+secret);
        JSONObject data = new JSONObject();
        data.put("token",token);
        if(StringUtils.isBlank(po.getChannel())){
            data.put("channel_name","微信");
        }else{
            if(po.getChannel().toLowerCase().toLowerCase().contains("ttt")){
                data.put("channel_name","头条");
            }else
                data.put("channel_name","百度");
        }
        data.put("name",po.getName());
        data.put("phone_number",po.getMobile());
        if(JudgeUtil.in(po.getHouse(),1,2,3) || JudgeUtil.in(po.getHouseExtension(),5,6,7,8)){
            data.put("house",1);
        }
        int houseType;
        if(null == po.getHouseExtension())
            houseType = 0;
        else{
            switch (po.getHouseExtension()){
                case 5:houseType=1;break;
                case 6:houseType=2;break;
                case 7:houseType=3;break;
                case 8:houseType=4;break;
                default:houseType=0;break;
            }
        }
        data.put("house_type",houseType);
        data.put("sex",po.getGender());
        data.put("age",po.getAge());
        data.put("loan_amount", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("house_estate",po.getCity());
        data.put("createtime",createtime);
//{"code":1,"msg":"操作成功","time":"1634108945","data":null}
        String result = HttpUtil.postForJSON(sendUrl,data);
        log.info("[成都简易蓉]发送结果:{}",result);
        int code = JSONUtil.toJSON(result).getIntValue("code");
        if(1 == code){
            String msg = "[成都简易蓉]"+result;
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,msg));
            return new SendResult(true,msg);
        }else if(2013 == code){
            String msg = "[成都简易蓉]"+result;
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,msg));
            return new SendResult(false,msg);
        }
        String msg = "[成都简易蓉]"+result;
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,msg));
        return new SendResult(false,"[成都简易蓉]失败:"+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试2236");
//        po.setMobile("13059692916");
//        po.setCity("成都市");
//        po.setLoanAmount("50");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(0);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setCreateBy("house");
//        po.setHouseExtension(5);
//        po.setUpdateDate(new Date());
//        ApiSender api = new CDJianYiRongHouseApi();
//        System.out.println(api.send(po,null));
//    }

}
