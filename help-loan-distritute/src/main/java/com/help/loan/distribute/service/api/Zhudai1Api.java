package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/*
 * 	助贷-包包
 */
@Component("apiSender_20001")
public class Zhudai1Api implements ApiSender {
    private static final Logger log = LoggerFactory.getLogger(Zhudai1Api.class);

    private static final String shUrl="https://jiekou.sudaipingtai.com/sem/loan_do.html";
    private static final String url="https://jiekou.chinazhudai.com/sem/loan_do.html";

    private static final String meiti="daofen";
    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
//            if(JudgeUtil.in(po.getCity(),"南京市","上海市","无锡市")){
//                if(po.getChannel().contains("ttt")){
//                    return sendResult(po,select);
//                }else{
//                    return new SendResult(false,"【助贷-包包】"+po.getCity()+",只接收【南京-头条】");
//                }
//            }
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[上海福力]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[上海福力]分发异常:"+e.getMessage()));
            return new SendResult(false,"[上海福力]分发异常"+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws UnsupportedEncodingException {

        // 撞库检查
        JSONObject data = new JSONObject();
        JSONObject json =null;
        if("杭州市".equals(po.getCity())){
            String url = "https://jiekou.chinazhudai.com/sem_oo/check_loan_hz.html?MobilePhone="+MD5Util.getMd5String(po.getMobile());
            json = JSONUtil.toJSON(HttpUtil.postForJSON(url,data));
        }else if("上海市".equals(po.getCity())){
            String url = "https://jiekou.sudaipingtai.com/sem_oo/check_loan.html?MobilePhone="+MD5Util.getMd5String(po.getMobile());
            json = JSONUtil.toJSON(HttpUtil.postForJSON(url,data));
        }else{
            String url = "https://jiekou.chinazhudai.com/sem_oo/check_loan.html?MobilePhone="+MD5Util.getMd5String(po.getMobile());
            json = JSONUtil.toJSON(HttpUtil.postForJSON(url,data));
        }
        log.info("[上海福力]分发撞库验证:{}",json);
        if("101".equals(json.getString("Code"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[上海福力]分发重复:"+json));
            return new SendResult(false,"[上海福力]分发重复:{}"+json.toJSONString());
        }
        isHaveAptitude(po);
        data.clear();
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("car","有");
        }else{
            data.put("car","无");
        }
        data.put("age",po.getAge());

        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            data.put("job","有");
        }else{
            data.put("job","无");
        }
        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("house","有");
        }else data.put("house","无");

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("baodan_is","有");
        }else data.put("baodan_is","无");
        data.put("sex",(null == po.getGender() || 2==po.getGender())?"女":"男");
        String money = LoanAmountUtil.transformToWan(po.getLoanAmount());
        if("5".equals(money)){
            data.put("money",8);
        }else
            data.put("money",Integer.valueOf(money));
        if(null != po.getChannel() && po.getChannel().contains("ttt"))
            data.put("source","liuwusi");
        else data.put("source","sansiwu");
        data.put("shebao","无");
        if(po.getPublicFund().contains("有，")){
            data.put("gongjijin","有");
        }else data.put("gongjijin","无");

        data.put("check_num","");
        data.put("ip", "127.0.0.1");
        data.put("credit_card","无");
        data.put("meiti","daofen");
        data.put("time", DateUtil.to10());
        data.put("weili",0);

        String result;
        if("上海市".equals(po.getCity())){
            result = HttpClientUtil.doPostForm("https://jiekou.sudaipingtai.com/sem/loan_do.html",data,"utf-8");
        }else{
            result = HttpClientUtil.doPostForm("https://jiekou.chinazhudai.com/sem/loan_do.html",data,"utf-8");
        }
        log.info("[助贷]分发结果:{}",result);//1471102
        Long code = Long.valueOf(result);
        if(code>1000){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[上海福力]分发成功:"+result));
            return new SendResult(true,"[上海福力]分发成功:"+result);
        }
        if(3 == code.intValue()){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[上海福力]分发重复:"+result));
            return new SendResult(false,"[上海福力]分发重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[上海福力]分发失败:"+result));
        return new SendResult(false,"[上海福力]分发失败:"+result);

    }

//    private String getInfo(UserAptitudePO po){
//        StringBuffer memo = new StringBuffer();
//        if(po.getCompany() ==1){
//            memo.append("有营业执照").append(",");
//        }
//        if(po.getPublicFund().contains("有，")){
//            memo.append("有公积金").append("，");
//        }
//
//        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
//            memo.append("有银行代发工资").append(",");
//        }
//        if(JudgeUtil.in(po.getHouse(),1,2)){
//            memo.append("有商品房").append(",");
//        }
//        if(JudgeUtil.in(po.getCar(),1,2)){
//            memo.append("有车").append(",");
//        }
//        if(JudgeUtil.in(po.getInsurance(),1,2)){
//            memo.append("有商业寿险保单").append(",");
//        }
//        memo.append("申请金额：").append(po.getLoanAmount());
//        return memo.toString();
//    }

    public static void main(String[] args){
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("测试请忽略1");
        po.setMobile("13632965518");
        po.setCity("南通市");
        po.setLoanAmount("500000");
        po.setCompany(0);
        po.setPublicFund("没有公积金");
        po.setCar(1);
        po.setHouse(1);
        po.setInsurance(1);
        po.setGetwayIncome(0);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setAge(30);
        po.setGender(1);
        po.setChannel("ttt");
        po.setUpdateDate(new Date());
        ApiSender api = new Zhudai1Api();
        System.out.println(api.send(po,null));
    }

}
