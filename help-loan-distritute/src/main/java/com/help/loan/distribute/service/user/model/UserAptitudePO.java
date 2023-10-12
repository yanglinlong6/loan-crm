package com.help.loan.distribute.service.user.model;


import com.help.loan.distribute.common.BasePO;
import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserAptitudePO extends BasePO {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long orgId;

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

    private Integer houseExtension;

    private Integer insurance;

    private Integer getwayIncome;

    private Integer level;

    private Integer occupation;

    private Integer houseState;

    private Integer wxType;

    private Integer age;

    private Integer gender;

    private String channel;

    private Double weight;

    private Boolean isRepeat=false;

    private Double carPrice;

    private String overdue; // 逾期时间:  即将逾期，1-3月，4-6月，7-12月，12月以上

    private String callTime;

    private Byte type; //  产品类型:0-综合信贷,1-房抵,2-车抵,3-债务,4-公积金

    private String extension;

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}
