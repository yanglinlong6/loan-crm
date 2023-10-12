package com.daofen.admin.service.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.daofen.admin.basic.AdminException;
import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.service.report.dao.ReportDao;

@Component
public class IReportService implements ReportService{
	
	@Autowired
	private ReportDao reportDao;
	
	private static final Logger log = LoggerFactory.getLogger(IReportService.class);
	
	@Override
	public JSONArray selectChannelReport(JSONObject params) {
		if(params.getInteger("dimension") == null) {
			params.put("dimension", 1);
		}
		if(params.getIntValue("dimension")<1||params.getIntValue("dimension")>3) {
			throw new AdminException(ResultCode.FAID,"param err");
		}
		if(params.getString("start")==null||params.getString("end")==null) {
			Calendar ca = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(params.getIntValue("dimension")==1) {
				Date time = ca.getTime();
				ca.add(Calendar.DATE, -7);
				Date time2 = ca.getTime();
				params.put("end", sdf.format(time));
				params.put("start", sdf.format(time2));
			}else if(params.getIntValue("dimension")==2) {
				Date time = ca.getTime();
				ca.add(Calendar.WEEK_OF_YEAR, -7);
				Date time2 = ca.getTime();
				params.put("end", sdf.format(time));
				params.put("start", sdf.format(time2));
			}else if(params.getIntValue("dimension")==3) {
				Date time = ca.getTime();
				ca.add(Calendar.MONTH, -7);
				Date time2 = ca.getTime();
				params.put("end", sdf.format(time));
				params.put("start", sdf.format(time2));
			}
		}
		params.put("end", params.getString("end")+" 23:59:59");
		params.put("start", params.getString("start")+" 00:00:00");
		List<JSONObject> selectChannelReport = reportDao.selectChannelReport(params);
		JSONArray result = new JSONArray();
		for(JSONObject o:selectChannelReport) {
			int quality = o.getIntValue("v5")+o.getIntValue("v4");
			int total =  o.getIntValue("v5")+o.getIntValue("v4")+ o.getIntValue("v3")+o.getIntValue("v2")+ o.getIntValue("v1");
			o.put("rate", BigDecimal.valueOf(quality).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP).doubleValue());
			result.add(o);
		}
		return result;
	}
	
}
