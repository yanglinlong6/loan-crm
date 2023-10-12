package com.help.loan.distribute.service.org.model;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrgAptitudePO extends BasePO {

    private Long orgId;// 机构id
    private String province;// 限制省份

    private String city;// 限制城市

    private String week; // 时间限制：星期几

    private String limitTime;//限制时间段例如：01-12

    private Integer limitCount;// 限制数量

    private BigDecimal singleIncome; // 单个流量收益

    private String wechat;// 专属公众号

    private String api;

    private Integer weight;

    private BigDecimal amountRate;

    private String loanAmount;//贷款金额限制

    private String channel;

    private Double repeatRate;

    private Byte type;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}