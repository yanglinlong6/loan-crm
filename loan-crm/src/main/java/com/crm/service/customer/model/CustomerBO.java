package com.crm.service.customer.model;

import com.crm.common.CrmConstant;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Setter
@Getter
public class CustomerBO extends CustomerPO{

    private String days;//未跟进时间：单位：天

    private String startDate; // 查询开始日期，例如：2021-05-30 00:00:00

    private String endDate; // 查询结束日期   例如：2021-05-30 23:59:59

    private CustomerRemarkPO customerRemark;//跟进记录对象，用于新增跟进记录

    private List<CustomerRemarkPO> remarkList;//用户分页查询

    private String comment; // 主管点评类容

    private Byte all = 0;// 标识是否查询我的全部客户：0-否，1-是

    private List<Long> idList; // 客户id集合

    private OrgEmployeePO employee;

    private int alls = 0; // 标识是否查询全部客户：0-否，1-是

    private Long channelId;

    private int followDayCount=0;

    private String employeeName; // 员工姓名

    private CustomerContractBO contractBO; // 签约信息

    private List<ContractProductPO> productList; // 合约产品列表

    private List<String> labels; // 标签列表

    private Boolean queryThirdParty=false; // 是否查询分给第三方合作机构的客户列表

    @Override
    public String toString() {
        if(null == this)
            return "CustomerBO{}";
        return JSONUtil.toJSONString(this);
    }

    /**
     * 获取机构配置的客户状态
     * @param processList
     * @param process 客户状态名称
     * @return 客户状态 Byte
     */
    public static Byte getOrgProcess(String processList,String process){
        if(StringUtils.isBlank(processList) || StringUtils.isBlank(process)){
            return CrmConstant.Customer.Progress.SIGN;
        }
        String[] array = processList.split(",");
        for(String str : array){
            if(!str.contains(process)){
                continue;
            }
            return Byte.valueOf(str.split("-")[0]);
        }
        return CrmConstant.Customer.Progress.SIGN;
    }
}
