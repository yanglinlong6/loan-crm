package com.crm;

import com.alibaba.fastjson.JSONObject;
import com.crm.service.esign.FlowBuild;
import com.crm.service.esign.flow.Signer;
import com.crm.service.esign.flow.SignfieldInfo;
import com.crm.service.esign.helper.AccountHelper;
import com.crm.service.esign.helper.FileTemplateHelper;
import com.crm.service.esign.helper.SignHelper;
import com.crm.service.esign.helper.TokenHelper;
import com.crm.service.esign.util.DefineException;
import com.crm.service.esign.vo.FlowConfigInfo;
import com.crm.service.esign.vo.FlowInfo;
import com.crm.service.esign.vo.IdType;
import com.crm.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

@SpringBootTest(classes = LoanCrmApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoanCrmApplicationTests {

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	private static final Logger LOG = LoggerFactory.getLogger(LoanCrmApplicationTests.class);

	@Test
	void contextLoads() {

		String mobile = "13632965527";

		String start = mobile.substring(0,3);
		String end = mobile.substring(8,mobile.length());

	}


	@Test
	public void testEsing() throws FileNotFoundException, URISyntaxException, DefineException {
		String host = "https://smlopenapi.esign.cn";
		String appid = "7438893344";
		String secret = "d35bac891c8663f91a0eb06052ecc026";


//		String sealId = "ed88b4f2-f14b-4fd7-b49d-c62a8a90e5cc"; // 印章id，通过e签宝官网获取对应实名主体下的印章编号。

		TokenHelper.getTokenData(stringRedisTemplate);
		// 发起人
		JSONObject startAccount = AccountHelper.createPersonAcct("18667811968", "王平", IdType.CH_IDCARD, "", "18667811968", null);
		String acctId = startAccount.getString("accountId");
		// 发起签署机构
		JSONObject startOrgAccount = AccountHelper.createOrgAcct("91440300349595705E", null, "深圳市德务商业法律顾问有限公司", IdType.ORG, "91440300349595705E");
		String orgId = startOrgAccount.getString("orgId");

		//签署个人
		JSONObject personAccount = AccountHelper.createPersonAcct("15393723077", "庆锋强", IdType.CH_IDCARD, null, "15393723077", "509124739@qq.com");
		String acctId2 = personAccount.getString("accountId");

		// 上传合同
		// 获取上传文件地址  回去文件id和文件上传地址
		String fileName = "德务客户协议.pdf";
		String filePath = "C:\\Users\\张先森\\Desktop\\德务\\德务客户协议.pdf";
		JSONObject uploadJSON = FileTemplateHelper.createFileByUpload(filePath, fileName, acctId);
		String uploadUrl = uploadJSON.getString("uploadUrl");
		String fileId = uploadJSON.getString("fileId");
		FileTemplateHelper.streamUpload(filePath,uploadUrl);// 上传

		// 上传附件
//		String attachmentName = "张秋平-授权委托书.docx";
//		String attachmentPath = "C:\\Users\\张先森\\Desktop\\德务\\张秋平-授权委托书.docx";
//		JSONObject attachmentUploadJSON = FileTemplateHelper.createFileByUpload(attachmentPath, attachmentName, acctId);
//		String attachmentUploadUrl = attachmentUploadJSON.getString("uploadUrl");
//		String attachmentFileId = attachmentUploadJSON.getString("fileId");
//		FileTemplateHelper.streamUpload(attachmentPath,attachmentUploadUrl); // 上传



		// 个人签署对象
		Signer signerPersonal = new Signer(Boolean.FALSE,1,null,null,null)
				.putSignerAccount(acctId2,null,"1,2",null)
				.putSignField(SignfieldInfo.createDefalutSignfield("0",fileId,null,false).putPosBean("4",253.4645f,283.4645f,18, DateUtil.yyyymmdd2));

		Signer signerPersonal2 = new Signer(Boolean.FALSE,1,null,null,null)
				.putSignerAccount(acctId2,null,"1,2",null)
				.putSignField(SignfieldInfo.createDefalutSignfield("0",fileId,null,false).putPosBean("5",253.4645f,283.4645f,18, DateUtil.yyyymmdd2));


		/**创建机构签署对象*/
		Signer signerOrg = new Signer(Boolean.TRUE,2,null,null,null)
				.putSignerAccount(acctId,orgId,"1,2",null)
				.putSignField(SignfieldInfo.createDefalutSignfield("2",fileId,null,true).putPosBean("4",253.4645669291339f,141.4251f,20, DateUtil.yyyymmdd2)); //1-个人盖章,2-机构盖章, 如果是2则sealId需要传入签章编号


		FlowBuild flowBuild = FlowBuild.createFlow()
				.setFlowInfo(new FlowInfo("测试流程两个签章",acctId,orgId,new FlowConfigInfo("http://dewu.bangzheng100.com/crm/esign/receive","1,2","http://www.baidu.com","1,2")))
				.addSigner(signerOrg)
				.addSigner(signerPersonal)
				.addSigner(signerPersonal2)
				.addDoc(fileId,fileName)
				.addCopier(acctId,1,orgId);// 创建流程对象


		JSONObject flowJSON = SignHelper.oneStepFlow(flowBuild);
		String id = flowJSON.getString("flowId");
		SignHelper.startSignFlow(id);
		System.out.println("发起完成");



//		LOG.info("---------------------一步发起签署start---------------------------------");
//		JSONObject flowJson = SignHelper.oneStepFlow(acctId, fileId, fileName, orgId);
//		String flowId = flowJson.getString("flowId");
//
//		LOG.info("---------------------签署流程开启 start-----------------------------");
//		SignHelper.startSignFlow(flowId);

	}
}
