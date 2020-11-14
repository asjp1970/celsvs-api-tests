package com.softuarium.celsvs.apitests.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;


import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.softuarium.celsvs.apitests.utils.dtos.ITestDto;
import com.softuarium.celsvs.apitests.utils.mongodb.MongoDbOperations;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public abstract class TestParent {
    
    protected String mongoDbUri;
    private String dbName;
    private MongoDbOperations mongoDbOperations;

    protected TestParent() {
        // TODO Auto-generated constructor stub
    }
    
    @Parameters({ "mongodbUri", "dbName", "celsvsBaseUri" })
    @BeforeSuite
    public void beforeSuite(final String mongodbUri, final String mongoDbName, final String celsvsUri) {
        this.dbName = mongoDbName;
        this.mongoDbOperations = new MongoDbOperations("mongodb+srv://celsvs:C0AkLkBoEc2wopDc@celsvs-cluster-0-hskuw.gcp.mongodb.net/test?retryWrites=true&w=majority");

        // RestAssured.baseURI = this.celsvsBaseUri;
    }
    
    protected void testGetExistingEntity(final String uri, Object entity) {
        
        // post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        resp = RestAssured.given().accept(ContentType.JSON).get(uri);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        
        // TODO
        //assertThat(parseString(resp.getBody().asString()),
        //        equalTo(parseString(getJsonFromFile("jsonfiles/expectedBookRecord.json"))));
        
        // cleanup
        delete(uri);
    }
    
    protected void testGetNonExistingEntity(final String uri) {
        Response resp = RestAssured.given().accept(ContentType.JSON).get(uri);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.CLIENT_ERR_NOT_FOUND));
    }
    
    protected void testGetExistingEntity2ndKey(final String baseUri, final String primaryId, final String secondaryId, Object entity) {
        
        // Given - post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(baseUri+"/"+primaryId);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // When - retrieved
        resp = RestAssured.given().accept(ContentType.JSON).get(baseUri+"/"+secondaryId);
        
        // Then - 200 (OK)
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        
        // TODO
        //assertThat(parseString(resp.getBody().asString()),
        //        equalTo(parseString(getJsonFromFile("jsonfiles/expectedBookRecord.json"))));
        
        // cleanup
        delete(baseUri+"/"+primaryId);
    }
    
    protected void testGetAllResources(final String uri) {
        Response resp = RestAssured.given().accept(ContentType.JSON).get(uri);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
    }
    
    protected <T> void testGetAllPaginatedAndSorted(final String paginationUri, List<T> dtosList) {
        
        final int totalRecords = 10;
        final int numPages = 5;
        final int sizePage = 2;

       
    }
    
    protected <T> void testPostNewResourceOk (final String uri, Object entity, Class<T> clazzEntity) {
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept(ContentType.JSON).get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(entity));
        
        // cleanup
        delete(uri);
    }
    
    protected void testPostExistingResourceNok (final String uri, Object entity) {
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Send the 2nd POST over the same resource
        resp = RestAssured
                .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
                .post(uri);
        
        // Check 409 - Conflict
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.CLIENT_ERR_CONFLICT));
        
        // cleanup
        delete(uri);
        
    }
    
    protected <T> void testPutNewResourceOk (final String uri, Object entity, Class<T> clazzEntity) {
        
        // Put a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .put(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept(ContentType.JSON).get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(entity));
        
        // cleanup
        delete(uri);
    }
    
    protected <T> void testPutExistingResourceOk (final String uri, Object entity, Class<T> clazzEntity) {
        
        // Put a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .put(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept(ContentType.JSON).get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(entity));
        
        // cleanup
        delete(uri);
    }
    
    protected <T> void testPutExistingEntityWhisSame2ndKeyNok (final String baseUri, final String primaryId, final String secondaryId, Object entity, Class<T> clazzEntity) {
        
        
    }
    
    protected void testDeleteExistingEntityOk(final String uri, Object entity) {
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Delete the resource and check 204 (No Content)
        delete(uri);
    }
    
    protected void testDeleteExistingResource2ndKeyOk(final String baseUri, final String primaryId, final String secondaryId, Object entity) {
        
        final String uri = baseUri+"/"+primaryId;
        final String uri2ndKey = baseUri+"/"+secondaryId;
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Delete the resource and check 204 (No Content)
        delete(uri2ndKey);
    }
    
    public void testDeleteNonExistingResourceOk(final String uri) {
        // Delete the resource and check 204 (No Content)
        delete(uri);
    }
    
    public static void stepInfo (final String stepInfoStr) {
        
    }
    
    protected void get(final String uri, final int expectedStatusCode) {
        
        RestAssured.given().accept(ContentType.JSON).get(uri).then().statusCode(expectedStatusCode);
                
    }
    
    protected <T> Response post (final String uri, Object entity, Class<T> clazzEntity) {
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept(ContentType.JSON).get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(entity));
        
        return resp;
    }
    
    protected <D extends ITestDto> Response post (final String uri, D dto, final int expectedStatusCode) {
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(dto)
            .post(uri);
        
        // Check http status code
        assertThat(resp.getStatusCode(), equalTo(expectedStatusCode));
        
        return resp;
    }
    
    protected <T> void put (final String uri, Object entity, Class<T> clazzEntity) {
        
        // Put a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .put(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept(ContentType.JSON).get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(entity));
        
    }
    
    protected <T> void put (final String uri, Object entity, Class<T> clazzEntity, final RestApiHttpStatusCodes expectedResultCode) {
        
        // Put a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .put(uri);
        
        // Check return code
        assertThat(resp.getStatusCode(), equalTo(expectedResultCode));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept(ContentType.JSON).get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(entity));
        
    }
    
    protected void delete(final String uri) {
        RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON)
            .delete(uri)
            .then().statusCode(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    protected void cleanupDbCollection(final List<String> collectionNames) {
        this.mongoDbOperations.cleanupDb(dbName, collectionNames);
    }

}
