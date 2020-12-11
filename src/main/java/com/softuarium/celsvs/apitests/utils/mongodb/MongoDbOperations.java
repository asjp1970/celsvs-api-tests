package com.softuarium.celsvs.apitests.utils.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.hamcrest.CoreMatchers.containsString;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDbOperations {
    
    private final String dbName;
    MongoClient mongoClient;
    MongoDatabase db;
    
    public MongoDbOperations (final String mongoDbUri, final String dbName) {
        
        this.dbName = dbName;
        
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoDbUri))
                .codecRegistry(pojoCodecRegistry)
                .build();
        this.mongoClient = MongoClients.create(clientSettings);
        this.db = this.mongoClient.getDatabase(this.dbName);

    }
        
    public void cleanupDb (final String dbName, final List<String> collectionNames) {
        try {
            collectionNames.forEach(coll -> {
                                        if (this.db.listCollectionNames()
                                                .into(new ArrayList<String>())
                                                .contains(coll)) {
                                            this.db.getCollection(coll).drop();
                                        }
                                        });
        }
        catch(MongoCommandException e) {
            // It happens when the collection to drop does not already exist
        }
    }
  
}
