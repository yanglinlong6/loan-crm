package com.crm.service.product.dao;


import com.crm.common.PageBO;
import com.crm.service.product.model.ProductChannelPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ProductChannelPOMapper {

    List<ProductChannelPO> selectPage(PageBO<ProductChannelPO> page);

    int selectPageCount(PageBO<ProductChannelPO> page);

    List<ProductChannelPO> selectAll(Long orgId);

    ProductChannelPO selectProductChannel(ProductChannelPO po);

    int insertProductChannel(ProductChannelPO po);

    int updateProductChannel(ProductChannelPO po);

    int deleteProductChannel(long id);
}