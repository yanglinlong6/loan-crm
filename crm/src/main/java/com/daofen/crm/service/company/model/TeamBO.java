package com.daofen.crm.service.company.model;


import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeamBO extends TeamPO {


    private String companyIds;

    private String city; // 公司所在城市

    private String companyName; // 公司名称

    private String shopName; // 团队所属门店名称

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}