package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 熊猫房抵贷
 */
//@Component("apiSender_10065")
public class XiongMaoApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(XiongMaoApi.class);

    private static final String checkUrl = "https://crm.dazhii.com/api/blade-api/clue/validate";

    private static final String sendUrl = "https://crm.dazhii.com/api/blade-api/clue/push";

    private static final String tenantId = "248117";

    private static final String channelCode = "62982406";

    private static final String cluePoolCode = "83194617";

    private static final String code = "DZSCRM";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[熊猫房抵贷]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【熊猫房抵贷】分发未知异常："+e.getMessage()));
            return new SendResult(false,"[熊猫房抵贷]分发异常:"+e.getMessage());
        }
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
        log.info("[熊猫房抵贷]撞库结果 : {}",checkResult);
        JSONObject json = JSONUtil.toJSON(checkResult);
        if("duplicate".equals(json.getString("data"))){
            log.info("[熊猫房抵贷]撞库-重复:{}",checkResult);
            return new SendResult(false,"[熊猫房抵贷]撞库结果-重复: "+checkResult);
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
        log.info("【熊猫房抵贷】分发结果：{}",sendResult);
        int code = JSONUtil.toJSON(sendResult).getIntValue("code");
        if(200==code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【熊猫房抵贷】http表单分发成功："+sendResult));
            return new SendResult(false,"【熊猫房抵贷】分发成功：{}"+sendResult);
        }else if(301 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【熊猫房抵贷】分发重复："+sendResult));
            return new SendResult(false,"【熊猫房抵贷】分发重复：{}"+sendResult);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【熊猫房抵贷】分发失败："+sendResult));
        return new SendResult(false,"【熊猫房抵贷】分发失败：{}"+sendResult);
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


    public static void main(String[] args){
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("张先森测试3");
        po.setMobile("13632965529");
        po.setCity("上海市");
        po.setLoanAmount("5000000");
        po.setCompany(0);
        po.setPublicFund("公积金有，");
        po.setCar(0);
        po.setHouse(0);
        po.setInsurance(0);
        po.setGetwayIncome(0);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setAge(30);
        po.setGender(1);
        po.setUpdateDate(new Date());
        ApiSender api = new XiongMaoApi();
        System.out.println(api.send(po,null));

    }
}
