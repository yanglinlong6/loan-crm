package com.daofen.admin.service.user.model;

import com.daofen.admin.utils.JSONUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * 分发查询管理VO
 */
@Getter
@Setter
public class TimeStatisticsPO {
    /**
     * 小时
     */
    private String hour;

    /**
     * 每天数量
     */
    private Integer num;

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
