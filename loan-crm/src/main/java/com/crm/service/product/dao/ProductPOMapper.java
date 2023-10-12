package com.crm.service.product.dao;


import com.crm.common.PageBO;
import com.crm.service.product.model.ProductBO;
import com.crm.service.product.model.ProductChannelPO;
import com.crm.service.product.model.ProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ProductPOMapper {

    List<ProductBO> selectProductPage(PageBO<ProductBO> page);

    int selectProductPageCount(PageBO<ProductBO> page);

    List<ProductBO> selectProductAll(Long orgId);

    int addProduct(ProductPO po);

    ProductPO selectProduct(ProductPO po);

    int updateProduct(ProductPO po);

    int delProduct(Long id);

    List<ProductBO> selectProductByChannelId(@Param("orgId") Long orgId,@Param("channelId")Long channelId);
}