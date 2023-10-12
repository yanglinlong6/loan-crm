package com.help.loan.distribute.service.lender.model;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class LenderPO extends BasePO {

    private static final long serialVersionUID = 3451455528561321193L;

    private String lenderId;

    private Long companyId;

    private String name;

    private String logoUrl;

    private String redirectUrl;

    private Byte status;

    private Byte quality;

    private String remark;

    private String require;

    private Integer minAmount;

    private Integer maxAmount;

    private String deadline;

    private String deadlineUnit;

    private String makeLoanTime;

    private String totalFee;

    private String evaluate;

    private Integer applyNum; // 产品申请数量

    private Integer applyBaseNum; // 产品申请技术

    private Byte isRecommend;

    private Integer average;

    private BigDecimal conversionRate;

    private String repayWay;

    private String applyCondition;

    private String aptitudeRequire;

    private String aptitudeRequireExp;

    private String serviceTelephone;

    private String serviceTime;

    private Byte queryCredit;

    private Byte prepayment;

    private Integer loanTime;

    private Byte baihu;

    private Byte canAddLimit;

    private String loanRateUnit;

    private BigDecimal loanRateMin;

    private BigDecimal loanRateMax;

    private String serviceCost;

    private Byte goHeavy;

    private Byte uniteLogin;

    private String recommendReason;

    private String lable;

    private String applyFlow;

    private Long sort;

    private Byte settlement;

    private Byte quota;

    public LenderPO() {
    }

    /**
     * @param companyId 机构id
     * @param status    产品状态
     * @param sort      排序值
     */
    public LenderPO(Long companyId, Byte status, Long sort) {
        this.companyId = companyId;
        this.status = status;
        this.sort = sort;
    }

    /**
     * @param lenderId 产品id
     * @param applyNum 申请数量
     */
    public LenderPO(String lenderId, Integer applyNum) {
        this.lenderId = lenderId;
        this.applyNum = applyNum;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}