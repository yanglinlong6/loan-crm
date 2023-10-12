package com.daofen.crm.service.media.model;

import com.daofen.crm.base.BasePO;

import lombok.Getter;
import lombok.Setter;
/**
 * 媒体实体类
 * @author Administrator
 *
 */
@Getter
@Setter
public class MediaPO extends BasePO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 媒体名称
	 */
	private String name;
	/**
	 * 英文简称
	 */
	private String shorthand;
	/**
	 * 媒体状态
	 */
	private Integer state;
	/**
	 * appid
	 */
	private String appid;
	/**
	 * secret
	 */
	private String secret;
	/**
	 * publicKey账户API对接加密使用
	 */
	private String publicKey;
	/**
	 * publicKey账户API对接解密使用
	 */
	private String privateKey;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 所属公司
	 */
	private Long companyId;
	
}
