package com.crm.service.channel.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 渠道成本
 */
@Setter
@Getter
public class ChannelPricePO extends BasePO {

    private Long rationId;//配量id

    private Long orgId;// 机构id

    private Long channelId;// 渠道id

    private String channelName;// 渠道名称

    private String media;//媒体

    private String city;// 城市

    private String inputDate;//日期

    private BigDecimal price;// 价格

    private String startDate;

    private String endDate;

    @Override
    public String toString() {
        if(null == this)
            return "ChannelPricePO{}";
        return JSONUtil.toJSONString(this);
    }
}