package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/*
 *  北京香鑫未来科技有限公司 20149
 */
@Component("apiSender_20149")
public class XiangXinFutureTechnologyForBeijingApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(XiangXinFutureTechnologyForBeijingApi.class);
    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResultForJinan(po, select);
        } catch (Exception e) {
            log.error("[北京香鑫未来科技有限公司]推送异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[北京香鑫未来科技有限公司]推送异常:" + e.getMessage()));
            return new SendResult(false, "[北京香鑫未来科技有限公司]推送异常:" + e.getMessage());
        }
    }

    /**
     * 北京
     *
     * @param po
     * @param select
     * @return
     */
    private SendResult sendResultForJinan(UserAptitudePO po, UserDTO select) {
        isHaveAptitude(po);
        int value = DateUtil.to10();
//        int value = 1700979562;
        Map<String, Object> data = Maps.newHashMap();
        data.put("vender_name", "yuefu");
        data.put("channel", "");
        data.put("branch", "越富");
        data.put("name", po.getName());
        data.put("city", StringUtils.isEmpty(po.getCity()) ? "" : po.getCity().replace("市", ""));
        data.put("idcard", "");
        data.put("sex", po.getGender() == 1 ? "男" : "女");
        data.put("telphone", po.getMobile());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -po.getAge());
        data.put("birthday", DateUtil.formatToString(cal.getTime(), "yyyy-MM-dd"));
        data.put("vocation", po.getOccupation() == 2 ? "小企业主" : "工薪族");
        data.put("salary", po.getGetwayIncome() == 1 ? "4000-5999" : "0-1999");
        data.put("loan_amount", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("loan_time", 12);
        data.put("have_loan", "N");
        data.put("have_credit_card", JudgeUtil.in(po.getCreditCard(), 1, 2) ? "Y" : "N");
        data.put("have_house", JudgeUtil.in(po.getHouse(), 1, 2) ? "Y" : "N");
        data.put("have_fangdai", "N");
        data.put("have_car", JudgeUtil.in(po.getCar(), 1) ? "有" : "无");
        data.put("have_chedai", "N");
        data.put("shebao", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? "有" : "无");
        data.put("have_fund", po.getPublicFund().contains("有，") ? "Y" : "N");
        data.put("have_baoxian", JudgeUtil.in(po.getInsurance(), 1, 2) ? "Y" : "N");
        data.put("has_wld_loan", "N");
        data.put("have_ant_credit", "N");
        data.put("product_id", "");
        data.put("salary_method", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? 2 : 0);
        data.put("appid", 43027);
        data.put("appkey", "xvzL7WnnrFM4xsZ9VTzalVToVvpz9yk4");
        data.put("timestamp", value);
        data.put("noncestr", MD5Util.getMd5String(value));
        System.out.println(JSONUtil.toJsonString(data));

        String url = "http://114.55.27.220:8009/customer/import";
        List<Map.Entry<String, Object>> infoIds = new ArrayList<>(data.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        infoIds.sort(Map.Entry.comparingByKey());
        // 构造签名键值对的格式
        StringBuilder sb = new StringBuilder();
        System.out.println("infoIds = " + JSONUtil.toJsonString(infoIds));
        for (Map.Entry<String, Object> item : infoIds) {
            String key = item.getKey();
            Object val = item.getValue();
            sb.append(key).append("=").append(val).append("&");
        }
        System.out.println("sb===" + sb.substring(0, sb.length() - 1));
        data.put("signature", SHA1Util.shaEncode02(sb.substring(0, sb.length() - 1)));
        System.out.println("signature===" + SHA1Util.shaEncode02(sb.substring(0, sb.length() - 1)));
        String result = HttpUtil.postFormUrlEncodedData(url, JSONUtil.toJsonString(data));
        JSONObject resultJSON = JSONUtil.toJSON(result);
        log.info("[北京香鑫未来科技有限公司]推送结果:{}", resultJSON);
        if (Long.parseLong(resultJSON.getString("resStatus")) == 1) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[北京香鑫未来科技有限公司]推送成功:" + result));
            return new SendResult(true, "[北京香鑫未来科技有限公司]推送成功：" + result);
        } else if (2 == Long.parseLong(resultJSON.getString("resStatus"))) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[北京香鑫未来科技有限公司]推送重复:" + result));
            return new SendResult(false, "[北京香鑫未来科技有限公司]推送重复：" + result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[北京香鑫未来科技有限公司]推送失败:" + result));
        return new SendResult(false, "[北京香鑫未来科技有限公司]推送失败：" + result);
    }

    public static void main(String[] args) {
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试渠道");
//        po.setMobile("13561691597");
////        po.setCity("济南市");
//        po.setCity("北京市");
//        po.setLoanAmount("20");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(0);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(34);
//        po.setGender(2);
//        po.setChannel("ttt-zxf-jn-jv60");
//        po.setUpdateDate(new Date());

        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("测试请忽略");
        po.setMobile("13561691602");
        po.setCity("北京市");
        po.setLoanAmount("500000");
        po.setCompany(1);
        po.setPublicFund("有，公积金月缴300-800");
        po.setCar(1);
        po.setHouse(1);
        po.setInsurance(1);
        po.setGetwayIncome(1);
        po.setOccupation(1);
        po.setCreditCard(1);
        po.setAge(30);
        po.setGender(1);
        po.setChannel("zxf-ttt");
        po.setUpdateDate(new Date());
        ApiSender api = new XiangXinFutureTechnologyForBeijingApi();
        System.out.println(api.send(po, null));
    }
}
