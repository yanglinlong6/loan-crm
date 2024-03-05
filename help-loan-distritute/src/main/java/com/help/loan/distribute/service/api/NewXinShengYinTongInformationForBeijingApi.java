package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.DistributeConstant;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.model.CustomerPO;
import com.help.loan.distribute.service.api.utils.DESUtil;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
 * 	新北京鑫晟银通信息咨询有限公司 20151
 */
@Component("apiSender_20152")
public class NewXinShengYinTongInformationForBeijingApi implements ApiSender {
    private static final Logger LOG = LoggerFactory.getLogger(NewXinShengYinTongInformationForBeijingApi.class);

    private static final String URL = "http://111.229.170.64:1280/api/customer/Baiduleads";

    private static Long CHENNEL = 195L;

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po, select);
        } catch (Exception e) {
            LOG.error("[新北京鑫晟银通信息咨询有限公司]推送异常：{}", e.getMessage(), e);
            if (!JudgeUtil.startWith(po.getChannel(), "360")) {
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[新北京鑫晟银通信息咨询有限公司]推送异常:" + e.getMessage()));
            }
            return new SendResult(false, "[新北京鑫晟银通信息咨询有限公司]推送异常：" + e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select) {
        JSONObject data = new JSONObject();
        data.put("ucid", "52623658");
        data.put("clueId", po.getId() + "");
        data.put("name", po.getName());
        data.put("mobile", po.getMobile());
        data.put("city", po.getCity());
        data.put("need", LoanAmountUtil.transform(po.getLoanAmount()).toString());
        data.put("remark", getContent5(po, getMedia(po.getChannel()), cacheService));
        data.put("media", getMedia(po.getChannel()));
        data.put("channel", CHENNEL + "");
        data.put("age", po.getAge() + "");
        data.put("sex", po.getGender() + "");
        data.put("ip", "127.0.0.1");
        System.out.println(data.toJSONString());
        String result = HttpUtil.postForObject(URL, data);
        JSONObject json = JSONUtil.toJSON(result);
        LOG.info("[新北京鑫晟银通信息咨询有限公司]推送结果：{}", result);
        int code = json.getIntValue("code");
        if (0 == code) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[新北京鑫晟银通信息咨询有限公司]推送成功：" + result));
            return new SendResult(true, "[新北京鑫晟银通信息咨询有限公司]推送成功：" + result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[新北京鑫晟银通信息咨询有限公司]推送失败：" + result));
        return new SendResult(false, "[新北京鑫晟银通信息咨询有限公司]推送失败：" + result);
    }

    private String getContent5(UserAptitudePO po, String media, CacheService cacheService) {
        StringBuffer content = new StringBuffer();
        content.append("申请产品：").append(parseAccount(po.getChannel())).append(";");
        content.append("贷款金额：").append(LoanAmountUtil.transform(po.getLoanAmount())).append(";");
        if (po.getPublicFund().contains("有，")) content.append("有300-800公积金").append(";");
        if (JudgeUtil.in(po.getGetwayIncome(), 1, 2)) content.append("有银行代发工资").append(";");
        if (JudgeUtil.in(po.getCar(), 1, 2)) content.append("有车").append(";");
        if (JudgeUtil.in(po.getInsurance(), 1, 2)) {
            content.append("有寿险保单").append(";");
        }
        if (1 == po.getCompany()) content.append("有企业纳税").append(";");
        if (JudgeUtil.in(po.getHouse())) content.append("有本地商品房").append(";");
        if (JudgeUtil.in(po.getCreditCard(), 1)) {
            content.append("有信用卡").append(";");
        }
        return content.toString();
    }


    public static void main(String[] args) {
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("张测试请忽略");
        po.setMobile("13410567155");
        po.setCity("北京市");
        po.setAge(32);
        po.setGender(2);
        po.setLoanAmount("30000");
        po.setCompany(1);
        po.setPublicFund("无");
        po.setCar(1);
        po.setHouse(1);
        po.setInsurance(1);
        po.setGetwayIncome(1);
        po.setOccupation(1);
        po.setCreditCard(1);
        po.setChannel("zxf-ttt-1009-108=28");
        po.setType(DistributeConstant.LoanType.CREDIT);
        po.setUpdateDate(new Date());
        ApiSender api = new NewXinShengYinTongInformationForBeijingApi();
        System.out.println(api.send(po, null));
    }
}
