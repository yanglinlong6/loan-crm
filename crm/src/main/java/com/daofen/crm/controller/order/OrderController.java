package com.daofen.crm.controller.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.service.order.OrderFacade;
import com.daofen.crm.service.order.model.BankPO;
import com.daofen.crm.service.order.model.OrderPO;

import javax.websocket.server.PathParam;

@RestController
public class OrderController extends AbstractController{
	
	@Autowired
	private OrderFacade orderFacade;
	
	@RequestMapping("/order/list/get")
    @ResponseBody
    public ResultVO getOrderList(@RequestBody()PageVO<OrderPO> pageVO){
        return this.success(orderFacade.getOrderList(pageVO));
    }
	
	@RequestMapping("/order/add")
    @ResponseBody
    public ResultVO addOrder(@RequestBody() OrderPO po){
		orderFacade.addOrder(po);
        return this.success();
    }
	
	@RequestMapping("/order/update")
    @ResponseBody
    public ResultVO updateOrder(@RequestBody() OrderPO po){
		orderFacade.updateOrder(po);
        return this.success();
    }
	
	@RequestMapping("/order/del")
    @ResponseBody
    public ResultVO delOrder(@PathParam("id")Long id){
		orderFacade.delOrder(id);
        return this.success();
    }
	
	@RequestMapping("/order/list/del")
    @ResponseBody
    public ResultVO delOrderList(@RequestParam String idList){
		String[] split = idList.split(",");
		for(String s:split) {
			orderFacade.delOrder(Long.valueOf(s));
		}
        return this.success();
    }
	
	@RequestMapping("/order/bank/list/get")
    @ResponseBody
    public ResultVO getBankList(@RequestBody()PageVO<BankPO> pageVO){
        return this.success(orderFacade.getBankList(pageVO));
    }
	
	@RequestMapping("/order/bank/add")
    @ResponseBody
    public ResultVO addBank(@RequestBody() BankPO po){
		orderFacade.addBank(po);
        return this.success();
    }
}
