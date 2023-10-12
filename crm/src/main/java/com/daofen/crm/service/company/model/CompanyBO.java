package com.daofen.crm.service.company.model;

import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.List;


@Setter
@Getter
public class CompanyBO extends CompanyPO {

    private List<CompanyBO> childList; // 子菜单

    @NonNull
    private CompanyCounselorBO counselor; // 顾问账户信息对象

    public CompanyBO() {
    }

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}