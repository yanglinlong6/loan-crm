package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AESUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Date;

/**
 * 常州腾汇商务信息咨询有限公司
 */
@Component("apiSender_20110")
class CHangZhouTenghuiIIApi implements ApiSender{

    private static final Logger LOG = LoggerFactory.getLogger(CHangZhouTenghuiIIApi.class);

    private static final String checkUrl = "https://thcrm.netbianli.com/api/CustomerPush/pushV1CheckRepeat";

    private static final String sendUrl = "https://thcrm.netbianli.com/api/CustomerPush/pushV1";

    private static final String KEY = "b6BjUmWG3tUgc13cVjgladqJEDst9stH";

    private static final int providerAPIId = 14;


    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po, select);
        } catch (Exception e) {
            LOG.error("[常州腾汇]推送异常：{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[常州腾汇]推送未知异常：" + e.getMessage()));
            return new SendResult(false, "[常州腾汇]推送异常:" + e.getMessage());
        }
    }



    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws Exception {


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("User-Agent","Apifox/1.0.0 (https://www.apifox.cn)");
        httpHeaders.add("Content-Type","application/json");

        JSONObject tel = new JSONObject();
        tel.put("tel", po.getMobile());

        JSONObject data = new JSONObject();
        data.put("data",encrypt(tel.toJSONString(),KEY));
        data.put("providerAPIId",providerAPIId);

        String result = HttpUtil.postForJSON(checkUrl,data,httpHeaders); // {"status":true,"code":0}
        LOG.info("[常州腾汇]撞库验证结果:{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        if(1 == resultJSON.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[常州腾汇]撞库重复：" +result));
            return new SendResult(false,"[常州腾汇]撞库重复");
        }

        data.clear();
        isHaveAptitude(po);

        data.put("cname",po.getName());
        data.put("tel",po.getMobile());
        data.put("age", (null == po.getAge() || po.getAge() < 25)?25:po.getAge());
        data.put("sex",po.getGender());
        data.put("ed", Integer.valueOf(LoanAmountUtil.transformToWan(po.getLoanAmount())));
        data.put("fc",JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("cc",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("wld",0);
        data.put("gjj",JudgeUtil.contain(po.getPublicFund(),"有，","1")?1:0);
        data.put("bd",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("zmf",0);
        data.put("sr",0);
        data.put("city","江苏,"+(po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity()));
        data.put("remarks",getContent(po));

        JSONObject data2 = new JSONObject();
        data2.put("data",encrypt(data.toJSONString(),KEY));
        data2.put("providerAPIId",providerAPIId);


        result = HttpUtil.postForJSON(sendUrl,data2,httpHeaders);
        LOG.info("[常州腾汇]推送结果:{}",result);
        resultJSON = JSONUtil.toJSON(result);
        String msg = resultJSON.getString("msg");
        int code = resultJSON.getIntValue("code");
        if(0 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[常州腾汇]推送成功：" +msg));
            return new SendResult(true,"[常州腾汇]推送成功:"+msg);
        }
        if(2 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[常州腾汇]推送重复：" +msg));
            return new SendResult(false,"[常州腾汇]推送重复:"+msg);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[常州腾汇]推送失败：" +msg));
        return new SendResult(false,"[常州腾汇]推送失败:"+msg);
    }

    /**
     * AES加密
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    public static String encrypt(String input, String key) throws Exception {

        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skey);
        byte[] crypted = cipher.doFinal(input.getBytes("UTF8"));
        return new String(Base64.encodeBase64(crypted));
    }


//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试09");
//        po.setMobile("13059692921");
//        po.setCity("常州市");
//        po.setLoanAmount("50");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setCreateBy("house");
//        po.setHouseExtension(5);
//        po.setUpdateDate(new Date());
//        ApiSender api = new CHangZhouTenghuiIIApi();
//        System.out.println(api.send(po,null));
//    }
}
