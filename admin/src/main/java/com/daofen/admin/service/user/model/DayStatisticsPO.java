package com.daofen.admin.service.user.model;

import com.daofen.admin.utils.JSONUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * 分发查询管理VO
 */
@Getter
@Setter
public class DayStatisticsPO {
    /**
     * 日期
     */
    private String day;

    /**
     * 每天数量
     */
    private Integer num;

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
