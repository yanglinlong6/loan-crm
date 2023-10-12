package com.daofen.crm.service.company.dao;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.company.model.ShopBO;
import com.daofen.crm.service.company.model.ShopPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ShopPOMapper {

    List<ShopBO> selectShopPage(PageVO<ShopBO> pageVO);

    Integer selectShopPageCount(PageVO<ShopBO> pageVO);

    int deleteShop(Long id);

    int insertShop(ShopPO record);

    ShopPO selectShop(Long id);

    ShopPO selectShopByName(String name);

    ShopPO selectShopByCompanyIdAndName(@Param("companyId") Long companyId, @Param("name") String name);

    List<ShopPO> selectCompanyShops(Long companyId);

    int updateShop(ShopPO record);
}