package com.crm.service.customer.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * 客户合约对象
 */
@Setter
@Getter
public class CustomerContractPO extends BasePO {

    private Long orgId;//机构id

    private Long shopId;//门店id

    private Long teamId;//团队id

    private Long customerId;//客户id

    private Long employeeId;//员工id

    private String contractCode;//合同编号

    private BigDecimal costRate;//费率

    private BigDecimal deposit=BigDecimal.valueOf(0); // 诚意金-改成合同金额

    private Byte way; //支付方式：0-初始状态，1-支付宝，2-现金，3-微信，4-刷卡，5-其他

    private Byte state; // 诚意金(改成合同金额)支付状态：0-未支付，1-支付，2-作废  改成:   0-初始, 1-已签约, 2-收款中, 3-收款完成, 4-完件   注意:编辑和新增都需要修改

    private String remark;//备注

    private String images;//合同pdf文件地址，或者图片地址，多个以,号隔开

    private String idcardFront=""; // 身份证正面

    private String idcardBack=""; // 身份证反面

    private String authorizeFile="";// 授权文件

    private String creditFile="";// 征信报告文件

    private String otherFile; // 多个文件以,号隔开

    private String fileId; // e签宝上传pdf合同文件的file id

    private String flowId; // e签宝签约的流程id

    private String fileName;

    private List<ContractProductPO> productList; // 签约产品列表

    private List<LocationPO> locations;

    private Byte isDoc=0;//合同是图片或者文档:0-图片,1-文档

    private Boolean esign = false;

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }

    public static void main(String[] args){
        System.out.println(File.separator);
    }
}