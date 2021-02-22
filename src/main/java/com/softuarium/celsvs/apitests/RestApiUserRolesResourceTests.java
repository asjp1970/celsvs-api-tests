package com.softuarium.celsvs.apitests;

import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.dtos.RoleDto;
import com.softuarium.celsvs.apitests.utils.mongodb.MongoDbOperations;

import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.delete;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.post;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.put;
import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createDto;

import org.testng.annotations.Test;

import org.testng.annotations.Parameters;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import java.util.Arrays;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Test(groups = { "functional", "api", "aum", "roles" })
public class RestApiUserRolesResourceTests extends RestApiBaseTester {
    
    private String rolesUri;
    
    @Parameters({ "mongodbUri", "dbName", "celsvsBaseUri", "rolesUri" })
    @BeforeClass
    public void beforeClass(final String mongodbUri, final String mongoDbName, final String celsvsUri, final String rolesUriFragment) {
        this.dbName = mongoDbName;
        this.mongoDbOperations = new MongoDbOperations(mongodbUri, mongoDbName);
        
        this.rolesUri = celsvsUri+"/"+rolesUriFragment;
        
    }
    
    @Parameters({"usersCollectionName"})
    @BeforeMethod
    @AfterMethod
    public void cleanup(final String usersColName) {
        cleanupDbCollection(Arrays.asList(usersColName));
    }
  
    // GET
    
    @Test(description="Given an existing user group, when retrieved, then 200 OK and correct json is received")
    public void test_restApiUserGroupsGet_01() {
        final String name = randomAlphabetic(12);
        final RoleDto ur = (RoleDto) createDto(RoleDto.class, name);
        
        testGetExistingEntity(rolesUri+"/"+name, ur);
    }
    
    @Test(description="Given a non-existing user record, when retrieved, then 404 not found is received")
    public void test_restApiUserGroupsGet_02() {
        String isbnRandom = randomNumeric(13);
    
        this.testGetNonExistingEntity(this.rolesUri+"/"+isbnRandom);
    }
    
    
    @Test(description="Given several existing user records, when all retrieved, then 200 OK")
    public void test_restApiUserGroupsGet_03() {
    
        //this.testGetAllResources(rolesUri, 0);
    }
    
    
    // POST
    
    @Test(description="Given a non-existing user record, when created, then 201 Created is received")
    public void test_restApiUserGroupPost_01() {
        
        final String name = randomAlphanumeric(10);
        final RoleDto dto = (RoleDto) createDto(RoleDto.class, name);
        
        this.testPostNewResourceOk(rolesUri+"/"+name, dto, RoleDto.class);
    }
    
    @Test(description="Given an existing user record, when a creation operation has the same uid, then 409 Conflict is received")
    public void test_restApiUserGroupPost_02() {
        
        final String name = randomNumeric(10);
        final RoleDto dto = (RoleDto) createDto(RoleDto.class, name);
        
        this.testPostExistingResourceNok(rolesUri+"/"+name, dto);
    }
    
    
    //PUT
    
    @Test(description="Given a non-existing user record, when updated, then 201 Created is received")
    public void test_restApiUserGroupsPut_01() {
        final String name = randomNumeric(10);
        final RoleDto dto = (RoleDto) createDto(RoleDto.class, name);
        
        this.testPutNewResourceOk(this.rolesUri+"/"+name, dto, RoleDto.class);
    }
     
    @Test(description="Given an existing user record, when updated, then 200 OK is received")
    public void test_restApiUserGroupsPut_02() {
        final String name = randomNumeric(10);
        final RoleDto dto = (RoleDto) createDto(RoleDto.class, name);
        
        // POST a user
        post(this.rolesUri+"/"+name, dto, RoleDto.class);
        
        // PUT to update same user
        final RoleDto dto2 = (RoleDto) createDto(RoleDto.class, name);
        put(this.rolesUri+"/"+name, dto2, RoleDto.class);
        
        // cleanup (plain post, put methods in parent class do not delete test entity
        delete(this.rolesUri+"/"+name, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }

    
    // DELETE
    
    @Test(description="Given an existing user record, when a delete operation with name, then 204 No Content is received with no Body")
    public void test_restApiUserGroupsDelete_01() {
        final String name = randomNumeric(20);
        final RoleDto dto = (RoleDto) createDto(RoleDto.class, name);
        
        this.testDeleteExistingEntityOk(this.rolesUri+"/"+name, dto);
    }
    
    
    @Test(description="Given a non-existing user record, when a delete operation with user id, then 204 No Content is received with no Body")
    public void test_restApiUserGroupsDelete_03() {
        
        this.testDeleteNonExistingResourceOk(this.rolesUri+"/"+randomNumeric(20));
        
    }
}
