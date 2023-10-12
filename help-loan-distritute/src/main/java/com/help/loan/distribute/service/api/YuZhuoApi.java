package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.*;

/**
 * 上海煜卓信息科技咨询有限公司 apiSender_20040
 */
@Component("apiSender_20040")
public class YuZhuoApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(YuZhuoApi.class);

        private static final String venderName = "bz";

    private static final  String appid = "7";

    private static final String appkey = "plCz497pZgZayjBIho9i0cqpUO3J6yS2";

    private static final String checkUrl = "http://47.114.117.30:8023/openapi/customer/check";

    private static final String url = "http://47.114.117.30:8023/openapi/customer/import";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            Map<String, String> httpHeader = new HashMap<>();
            httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            JSONObject data = new JSONObject();
            data.put("tel", MD5Util.getMd5String(po.getMobile()));
            String result = HttpClientProxy.doPost(checkUrl, data, "UTF-8", 3000, httpHeader);
            log.info("【上海煜卓】撞库结果:{}",result);
            int code = JSONUtil.toJSON(result).getIntValue("resStatus");
            if(1 != code){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海煜卓】撞库重复:"+result));
                return new SendResult(false,"");
            }
            if(JudgeUtil.in(po.getCity(),"上海市")){
                if(StringUtils.isNotBlank(po.getChannel()) && po.getChannel().contains("ttt")){
                    return send2(po,select);
                }else{
                    return new SendResult(false,"【上海煜卓】"+po.getCity()+",只接收【上海市-头条】");
                }
            }
            return send2(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海煜卓】发送异常:"+e.getMessage()));
            return new SendResult(false,e.getMessage());
        }

    }


    private SendResult send2(UserAptitudePO po, UserDTO select) throws Exception {
        isHaveAptitude(po);
        if(null != select && StringUtils.isBlank(po.getName())){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isBlank(parse.getString("openid"))) {
                po.setName("公众号用户");
            } else {
                po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
            }
        }
        Map<String,Object> data = new HashMap<>();
        if(org.apache.commons.lang3.StringUtils.isNotBlank(po.getChannel()) && po.getChannel().startsWith("ttt")){
            data.put("channel","头条");
        }else  data.put("channel","朋友圈");
        data.put("vender_name",venderName);
        data.put("name",po.getName());
        data.put("city",po.getCity().substring(0,po.getCity().length()-1));
        if(JudgeUtil.in(po.getGender(),1))
            data.put("sex","男");
        else data.put("sex","女");
        data.put("telphone",po.getMobile());
        data.put("birthday","1980-01-01");
        if(JudgeUtil.in(po.getCompany(),1))
            data.put("vocation","小企业主");
        else data.put("vocation","工薪族");
        if(JudgeUtil.in(po.getGetwayIncome(),2))
            data.put("salary","6000-9999");
        else if (JudgeUtil.in(po.getGetwayIncome(),1))
            data.put("salary","3000-3999");
        else data.put("salary","0-1999");
        data.put("loan_amount", Integer.valueOf(LoanAmountUtil.transformToWan(po.getLoanAmount())));
        data.put("loan_time",12);
        data.put("have_loan","N");
        data.put("have_credit_card", JudgeUtil.in(po.getCreditCard(),1,2)?"Y":"N");
        data.put("have_house",JudgeUtil.in(po.getHouse(),1,2)?"Y":"N");
        data.put("have_fangdai","N");
        data.put("have_car",JudgeUtil.in(po.getCar(),1,2)?"Y":"N");
        data.put("have_chedai","N");
        data.put("shebao","N");
        data.put("have_fund",po.getPublicFund().contains("有，")?"Y":"N");
        data.put("have_baoxian",JudgeUtil.in(po.getInsurance(),1,2)?"Y":"N");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2))
            data.put("salary_method",1);
        else data.put("salary_method",0);
        data.put("has_wld_loan","N");

        data.put("appid",appid);
        data.put("appkey",appkey);
        data.put("timestamp", DateUtil.to10());
        data.put("noncestr", UUID.randomUUID().toString().replaceAll("-","").replaceAll("_",""));

        Set<String> keySet = data.keySet();
        List<String> keyList = new ArrayList<>(keySet);
        keyList.sort((o1,o2) -> o1.compareTo(o2));
        StringBuffer signString = new StringBuffer();
        for(int i=0;i<keyList.size();i++){
            String key = keyList.get(i);
            if(i == 0)
                signString.append(key).append("=").append(data.get(key));
            else
                signString.append("&").append(key).append("=").append(data.get(key));
        }
        data.put("signature",sha1(signString.toString()));
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        //{"resStatus":1,"resMsg":"succeed"}
        String result = HttpClientProxy.doPost(url, data, "UTF-8", 3000, httpHeader);
        JSONObject resultJson = JSONUtil.toJSON(result);
        int status = resultJson.getIntValue("resStatus");
        if(1==status){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【煜卓】成功:"+result));
            return new SendResult(true,result);
        }else if(2 == status){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【煜卓】重复:"+result));
            return new SendResult(false,"【煜卓】重复："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【煜卓】失败:"+result));
        return new SendResult(false,"【煜卓】失败："+result);
    }

    public String sha1(String data) throws  Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA");
        byte[] byteArray = data.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }
    public static void main(String[] args){

        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("517135ba6a244cd0b1ac9210f495cd18");
        po.setName("伍散人测试3");
        po.setHouse(1);
        po.setAge(35);
        po.setCity("上海市");
        po.setCompany(0);
        po.setInsurance(1);
        po.setLoanAmount("100000");
        po.setMobile("18971549226");
        po.setOccupation(1);
        po.setPublicFund("有，个人月缴300-800元");
        po.setOccupation(1);
        po.setGetwayIncome(1);
        po.setCreditCard(0);
        po.setCar(1);
        po.setGender(1);
        po.setChannel("ttt");
        po.setUpdateDate(new Date());
        YuZhuoApi api = new YuZhuoApi();
        System.out.println(api.send(po,null));

    }
}
