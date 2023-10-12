package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.city.CityService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import com.help.loan.distribute.util.DisConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
 *  淘前车抵房抵 apiSender_20034
 */
@Component("apiSender_20034")
public class TaoQianWithHouseApi implements ApiSender {
    private static final Logger log = LoggerFactory.getLogger(TaoQianWithHouseApi.class);

    private static final String sendUrl="https://wx.taoqianhua.com/app/loan/submitloan";

    private static final String key = "3845f1017952979c01be700fd8c2539e";

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
                default: sendResult = new SendResult(false,"不是车抵or房抵客户："+po.getCreateBy()); break;
            }
            return sendResult;
        }catch (Exception e){
            log.error("[淘前-车抵or房抵]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[淘前-车抵or房抵]分发异常:"+e.getMessage()));
            return new SendResult(false,"[沃得财富-北京]分发异常"+e.getMessage());
        }

    }

    private SendResult sendResultWithHouse(UserAptitudePO po, UserDTO select){
        JSONObject data = new JSONObject();
        data.put("site","http://fd.gzhsdph.com/#/?accountId=1705423721249799&channel=ttt_house_gx_wp_24&sitename=抖音火山");
        data.put("sitename","抖音火山");
        data.put("username",po.getName());
        data.put("mobile",po.getMobile());

        data.put("amount", null == po.getLoanAmount()? 50000:Double.valueOf(po.getLoanAmount())*10000);
        data.put("total",po.getCarPrice() != null ? po.getCarPrice()*10000:0);

        String houseType;
        if(null == po.getHouseExtension())
            houseType = "未知";
        else{
            switch (po.getHouseExtension()){
                case 5:houseType="商品房";break;
                case 6:houseType="公寓";break;
                case 7:houseType="别墅";break;
                case 8:houseType="商铺";break;
                default:houseType="未知";break;
            }
        }
        data.put("housetype",houseType);

        String house;
        switch (po.getHouse()){
            case 1:house="名下全款有证";break;
            case 2:house="名下按揭有证";break;
            default:house="未知";break;
        }
        data.put("house",house);
        data.put("code",cityService == null ? po.getCity():cityService.getProvince(po.getCity())+" "+po.getCity());
        data.put("type",3);
        data.put("appkey",key);
        // {"code":0,"msg":"D1052165"}
        String result = HttpUtil.postForJSON(sendUrl,data);
        log.info("[淘前-房抵]发送结果:{}",result);
        int code = JSONUtil.toJSON(result).getIntValue("code");
        if(0 == code){
            String msg = "[淘前-房抵]"+result;
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,msg));
            return new SendResult(true,msg);
        }else if(2013 == code){
            String msg = "[淘前-房抵]"+result;
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,msg));
            return new SendResult(false,msg);
        }
        String msg = "[淘前-房抵]"+result;
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,msg));
        return new SendResult(false,"[淘前-房抵]失败:"+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试2236");
//        po.setMobile("13059692916");
//        po.setCity("武汉市");
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
//        ApiSender api = new TaoQianWithHouseApi();
//        System.out.println(api.send(po,null));
//    }

}
