package com.crm.service.customer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CustomerContractBO extends CustomerContractPO {

    private String customerName; // 客户姓名

    private String customerMobile;//客户手机

    private String startDate;//开始日期

    private String endDate;//结束日期

    private String days;//未跟进时间

    private Integer incomeCount=0;

    private Double incomeAmount=0d;

    private Double surplusAmount=0d;

    private String contractStatus; // 合同状态

    private List<ContractProductPO> productList; // 合约产品列表


}
