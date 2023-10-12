package com.loan.wechat.docking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loan.wechat.common.Result;
import com.loan.wechat.common.SessionLock;

@RestController
public class SyncLockContr {

	@RequestMapping(value = "/sync/lock")
	public Result lock(@RequestParam String userId) {
		SessionLock.lock(userId);
		return Result.success();
	}
	
	@RequestMapping(value = "/sync/unlock")
	public Result unlock(@RequestParam String userId) {
		SessionLock.unlock(userId);
		return Result.success();
	}
	
}
