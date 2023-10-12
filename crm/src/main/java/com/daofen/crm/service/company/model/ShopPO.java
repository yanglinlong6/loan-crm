package com.daofen.crm.service.company.model;


import com.daofen.crm.base.BasePO;
import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class ShopPO extends BasePO {
//    @NotNull(message = "公司id不能为空")
    private Long companyId; // 机构id

    @NotNull(message = "门店名称不能为空")
    private String name; // 门店名称

    private String address; // 门店地址

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}