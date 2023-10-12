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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 湖南小微纳克网络科技有限公司
 */
@Component("apiSender_10061")
public class XiaoWeiNaKeApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(XiaoWeiNaKeApi.class);

    private static final String checkUrl = "http://data.xiaoweiws.com/data/api.hit/duplicate?mobile=";

    private static final String sendUrl = "http://data.xiaoweiws.com/data/api.datasource/add_encrypt";

    private static final String code = "dfkjtg01";

    private static final String key = "TQlaJ8al3wXSNotP";

    private static final String flag = "xwdftg";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【小微纳克】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【小微纳克】分发未知异常："+e.getMessage()));
            return new SendResult(false,"");
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws Exception {
        isHaveAptitude(po);

        String checkResult = HttpUtil.getForObject(checkUrl+ MD5Util.getMd5String(po.getMobile()));
        log.info("【小微纳克】撞库结果：{}",checkResult);
        //{ "code": "200", "message": "请求成功" }
        int resultCode = JSONUtil.toJSON(checkResult).getIntValue("code");
        if(200 != resultCode){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【小微纳克】分发重复："+checkResult));
            return new SendResult(false,checkResult);
        }
        if(org.apache.commons.lang3.StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isEmpty(po.getName())) {
                if(StringUtils.isEmpty(parse.get("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        JSONObject data = new JSONObject();
        data.put("code",code);
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("money", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("age",(null == po.getAge() || po.getAge() <=0)?30:po.getAge());
        data.put("sex",(null == po.getGender() || po.getGender() <=0)?0:1);
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("fund",po.getPublicFund().contains("有，")?1:0);
        data.put("social",0);
        data.put("credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("webank",0);
        data.put("tax",0);
        data.put("work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("city",po.getCity());
        data.put("flag",flag);

        String encryptData = AESUtil.java_openssl_encrypt(data.toJSONString(),key);
        data.clear();
        data.put("data",encryptData);
        String result = HttpUtil.postForJSON(sendUrl,data);
        JSONObject resultJson = JSONUtil.toJSON(result);
        //{"code":500,"status":"success","msg":"添加成功"}
        if(500 == resultJson.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【小微纳克】分发成功："+result));
            return new SendResult(true,result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【小微纳克】分发失败："+result ));
        return new SendResult(false,checkResult);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("海燕");
//        po.setMobile("13671948202");
//        po.setCity("上海市");
//        po.setLoanAmount("《3-10万》");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setUpdateDate(new Date());
//        XiaoWeiNaKeApi api = new XiaoWeiNaKeApi();
//        System.out.println(api.send(po,null));
//    }
}
