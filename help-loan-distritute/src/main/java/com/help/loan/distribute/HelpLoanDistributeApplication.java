package com.help.loan.distribute;

import com.help.loan.distribute.common.annotation.EnableRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableScheduling
@EnableRedis
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, FreeMarkerAutoConfiguration.class})
public class HelpLoanDistributeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelpLoanDistributeApplication.class, args);
    }

}
