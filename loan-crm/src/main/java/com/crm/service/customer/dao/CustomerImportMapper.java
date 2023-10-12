package com.crm.service.customer.dao;


import com.crm.common.PageBO;
import com.crm.service.customer.model.CustomerImportBO;
import com.crm.service.customer.model.CustomerImportPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Component
@Mapper
public interface CustomerImportMapper {

    /**
     * 客户进件分页查询
     * @param page PageBO<CustomerImportBO>
     * @return  List<CustomerImportBO>
     */
    List<CustomerImportBO> selectPage(PageBO<CustomerImportBO> page);

    /**
     * 客户进件分页查询：总数量
     * @param page PageBO<CustomerImportBO>
     * @return 我的客户进件总数量
     */
    int selectPageCount(PageBO<CustomerImportBO> page);


    int insertImport(CustomerImportPO po);

    int updateImport(CustomerImportPO po);


    /**
     * 查询员工的进件，根据签约合同id 和产品id查询进件信息，一个员工下的客户签约id和产品id不能重复添加进件
     * @param employeeId 员工id
     * @param contractId 签约合同id
     * @param productId  产品id
     * @return CustomerImportBO
     */
    CustomerImportBO selectImportByEmployeeAndContractIdAndProduct(@Param("employeeId") Long employeeId,
                                                                   @Param("contractId")Long contractId,
                                                                   @Param("productId")Long productId);

    List<CustomerImportBO> selectImportProduct(@Param("orgId")Long orgId,@Param("productId")Long productId);

    /**
     *
     * @param contractId 合同id 必填
     * @param orgId 机构id
     * @param shopId 门店id
     * @param teamId 团队id
     * @param employeeId 员工id
     * @return List<CustomerImportPO>  签的合约:进件列表
     */
    List<CustomerImportPO> selectImportsByContract( @Param("contractId")Long contractId,
                                                    @Param("orgId")Long orgId,
                                                    @Param("shopId")Long shopId,
                                                    @Param("teamId")Long teamId,
                                                    @Param("employeeId") Long employeeId);



}