package com.daofen.crm.service.counselor.model;

import com.daofen.crm.service.company.model.CompanyPO;
import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;


@Getter
@Setter
public class CompanyCounselorBO extends CompanyCounselorPO {

    private RoleBO role; // 角色

    @Nullable
    private CompanyPO company;// 顾问所属公司

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
