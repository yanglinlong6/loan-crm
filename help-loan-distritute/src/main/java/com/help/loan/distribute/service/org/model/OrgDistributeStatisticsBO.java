package com.help.loan.distribute.service.org.model;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrgDistributeStatisticsBO {

    private Long orgId;

    private String orgName;

    private String city;

    private Integer limitCount;

    private Integer counts;

    private String loanAmount; // 贷款金额限制配置，例如：{"《3-5万》":0.6,"《5-10万》":0.2,"《10-30万》":0.15,"《30-50万》":0.05}


    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}
