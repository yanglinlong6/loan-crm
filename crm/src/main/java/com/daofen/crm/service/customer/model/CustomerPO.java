package com.daofen.crm.service.customer.model;

import com.daofen.crm.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CustomerPO extends BasePO{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	/**
	 * 客户姓名
	 */
	private String name;
	/**
	 * 客户性别
	 */
	private Integer gender;
	/**
	 * 手机
	 */
	private String mobile;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 年龄
	 */
	private Integer age;
	/**
	 * 数据状态 0 未分配 1已分配 2 公共池 3 等待池
	 */
	private Integer dataState;
	/**
	 * 星级
	 */
	private Integer level;
	/**
	 * 业务状态：0未受理 1已受理 2未接通 3待跟进 4资质不符 5待邀约 6已上门 7已签约 8审核中 9已放款 10已拒批 11捣乱申请 12转介绍
	 */
	private Integer state;
	/**
	 * 问询资质
	 */
	private String qualification;
	/**
	 * 贷款类型：0未知 1信用贷 2房抵贷 3车抵贷 4其他
	 */
	private Integer loanType;
	/**
	 * 所属业务员ID
	 */
	private Long businessId;
	/**
	 * 来源ID
	 */
	private Long sourceId;
	/**
	 * 申请时间
	 */
	private Date loanTime;
	/**
	 * 申请额度
	 */
	private Integer quota;
	/**
	 * 公积金：0未知 1有 2无
	 */
	private Integer fund;
	/**
	 * 车：0未知 1有 2无
	 */
	private Integer car;
	/**
	 * 房：0未知 1有 2无
	 */
	private Integer house;
	/**
	 * 保单：0未知 1有 2无
	 */
	private Integer policy;
	/**
	 * 寿险：0未知 1有 2无
	 */
	private Integer lifeInsurance;
	/**
	 * 营业执照：0未知 1有 2无
	 */
	private Integer businessLicense;
	/**
	 * 代发工资：0未知 1有 2无
	 */
	private Integer payroll;
	/**
	 * 芝麻分
	 */
	private Integer zhimaScore;
	/**
	 * 微粒贷
	 */
	private Integer weili;
	/**
	 * 公积金补充详情
	 */
	private String spmFund;
	/**
	 * 车补充详情
	 */
	private String spmCar;
	/**
	 * 房补充详情
	 */
	private String spmHouse;
	/**
	 * 保单补充详情
	 */
	private String spmPolicy;
	/**
	 * 寿险补充详情
	 */
	private String spmLifeInsurance;
	/**
	 * 营业执照补充详情
	 */
	private String spmBusinessLicense;
	/**
	 * 代发工资补充详情
	 */
	private String spmPayroll;
	/**
	 * 手机号MD5
	 */
	private String mobileMd5;
	/**
	 * 手机号归属地
	 */
	private String mobileCity;
	/**
	 * 所属公司ID
	 */
	private Long companyId;
	/**
	 * 备注条数
	 */
	private Integer remarkNum;
	/**
	 * 备注内容
	 */
	private String remark;
	/**
	 * 查询时间类型
	 */
	private Integer dateType;
	/**
	 * 查询开始时间
	 */
	private String startDate;
	/**
	 * 查询结束时间
	 */
	private String endDate;
	/**
	 * 未联系天数
	 */
	private Integer uncontactedDate;
	/**
	 * 团队ID
	 */
	private Long teamId;
	/**
	 * 门店ID
	 */
	private Long shopId;
	/**
	 * 媒体ID
	 */
	private Long mediaId;
	/**
	 * 来源名称
	 */
	private String sourceName;
	/**
	 * 媒体名称
	 */
	private String mediaName;
	/**
	 * 顾问名称
	 */
	private String counselorName;
	/**
	 * 团队名称
	 */
	private String teamName;
	/**
	 * 门店名称
	 */
	private String shopName;
	/**
	 * 流转记录
	 */
	private List<CirculationLogPO> circulationLogs;
	/**
	 * 备注列表
	 */
	private List<RemarkPO> remarks;
	/**
	 * 转介绍客户ID
	 */
	private Long customerId;
	/**
	 * 备忘录
	 */
	private MemorandumPO memorandum;
	
	private String idList;
	
}
