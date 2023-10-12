package com.loan.wechat.entrances.processor;

import java.util.EnumMap;
/**
 * 消息类型枚举
 * @author kongzhimin
 *
 */
public enum MsgTypeEnum {
	//文本                                               事件			语音				视频					短视频					图片                                                     其他
	TEXT("text"),EVENT("event"),VOICE("voice"),VIDEO("video"),SHORTVIDEO("shortvideo"),IMAGE("image"),OTHER("other");

	private String msgType;

	private MsgTypeEnum(String msgType) {
		this.msgType = msgType;
	}
	
	public String getMsgType() {
		return msgType;
	}
	/**
	 * 枚举Map集 保存对应消息类型处理器
	 */
	private static EnumMap<MsgTypeEnum,Processor> em = new EnumMap<MsgTypeEnum,Processor>(MsgTypeEnum.class);
	
	static{
		em.put(MsgTypeEnum.TEXT, new TextProcessor());
		em.put(MsgTypeEnum.EVENT, new EventProcessor());
		em.put(MsgTypeEnum.VOICE, new VoiceProcessor());
		em.put(MsgTypeEnum.VIDEO, new VideoProcessor());
		em.put(MsgTypeEnum.SHORTVIDEO, new ShortVideoProcessor());
		em.put(MsgTypeEnum.IMAGE, new ImageProcessor());
		em.put(MsgTypeEnum.OTHER, new OtherProcessor());
	}
	/**
	 * 获取对应消息类型处理器
	 * @param msgType 消息类型
	 * @return
	 */
	public static Processor getProcessor(String msgType) {
		for(MsgTypeEnum type:MsgTypeEnum.values()) {
			if(type.getMsgType().equals(msgType)) {
				return em.get(type);
			}
		}
		return em.get(MsgTypeEnum.OTHER);
	}
	
}
