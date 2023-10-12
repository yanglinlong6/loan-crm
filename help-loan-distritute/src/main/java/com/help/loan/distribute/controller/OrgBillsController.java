package com.help.loan.distribute.controller;

import com.help.loan.distribute.common.ResultCode;
import com.help.loan.distribute.common.ResultVO;
import com.help.loan.distribute.service.orgOffer.OrgBillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

/**
 * 机构结算接口
 */
@RestController
public class OrgBillsController {

    @Autowired
    private OrgBillsService orgBillsService;

    @RequestMapping("/org/bills")
    public ResultVO orgBills(@PathParam("dateStr") String dateStr) throws InterruptedException {
        orgBillsService.bills(dateStr);
        return new ResultVO(ResultCode.SUCCESS,"正在结算，请稍后");
    }



}
