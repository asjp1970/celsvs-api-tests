package com.softuarium.celsvs.apitests.utils.mongodb;

import java.util.List;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class MongoDbOperations {
    
    private final String dbUri;
    MongoClient mongoClient;
    
    public MongoDbOperations (final String mongoDbUri) {
        this.dbUri = mongoDbUri;
        this.mongoClient = MongoClients.create(new ConnectionString(dbUri));
    }
    
    public void cleanupDb (final String dbName, final List<String> collectionNames) {
        MongoDatabase db = this.mongoClient.getDatabase("biblioteca_test_db");
        collectionNames.forEach(coll -> db.getCollection(coll).drop());
    }

}
