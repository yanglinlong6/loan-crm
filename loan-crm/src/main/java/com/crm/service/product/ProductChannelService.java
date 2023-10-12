package com.crm.service.product;

import com.crm.common.PageBO;
import com.crm.service.product.model.ProductChannelPO;

import java.util.List;

public interface ProductChannelService {

    void getProductChannelPage(PageBO<ProductChannelPO> page);

    void addProductChannel(ProductChannelPO productChannel);

    void updateProductChannel(ProductChannelPO productChannel);

    void delProductChannel(Long id);

    List<ProductChannelPO> getAll(Long orgId);
}
