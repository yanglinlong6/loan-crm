package com.crm.config;

import com.crm.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ControllerHandler implements ResponseBodyAdvice {

    private static final Logger log = LoggerFactory.getLogger(ControllerHandler.class);
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if(log.isDebugEnabled()){
            log.debug("请求地址：{}，返回内容：{}",serverHttpRequest.getURI().getPath(), JSONUtil.toJSONString(body));
        }
        return body;
    }
}
