package com.loan.cps.service.accounting;

import com.loan.cps.common.BasePO;
import com.loan.cps.common.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountingUserPO extends BasePO {

    private String name;

    private Integer age;

    private Byte occupation;//0-默认(未填) 1-在校生  2- 财会在职人员

    private String area;// 区域

    private String mobile; // 手机号码

    private Byte status=0;


    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
