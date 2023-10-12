package com.loan.cps.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.R;
import com.loan.cps.service.WebSocket;

@RestController
public class SocketController {

    @RequestMapping("/push")
    public R pushToWeb(@RequestBody JSONObject obj) throws IOException {
    	if(WebSocket.sendInfo(obj)) {
    		return R.ok();
    	}
        return R.fail("1", "连接已断开");
    }
}
