package com.crm.service.esign.helper;

import com.alibaba.fastjson.JSONObject;
import com.crm.service.esign.util.*;
import com.crm.service.esign.vo.ConfigConstant;
import com.crm.service.esign.vo.RequestType;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;


/**
 * @description 文件模板相关辅助类
 * @date 2019年7月20日 下午7:05:42
 * @since JDK1.7
 */
public class FileTemplateHelper {

	private FileTemplateHelper() {
	}

	// -----------------------------------------------------------------------------------------------文件管理相关start--------------

	/**
	 * @description 通过上传方式创建文件
	 *
	 *              说明:
	 *              <p>
	 *              为隔离业务数据流和文件流，e 签宝采用文件直传的方式完成用户文件的上传。对 接方通过该方法获取文件上传的授权地址
	 *              <p>
	 *              对接方直接使用本地待签文件进行签署时，需先调用该接口完成本地文件上传 e 签宝
	 *              <p>
	 *              对接方通过接口创建文件模板时，需要先调用该接口完成模板文件上传 e 签宝
	 *              <p>
	 *              目前仅支持文件格式: pdf
	 *
	 *
	 *              组装参数：
	 *              <p>
	 *              {@link FileTemplateParamUtil#getUploadUrlParam(String, String, String)}
	 *
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午7:13:58
	 */
	public static JSONObject createFileByUpload(String filePath, String fileName, String accountId)
			throws DefineException {
		String param;
		if(StringUtils.isNotBlank(filePath) && filePath.endsWith("pdf")){
			param = FileTemplateParamUtil.getUploadUrlParam2(filePath, fileName, accountId,true);
		}else{
			param = FileTemplateParamUtil.getUploadUrlParam2(filePath, fileName, accountId,false);
		}
		JSONObject json = HttpHelper.doCommHttp(RequestType.POST, ConfigConstant.fileUpload_URL(), param);
		return JSONHelper.castDataJson(json, JSONObject.class);
	}

	public static JSONObject createFileByUpload(String filePath, String fileName, String accountId,Boolean isPdf)
			throws DefineException {
		String param = FileTemplateParamUtil.getUploadUrlParam2(filePath, fileName, accountId,isPdf);
		JSONObject json = HttpHelper.doCommHttp(RequestType.POST, ConfigConstant.fileUpload_URL(), param);
		return JSONHelper.castDataJson(json, JSONObject.class);
	}

	/**
	 * @description 文件流上传文件
	 *
	 *              说明：
	 *              <p>
	 *              要注意正确设置文件的contentMd5、文件MIME以及字节流等信息，否则会导致Http状态为400的异常
	 *
	 *
	 * @param filePath  文件路径
	 * @param uploadUrl 上传方式创建文件时返回的uploadUrl
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @throws IOException
	 * @date 2019年7月20日 下午8:26:03
	 */
	public static void streamUpload(String filePath, String uploadUrl) throws DefineException {

		byte[] bytes = FileHelper.getBytes(filePath);
		String contentMd5 = FileHelper.getContentMD5(filePath);
		String contentType = FileHelper.getContentType(filePath);
		JSONObject json = HttpHelper.doUploadHttp(RequestType.PUT, uploadUrl, bytes, contentMd5, contentType);
		JSONHelper.castDataJson(json, Object.class);
	}

	/**
	 * @description 获取文件下载地址
	 *
	 * @param fileId 文件id
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午8:59:08
	 */
	public static JSONObject getDownLoadUrl(String fileId) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.GET, ConfigConstant.fileDownloadByFileId_URL(fileId), null);
		return JSONHelper.castDataJson(json, JSONObject.class);
	}
	// -----------------------------------------------------------------------------------------------文件管理相关end----------------

	// -----------------------------------------------------------------------------------------------模板管理相关start--------------

	/**
	 * @description 通过上传方式创建模板
	 * 
	 *              组装参数：
	 *              <p>
	 *              {@link FileTemplateParamUtil#addTemplateByUploadUrlParam(String, String)}
	 *
	 * @param filePath 模板路径
	 * @param fileName 模板名称
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午9:09:59
	 */
	public static JSONObject createTemplateByUpload(String filePath, String fileName) throws DefineException {

		String param = FileTemplateParamUtil.addTemplateByUploadUrlParam(filePath, fileName);
		JSONObject json = HttpHelper.doCommHttp(RequestType.POST, ConfigConstant.createTemplateByUpload_URL(), param);
		return JSONHelper.castDataJson(json, JSONObject.class);
	}

	/**
	 * @description 添加输入项组件
	 *
	 *              组装参数：
	 *              <p>
	 *              {@link FileTemplateParamUtil#addTemplateComponentsParam()}
	 *
	 * @param templateId 模板id，通过上传方式创建模板时返回
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午9:18:08
	 */
	@SuppressWarnings("unchecked")
	public static List<String> addComponents(String templateId) throws DefineException {

		String param = FileTemplateParamUtil.addTemplateComponentsParam();
		JSONObject json = HttpHelper.doCommHttp(RequestType.POST, ConfigConstant.addInputNodes_URL(templateId), param);
		return JSONHelper.castDataJson(json, List.class);
	}

	/**
	 * @description 删除输入项组件
	 *
	 * @param templateId 模板ID
	 * @param ids        输入项组件id集合，逗号分隔
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午9:41:24
	 */
	public static void delComponents(String templateId, String ids) throws DefineException {
		HttpHelper.doCommHttp(RequestType.DELETE, ConfigConstant.deleteInputNodes_URL(templateId, ids), null);
	}

	/**
	 * @description 查询输入项详情
	 *
	 * @param templateId 模板ID
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午9:49:16
	 */
	public static JSONObject qryComponents(String templateId) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.GET, ConfigConstant.queryInputNodes_URL(templateId), null);
		return JSONHelper.castDataJson(json, JSONObject.class);
	}

	// -----------------------------------------------------------------------------------------------模板管理相关end----------------

}
