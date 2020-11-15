package com.softuarium.celsvs.apitests;

import com.softuarium.celsvs.apitests.utils.dtos.Address;
import com.softuarium.celsvs.apitests.utils.dtos.ContactInfo;
import com.softuarium.celsvs.apitests.utils.dtos.UserDto;
import com.softuarium.celsvs.apitests.utils.mongodb.MongoDbOperations;

import org.testng.annotations.Test;



import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterClass;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Test(groups = { "functional", "api" })
public class RestApiUserResourceTests extends RestApiBaseTester {
    
    private String usersUri;
    
    @Parameters({ "celsvsBaseUri", "usersUri" })
    @BeforeClass
    public void beforeClass(final String celsvsUri, final String usersUriFragment) {
        
        this.usersUri = celsvsUri+"/"+usersUriFragment;
        
    }
    
    @Parameters({ "usersCollectionName" })
    @AfterClass
    public void afterClass(final String usersCollection) {
        
        List<String> collectionNames = Arrays.asList(usersCollection);
        super.cleanupDbCollection(collectionNames);
        
    }
    
    
    // GET
    
    @Test(description="Given an existing user record, when retrieved, then 200 OK and correct json is received")
    public void test_restApiUsersGet_01() {
        final String userId = randomNumeric(10);
        final UserDto ur = instantiateUserRecord(userId, randomAlphanumeric(10));
        
        testGetExistingEntity(usersUri+"/"+userId, ur);
    }
    
    @Test(description="Given a non-existing user record, when retrieved, then 404 not found is received")
    public void test_restApiUsersGet_02() {
        String isbnRandom = randomNumeric(13);
    
        this.testGetNonExistingEntity(this.usersUri+"/"+isbnRandom);
    }
    
    @Test(description="Given an existing user record, when retrieved using public Id, then 200 OK and correct json is received")
    public void test_restApiUsersGet_03() {
        final String userId = randomNumeric(10);
        final String secondaryUid = randomAlphanumeric(10);
        final UserDto ur = instantiateUserRecord(userId, secondaryUid);
        
        this.testGetExistingEntity2ndKey(usersUri, userId, secondaryUid, ur);
    }
    
    @Test(description="Given several existing user records, when all retrieved, then 200 OK")
    public void test_restApiUsersGet_04() {
    
        this.testGetAllResources(usersUri);
    }
    
    @Test(description="Given a non-existing user record, when created, then 201 Created is received")
    public void test_restApiUserPost_01() {
        
        final String uid = randomAlphanumeric(10);
        final UserDto r = instantiateUserRecord(uid, randomAlphanumeric(10));
        
        this.testPostNewResourceOk(usersUri+"/"+uid, r, UserDto.class);
    }
    
    @Test(description="Given an existing user record, when a creation operation has the same uid, then 409 Conflict is received")
    public void test_restApiUserPost_02() {
        
        final String uid = randomNumeric(10);
        final UserDto r = instantiateUserRecord(uid, randomAlphanumeric(10));
        
        this.testPostExistingResourceNok(usersUri+"/"+uid, r);
    }
    
    private UserDto instantiateUserRecord(final String uid, final String secondId) {
        
        return new UserDto(uid, secondId,
                randomAlphabetic(8),        // first name
                randomAlphabetic(6),        // last name
                new ContactInfo(
                        new Address(
                                randomAlphabetic(20),   // street
                                57,                     // num
                                randomAlphabetic(20),   // additional info
                                randomAlphabetic(20),   // city
                                randomNumeric(5)),      // zip code
                        randomAlphabetic(20),
                        Arrays.asList(randomNumeric(12), randomNumeric(12))),
                randomAlphanumeric(10));
    }
    
}
