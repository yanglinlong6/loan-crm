package com.daofen.admin.service.org.model;

import com.alibaba.fastjson.JSONObject;
import com.daofen.admin.basic.BasePO;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrgPO extends BasePO {

    private Long orgId;

    private String orgName;

    private String orgNickname;

    private String linkman;

    private String linkmanMobile;

    private String address;

    private Byte state;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}