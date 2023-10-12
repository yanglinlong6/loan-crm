package com.crm.service.statistic.model;

import com.crm.util.JSONUtil;

import java.math.BigDecimal;


public class CustomerStatisticBO {

    private Long id;

    private Long orgId;

    private Long shopId;

    private Long teamId;

    private Long employeeId;

    private String name;

    private int type = 0; // 客户类型：0-默认(全部客户),1-新客户，2-再分配客户，3-自建客户

    private Integer totalCount=0;

    private Integer level0Count=0; // 0星数量
    private String level0Rate="(0)0%";// 0星百分比

    private Integer level1Count = 0; // 1星数量
    private String level1Rate="(0)0%"; // 1星数量占比

    private Integer level2Count = 0; // 2星数量
    private String level2Rate="(0)0%"; // 2星数量占比

    private Integer level25Count = 0; // 2.5星数量
    private String level25Rate="(0)0%"; // 2.5星数量占比

    private Integer level3Count = 0; // 3星数量
    private String level3Rate="(0)0%"; // 3星数量占比

    private Integer level4Count = 0; // 4星数量
    private String level4Rate="(0)0%"; // 4星数量占比

    private Integer level5Count = 0; // 5星数量
    private String level5Rate="(0)0%"; // 5星数量占比

    private Integer callCount = 0;//接通数量
    private String callRate="(0)0%"; // 接通率

    private Integer fixCount = 0;// 有效数量
    private String fixRate="(0)0%";//有效率

    private String startDate;
    private String endDate;

    private static final String model = "(%s)%s";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getLevel0Count() {
        return level0Count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setLevel0Count(Integer level0Count) {
        if(null == level0Count || level0Count<=0 || this.totalCount <= 0){
            this.setLevel0Rate("(0)0%");
        }else{
            double rate = new BigDecimal(level0Count).divide(new BigDecimal(this.totalCount),4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            this.setLevel0Rate(String.format(model,level0Count,rate)+"%");
        }
        this.level0Count = level0Count;
    }

    public String getLevel0Rate() {
        return level0Rate;
    }

    public void setLevel0Rate(String level0Rate) {
        this.level0Rate = level0Rate;
    }

    public Integer getLevel1Count() {
        return level1Count;
    }

    public void setLevel1Count(Integer level1Count) {
        if(null == level1Count || level1Count<=0 || this.totalCount <=0){
            this.setLevel1Rate("(0)0%");
        }else{
            double rate = new BigDecimal(level1Count).divide(new BigDecimal(this.totalCount),4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            this.setLevel1Rate(String.format(model,level1Count,rate)+"%");
        }
        this.level1Count = level1Count;
    }

    public String getLevel1Rate() {
        return level1Rate;
    }

    public void setLevel1Rate(String level1Rate) {
        this.level1Rate = level1Rate;
    }

    public Integer getLevel2Count() {
        return level2Count;
    }

    public void setLevel2Count(Integer level2Count) {
        if(null == level2Count || level2Count<=0 || this.totalCount <=0){
            this.setLevel2Rate("(0)0%");
        }else{
            double rate = new BigDecimal(level2Count).divide(new BigDecimal(this.totalCount),4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            this.setLevel2Rate(String.format(model,level2Count,rate)+"%");
        }
        this.level2Count = level2Count;
    }

    public String getLevel2Rate() {
        return level2Rate;
    }

    public void setLevel2Rate(String level2Rate) {
        this.level2Rate = level2Rate;
    }

    public Integer getLevel25Count() {
        return level25Count;
    }

    public void setLevel25Count(Integer level25Count) {
        if(null == level25Count || level25Count<=0 || this.totalCount <=0){
            this.setLevel25Rate("(0)0%");
        }else{
            double rate = new BigDecimal(level25Count).divide(new BigDecimal(this.totalCount),4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            this.setLevel25Rate(String.format(model,level25Count,rate)+"%");
        }
        this.level25Count = level25Count;
    }

    public String getLevel25Rate() {
        return level25Rate;
    }

    public void setLevel25Rate(String level25Rate) {
        this.level25Rate = level25Rate;
    }

    public Integer getLevel3Count() {
        return level3Count;
    }

    public void setLevel3Count(Integer level3Count) {
        if(null == level3Count || level3Count<=0 || this.totalCount <=0){
            this.setLevel3Rate("(0)0%");
        }else{
            double rate = new BigDecimal(level3Count).divide(new BigDecimal(this.totalCount),4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            this.setLevel3Rate(String.format(model,level3Count,rate)+"%");
        }
        this.level3Count = level3Count;
    }

    public String getLevel3Rate() {
        return level3Rate;
    }

    public void setLevel3Rate(String level3Rate) {
        this.level3Rate = level3Rate;
    }

    public Integer getLevel4Count() {
        return level4Count;
    }

    public void setLevel4Count(Integer level4Count) {
        if(null == level4Count || level4Count<=0 || this.totalCount <=0){
            this.setLevel4Rate("(0)0%");
        }else{
            double rate = new BigDecimal(level4Count).divide(new BigDecimal(this.totalCount),4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            this.setLevel4Rate(String.format(model,level4Count,rate)+"%");
        }
        this.level4Count = level4Count;
    }

    public String getLevel4Rate() {
        return level4Rate;
    }

    public void setLevel4Rate(String level4Rate) {
        this.level4Rate = level4Rate;
    }

    public Integer getLevel5Count() {
        return level5Count;
    }

    public void setLevel5Count(Integer level5Count) {
        if(null == level5Count || level5Count<=0 || this.totalCount <=0){
            this.setLevel5Rate("(0)0%");
        }else{
            double rate = new BigDecimal(level5Count).divide(new BigDecimal(this.totalCount),4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            this.setLevel5Rate(String.format(model,level5Count,rate)+"%");
        }
        this.level5Count = level5Count;
    }

    public String getLevel5Rate() {
        return level5Rate;
    }

    public void setLevel5Rate(String level5Rate) {
        this.level5Rate = level5Rate;
    }

    public Integer getCallCount() {
        return callCount;
    }

    public void setCallCount(Integer callCount) {
        if(null == callCount || callCount<=0 || this.totalCount <=0){
            this.setCallRate("(0)0%");
        }else{
            double rate = new BigDecimal(callCount).divide(new BigDecimal(this.totalCount),4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            this.setCallRate(String.format(model,callCount,rate)+"%");
        }
        this.callCount = callCount;
    }

    public String getCallRate() {
        return callRate;
    }

    public void setCallRate(String callRate) {
        this.callRate = callRate;
    }

    public Integer getFixCount() {
        return fixCount;
    }

    public void setFixCount(Integer fixCount) {
        if(null == fixCount || fixCount<=0 || this.totalCount <=0){
            this.setFixRate("(0)0%");
        }else{
            double rate = new BigDecimal(fixCount).divide(new BigDecimal(this.totalCount),4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            this.setFixRate(String.format(model,fixCount,rate)+"%");
        }
        this.fixCount = fixCount;
    }

    public String getFixRate() {
        return fixRate;
    }

    public void setFixRate(String fixRate) {
        this.fixRate = fixRate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        if(null == this)
            return "CustomerStatisticBO{null}";
        return JSONUtil.toJSONString(this);
    }

}
