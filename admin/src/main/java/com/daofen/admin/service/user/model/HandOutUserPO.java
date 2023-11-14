package com.daofen.admin.service.user.model;

import com.daofen.admin.utils.JSONUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * 分发查询管理VO
 */
@Getter
@Setter
public class HandOutUserPO {
    /**
     * 用户表 ID
     */
    private Integer uid;

    /**
     * 用户表 name
     */
    private String username;

    /**
     * 用户表 mobile
     */
    private String mobile;

    /**
     * 用户表 city
     */
    private String city;

    /**
     * 用户表 loan_amount
     */
    private String loanAmount;

    /**
     * 用户表 创建时间
     */
    private String createDate;

    /**
     * 用户表 更新时间
     */
    private String updateDate;

    /**
     * 用户表 分发状态
     */
    private Integer dispatchStatus;

    /**
     * 用户表 分发结果
     */
    private String dispatchResult;

    /**
     * 用户表 分发结果
     */
    private String orgName;

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
