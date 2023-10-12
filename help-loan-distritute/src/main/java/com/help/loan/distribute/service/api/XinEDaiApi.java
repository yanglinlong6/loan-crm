package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 上海桦池信息科技有限公司：上海：apiSender_20007
 */
@Component("apiSender_20007")
public class XinEDaiApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(XinEDaiApi.class);

    private static final String sendUrl = "http://dbz.huidaikeji.com/api/api/addwl";
    private static final String checkUrl = "http://dbz.huidaikeji.com/api/api/collidingxx?mobile=";

    private static final String CODE = "wldfdyy";
    private static final int DEPARTMENT_ID =393;
    private static final String KEY = "DbzTJ2txzXiaoLia";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【信e贷】分发未知异常："+e.getMessage()));
            return new SendResult(false,"【信e贷】发送异常："+e.getMessage());
        }

    }



    private SendResult send2(UserAptitudePO po, UserDTO select) throws Exception {

        String url = checkUrl+ MD5Util.getMd5String(po.getMobile())+"&department_id="+ DEPARTMENT_ID;
        String checkResult = HttpUtil.getForObject(url);
        log.info("【信e贷】验证手机号码结果：{}",checkResult);
        //{"code":200,"message":"请求成功"}
        if(200 != JSONUtil.toJSON(checkResult).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【信e贷】分发重复："+checkResult));
            return new SendResult(false,"【信e贷】验证手机号码:重复："+checkResult);
        }
        JSONObject data = new JSONObject();
        data.put("code", CODE);
        if(StringUtils.isBlank(po.getChannel())){
            data.put("flag","pyq");
        }else{
            if(po.getChannel().startsWith("ttt")){
                data.put("flag","tt");
            }else{
                data.put("flag","pyq");
            }
        }
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("money", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("age",(po.getAge()==null || po.getAge() <= 0 )?30:po.getAge());
        data.put("sex",(po.getGender() == null || po.getGender() != 1)?0:1);
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("fund",po.getPublicFund().contains("有，")?1:0);
        data.put("social",0);
        data.put("credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("webank",0);
        data.put("tax",0);
        data.put("work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("city",po.getCity());

        String encrypt = AESUtil.java_openssl_encrypt(data.toJSONString(),KEY).toLowerCase();
        data.clear();
        data.put("data",encrypt);
        String result  = HttpUtil.postForJSON(sendUrl,data);
        log.info("【信e贷】分发结果：{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        if(500 == resultJSON.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【信e贷】成功"+result));
            return new SendResult(true,"【信e贷】:"+result);
        }
        String msg = resultJSON.getString("msg");
        if("电话号码重复".equals(msg)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【信e贷】重复"+result));
            return new SendResult(false,"【信e贷】"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【信e贷】失败"+result));
        return new SendResult(false,"【信e贷】"+result);
    }

    private static final String departmentUrlMode = "https://api.kdzl.cn/cgi-bin/roster/department/list?access_token=%s&department_id=1&fetch_child=0";
    private static final String employeeUrlMode = "https://api.kdzl.cn/cgi-bin/roster/department/get_member?access_token=%s&department_id=%s&fetch_child=0";
    private static final String TOKEN_URL = "https://api.kdzl.cn/cgi-bin/oauth/access_token?appid=70880&did=8920313&secret=4daa76bb22344e9e&expire=3600";
    private static final String SEND_URL = "https://api.kdzl.cn/cgi-bin/customer/import?access_token=%s";
    private static final List<Long> pidList = new ArrayList<>();  // 员工id
    private static int i = 3;

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        String result = HttpUtil.getForObject(TOKEN_URL);
        log.info("【信e贷】获取token结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        String token = null;
        if(0 == json.getIntValue("result")){
            token = json.getString("access_token");
        }
        if(StringUtils.isBlank(token)){
            return new SendResult(false,"【信e贷】获取token失败："+result);
        }
        isHaveAptitude(po);
        // 1.获取部门列表
        String url = String.format(departmentUrlMode,token);
        result = HttpUtil.getForObject(url);
        json = JSONUtil.toJSON(result);
        JSONArray departments = null;
        if(json.containsKey("departments")){
            departments = json.getJSONArray("departments");
        }

        pidList.clear();
        // 2.获取员工列表
        if(null != departments && !departments.isEmpty()){
            for(int index=0;index< departments.size();index++){
                JSONObject deparment = departments.getJSONObject(index);
                if(deparment.getLongValue("id") != 15){
                    continue;
                }
                url = String.format(employeeUrlMode,token,deparment.getLong("id"));
                result = HttpUtil.getForObject(url);
                json = JSONUtil.toJSON(result);
                if(json.containsKey("member")){
                    JSONArray employeeArray = json.getJSONArray("member");
                    if(null == employeeArray || employeeArray.isEmpty()){
                        continue;
                    }
                    for(int index2=0;index2 < employeeArray.size();index2++){
                        pidList.add(employeeArray.getJSONObject(index2).getLongValue("userid"));
                    }
                }
            }
        }


        JSONObject contact = new JSONObject();
        contact.put("name",po.getName());
        contact.put("mobiles",new String[]{po.getMobile()});

        JSONArray contacts = new JSONArray();
        contacts.add(contact);


        int index = i%pidList.size();
        JSONObject follower = new JSONObject();
        follower.put("pid",pidList.get(index));

        JSONArray followers = new JSONArray();
        followers.add(follower);

        JSONObject custm = new JSONObject();
        custm.put("name","头条新闻");
        custm.put("contacts",contacts);
        custm.put("followers",followers);
        custm.put("introduction",getContent(po));

        JSONArray custms = new JSONArray();
        custms.add(custm);

        JSONObject data = new JSONObject();
        data.put("op_pid",pidList.get(index));
        data.put("custms",custms);

        url = String.format(SEND_URL,token);
        result = HttpUtil.postForJSON(url,data);
        log.info("【信e贷-口袋助理】推送结果:{}",result);
        json = JSONUtil.toJSON(result);
        if(0 == json.getIntValue("result") && "ok".equals(json.getString("errmsg"))){
            index++;
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【信e贷-口袋助理】推送成功："+json.toJSONString()));
            return new SendResult(true,"【信e贷-口袋助理】推送成功："+json.toJSONString());
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【信e贷-口袋助理】推送失败："+json.toJSONString()));
        return new SendResult(false,"【信e贷-口袋助理】推送失败："+json.toJSONString());
    }


    public static void main(String[] args){
        for(int i=5;i<10;i++){
            UserAptitudePO po = new UserAptitudePO();
            po.setUserId("fc1c19f47ad64682984d28f9278b298c");
            po.setName("伍散人测试"+i);
            po.setMobile("1363292153"+i);
            po.setCity("北京市");
            po.setLoanAmount("《3-10万》");
            po.setCar(1);
            po.setHouse(1);
            po.setCompany(1);
            po.setPublicFund("有，个人月缴300-800元");
            po.setGetwayIncome(1);
            po.setInsurance(1);
            po.setAge(33);
            po.setChannel("ttt0012");
            po.setOccupation(0);
            po.setCreditCard(0);
            po.setUpdateDate(new Date());
            ApiSender api = new XinEDaiApi();
            System.out.println(api.send(po,null));
        }

    }
}
