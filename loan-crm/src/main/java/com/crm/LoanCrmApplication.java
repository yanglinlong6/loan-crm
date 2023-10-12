package com.crm;

import com.crm.config.redis.EnableRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;
import java.net.UnknownHostException;

@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableRedis
public class LoanCrmApplication {

	public static void main(String[] args){
		SpringApplication.run(LoanCrmApplication.class, args);
	}

}
