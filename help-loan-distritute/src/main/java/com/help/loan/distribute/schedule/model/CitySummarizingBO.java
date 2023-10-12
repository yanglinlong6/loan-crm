package com.help.loan.distribute.schedule.model;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CitySummarizingBO {

    private String city;

    private int limitCouint;

    private int sendSuccessCount;

    private double income;

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}
