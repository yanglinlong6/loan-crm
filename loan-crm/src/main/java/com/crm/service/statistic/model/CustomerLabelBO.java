package com.crm.service.statistic.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 客户标签统计
 */
@Setter
@Getter
public class CustomerLabelBO {

    private Long orgId;

    private Long shopId;

    private Long teamId;

    private Long employeeId;

    private Long channelId;

    private String media;//媒体名称

    private String startDate;

    private String endDate;

    private String label;

    private Integer count;

    private String rate;


    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
