package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.WechatConstants;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("apiSender_10023")
public class ChinaBankApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(ChinaBankApi.class);

    /**推荐产品，申请产品跳转链接模板*/
    private static final String APPLY_LENDER_URL_MODEL = "http://%s/apply/middle?uuid=%s&lenderId=%s&companyId=%s";

    /**中国银行产品id：cae5032500834205ab0e8b6f23b80f13*/
    private static final String lenderId = "cae5032500834205ab0e8b6f23b80f13";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            LOG.error("【中国银行】分发异常-{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,e.getMessage()));
            return new SendResult(false,"【中国银行】分发异常："+e.getMessage());
        }
    }


    public SendResult send2(UserAptitudePO po, UserDTO select) {
        int status;
//        if(!JudgeUtil.in(po.getCompany(),1)){
//            status = 2;
//            return new SendResult(false,"没有营业执照不符合");
//        }
//        if(null == select){
//            return new SendResult(false,"不是公众号用户");
//        }
        String response = WechatCenterUtil.getWechat("","",po.getUserId());
        JSONObject wechat = JSON.parseObject(response);

        String url = String.format(APPLY_LENDER_URL_MODEL,wechat.getJSONObject("o").getString("domain2"),po.getUserId(),lenderId,46);
        String content = "经过大数据匹配，为您推荐\n【中国银行-小微贷】助力小微企业\n<a href='weixin://bizmsgmenu?msgmenucontent=点击下面链接马上申请&msgmenuid=9999'>点击下面链接马上申请</a>\n\n\uD83D\uDC49<a href='"+url+"'>中国银行-小微贷款->年利率低至5%</a>";
        JSONObject custmerMsg = new JSONObject();
        custmerMsg.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT);
        custmerMsg.put("touser",po.getUserId());
        JSONObject text = new JSONObject();
        text.put("content",content);
        custmerMsg.put("text", text);
        String wechatId = wechat.getJSONObject("o").getString("wechatId");
        String result = WechatCenterUtil.sendCustMsg(custmerMsg,wechatId,"","");
        if(JSONUtil.isJsonString(result)){
            JSONObject jsonObject = JSONObject.parseObject(result);
            if(0 == jsonObject.getIntValue("errcode")){
                status = 1;
            }else {
                status = 2;
            }
        }else{
            status = 2;
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),status,result));
        return new SendResult(true,result);
    }


//    public static void main(String[] args){

//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("9e7710ddd0dc49c59dc3468214be44d1");
//        po.setName("测试林先森");
//        po.setHouse(0);
//        po.setCity("上海市");
//        po.setCompany(1);
//        po.setGetwayIncome(0);
//        po.setInsurance(0);
//        po.setLoanAmount("《10-30万》");
//        po.setMobile("13632965140");
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴300-500元");
//        po.setCreditCard(1);
//        po.setCar(1);
//        po.setUpdateDate(new Date());
//        ChinaBankApi api = new ChinaBankApi();
//        System.out.println(api.send(po,null));


//        JSONObject wechat = JSON.parseObject(WechatCenterUtil.getWechat("","","9e7710ddd0dc49c59dc3468214be44d1")).getJSONObject("o");
//        String wechatId = wechat.getString("wechatId");
//        JSONObject custmerMsg2 = new JSONObject();
//        custmerMsg2.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT);
//        custmerMsg2.put("touser","9e7710ddd0dc49c59dc3468214be44d1");
//        StringBuffer strategyText = new StringBuffer();
//        strategyText.append("为您推荐【贷款攻略】\uD83D\uDC47").append("\n\n");
//        strategyText.append("\uD83D\uDC49").append("<a href='"+(WechatConstants.UrlModel.jiyongqianModel.replace("#appid#", wechat.getString("appId")).replace("#domain2#",wechat.getString("domain2")))+"'>急用钱：教我信用卡借钱</a>").append("\n\n");
//        strategyText.append("\uD83D\uDC49").append("<a href='"+String.format(WechatConstants.UrlModel.feedbackModel,wechat.getString("appId"),wechat.getString("domain2"))+"'>投诉与反馈</a>").append("\n\n");
//        JSONObject text2 = new JSONObject();
//        text2.put("content",strategyText.toString());
//        custmerMsg2.put("text", text2);
//        String result2 = WechatCenterUtil.sendCustMsg(custmerMsg2,wechatId,"","");
//        System.out.println(result2);

//    }
}
