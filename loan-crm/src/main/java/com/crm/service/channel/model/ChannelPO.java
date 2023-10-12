package com.crm.service.channel.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ChannelPO extends BasePO {

    private Long orgId;

    private String nickname;// 渠道简称

    private String company; //渠道公司名称

    private String key; // 加密密钥

    @Override
    public String toString() {
        if(null == this)
            return "ChannelPO{}";
        return JSONUtil.toJSONString(this);
    }
}