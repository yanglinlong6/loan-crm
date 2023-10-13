package com.loan.cps;

import cn.hutool.json.JSONUtil;
import com.loan.cps.entity.ZxfCustomerBO;

import java.util.Date;

/**
 * @author Yanglinlong
 * @date 2023/10/13 10:42
 */
public class MainTest01 {
    public static void main(String[] args) {
        ZxfCustomerBO customerBO = new ZxfCustomerBO();
        customerBO.setClueId(1L);
        customerBO.setName("杨林龙");
        customerBO.setMobile("13086716076");
        customerBO.setCustomerLocation("广东-深圳");
        customerBO.setLocation("广东-深圳");
        customerBO.setCusSourceTyp("字节投放");
        customerBO.setCusSourceNum("5696863658");
        customerBO.setCusTypeStr("信贷");
//        customerBO.setRemark("住宅、公寓、写字楼、商铺");
        customerBO.setLoanAmount("20-50万元");
        customerBO.setCreateTime(new Date().getTime() / 1000);
        customerBO.setTimestamp(new Date().getTime() / 1000);
        customerBO.setTextData("zuoxinfang");
        customerBO.setIp("111.0.120.236");
        customerBO.setSign("c0647cd695f44cea795a4f259a6c0a37");
        customerBO.setAge(30);
        System.out.println("customerBO = " + JSONUtil.toJsonPrettyStr(customerBO));
    }
}
