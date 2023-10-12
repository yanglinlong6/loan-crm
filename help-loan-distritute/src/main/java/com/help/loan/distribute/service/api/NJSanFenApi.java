package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ec.v2.utlis.Md5Util;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.common.utils.http.HttpClientUtil;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 南京小微
 */
@Component("apiSender_20004")
public class NJSanFenApi implements ApiSender{


    private static Logger LOG = LoggerFactory.getLogger(NJAnXinApi.class);

    private static final String checkUrl = "https://api.xiaoweizixun.com/sem_new/checkmb.html?name=%s&mobile=%s&city=%s&source=%s";
    private static final String sendUrl = "https://api.xiaoweizixun.com/sem_new/loan_sh.html";

    private static String dataModel = "name=%s&mobile=%s&city=%s&source=xwll01&sex=%s&age=%s&money=%s&car=%s&house=%s&shebao=%s&gongjijin=%s&credit_card=%s&credit_situation=%s&isbankpay=%s";
    private static String dataModel_tt = "name=%s&mobile=%s&city=%s&source=xwll02&sex=%s&age=%s&money=%s&car=%s&house=%s&shebao=%s&gongjijin=%s&credit_card=%s&credit_situation=%s&isbankpay=%s";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            LOG.error("【南京小微】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【南京三分】分发异常"));
            return new SendResult(false,""+e.getMessage());
        }
    }

    private SendResult send2(UserAptitudePO po, UserDTO select){

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put("uname", "SHANGHAIEF");
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "multipart/form-data");
        String city = po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity();
        String url = String.format(checkUrl,po.getName(), Md5Util.encryptMd5(po.getMobile()),city,"xwll01");
        String result = HttpClientProxy.doPost(url, new JSONObject(),"UTF-8", 3000, httpHeader);
        LOG.info("【南京小微】撞库结果：{}",result);
        if(!"0".equals(result)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【南京小微】撞库重复:"+result));
            return new SendResult(false,"【南京小微】撞库重复："+result);
        }
        isHaveAptitude(po);
        String sex = (po.getGender() == null || po.getGender() != 1)?"女":"男";
        String money = LoanAmountUtil.transformToWan(po.getLoanAmount());
        String car = JudgeUtil.in(po.getCar(),1,2)?"有":"";
        String house = JudgeUtil.in(po.getHouse(),1,2)?"有":"";
        String gongjijin = po.getPublicFund().contains("有，")?"有":"";
        String isbankpay = JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"";
        String data = null;
        if(po.getChannel().contains("ttt")){
            data = String.format(dataModel_tt,po.getName(),po.getMobile(),city,sex,po.getAge(),money,car,house,"",gongjijin,"","",isbankpay);
        }else{
            data = String.format(dataModel,po.getName(),po.getMobile(),city,sex,po.getAge(),money,car,house,"",gongjijin,"","",isbankpay);
        }
        result = HttpClientProxy.doPost(sendUrl+"?"+data, new JSONObject(),"UTF-8", 3000, httpHeader);
        LOG.info("【南京小微】分发结果：{}",result);
        if("0".equals(result)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【南京小微】分发成功:"+result));
            return new SendResult(true,"【南京小微】分发成功："+result);
        }else if("3".equals(result)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【南京小微】分发重复:"+result));
            return new SendResult(false,"【南京小微】分发重复："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【南京小微】分发失败:"+result));
        return new SendResult(false,"【南京小微】分发失败："+result);
    }


//    public static void main(String[] args){
//
//        NJSanFenApi api = new NJSanFenApi();
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("f4f8ccab843d43a4b0a221e534fbed99");
//        po.setCar(4);
//        po.setHouse(2);
//        po.setCity("南京市");
//        po.setCompany(1);
//        po.setGetwayIncome(1);
//        po.setInsurance(2);
//        po.setLoanAmount("《3-5万》");
//        po.setMobile("13632961519");
//        po.setName("测试三请忽略2022");
//        po.setAge(33);
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴300-500元");
//        po.setCreditCard(1);
//        po.setCar(1);
//        SendResult result = api.send(po,null);
//        System.out.println(JSONUtil.toJsonString(result));
//    }
}
