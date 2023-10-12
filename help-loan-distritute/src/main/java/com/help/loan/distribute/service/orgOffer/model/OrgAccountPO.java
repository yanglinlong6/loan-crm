package com.help.loan.distribute.service.orgOffer.model;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 机构账户对象
 */
@Setter
@Getter
public class OrgAccountPO {

    private Long id;

    private Long orgId; //机构id

    private String orgNickname; // 机构昵称

    private BigDecimal initAmount; // 机构账户初始金额

    private BigDecimal remainingAmount; // 机构账户余额

    private String email;// 邮箱地址，用于接收结算邮件

    private String mobile; // 手机号码 用于接收结算短信

    private Date createDate;

    private Date updateDate;


    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}