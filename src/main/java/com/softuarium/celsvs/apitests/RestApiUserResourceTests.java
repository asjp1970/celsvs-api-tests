package com.softuarium.celsvs.apitests;

import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.dtos.UserDto;
import com.softuarium.celsvs.apitests.utils.mongodb.MongoDbOperations;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.delete;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.post;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.put;
import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createDto;
import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createManyDtos;

import org.testng.annotations.Test;

import org.testng.annotations.Parameters;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Test(groups = { "functional", "api", "aum", "users" })
public class RestApiUserResourceTests extends RestApiBaseTester {
    
    private String usersUri;
      
    @Parameters({ "mongodbUri", "dbName", "celsvsBaseUri", "usersUri" })
    @BeforeClass
    public void beforeClass(final String mongodbUri, final String mongoDbName, final String celsvsUri, final String usersUriFragment) {
        this.dbName = mongoDbName;
        this.mongoDbOperations = new MongoDbOperations(mongodbUri, mongoDbName);
        
        this.usersUri = celsvsUri+"/"+usersUriFragment;
        
    }
    
    @Parameters({"usersCollectionName"})
    @BeforeMethod
    @AfterMethod
    public void cleanup(final String usersCollectionName) {
        cleanupDbCollection(Arrays.asList(usersCollectionName));
    }
  
    // GET
    
    @Test(description="Given an existing user record, when retrieved, then 200 OK and correct json is received")
    public void test_restApiUsersGet_01() {
        final String userId = randomNumeric(10);
        final UserDto ur = (UserDto) createDto(UserDto.class, userId);
        
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
        final UserDto dto = (UserDto) createDto(UserDto.class, userId, secondaryUid);
        
        this.testGetExistingEntity2ndKey(usersUri, userId, secondaryUid, dto);
    }
    
    @Test(description="Given several existing user records, when all retrieved, then 200 OK")
    public void test_restApiUsersGet_04() {
        final int totalRecords = 10;
        
        List<UserDto> list = createManyDtos(UserDto.class, totalRecords);
        list.forEach(r -> {
            Response resp = RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON).body(r)
                                .post(this.usersUri+"/"+r.getUserId());
            assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        });
        
        this.testGetAllResources(this.usersUri, totalRecords, UserDto.class);
        
        // cleanup
        list.forEach(r -> {
            Response resp = RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON)
                    .delete(this.usersUri+"/"+r.getUserId());
            assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT));
            }
            );
    }
    
    
    // POST
    
    @Test(description="Given a non-existing user record, when created, then 201 Created is received")
    public void test_restApiUserPost_01() {
        
        final String userId = randomAlphanumeric(10);
        final UserDto dto = (UserDto) createDto(UserDto.class, userId);
        
        this.testPostNewResourceOk(usersUri+"/"+userId, dto, UserDto.class);
    }
    
    @Test(description="Given an existing user record, when a creation operation has the same uid, then 409 Conflict is received")
    public void test_restApiUserPost_02() {
        
        final String userId = randomNumeric(10);
        final UserDto dto = (UserDto) createDto(UserDto.class, userId);
        
        this.testPostExistingResourceNok(usersUri+"/"+userId, dto);
    }
    
    
    //PUT
    
    @Test(description="Given a non-existing user record, when updated, then 201 Created is received")
    public void test_restApiUsersPut_01() {
        final String userId = randomNumeric(10);
        final UserDto dto = (UserDto) createDto(UserDto.class, userId);
        
        this.testPutNewResourceOk(this.usersUri+"/"+userId, dto, UserDto.class);
    }
     
    @Test(description="Given an existing user record, when updated, then 200 OK is received")
    public void test_restApiUsersPut_02() {
        final String userId = randomNumeric(10);
        final UserDto dto = (UserDto) createDto(UserDto.class, userId);
        
        // POST a user
        post(this.usersUri+"/"+userId, dto, UserDto.class);
        
        // PUT to update same user
        dto.setPassword("ABadPassword");
        put(this.usersUri+"/"+userId, dto, UserDto.class);
        
        // cleanup (plain post, put methods in parent class do not delete test entity
        delete(this.usersUri+"/"+userId, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    @Test(description="Given an existing user record, when PUT operation has the second id of another existing user record, then 409 Conflict is received")
    public void test_restApiUsersPut_03() {
        final String userId = randomNumeric(10);
        final String secondId = randomAlphanumeric(10);
        final UserDto dto = (UserDto) createDto(UserDto.class, userId, secondId);
        
        // post a user and
        post(this.usersUri+"/"+userId, dto, RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        // put to update: new user record with a clashing secondary ID:
        final String userId2 = randomNumeric(10);
        final UserDto br2 = (UserDto) createDto(UserDto.class, userId2, secondId);
        
        put(this.usersUri+"/"+userId2, br2, RestApiHttpStatusCodes.CLIENT_ERR_CONFLICT);
        
        // cleanup
        delete(this.usersUri+"/"+userId, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        delete(this.usersUri+"/"+userId2, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    
    // DELETE
    
    @Test(description="Given an existing user record, when a delete operation with userId, then 204 No Content is received with no Body")
    public void test_restApiUsersDelete_01() {
        final String userId = randomNumeric(10);
        final UserDto dto = (UserDto) createDto(UserDto.class, userId);
        
        this.testDeleteExistingEntityOk(this.usersUri+"/"+userId, dto);
    }
    
    @Test(description="Given an existing user record, when a delete operation with secondary Id, then 204 No Content is received with no Body")
    public void test_restApiUsersDelete_02() {
        final String userId = randomNumeric(10);
        final String secondId = randomAlphanumeric(10);
        final UserDto dto = (UserDto) createDto(UserDto.class, userId, secondId);
        
        this.testDeleteExistingResource2ndKeyOk(this.usersUri, userId, secondId, dto);
        
    }
    
    @Test(description="Given a non-existing user record, when a delete operation with user id, then 204 No Content is received with no Body")
    public void test_restApiUsersDelete_03() {
        
        this.testDeleteNonExistingResourceOk(this.usersUri+"/"+randomNumeric(13));
        
    }
}
