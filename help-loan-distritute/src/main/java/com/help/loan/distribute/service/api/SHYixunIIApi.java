package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.DistributeConstant;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
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
 * 上海宜寻网络科技有限公司   13515688108  1131225728@qq.com
 */
@Component("apiSender_20046")
public class SHYixunIIApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SHYixunIIApi.class);

    private static final String sendUrl = "http://fenfa.fuzejinfu.com/api/add_kh";

    private static String accountKey = "4FatRiOXGEEciZCSKsWipdCWCRzYPu";

    private static String venderName = "宜寻中赢头条";

    private static String channel = "宜寻中赢头条";


    @Autowired
    DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po, select);
        } catch (Exception e) {
            log.error("[上海宜寻]分发异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "【上海宜寻】分发未知异常：" + e.getMessage()));
            return new SendResult(false, "[上海宜寻]分发异常:" + e.getMessage());
        }
    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select) {
        isHaveAptitude(po);
        if(JudgeUtil.in(po.getType(), DistributeConstant.LoanType.HOUSE)){
            accountKey = "yoP9fv4WuaXiaychaYB1YYrKS1Aybz";
            venderName = "宜寻中赢BD";
            channel = "宜寻中赢BD";
        }else{
            accountKey = "4FatRiOXGEEciZCSKsWipdCWCRzYPu";
            venderName = "宜寻中赢头条";
            channel = "宜寻中赢头条";
        }
        JSONObject data = new JSONObject();
        data.put("account_key", accountKey);
        data.put("phone", po.getMobile());
        data.put("name", po.getName());
        data.put("city", po.getCity());
        data.put("loan_amount", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("channel", channel);
        data.put("vender_name",venderName);
        data.put("remark",getContent(po));
        data.put("car_message", !JudgeUtil.in(po.getCar(), 1, 2) ? 1 : 2);
        data.put("house_message",!JudgeUtil.in(po.getHouse(), 1, 2) ? 1 : 2);
        data.put("house_type",2);
        data.put("company_nature",!JudgeUtil.in(po.getCompany(),1)?1:3);
        data.put("pay_price",!JudgeUtil.in(po.getGetwayIncome(),1,2)?1:3);
        data.put("pay_shebao",!JudgeUtil.in(po.getGetwayIncome(),1,2)?1:2);

        String sendResult = HttpUtil.postFormData(sendUrl, data);
        log.info("【上海宜寻】分发结果：{}", sendResult);
        int code = JSONUtil.toJSON(sendResult).getIntValue("code");
        if (1 == code) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "【上海宜寻】http表单分发成功：" + sendResult));
            return new SendResult(true, "【上海宜寻】分发成功：{}" + sendResult);
        } else if (-1 == code) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "【上海宜寻】分发重复：" + sendResult));
            return new SendResult(false, "【上海宜寻】分发重复：{}" + sendResult);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "【上海宜寻】分发失败：" + sendResult));
        return new SendResult(false, "【上海宜寻】分发失败：{}" + sendResult);
    }




//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试请忽略");
//        po.setMobile("13632965539");
//        po.setCity("上海市");
//        po.setLoanAmount("5000000");
//        po.setCompany(0);
//        po.setPublicFund("公积金有，");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setType(DistributeConstant.LoanType.HOUSE);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setChannel("112233_test");
//        po.setUpdateDate(new Date());
//        ApiSender api = new SHYixunIIApi();
//        System.out.println(api.send(po,null));
//
//    }


}
