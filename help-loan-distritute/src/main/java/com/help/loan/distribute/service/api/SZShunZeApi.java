package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.CustomerPO;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import com.help.loan.distribute.util.DESUtil;
import com.help.loan.distribute.util.DisConstant;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 苏州顺泽
 */
@Component("apiSender_20058")
public class SZShunZeApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(SZShunZeApi.class);

    private static final String checkUrl = "https://crm.dazhii.com/api/blade-api/clue/validate";

    private static final String sendUrl = "https://crm.dazhii.com/api/blade-api/clue/push";

    private static final String tenantId = "434868";

    private static final String channelCode = "50061075";

    private static final String cluePoolCode = "02253509";

    private static final String code = "DZSCRM";

    private static final Logger LOG = LoggerFactory.getLogger(SZShunZeApi.class);

    private static final String URL = "http://shunze.bangzheng100.com/crm/import/customer";

    private static String KEY = "761d1e6364684f4fa72108ab79dffbbc";

    private static Long channelId = 29l;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            String type = po.getCreateBy();
            if(JudgeUtil.in(type, DisConstant.User.Type.HOUSE)){
                return sendResult2(po,select);
            }
            return new SendResult(false,"[苏州顺泽]只接收房抵客户");
        }catch (Exception e){
            log.error("[苏州顺泽]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【苏州顺泽】分发未知异常："+e.getMessage()));
            return new SendResult(false,"[苏州顺泽]分发异常:"+e.getMessage());
        }
    }

    private SendResult sendResult2(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);
        CustomerPO customerPO = new CustomerPO();
        customerPO.setName(po.getName());
        customerPO.setMobile(po.getMobile());
        customerPO.setChannel(channelId);
        if(StringUtils.isNotBlank(po.getChannel()) && po.getChannel().contains("ttt")){
            customerPO.setMedia("头条");
        }else{
            customerPO.setMedia("微信朋友圈");
        }
        customerPO.setCity(po.getCity());
        customerPO.setAge(po.getAge());
        customerPO.setSex(po.getGender().byteValue());
        customerPO.setNeed(LoanAmountUtil.transform(po.getLoanAmount()).toString());

        if(po.getPublicFund().contains("有，"))
            customerPO.setField2("有");
        else customerPO.setField2("无");

        if(JudgeUtil.in(po.getHouse(),1,2)){
            customerPO.setField3("有");
        }else customerPO.setField3("无");

        if(JudgeUtil.in(po.getCar(),1,2)){
            customerPO.setField4("有");
        }else customerPO.setField4("无");

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            customerPO.setField5("有");
        }else  customerPO.setField5("无");

        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            customerPO.setField6("有");
            customerPO.setField9("有");
        }else {
            customerPO.setField6("无");
            customerPO.setField9("无");
        }

        if(JudgeUtil.in(po.getCompany(),1)){
            customerPO.setField7("有");
        }else customerPO.setField7("无");
        customerPO.setRemark(getContent2(po));

        String content = DESUtil.encrypt(KEY,customerPO.toString());

        JSONObject data = new JSONObject();
        data.put("channelId",channelId);
        data.put("data",content);
        String result = HttpUtil.postForJSON(URL,data);
        JSONObject json = JSONUtil.toJSON(result);
        LOG.info("【苏州顺泽】发送结果：{}",result);
        int code = json.getIntValue("code");
        if(200 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【苏州顺泽】分发成功："+result));
            return new SendResult(true,"【苏州顺泽】分发成功："+result);
        }else if(601 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【苏州顺泽】发送重复："+result));
            return new SendResult(false,"【苏州顺泽】发送重复："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【苏州顺泽】发送失败："+result));
        return new SendResult(false,"【苏州顺泽】发送失败："+result);
    }

    public String getContent2(UserAptitudePO po){
        StringBuffer content = new StringBuffer();
        Integer amount = LoanAmountUtil.transform(po.getLoanAmount());
        content.append("贷款金额：").append(amount);
        if(po.getPublicFund().contains("有，"))
            content.append("，").append(po.getPublicFund());
        if(JudgeUtil.in(po.getHouse(),1,2))
            content.append("，").append("有本地商品房");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2))
            content.append("，").append("有银行代发工资");
        if(JudgeUtil.in(po.getCar(),1,2))
            content.append("，").append("有车");
        if(JudgeUtil.in(po.getInsurance(),1,2))
            content.append("，").append("有商业保单");
        if(1 == po.getCompany())
            content.append("，").append("有公司营业执照");
        return content.toString();
    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("requestTime",""+System.currentTimeMillis());
        data.put("tenantId",tenantId);
        data.put("code",code);
        data.put("md5Code",MD5Util.getMd5String(po.getMobile()));
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        String checkResult = HttpUtilForJava.doPost(checkUrl,data,headers);
//        String checkResult = HttpUtil.postForObject(checkUrl,data);
        log.info("[苏州融度信息]撞库结果 : {}",checkResult);
        JSONObject json = JSONUtil.toJSON(checkResult);
        if("duplicate".equals(json.getString("data"))){
            log.info("[苏州顺泽]撞库-重复:{}",checkResult);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【苏州顺泽】撞库重复："+checkResult));
            return new SendResult(false,"[苏州顺泽]撞库结果-重复: "+checkResult);
        }
        data.clear();
        data.put("tenantId",tenantId);
        data.put("channelCode",channelCode);
        data.put("cluePoolCode",cluePoolCode);
        data.put("name",po.getName());
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("phoneNumber",po.getMobile());
        data.put("loanAmount",LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("sex",po.getGender());
        data.put("age",po.getAge()<=25?25:po.getAge());
        String noncestr = (DateUtil.to10()+"").substring(0,6);
        data.put("noncestr", noncestr);
        String timestamp = System.currentTimeMillis()+"";
        data.put("timestamp",timestamp);
        data.put("signature",MD5Util.getMd5String(tenantId+po.getMobile()+noncestr+timestamp));

        JSONObject exdFields = new JSONObject();
        exdFields.put("car", JudgeUtil.in(po.getCar(),1,2)?1:2);
        exdFields.put("house", JudgeUtil.in(po.getHouse(),1,2)?1:2);
        exdFields.put("socialInsurance",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:2);
        exdFields.put("houseFund",po.getPublicFund().contains("有，")?1:2);
        exdFields.put("lifeInsurance", JudgeUtil.in(po.getCar(),1,2)?1:2);
        if(po.getCompany() == 1){
            exdFields.put("profession",3);
        }else if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            exdFields.put("profession",1);
        }else{
            exdFields.put("profession",5);
        }
        exdFields.put("salaryMethod",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:3);
        exdFields.put("creditCard",JudgeUtil.in(po.getCreditCard(),1,2)?1:2);
        exdFields.put("particulateLoan",1);

        data.put("exdFields",exdFields);
        String sendResult = HttpUtil.postForJSON(sendUrl,data);
        log.info("【苏州顺泽】分发结果：{}",sendResult);
        int code = JSONUtil.toJSON(sendResult).getIntValue("code");
        if(200==code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【苏州顺泽】http表单分发成功："+sendResult));
            return new SendResult(true,"【苏州顺泽】分发成功：{}"+sendResult);
        }else if(301 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【苏州顺泽】分发重复："+sendResult));
            return new SendResult(false,"【苏州顺泽】分发重复：{}"+sendResult);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【苏州顺泽】分发失败："+sendResult));
        return new SendResult(false,"【苏州顺泽】分发失败：{}"+sendResult);
    }

    public static class CryptAES {

        private static final String AESTYPE ="AES/ECB/PKCS5Padding";

        public static String AES_Encrypt(String keyStr, String plainText) {
            byte[] encrypt = null;
            try{
                Key key = generateKey(keyStr);
                Cipher cipher = Cipher.getInstance(AESTYPE);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                encrypt = cipher.doFinal(plainText.getBytes());
            }catch(Exception e){
                e.printStackTrace();
            }
            return new String(Base64.encodeBase64(encrypt));
        }

        public static String AES_Decrypt(String keyStr, String encryptData) {
            byte[] decrypt = null;
            try{
                Key key = generateKey(keyStr);
                Cipher cipher = Cipher.getInstance(AESTYPE);
                cipher.init(Cipher.DECRYPT_MODE, key);
                decrypt = cipher.doFinal(Base64.decodeBase64(encryptData));
            }catch(Exception e){
                e.printStackTrace();
            }
            return new String(decrypt).trim();
        }

        private static  Key generateKey(String key)throws Exception{
            try{
                SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
                return keySpec;
            }catch(Exception e){
                e.printStackTrace();
                throw e;
            }

        }
    }


//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("张先森测试3");
//        po.setMobile("13632965529");
//        po.setCity("苏州市");
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
//        po.setUpdateDate(new Date());
//        ApiSender api = new SZShunZeApi();
//        System.out.println(api.send(po,null));
//
//    }
}
