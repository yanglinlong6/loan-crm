package com.crm.service.statistic.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 首页排行榜 业务对象
 */
@Setter
@Getter
public class HomeIncomeSortBO {

    private Long orgId;

    private Long employeeId;

    private String sort; // 排名

    private String employeeName; // 员工姓名

    private Integer newCount; // 新客户数量

    private Integer contractCount; // 签约数量

    private String contractRate; // 签约率

    private Double incomeAmount;// 收款金额

    private String  startDate; // 开始查询日期

    private String endDate; // 结束查询日期

    public HomeIncomeSortBO() {
    }

    public HomeIncomeSortBO(Long orgId, Long employeeId, String employeeName, String startDate, String endDate) {
        this.orgId = orgId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }


    public void computeContarctRate(){
        if(null == this.contractCount || this.contractCount <= 0 || null == this.newCount || this.newCount <= 0){
            this.contractRate = "0.00%";
        }else{
            double rate = BigDecimal.valueOf(this.contractCount).divide(BigDecimal.valueOf(this.newCount),BigDecimal.ROUND_HALF_UP,2).doubleValue();
            rate = rate*100;
            this.contractRate = rate+"%";
        }
    }
}
