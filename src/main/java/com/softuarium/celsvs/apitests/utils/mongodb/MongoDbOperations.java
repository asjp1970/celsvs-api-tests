package com.softuarium.celsvs.apitests.utils.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


public class MongoDbOperations {
    
    private final String dbUri;
    
    public MongoDbOperations (final String mongoDbUri) {
        this.dbUri = mongoDbUri;
        MongoClient mongoClient = MongoClients.create(new ConnectionString(dbUri));
    }
    
    public boolean insertBookRecord () {
        
        // Create a complete book record in the database
        
        return true;
    }

}
