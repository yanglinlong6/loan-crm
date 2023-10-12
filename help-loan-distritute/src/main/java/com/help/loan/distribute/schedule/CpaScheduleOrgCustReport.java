package com.help.loan.distribute.schedule;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Component
public class CpaScheduleOrgCustReport {
	
	@Autowired
	private ReportService reportService;
	
	private static final Logger log = LoggerFactory.getLogger(CpaScheduleOrgCustReport.class);
	
//	@Scheduled(cron = "0 0 9 * * ?")
	public void generateOrgReport() {
		log.info("org report generate start");
		List<JSONObject> orgApt = getOrgApt();
		Map<String, List<JSONObject>> collect = orgApt.stream().collect(Collectors.groupingBy(o ->o.getString("org_name")));
		List<JSONObject> report = new ArrayList<JSONObject>();
		List<JSONObject> selOrgDetailedReport = reportService.selOrgDetailedReport();
		collect.entrySet().stream().forEach(e ->{
			String channel = e.getKey();
			List<JSONObject> value = e.getValue();
			Map<String, List<JSONObject>> collect2 = value.stream().collect(Collectors.groupingBy(o ->o.getString("city")));
			for(Entry<String, List<JSONObject>> en:collect2.entrySet()) {
				String city = en.getKey();
				double[] conversionRate = getConversionRate(selOrgDetailedReport,channel,city);
				List<JSONObject> value2 = en.getValue();
				JSONObject r = new JSONObject();
				r.put("v1", 0);
				r.put("v2", 0);
				r.put("v3", 0);
				r.put("v4", 0);
				r.put("v5", 0);
				r.put("org_name", channel);
				r.put("city", city);
				r.put("quality1", 0);
				r.put("quality2", 0);
				r.put("quality3", 0);
				int sum = 0;
				int quality = 0;
				for(JSONObject jsonb:value2) {
					Integer aptitude = jsonb.getInteger("aptitude");
					Integer num = jsonb.getInteger("num");
					sum =sum+ num;
					if(aptitude>=4) {
						quality = quality+num;
					}
					r.put("v"+aptitude.toString(), num);
				}
				r.put("quality1", BigDecimal.valueOf(quality).divide(BigDecimal.valueOf(sum),3,RoundingMode.HALF_UP).doubleValue());
				BigDecimal multiply = BigDecimal.valueOf(quality).multiply(BigDecimal.valueOf(conversionRate[1])).add(BigDecimal.valueOf(sum-quality).multiply(BigDecimal.valueOf(conversionRate[0])));
				double qualityNum = multiply.divide(BigDecimal.valueOf(sum),3,RoundingMode.HALF_UP).doubleValue();
				r.put("quality2", qualityNum);
				report.add(r);
			}
		});;
		reportService.insertOrgCustAptReport(report);
		log.info("org report generate end");
	}
	
	private List<JSONObject> getOrgApt(){
		JSONObject o = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String format = sdf.format(cal.getTime());
		o.put("startDate", format+" 00:00:00");
		o.put("endDate", format+" 23:59:59");
		return reportService.getOrgApt(o);
	}
	
	private double[] getConversionRate(List<JSONObject> selOrgDetailedReport,String orgName,String city) {
		double[] cc = {0.1d,0.4d};
		if(selOrgDetailedReport == null || selOrgDetailedReport.size()==0) {
			return cc;
		}
		for(JSONObject o:selOrgDetailedReport) {
			if(o.getString("org_name").contains(orgName)&&o.getString("city").contains(city)) {
				cc[0]=o.getDoubleValue("low_quality");
				cc[1]=o.getDoubleValue("high_quality");
			}
		}
		return cc;
	}
	
}
