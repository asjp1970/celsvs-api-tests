package com.softuarium.celsvs.apitests;

import com.softuarium.celsvs.apitests.utils.TestParent;
import com.softuarium.celsvs.apitests.utils.dtos.CkeckoutDto;

import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createDto;

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
public class RestApiCheckoutResourceTests extends TestParent {
    
    private String checkoutsUri;
    private String checkoutsDocUri;
    
    @Parameters({ "celsvsBaseUri", "checkoutsUri" })
    @BeforeClass
    public void beforeClass(final String celsvsUri, final String checkoutsUriFragment) {
        
        this.checkoutsUri = celsvsUri + "/" + checkoutsUriFragment;
        this.checkoutsDocUri = checkoutsUri + "/document";
        
    }
    
    @Parameters({ "checkoutsCollectionName" })
    @AfterClass
    public void afterClass(final String checkoutsCollection) {
        
        List<String> collectionNames = Arrays.asList(checkoutsCollection);
        super.cleanupDbCollection(collectionNames);
        
    }
    
    
    // GET
    
    @Test(description="Given an existing checkout record, when retrieved, then 200 OK and correct json is received")
    public void test_restApiCheckoutsGet_01() {
        final String signature = randomNumeric(10);
        final CkeckoutDto dto = (CkeckoutDto) createDto(CkeckoutDto.class, signature);
        
        testGetExistingEntity(checkoutsDocUri + "/" + signature, dto);
    }
    
    @Test(description="Given a non-existing user record, when retrieved, then 404 not found is received")
    public void test_restApiUsersGet_02() {
        String isbnRandom = randomNumeric(13);
    
        this.testGetNonExistingEntity(this.checkoutsUri+"/"+isbnRandom);
    }
    
    @Test(description="Given an existing user record, when retrieved using public Id, then 200 OK and correct json is received")
    public void test_restApiUsersGet_03() {
        final String userId = randomNumeric(10);
        final String secondaryUid = randomAlphanumeric(10);
        //final UserDto ur = instantiateCheckoutDto(userId, secondaryUid);
        
        //this.testGetExistingEntity2ndKey(checkoutsUri, userId, secondaryUid, ur);
    }
    
    @Test(description="Given several existing user records, when all retrieved, then 200 OK")
    public void test_restApiUsersGet_04() {
    
        this.testGetAllResources(checkoutsUri);
    }
    
    @Test(description="Given a non-existing user record, when created, then 201 Created is received")
    public void test_restApiUserPost_01() {
        
        final String uid = randomAlphanumeric(10);
        //final UserDto r = instantiateCheckoutDto(uid, randomAlphanumeric(10));
        
        //this.testPostNewResourceOk(checkoutsUri+"/"+uid, r, UserDto.class);
    }
    
    @Test(description="Given an existing user record, when a creation operation has the same uid, then 409 Conflict is received")
    public void test_restApiUserPost_02() {
        
        final String uid = randomNumeric(10);
        //final UserDto r = instantiateCheckoutDto(uid, randomAlphanumeric(10));
        
        //this.testPostExistingResourceNok(checkoutsUri+"/"+uid, r);
    }
    
    
}
