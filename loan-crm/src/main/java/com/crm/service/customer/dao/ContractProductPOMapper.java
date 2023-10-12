package com.crm.service.customer.dao;


import com.crm.service.customer.model.ContractProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public
interface ContractProductPOMapper {


    int insertContractProduct(ContractProductPO contractProduct);

    int updateContractProductById(ContractProductPO record);

    /**
     * 获取单个签约产品
     * @param id
     * @return ContractProductPO
     */
    ContractProductPO selectContractProductById(Long id);

    /**
     * 获取签约产品
     * @param orgId 机构id
     * @param contractId 签约id
     * @return List<ContractProductPO>
     */
    List<ContractProductPO> selectContractProduct(Long orgId,Long contractId);
}