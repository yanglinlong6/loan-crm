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

/**
 * 51公积金贷:apiSender_10030
 */
@Component("apiSender_10030")
public class PublicFund51Api implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(PublicFund51Api.class);

    /**推荐产品，申请产品跳转链接模板*/
    private static final String APPLY_LENDER_URL_MODEL = "http://%s/apply/middle?uuid=%s&lenderId=%s&companyId=%s";

    /**中国银行产品id：887a786c6cc911ea886bb8599f49ec84*/
    private static final String lenderId = "887a786c6cc911ea886bb8599f49ec84";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            return send2(po,select);
        }catch (Exception e){
            LOG.error("【51公积金贷】分发异常-{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【51公积金贷】分发异常："+e.getMessage()));
            return new SendResult(false,"【51公积金贷】分发异常："+e.getMessage());
        }
    }


    public SendResult send2(UserAptitudePO po, UserDTO select) {

        if(null == select){
            return new SendResult(false,"不是公众号用户");
        }

        if(!po.getPublicFund().contains("有，"))
            return new SendResult(false,"【51公积金贷】没有公积金【资质不符合】");
        String response = WechatCenterUtil.getWechat("","",po.getUserId());
        JSONObject wechat = JSON.parseObject(response);

        String url = String.format(APPLY_LENDER_URL_MODEL,wechat.getJSONObject("o").getString("domain2"),po.getUserId(),lenderId,48);
        String content = "经过大数据匹配，为您推荐\n【51公积金贷】有公积金可贷\n点击下面链接马上申请\n\n\uD83D\uDC49<a href='"+url+"'>51公积金贷->下款率99%</a>";
        JSONObject custmerMsg = new JSONObject();
        custmerMsg.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT);
        custmerMsg.put("touser",po.getUserId());
        JSONObject text = new JSONObject();
        text.put("content",content);
        custmerMsg.put("text", text);
        String wechatId = wechat.getJSONObject("o").getString("wechatId");
        String result = WechatCenterUtil.sendCustMsg(custmerMsg,wechatId,"","");
        int status;
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
        return new SendResult(false,result);//一直返回false，表示会一直选择下一个机构分发
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3e491487931543d7ab1f088e254f1812");
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
//        PublicFund51Api api = new PublicFund51Api();
//        System.out.println(api.send(po,null));
//
//
//    }
}
