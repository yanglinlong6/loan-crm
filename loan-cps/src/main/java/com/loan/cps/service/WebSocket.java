package com.loan.cps.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.HttpUtil;

@Component
@ServerEndpoint("/websocket/{user}")
public class WebSocket {
	
	/**
     *  与某个客户端的连接对话，需要通过它来给客户端发送消息
     */
    private Session session;
 
     /**
     * 标识当前连接客户端的用户名
     */
    private String name;
 
    /**
     *  用于存所有的连接服务的客户端，这个对象存储是安全的
     */
    private static ConcurrentHashMap<String,WebSocket> webSocketSet = new ConcurrentHashMap<>();
    
    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "user") String name){
    	session.setMaxIdleTimeout(getMaxTimeOut());
        this.session = session;
        this.name = name;
        // name是用来表示唯一客户端，如果需要指定发送，需要指定发送通过name来区分
        webSocketSet.put(name,this);
        try {
        	HttpUtil.getForObject("http://crm.daofen100.com/crm/join?user="+name);
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
    }
 
 
    @OnClose
    public void OnClose(){
        webSocketSet.remove(this.name);
        try {
        	HttpUtil.getForObject("http://crm.daofen100.com/crm/leave?user="+name);
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
        }
    }
 
    @OnMessage
    public void OnMessage(String message){
		try {
			JSONObject parseObject = JSON.parseObject(message);
			if(parseObject.getInteger("type")!=null&&parseObject.getInteger("type")!=3) {
				HttpUtil.postForObject("http://crm.daofen100.com/crm/push", message);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
    private long getMaxTimeOut() {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date parse = sdf.parse(sdf.format(new Date())) ;
			Calendar ca = Calendar.getInstance();
			ca.setTime(parse);
			ca.add(Calendar.DATE, 1);
			Date time = ca.getTime();
			return time.getTime()-System.currentTimeMillis();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0l;
    }
 
    /**
     * 指定发送
     * @param name
     * @param message
     */
    public void AppointSending(String name,String message){
        try {
            webSocketSet.get(name).session.getBasicRemote().sendText(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static boolean sendInfo(JSONObject obj) throws IOException {
        if(StringUtils.isNotBlank(obj.getString("to"))&&webSocketSet.containsKey(obj.getString("to"))){
        	webSocketSet.get(obj.getString("to")).session.getBasicRemote().sendText(obj.toJSONString());
        	return true;
        }
        return false;
    }
    
}
