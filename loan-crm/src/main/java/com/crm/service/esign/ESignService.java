package com.crm.service.esign;

import com.crm.service.customer.model.CustomerContractPO;
import com.crm.service.esign.model.OrgESignPO;
import com.crm.service.esign.util.DefineException;
import com.crm.service.esign.vo.EsignReceiveBO;

/**
 * e签宝service
 */
public interface ESignService {

    /**
     * e签宝电子签约
     * @param po CustomerContractPO 客户合约对象
     */
    void esign(CustomerContractPO po);

    OrgESignPO getOrgESign(Long orgId);

    /**
     * 修改机构配置信息
     * @param orgESignPO OrgESignPO
     */
    void updateOrgESign(OrgESignPO orgESignPO);

    /**
     * 更新合同签署状态
     * @param esignReceiveBO
     * @return Boolean  true-流程签署成功,false-还未签署成功
     */
    Boolean updateFlow(EsignReceiveBO esignReceiveBO) throws DefineException;

}
