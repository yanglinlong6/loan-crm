package com.help.loan.distribute;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class TestDate {

    // 19440095  2a029a5f6a3bdb5f561f73316fddeae5  1111629340
    // 19034811  e8106a7396eb4856d24515de3bd7f84b  1111517895
    // 19034815  4508c78f2d2e4c516dab33e7f87c59d8  1111595428
    // 18958415  0b6a1483239dbc3a3335adf4053233bc  1111748968
    // 19440078  dbc2a70a17a7161d8423eb46906db067  1111551553
    // 19034808  8ca369cdf8bbb702f354426ee8347f74  1111595418
    // 20005450  bf14ab8a4ef01394e161de9821f5b6de  1111797344
    // 20005415  d9f2a60b88137d410beb523de4410201  1111797368
    // 20005452  554336a559159f5cef544e8beec201e5  1111719833
    // 19389978  fb0a480ffe8556453af61738575b141c  1111877380
    // 19389973  d042db7ab853a5cd979c311720f97caf  1111877376
    // 19440112  233966a35721b540786d22aa0415c81c  1111629352
    // 19389985  4e98bce7e0b90bbf1d079264ddc51f5d  1111800647
    // 19440095  2a029a5f6a3bdb5f561f73316fddeae5  1111629340
    // 19389983  295b333a75b8b4f1acf9f0d4f4ea1bd8  1111800601
    // 20455866  59bd65ffa157a060718e24fcb591c3ea  1111916423
    @Test
    public void testGdt() {

        String urlModel = "https://api.e.qq.com/v1.1/user_actions/add?access_token=%s&timestamp=%s&nonce=%s";

        int account_id = 20455866;
        String access_token = "59bd65ffa157a060718e24fcb591c3ea";
        int user_action_set_id = 1111916423;
        int timestamp = DateUtil.to10();
        String nonce = UUID.randomUUID().toString().toLowerCase().replace("-","");

        JSONArray actions = new JSONArray();
        JSONObject action = new JSONObject();
        action.put("action_time",timestamp);
        action.put("action_type","RESERVATION");
        action.put("url","http://gdt2.fxsk100.com/?wx_aid=4521811648&tid=4521811737&gdt_vid=wx0ovxcfgmrbpnt600&wx_traceid=wx0ovxcfgmrbpnt600#/?city=chengdu&accountId=20455866&channel=5866-cd-120101-1007");
//        action.put("channel","TENCENT");
        JSONObject trace = new JSONObject();
        trace.put("click_id","wx0ovxcfgmrbpnt600");
        action.put("trace",trace);
        actions.add(action);


        JSONObject data = new JSONObject();
        data.put("account_id",account_id);
        data.put("user_action_set_id",user_action_set_id);
        data.put("actions",actions);

        String url = String.format(urlModel,access_token,timestamp,nonce);

        String result = HttpUtil.postForJSON(url,data);
        System.out.println(result);


    }


    @Test
    public void testMP() {

        String urlModel = "https://api.weixin.qq.com/marketing/user_actions/add?version=v1.0&access_token=%s";

        String access_token = "44_ofmxEMIk8YzVjJSti6Rqh0M7_iAaTZ29zSjjvRY8LwLdBbV9y4Yzk260mxqqDQCvY4aVpeFIeEG9aRKGRzTFgT7R67KUml3IT0opb4t8SiA7HdSeJlBH5OhSrBTowA7GPK0E-x_8HIxfq4GbNUJhADASZO";
        int timestamp = DateUtil.to10();
        String nonce = UUID.randomUUID().toString().toLowerCase().replace("-","");
        int user_action_set_id = 1111692882;

        JSONObject total = new JSONObject();
        JSONArray actions = new JSONArray();
        Long time =System.currentTimeMillis()/1000;

        JSONObject action = new JSONObject();
        action.put("user_action_set_id", user_action_set_id);
        action.put("url", "http://xrzd.fxsk100.com/v20/?channel=v20_78aa_bj_18&wx_aid=3248818277&tid=3248818280&gdt_vid=wx0ruuicqu4ciz3200&wx_traceid=wx0ruuicqu4ciz3200");
        action.put("action_time",time );
        action.put("action_type", "RESERVATION");
        JSONObject trace = new JSONObject();
        trace.put("click_id", "wx0ruuicqu4ciz3200");
        action.put("trace", trace);
        actions.add(action);
        total.put("actions", actions);

        String url = String.format(urlModel,access_token,timestamp,nonce);

        String result = HttpUtil.postForJSON(url,total);

        System.out.println(result);


    }

//    @Test
//    public void testActionId(){
//        String access_token = "dfec70fe34381789306fe5886ce8650d";
//        int account_id = 19464356;
//        int timestamp = DateUtil.to10();
//        String nonce = UUID.randomUUID().toString().toLowerCase().replace("-","");
//
//        String urlModel = "https://api.e.qq.com/v1.1/user_action_sets/add?access_token=%s&timestamp=%s&nonce=%s";
//        String url = String.format(urlModel,access_token,timestamp,nonce);
//
//        JSONObject data = new JSONObject();
//        data.put("account_id",account_id);
//        data.put("type","WEB");
//        data.put("name","急速信用表单预约");
//        data.put("description","急速信用表单预约");
//
//        String result = HttpUtil.postForObject(url,data);
//        System.out.println(result);
//    }

    //
//    public static void main(String[] args){
////        System.out.println(DateUtil.to10());
//
//        String str = "";
//        String[] array = str.split("、");
//        for(int i=0;i<array.length;i++){
//            System.out.println(array[i]);
//        }
//    }


}
