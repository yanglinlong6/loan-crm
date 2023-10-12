package com.help.loan.distribute.service.api.model;

import com.help.loan.distribute.common.BasePO;
import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class CustomerPO extends BasePO {

    private Long orgId;

    private Long shopId;

    private Long teamId;

    private Long channel;

    private String media;

    private String city;

    private String name;

    private String mobile;

    private String mobileMd5;

    private Byte sex;

    private Integer age;

    private String level;

    private Byte call;

    private Byte fit;

    private String need; // 需求

    private Byte progress;

    private String field1;

    private String field2;

    private String field3;

    private String field4;

    private String field5;

    private String field6;

    private String field7;

    private String field8;

    private String field9;

    private String field10;

    private String field11;

    private Long employeeId; // 首次分配给谁

    private Date distributeDate; // 分配日期

    private Byte againDistribute; // 是否是再次分配

    private Long againEmployeeId; // 再次分配给谁

    private Date againDistributeDate; // 再次分配给谁

    private Long helpEmployeeId;// 协助人

    private String remark;

    private Date remarkDate;

    private Byte publicPool; // 公共池：0-否，1-是

    private Date appointmentDate; // 预约日期

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}