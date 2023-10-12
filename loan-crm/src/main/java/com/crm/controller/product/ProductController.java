package com.crm.controller.product;

import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.product.ProductService;
import com.crm.service.product.model.ProductBO;
import com.crm.service.product.model.ProductPO;
import com.crm.util.DateUtil;
import com.crm.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;

    @PostMapping("/product/page")
    @ResponseBody
    public ResultVO page(@RequestBody() PageBO<ProductBO> page){
        ProductBO bo = page.getParamObject();
        if(null == bo)
            bo = new ProductBO();
        bo.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
//        bo.setStartDate(DateUtil.cumputeStartDate(bo.getStartDate()));
//        bo.setEndDate(DateUtil.computeEndDate(bo.getEndDate()));
        page.setParamObject(bo);
        productService.getProductPage(page);
        page.setParamObject(null);
        return ResultVO.success("产品分页成功",page);
    }


    @PostMapping("/product/all")
    @ResponseBody
    public ResultVO all(){
        return ResultVO.success("查询全部产品成功",productService.getProductAll(LoginUtil.getLoginEmployee().getOrgId()));
    }


    @PostMapping("/product/add")
    @ResponseBody
    public ResultVO add(@RequestBody() ProductPO po){
        if(StringUtils.isBlank(po.getName()))
            return ResultVO.fail("产品管理-新增：缺少产品名称",null);
        if(null == po.getChannelId())
            return ResultVO.fail("产品管理-新增：缺少产品渠道",null);
        productService.addProduct(po);
        return ResultVO.success("产品管理-新增成功",null);
    }

    @PostMapping("/product/update")
    @ResponseBody
    public ResultVO update(@RequestBody() ProductPO po){
        if(null == po.getId())
            return ResultVO.fail("产品管理-新增：缺少产品id",null);
        if(StringUtils.isBlank(po.getName()))
            return ResultVO.fail("产品管理-新增：缺少产品名称",null);
        if(null == po.getChannelId())
            return ResultVO.fail("产品管理-新增：缺少产品渠道",null);
        productService.updateProduct(po);
        return ResultVO.success("产品管理-新增成功",null);
    }

    @PostMapping("/product/del")
    @ResponseBody
    public ResultVO del(@RequestBody() Long id){
        productService.delProduct(id);
        return ResultVO.success("产品管理-删除成功",null);
    }



}
