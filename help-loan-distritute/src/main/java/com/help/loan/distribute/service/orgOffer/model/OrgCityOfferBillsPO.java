package com.help.loan.distribute.service.orgOffer.model;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 机构城市结算单对象
 */
@Setter
@Getter
public class OrgCityOfferBillsPO {

    private Long id;

    private String dateNum; // 日期字符串，例如：20200915

    private Long orgId; // 机构id

    private String city; // 城市

    private String orgName;//机构名称

    private BigDecimal realPrice; // 真实成本价

    private BigDecimal secondPrice;// 第二成本价

    private BigDecimal windUpPrice; // 结算价格 = secondPrice + secondPrice*结算比例

    private Integer windCount; // 当天机构城市结算数量

    private BigDecimal windAmount; // 当日结算金额

    private String remark;// 备注信息

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}