package com.help.loan.distribute.service.org.model;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.BasePO;
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