package com.help.loan.distribute.service.api.crm;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParamBO {

    private String name;

    private int gender;// 性别：1-男；2-女

    private String mobile;

    private String city;

    private int age;

    private int quota;// 申请额度

    private int fund;// 是否有公积金：1-有，2-无

    private int car;// 是否有车：1-有，2-无

    private int house;// 有无房产：：1-有，2-无

    private int policy;// 是否有保单:1-有，2-无

    private int lifeInsurance;// 是否有寿险：1-有，2-无

    private int businessLicense;//营业执照 1有 2无

    private int payroll;//代发工资：：1-有，2-无

    private String remark;//备注

    private int sourceId;// 来源id

    private String secret;

    private String qualification;

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }

}
