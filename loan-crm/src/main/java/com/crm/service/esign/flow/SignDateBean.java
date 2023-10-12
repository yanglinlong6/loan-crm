package com.crm.service.esign.flow;

import lombok.Getter;
import lombok.Setter;

/**
 * @description 签署日期信息
 * @author 宫清
 * @date 2019年11月19日 下午2:46:03
 * @since JDK1.7
 */
@Getter
@Setter
public class SignDateBean {
	
	//是否添加签署日期
	private Boolean addSignTime;
	
	//签章日期字体大小
	private Integer fontSize;
	
	//签章日期格式
	private String format;

	//页码信息
	private String posPage;

	//x坐标
	private Float posX;

	//y坐标
	private Float posY;

	public SignDateBean(Boolean addSignTime, Integer fontSize, String format, String posPage, Float posX, Float posY) {
		this.addSignTime = addSignTime;
		this.fontSize = fontSize;
		this.format = format;
		this.posPage = posPage;
		this.posX = posX;
		this.posY = posY;
	}

	public SignDateBean(Boolean addSignTime, Integer fontSize, String format) {
		this.addSignTime = addSignTime;
		this.fontSize = fontSize;
		this.format = format;
	}

	public SignDateBean() {
	}
	
	
}
