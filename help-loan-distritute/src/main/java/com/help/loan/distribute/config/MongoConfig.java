package com.help.loan.distribute.config;

import com.help.loan.distribute.common.utils.MongoDbUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

//@Configuration
//@Import(value = {MongoProperties.class})
public class MongoConfig {

//    @Autowired
//    private MongoProperties mongoProperties;

//    @Value("${spring.data.mongodb.uri}")
//    private String mongodbURL;
//
//    @Value("${mongo.dbname}")
//    private String dbname;

//    @Bean
//    public MongoClient mongoClient() {
//        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoProperties.getUrl()));
//        MongoDbUtils.setMongoDatabase(mongoClient.getDatabase(mongoProperties.getDbName()));
//        return mongoClient;
//    }


}
