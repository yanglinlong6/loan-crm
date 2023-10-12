package com.crm.controller.org;

import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.service.org.ShopService;
import com.crm.service.org.model.ShopPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopController {

    @Autowired
    ShopService shopService;

    @PostMapping("/shop/page")
    @ResponseBody
    public ResultVO page(@RequestBody PageBO<ShopPO> page){
        shopService.getShopByPage(page);
        return ResultVO.success("获取门店分页列表成功",page);
    }

    @PostMapping("/shop/add")
    @ResponseBody
    public ResultVO add(@RequestBody()ShopPO shopPO){
        shopService.addShop(shopPO);
        return ResultVO.success("新增门店成功",shopPO);
    }

    @PostMapping("/shop/update")
    @ResponseBody
    public ResultVO update(@RequestBody()ShopPO shopPO){
        shopService.updateShop(shopPO);
        return ResultVO.success("更新门店成功",shopPO);
    }

    @PostMapping("/shop/list")
    public ResultVO getAll(@RequestBody(required = false) ShopPO shop){
        Byte type = null;
        if(null != shop){
            type = shop.getType();
        }
        return ResultVO.success("获取全部门店列表成功",shopService.getAll(type));
    }




}
