package com.help.loan.distribute.controller;



import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.help.loan.distribute.schedule.CpaScheduleMediaChannelReport;
import com.help.loan.distribute.schedule.CpaScheduleOrgCustReport;
import com.help.loan.distribute.service.ConversionReportService;


/**
 * 转化报表
 * @author kongzhimin
 *
 */
@RestController
public class ReportController {
	
	@Autowired
	private ConversionReportService conversionReportService;
	
	@Autowired
	private CpaScheduleMediaChannelReport cpaScheduleMediaChannelReport;
	
	@Autowired
	private CpaScheduleOrgCustReport cpaScheduleOrgCustReport;
	
	@RequestMapping("/conversion/report/get")
	public void getConversionReport(@RequestParam String startDate,@RequestParam String endDate,HttpServletResponse res)
	{	
		conversionReportService.getConversionReport(startDate, endDate, res);
	}
	
	@RequestMapping("/org/report/get")
	public Integer generateOrgReport()
	{	
		cpaScheduleOrgCustReport.generateOrgReport();
		return 0;
	}
	
	@RequestMapping("/media/report/get")
	public Integer generateMediaReport()
	{	
		cpaScheduleMediaChannelReport.generateMediaReport();
		return 0;
	}
	
}
