package com.crm.service.customer.model;


import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 客户资料对象
 */
@Setter
@Getter
public class CustomerZiliaoPO extends BasePO {

    private Long customerId;

    private Long orgId;

    private Long employeeId;

    private String image; // 资料文件图片地址

    private Byte status;// 资料状态：1-正常，2-已删除

    public CustomerZiliaoPO(Long customerId, Long orgId, Long employeeId, String image) {
        this.customerId = customerId;
        this.orgId = orgId;
        this.employeeId = employeeId;
        this.image = image;
        this.status = 1;
    }

    public CustomerZiliaoPO() {
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
