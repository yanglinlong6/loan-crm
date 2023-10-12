package com.crm.schedule;

import com.crm.common.CrmConstant;
import com.crm.controller.api.WebSocket;
import com.crm.service.employee.EmployeeMsgService;
import com.crm.service.employee.model.EmployeeMsgPO;
import com.crm.util.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EmployeeMsgSchedule {

    private static final String KEY = "Employee_Msg_Schedule_Key";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EmployeeMsgService employeeMsgService;

//    @Scheduled(cron = "0/10 * * * * ?")
    public void send(){
        try{
            log.info("发送消息定时任务执行");
            if(stringRedisTemplate.hasKey(KEY)){
                log.info("【员工新消息正在发送中，直接返回】");
                return;
            }
            ConcurrentHashMap<String,WebSocket> map =  WebSocket.socketMap;
            if(map.isEmpty()){
                log.info("发送消息定时任务结束：没有客户端连接");
                return;
            }
            send1(map);
        }catch (Exception e){
            log.error("发送消息定时任务异常-send：{}",e.getMessage(),e);
        }
    }

    private void send1(ConcurrentHashMap<String,WebSocket> map){
        try{
            stringRedisTemplate.opsForValue().set(KEY,"1",1, TimeUnit.MINUTES);
            Set<String> keys = map.keySet();
            for(String key : keys){
                List<EmployeeMsgPO> list = employeeMsgService.getMsg(null,key);
                send2(map.get(key),list);
            }
        }catch (Exception e){
            log.error("发送消息定时任务异常-send1：{}",e.getMessage(),e);
        }finally {
            stringRedisTemplate.delete(KEY);
        }

    }

    private void send2(WebSocket webSocket,List<EmployeeMsgPO> msgList){
        if(null == webSocket || ListUtil.isEmpty(msgList)){
            log.info("发送消息定时任务结束-{}：没有消息",webSocket.getMobile());
            return;
        }
        for(EmployeeMsgPO msg : msgList){
            webSocket.sendMessage(msg.getMsg(),webSocket);
            msg.setStatus(CrmConstant.Employee.Msg.Status.SEND);
            employeeMsgService.updateMsg(msg);
        }
    }

}
