package com.crm.service.esign.flow;

import com.crm.service.esign.signarea.PosBeanInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

@Setter
@Getter
public class SignfieldInfo {

	private boolean assignedPosbean=false;

	//是否自动执行
	private Boolean autoExecute;

	//文件fileId
	private String fileId;

	/** 印章id，通过e签宝官网获取对应实名主体下的印章编号*/
	private String sealId;

	//机构签约类别
	private String actorIndentityType="0";

	/**签署方式，个人签署时支持多种签署方式，0-手绘签名  ，1-模板印章签名，多种类型时逗号分割，为空不限制*/
	private String sealType;

	//签署类型
	private Integer signType=1; // 签署类型，0-不限，1-单页签署，2-骑缝签署，默认1

	//签署区的宽度
	private Integer width;

	/**是否需要添加签署日期，0-禁止 1-必须 2-不限制，默认0*/
	private int signDateBeanType = 1;

	/**签署日期信息*/
	private SignDateBean signDateBean;

	/**签署区位置信息 */
	private PosBeanInfo posBean;



	public SignfieldInfo(Boolean autoExecute, String actorIndentityType, String fileId, String sealType,
                         SignDateBean signDateBean, Integer signType, PosBeanInfo posBean, Integer width) {
		this.autoExecute = autoExecute;
		this.actorIndentityType = actorIndentityType;
		this.fileId = fileId;
		this.sealType = sealType;
		this.signDateBean = signDateBean;
		this.signType = signType;
		this.posBean = posBean;
		this.width = width;
	}
	public SignfieldInfo() {
	}

	/**
	 * 创建默认的签署文件信息对象
	 * @param actorIndentityType 企业主体签约类型：0-个人盖章，2-机构盖章；<必填>
	 * @param fileId 文件fileId <必填>
	 * @param sealId 印章id，通过e签宝官网获取对应实名主体下的印章编号。<非必填,如果actorIndentityType=2,则必填>
	 * @return SignfieldInfo 当前对象
	 */
	public static SignfieldInfo createDefalutSignfield(String actorIndentityType,String fileId,String sealId,Boolean autoExecute){
		SignfieldInfo signfieldInfo = new SignfieldInfo();
		signfieldInfo.setAssignedPosbean(false);
		signfieldInfo.setAutoExecute(autoExecute);
		signfieldInfo.setActorIndentityType(actorIndentityType);
		signfieldInfo.setFileId(fileId);
		signfieldInfo.setSealId(sealId);
		signfieldInfo.setSealType(null);//签署方式，个人签署时支持多种签署方式，0-手绘签名  ，1-模板印章签名，多种类型时逗号分割，为空不限制
		signfieldInfo.setSignType(1);//签署类型，0-不限，1-单页签署，2-骑缝签署，默认1
		signfieldInfo.setSignDateBeanType(1); // 是否需要添加签署日期，0-禁止 1-必须 2-不限制，默认0
		return signfieldInfo;
	}





	/**
	 * 这是签署位置
	 * @param posPage，
	 * 	当签署区signType为2时, 页码可以'-'分割指定页码范围, 传all代表全部页码。其他情况只能是数字
	 * @param posX x坐标，坐标为印章中心点
	 * @param posY y坐标，坐标为印章中心点
	 * @param fontSize 字体大小
	 * @param dateFormat 签署日期格式
	 * @return SignfieldInfo 当前对象
	 */
	public SignfieldInfo putPosBean(String posPage,Float posX,Float posY,int fontSize,String dateFormat){
		if(StringUtils.isBlank(posPage) && null == posX && null == posY){
			return this;
		}
		PosBeanInfo posBeanInfo = new PosBeanInfo(posPage,posX,posY,new SignDateBean(true,fontSize,dateFormat,posPage,posX,posY));
		this.setPosBean(posBeanInfo);
		return this;
	}



	
	
}
