package com.loan.cps.service.accounting;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.*;
import com.loan.cps.controller.AccountingController;
import com.loan.cps.dao.AccountingUserDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 财会模块service
 */
@Service
public class AccountingServiceImpl implements AccountingService {

    @Autowired
    private AccountingUserDao accountingUserDao;

    private static final Logger LOG = LoggerFactory.getLogger(AccountingServiceImpl.class);

    /** 新客户*/
    public static final Byte NEW_USER = 0;

    public static final Byte SECCESS = 1;

    public static final Byte FAIL = 2;

    private static final String URL = "http://accounting.bangzheng100.com/crm/import/customer/accounting";

    private static String KEY = "d0cf077270b3476abd86275d07168d38";

    private static Long channelId = 34l;

    @Override
    public R addAccountingUser(AccountingUserPO po) {
        if(StringUtils.isBlank(po.getName())){
            return R.fail("客户姓名必填");
        }
        if(StringUtils.isBlank(po.getMobile())){
            return R.fail("客户手机必填");
        }
        if(StringUtils.isBlank(po.getArea())){
            return R.fail("客户区域必填");
        }
        if(null == po.getAge() || po.getAge()<=18){
            return R.fail("请输入正确的年龄");
        }
        if(null == po.getOccupation() || po.getOccupation()< 0){
            return R.fail("请选择正确的职业");
        }
        po.setStatus(NEW_USER);
        accountingUserDao.insertAccountUser(po);
        //发送到crm
        R r = R.ok();
        r.setData(send(po));
        return r;
    }

    public String send(AccountingUserPO po){
        JSONObject customer = new JSONObject();
        customer.put("name",po.getName());
        customer.put("mobile",po.getMobile());
        customer.put("channel",channelId);
        customer.put("city",po.getArea());
        customer.put("age",po.getAge());
        if(po.getOccupation() == null){
            customer.put("field1","其他");
        }else if(po.getOccupation() == 1){
            customer.put("field1","在校生");
        }else if(po.getOccupation() == 2){
            customer.put("field1","财会在职人员");
        }else
            customer.put("field1","其他");
        customer.put("media","头条");
        String content = DESUtil.encrypt(KEY,customer.toJSONString());

        JSONObject data = new JSONObject();
        data.put("channelId",channelId);
        data.put("data",content);
        String result = HttpUtil.postForObject(URL,data);
        LOG.info("【财会】发送结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        int code = json.getIntValue("code");
        if(200 == code){ // 成功
            po.setStatus(SECCESS);
            updateAccountingUserById(po);
            return json.getString("data");
        }
        po.setStatus(FAIL);
        updateAccountingUserById(po);
        return null;
    }

    @Override
    public void updateAccountingUserById(AccountingUserPO accountingUserPO) {
        accountingUserDao.updateAccountingUserById(accountingUserPO);
    }

    @Override
    public List<AccountingUserPO> getAccountUserList(int count, Byte status) {
        if(count < 1 || !JudgeUtil.in(status.intValue(),0,1,2)){
            return null;
        }
        return accountingUserDao.getAccountingUserWithNotSend(count,status);
    }

//    public static void main(String[] args){
//        JSONObject customer = new JSONObject();
//        customer.put("name","测试财会");
//        customer.put("mobile","13239875567");
//        customer.put("channel",channelId);
//        customer.put("city","广东");
//        customer.put("age",32);
//        customer.put("media","头条");
//        customer.put("field1","在校生");
//        String content = DESUtil.encrypt(KEY,customer.toJSONString());
//        JSONObject data = new JSONObject();
//        data.put("channelId",channelId);
//        data.put("data",content);
//
//        System.out.println(data.toJSONString());
//        String result = HttpUtil.postForObject(URL,data);
//        LOG.info("【财会】发送结果：{}",result);
//    }
}
