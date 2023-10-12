package com.crm.service.statistic.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 客户状态统计业务对象
 */
@Setter
@Getter
public class CustomerStateBO {

    private Long orgId;

    private Long shopId;

    private Long teamId;

    private Long employeeId;

    private Long channelId;

    private String media;//媒体名称

    private String startDate;

    private String endDate;

    private Byte  process;

    private String processName;

    private Integer counts;

    private String rate;


    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
