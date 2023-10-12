package com.loan.wechat.entrances.processor;

import java.util.EnumMap;

public enum EventTypeEnum {

	UNSUBSCRIBE("unsubscribe"),SUBSCRIBE("subscribe"),LOCATION("LOCATION"),CLICK("CLICK"),VIEW("VIEW"),TEMPLATESENDJOBFINISH("TEMPLATESENDJOBFINISH"),OTHER("other"),SCAN("SCAN");

	private String eventType;

	private EventTypeEnum(String eventType) {
		this.eventType = eventType;
	}
	
	public String getEventType() {
		return eventType;
	}

	private static EnumMap<EventTypeEnum,Processor> em = new EnumMap<EventTypeEnum,Processor>(EventTypeEnum.class);
	
	static{
		em.put(EventTypeEnum.UNSUBSCRIBE, new UnsubProcessor());
		em.put(EventTypeEnum.SUBSCRIBE, new SubProcessor());
		em.put(EventTypeEnum.LOCATION, new LocationProcessor());
		em.put(EventTypeEnum.CLICK, new ClickProcessor());
		em.put(EventTypeEnum.VIEW, new ViewProcessor());
		em.put(EventTypeEnum.OTHER, new OtherProcessor());
		em.put(EventTypeEnum.TEMPLATESENDJOBFINISH, new TempProcessor());
		em.put(EventTypeEnum.SCAN, new ScanProcessor());
	}
	
	public static Processor getProcessor(String eventType) {
		for(EventTypeEnum type:EventTypeEnum.values()) {
			if(type.getEventType().equals(eventType)) {
				return em.get(type);
			}
		}
		return em.get(EventTypeEnum.OTHER);
	}
	
}
