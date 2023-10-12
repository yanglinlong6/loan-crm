package com.crm.service.thirdparty.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 第三方合作机构对象类
 */
@Getter
@Setter
public class OrgThirdPartyPO extends BasePO {

    private Long orgId;

    private Long belongOrgId; // 归属机构id

    private String nickname;

    private String name;

    private String address;

    private String orgCode;

    private String adminName;

    private String adminPhone;

    private Long productId;

    private String productName;

    private String city;

    private Byte status;

    private Byte auto;

    private List<Long> customerIdList; // 客户id


    public OrgThirdPartyPO() {
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}