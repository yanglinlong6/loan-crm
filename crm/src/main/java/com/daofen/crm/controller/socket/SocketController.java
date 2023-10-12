package com.daofen.crm.controller.socket;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;

@RestController
public class SocketController extends AbstractController{
	
	@Autowired
	private WebSocket webSocket;
	
	@RequestMapping("/leave")
    public ResultVO leave(@RequestParam String user) throws IOException {
		if(webSocket.leave(user)) {
			return this.success();
		}
        return this.failed();
    }
	
	@RequestMapping("/join")
    public ResultVO join(@RequestParam String user) throws IOException {
		if(webSocket.join(user)) {
			return this.success();
		}
        return this.failed();
    }

    @RequestMapping("/push")
    public ResultVO pushToWeb(@RequestBody JSONObject obj) throws IOException {
    	if(webSocket.sendInfo(obj)) {
    		return this.success();
    	}
        return this.failed();
    }
}
