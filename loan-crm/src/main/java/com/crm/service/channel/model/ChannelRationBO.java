package com.crm.service.channel.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChannelRationBO  extends ChannelRationPO{

    private String channel;//渠道名称

    @Override
    public String toString() {
        if(null == this)
            return "ChannelRationBO{null}";
        return JSONUtil.toJSONString(this);
    }
}
