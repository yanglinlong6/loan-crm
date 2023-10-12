package com.crm.service.employee.model;

import com.crm.common.BasePO;
import com.crm.service.org.model.OrgPO;
import com.crm.service.role.model.RoleBO;
import com.crm.service.role.model.RolePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrgEmployeeBO extends OrgEmployeePO {

    private String shopName;// 门店名称
    private String teamName; // 团队名称

    private RoleBO role;

    List<Long> idList; // 员工id集合

    private OrgPO org;


    @Override
    public String toString() {
        if(null == this)
            return "OrgEmployeePO{}";
        return JSONUtil.toJSONString(this);
    }
}