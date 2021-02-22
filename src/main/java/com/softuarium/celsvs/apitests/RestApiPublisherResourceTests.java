package com.softuarium.celsvs.apitests;

import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.dtos.PublisherDto;
import com.softuarium.celsvs.apitests.utils.mongodb.MongoDbOperations;

import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createDto;
import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createManyDtos;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.delete;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Parameters;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Test(groups = { "functional", "api", "books" })
public class RestApiPublisherResourceTests extends RestApiBaseTester {
   
    private String publishersUri;
    
    @Parameters({ "mongodbUri", "dbName", "celsvsBaseUri", "publishersUri" })
    @BeforeClass
    public void beforeClass(final String mongodbUri, final String mongoDbName, final String celsvsUri, final String publishersUriFragment) {
        this.dbName = mongoDbName;
        this.mongoDbOperations = new MongoDbOperations(mongodbUri, mongoDbName);
        
        this.publishersUri = celsvsUri+"/"+publishersUriFragment;
        
    }
    
    @Parameters({ "publishersCollectionName" })
    @BeforeMethod
    @AfterMethod
    public void cleanup(final String publishersCollectionName) {
        cleanupDbCollection(Arrays.asList(publishersCollectionName));
    }
    
    // GET
    
    @Test(description="Given an existing publisher, when retrieved, then 200 OK and correct json is received")
    public void test_restApiPublisherGet_01() {
        final String name = randomAlphabetic(12);
        final PublisherDto dto = (PublisherDto) createDto(PublisherDto.class, name);
        
        testGetExistingEntity(this.publishersUri+"/"+name, dto);
    }
    
    @Test(description="Given a non-existing publisher, when retrieved, then 404 not found is received")
    public void test_restApiPublisherGet_02() {
    
        this.testGetNonExistingEntity(this.publishersUri+"/"+randomNumeric(13));
    }
    
    @Test(description="Given several existing checkout records, when all retrieved, then 200 OK")
    public void test_restApiPublisherGet_03() {
        final int totalRecords = 10;
        
        List<PublisherDto> list = createManyDtos(PublisherDto.class, totalRecords);
        list.forEach(p -> {
            Response resp = RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON).body(p)
                                .post(this.publishersUri+"/"+p.getName());
            assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        });
        
        String fetchAllUri = this.publishersUri.concat(String.format("?page=0&size=%d", totalRecords));
        
        this.testGetAllResources(fetchAllUri, totalRecords, PublisherDto.class);
        
        // cleanup
        list.forEach(p -> {
            Response resp = RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON)
                    .delete(this.publishersUri+"/"+p.getName());
            assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT));
            }
            );
    }
    
    @Test (description="Given several existing publishers, when pages retrieved, then 200 OK and pages are OK")
    public void test_restApiPublisherGet_04() {
        
        final int totalRecords = 10;
        final int pages = 2;
        
        // publishers?page=0&size=2&sortBy=isbn&sortOrder=asc
        final String uriResource = this.publishersUri.concat(String.format("?page=%d&size=%d&sortBy=name&sortOrder=asc", 0, pages));
    
        List<PublisherDto> list = createManyDtos(PublisherDto.class, totalRecords);
        list.forEach(p -> {
            Response resp = RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON).body(p)
                                .post(this.publishersUri+"/"+p.getName());
            assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        });
        
        this.testGetAllPaginatedAndSorted(uriResource, pages, PublisherDto.class);
        
        // cleanup
        
        list.forEach(p -> {
            Response resp = RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON)
                    .delete(this.publishersUri+"/"+p.getName());
            assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT));
            }
            );
    }
    
    @Test(description="Given an existing Publisher, when retrieved with Accept other than 'application/hal+json', then 406 Not Acceptable is received")
    public void test_restApiPublisherGet_05() {
        final String name = randomNumeric(13);
        final PublisherDto p = (PublisherDto) createDto(PublisherDto.class, name);
        
        testGetWithWrongAcceptHeader(this.publishersUri+"/"+name, p);
    }
    
    
    // POST
    
    @Test(description="Given a non-existing Publisher, when created, then 201 Created is received")
    public void test_restApiPublisherPost_01() {
        
        final String name = randomAlphabetic(12);
        final PublisherDto dto = (PublisherDto) createDto(PublisherDto.class, name);
        
        this.testPostNewResourceOk(this.publishersUri+"/"+name, dto, PublisherDto.class);
    }
    
    @Test(description="Given an existing Publisher, when a creation operation has the same name, then 409 Conflict is received")
    public void test_restApiPublisherPost_02() {
        
        final String name = randomAlphabetic(12);
        final PublisherDto dto = (PublisherDto) createDto(PublisherDto.class, name);
        
        this.testPostExistingResourceNok(this.publishersUri+"/"+name, dto);
    }
    
    
    //PUT
    
    @Test(description="Given a non-existing book record, when updated, then 201 Created is received")
    public void test_restApiPublisherPut_01() {
        final String name = randomAlphabetic(12);
        final PublisherDto dto = (PublisherDto) createDto(PublisherDto.class, name);
        
        this.testPutNewResourceOk(this.publishersUri+"/"+name, dto, PublisherDto.class);
    }
     
    @Test(description="Given an existing Publisher, when updated, then 200 OK is received")
    public void test_restApiPublisherPut_02() {
        final String name = randomAlphabetic(12);
        final PublisherDto dto = (PublisherDto) createDto(PublisherDto.class, name);
        
        this.testPutExistingResourceOk(this.publishersUri+"/"+name, dto, PublisherDto.class);
        
        // cleanup (plain post, put methods in parent class do not delete test entity
        delete(this.publishersUri+"/"+name, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
       
    // DELETE
    
    @Test(description="Given an existing Publisher, when a deleted, then 204 No Content is received with no Body")
    public void test_restApiPublisherDelete_01() {
        final String name = randomAlphabetic(12);
        final PublisherDto dto = (PublisherDto) createDto(PublisherDto.class, name);
        
        this.testDeleteExistingEntityOk(this.publishersUri+"/"+name, dto);
    }
    
    @Test(description="Given a non-existing publisher, when deleted, then 204 No Content is received with no Body")
    public void test_restApiPublisherDelete_03() {
        
        this.testDeleteNonExistingResourceOk(this.publishersUri+"/"+randomNumeric(12));
        
    }
}
