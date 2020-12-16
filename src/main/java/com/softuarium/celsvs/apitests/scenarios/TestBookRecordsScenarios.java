package com.softuarium.celsvs.apitests.scenarios;

import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createDto;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.dtos.BookAdditionalInfo;
import com.softuarium.celsvs.apitests.utils.dtos.BookDto;
import com.softuarium.celsvs.apitests.utils.dtos.CheckoutDto;
import com.softuarium.celsvs.apitests.utils.dtos.UserDto;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.get;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.post;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.delete;

@Test(groups = { "functional", "scenarios" })
public class TestBookRecordsScenarios {
    
    private String checkoutsUri;
    private String checkoutsDocUri;
    private String booksUri;
    private String publishersUri;
    private String usersUri;

    public TestBookRecordsScenarios() {
        // TODO Auto-generated constructor stub
    }
    
    @Parameters({ "celsvsBaseUri", "checkoutsUri", "booksUri", "usersUri", "publishersUri" })
    @BeforeClass
    public void beforeClass(final String celsvsUri, final String checkoutsUriFragment,
            final String booksUriFragment, final String usersUriFragment, final String publishersUriFragment) {
        
        this.checkoutsUri = celsvsUri + "/" + checkoutsUriFragment;
        this.checkoutsDocUri = checkoutsUri + "/document";
        this.booksUri = celsvsUri+"/"+booksUriFragment;
        this.publishersUri = celsvsUri+"/"+publishersUriFragment;
        this.usersUri = celsvsUri+"/"+usersUriFragment;
        
    }
    

    /**
     * Scenario 1
     * Given inexistent book and publisher records , when the book is created, then 201 Created is returned,
     * the book stored is the same and the Publisher's name is normalized:
     *  - If the name has 1 word, then the first one with be capital letter and the rest small letters
     *  - If the name has several words, the fist letter of each word will be transformed in capital letter
     *    and the rest small letters
     */
    @Test(description="Given new book record with publisher in additional info, the name of the publisher is stored normalized")
    public void test_BookScenario_01() {
        
        final String isbn = randomNumeric(13);
        BookDto bookDto = (BookDto) createDto(BookDto.class, isbn);
        
        BookAdditionalInfo detailedInfo = bookDto.getDetailedInfo();
        String publisherName = detailedInfo.getPublisher();
        detailedInfo.setPublisher("editorial Crítica");
        
        post(this.booksUri + "/" + isbn, bookDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        get(this.publishersUri + "/" + "editorial Crítica", RestApiHttpStatusCodes.SUCCESS_OK);
        
        
        // cleanup resources
        delete(this.booksUri + "/" + isbn, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        delete(this.publishersUri + "/" + publisherName, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
       
    }
    
    /**
     * Scenario 2
     * The deletion of a document record is not possible if there are checkouts for it.
     * Steps:
     *  - Create a book record
     *  - POST a checkout for that book record
     *  - DELETE the book record. The operation should fail because a user checked the book out
     *  - DELETE the checkout record (the book is returned to the library)
     *  - DELETE the book record. The server returns 204 No content 
     */
    @Test(description="Given a book record with a checkout, the delete operation fails with 409 - Conflict")
    public void test_BookScenario_02() {
        
        final String isbn = randomNumeric(13);
        final String userId = randomAlphanumeric(8);
        
        // given - a book record and a a checkout for that book...
        BookDto bookDto = (BookDto) createDto(BookDto.class, isbn);
        final String signature = bookDto.getSignature();
        UserDto userDto = (UserDto) createDto(UserDto.class, userId);
        CheckoutDto checkoutDto = (CheckoutDto) createDto(CheckoutDto.class, signature);
        checkoutDto.setUserId(userDto.getUserId());
        post(this.booksUri + "/" + isbn, bookDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
        post(this.usersUri + "/" + userId, userDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
        post(this.checkoutsDocUri + "/" + signature, checkoutDto,
                RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        // when - book record deleted then - 409 Conflict is received and the book record is not deleted
        delete(this.booksUri + "/" + isbn, RestApiHttpStatusCodes.CLIENT_ERR_CONFLICT);
        get(this.booksUri + "/" + isbn, RestApiHttpStatusCodes.SUCCESS_OK);

        
        // delete the checkout record (the book is returned to the library)
        delete(this.checkoutsDocUri + "/" + signature, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
        
        // when - book record deleted then - 204 No Content is received and the book record is deleted
        delete(this.booksUri + "/" + isbn, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);

        // Cleanup:
        delete(this.usersUri + "/" + userId, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);

    }
    
}


