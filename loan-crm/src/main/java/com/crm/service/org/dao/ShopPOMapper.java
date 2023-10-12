package com.crm.service.org.dao;


import com.crm.common.PageBO;
import com.crm.service.org.model.ShopPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ShopPOMapper {

    List<ShopPO> selectShopListByPage(PageBO<ShopPO> page);

    int selectShopTotalCountByPage(PageBO<ShopPO> page);

    ShopPO selectShopById(Long id);

    ShopPO selectShop(ShopPO shop);

    /**
     * 查询机构下的所有门店
     * @param orgId 机构id
     * @param city 城市
     * @return List<ShopPO>
     */
    List<ShopPO> selectShopByOrgId(@Param("orgId") Long orgId,@Param("city") String city,@Param("type") Byte type);

    int delShop(Long id);

    int insertShop(ShopPO shop);


    int updateShop(ShopPO shop);


    List<ShopPO> selectAll(@Param("orgId") Long orgId, @Param("type")Byte type);

}