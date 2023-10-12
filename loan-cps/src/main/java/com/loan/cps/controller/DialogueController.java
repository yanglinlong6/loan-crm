package com.loan.cps.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.R;
import com.loan.cps.service.DialogueFacade;
import com.loan.cps.service.NodeService;

@RestController
public class DialogueController {
	
	@Autowired
	private DialogueFacade dialogueFacade;
	
	@Autowired
	private NodeService nodeService;

	@RequestMapping(value = "/node/exc")
	public R exc(@RequestBody JSONObject msg) {
		dialogueFacade.exc(msg);
		return R.ok();
	}
	
	@RequestMapping(value = "/node/start")
	public R start(@RequestBody JSONObject msg) {
		dialogueFacade.start(msg);
		return R.ok();
	}
	
	@RequestMapping(value = "/node/end")
	public R end(@RequestBody JSONObject msg) {
		dialogueFacade.end(msg);
		return R.ok();
	}
	
	@RequestMapping(value = "/node/proceed")
	public R proceed(@RequestBody JSONObject msg ,HttpServletRequest req) {
		dialogueFacade.proceed(msg.getString("FromUserName"), req.getServerName());
		return R.ok();
	}
	
	@RequestMapping(value = "/node/reload")
	public R proceed() {
		nodeService.resetNodeConfig();
		return R.ok();
	}
	
	@RequestMapping(value = "/node/proceed2")
	public R proceed2(@RequestParam String userId ,HttpServletRequest req) {
		dialogueFacade.proceed2(userId);
		return R.ok();
	}
	
}
