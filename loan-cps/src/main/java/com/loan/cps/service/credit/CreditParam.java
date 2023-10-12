package com.loan.cps.service.credit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Random;

public class CreditParam {


    public static final String[] questions = {
            "信用逾期可以修复吗?",
            "无法偿还信用卡怎么办?",
            "用卡逾期可以修复吗?",
            "信用卡逾期可以修复吗?",
            "用卡逾期有什么影响?",
            "网贷逾期怎么办？",
            "无力偿还网贷怎么办？",
            "网贷逾期有什么影响?",
            "征信黑名单可以修复?",
            };


    public static final String[] history = {
            "张*,13338660882",
            "邓*,13738187733",
            "张*,13776227128",
            "闫*,18762218552",
            "程*,18269938062",
            "雷*,18521518015",
            "李*,19889905746",
            "余*,18635388803",
            "王*,15110708157",
            "徐*,13837585038",
            "蒋*,15051419158",
            "张*,15190090753",
            "王*,15851506256",
            "杨*,15800987707",
            "顾*,13962490977",
            "丁*,13260767558",
            "蔡*,17505631133",
            "陈*,15221196985",
            "张*,18621241570",
            "常*,13072561966",
            "朱*,15850391005",
            "刘*,18625241498",
            "孙*,19118534819",
            "李*,13918316316",
            "沈*,13147079405",
            "李*,19824885718",
            "何*,13738187733",
            "孔*,17633805565",
            "伍*,13818765296",
            "吴*,15110708157",
            "柯*,15293846004",
            "常*,15900611760",
            "谭*,17501666047",
            "齐*,13925807133",
            "车*,13641850007",
            "李*,13641850007",
            "程*,15502154544",
            "伍*,15821804254",
            "刘*,17601327683",
            "沈*,13764518062",
    };

    public static JSONArray getHistory(){
        Random r = new Random();
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<10;i++){
            int index = r.nextInt(history.length);
            String[] array = history[index].split(",");
            JSONObject json = new JSONObject();
            json.put("name",array[0]);
            String mobile = array[1];
            json.put("mobile",mobile.substring(0,3)+"***"+mobile.substring(mobile.length()-4,mobile.length()));
            json.put("time",60-index+"分钟前");
            jsonArray.add(json);
        }
        return jsonArray;
    }

}
