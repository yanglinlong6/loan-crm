package com.daofen.admin.service.org.model;

import com.alibaba.fastjson.JSONObject;
import com.daofen.admin.basic.AdminException;
import com.daofen.admin.basic.BasePO;
import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.service.city.CityService;
import com.daofen.admin.service.city.model.CityPO;
import com.daofen.admin.service.org.dao.OrgDao;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Getter
@Setter
public class OrgAptitudePO extends BasePO {

    private Long orgId;// 机构id

    private String orgName;

    private String province;// 限制省份

    private String city;// 限制城市

    private String week; // 时间限制：星期几

    private String limitTime;//限制时间段例如：01-12

    private Integer limitCount;// 限制数量

    private BigDecimal singleIncome; // 单个流量收益

    private String wechat;// 专属公众号

    private String api;

    private Integer weight;

    private BigDecimal amountRate;

    private String loanAmount;//贷款金额限制

    private String channel;

    /**
     * 规则状态 0：正常 -1：暂停
     */
    private Integer status;

    public OrgAptitudePO checkSingleIncome() {
        if (null == singleIncome || singleIncome.intValue() <= 0)
            throw new AdminException(ResultCode.FAID, "新增机构配量参数错误:单价参数不合法");
        return this;
    }

    public OrgAptitudePO checkLimitCount() {
        if (null == limitCount || limitCount < 0)
            throw new AdminException(ResultCode.FAID, "新增机构配量参数错误:每日限制数量参数不合法");
        return this;
    }

    public OrgAptitudePO checkLimitTime() {
        if (StringUtils.isBlank(limitTime))
            throw new AdminException(ResultCode.FAID, "新增机构配量参数错误:时间限制参数不合法");

        String[] array = limitTime.split("-");
        if (array.length != 2)
            throw new AdminException(ResultCode.FAID, "新增机构配量参数错误:时间限制参数不合法");

        int start = Integer.valueOf(array[0]);
        int end = Integer.valueOf(array[1]);
        if (start >= end)
            throw new AdminException(ResultCode.FAID, "新增机构配量参数错误:时间限制参数不合法");
        return this;
    }

    /**
     * 验证机构参数
     *
     * @param orgDao OrgDao
     * @return OrgAptitudePO
     */
    public OrgAptitudePO checkOrgId(OrgDao orgDao) {
        if (null == orgDao || null == orgId) {
            throw new AdminException(ResultCode.FAID, "新增机构配量参数错误:机构参数不合法");
        }
        OrgPO orgPO = orgDao.selectOrgByOrgId(orgId);
        if (null == orgPO)
            throw new AdminException(ResultCode.FAID, "新增机构配量参数错误:机构参数不合法");
        this.setOrgName(orgPO.getOrgName());
        return this;
    }

    private static final String weeks = "星期一,星期二,星期三,星期四,星期五,星期六,星期日";

    /**
     * 判断是否是合法的星期日期
     */
    public OrgAptitudePO checkWeek() {
        if (StringUtils.isBlank(week))
            return this;
        String[] array = week.split(",");
        for (String str : array) {
            if (!weeks.contains(str)) {
                throw new AdminException(ResultCode.FAID, "新增机构配量参数错误:星期限制参数不合法");
            }
        }
        return this;
    }

    /**
     * 验证城市
     */
    public OrgAptitudePO checkCity(CityService cityService) {
        if (null == cityService || StringUtils.isBlank(this.getCity())) {
            throw new AdminException(ResultCode.FAID, "新增机构配量参数错误:城市参数不合法");
        }
        CityPO cityPO = cityService.getCityPO(this.getCity());
        if (null == cityPO) {
            throw new AdminException(ResultCode.FAID, "新增机构配量参数错误:城市参数不合法");
        }
        return this;
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}