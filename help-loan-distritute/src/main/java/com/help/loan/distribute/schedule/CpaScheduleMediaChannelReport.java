package com.help.loan.distribute.schedule;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
public class CpaScheduleMediaChannelReport {
	
//	@Autowired
	private ReportService reportService;
	
	private static final Logger log = LoggerFactory.getLogger(CpaScheduleMediaChannelReport.class);
	
//	@Scheduled(cron = "0 0 9 * * ?")
	public void generateMediaReport() {
		log.info("media report generate start");
		List<JSONObject> mediaChannelCustApt = getMediaChannelCustApt();
		Map<String, List<JSONObject>> collect = mediaChannelCustApt.stream().collect(Collectors.groupingBy(o ->o.getString("channel")));
		List<JSONObject> report = new ArrayList<JSONObject>();
		collect.entrySet().stream().forEach(e ->{
			String channel = e.getKey();
			List<JSONObject> value = e.getValue();
			Map<String, List<JSONObject>> collect2 = value.stream().collect(Collectors.groupingBy(o ->o.getString("city")));
			for(Entry<String, List<JSONObject>> en:collect2.entrySet()) {
				String city = en.getKey();
				List<JSONObject> value2 = en.getValue();
				JSONObject r = new JSONObject();
				r.put("v1", 0);
				r.put("v2", 0);
				r.put("v3", 0);
				r.put("v4", 0);
				r.put("v5", 0);
				r.put("channel", channel);
				r.put("city", city);
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
				r.put("quality", BigDecimal.valueOf(quality).divide(BigDecimal.valueOf(sum),3,RoundingMode.HALF_UP).doubleValue());
				report.add(r);
			}
		});;
		reportService.insertMediaCustAptReport(report);
		log.info("media report generate end");
	}
	
	private List<JSONObject> getMediaChannelCustApt(){
		JSONObject o = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String format = sdf.format(cal.getTime());
		o.put("startDate", format+" 00:00:00");
		o.put("endDate", format+" 23:59:59");
		return reportService.getMediaChannelCustApt(o);
	}
	
}
