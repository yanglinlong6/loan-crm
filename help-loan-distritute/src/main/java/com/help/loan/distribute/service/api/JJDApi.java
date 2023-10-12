package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.RSAUtils;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.util.*;

/**
 * 北京兴易融信息技术有限公司： 15600050752  北京市东城区东四十条南新仓商务大厦B座723
 */
@Component("apiSender_20088")
public class JJDApi implements ApiSender {

    private static final String JJD_URL = "http://39.105.186.215:8080/jjd/api";

    private static final String JJD_URL_WUHAN = "http://www.jiejie888.com:8080/jjd/api";

    private static final String JJD_URL_KM = "http://123.57.137.109:8080/jjd/api";

//    private static final String JJD_URL_BEIJING = "http://39.105.186.215:8080/jjd/api";

    private static final String JJD_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKZYSL5n3dlyMmpZ\n" +
            "ytyhCT7KDQ90ITazhNuivBAszDWA6/GgB8s2S517eIB7sMEh06hSauBQgpLQxS2e\n" +
            "63MyqIV23kJY9TV2enzF1udLy8u0rPu9D63i5eCddKBjpWoFbKOXNQV2eHGZZ6Qi\n" +
            "mKnk3EST1fRBdHu6BO8xbn/9bZLDAgMBAAECgYAvtdCfdBEIZh2ZrUlnAHUzYiZe\n" +
            "0VMZamHn0QQZq4+ipOiHc/voSBlmANzuFQv8SOR5ObJXxh+9Vk3HqPYlI1l7/9y3\n" +
            "2yS113SDWJxY1nnxiyfBoakt8qNM3i0hV/5iPpVA4WWeTw40ExjPcc0qMontVO+X\n" +
            "QAMnLcfdg4icE8rfMQJBAM61W4VdUjUvZb0K91AZKgC+Fovc82PNU1c10txmz8NH\n" +
            "wfU2Twqfv86PaMEhZ8m0wxuLP6ukYys8Y7ry5Ta8onsCQQDOAuhvfAli9IP1gJ3/\n" +
            "gCGj5jkGuPN0VVdPBB/QVC+NbJf/oj4EtwatPPO/wcFXMxxuUtjfRPIuBLXanAYJ\n" +
            "EmJZAkA8v1eSLgbG0pMXp21fAMYzSanemKR+pAzmCE9brmtIfIInyjoYaTI5RjQG\n" +
            "7gZOZO0h3x5rFvh98FVuz4tFNtKZAkBrL1Qj/RMdKz8lEqAh+VaHYhc1ijEQy/47\n" +
            "HFg5GjQLjv6egJln9ppAtD/V+0dOyUb3l59CuyqOCuFbNmZlfe0xAkA2qdnojz4Z\n" +
            "4CXF9QGmvZeVXDo5XKMTS7ZocEIX1D+MO+P/slWHpHGgAceHNMz0P8WHSGD27rAY\n" +
            "nVwscglKAyeJ";

    private static final String JJD_PHONE_MATCH_API = "jjd.api.phone.match";

    private static final String JJD_CUST_IMPORT_API = "jjd.api.customer.import";

    private static final String JJD_FORMAT = "json";

    private static final String JJD_SIGN_TYPE = "RSA";

    private static final String JJD_UID = "8bff60dc966645ee9637540a3ace59bc";

    private static final String JJD_SOURCE = "daof";

    private static final String JJD_SUCCESS_CODE = "1";

    private static Logger log = LoggerFactory.getLogger(JJDApi.class);

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[北京兴易融]推送异常",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京兴易融]推送异常"+e.getMessage()));
            return new SendResult(false,"[北京兴易融]推送异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        JSONObject o = new JSONObject();
        o.put("phone", po.getMobile());
        String sendJJDApi = sendJJDApi(o, JJD_PHONE_MATCH_API,po);
        JSONObject parseObject = JSON.parseObject(sendJJDApi);
        SendResult result = new SendResult();
        if(JJD_SUCCESS_CODE.equals(parseObject.getString("code"))) {
            if(StringUtils.isEmpty(po.getName()) && null != select){
                String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
                JSONObject parse = JSON.parseObject(userInfo);
                if(StringUtils.isEmpty(po.getName())) {
                    if(StringUtils.isEmpty(parse.get("openid"))) {
                        po.setName("公众号用户");
                    } else {
                        po.setName(EmojiFilter.filterEmoji(parse.getString("nickname")));
                    }
                }
            }
            o.put("username", po.getName());
            o.put("source", JJD_SOURCE);
            String sex = "1";
            if(po.getGender() == null) {
                sex = "1";
            } else if(po.getGender().equals(1)) {
                sex = "1";
            } else if(po.getGender().equals(2)) {
                sex = "2";
            }
            o.put("sex", sex);
            o.put("amount", LoanAmountUtil.transformToWan(po.getLoanAmount()));
            o.put("customerIp", "0");
            o.put("cityName", po.getCity());
            if(po.getOccupation() != null && po.getOccupation() != 0) {
                if(po.getOccupation() == 1 || po.getOccupation() == 3) {
                    o.put("identityType", 1);
                } else if(po.getOccupation() == 2) {
                    o.put("identityType", 2);
                } else if(po.getOccupation() == 4) {
                    o.put("identityType", 4);
                }
            }
            o.put("houseInfo", JudgeUtil.in(po.getHouse(),1,2)?3:1);
            o.put("carInfo", JudgeUtil.in(po.getCar(),1,2)?2:3);
            o.put("isHasPublicFund", po.getPublicFund().contains("有，")?2:1);
            o.put("warranty", JudgeUtil.in(po.getInsurance(),1,2)?1:3);
            o.put("hasCompany",JudgeUtil.in(po.getCompany(),1)?2:1);
            Integer wages = 2;
            if(po.getGetwayIncome().intValue() ==1) {
                wages = 1;
                o.put("cashIncome", 8000);
            } else if(po.getGetwayIncome().intValue() ==2) {
                wages = 1;
                o.put("cashIncome", 4000);
            }
            o.put("workerWagesPay", wages);

            String addCust = sendJJDApi(o, JJD_CUST_IMPORT_API,po);;
            JSONObject parseObject2 = JSON.parseObject(addCust);
            if(JJD_SUCCESS_CODE.equals(parseObject2.getString("code"))) {
                log.info("send JJD SUCCESS userid :{}" ,po.getUserId());
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京兴易融]"+sendJJDApi));
                result.setSuccess(true);
            } else {
                log.info("send JJD failed userid = {}, result = -{}" ,po.getUserId(), addCust);
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京兴易融]"+sendJJDApi));
            }
            result.setResultMsg(addCust);
        } else {
            log.info("send JJD failed mobile repeat");
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京兴易融]"+sendJJDApi));
            result.setResultMsg(sendJJDApi);
        }
        return result;
    }

    private static String sendJJDApi(JSONObject o, String method,UserAptitudePO po) {

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        String data = "";
        try {
            PrivateKey pk = RSAUtils.getPrivateKey(JJD_PRIVATE_KEY);
            data = RSAUtils.encrypt(o.toJSONString(), pk);
        } catch(Exception e) {
            e.printStackTrace();
        }
        params.add("data", data);
        params.add("method", method);
        params.add("dataEnc", "1");
        params.add("uid", JJD_UID);
        params.add("signType", JJD_SIGN_TYPE);
        params.add("format", JJD_FORMAT);
        params.add("timestamp", String.valueOf(new Date().getTime() / 1000));
        String sign = "";
        try {
            sign = RSAUtils.sign(paramSplicing(params), RSAUtils.getPrivateKey(JJD_PRIVATE_KEY));
        } catch(Exception e) {
            log.error("[北京兴易融]分发，加密异常：{}",e.getMessage(),e);
        }
        params.add("sign", sign);

//        System.out.println(JSONUtil.toJsonString(params));

        String url;
        if("昆明市".equals(po.getCity())){
            url = JJD_URL_KM;
        }else if("武汉市".equals(po.getCity()))
            url = JJD_URL_WUHAN;
        else
            url = JJD_URL;
        return HttpUtil.postFormForObject(url, params);
    }

    private static String paramSplicing(LinkedMultiValueMap<String, Object> paramsMap) {
        Set<String> keySet = paramsMap.keySet();
        List<String> sortList = new ArrayList<String>(keySet);
        sortList.sort(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        Iterator<String> iterator = sortList.iterator();
        StringBuilder builder = null;
        while(iterator.hasNext()) {
            String next = iterator.next();
            if(builder == null) {
                builder = new StringBuilder();
            } else {
                builder.append("&");
            }
            builder.append(next);
            builder.append("=");
            builder.append(paramsMap.get(next).get(0));
        }
        String result = "";
        if(builder != null) {
            result = builder.toString();
        }
        return result;
    }

    public static void main(String[] args) {
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("fdf8bb58825343cb9f95a642860356e3");
        po.setName("伍散人-测试");
        po.setMobile("13049692811");
        po.setCity("北京市");
        po.setLoanAmount("50000");
        po.setCompany(1);
        po.setPublicFund("有，个人月缴300-500元");
        po.setCar(3);
        po.setHouse(4);
        po.setInsurance(1);
        po.setGetwayIncome(1);
        po.setOccupation(0);
        po.setChannel("ttt-zxf-001");
        JJDApi api = new JJDApi();
        SendResult send = api.send(po, null);
        System.out.println(JSON.toJSONString(send));
    }

}
