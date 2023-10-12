package com.crm.service.esign.helper;

import com.alibaba.fastjson.JSONObject;
import com.crm.service.esign.util.AccountParamUtil;
import com.crm.service.esign.util.DefineException;
import com.crm.service.esign.vo.ConfigConstant;
import com.crm.service.esign.vo.RequestType;


/**
 * @description 账号相关 辅助类
 */
public class AccountHelper {

	/**
	 * 不允许外部创建实例
	 */
	private AccountHelper() {
	}


	// --------------------------------------------------------------------------------------------------个人账号相关start-----------

	/**
	 * 创建个人账号,说明：
	 * 对接方调用本接口在 e 签宝平台中创建个人账号，生成的accountId请妥善保管,后续有关该用户的所有 操作都需使用该用户的
	 * accountId。如提供用户证件信息，则将根据提供的用户证件 信息申请数字证书
	 * @param thirdPartyUserId  创建个人账号的唯一标识。可将个人证件号、手机号、邮箱地址等作为此账号的唯一标识。
	 * @param name
	 * @param idType
	 *  	证件类型，默认CRED_PSN_CH_IDCARD
	 * 		（1）CRED_PSN_CH_IDCARD 大陆身份证，默认值
	 * 		（2）CRED_PSN_CH_TWCARD 台湾来往大陆通行证
	 * 		（3）CRED_PSN_CH_MACAO 澳门来往大陆通行证
	 * 		（4）CRED_PSN_CH_HONGKONG 香港来往大陆通行证
	 * 		（5）CRED_PSN_PASSPORT 护照
	 * @param idNumber 证件号，需传入真实存在的证件信息  注：身份证中有X字母的，需要传入大写的X
	 * @param mobile  手机号码，可空  注：手机号为空时，无法通过短信接收签署相关通知， 且签署过程无法使用短信验证码做意愿认证
	 * @param email 邮箱地址，默认空 邮箱为空时，无法通过邮箱接收签署相关邮件通知
	 * @return
		 * {
		 *     "code":0,
		 *     "message":"成功",
		 *     "data":{
		 *         "accountId":"1eaf205d5d6d4e579a9d221dxxx"
		 *     }
		 * }
	 * @throws DefineException
	 */
	public static JSONObject createPersonAcct(String thirdPartyUserId, String name, String idType, String idNumber,
			String mobile, String email) throws DefineException {

		String param = AccountParamUtil.createPersonAcctParam(thirdPartyUserId, name, idType, idNumber, mobile, email);
		try{
			JSONObject json = HttpHelper.doCommHttp(RequestType.POST, ConfigConstant.createPerAcc_URL(), param);
			return JSONHelper.castDataJson(json,JSONObject.class);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @description 个人账号修改【 按照账号 ID】
	 *
	 *              说明：
	 *              <p>
	 *              对接方在 e 签宝平台中使用账号 id 更新个人账号，如变更姓 名，则系统根据变更后的身份信息自动申请新数字证书，
	 *              如账号已实名则会变更为未 实名状态。
	 * 
	 *              <p>
	 *              组装参数：
	 *              {@link AccountParamUtil#updatePersonAcctParam(String, String, String)}
	 *
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午4:58:01
	 */
	public static JSONObject updatePersonAcctByAcctId(String accountId, String email, String mobile, String name)
			throws DefineException {

		String param = AccountParamUtil.updatePersonAcctParam(email, mobile, name);
		JSONObject json = HttpHelper.doCommHttp(RequestType.PUT, ConfigConstant.modifyPerAccById_URL(accountId), param);
		return JSONHelper.castDataJson(json,JSONObject.class);
	}

	/**
	 * @description 个人账号修改【按照第三方用户 ID】
	 *
	 *              说明：
	 *              <p>
	 *              对接方在 e 签宝平台中使用第三方用户 id 更新个人账号 ，如 变更姓名，则系统根据变更后的身份信息自动申请新数字证书，
	 *              如账号已实名则会变 更为未实名状态。
	 * 
	 *              <p>
	 *              组装参数：
	 *              {@link AccountParamUtil#updatePersonAcctParam(String, String, String)}
	 *
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午4:58:01
	 */
	public static JSONObject updatePersonAcctByThirdId(String thirdId, String email, String mobile, String name)
			throws DefineException {

		String param = AccountParamUtil.updatePersonAcctParam(email, mobile, name);
		JSONObject json = HttpHelper.doCommHttp(RequestType.POST, ConfigConstant.modifyPerAccByThirdId_URL(thirdId), param);
		return JSONHelper.castDataJson(json,JSONObject.class);
	}

	/**
	 * @description 查询个人账号【按照账号ID】
	 * 
	 *              说明：
	 *              <p>
	 *              使用创建账号返回的账号 id 查询用户的账号
	 *
	 * @param accountId 创建返回的账号id
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午5:09:49
	 */
	public static JSONObject qryPersonAcctByAcctId(String accountId) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.GET, ConfigConstant.queryAccById_URL(accountId), null);
		return JSONHelper.castDataJson(json,JSONObject.class);
	}

	/**
	 * @description 查询个人账号【按照第三方用户 ID】
	 * 
	 *              说明：
	 *              <p>
	 *              当对接方创建账户时传入第三方平台的 userId 时，支持使用第三方平台的 userId 查询用户的账号
	 *
	 * @param thirdId 创建账户时传入的第三方用户id
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午5:09:49
	 */
	public static JSONObject qryPersonAcctByThirdId(String thirdId) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.GET, ConfigConstant.queryAccByThirdId_URL(thirdId), null);
		return JSONHelper.castDataJson(json,JSONObject.class);
	}

	/**
	 * @description 注销个人账号【按照账号 ID】
	 * 
	 *              说明：
	 *              <p>
	 *              1. 通过账号 id 注销 e 签宝平台的个人账号
	 *              <p>
	 *              2. 注销后的账号不能再发起签署，已发起的流程也无法继续签署
	 *
	 * @param accountId 创建返回的账号id
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午5:13:57
	 */
	public static void delPersonAcctByAcctId(String accountId) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.DELETE, ConfigConstant.logoutAccById_URL(accountId), null);
		JSONHelper.castDataJson(json,Object.class);
	}

	/**
	 * @description 注销个人账号【按照第三方用户 ID】
	 * 
	 *              说明： 当对接方创建账户时传入第三方平台的 userId 时，
	 *              <p>
	 *              1. 通过第三方平台的 userId 注销 e 签宝平台的个人账号
	 *              <p>
	 *              2. 注销后的账号不能再发起签署，但可以在已发起的流程中继续进行签署
	 *
	 * @param thirdId 创建账户时传入的第三方用户id
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午5:15:54
	 */
	public static void delPersonAcctByThirdId(String thirdId) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.DELETE, ConfigConstant.logoutAccByThirdId_URL(thirdId), null);
		JSONHelper.castDataJson(json,Object.class);
	}

	// --------------------------------------------------------------------------------------------------个人账号相关end-------------

	// --------------------------------------------------------------------------------------------------机构账号相关start-------------

	/**
	 * @description 创建机构账号
	 * 
	 *              说明：
	 *              <p>
	 *              1. 对接方调用本接口在 e 签宝平台中创建机构账号，后续有关该机构的所有操作都 需使用该机构的
	 *              accountId。如提供机构证件信息，则将根据提供的机构证件信 息申请数字证书。
	 * 
	 *              <p>
	 *              2. 由于机构是一个组织实体，机构账号时需要机构创建人执行，同时如该机构创建
	 *              人完成机构实名认证，则在签署和管理操作时需要由创建人执行，故在创建机构
	 *              时需要先调用{@link AccountHelper#createPersonAcct}接口完成机构创建人创建，并将机构创建人的账号
	 *              Id 传入接口
	 * 
	 *              组装参数：
	 *              <p>
	 *              {@link AccountParamUtil#createOrgAcctParam(String, String, String, String, String)}
	 *
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午5:23:31
	 */
	public static JSONObject createOrgAcct(String thirdPartyUserId, String creatorId, String name, String idType,
			String idNumber) throws DefineException {

		String param = AccountParamUtil.createOrgAcctParam(thirdPartyUserId, creatorId, name, idType, idNumber);
		JSONObject json = HttpHelper.doCommHttp(RequestType.POST, ConfigConstant.createOrgAcc_URL(), param);
		return JSONHelper.castDataJson(json,JSONObject.class);
	}

	/**
	 * @description 机构账号修改【按照账号 ID】
	 * 
	 *              说明：
	 *              <p>
	 *              对接方调用本接口在 e 签宝平台中使用账号 id 更新机构账号，如变更名 称后，则系统根据变更后的信息自动申请新数字证书，
	 *              如账号已实名则会变更为未实 名状态。
	 *
	 *
	 *              组装参数：
	 *              <p>
	 *              {@link AccountParamUtil#updateOrgAcct(String)}
	 * @return
	 * @author 宫清
	 * @date 2019年7月20日 下午5:43:43
	 */
	public static JSONObject updateOrgAcctByOrgId(String orgId, String name) throws DefineException {
		String param = AccountParamUtil.updateOrgAcct(name);
		JSONObject json = HttpHelper.doCommHttp(RequestType.PUT, ConfigConstant.modifyOrgAccById_URL(orgId), param);
		return JSONHelper.castDataJson(json,JSONObject.class);
	}

	/**
	 * @description 机构账号修改【按照第三方用户 ID】
	 * 
	 *              说明：
	 *              <p>
	 *              对接方调用本接口在 e 签宝平台中使用账号 id 更新机构账号，如变更名 称后，则系统根据变更后的信息自动申请新数字证书，
	 *              如账号已实名则会变更为未实 名状态。
	 *
	 *
	 *              组装参数：
	 *              <p>
	 *              {@link AccountParamUtil#updateOrgAcct(String)}
	 * @return
	 * @author 宫清
	 * @date 2019年7月20日 下午5:43:43
	 */
	public static JSONObject updateOrgAcctByThirdId(String thirdId, String name) throws DefineException {
		String param = AccountParamUtil.updateOrgAcct(name);
		JSONObject json = HttpHelper.doCommHttp(RequestType.POST, ConfigConstant.modifyOrgAccByThirdId_URL(thirdId), param);
		return JSONHelper.castDataJson(json,JSONObject.class);
	}

	/**
	 * @description 查询机构账号【按照机构ID】
	 *
	 *              说明：
	 *              <p>
	 *              使用创建账号返回的账号 id 查询机构账号
	 *
	 * @param orgId 创建机构账号返回的机构ID
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午5:52:06
	 */
	public static JSONObject qryOrgAcctByOrgId(String orgId) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.GET, ConfigConstant.queryOrgAccById_URL(orgId), null);
		return JSONHelper.castDataJson(json,JSONObject.class);
	}

	/**
	 * @description 查询机构账号【按照第三方用户 ID】
	 *
	 *              说明：
	 *              <p>
	 *              当对接方创建账户时传入第三方平台的 userId 时，支持使用第三方平台的 userId 查询机构的账号
	 *
	 * @param thirdId 第三方平台的 userId
	 * @return
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午5:52:06
	 */
	public static JSONObject qryOrgAcctByThirdId(String thirdId) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.GET, ConfigConstant.queryOrgAccByThirdId_URL(thirdId), null);
		return JSONHelper.castDataJson(json,JSONObject.class);
	}

	/**
	 * @description 注销机构账号【按照账号 ID】
	 *
	 *              说明：
	 *              <p>
	 *              通过账号 id 注销 e 签宝平台的机构账号，注销后的账号不能再发起签署， 已发起的流程也无法继续签署
	 *
	 * @param orgId 创建机构时返回的账号
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午5:55:39
	 */
	public static void delOrgAcctByOrgId(String orgId) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.DELETE, ConfigConstant.logoutOrgAccById_URL(orgId), null);
		JSONHelper.castDataJson(json,Object.class);
	}

	/**
	 * @description 注销机构账号【按照第三方用户 ID】
	 *
	 *              说明：
	 *              <p>
	 *              通过账号 id 注销 e 签宝平台的机构账号，注销后的账号不能再发起签署， 已发起的流程也无法继续签署
	 *
	 * @param thirdId 按照第三方用户 ID
	 * @throws DefineException
	 * @author 宫清
	 * @date 2019年7月20日 下午5:55:39
	 */
	public static void delOrgAcctByThirdId(String thirdId) throws DefineException {
		JSONObject json = HttpHelper.doCommHttp(RequestType.DELETE, ConfigConstant.logoutOrgAccByThirdId_URL(thirdId),
				null);
		JSONHelper.castDataJson(json,Object.class);
	}
	// --------------------------------------------------------------------------------------------------机构账号相关end---------------

	// --------------------------------------------------------------------------------------------------邀请开通任务相关start---------

	/**
	 * @description 创建邀请任务
	 * 
	 *              组装参数：
	 *              <p>
	 *              {@link AccountParamUtil#createInvitions(String, String, String, String, String)}
	 *              
	 * @throws DefineException
	 * @return
	 * @author 宫清
	 * @date 2019年7月20日 下午6:37:37
	 */
	public static JSONObject createInvitions(String inviteeOid, String redirectURL, String callbackURL, String bizId,
			String notifyType) throws DefineException {
		String param = AccountParamUtil.createInvitions(inviteeOid, redirectURL, callbackURL, bizId, notifyType);
		JSONObject json = HttpHelper.doCommHttp(RequestType.POST, ConfigConstant.create_invite_URL(), param);
		return JSONHelper.castDataJson(json, JSONObject.class);
	}

	// --------------------------------------------------------------------------------------------------邀请开通任务相关end-----------


}
