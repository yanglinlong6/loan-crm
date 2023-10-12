package com.crm.controller.esign;

import com.alibaba.fastjson.JSONObject;
import com.crm.service.esign.ESignFlowProcessThread;
import com.crm.service.esign.ESignService;
import com.crm.service.esign.vo.EsignReceiveBO;
import com.crm.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * e签宝接口控制器
 */
@RestController("/esign")
public class ESignController {

    private static final Logger LOG = LoggerFactory.getLogger(ESignController.class);

    @Autowired
    ESignService eSignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 接收e签宝回调
     * @return JSONObject
     */
    @RequestMapping("/receive")
    @ResponseBody
    private JSONObject receive(@RequestBody()JSONObject data){
        LOG.info("e签宝回调:{}",data.toJSONString());
        System.out.println(data.toJSONString());
        JSONObject success = new JSONObject();
        success.put("code","200");
        success.put("msg","success");
        if(null == data || data.isEmpty()){
            return success;
        }
        EsignReceiveBO esignReceiveBO = JSONUtil.toJavaBean(data.toJSONString(), EsignReceiveBO.class);
        ESignFlowProcessThread thread = new ESignFlowProcessThread(eSignService,redisTemplate,esignReceiveBO);
        thread.start();
        return success;
    }

}
