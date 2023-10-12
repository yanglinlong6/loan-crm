package com.help.loan.distribute.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobileLocationUtil {

    private static final Logger LOG = LoggerFactory.getLogger(MobileLocationUtil.class);

    private static String MOBILE_LOCATION_URL = "http://mobsec-dianhua.baidu.com/dianhua_api/open/location?tel=%s";

    private static String SUCCESS_RESULT_CODE = "200";

    public static String getCity(String mobile) {
        try {
            String forObject = HttpUtil.getForObject(String.format(MOBILE_LOCATION_URL, mobile));
            JSONObject parseObject = JSONObject.parseObject(forObject);
            JSONObject result = null;
            JSONObject jsonObject = parseObject.getJSONObject("responseHeader");
            if(SUCCESS_RESULT_CODE.equals(jsonObject.getString("status"))) {
                result = new JSONObject();
                JSONObject jsonObject2 = parseObject.getJSONObject("response").getJSONObject(mobile).getJSONObject("detail");
                String city = jsonObject2.getJSONArray("area").getJSONObject(0).getString("city");
                if(city.contains("市"))
                    return city;
                else {
                    return city+"市";
                }
            }
            return null;
        }catch (Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }
    }



    public static void main(String[] args){

        System.out.println(getCity("13632965527"));
    }

}
