package com.daofen.crm;

import com.daofen.crm.config.redis.EnableRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@ComponentScan(basePackages={"com.daofen.crm"})
@EnableTransactionManagement
@EnableRedis
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
@EnableScheduling
public class CrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
    }

}
