package com.daofen.crm.service.company.model;


import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShopBO extends ShopPO{

    private String companyIds;// 多个机构id以英文","隔开

    private String companyName; // 公司名称

    private String city; // 机构城市

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}