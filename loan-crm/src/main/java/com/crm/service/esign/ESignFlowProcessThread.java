package com.crm.service.esign;

import com.crm.service.email.EmailBO;
import com.crm.service.email.EmailService;
import com.crm.service.esign.model.FlowStatus;
import com.crm.service.esign.vo.EsignReceiveBO;
import com.crm.util.JudgeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

public class ESignFlowProcessThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(ESignFlowProcessThread.class);

    ESignService eSignService;

    StringRedisTemplate redisTemplate;

    EsignReceiveBO esignReceiveBO;

    public ESignFlowProcessThread(ESignService eSignService, StringRedisTemplate redisTemplate, EsignReceiveBO esignReceiveBO){
        this.eSignService = eSignService;
        this.redisTemplate = redisTemplate;
        this.esignReceiveBO = esignReceiveBO;
    }
    @Override
    public void run() {
        LOG.info("启动E签宝更新流程:{}",this.esignReceiveBO);
        if(null == this.eSignService || null == this.redisTemplate || null == this.esignReceiveBO ){
            LOG.info("启动E签宝更新流程:{}-{}-{}[参数为空]",this.eSignService,this.redisTemplate,this.esignReceiveBO);
            return;
        }
        String action = this.esignReceiveBO.getAction();
        if(!JudgeUtil.in(action,FlowStatus.SIGN_FLOW_FINISH.getAction(),FlowStatus.SIGN_FLOW_UPDATE.getAction())){
            LOG.info("启动E签宝流程:{}-{}-{}[没有完成]",this.esignReceiveBO.getFlowId(),action,this.esignReceiveBO.getResultDescription());
            return;
        }
        try{
            eSignService.updateFlow(this.esignReceiveBO);
        }catch (Exception e){
            String msg = "";
            LOG.info("启动E签宝更新流程:{}[执行异常]",this.esignReceiveBO);
            try{
                EmailService.sendMessage(new EmailBO("启动E签宝更新流程异常",this.esignReceiveBO.toString(),"509124739@qq.com"));
            }catch (Exception e1){
                LOG.error("发送邮件失败:{}",e1.getMessage(),e);
            }

        }

    }
}
