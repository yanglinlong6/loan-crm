package com.daofen.crm.config.mongodb;


//@Configuration
//@ConfigurationProperties(prefix = "mongodb.config")
public class MongoProperties {

    private String url;

    private String dbName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
