package com.loan.cps.entity;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.BasePO;

/**
 * 用户申请记录对象：user_apply_record
 */

public class UserApplyRecordPO extends BasePO {

    private String applyId; // 申请记录id，使用uuid生成
    private Long companyId; // 机构id
    private String lenderId; //  产品id，也是uuid自动生成
    private String mobile;
    private String redirectUrl; // 跳转地址
    private Byte resource; // 来源：1(菜单)，2(ai客服)，3(投放)，4(其他)
    private String userId; // 用户id 也是uuid自动生成

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getLenderId() {
        return lenderId;
    }

    public void setLenderId(String lenderId) {
        this.lenderId = lenderId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public Byte getResource() {
        return resource;
    }

    public void setResource(Byte resource) {
        this.resource = resource;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}