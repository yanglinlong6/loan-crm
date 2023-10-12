package com.help.loan.distribute.service;

import javax.servlet.http.HttpServletResponse;

public interface ConversionReportService {
	
	void getConversionReport(String startDate, String endDate,HttpServletResponse res);
	
}
