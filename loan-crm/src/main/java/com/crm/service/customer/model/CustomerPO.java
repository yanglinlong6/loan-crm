package com.crm.service.customer.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class CustomerPO extends BasePO {

    private Long orgId;

    private Long shopId;

    private Long teamId;

    private Long channel;

    private String media;

    private String city;

    private String name;

    private String mobile;

    private String mobileMd5;

    private Byte sex;

    private Integer age;

    private String level;

    private Byte call;

    private Byte fit;

    private String need; // 需求

    private Byte progress;

    private String field1;

    private String field2;

    private String field3;

    private String field4;

    private String field5;

    private String field6;

    private String field7;

    private String field8;

    private String field9;

    private String field10;

    private String field11;

    private Long currentEmployee;

    private Long employeeId; // 首次分配给谁

    private Date distributeDate; // 分配日期

    private Byte againDistribute; // 是否是再次分配

    private Long againEmployeeId; // 再次分配给谁

    private Date againDistributeDate; // 再次分配给谁

    private Long helpEmployeeId;// 协助人

    private String remark;

    private Date remarkDate;

    private Byte publicPool; // 公共池：0-否，1-是

    private Date appointmentDate; // 预约日期

    private Byte zijian; //  是否是业务员添加的客户:1-是, 0-否(默认)

    private Long firstEmployee; // 第一次分配的员工id

    private String idcardFront=""; // 身份证正面

    private String idcardBack=""; // 身份证反面

    private String authorizeFile="";// 授权文件

    private String creditFile="";// 征信报告文件

    private String otherFile; // 多个文件以,号隔开

    private String label; // 客户标签,多个标签以,号分割

    private List<CustomerZiliaoPO> customerZiliao;//客户其他字段列表

    private Byte thirdparty; // 是否标记为第三方客户,默认0,0-不是,1-是

    private Long thirdpartyOrgId;

    private String thirdPartyRemark;

    private String accountId; // e签宝账户id

    private Long oldId; // 客户原id

    private Long oldOrgId; // 客户原归属机构id

    public CustomerPO() {
    }

    public CustomerPO(Long id,Byte thirdparty) {
        this.id = id;
        this.thirdparty = thirdparty;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}