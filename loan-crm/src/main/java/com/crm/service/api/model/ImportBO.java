package com.crm.service.api.model;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImportBO {

    private Long channelId;

    private String data;


    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
