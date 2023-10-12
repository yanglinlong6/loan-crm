package com.help.loan.distribute;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;

/**
 * @author Yanglinlong
 * @date 2023/10/12 14:22
 */
public class MainTest01 {

    private static final String URL = "https://api.xingdaiqianglian.com/sem/loan_do.html";

    public static void main(String[] args) {
        JSONObject params = new JSONObject();
        params.put("name", "客户姓名");
        params.put("mobile", "手机号");
        params.put("city", "城市");
        params.put("car", "有");
        params.put("age", "39");
        params.put("job", "有");
        params.put("house", "有");
        params.put("baodan_is", "有");
        params.put("sex", "男");
        params.put("money", "20");
        params.put("source", "zuoxinfang");
        params.put("shebao", "有");
        params.put("gongjijin", "有");
        params.put("isbankpay", "是");
        params.put("check_num", "");
        params.put("ip", "127.0.0.1");
        params.put("credit_card", "有");
        params.put("meiti", "媒体名称");
        params.put("time", "1467100609");
        params.put("weili", "10000");
        String result = HttpUtil.postForJSON(URL, params);
        System.out.println("result = " + result);
    }
}
