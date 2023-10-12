package com.crm.service.org.model;

import com.alibaba.fastjson.JSONObject;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class OrgRegisterPO {

    private Long id;

    private Long orgId;

    private String mobile;

    private Date createDate;

    public OrgRegisterPO() {
    }

    public OrgRegisterPO(Long id, Long orgId, String mobile) {
        this.id = id;
        this.orgId = orgId;
        this.mobile = mobile;
    }

    public OrgRegisterPO(Long orgId, String mobile) {
        this.orgId = orgId;
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}