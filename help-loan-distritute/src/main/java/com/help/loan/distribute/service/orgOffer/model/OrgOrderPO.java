package com.help.loan.distribute.service.orgOffer.model;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class OrgOrderPO {
    private Long id;

    private String orderNum;//日期字符串，例如：20200915

    private Long orgId;//机构id

    private String orgNickname;//机构昵称

    private BigDecimal amount;//

    private Byte status;

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}