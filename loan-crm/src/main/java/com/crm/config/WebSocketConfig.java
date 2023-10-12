package com.crm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket
 */
@Configuration  
public class WebSocketConfig {  
	
	private static Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
	
	@Bean
    public ServerEndpointExporter serverEndpointExporter() {
        log.info("ServerEndpointExporter start");
        return new ServerEndpointExporter();
    }
	
}