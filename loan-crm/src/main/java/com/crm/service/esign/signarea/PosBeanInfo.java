package com.crm.service.esign.signarea;

import com.crm.service.esign.flow.SignDateBean;
import lombok.Getter;
import lombok.Setter;

/**
 * @description 签署区位置信息
 * @author 宫清
 * @date 2019年11月19日 下午2:48:29
 * @since JDK1.7
 */
@Setter
@Getter
public class PosBeanInfo {
	
	//页码信息
	private String posPage;
	
	//x坐标
	private Float posX;
	
	//y坐标
	private Float posY;

	private SignDateBean signDateBean;


	public PosBeanInfo(String posPage, Float posX, Float posY) {
		this.posPage = posPage;
		this.posX = posX;
		this.posY = posY;
	}

	public PosBeanInfo(String posPage, Float posX, Float posY, SignDateBean signDateBean) {
		this.posPage = posPage;
		this.posX = posX;
		this.posY = posY;
		this.signDateBean = signDateBean;
	}

	public PosBeanInfo() {
	}
	
	
}
