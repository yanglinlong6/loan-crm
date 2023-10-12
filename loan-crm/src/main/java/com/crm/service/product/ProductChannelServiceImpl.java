package com.crm.service.product;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.product.dao.ProductChannelPOMapper;
import com.crm.service.product.model.ProductBO;
import com.crm.service.product.model.ProductChannelPO;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.omg.PortableServer.POA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import java.util.List;

@Service
public class ProductChannelServiceImpl implements ProductChannelService{

    @Autowired
    ProductChannelPOMapper productChannelPOMapper;

    @Autowired
    ProductService productService;

    @Override
    public void getProductChannelPage(PageBO<ProductChannelPO> page) {
        page.setDataList(productChannelPOMapper.selectPage(page));
        int totalCount = productChannelPOMapper.selectPageCount(page);
        page.setTotalCount(totalCount);
        if(totalCount == 0){
            page.setPageCount(0);
        }else if(totalCount%page.getSize() == 0){
            page.setPageCount(totalCount/page.getSize());
        }else
            page.setPageCount(totalCount/page.getSize()+1);
    }

    @Override
    public void addProductChannel(ProductChannelPO po) {
        if(null == po)
            throw new CrmException(CrmConstant.ResultCode.FAIL,"产品渠道-未提交参数");
        if(StringUtils.isBlank(po.getName()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"产品渠道-未提交渠道名称");
        if(StringUtils.isBlank(po.getCompanyName()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"产品渠道-未提交渠道公司名称");
        if(null != productChannelPOMapper.selectProductChannel(po))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"产品渠道-产品渠道或者公司名称已存在");
        po.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        ProductChannelPO productChannel = productChannelPOMapper.selectProductChannel(po);
        if(null != productChannel)
            throw new CrmException(CrmConstant.ResultCode.FAIL,"渠道名称或者渠道公司名称已存在");
        po.setCreateBy(LoginUtil.getLoginEmployee().getName());
        productChannelPOMapper.insertProductChannel(po);
    }

    @Override
    public void updateProductChannel(ProductChannelPO po) {
        if(null == po || null == po.getId())
            throw new CrmException(CrmConstant.ResultCode.FAIL,"产品渠道-未提交参数");

        ProductChannelPO productChannelPO = productChannelPOMapper.selectProductChannel(po);
        if(null != productChannelPO && po.getId() != productChannelPO.getId())
            throw new CrmException(CrmConstant.ResultCode.FAIL,"产品渠道-产品渠道或者公司名称已存在");
        po.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        productChannelPOMapper.updateProductChannel(po);
    }

    @Override
    public void delProductChannel(Long id) {
        if(null == id || id <= 0)
            return;
        List<ProductBO> list = productService.getProductByChannelId(LoginUtil.getLoginEmployee().getOrgId(),id);
        if(ListUtil.isEmpty(list)){
            productChannelPOMapper.deleteProductChannel(id);
            return;
        }
        throw new CrmException(CrmConstant.ResultCode.FAIL,"产品渠道已关联产品，不能删除");
    }

    @Override
    public List<ProductChannelPO> getAll(Long orgId) {
        if(null == orgId)
            throw new CrmException(CrmConstant.ResultCode.FAIL,"缺少机构参数");
        return productChannelPOMapper.selectAll(orgId);
    }

}
