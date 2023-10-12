package com.daofen.crm.controller.socket;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.base.CrmException;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.config.AppContextUtil;
import com.daofen.crm.service.counselor.dao.CounselorMapper;
import com.daofen.crm.service.counselor.model.CompanyCounselorPO;
import com.daofen.crm.service.customer.dao.CustomerMapper;
import com.daofen.crm.service.customer.dao.MsgLogMapper;
import com.daofen.crm.service.customer.model.CustomerPO;
import com.daofen.crm.service.customer.model.MsgLogPO;
import com.daofen.crm.service.customer.model.SocketMsg;
import com.daofen.crm.utils.HttpUtil;
import com.daofen.crm.utils.JSONUtil;

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
    
    private static final String SOCKET_PREFIX = "SOCKET_";
    
    private static final Logger LOG = LoggerFactory.getLogger(WebSocket.class);
    
    @Autowired
	private CustomerMapper customerMapper;
    
    @Autowired
    private CounselorMapper counselorMapper;
    
    @Autowired
    private MsgLogMapper msgLogMapper;
    
    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "user") String name){
    	session.setMaxIdleTimeout(getMaxTimeOut());
        this.session = session;
        this.name = name;
        // name是用来表示唯一客户端，如果需要指定发送，需要指定发送通过name来区分
        String value = AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().get(name);
        if(StringUtils.isBlank(value) || !JSONUtil.isJsonString(value)){
            throw new CrmException(ResultVO.ResultCode.LOGIN,"登录已失效,请重新登陆！",null);
        }
        webSocketSet.put(name,this);
    }
 
 
    @OnClose
    public void OnClose(){
        webSocketSet.remove(this.name);
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
    
    @OnMessage
    public void OnMessage(String message){
		try {
			JSONObject parseObject = JSON.parseObject(message);
			if(parseObject.getInteger("type")!=null&&parseObject.getInteger("type")!=3) {
				HttpUtil.postForObject("http://crm.daofen100.com/dialogue/push/", message);
				insertLog(parseObject,1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOG.info(e.toString());
		}
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
    
    public  boolean sendInfo(JSONObject obj) throws IOException {
        if(StringUtils.isNotBlank(obj.getString("to"))&&webSocketSet.containsKey(obj.getString("to"))){
        	 try {
        		webSocketSet.get(obj.getString("to")).session.getBasicRemote().sendText(obj.toJSONString());
        		insertLog(obj,0);
        	 }catch (Exception e){
        		 e.printStackTrace();
        	 }
        	return true;
        }
        return false;
    }
    
    private String getMobile(String token) {
    	String[] split = token.split("_");
    	return split[3];
    }
    
    private  void insertLog(JSONObject obj,int type) {
    	MsgLogPO javaObject = obj.toJavaObject(MsgLogPO.class);
    	if(type == 0) {
    		javaObject.setTo(getMobile(obj.getString("to")));
    	}else {
    		
    		javaObject.setFrom(getMobile(obj.getString("from")));
    	}
    	javaObject.setDataType(type);
    	javaObject.setCreateBy("system");
    	javaObject.setCreateDate(new Date());
    	AppContextUtil.getBean(MsgLogMapper.class).addMsgLog(javaObject);
    }
    
    public boolean join(String name){
        try {
        	if(StringUtils.isNotBlank(name)){
        		CustomerPO selSocketCust = customerMapper.selSocketCust(name);
        		if(selSocketCust!=null) {
        			if(selSocketCust.getBusinessId()==0) {
        				SocketMsg generate3 = SocketMsg.generate();
        				generate3.setType(SocketMsg.OFFLINE);
        				generate3.setTime(System.currentTimeMillis());
            			generate3.setTo(name);
            			HttpUtil.postForObject("http://crm.daofen100.com/dialogue/push", JSON.toJSONString(generate3));
            			LOG.info("连接失败：顾问不在线 name = " + name);
        				return false;
        			}
        			CompanyCounselorPO selectCounselorById = counselorMapper.selectCounselorById(selSocketCust.getBusinessId());
        			String byMobile = getByMobile(selectCounselorById.getMobile());
        			if(StringUtils.isBlank(byMobile)) {
        				SocketMsg generate3 = SocketMsg.generate();
        				generate3.setType(SocketMsg.OFFLINE);
        				generate3.setTime(System.currentTimeMillis());
            			generate3.setTo(name);
            			HttpUtil.postForObject("http://crm.daofen100.com/dialogue/push", JSON.toJSONString(generate3));
            			LOG.info("连接失败：顾问不在线 name = " + name);
        				return false;
        			}
        			SocketMsg generate = SocketMsg.generate();
        			generate.setType(SocketMsg.ONLINE);
        			generate.setTime(System.currentTimeMillis());
        			generate.setParams(selSocketCust);
        			generate.setFrom(name);
        			generate.setTo(byMobile);
        			webSocketSet.get(byMobile).session.getBasicRemote().sendText(JSON.toJSONString(generate));
        			SocketMsg generate4 = SocketMsg.generate();
        			generate4.setType(SocketMsg.TEXT);
        			generate4.setTime(System.currentTimeMillis());
        			generate4.setFrom(name);
        			generate4.setContent("客户接入");
        			generate4.setTo(byMobile);
        			webSocketSet.get(byMobile).session.getBasicRemote().sendText(JSON.toJSONString(generate));
        			insertLog(JSON.parseObject(JSON.toJSONString(generate)),0);
        			SocketMsg generate2 = SocketMsg.generate();
        			generate2.setType(SocketMsg.ONLINE);
        			generate2.setTime(System.currentTimeMillis());
        			JSONObject jobj = new JSONObject();
        			jobj.put("name", selSocketCust.getCounselorName());
        			generate2.setParams(jobj);
        			generate2.setFrom(byMobile);
        			generate2.setTo(name);
        			HttpUtil.postForObject("http://crm.daofen100.com/dialogue/push", JSON.toJSONString(generate2));
        			SocketMsg generate3 = SocketMsg.generate();
        			generate3.setType(SocketMsg.TEXT);
        			generate3.setTime(System.currentTimeMillis());
        			generate3.setFrom(byMobile);
        			generate3.setContent("您好，专业贷款顾问："+selectCounselorById.getName()+"，真诚为您服务，有什么问题都可以咨询我哦！");
        			generate3.setTo(name);
        			HttpUtil.postForObject("http://crm.daofen100.com/dialogue/push", JSON.toJSONString(generate3));
        			AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().set(SOCKET_PREFIX+name,byMobile,2*60*60,TimeUnit.SECONDS);
        			LOG.info("连接成功： name = " + name + " coun = " +byMobile);
        			return true;
        		}else {
        			LOG.info("连接失败： 客户不存在   cust = " +JSON.toJSONString(selSocketCust));
        	        return false;
        		}
            }
        }catch (Exception e){
        	 LOG.info(e.toString());
        }
        LOG.info("连接失败： name为空   name = " +name);
        return false;
    }
    
    private String getByMobile(String mobile) {
    	Iterator<String> iterator = webSocketSet.keySet().iterator();
    	while(iterator.hasNext()) {
    		String next = iterator.next();
    		if(next.contains(mobile)) {
    			return next;
    		}
    	}
    	return null;
    }
    
    public boolean leave(String name){
        try {
        	if(StringUtils.isNotBlank(name)){
        		String string = AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().get(SOCKET_PREFIX+name);
        		if(StringUtils.isBlank(string)) {
    				return false;
    			}
        		SocketMsg generate = SocketMsg.generate();
    			generate.setType(SocketMsg.OFFLINE);
    			generate.setTime(System.currentTimeMillis());
    			generate.setFrom(name);
    			generate.setTo(string);
            	webSocketSet.get(string).session.getBasicRemote().sendText(JSON.toJSONString(generate));
            	insertLog(JSON.parseObject(JSON.toJSONString(generate)),0);
            	return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
}
