package com.loan.cps.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loan.cps.common.R;
import com.loan.cps.entity.Session;
import com.loan.cps.entity.UserApplyRecordPO;
import com.loan.cps.process.NodeManager;
import com.loan.cps.service.SessionService;
import com.loan.cps.service.UserApplyRecordService;


@RestController
public class LenderController {
	
	@Autowired
	private UserApplyRecordService userApplyRecordService;
	
	@Autowired
	private SessionService sessionService;
	
	@Autowired
	private NodeManager nodeManager;
	
	private static final Log logger = LogFactory.getLog(LenderController.class);
	
	@RequestMapping(value = "/lender/apply")
	public String exc(@RequestParam String uuid,@RequestParam String lenderId,@RequestParam byte location) {
		logger.info("user = "+uuid+" apply lenderid = "+lenderId);
		UserApplyRecordPO po = new UserApplyRecordPO();
		po.setUserId(uuid);
		po.setLenderId(lenderId);
		po.setResource(location);
		String result = "";
		if(userApplyRecordService.addApplyRecord(po)) {
			result = "<script>window.location.href='"+po.getRedirectUrl()+"';</script>";
		}else {
			result = "<script>alert('该产品申请已满，您的资质可以试试其它很多产品哦');</script>";
		}
		return result;
	}
	
	@RequestMapping(value = "/lender/recommend")
	public R recommend(@RequestParam String uuid) {
		logger.info("recommend lenders user = "+uuid);
		Session session = sessionService.getSession(uuid);
		if(session!=null) {
			if(session.getSettlement()==null) {
				session.setSettlement(1);
			}
			nodeManager.getNode(NodeManager.FINISH3).sendQuestion(session);
			sessionService.saveSession(session);
		}else {
			logger.info("recommend lenders user = "+uuid+" failed user unsub");
		}
		return R.ok();
	}
}
