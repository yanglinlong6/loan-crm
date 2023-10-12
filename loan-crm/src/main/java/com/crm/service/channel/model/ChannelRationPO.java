package com.crm.service.channel.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 渠道配量对象信息
 */
@Setter
@Getter
public class ChannelRationPO extends BasePO {

    private Long orgId;

    private Long channelId;

    private String media;

    private String city;

    private Integer dayCount;

    private Integer weekCount;

    private Integer monthCount;

    @Override
    public String toString() {
        if(null == this)
            return "ChannelRationPO{}";
        return JSONUtil.toJSONString(this);
    }
}