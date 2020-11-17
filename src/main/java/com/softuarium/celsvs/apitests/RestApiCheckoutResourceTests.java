package com.softuarium.celsvs.apitests;

import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.dtos.BookDto;
import com.softuarium.celsvs.apitests.utils.dtos.CheckoutDto;
import com.softuarium.celsvs.apitests.utils.dtos.UserDto;
import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createDto;
import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createManyDtos;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.post;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.delete;

import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeClass;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import java.util.List;

@Test(groups = { "functional", "api", "checkouts" })
public class RestApiCheckoutResourceTests extends RestApiBaseTester {
    
    private String checkoutsUri;
    private String checkoutsDocUri;
    private String booksUri;
    private String usersUri;
    
    @Parameters({ "celsvsBaseUri", "checkoutsUri", "booksUri", "usersUri" })
    @BeforeClass
    public void beforeClass(final String celsvsUri, final String checkoutsUriFragment, final String booksUri, final String usersUri) {
        
        this.checkoutsUri = celsvsUri + "/" + checkoutsUriFragment;
        this.checkoutsDocUri = checkoutsUri + "/document";
        this.booksUri = celsvsUri + "/" + booksUri;
        this.usersUri = celsvsUri + "/" + usersUri;
    }
    
    
    // GET
    
    @Test(description="Given an existing checkout record, when retrieved, then 200 OK and correct json is received")
    public void test_restApiCheckoutsGet_01() {
        final String isbn = randomNumeric(10);
        final String signature = randomNumeric(10);
        final String userId = randomNumeric(8);
        
        // Create the preconditions: a book and a user records must exist:
        final BookDto bookDto = (BookDto) createDto(BookDto.class, isbn, signature);
        final UserDto userDto = (UserDto) createDto(UserDto.class, userId);
        post(booksUri + "/" + isbn, bookDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
        post(usersUri + "/" + userId, userDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
              
        // POST & GET the checkout record
        CheckoutDto checkoutDto = (CheckoutDto) createDto(CheckoutDto.class, signature);
        checkoutDto.setUserId(userId);
        testGetExistingEntity(checkoutsDocUri + "/" + signature, checkoutDto);
        
        // cleanup
        delete(booksUri + "/" + isbn, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        delete(usersUri + "/" + userId, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    @Test(description="Given a non-existing checkout record, when retrieved, then 404 not found is received")
    public void test_restApiCheckoutsGet_02() {
        final String signature = randomNumeric(10);
    
        this.testGetNonExistingEntity(this.checkoutsDocUri+"/"+signature);
    }
    
    @Test(description="Given several existing checkout records, when all retrieved, then 200 OK")
    public void test_restApiCheckoutsGet_03() {
        
        // Create the preconditions: 3 book and 1 user records must exist and POST 3 checkouts:
        List<BookDto> bookDtosList = createManyDtos(BookDto.class, 3);
        bookDtosList.forEach(d -> {
                post(booksUri + "/" + d.getIsbn(), d, RestApiHttpStatusCodes.SUCCESS_CREATED);
            });
        
        final String userId = randomNumeric(8);
        final UserDto userDto = (UserDto) createDto(UserDto.class, userId);
        post(usersUri + "/" + userId, userDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        bookDtosList.forEach(d -> {
                CheckoutDto checkoutDto = (CheckoutDto) createDto(CheckoutDto.class, d.getSignature());
                checkoutDto.setUserId(userId);
                post(checkoutsDocUri + "/" + d.getSignature(), checkoutDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
            });
              
        // GET ALL checkout records
    
        this.testGetAllResources(checkoutsUri);
        
        // cleanup
        bookDtosList.forEach(d -> {
            delete(checkoutsDocUri + "/" + d.getSignature(), RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
            delete(booksUri + "/" + d.getIsbn(), RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        });
        delete(usersUri + "/" + userId, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
    @Test(description="Given an existing checkout record, when a creation operation has the same signature, then 409 Conflict is received")
    public void test_restApiCheckoutsPost_01() {
               
        // Step 0: Create the preconditions: a book and a user records must exist:
        final String isbn = randomNumeric(10);
        final String signature = randomAlphanumeric(8);
        final String userId = randomNumeric(8);
        final BookDto bookDto = (BookDto) createDto(BookDto.class, isbn, signature);
        final UserDto userDto = (UserDto) createDto(UserDto.class, userId);
        final CheckoutDto checkoutDto = (CheckoutDto) createDto(CheckoutDto.class, signature);
        checkoutDto.setUserId(userId);
        post(booksUri + "/" + isbn, bookDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
        post(usersUri + "/" + userId, userDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        // Step 1: send a POST to checkout a document. 201 Created expected
        post(checkoutsDocUri + "/" + signature, checkoutDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        // Step 2: send a 2nd POST to checkout the same document by another user
        final String userId2 = randomNumeric(8);
        final UserDto userDto2 = (UserDto) createDto(UserDto.class, userId2);
        post(usersUri + "/" + userId2, userDto2, RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        final CheckoutDto checkoutDto2 = (CheckoutDto) createDto(CheckoutDto.class, signature);
        checkoutDto2.setUserId(userId2);
        post(checkoutsDocUri + "/" + signature, checkoutDto2, RestApiHttpStatusCodes.CLIENT_ERR_CONFLICT);

        
    }
    
}
