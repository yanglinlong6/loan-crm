package com.crm.service.statistic.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class AchievementBO {

    private long id;

    private Long orgId;

    private Long shopId;

    private String city;

    private Long teamId;

    private Long employeeId;

    private String name;

    private String shopName;

    private String teamName;

    private Integer counts=0; // 新客户数

    private Double cost;

    private Integer contractCount=0;// 签约数

    private Integer importCount=0; // 进件次数

    private Double  productTotalAmount=0.00;

    private Double  depositAmount=0.00; // 合同金额

    private Double incomeTotalAmount=0.00; // 已收金额汇总

    private Double surplusAmount = 0.00; // 未收金额汇总

    private Double consumeAmount = 0.00;

    private String consumeIncomeRate = "0:0"; // 默认

    private double rate; // 平均费率

    private String callRate="(0)0%"; // 接通率

    private String fitRate="(0)0%"; // 有效率

    private String startDate;

    private String endDate;


    @Override
    public String toString() {
        if(null == this)
            return "AchievementBO{}";
        return JSONUtil.toJSONString(this);
    }
}
