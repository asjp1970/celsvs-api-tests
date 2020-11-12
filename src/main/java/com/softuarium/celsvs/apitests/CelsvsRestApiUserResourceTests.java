package com.softuarium.celsvs.apitests;

import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.TestParent;
import com.softuarium.celsvs.apitests.utils.entities.Address;
import com.softuarium.celsvs.apitests.utils.entities.ContactInfo;
import com.softuarium.celsvs.apitests.utils.entities.UserRecordPojo;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import org.testng.annotations.BeforeMethod;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Slf4j
@Test(groups = { "functional", "api" })
public class CelsvsRestApiUserResourceTests extends TestParent {
        
    
    @BeforeClass
    public void beforeClass() {
    }
    
    @BeforeMethod
    public void beforeMethod() {
        // TODO: check that the nr of resources before 
    }
    
    @AfterMethod
    public void afterMethod() {
        // TODO: check that the nr of resources are the same as before, or 0, we'll see
    }
    
    @BeforeTest
    public void beforeTest() {
    }
    
    @AfterTest
    public void afterTest() {
    }
    
    @BeforeSuite
    public void beforeSuite() {
       
    }
    
    @AfterSuite
    public void afterSuite() {
    }
    
    
    // GET
    
    @Test(description="Given an existing user record, when retrieved, then 200 OK and correct json is received")
    public void test_restApiUsersGet_01() {
        final String userId = randomNumeric(10);
        final UserRecordPojo ur = instantiateUserRecord(userId, randomAlphanumeric(10));
        
        testGetExistingEntity(this.celsvsBaseUri+"/users"+"/"+userId, ur);
    }
    
    @Test(description="Given a non-existing book record, when retrieved, then 404 not found is received")
    public void test_restApiUsersGet_02() {
        String isbnRandom = randomNumeric(13);
    
        this.testGetNonExistingEntity(this.celsvsBaseUri+"/books"+"/"+isbnRandom);
    }
    
    @Test(description="Given an existing book record, when retrieved using signature, then 200 OK and correct json is received")
    public void test_restApiUsersGet_03() {
        final String userId = randomNumeric(10);
        final String secondaryUid = randomAlphanumeric(10);
        final UserRecordPojo ur = instantiateUserRecord(userId, secondaryUid);
        
        this.testGetExistingEntity2ndKey(this.celsvsBaseUri+"/users", userId, secondaryUid, ur);
    }
    
    @Test(description="Given several existing book records, when all retrieved, then 200 OK")
    public void test_restApiUsersGet_04() {
    
        this.testGetAllResources(this.celsvsBaseUri+"/books");
    }
    
    private UserRecordPojo instantiateUserRecord(final String uid, final String secondId) {
        
        return new UserRecordPojo(uid, secondId,
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
