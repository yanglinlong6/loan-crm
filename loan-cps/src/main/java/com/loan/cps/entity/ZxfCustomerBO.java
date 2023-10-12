package com.loan.cps.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 左心房客户对象
 */
@Getter
@Setter
public class ZxfCustomerBO {


    private Long clueId;

    private String name;

    private String mobile;

    private String customerLocation; // 客户归属地

    private String location; // 表单填写地

    private String cusSourceTyp; // 来源分类:字节投放/广点通投放

    private String cusSourceNum; // 计划id


    private String cusTypeStr; // 投放类型: 房抵、车抵、信贷

    private String remark; // 客户资质备注

    private String loanAmount;

    private Long createTime;

    private Long timestamp;

    private String textData;

    private String ip;

    private String sign;

    private int age;
}
