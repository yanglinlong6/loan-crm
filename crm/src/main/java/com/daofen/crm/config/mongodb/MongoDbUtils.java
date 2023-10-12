package com.daofen.crm.config.mongodb;



public class MongoDbUtils {

//    private static MongoDatabase mongoDatabase = null;
//
//    public static MongoDatabase getMongoDatabase() {
//        if(null != mongoDatabase) {
//            return mongoDatabase;
//        }
//        mongoDatabase = new MongoClient("", 27017).getDatabase("");
//        return mongoDatabase;
//    }
//
//    public static void setMongoDatabase(MongoDatabase mongoDatabase) {
//        MongoDbUtils.mongoDatabase = mongoDatabase;
//    }
//
//    public static void insert(Document doc, String collectionName) {
//        mongoDatabase.getCollection(collectionName).insertOne(doc);
//    }
//
//    public static void update(String colName, Object val, Document doc, String collectionName) {
//        Document where = new Document();
//        where.put(colName, val);
//        mongoDatabase.getCollection(collectionName).updateOne(where, doc);
//    }
//
//    public static void del(String colName, Object val, String collectionName) {
//        Document where = new Document();
//        where.put(colName, val);
//        mongoDatabase.getCollection(collectionName).deleteOne(where);
//    }
//
//
//    public static Document findOne(String colName, Object val, String collectionName) {
//        Document where = new Document();
//        where.put(colName, val);
//        FindIterable<Document> findIt = mongoDatabase.getCollection(collectionName).find(where);
//        if(findIt == null) {
//            return null;
//        }
//
//        return findIt.first();
//    }
//
//    public static List<Document> findAll(String colName, String whereflag, Object val, String collectionName) {
//        Document where = new Document();
//        where.put(colName, new BasicDBObject(whereflag, val));
//        FindIterable<Document> findIt = mongoDatabase.getCollection(collectionName).find(where);
//        if(findIt == null) {
//            return null;
//        }
//        List<Document> cc = new ArrayList<Document>();
//        MongoCursor<Document> iterator = findIt.iterator();
//        if(iterator.hasNext()) {
//            cc.add(iterator.next());
//        }
//        return cc;
//    }
//
//    public static List<Document> find(BasicDBObject query, String collectionName) {
//        FindIterable<Document> findIt = mongoDatabase.getCollection(collectionName).find(query);
//        if(findIt == null) {
//            return null;
//        }
//        List<Document> cc = new ArrayList<Document>();
//        MongoCursor<Document> iterator = findIt.iterator();
//        while(iterator.hasNext()) {
//            cc.add(iterator.next());
//        }
//        return cc;
//    }

}
