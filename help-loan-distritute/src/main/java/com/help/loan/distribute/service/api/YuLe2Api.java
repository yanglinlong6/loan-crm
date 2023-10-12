package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.*;
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

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

/**
 * 上海誉勒：上海：apiSender_20033
 */
@Component("apiSender_20033")
public class YuLe2Api implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(YuLe2Api.class);

    private static final String checkUrl = "http://120.79.73.175//api/customer/check_mobile";

    private static final String sendUrl = "http://120.79.73.175//api/customer/import_tttg";

    private static final int file_id = 4;
    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            if(StringUtils.isNotBlank(po.getChannel()) && po.getChannel().contains("ttt")){
                return send2(po,select);
            }
            return new SendResult(false,"【上海誉勒】客户只接收头条客户");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海誉勒】分发未知异常："+e.getMessage()));
            return new SendResult(false,"【上海誉勒】发送异常："+e.getMessage());
        }

    }

    private SendResult send2(UserAptitudePO po, UserDTO select) throws Exception {

        JSONObject json = new JSONObject();
        json.put("mobile",MD5Util.getMd5String(po.getMobile()));
        String checkResult = HttpUtil.postForJSON(checkUrl,json);
        log.info("【上海誉勒】验证手机号码结果：{}",checkResult);
        //{"code":200,"message":"请求成功"}
        if(0 != JSONUtil.toJSON(checkResult).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海誉勒】分发重复："+checkResult));
            return new SendResult(false,"【上海誉勒】验证手机号码:重复："+checkResult);
        }
        if(StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isEmpty(po.getName())) {
                if(StringUtils.isBlank(parse.getString("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("file_id",file_id);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("age",po.getAge());
        data.put("sex",po.getGender());
        data.put("city",po.getCity());
        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("is_house",1);
        }else data.put("is_house",0);

        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("is_car",1);
        }else data.put("is_car",0);

        if(JudgeUtil.in(po.getCompany(),1)){
            data.put("is_company",1);
        }else data.put("is_company",0);

        if(JudgeUtil.in(po.getCreditCard(),1,2)){
            data.put("is_credit",1);
        }else data.put("is_credit",0);

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("is_insurance",1);
        }else data.put("is_insurance",0);
        data.put("is_social",0);
        if(po.getPublicFund().contains("有，")){
            data.put("is_fund",1);
        }else  data.put("is_fund",0);
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            data.put("is_work",1);
        }else data.put("is_work",0);
        data.put("is_tax",0);
        data.put("webank",0);
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()));
        //{"code":"0","msg":"提交成功"}
        String result=HttpUtil.postForJSON(sendUrl,data);
        log.info("【上海誉勒】分发结果：{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        String code = resultJSON.getString("code");
        if("0".equals(code)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海誉勒】成功"+result));
            return new SendResult(true,"【上海誉勒】:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海誉勒】失败"+result));
        return new SendResult(false,"【上海誉勒】"+result);
    }


//    public static void main(String[] args) throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {

//        File file = new File("C:\\software\\test1.xlsx");
//        InputStream inputStream = new FileInputStream(file);
//        List<String[]> readerExcel = XLSXCovertCSVReader.readerExcel(inputStream, "Sheet1", 14);
//        for(String[] array : readerExcel){
//            UserAptitudePO po = new UserAptitudePO();
//            po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//            po.setName(array[1]);
//            po.setMobile(array[2]);
//            po.setCity(array[3]);
//            po.setLoanAmount(array[4]);
//            po.setCar(0);
//            po.setHouse(0);
//            po.setCompany(0);
//            po.setPublicFund("有，个人月缴300-800元");
//            po.setGetwayIncome(1);
//            po.setInsurance(1);
//            po.setAge(33);
//            po.setGender(1);
//            po.setChannel(array[5]);
//            po.setOccupation(0);
//            po.setCreditCard(0);
//            po.setCreateBy("web");
//            po.setUpdateDate(new Date());
//            ApiSender api = new YuLe2Api();
//            System.out.println(api.send(po,null));
//        }

//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("测试朋友圈");
//        po.setMobile("13632965532");
//        po.setCity("上海市");
//        po.setLoanAmount("《3-10万》");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setAge(33);
//        po.setGender(1);
//        po.setChannel("tt0012");
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setCreateBy("car");
//        po.setUpdateDate(new Date());
//        ApiSender api = new YuLe2Api();
//        System.out.println(api.send(po,null));
//    }
}
