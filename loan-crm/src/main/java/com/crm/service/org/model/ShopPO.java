package com.crm.service.org.model;

import com.crm.common.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ShopPO extends BasePO {

    private Long orgId;// 所属机构id

    private String name;// 门店名称

    private String city;

    private String adress;// 门店地址

    private Byte state=1;// 门店状态：1-营业中（默认），0-关闭

    private Integer count=0; // 门店每日需求量

    private byte type; // 门店类型: 1-前端门店,2-后端门店

}