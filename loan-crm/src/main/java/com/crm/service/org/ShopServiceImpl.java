package com.crm.service.org;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.org.dao.ShopPOMapper;
import com.crm.service.org.model.ShopPO;
import com.crm.util.JudgeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    ShopPOMapper shopPOMapper;

    @Override
    public void getShopByPage(PageBO<ShopPO> page) {
        if(null == page){
            return;
        }
        if(page.getParamObject()  == null){
            ShopPO shop = new ShopPO();
            shop.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
            page.setParamObject(shop);
        }else
            page.getParamObject().setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        page.setDataList(shopPOMapper.selectShopListByPage(page));
        int totalCount = shopPOMapper.selectShopTotalCountByPage(page);
        page.setTotalCount(totalCount);
        if(totalCount == 0){
            page.setPageCount(0);
        }else if(totalCount%page.getSize() == 0){
            page.setPageCount(totalCount/page.getSize());
        }else
            page.setPageCount(totalCount/page.getSize()+1);
    }


    @Override
    public void addShop(ShopPO shopPO) {
        if(StringUtils.isBlank(shopPO.getName())){
            throw new CrmException(CrmConstant.ResultCode.EX,"门店名称不能空");
        }
        ShopPO selectShop = new ShopPO();
        selectShop.setName(shopPO.getName());
        selectShop.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        if(null != shopPOMapper.selectShop(selectShop)){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"门店名称已存在！");
        }
        OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();
        shopPO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        shopPO.setCreateBy(employeeBO.getName());
        shopPOMapper.insertShop(shopPO);
    }

    @Override
    public void delShop(Long id) {
        shopPOMapper.delShop(id);
    }

    @Override
    public List<ShopPO> getShopByOrgId(Long orgId, String city,Byte type) {
        if(null == orgId || orgId <= 0)
            return null;
        return shopPOMapper.selectShopByOrgId(orgId,city,type);
    }

    @Override
    public void updateShop(ShopPO shopPO) {
        if(null == shopPO || shopPO.getId() == null || StringUtils.isBlank(shopPO.getName())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"修改门店信息，参数不合法！");
        }
        ShopPO selectShop = new ShopPO();
        selectShop.setName(shopPO.getName());
        selectShop.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        selectShop.setState(null);
        selectShop.setId(shopPO.getId());
        if(null != shopPOMapper.selectShop(selectShop)){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"门店名称已存在！");
        }
        if(shopPO.getCount() <0){
            shopPO.setCount(0);
        }
        shopPOMapper.updateShop(shopPO);
    }

    @Override
    public List<ShopPO> getAll(Byte type) {
        if(null == type || JudgeUtil.in(type,CrmConstant.Shop.Type.HOU,CrmConstant.Shop.Type.QIAN)){
            return shopPOMapper.selectAll(LoginUtil.getLoginEmployee().getOrgId(),type);
        }
        return null;
    }

    @Override
    public ShopPO getShop(Long id) {
        if(null == id || id<=0){
            return null;
        }
        return shopPOMapper.selectShopById(id);
    }
}
