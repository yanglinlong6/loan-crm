package com.crm.service.product;


import com.crm.common.PageBO;
import com.crm.service.product.model.ProductBO;
import com.crm.service.product.model.ProductChannelPO;
import com.crm.service.product.model.ProductPO;

import java.util.List;

public interface ProductService {



    ProductChannelPO getProductChannel(ProductChannelPO productChannel);

    void getProductPage(PageBO<ProductBO> page);

    List<ProductBO> getProductAll(Long orgId);

    void addProduct(ProductPO product);

    void updateProduct(ProductPO product);

    void delProduct(Long id);

    List<ProductBO> getProductByChannelId(Long orgId,Long channelId);


}
