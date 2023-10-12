package com.daofen.crm.controller.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.service.customer.MemorandumService;
import com.daofen.crm.service.customer.model.MemorandumPO;

@RestController
public class MemorandumController extends AbstractController {

	@Autowired
	private MemorandumService memorandumService;
	
	@RequestMapping("/memorandum/add")
    public ResultVO addMemorandum(@RequestBody MemorandumPO po){
		memorandumService.add(po);
        return this.success();
    }
	
	@RequestMapping("/memorandum/del")
    public ResultVO delMemorandum(@RequestParam Long id){
		memorandumService.del(id);
        return this.success();
    }

	
}
