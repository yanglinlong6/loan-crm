package com.crm.service.customer.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ContractProductPO extends BasePO {

    private Long contractId; // 签约id

    private Long orgId;// 机构id

    private Long customerId; // 客户id

    private Long productId;//产品id

    private String productName;//产品名称

    private String certificate; // 凭证图片地址

    private String remark;// 备注

    private Byte process=0; // 产品处理状态:0-新建，1-处理中，2-完成


    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}