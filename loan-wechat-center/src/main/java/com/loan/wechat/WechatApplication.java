package com.loan.wechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class,FreeMarkerAutoConfiguration.class})
@PropertySource(value= {"file:${properties.path}/env.properties"},encoding="UTF-8")
@EnableAsync
@EnableScheduling
public class WechatApplication{
	public static void main(String[] args) {
        SpringApplication.run(WechatApplication.class,args);
	}
}
