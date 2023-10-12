package com.crm.service.behind.model;

import com.crm.common.BasePO;
import com.crm.service.customer.model.CustomerPO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class CustomerBehindBO extends CustomerBehindPO {

    private String name;

    private String mobile;

    private Integer noProcessDay;// 未跟进时间  单位：天

    private String startDate; // 查询开始日期，例如：2021-12-01

    private String endDate; // 查询结束日期： 例如： 2021-12-31

    private CustomerPO customer; // 客户信息

    private List<Long> customerIds; // 客户id集合


    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}