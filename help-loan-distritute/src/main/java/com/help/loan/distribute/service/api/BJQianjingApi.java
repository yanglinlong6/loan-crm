package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
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
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.*;

/**
 * 北京钱景
 */
@Component("apiSender_20083")
public class BJQianjingApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(BJQianjingApi.class);

    private static final String venderName = "daofen";

    private static final  int appid = 43013;

    private static final String appkey = "IYe3fpODTUnUGNz8NuYj5i9RDUUY3Z74yNKf";

    private static final String checkUrl = "http://114.55.27.220:8009/HitLibrary/checkTel";

    private static final String url = "http://114.55.27.220:8009/customer/import";

    @Autowired
    DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            JSONObject data = new JSONObject();
            data.put("MobilePhone", MD5Util.getMd5String(po.getMobile()));
            data.put("appId", appid);
            data.put("appKey", appkey);
            Map<String, String> httpHeader = new HashMap<>();
            httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            //{"resStatus":0,"resMsg":"appid error"}
            // {"resStatus":2,"resMsg":"tel duplicate"}
            // {"resStatus":1,"resMsg":"tel validity"}
            String result = HttpClientProxy.doPost(checkUrl, data, "UTF-8", 3000, httpHeader);
            JSONObject json = JSONUtil.toJSON(result);
            String code = json.getString("Code");
            String msg = json.getString("Msg");
            if(!"100".equals(code)){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【北京前景】撞库:"+msg));
                return new SendResult(false,"【北京前景】撞库："+result);
            }
            return send2(po,select);
        }catch (Exception e){
            LOG.error("[北京前景]发送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京前景】:"+e.getMessage()));
            return new SendResult(false,e.getMessage());
        }

    }


    private SendResult send2(UserAptitudePO po, UserDTO select) throws Exception {
        isHaveAptitude(po);

        Map<String,Object> data = new HashMap<>();
        data.put("channel",venderName);
        data.put("vender_name",venderName);
        data.put("name",po.getName());
        data.put("city",po.getCity().substring(0,po.getCity().length()-1));
        if(JudgeUtil.in(po.getGender(),1))
            data.put("sex","男");
        else data.put("sex","女");
        data.put("telphone",po.getMobile());
        data.put("birthday","1980-01-01");
        if(JudgeUtil.in(po.getCompany(),1))
            data.put("vocation","小企业主");
        else data.put("vocation","工薪族");
        if(JudgeUtil.in(po.getGetwayIncome(),2))
            data.put("salary","6000-9999");
        else if (JudgeUtil.in(po.getGetwayIncome(),1))
            data.put("salary","3000-3999");
        else data.put("salary","0-1999");
        data.put("loan_amount", Integer.valueOf(LoanAmountUtil.transformToWan(po.getLoanAmount())));
        data.put("loan_time",12);
        data.put("have_loan","N");
        data.put("have_credit_card", JudgeUtil.in(po.getCreditCard(),1,2)?"Y":"N");
        data.put("have_house",JudgeUtil.in(po.getHouse(),1,2)?"Y":"N");
        data.put("have_fangdai","N");
        data.put("have_car",JudgeUtil.in(po.getCar(),1,2)?"Y":"N");
        data.put("have_chedai","N");
        data.put("shebao","N");
        data.put("have_fund",po.getPublicFund().contains("有，")?"Y":"N");
        data.put("have_baoxian",JudgeUtil.in(po.getInsurance(),1,2)?"Y":"N");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2))
            data.put("salary_method",1);
        else data.put("salary_method",0);
        data.put("has_wld_loan","N");

        data.put("appid",appid);
        data.put("appkey",appkey);
        data.put("timestamp", DateUtil.to10());
        data.put("noncestr", UUID.randomUUID().toString().replaceAll("-","").replaceAll("_",""));
        Set<String> keySet = data.keySet();
        List<String> keyList = new ArrayList<>(keySet);
        keyList.sort((o1,o2) -> o1.compareTo(o2));
        StringBuffer signString = new StringBuffer();
        for(int i=0;i<keyList.size();i++){
            String key = keyList.get(i);
            if(i == 0)
                signString.append(key).append("=").append(data.get(key));
            else
                signString.append("&").append(key).append("=").append(data.get(key));
        }
        data.put("signature",sha1(signString.toString()));

//        String data2 = encrypt(JSONUtil.toJsonString(data),appkey);
//        data.clear();
//        data.put("appid",appid);
//        data.put("data",data2);

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        String result = HttpClientProxy.doPost(url, data, "UTF-8", 3000, httpHeader);
        JSONObject resultJson = JSONUtil.toJSON(result);
        int status = resultJson.getIntValue("resStatus");
        String resMsg = resultJson.getString("resMsg");
        if(1==status){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【北京前景】成功:"+resMsg));
            return new SendResult(true,resMsg);
        }else if(2 == status){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【北京前景】重复:"+resMsg));
            return new SendResult(false,"【北京前景】重复："+resMsg);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京前景】:"+resMsg));
        return new SendResult(false,"【北京前景】失败："+resMsg);
    }

    public String sha1(String data) throws  Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA");
        byte[] byteArray = data.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    // 加密
    public static String encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

        return Base64.getEncoder().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("517135ba6a244cd0b1ac9210f495cd18");
//        po.setName("伍测试3请忽略");
//        po.setHouse(1);
//        po.setAge(35);
//        po.setCity("北京市");
//        po.setCompany(0);
//        po.setInsurance(1);
//        po.setLoanAmount("100000");
//        po.setMobile("13410567333");
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setOccupation(1);
//        po.setGetwayIncome(1);
//        po.setCreditCard(0);
//        po.setCar(1);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new BJQianjingApi();
//        System.out.println(api.send(po,null));
//
//    }
}
