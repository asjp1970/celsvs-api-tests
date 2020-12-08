package com.softuarium.celsvs.apitests.utils.mongodb;

import java.util.Arrays;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.softuarium.celsvs.apitests.utils.dtos.Address;
import com.softuarium.celsvs.apitests.utils.dtos.BookAdditionalInfo;
import com.softuarium.celsvs.apitests.utils.dtos.ContactInfo;
import com.softuarium.celsvs.apitests.utils.dtos.Publisher;
import com.softuarium.celsvs.apitests.utils.dtos.Synopsis;
import com.softuarium.celsvs.apitests.utils.mongodb.entities.BookRecord;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class MongoDbOperations {
    
    private final String dbName;
    MongoClient mongoClient;
    MongoDatabase db;
    MongoCollection<BookRecord> brCollection;
    
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
        this.brCollection = this.db.getCollection("books", BookRecord.class);

    }
        
    public void cleanupDb (final String dbName, final List<String> collectionNames) {
        try {
            collectionNames.forEach(coll -> this.db.getCollection(coll).drop());
        }
        catch(MongoCommandException e) {
            // It happens when the collection to drop does not already exist
        }
    }
    
    public void insertBookRecord() {
        
        BookRecord br = new BookRecord(randomNumeric(13),
                                       randomAlphanumeric(8),
                                       randomAlphabetic(20),
                                       Arrays.asList(randomAlphabetic(15), randomAlphabetic(15)), // authors
                                       randomAlphabetic(50),
                                       3,
                                       true,
                                       randomAlphabetic(15),
                                       new BookAdditionalInfo(
                                               new Publisher(
                                                       randomAlphabetic(10),   // publisher's name
                                                       randomAlphabetic(10),   // publisher's group
                                                       new ContactInfo(
                                                               new Address(
                                                                       randomAlphabetic(20),   // street
                                                                       57,                     // num
                                                                       randomAlphabetic(20),   // additional info
                                                                       randomAlphabetic(20),   // city
                                                                       randomNumeric(5)),      // zip code
                                                               randomAlphabetic(20),
                                                               Arrays.asList(randomNumeric(12), randomNumeric(12))),
                                                       randomAlphabetic(20)),  // translator
                                                       randomAlphabetic(15),   // collection
                                                       new Synopsis(
                                                               478,
                                                               randomAlphabetic(20),   // genre
                                                               randomAlphabetic(20),   // subgenre
                                                               randomAlphabetic(400)), // synopsis
                                                       randomAlphabetic(20),   // original title
                                                       randomAlphabetic(15)    // translator
                                               ));
        
        this.brCollection.insertOne(br);
    }

}
