package com.daofen.crm.service.customer;

import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.service.customer.model.CustomerPO;
import com.daofen.crm.service.customer.model.MemorandumPO;

@Component
public class ServerEventListener {

	private static ConcurrentHashMap<String,SseEmitter> ssemap = new ConcurrentHashMap<>();
	
	public static void addSseEmitter(String userCode,SseEmitter sse) {
		ssemap.put(userCode, sse);
	}
	
	public static void push(String userCode,String msg) {
		SseEmitter sseEmitter = ssemap.get(userCode);
		if(sseEmitter==null) {
			return ;
		}
		try {
			sseEmitter.send(msg);
			sseEmitter.complete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Scheduled(fixedRate = 50*1000)
	private void trigger() {
		JSONObject o = new JSONObject();
		o.put("type", 0);
		o.put("message", "");
		ssemap.forEach((key,value)->{
			try {
				value.send(o.toJSONString());
				value.complete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	
	public static void sendNewCustMsg(String userCode) {
		JSONObject o = new JSONObject();
		o.put("type", 1);
		o.put("message", "有新客户分配给您，请及时跟进处理");
		push(userCode,o.toJSONString());
	}
	
	public static void sendMemorandum(String userCode,MemorandumPO po,CustomerPO cust) {
		StringBuilder builder = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String format = sdf.format(po.getThingTime());
		builder.append("您好，你在");
		builder.append(format);
		builder.append("待处理：");
		builder.append(cust.getName());
		builder.append("客户，");
		builder.append(po.getContent());
		JSONObject o = new JSONObject();
		o.put("type", 2);
		o.put("message", builder.toString());
		push(userCode,o.toString());
	}
}
