package com.softuarium.celsvs.apitests;

import static com.google.gson.JsonParser.parseString;
import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.TestParent;
import com.softuarium.celsvs.apitests.utils.mongodb.Address;
import com.softuarium.celsvs.apitests.utils.mongodb.BookAdditionalInfo;
import com.softuarium.celsvs.apitests.utils.mongodb.BookRecordPojo;
import com.softuarium.celsvs.apitests.utils.mongodb.ContactInfo;
import com.softuarium.celsvs.apitests.utils.mongodb.Library;
import com.softuarium.celsvs.apitests.utils.mongodb.Publisher;
import com.softuarium.celsvs.apitests.utils.mongodb.Synopsis;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

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
import java.util.Arrays;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;


public class CelsvsRestApiBookResourceTests extends TestParent {

    private String mongoDbUri;
    
    private String celsvsBaseUri;
    
    @Parameters({ "mongodbUri", "celsvsBaseUri" })
    @BeforeClass
    public void beforeClass(final String mongoDbUri, final String celsvsUri) {
        this.mongoDbUri = mongoDbUri;
        this.celsvsBaseUri = celsvsUri;
    }
    
    @BeforeMethod
    public void beforeMethod() {
    }
    
    @AfterMethod
    public void afterMethod() {
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
    
    @Test(description="Given an existing resource, when retrieved, then 200 OK and correct json is received")
    public void test_restApiBooksGet_01() {
    
        Response resp = RestAssured.given().accept(ContentType.JSON).get(celsvsBaseUri+"/books/9788446027744");
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        
        assertThat(parseString(resp.getBody().asString()),
                equalTo(parseString(getJsonFromFile("jsonfiles/expectedBookRecord.json"))));
    }
    
    @Test(description="Given a non-existing resource, when retrieved, then 404 not found is received")
    public void test_restApiBooksGet_02() {
        String isbnRandom = randomNumeric(13);
    
        Response resp = RestAssured.given().accept(ContentType.JSON).get(celsvsBaseUri+"/books"+"/"+isbnRandom);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.CLIENT_ERR_NOT_FOUND));
    }
    
    @Test(description="Given an existing resource, when retrieved using signature, then 200 OK and correct json is received")
    public void test_restApiBooksGet_03() {
    
        Response resp = RestAssured.given().accept(ContentType.JSON).get(celsvsBaseUri+"/books/AD-11-CDD2");
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        
        assertThat(parseString(resp.getBody().asString()),
                equalTo(parseString(getJsonFromFile("jsonfiles/expectedBookRecord.json"))));
    }
    
    @Test(description="Given several existing resources, when all retrieved, then 200 OK")
    public void test_restApiBooksGet_04() {
    
        Response resp = RestAssured.given().accept(ContentType.JSON).get(celsvsBaseUri+"/books");
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        
        //assertThat(parseString(resp.getBody().asString()),
        //equalTo(parseString(getJsonFromFile("jsonfiles/expectedBookRecord.json"))));
    }
    
    @Test(description="Given several existing resources, when pages retrieved, then 200 OK and pages are OK")
    public void test_restApiBooksGet_05() {
    
        fail("Not yet implemented");

        //assertThat(parseString(resp.getBody().asString()),
        //equalTo(parseString(getJsonFromFile("jsonfiles/expectedBookRecord.json"))));
    }
    
    
    // POST
    
    @Test(description="Given a non-existing resource, when created, then 201 Created is received")
    public void test_restApiBooksPost_01() {
        final String isbn = randomNumeric(13);
        final BookRecordPojo br = instantiateBookRecord(isbn, randomAlphanumeric(10));
        final String uriResource = this.celsvsBaseUri+"/books"+"/"+isbn;
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br)
            .post(uriResource);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept(ContentType.JSON).get(uriResource);
        assertThat(resp.getBody().as(BookRecordPojo.class), equalTo(br));
    }
    
    @Test(description="Given an existing resource, when a creation operation has the same ID, then 409 Conflict is received")
    public void test_restApiBooksPost_02() {
        final String isbn = randomNumeric(13);
        final BookRecordPojo br = instantiateBookRecord(isbn, randomAlphanumeric(10));
        final String uriResource = this.celsvsBaseUri+"/books"+"/"+isbn;
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br)
            .post(uriResource);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Send the 2nd POST over the same resource
        resp = RestAssured
                .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br)
                .post(uriResource);
        
        // Check 409 - Conflict
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.CLIENT_ERR_CONFLICT));
        
    }
    
    @Test(description="Given an existing resource, when a creation operation has the same 2ndary ID, then 409 Conflict is received")
    public void test_restApiBooksPost_03() {
        fail("Not yet implemented");
    }
    
    
    //PUT
    
    @Test(description="Given a non-existing resource, when updated, then 201 Created is received")
    public void test_restApiBooksPut_01() {
        fail("Not yet implemented");
    }
     
    @Test(description="Given an existing resource, when a updated, then 200 OK is received")
    public void test_restApiBooksPut_02() {
        fail("Not yet implemented");
    }
     
    @Test(description="Given an existing resource, when update operationhas the 2ndary ID of another existing resource, then 409 Conflict is received")
    public void test_restApiBooksPut_03() {
        fail("Not yet implemented");
    }
    
     
    // DELETE
    
    @Test(description="Given an existing resource, when a delete operation with primary ID, then 204 No Content is received with no Body")
    public void test_restApiBooksDelete_01() {
        final String isbn = randomNumeric(13);
        final BookRecordPojo br = instantiateBookRecord(isbn, randomAlphanumeric(10));
        final String uriResource = this.celsvsBaseUri+"/books"+"/"+isbn;
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br)
            .post(uriResource);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Delete the resource
        resp = RestAssured
                .given().accept(ContentType.JSON).contentType(ContentType.JSON)
                .delete(uriResource);
        
        // Check 204 - No content
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT));
        
    }
    
    @Test(description="Given an existing resource, when a delete operation with 2ndary ID, then 204 No Content is received with no Body")
    public void test_restApiBooksDelete_02() {
        final String isbn = randomNumeric(13);
        final String signature = randomAlphanumeric(10);
        final BookRecordPojo br = instantiateBookRecord(isbn, signature);
        final String uriResource = this.celsvsBaseUri+"/books"+"/"+isbn;
        final String uriResourceDel = this.celsvsBaseUri+"/books"+"/"+signature;
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br)
            .post(uriResource);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Delete the resource
        resp = RestAssured
                .given().accept(ContentType.JSON).contentType(ContentType.JSON)
                .delete(uriResourceDel);
        
        // Check 204 - No content
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT));
    }
    
    @Test(description="Given a non-existing resource, when a delete operation with primary ID, then 204 No Content is received with no Body")
    public void test_restApiBooksDelete_03() {
        final String uriResource = this.celsvsBaseUri+"/books"+"/"+randomNumeric(13);
        
        // Delete the resource
        Response resp = RestAssured
                .given().accept(ContentType.JSON).contentType(ContentType.JSON)
                .delete(uriResource);
        
        // Check 204 - No content (it's idempotent: deleting or not existing doesn't change the resource set)
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT));
    }
    
    
    // HATEOAS
    
    @Test(description="Given an existing resource, when retrieved, then the expected hypermedia links are received and lead to the rigth resources")
    public void test_hypermedia_01() {
        fail("Not yet implemented");
    }
    
    @Test(description="Given a non-existing resource, when created, then the expected hypermedia links are received and lead to the rigth resources")
    public void test_hypermedia_02() {
        fail("Not yet implemented");
    }
    
    
    // Helper methods
    
    private String getJsonFromFile(final String jsonFilePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(jsonFilePath).getFile());
        try {
            return FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            fail(String.format("Could not read file from jar (%s)", e.getMessage()));
            return null;
        }
    }
    
    private BookRecordPojo instantiateBookRecord(final String isbn, final String sign) {
        return new BookRecordPojo(
                isbn, sign,                     // isbn & signature
                randomAlphabetic(20),           // title
                Arrays.asList(randomAlphabetic(15), randomAlphabetic(15)), // authors
                randomAlphabetic(50),           // subtitle
                3,                              // num copies
                true,
                new Library(randomAlphabetic(15),       // library name
                        new Address(
                                randomAlphabetic(20),   // street
                                34,                     // num
                                randomAlphabetic(20),   // additional info
                                randomAlphabetic(20),   // city
                                randomNumeric(5))),
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
                        )
                );
    }
}
