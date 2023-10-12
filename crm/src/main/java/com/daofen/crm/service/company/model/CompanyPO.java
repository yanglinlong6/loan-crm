package com.daofen.crm.service.company.model;

import com.daofen.crm.base.BasePO;
import com.daofen.crm.service.counselor.model.CompanyCounselorPO;
import com.daofen.crm.utils.JSONUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.validation.constraints.*;


@Setter
@Getter
public class CompanyPO extends BasePO {

    private Long parentId;

    @NotBlank(message = "公司名称不能为空")
    private String name;

    @DecimalMin(value="1",message = "公司类型必修是1-3之间")
    @DecimalMax(value = "3",message = "公司类型必修是1-3之间")
    private Byte type;

    @NotBlank(message = "城市不能为空")
    private String city;

    private String remark;




    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}