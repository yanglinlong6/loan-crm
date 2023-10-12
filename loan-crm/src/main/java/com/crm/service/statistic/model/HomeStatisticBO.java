package com.crm.service.statistic.model;

import lombok.Getter;
import lombok.Setter;
import org.omg.CORBA.PRIVATE_MEMBER;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;

@Setter
@Getter
public class HomeStatisticBO {

    private Long orgId;

    private Long shopId;

    private Long teamId;

    private Long employeeId;

    private String name;

    private Integer newCount=0;// 新客户数量

    private Integer helpCount=0; // 协助客户数量

    private Integer againCount=0; // 再分配数量

    private String callRate; // 接通率

    private String fitRate; // 有效率;

    private Integer appointmentCount=0; //预约上门数量

    private Integer upCount=0;// 上门数量

    private Integer contractCount=0;// 签约数

    private Integer importCount=0;// 进件数量

    private Double depositAmount=0d; // 诚意金

    private Double incomeAmount=0d;// 已收金额

    private Double shengyu = 0d;

    private String startDate;

    private String endDate;


    private static final String model = "(%s)%s%";
    public String computeRate(Integer count1, Integer count2){
        if(null == count1 || count1.intValue() <= 0){
            return "(0)0%";
        }
        if(null == count2 || count2.intValue() <= 0){
            return "(0)0%";
        }
        if(count2.intValue() > count1.intValue()){
            String.format(model,count2,100);
        }
        double value = BigDecimal.valueOf(count2).divide(BigDecimal.valueOf(count1),4,BigDecimal.ROUND_HALF_UP).doubleValue()*100;
        return "("+count2+")"+value+"%";
    }



    @Override
    public String toString() {
        return "HomeStatisticBO{}";
    }
}
