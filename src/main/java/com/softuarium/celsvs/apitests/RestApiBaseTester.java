package com.softuarium.celsvs.apitests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.dtos.BookDto;
import com.softuarium.celsvs.apitests.utils.dtos.CheckoutDto;
import com.softuarium.celsvs.apitests.utils.dtos.ITestDto;
import com.softuarium.celsvs.apitests.utils.dtos.PublisherDto;
import com.softuarium.celsvs.apitests.utils.dtos.RoleDto;
import com.softuarium.celsvs.apitests.utils.dtos.UserDto;
import com.softuarium.celsvs.apitests.utils.mongodb.MongoDbOperations;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.delete;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public abstract class RestApiBaseTester {
    
    protected String mongoDbUri;
    protected String dbName;
    protected MongoDbOperations mongoDbOperations;
           
    @Parameters({ "mongodbUri", "dbName", "celsvsBaseUri" })
    @BeforeSuite(alwaysRun=true)
    public void testSuiteSetup(final String mongodbUri, final String mongoDbName, final String celsvsUri) {
        
    }
    
    @AfterSuite(alwaysRun=true)
    public void testSuiteTeardown() {
    }
    
    protected void testGetExistingEntity(final String uri, Object entity) {
        
        // post the entity
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        assertThat(resp.getBody().as(entity.getClass()), equalTo(entity));
        
        // cleanup
        delete(uri, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    protected void testGetWithWrongAcceptHeader(final String uri, Object entity) {
        
        // post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        resp = RestAssured.given().accept(ContentType.JSON).get(uri);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.CLIENT_ERR_NOT_ACCEPTABLE));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        assertThat(resp.getBody().as(entity.getClass()), equalTo(entity));
        
        // cleanup
        delete(uri, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    protected void testGetExistingEntityWithoutBodyComparison(final String uri, Object entity) {
        
        // post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        
        // cleanup
        delete(uri, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    protected void testGetNonExistingEntity(final String uri) {
        Response resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.CLIENT_ERR_NOT_FOUND));
    }
    
    protected void testGetExistingEntity2ndKey(final String baseUri, final String primaryId, final String secondaryId, Object entity) {
        
        // Given - post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(baseUri+"/"+primaryId);
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // When - retrieved
        resp = RestAssured.given().accept("application/hal+json").get(baseUri+"/"+secondaryId);
        
        // Then - 200 (OK)
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        
        // TODO
        //assertThat(parseString(resp.getBody().asString()),
        //        equalTo(parseString(getJsonFromFile("jsonfiles/expectedBookRecord.json"))));
        
        // cleanup
        delete(baseUri+"/"+primaryId, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);

    }
    
    protected <D extends ITestDto> void testGetAllResources(final String uri, final int nrOfResources, Class<D> dtoClazz) {
        
        Response resp = RestAssured.given().accept("application/hal+json").get(uri); 
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        assertThat(resp.getBody().jsonPath().getList("_embedded"+"."+this.relName(dtoClazz)).size(), equalTo(nrOfResources));
        
    }
    
    protected <D extends ITestDto> void testGetAllPaginatedAndSorted(final String paginationUri, final int sizePage, Class<D> dtoClazz) {
        
        Response response = RestAssured.given().accept("application/json").get(paginationUri);
        
        assertThat(response.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));

        assertThat(response.getBody().jsonPath().getList("_embedded"+"."+this.relName(dtoClazz)).size(), equalTo(sizePage));
        
    }
    
    protected <T> void testPostNewResourceOk (final String uri, Object entity, Class<T> clazzEntity) {
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(entity));
        
        // cleanup
        delete(uri, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
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
        delete(uri, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        
    }
    
    protected <T> void testPutNewResourceOk (final String uri, Object entity, Class<T> clazzEntity) {
        
        // Put a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .put(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(entity));
        
        // cleanup
        delete(uri, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    protected <T> void testPutExistingResourceOk (final String uri, Object entity, Class<T> clazzEntity) {
        
        // Put a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(entity)
            .put(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(entity));
        
        // cleanup
        delete(uri, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
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
        delete(uri, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
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
        delete(uri2ndKey, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    public void testDeleteNonExistingResourceOk(final String uri) {
        // Delete the resource and check 204 (No Content)
        delete(uri, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    public static void stepInfo (final String stepInfoStr) {
        
    }

    
    protected void cleanupDbCollection(final List<String> collectionNames) {
        
        this.mongoDbOperations.cleanupDb(dbName, collectionNames);
    }
    
    protected <E extends ITestDto> String relName (Class<E> dtoClazz) {
        
        if (dtoClazz.isAssignableFrom(CheckoutDto.class)) {
            return "checkoutRecords";
        }
        else if (dtoClazz.isAssignableFrom(BookDto.class)) {                    
            return "books";
        }
        else if(dtoClazz.isAssignableFrom(UserDto.class)) {
            return "users";
        }
        else if(dtoClazz.isAssignableFrom(RoleDto.class)) {
            return "roles";
        }
        else if(dtoClazz.isAssignableFrom(PublisherDto.class)) {
            return "publishers";
        }
        return null;
        
    }

}
