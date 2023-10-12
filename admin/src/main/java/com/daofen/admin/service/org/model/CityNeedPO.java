package com.daofen.admin.service.org.model;

import com.daofen.admin.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 城市机构需求对象
 */
@Setter
@Getter
public class CityNeedPO {

    private String city;//城市

    private Integer limitCount;//限制数量

    private Integer sendCount;// 发送成功数量


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CityNeedPO that = (CityNeedPO) o;
        return this.getCity().equals(that.getCity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(city);
    }

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
