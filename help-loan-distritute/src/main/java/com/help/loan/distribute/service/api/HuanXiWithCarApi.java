package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import com.help.loan.distribute.util.DisConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 *  欢喜房抵车抵 apiSender_20036
 */
@Component("apiSender_20036")
public class HuanXiWithCarApi implements ApiSender {
    private static final Logger log = LoggerFactory.getLogger(HuanXiWithCarApi.class);

    private static final String sendUrl="https://rdtt02.rdmsxd.com/api/reg?cid=%s&src=%s&v=%s";

    private static final int cid_car = 154;
    private static final String src_car = "bzcd01";


//    private static final int cid_house = 153;
//    private static final String src_house = "bzfd01";

    private static final String key = "aa81ec4afc277b97";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            if(!JudgeUtil.in(po.getCreateBy(),DisConstant.User.Type.CAR)){
                return new SendResult(false,"不是车抵客户："+po.getCreateBy());
            }
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[欢喜-车抵]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[欢喜-车抵]分发异常:"+e.getMessage()));
            return new SendResult(false,"[欢喜-车抵]分发异常"+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws IOException {

        String city = po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity();
        int houses = !JudgeUtil.in(po.getHouse(),1,2)?3:po.getHouse();
        int car = 3;
        if(JudgeUtil.in(po.getCar(),2,5)){
            car = 2;
        }else if (JudgeUtil.in(po.getCar(),1)){
            car = 1;
        }else car = 3;

        String v = MD5Util.getMd5String(po.getMobile()+cid_car+key);
        String url = String.format(sendUrl,cid_car,src_car,v);
        LinkedHashMap map = new LinkedHashMap();
        map.put("name",po.getName());
        map.put("age",25);
        map.put("sex",0);
        map.put("mobile",po.getMobile());
        map.put("city", city);
        map.put("loan_amount", po.getLoanAmount());
        map.put("ip","127.0.0.1");
        map.put("houses",houses);
        map.put("car",car);
        map.put("life_policy",2);
        map.put("epf_time",1);
        map.put("social_security",1);
        map.put("remark", Base64Utils.encodeToString(getRemark(po).getBytes("UTF-8")));
        Map<String,String> header = new HashMap<>();
        header.put(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        // {"errno":"1","msg":"\u7b7e\u540d\u9519\u8bef"}
        String result = HttpClientProxy.doPost(url,map,"UTF-8",5000,header);
        log.info("[欢喜-车抵or房抵]分发结果:{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        String errno = json.getString("errno");
        String msg = json.getString("msg");
        if("0".equals(errno) && "OK".equals(msg)){
            String resultMsg = "[欢喜-"+po.getCreateBy()+"]发送成功:"+msg;
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,resultMsg));
            return new SendResult(true,resultMsg);
        }else if("5".equals(errno)){
            String resultMsg = "[欢喜-"+po.getCreateBy()+"]发送重复:"+msg;
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,resultMsg));
            return new SendResult(false,resultMsg);
        }
        String resultMsg = "[欢喜-"+po.getCreateBy()+"]发送失败:"+msg;
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,resultMsg));
        return new SendResult(false,resultMsg);

    }

    private String getRemark(UserAptitudePO po){
        JSONObject json = new JSONObject();
        if(JudgeUtil.in(po.getCreateBy(), DisConstant.User.Type.CAR)){
            if(JudgeUtil.in(po.getCar(),1)){
                json.put("车辆情况","名下有全款车");
            }else if(JudgeUtil.in(po.getCar(),2,5)){
                json.put("车辆情况","名下有按揭车还完或者接收押车");
            }else{
                json.put("车辆情况","无车");
            }
            json.put("裸车价",po.getCarPrice());
            return json.toJSONString();
        }
        if(JudgeUtil.in(po.getCreateBy(),DisConstant.User.Type.HOUSE)){
            if(JudgeUtil.in(po.getHouse(),1)){
                json.put("房产情况","名下全款房有证");
            }else if(JudgeUtil.in(po.getHouse(),2)){
                json.put("房产情况","名下按揭房有证");
            }else {
                json.put("房产情况","未知");
            }

            switch (po.getHouseExtension()){
                case 5:
                    json.put("房产类型","商品房");
                    break;
                case 6:
                    json.put("房产类型","公寓");
                    break;
                case 7:
                    json.put("房产类型","别墅");
                    break;
                case 8:
                    json.put("房产类型","商铺");
                    break;
                default:json.put("房产类型","未知");break;
            }
        }
        return json.toJSONString();
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试451");
//        po.setMobile("13049692417");
//        po.setCity("武汉市");
//        po.setLoanAmount("50000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(0);
//        po.setInsurance(0);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setCreateBy("car");
//        po.setUpdateDate(new Date());
//        ApiSender api = new HuanXiWithCarApi();
//        System.out.println(api.send(po,null));
//    }

}
