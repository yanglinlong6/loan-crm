package com.crm.service.customer;

import com.crm.common.PageBO;
import com.crm.service.customer.model.CustomerImportBO;
import com.crm.service.customer.model.CustomerImportPO;

import java.util.List;

public interface ImportService {

    void getPage(PageBO<CustomerImportBO> pageBO);

    void addImport(CustomerImportBO bo);

    void updateImport(CustomerImportBO bo);


    List<CustomerImportBO> getImportProduct(Long orgId,Long productId);
}
