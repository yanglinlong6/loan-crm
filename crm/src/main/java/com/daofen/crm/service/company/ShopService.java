package com.daofen.crm.service.company;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.company.model.ShopBO;
import com.daofen.crm.service.company.model.ShopPO;

import java.util.List;

/**
 * 门店service接口
 */
public interface ShopService {

    void getPage(PageVO<ShopBO> pageVO);

    void addShop(ShopPO shopPO);

    void saveShop(ShopPO shopPO);

    void delShop(Long id);

    List<ShopPO> getShopListByCompanyId(Long companyId);
}
