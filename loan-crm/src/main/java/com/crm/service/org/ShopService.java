package com.crm.service.org;

import com.crm.common.PageBO;
import com.crm.service.org.model.ShopPO;
import com.ec.v2.entity.trajectory.Page;

import java.util.List;

public interface ShopService {

    /**
     * 分页查询
     * @param page PageBO<ShopPO>
     */
    void getShopByPage(PageBO<ShopPO> page);

    /**
     * 新增门店
     * @param shopPO ShopPO
     */
    void addShop(ShopPO shopPO);

    /**
     * 删除门店
     * @param id
     */
    void delShop(Long id);

    /**
     * 过去机构下所有门店
     * @param orgId 机构id
     * @param city  城市
     * @param type 门店类型 1-前端，2-后端
     * @return List<ShopPO>
     */
    List<ShopPO> getShopByOrgId(Long orgId,String city,Byte type);

    /**
     * 更新门店信息
     * @param shopPO ShopPO
     */
    void updateShop(ShopPO shopPO);

    /**
     * 获取机构下的全部门店列表
     * @param  type 门店类型: 1-前端门店,2-后端门店
     * @return List<ShopPO>
     */
    List<ShopPO> getAll(Byte type);


    /**
     * 根据门店id，获取门店对象信息
     * @param id 门店id
     * @return ShopPO
     */
    ShopPO getShop(Long id);

}
