package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AESUtil;
import com.help.loan.distribute.common.utils.HttpUtilForJava;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.api.utils.RSAUtils;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * 	北京亿贷
 */
@Component("apiSender_20079")
public class BJYiDaiCenterApi implements ApiSender {
    private static final Logger LOG = LoggerFactory.getLogger(BJYiDaiCenterApi.class);

    private static final String CHECK_URL = "http://12.56.175.54:10183/cust-info/checkPhoneNum?phoneNumEncrypt=%s";

    private static final String SEND_URL = "http://123.56.175.54:10183/cust-info/saveFromOut/withCheck?encryptionData=%s";

    private static final String KEY = "QunJe#XC%SLCC@18";

    String PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIS7ibl2Wds7Go5IkC8hVztKnIXK0nXDLJmenK" +
            "Gv0BuRDR9B8k9irwW+YGydEnmjjI92b9JHDidwunETorKfYLWTRKU3QDqnTQiWWaqK1nK4+F55Uh" +
            "cHshxvZJNGT+b6k6LXxNHgpEJaUyBUhUTx69Em/WsWAisoYqRerUwp0ErQIDAQAB";
    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{

            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("[北京亿贷中心]推送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京亿贷中心]推送异常:"+e.getMessage()));
            return new SendResult(false,"[北京亿贷中心]推送异常"+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws Exception {

        // 撞库检查
        JSONObject data = new JSONObject();
//        data.put("phoneNumEncrypt", MD5Util.getMd5String(po.getMobile()));
//
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "form-data");
//
//        String checkUrl = String.format(CHECK_URL,MD5Util.getMd5String(po.getMobile()));
//
//        String checkResult = HttpClientProxy.doPost(checkUrl, data, "UTF-8", 3000, httpHeader);
//        LOG.info("[北京亿贷中心]撞库结果:{}",checkResult);
//        if(!JSONUtil.toJSON(checkResult).getBooleanValue("success")){
//            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京亿贷中心]撞库重复:"+checkResult));
//            return new SendResult(false,"[北京亿贷中心]撞库重复:"+checkResult);
//        }
        isHaveAptitude(po);


        data.clear();
        data.put("custName",po.getName());
        data.put("phoneNum",po.getMobile());
        data.put("age", po.getAge());
        data.put("sex",JudgeUtil.in(po.getGender(),1)?0:1);
        data.put("loanAmount", LoanAmountUtil.transform(po.getLoanAmount()));

        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("house",0);
        }else data.put("house",1);

        if(po.getPublicFund().contains("有，")){
            data.put("providentFund",0);
        }else data.put("providentFund",1);

        data.put("socialSecurity",JudgeUtil.in(po.getGetwayIncome(),1)?0:1);

        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("car",0);
        }else data.put("car",1);

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("insurancePolicy",0);
        }else data.put("insurancePolicy",1);

        if(JudgeUtil.in(po.getCreditCard(),1,2)){
            data.put("is_credit",0);
        }else data.put("is_credit",1);

        if(JudgeUtil.in(po.getCompany(),1)){
            data.put("businessLicense",0);
        }else data.put("businessLicense",1);

        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            data.put("onBehalfOf",0);
        }else{
            data.put("onBehalfOf",1);
        }
        data.put("custRemark",getInfo(po));
        data.put("dataFrom","广告5头条");

        JSONArray array = new JSONArray();
        array.add(data);

        JSONObject reqBody = new JSONObject();
        reqBody.put("reqBody",array);

        JSONObject encryptionData = new JSONObject();
        encryptionData.put("encryptionData", RSAUtils.encrypt(reqBody.toJSONString(), PUB_KEY));

        String url = String.format(SEND_URL,URLEncoder.encode(RSAUtils.encrypt(reqBody.toJSONString(), PUB_KEY),"UTF-8"));


        // {"retCode":"0000000","retDesc":"操作成功!","timestamp":"2022-03-24 15:56:46.731","rspBody":null}
        // {"retCode":"0000000","retDesc":"数据重复>>>>ccb699299d4742aead4c2fe707e06279","timestamp":"2022-03-24 16:04:50.535","rspBody":"hOvpbvRpE+5FGErB/2i6BknQ9wdbk0ggMnpfvQZLZDq7jx1rfM0reomzxDWOrIyRJLKdh889GhI+\nszdDSJ8mGDyamp1YfMafdUGB9/PS/56oPGWOXJPHS8uwBjUnsk3XXHGXE3dTibEv23AzWRGU423N\n1vdhs306f1lIUftCKGkGIm9dINN9eyxceNhistXYWLG35rVfWetECYGgdR+qbe5iliRG7xSCxzTl\nwutuCXaDIZfMXBTPtjfelwTPq2Y59WOJnJa3M4EQXyx6SfmgnApGwesXVXcWwvjPcLF/WpMKJed3\nbC3f9YmSrVwLCBA9N6HPbnKAKBXkBDad3bbJa1TsxrpgBPrhg0sYG5+DWs5IQtiP0XBJBQNgikQ7\npOplAliUS7TmwjIahKRTSk4YGoJ2pcEn43XRJvQPI91WLPk9H9bMO/g363VlMLD6V4AsKIL3KSh+\n5qIxtxV1QHKFY8HXMWPTiJkztXg+W+aP8Wo7+uBAiRxyj27TdDT5BalbQhdb4fNaChumKy9gVMwf\nuAps2e6AtNBBgT5fgLbNAV+XZ6NAwnwWd2gqWVt4CUW0YrjHUAW34vslDDGIE/xrx8RG56CXgXpQ\nGay86TwPVGMyVVLWLgAOUOI2rlbLuMH1FqYvIwqs3bp6l59//b5TBBEtAx7oIyAqZG041VKuk3ck\np77q2poXrMjcXjauT6ezBXZuN2PdFfrKCaob3Ba4PxjWmpGrGezMD2RQPELly9oY4lFgw0ikxSEk\nrXRGu+pERnuOOTAW2Hfy7pQQq23lvy7ZhvOWV6A9810X54RPn7lCAoa2+mjhT59ARY0uzL5Hf2hS\nu2ijw1xBnIZkeIKS9VR7ux5y8sJf6oswtWAc5jO6ek16/Ys8tALfdIoRNTIOvuIiMTVVPaLPcMGV\nVDGiVGjJQZk4OK7BQb8i8lcdq/OKYEd52IQ8cQooYPJiEBekLATPSpaVv24omO00CbUXhsMuuRmN\n1DFvKg2CupN+U6JIqXR69qqOcIzuEkpz+gNL\n"}
        String result = HttpClientProxy.doPost(url, new JSONObject(), "UTF-8", 3000, httpHeader);
        LOG.info("[北京亿贷中心]分发结果:{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        String code = json.getString("retCode");
        String messsage = json.getString("retDesc");
        if("0000000".equals(code)){
            if(messsage.contains("成功")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京亿贷中心]推送成功:"+result));
                return new SendResult(true,"[北京亿贷中心]推送成功:"+result);
            }else if(messsage.contains("数据重复")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京亿贷中心]推送重复:"+result));
                return new SendResult(false,"[北京亿贷中心]推送重复:"+result);
            }else{
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京亿贷中心]推送失败:"+result));
                return new SendResult(false,"[北京亿贷中心]推送失败:"+result);
            }
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京亿贷中心]推送失败:"+result));
        return new SendResult(false,"[北京亿贷中心]推送失败:"+result);

    }

    private String getInfo(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getCompany() ==1){
            memo.append("有营业执照").append(",");
        }
        if(po.getPublicFund().contains("有，")){
            memo.append("有公积金").append("，");
        }

        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            memo.append("有银行代发工资").append(",");
        }
        if(JudgeUtil.in(po.getHouse(),1,2)){
            memo.append("有商品房").append(",");
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            memo.append("有车").append(",");
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            memo.append("有商业寿险保单").append(",");
        }
        memo.append("申请金额：").append(po.getLoanAmount());
        return memo.toString();
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("张测试2");
//        po.setMobile("13049692811");
//        po.setCity("北京市");
//        po.setLoanAmount("50000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new BJYiDaiCenterApi();
//        System.out.println(api.send(po,null));
//
//    }

}
