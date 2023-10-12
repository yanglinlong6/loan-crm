package com.crm.service.product;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.customer.ImportService;
import com.crm.service.customer.model.CustomerImportBO;
import com.crm.service.product.dao.ProductChannelPOMapper;
import com.crm.service.product.dao.ProductPOMapper;
import com.crm.service.product.model.ProductBO;
import com.crm.service.product.model.ProductChannelPO;
import com.crm.service.product.model.ProductPO;
import com.crm.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductPOMapper productPOMapper;

    @Autowired
    ImportService importService;


    @Override
    public ProductChannelPO getProductChannel(ProductChannelPO productChannel) {
        return null;
    }

    @Override
    public void getProductPage(PageBO<ProductBO> page) {
        page.setDataList(productPOMapper.selectProductPage(page));
        int totalCount = productPOMapper.selectProductPageCount(page);
        page.setTotalCount(totalCount);
        if(totalCount == 0){
            page.setPageCount(0);
        }else if(totalCount%page.getSize() == 0){
            page.setPageCount(totalCount/page.getSize());
        }else
            page.setPageCount(totalCount/page.getSize()+1);
    }

    @Override
    public List<ProductBO> getProductAll(Long orgId) {
        if(null == orgId || orgId <= 0)
            return null;
        return productPOMapper.selectProductAll(orgId);
    }

    @Override
    public void addProduct(ProductPO product) {
        product.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        if(null != productPOMapper.selectProduct(product))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"产品已存在");
        product.setCreateBy(LoginUtil.getLoginEmployee().getName());
        productPOMapper.addProduct(product);
    }

    @Override
    public void updateProduct(ProductPO product) {
        product.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        ProductPO po = productPOMapper.selectProduct(product);
        if(null != productPOMapper.selectProduct(product) && po.getId() != product.getId())
            throw new CrmException(CrmConstant.ResultCode.FAIL,"产品已存在");
        product.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        productPOMapper.updateProduct(product);
    }

    @Override
    public void delProduct(Long id) {
        if(null == id || id <= 0l)
            return;
        List<CustomerImportBO> importList = importService.getImportProduct(LoginUtil.getLoginEmployee().getOrgId(),id);
        if(ListUtil.isEmpty(importList))
            productPOMapper.delProduct(id);
        throw new CrmException(CrmConstant.ResultCode.FAIL,"该产品已有进件，不能删除");
    }

    @Override
    public List<ProductBO> getProductByChannelId(Long orgId, Long channelId) {
        return productPOMapper.selectProductByChannelId(orgId,channelId);
    }

}
