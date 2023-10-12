package com.crm.service.esign.vo;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @description 附件信息
 * @date 2019年11月19日 下午2:22:40
 * @since JDK1.7
 */
@Setter
@Getter
public class Attachment {

	//附件Id
	private String fileId;
	//附件名称
	private String attachmentName;

	public Attachment(String fileId, String attachmentName) {
		this.fileId = fileId;
		this.attachmentName = attachmentName;
	}
	public Attachment() {
	}


	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}
}
