package com.daofen.crm.service.company.model;


import com.daofen.crm.base.BasePO;
import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class TeamPO extends BasePO {
    private Long companyId;
    private Long shopId;
    @NotBlank(message = "门店团队名称不能为空")
    private String name;

    private String remark;



    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}