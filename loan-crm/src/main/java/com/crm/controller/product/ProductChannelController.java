package com.crm.controller.product;

import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.product.ProductChannelService;
import com.crm.service.product.model.ProductChannelPO;
import com.crm.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductChannelController {

    @Autowired
    ProductChannelService productChannelService;

    @PostMapping("/product/channel/page")
    @ResponseBody
    public ResultVO page(@RequestBody() PageBO<ProductChannelPO> page){
        ProductChannelPO param = page.getParamObject();
        if(null == param)
            param = new ProductChannelPO();
        param.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        param.setStartDate(DateUtil.cumputeStartDate(param.getStartDate()));
        param.setEndDate(DateUtil.computeEndDate(param.getEndDate()));
        page.setParamObject(param);
        productChannelService.getProductChannelPage(page);
        page.setParamObject(null);
        return ResultVO.success("产品渠道分页成功",page);
    }

    @PostMapping("/product/channel/all")
    @ResponseBody
    public ResultVO all(){
        return ResultVO.success("产品渠道-新增成功",productChannelService.getAll(LoginUtil.getLoginEmployee().getOrgId()));
    }

    @PostMapping("/product/channel/add")
    @ResponseBody
    public ResultVO add(@RequestBody() ProductChannelPO po){
        productChannelService.addProductChannel(po);
        return ResultVO.success("产品渠道-新增成功",null);
    }

    @PostMapping("/product/channel/update")
    @ResponseBody
    public ResultVO update(@RequestBody() ProductChannelPO po){
        productChannelService.updateProductChannel(po);
        return ResultVO.success("产品渠道-新增成功",null);
    }

    @PostMapping("/product/channel/del")
    @ResponseBody
    public ResultVO del(@RequestBody() ProductChannelPO po){
        if(null == po.getId())
            return ResultVO.fail("缺少产品渠道ID",null);
        productChannelService.delProductChannel(po.getId());
        return ResultVO.success("产品渠道-新增成功",null);
    }


}
