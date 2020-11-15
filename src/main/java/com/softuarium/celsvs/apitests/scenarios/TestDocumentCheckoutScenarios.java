package com.softuarium.celsvs.apitests.scenarios;

import com.softuarium.celsvs.apitests.TestParent;
import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.dtos.BookDto;
import com.softuarium.celsvs.apitests.utils.dtos.CkeckoutDto;
import com.softuarium.celsvs.apitests.utils.dtos.UserDto;
import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createDto;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Test(groups = { "functional", "scenarios" })
public class TestDocumentCheckoutScenarios extends TestParent {
    
    private String checkoutsUri;
    private String checkoutsDocUri;
    private String booksUri;
    private String usersUri;

    public TestDocumentCheckoutScenarios() {
        // TODO Auto-generated constructor stub
    }
    
    @Parameters({ "celsvsBaseUri", "checkoutsUri", "booksUri", "usersUri" })
    @BeforeClass
    public void beforeClass(final String celsvsUri, final String checkoutsUriFragment,
            final String booksUriFragment, final String usersUriFragment) {
        
        this.checkoutsUri = celsvsUri + "/" + checkoutsUriFragment;
        this.checkoutsDocUri = checkoutsUri + "/document";
        this.booksUri = celsvsUri+"/"+booksUriFragment;
        this.usersUri = celsvsUri+"/"+usersUriFragment;
        
    }
    
    /**
     * Scenario 1
     * Given inexistent book and user records in database, when attempt to checkout, then if fails until all resources are created
     */
    @Test(description="Checkout not possible until book and user records exist in database")
    public void test_checkoutScenario_01() {
        
        final String isbn = randomNumeric(13);
        final String signature = randomAlphanumeric(10);
        final String userId = randomAlphanumeric(8);
        CkeckoutDto checkoutDto = (CkeckoutDto) createDto(CkeckoutDto.class, signature);
        checkoutDto.setUserId(userId);
        
        // Step 1. Attempt the checkout by doing a POST on the checkout. Check that 404 Not found
        // is returned because the document does not exist
        this.post(this.checkoutsDocUri + "/" + signature, checkoutDto,
                RestApiHttpStatusCodes.CLIENT_ERR_NOT_FOUND);
        
        // Step 2. Post the document to checkout
        this.post(this.booksUri + "/" + isbn, 
                createDto(BookDto.class, isbn, signature),
                RestApiHttpStatusCodes.SUCCESS_CREATED);

        // Step 3. Attempt the checkout again. Now it should fail because the user is not defined,
        // again with 404 Not found
        this.post(this.checkoutsDocUri + "/" + signature, checkoutDto,
                RestApiHttpStatusCodes.CLIENT_ERR_NOT_FOUND);
        
        // Step 4. Post the library user
        this.post(this.usersUri + "/" + userId, 
                createDto(UserDto.class, userId),
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        // Step 5. Attempt the checkout again. This time it is expected 202 Created
        this.post(this.checkoutsDocUri + "/" + signature, checkoutDto,
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        // Step 6. cleanup resources
        this.delete(this.checkoutsDocUri + "/" + signature, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        this.delete(this.usersUri + "/" + userId, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        this.delete(this.booksUri + "/" + isbn, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);

        
    }
    
    /**
     * Scenario 2
     * Given a limit of simultaneous document checkouts = 3, when the 4rth checkout is attempted, then it fails with 403 Forbidden
     */
    @Test(description="Check limit of nr of document checkouts for the same user")
    public void test_checkoutScenario_02() {
        
        final String isbn1 = randomNumeric(13);
        final String isbn2 = randomNumeric(13);
        final String isbn3 = randomNumeric(13);
        final String isbn4 = randomNumeric(13);
        final String signature1 = randomAlphanumeric(10);
        final String signature2 = randomAlphanumeric(10);
        final String signature3 = randomAlphanumeric(10);
        final String signature4 = randomAlphanumeric(10);
        final String userId = randomAlphanumeric(8);
                
        // Step 1. Create 4 book and 1 user records
        this.post(this.booksUri + "/" + isbn1, 
                createDto(BookDto.class, isbn1, signature1),
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        this.post(this.booksUri + "/" + isbn2, 
                createDto(BookDto.class, isbn2, signature2),
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        this.post(this.booksUri + "/" + isbn3, 
                createDto(BookDto.class, isbn3, signature3),
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        this.post(this.booksUri + "/" + isbn4, 
                createDto(BookDto.class, isbn4, signature4),
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        this.post(this.usersUri + "/" + userId, 
                createDto(UserDto.class, userId),
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        // Step 2. checkout 3 documents for the same user. In all cases 202 Created is expected
        // because the user is under the limit of simultaneous checkouts
        CkeckoutDto checkoutDto1 = (CkeckoutDto) createDto(CkeckoutDto.class, signature1);
        checkoutDto1.setUserId(userId);
        this.post(this.checkoutsDocUri + "/" + signature1, checkoutDto1,
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        CkeckoutDto checkoutDto2 = (CkeckoutDto) createDto(CkeckoutDto.class, signature2);
        checkoutDto2.setUserId(userId);
        this.post(this.checkoutsDocUri + "/" + signature2, checkoutDto2,
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        CkeckoutDto checkoutDto3 = (CkeckoutDto) createDto(CkeckoutDto.class, signature3);
        checkoutDto3.setUserId(userId);
        this.post(this.checkoutsDocUri + "/" + signature3, checkoutDto3,
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        // Step 3. A 4rth document checkout is expected to be answered with 403 - Forbidden:
        CkeckoutDto checkoutDto4 = (CkeckoutDto) createDto(CkeckoutDto.class, signature4);
        checkoutDto4.setUserId(userId);
        this.post(this.checkoutsDocUri + "/" + signature4, checkoutDto4,
                RestApiHttpStatusCodes.CLIENT_ERR_FORBIDDEN);
        
        // Step 4. cleanup resources
        this.delete(this.checkoutsDocUri + "/" + signature1, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        this.delete(this.checkoutsDocUri + "/" + signature2, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        this.delete(this.checkoutsDocUri + "/" + signature3, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        this.delete(this.usersUri + "/" + userId, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        this.delete(this.booksUri + "/" + isbn1, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        this.delete(this.booksUri + "/" + isbn2, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        this.delete(this.booksUri + "/" + isbn3, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        this.delete(this.booksUri + "/" + isbn4, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);

    }
    
}


