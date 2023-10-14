package com.help.loan.distribute;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yanglinlong
 * @date 2023/10/12 14:22
 */
@Slf4j
public class MainTest02 {

    private static final String URL = "https://api.xingdaiqianglian.com/sem/loan_do.html";

    public static void main(String[] args) {
        JSONObject data = new JSONObject();
        data.put("name", "樊先生");
        data.put("phone", "13000000000");
        data.put("city", "南京");
        data.put("car", "无");
        data.put("age", 38);
        data.put("job", "有");
        data.put("house", "无");
        data.put("baodan_is", "无");
        data.put("sex", "男");
        data.put("money", 5);
        // 2023/10/13 固定值，具体需要和对接人确定
        data.put("source", "qdyuefu");
        data.put("shebao", "无");
        data.put("gongjijin", "无");
        data.put("isbankpay", "是");
        data.put("check_num", "");
        data.put("ip", "127.0.0.1");
        data.put("credit_card", "无");
        data.put("meiti", "花");
        data.put("time", DateUtil.to10());
        data.put("weili", 0);
        System.out.println(data.toJSONString());

        String url = "https://api.shanjiezhongxin.com/sem/loan_do.html";
        String result = HttpUtil.postFormForObject(url, data);
        log.info("[北京泽安讯代信息科技有限公司]推送结果:{}", result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        System.out.println("jsonObject = " + jsonObject.toJSONString());
    }
}
