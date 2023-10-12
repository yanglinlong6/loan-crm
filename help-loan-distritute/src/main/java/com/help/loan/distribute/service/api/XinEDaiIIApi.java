package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AESUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
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

import java.util.*;

/**
 * 上海桦池信息科技有限公司：信e贷-北京 20081
 *
 */
@Component("apiSender_20081")
public class XinEDaiIIApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(XinEDaiIIApi.class);

    private static final String departmentUrlMode = "https://api.kdzl.cn/cgi-bin/roster/department/list?access_token=%s&department_id=1&fetch_child=0";
    private static final String employeeUrlMode = "https://api.kdzl.cn/cgi-bin/roster/department/get_member?access_token=%s&department_id=%s&fetch_child=0";
    private static final String tokenUrl = "https://api.kdzl.cn/cgi-bin/oauth/access_token?appid=70880&did=8920313&secret=4daa76bb22344e9e&expire=3600";
    private static final String sendUrl = "https://api.kdzl.cn/cgi-bin/customer/import?access_token=%s";
    private static final List<Long> pidList = new ArrayList<>();  // 员工id
    private static int i = 1;
    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【信e贷-口袋助理】推送未知异常："+e.getMessage()));
            return new SendResult(false,"【信e贷-口袋助理】推送异常："+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        String result = HttpUtil.getForObject(tokenUrl);
        log.info("【信e贷】获取token结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        String token = null;
        if(0 == json.getIntValue("result")){
            token = json.getString("access_token");
        }
        if(StringUtils.isBlank(token)){
            return new SendResult(false,"【信e贷】获取token失败："+result);
        }

//        String url2 = "https://api.kdzl.cn/cgi-bin/roster/user/get?access_token="+token+"&alias="+"13636383142";
//        String str = HttpUtil.getForObject(url2);

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
        data.put("op_pid",6169693);
        data.put("custms",custms);

//        System.out.println(data.toJSONString());
        url = String.format(sendUrl,token);
        result = HttpUtil.postForJSON(url,data);
        log.info("【信e贷-口袋助理】推送结果:{}-{}-{}",result,index,pidList.size());
        json = JSONUtil.toJSON(result);
        if(0 == json.getIntValue("result") && "ok".equals(json.getString("errmsg"))){
            i++;
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【信e贷-口袋助理】推送成功："+json.toJSONString()));
            return new SendResult(true,"【信e贷-口袋助理】推送成功："+json.toJSONString());
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【信e贷-口袋助理】推送失败："+json.toJSONString()));
        return new SendResult(false,"【信e贷-口袋助理】推送失败："+json.toJSONString());
    }

    public static void main(String[] args){

        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
        po.setName("伍散人测试");
        po.setMobile("13632965139");
        po.setCity("北京市");
        po.setLoanAmount("《3-10万》");
        po.setCar(0);
        po.setHouse(0);
        po.setCompany(0);
        po.setPublicFund("有，个人月缴300-800元");
        po.setGetwayIncome(1);
        po.setInsurance(1);
        po.setAge(33);
        po.setChannel("tt0012");
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setUpdateDate(new Date());
        ApiSender api = new XinEDaiIIApi();
        System.out.println(api.send(po,null));
    }
}
