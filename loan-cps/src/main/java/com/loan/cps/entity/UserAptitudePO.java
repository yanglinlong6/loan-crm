package com.loan.cps.entity;


import com.loan.cps.common.BasePO;
import com.loan.cps.common.JSONUtil;
import lombok.Getter;
import lombok.Setter;
import org.msgpack.annotation.Message;

@Setter
@Getter
@Message
public class UserAptitudePO extends BasePO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	
	private String name;
	
	private String mobile;

	private String province;
	
	private String city;
	
	private String loanAmount;
	
	private Integer creditCard;
	
	private Integer zhima;
	
	private Integer zhimaScore;
	
	private Integer company;
	
	private String publicFund;
	
	private Integer car;
	
	private Integer house;
	
	private Integer insurance;
	
	private Integer getwayIncome;

	private Integer shebao;
	
	private Integer level;
	
	private Integer occupation;
	
	private Integer houseState;
	
	private Integer age;
	
	private Integer gender;
	
	private String channel;
	
	private Double weight;
	
	private String mobileLocation;
	
	private String ipLocation;

	private Integer houseExtension;

	private Double carPrice;

	private String overdue;

	private String callTime;// 随时回访、上午回访、下午回访、晚上回访

	private String md5;

	private Byte type; // 产品类型: 0-综合信贷, 1-房抵 , 2-车抵, 3-债务, 4-公积金

	private String extension; // 扩展内容

	// 百度参数
	private Long ucid; // 账户id
	private long clueId; // 线索id
	private String sex;// 性别
	private Double amount;// 金额
	private String aptitude;//资质



	@Override
	public String toString() {
		return JSONUtil.toString(this);
	}
}
