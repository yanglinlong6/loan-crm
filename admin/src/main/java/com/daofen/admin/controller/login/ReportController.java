package com.daofen.admin.controller.login;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.daofen.admin.basic.AbstractController;
import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.basic.ResultVO;
import com.daofen.admin.service.report.ReportService;

@RestController
public class ReportController extends AbstractController {
	
	@Autowired
	private ReportService reportService;
	
	@RequestMapping("/report/channel/get")
    @ResponseBody
    public ResultVO selectChannelReport(@RequestBody()JSONObject params){
        return this.success(ResultCode.SUC.getCode(),ResultCode.SUC.getDesc(),reportService.selectChannelReport(params));
    }
	
}
