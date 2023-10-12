package com.loan.wechat.user;


import com.loan.wechat.common.BasePO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserAptitudePO extends BasePO {

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
	
	private Integer level;
	
	private Integer occupation;
	
	private Integer houseState;
	
	private Integer age;
	
	private Integer gender;
	
	private String channel;
	
	private Double weight;
	
	private String mobileLocation;
	
	private String ipLocation;

	private Integer inCity = 0;
}
