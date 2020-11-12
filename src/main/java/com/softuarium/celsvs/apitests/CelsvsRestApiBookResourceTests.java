package com.softuarium.celsvs.apitests;

import static com.google.gson.JsonParser.parseString;
import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.TestParent;
import com.softuarium.celsvs.apitests.utils.mongodb.Address;
import com.softuarium.celsvs.apitests.utils.mongodb.BookAdditionalInfo;
import com.softuarium.celsvs.apitests.utils.mongodb.BookRecordPojo;
import com.softuarium.celsvs.apitests.utils.mongodb.ContactInfo;
import com.softuarium.celsvs.apitests.utils.mongodb.Publisher;
import com.softuarium.celsvs.apitests.utils.mongodb.Synopsis;

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
public class CelsvsRestApiBookResourceTests extends TestParent {
        
    private String celsvsBaseUri;
    
    @Parameters({ "celsvsBaseUri" })
    @BeforeClass
    public void beforeClass(final String celsvsUri) {
        this.celsvsBaseUri = celsvsUri;
        // RestAssured.baseURI = this.celsvsBaseUri;
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
    
    @Test(description="Given an existing book record, when retrieved, then 200 OK and correct json is received")
    public void test_restApiBooksGet_01() {
        final String isbn = randomNumeric(13);
        final BookRecordPojo br = instantiateBookRecord(isbn, randomAlphanumeric(10));
        final String uriResource = this.celsvsBaseUri+"/books"+"/"+isbn;
        
        testGetExistingEntity(uriResource, br);
    }
    
    @Test(description="Given a non-existing book record, when retrieved, then 404 not found is received")
    public void test_restApiBooksGet_02() {
        String isbnRandom = randomNumeric(13);
    
        this.testGetNonExistingEntity(this.celsvsBaseUri+"/books"+"/"+isbnRandom);
    }
    
    @Test(description="Given an existing book record, when retrieved using signature, then 200 OK and correct json is received")
    public void test_restApiBooksGet_03() {
        final String isbn = randomNumeric(13);
        final String sign = randomAlphanumeric(10);
        final BookRecordPojo br = instantiateBookRecord(isbn, sign);
        
        this.testGetExistingEntity2ndKey(this.celsvsBaseUri+"/books", isbn, sign, br);
    }
    
    @Test(description="Given several existing book records, when all retrieved, then 200 OK")
    public void test_restApiBooksGet_04() {
    
        this.testGetAllResources(this.celsvsBaseUri+"/books");
    }
    
    @Test(groups = {"unstable"},
          description="Given several existing book records, when pages retrieved, then 200 OK and pages are OK")
    public void test_restApiBooksGet_05() {
        
        final int totalRecords = 10;
        final int numPages = 5;
        final int sizePage = 2;
        final String uriResource = this.celsvsBaseUri.concat("/books/").concat(String.format("?page=%d&size=%d&sortBy=isbn&sortOrder=asc", numPages, sizePage));
    
        List<BookRecordPojo> list = createManyBookRecords(totalRecords);
        list.forEach(br -> {
            Response resp = RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br)
                                .post(this.celsvsBaseUri+"/books"+"/"+br.getIsbn());
            assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
            }
            );
        
        Response response = RestAssured.given().accept(ContentType.JSON).get(uriResource);
        
        assertThat(response.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        assertThat(response.getBody().jsonPath().getList(".", BookRecordPojo.class).size(), equalTo(sizePage));       
        
        // cleanup
        list.forEach(br -> {
            Response resp = RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON)
                    .delete(this.celsvsBaseUri+"/books"+"/"+br.getIsbn());
            assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT));
            }
            );
    }
    
    
    // POST
    
    @Test(description="Given a non-existing book record, when created, then 201 Created is received")
    public void test_restApiBooksPost_01() {
        
        final String isbn = randomNumeric(13);
        final BookRecordPojo br = instantiateBookRecord(isbn, randomAlphanumeric(10));
        
        this.testPostNewResourceOk(this.celsvsBaseUri+"/books"+"/"+isbn, br, BookRecordPojo.class);
    }
    
    @Test(description="Given an existing book record, when a creation operation has the same isbn, then 409 Conflict is received")
    public void test_restApiBooksPost_02() {
        
        final String isbn = randomNumeric(13);
        final BookRecordPojo br = instantiateBookRecord(isbn, randomAlphanumeric(10));
        
        this.testPostExistingResourceNok(this.celsvsBaseUri+"/books"+"/"+isbn, br);
    }
    
    
    //PUT
    
    @Test(description="Given a non-existing book record, when updated, then 201 Created is received")
    public void test_restApiBooksPut_01() {
        final String isbn = randomNumeric(13);
        final BookRecordPojo br = instantiateBookRecord(isbn, randomAlphanumeric(10));
        
        this.testPutNewResourceOk(this.celsvsBaseUri+"/books"+"/"+isbn, br, BookRecordPojo.class);
    }
     
    @Test(description="Given an existing resource, when a updated, then 200 OK is received")
    public void test_restApiBooksPut_02() {
        final String isbn = randomNumeric(13);
        final BookRecordPojo br = instantiateBookRecord(isbn, randomAlphanumeric(10));
        final String uriResource = this.celsvsBaseUri+"/books"+"/"+isbn;
        
        // post a book
        this.post(uriResource, br, BookRecordPojo.class);
        
        // put to update
        br.setTitle("Much ado about nothing");
        this.put(uriResource, br, BookRecordPojo.class);
        
        // cleanup (plain post, put methods in parent class do not delete test entiti
        this.delete(uriResource);
    }
     
    @Test(description="Given an existing resource, when update operation has the 2ndary ID of another existing resource, then 409 Conflict is received")
    public void test_restApiBooksPut_03() {
        final String isbn = randomNumeric(13);
        final String sign = randomAlphanumeric(10);
        final BookRecordPojo br = instantiateBookRecord(isbn, sign);
        final String uriResource = this.celsvsBaseUri+"/books"+"/"+isbn;
        
        // post a book and
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br)
            .post(uriResource);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // put to update: new book record with a clashing signature:
        final String isbn2 = randomNumeric(13);
        final BookRecordPojo br2 = instantiateBookRecord(isbn2, sign);
        final String uriResource2 = this.celsvsBaseUri+"/books"+"/"+isbn2;
        RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br2)
            .put(uriResource2);
        // Check 
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.CLIENT_ERR_CONFLICT));
        
        // cleanup
        RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON)
                   .delete(uriResource)
                   .then().statusCode(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
     
    // DELETE
    
    @Test(description="Given an existing resource, when a delete operation with primary ID, then 204 No Content is received with no Body")
    public void test_restApiBooksDelete_01() {
        final String isbn = randomNumeric(13);
        final BookRecordPojo br = instantiateBookRecord(isbn, randomAlphanumeric(10));
        
        this.testDeleteExistingEntityOk(this.celsvsBaseUri+"/books"+"/"+isbn, br);
    }
    
    @Test(description="Given an existing resource, when a delete operation with 2ndary ID, then 204 No Content is received with no Body")
    public void test_restApiBooksDelete_02() {
        final String isbn = randomNumeric(13);
        final String signature = randomAlphanumeric(10);
        final BookRecordPojo br = instantiateBookRecord(isbn, signature);
        
        this.testDeleteExistingResource2ndKeyOk(this.celsvsBaseUri+"/books", isbn, signature, br);
        
    }
    
    @Test(description="Given a non-existing resource, when a delete operation with primary ID, then 204 No Content is received with no Body")
    public void test_restApiBooksDelete_03() {
        
        this.testDeleteNonExistingResourceOk(this.celsvsBaseUri+"/books"+"/"+randomNumeric(13));
        
    }
    
    
    // HATEOAS
    
    @Test(groups = {"hypermedia"},
          description="Given an existing resource, when retrieved, then the expected hypermedia links are received and lead to the rigth resources")
    public void test_hypermedia_01() {
        fail("Not yet implemented");
    }
    
    @Test(groups = {"hypermedia"},
          description="Given a non-existing resource, when created, then the expected hypermedia links are received and lead to the rigth resources")
    public void test_hypermedia_02() {
        fail("Not yet implemented");
    }
    
    @Test(groups = {"hypermedia"},
          description="Given a a set of resources, when all retrieved, then the expected hypermedia links are received allowing page navigation with 'next'")
    public void test_hypermedia_03() {
        fail("Not yet implemented");
    }
    
    @Test(groups = {"hypermedia"},
          description="Given a a set of resources, when retrieved paginated, then the expected hypermedia links are received allowing page navigation with 'next'")
    public void test_hypermedia_04() {
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
    
    private List<BookRecordPojo> createManyBookRecords (final int qty){
        List<BookRecordPojo> list = new ArrayList<BookRecordPojo>();
        for (int i=0; i<qty; i++) {
            list.add(instantiateBookRecord(randomNumeric(13), randomAlphanumeric(10)));
        }
        return list;
    }
    
    private BookRecordPojo instantiateBookRecord(final String isbn, final String sign) {
        return new BookRecordPojo(
                isbn, sign,                     // isbn & signature
                randomAlphabetic(20),           // title
                Arrays.asList(randomAlphabetic(15), randomAlphabetic(15)), // authors
                randomAlphabetic(50),           // subtitle
                3,                              // num copies
                true,
                /*new Library(randomAlphabetic(15),       // library name
                        new Address(
                                randomAlphabetic(20),   // street
                                34,                     // num
                                randomAlphabetic(20),   // additional info
                                randomAlphabetic(20),   // city
                                randomNumeric(5))),*/
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
