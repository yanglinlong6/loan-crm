package com.crm.service.org.model;

import com.crm.common.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TeamPO extends BasePO {

    private Long orgId; // 所属机构id

    private Long shopId; // 所属门店id

    private String name; // 团队名称

    private Byte state; // 团队状态：0-休息中，1-奋斗中


}