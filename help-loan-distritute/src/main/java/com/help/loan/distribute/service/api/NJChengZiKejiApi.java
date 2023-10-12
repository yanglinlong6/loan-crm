package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.omg.CORBA.DATA_CONVERSION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 南京-橙子科技 apiSender_20019
 */
@Component("apiSender_20019")
public class NJChengZiKejiApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(NJChengZiKejiApi.class);

    private static final String checkNJ = "https://api.sudaixia.com/sem_oo/check_loan.html";
    private static final String checkNJ_NO = "https://jiekou.shanjiewang.com/sem_oo/check_loan.html";

    private static final String sendUrlNJ     = "https://api.sudaixia.com/sem/loan_do.html";
    private static final String sendUrlNJ_NO  = "https://jiekou.shanjiewang.com/sem/loan_do.html";

    private static final String source = "bangzheng";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            JSONObject data = new JSONObject();
            data.put("MobilePhone",MD5Util.getMd5String(po.getMobile()));
            String url = "";
            if(JudgeUtil.in(po.getCity(),"南京市"))
                url = checkNJ;
            else url = checkNJ_NO;
            String response = HttpUtil.postFormData(url,data);
            LOG.info("[橙子科技-{}]撞库结果:{}",po.getCity(),response);
            JSONObject json = JSONUtil.toJSON(response);
            String  msg = json.getString("Msg");
            if(100 != json.getIntValue("Code")){
                return new SendResult(false, "[橙子科技-南京]撞库:" +msg);
            }
            return send1(po, select);
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
            String msg = String.format(msgModel,po.getCity(),"异常",e.getMessage());
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【橙子科技】"+msg));
            return new SendResult(false, "[橙子科技]" +msg);
        }

    }


    private SendResult send1(UserAptitudePO po, UserDTO select) {
        return register(po,select);
    }


    private SendResult register(UserAptitudePO po,UserDTO select){
        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("name", po.getName());
        data.put("mobile", po.getMobile());
        data.put("sex", JudgeUtil.in(po.getGender(),1)? "男":"女");
        data.put("age", po.getAge());
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("car", JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("house", JudgeUtil.in(po.getHouse(), 1, 2, 4)?"有":"无");
        data.put("baodan_is", JudgeUtil.in(po.getInsurance(), 1, 2)?"有":"无");
        data.put("gongjijin", JudgeUtil.in(po.getPublicFund(), "0", "没有")?"无":"有");
        data.put("money", Integer.valueOf(LoanAmountUtil.transformToWan(po.getLoanAmount())));
        data.put("source", source);// 固定值
        data.put("check_num","");
        data.put("ip", "120.73.25.225");
        data.put("credit_card", JudgeUtil.in(po.getCreditCard(), 1, 2)?"有":"无");
        if(JudgeUtil.in(po.getGetwayIncome(), 1, 2) || JudgeUtil.in(po.getOccupation(), 1, 2, 3)) {
            data.put("job", "有");
            data.put("isbankpay", "是");
            data.put("shebao", "有");
        } else {
            data.put("job", "无");
            data.put("isbankpay", "否");
            data.put("shebao", "无");
        }
        data.put("meiti", "邦正");
        data.put("time", DateUtil.to10());
        data.put("weili", "无");
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        String url = "";
        if(JudgeUtil.in(po.getCity(),"南京市"))
            url = sendUrlNJ;
        else url = sendUrlNJ_NO;
        String response = HttpClientProxy.doPost(url, data, "UTF-8", 3000, httpHeader);
        LOG.info("[橙子科技]-{}，用户：{},推送地址:{},分发结果：{}",po.getCity(), po.getMobile(),url, response);
        if("n".equals(response)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【橙子科技】不再接收入库"+response));
            return new SendResult(false, response);
        }
        int code = Integer.valueOf(response);
        if(code > 10000) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【橙子科技】"+response));
            return new SendResult(true, "【橙子科技】"+response);
        }
        if(3 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【橙子科技】"+response));
            return new SendResult(false, "【橙子科技】撞库:"+response);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【橙子科技】"+response));
        return new SendResult(false, response);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("71f63d78911a4ab3806d69eccccb0642");
//        po.setName("伍散人2-测试");
//        po.setAge(33);
//        po.setMobile("13049632819");
//        po.setCity("苏州市");
//        po.setLoanAmount("50000");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setCompany(1);
//        po.setPublicFund("无");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setChannel("zxf-ttt-30");
//        po.setUpdateDate(new Date());
//        ApiSender api = new NJChengZiKejiApi();
//        System.out.println(api.send(po,null));
//    }

}
