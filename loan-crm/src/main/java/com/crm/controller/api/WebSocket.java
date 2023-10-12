package com.crm.controller.api;

import com.crm.common.CrmConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
@Slf4j
@ServerEndpoint(value = "/socket/{mobile}")
@Component
public class WebSocket {

    private Session session;

    private String mobile;

    public static ConcurrentHashMap<String,WebSocket> socketMap = new ConcurrentHashMap<>();

    private static RedisTemplate redisTemplate;

    @Autowired
    public void setMyServiceImpl(RedisTemplate redisTemplate) {
        WebSocket.redisTemplate = redisTemplate;
    }

    @OnOpen
    public void onOpen(Session session,@PathParam("mobile")String mobile){
        redisTemplate.opsForValue().increment(CrmConstant.Config.Socket.WEB_SOCKET_COUNTS);
        log.info("WebSocket当前连接人：{}，连接数：{}",mobile,redisTemplate.opsForValue().get(CrmConstant.Config.Socket.WEB_SOCKET_COUNTS));
        this.mobile = mobile;
        this.session = session;
        socketMap.put(mobile,this);
    }


    @OnClose
    public void onClose(Session session,@PathParam("mobile")String mobile){
        redisTemplate.opsForValue().decrement(CrmConstant.Config.Socket.WEB_SOCKET_COUNTS);
        log.info("WebSocket关闭：{}，当前连接数：{}",mobile,redisTemplate.opsForValue().get(CrmConstant.Config.Socket.WEB_SOCKET_COUNTS));
        socketMap.remove(mobile);
    }


    @OnMessage
    public void onMessage(String message, Session session,@PathParam("mobile")String mobile) {
        log.info("服务端收到客户端[{}]的消息:{}", mobile,message);
        sendMessage(message,socketMap.get(mobile));
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    public void sendMessage(String message, WebSocket webSocket) {
        try {
            log.info("服务端给客户端[{}]发送消息：{}", webSocket.getMobile(), message);
            webSocket.getSession().getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败：{}-{}-{}", webSocket.getMobile(),message,e);
        }
    }
}
