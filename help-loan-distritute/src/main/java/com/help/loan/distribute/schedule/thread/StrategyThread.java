package com.help.loan.distribute.schedule.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.WechatConstants;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrategyThread implements Runnable {
    private static Logger LOG = LoggerFactory.getLogger(StrategyThread.class);
    private String mobile;
    private UserDTO user;

    public StrategyThread(String mobile, UserDTO user) {
        this.mobile = mobile;
        this.user = user;
    }

    @Override
    public void run() {
        try {
            if(null == user){
                return;
            }
            Thread.sleep(5000);// 暂停2秒后再发送客服消息
            JSONObject wechat = JSON.parseObject(WechatCenterUtil.getWechat("","",user.getUserId())).getJSONObject("o");
            String wechatId = wechat.getString("wechatId");
            JSONObject custmerMsg2 = new JSONObject();
            custmerMsg2.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT);
            custmerMsg2.put("touser",user.getUserId());
            StringBuffer strategyText = new StringBuffer();
            strategyText.append("继续为您推荐【贷款攻略】").append("\n\n");
            strategyText.append("\uD83D\uDC49").append("<a href='"+(WechatConstants.UrlModel.jiyongqianModel.replace("#appid#", wechat.getString("appId")).replace("#domain2#",wechat.getString("domain2")))+"'>急用钱：教我信用卡借钱</a>").append("\n\n");
            strategyText.append("\uD83D\uDC49").append("<a href='"+String.format(WechatConstants.UrlModel.feedbackModel,wechat.getString("appId"),wechat.getString("domain2"))+"'>投诉与反馈</a>").append("\n\n");
            JSONObject text2 = new JSONObject();
            text2.put("content",strategyText.toString());
            custmerMsg2.put("text", text2);
            String result2 = WechatCenterUtil.sendCustMsg(custmerMsg2,wechatId,"","");
            LOG.info("用户-{}，推送攻略消息结果：-{}",mobile,result2);
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
