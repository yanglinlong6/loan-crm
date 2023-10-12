package com.daofen.admin.service.org.model;

import com.daofen.admin.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class OrgBO extends OrgPO {
    private static final Logger log = LoggerFactory.getLogger(OrgBO.class);
    private OrgAptitudePO orgAptitude; // 机构分城市配置属性
    private String distributeDate; // 分发日期
    private Double score;//待分发的用户分数值
    private Integer weight;//权重值
    private Integer distributeCount = Integer.valueOf(0);
    private Long distributeSort; // 排序值
    private Boolean checkPass = Boolean.valueOf(true);
    private BigDecimal houseAndPublicFundRate = new BigDecimal(100);//有房和公积金比率
    private boolean exceedNoAptitudeRate=false; // 超出没有可贷点资质
    private boolean exceedAptitudeRate = false; // 超出资质


    private String key; // 机构城市资质占比的key值

    public String getKey() {
        return key;
    }

    public Double getScore() {
        return score;
    }
    public void setScore(Double score) {
        this.score = score;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public boolean isExceedAptitudeRate() {
        return exceedAptitudeRate;
    }
    public void setExceedAptitudeRate(boolean exceedAptitudeRate) {
        this.exceedAptitudeRate = exceedAptitudeRate;
    }
    public BigDecimal getHouseAndPublicFundRate() {
        return houseAndPublicFundRate;
    }
    public void setHouseAndPublicFundRate(BigDecimal houseAndPublicFundRate) {
        this.houseAndPublicFundRate = houseAndPublicFundRate;
    }
    public Integer getWeight() {
        return weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    public void setDistributeSort(Long distributeSort) {
        this.distributeSort = distributeSort;
    }

    public void setDistributeCount(Integer distributeCount) {
        this.distributeCount = distributeCount;
    }

    public void setDistributeDate(String distributeDate) {
        this.distributeDate = distributeDate;
    }

    public void setOrgAptitude(OrgAptitudePO orgAptitude) {
        this.orgAptitude = orgAptitude;
    }

    public boolean getExceedNoAptitudeRate() {
        return exceedNoAptitudeRate;
    }
    public void setExceedNoAptitudeRate(boolean exceedNoAptitudeRate) {
        this.exceedNoAptitudeRate = exceedNoAptitudeRate;
    }
    public void setCheckPass(Boolean checkPass) {
        this.checkPass = checkPass;
    }
    public OrgAptitudePO getOrgAptitude() {
        return this.orgAptitude;
    }

    public String getDistributeDate() {
        return this.distributeDate;
    }

    public Integer getDistributeCount() {
        return this.distributeCount;
    }

    public Long getDistributeSort() {
        return this.distributeSort;
    }

    public Boolean getCheckPass() {
        return this.checkPass;
    }

    public String toString() {
        return JSONUtil.toString(this);
    }
}
