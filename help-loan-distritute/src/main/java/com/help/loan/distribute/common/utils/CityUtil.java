package com.help.loan.distribute.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class CityUtil {

    public static JSONObject getByName(List<String> names, Integer level) {
        JSONObject parseObject = JSON.parseObject(HttpUtil.getForObject("http://10.5.21.233:80/menu/city/get"));
        JSONArray jsonArray = parseObject.getJSONArray("data");
        for(int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray jsonArray2 = jsonObject.getJSONArray("childrenCity");
            for(int a = 0; a < jsonArray2.size(); a++) {
                JSONObject jsonObject2 = jsonArray2.getJSONObject(a);
                for(String name : names) {
                    if(jsonObject2.getString("name").contains(name) || name.contains(jsonObject2.getString("name"))) {
                        return jsonObject2;
                    }
                }
            }
        }
        return null;
    }


}
