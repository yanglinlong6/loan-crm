package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 北京万益融
 * 公司名称：万益融(北京)金融服务外包有限公司
 * 通讯地址：北京市朝阳区朝外大街22号9层908
 * 业务联系人：梅婷婷
 * 联系电话：13311345217
 * 电子邮箱：692124698@qq.com
 * 开票类型：普票
 * 发票抬头：万益融（北京）金融服务外包有限公司
 * 公司税号：91110107MA019BD262
 */
@Component("apiSender_20053")
public class BJWanYiRongApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(BJWanYiRongApi.class);

    private static final String sendUrl = "http://39.107.96.94/api/channelClue";

    private static final int channel = 34;
    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[北京万益融]发送异常：{}-{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京万益融]发送异常："+e.getMessage()));
            return new SendResult(false,"[北京万益融]发送异常："+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("channel",channel);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("quota", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("car_status", JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("house_status", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("insurance_status", JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("fund_status", po.getPublicFund().contains("有，")?1:0);
        data.put("warranty_status", JudgeUtil.in(po.getInsurance(),1,2)?1:0);

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        // {"status":200,"data":[],"msg":"\u7533\u8bf7\u6210\u529f\uff01"}
        // {"status":203,"data":[],"msg":"\u8be5\u624b\u673a\u53f7\u5df2\u7533\u8bf7\uff01"}
        String result = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        JSONObject json = JSONUtil.toJSON(result);
        int status = json.getIntValue("status");
        String message = json.getString("msg");
        log.info("[北京万益融],发送结果-{}-{}",status,message);
        if(200 == status){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京万益融]成功"+status+message));
            return new SendResult(true,"[北京万益融]成功："+message);
        }else if(203 == status){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京万益融]成功"+status+message));
            return new SendResult(false,"[北京万益融]重复："+message);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京万益融]成功"+status+message));
        return new SendResult(false,"[北京万益融]失败："+message);
    }

//    public static void main(String[] args) {
//		UserAptitudePO po = new UserAptitudePO();
//		po.setUserId(null);
//		po.setName("伍散人测试请忽略");
//		po.setMobile("13049692801");
//		po.setCity("北京市");
//		po.setLoanAmount("50000");
//		po.setCompany(0);
//		po.setPublicFund("没有公积金");
//		po.setCar(0);
//		po.setHouse(1);
//		po.setInsurance(1);
//		po.setGetwayIncome(0);
//		po.setOccupation(0);
//		po.setCreditCard(0);
//		po.setAge(30);
//		po.setGender(1);
//		po.setUpdateDate(new Date());
//		ApiSender api = new BJWanYiRongApi();
//		System.out.println(api.send(po, null));
//	}
}
