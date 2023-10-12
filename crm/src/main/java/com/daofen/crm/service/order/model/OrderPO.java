package com.daofen.crm.service.order.model;

import com.daofen.crm.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OrderPO extends BasePO{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 客户ID
	 */
	private Long customerId;
	/**
	 * 客户姓名
	 */
	private String customerName;
	/**
	 * 客户手机
	 */
	private String customerMobile;
	/**
	 *	城市
	 */
	private String city;
	/**
	 * 进件日期
	 */
	private Date orderDate;
	/**
	 * 业务员ID
	 */
	private Long businessId;
	/**
	 * 费率
	 */
	private Double rate;
	/**
	 * 进件额度
	 */
	private Integer quota;
	/**
	 * 合同号
	 */
	private String contract;
	/**
	 * 银行名称
	 */
	private String bank;
	/**
	 * 银行经理名称
	 */
	private String bankManager;
	/**
	 * 在审计状态：0审核中 1已放款 2已拒批 3已收款
	 */
	private Integer state;
	/**
	 * 数据状态：0正常 1删除
	 */
	private Integer dataState;
	/**
	 * 批款额度
	 */
	private Integer amount;
	/**
	 * 诚意金
	 */
	private Integer sincerity;
	/**
	 * 收款人
	 */
	private String receivables;
	/**
	 * 收款银行
	 */
	private String receivablesBank;
	/**
	 * 收款额度
	 */
	private Integer receivablesAmount;
	/**
	 * 收款日期
	 */
	private Date receivablesDate;
	/**
	 * 渠道费
	 */
	private Integer channelCost;
	/**
	 * 介绍费
	 */
	private Integer introduceCost;
	/**
	 * 净收入
	 */
	private Integer netIncome;
	/**
	 * 贷款类型：0未知 1信用贷 2房抵贷 3车抵贷 4其他
	 */
	private Integer loanType;
	/**
	 * 查询时间类型
	 */
	private Integer dateType;
	/**
	 * 查询开始时间
	 */
	private Date startDate;
	/**
	 * 查询结束时间
	 */
	private Date endDate;
	/**
	 * 公司ID
	 */
	private Long companyId;
	/**
	 * 公司ID
	 */
	private Long shopId;
	/**
	 * 公司ID
	 */
	private Long teamId;
	/**
	 * 业务员名称
	 */
	private String businessName;
}
