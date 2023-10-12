package com.daofen.crm.controller.company;

import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.company.ShopService;
import com.daofen.crm.service.company.model.ShopBO;
import com.daofen.crm.service.company.model.ShopPO;
import com.daofen.crm.utils.JudgeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
public class ShopController extends AbstractController {

    @Autowired
    private ShopService shopService;

    /**
     * 门店模块:分页接口
     * @param pageVO PageVO<ShopBO>
     * @return ResultVO
     */
    @RequestMapping("/shop/list")
    @ResponseBody
    public ResultVO shopList(@RequestBody() PageVO<ShopBO> pageVO){
        shopService.getPage(pageVO);
        return this.success(pageVO);
    }

    @RequestMapping("/shop/add")
    @ResponseBody
    public ResultVO addShop(@Validated()@RequestBody()ShopPO shopPO){
        if(null == shopPO.getCompanyId())
            shopPO.setCompanyId(LoginUtil.getLoginUser().getCompany().getId());
        shopService.addShop(shopPO);
        return this.success();
    }


    @RequestMapping("/shop/edit")
    @ResponseBody
    public ResultVO editShop(@Validated()@RequestBody()ShopPO shopPO){
        shopService.saveShop(shopPO);
        return this.success();
    }

    @RequestMapping("/shop/del")
    @ResponseBody
    public ResultVO delShop(@PathParam("id") Long id){
        if(null == id || id <= 0 )
            return this.failed("请选择要删除的门店",null);
        byte roleType = LoginUtil.getLoginUser().getRole().getType();
        if(!JudgeUtil.in(roleType,CrmConstant.Role.Type.COMPANY,CrmConstant.Role.Type.ADMIN)){
            return this.failed("只有公司管理员才能删除门店",null);
        }
        shopService.delShop(id);
        return this.success();
    }


    /**
     * 获取公司下的门店
     * @param companyId 公司id
     * @return ResultVO
     */
    @GetMapping("/shop/by/companyId")
    @ResponseBody
    public ResultVO getAll(@PathParam("companyId") Long companyId){
        if(null == companyId)
            companyId = LoginUtil.getLoginUser().getCompanyId();
        return this.success(shopService.getShopListByCompanyId(companyId));
    }


}
