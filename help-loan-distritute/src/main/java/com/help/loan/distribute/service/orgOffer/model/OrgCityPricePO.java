package com.help.loan.distribute.service.orgOffer.model;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 城市每日价格表
 */
@Setter
@Getter
class OrgCityPricePO {
    private Long id;

    private String dateNum;// 日期字符串，例如：20200915

    private String city;//城市

    private BigDecimal realPrice;//真实价格

    private BigDecimal secondPrice;// 第二价格

    private Byte cost; // 是否以secondPrice结算，当当天成本过高时，结算价=成本价

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}