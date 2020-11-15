package com.softuarium.celsvs.apitests.scenarios;

import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createDto;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.dtos.BookDto;
import com.softuarium.celsvs.apitests.utils.dtos.Publisher;
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
     * Given inexistent book and publisher records , when the book is created, then 201 Created is returned,
     * the book stored is the same and the Publisher's name is normalized:
     *  - If the name has 1 word, then the first one with be capital letter and the rest small letters
     *  - If the name has several words, the fist letter of each word will be transformed in capital letter
     *    and the rest small letters
     */
    @Test(description="Given new book record with publisher in additional info, the name of the publisher is stored normalized")
    public void test_BookScenario_01() {
        
        final String isbn1 = randomNumeric(13);
        BookDto bookDto = (BookDto) createDto(BookDto.class, isbn1);
        
        Publisher publisher = bookDto.getDetailedInfo().getPublisher();
        publisher.setName("editorial Crítica");
        
        post(this.booksUri + "/" + isbn1, bookDto, RestApiHttpStatusCodes.SUCCESS_CREATED);
        
        get(this.publishersUri + "/" + publisher.getName(), RestApiHttpStatusCodes.SUCCESS_OK);
        
        
        // cleanup resources
        delete(this.booksUri + "/" + isbn1, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
       
    }
    
    /**
     * TODO
     * Scenario 2
     * The deletion of a document record is not possible unless there is no checkout. The test first 
     * Steps:
     *  - Create a book record
     *  - POST a checkout for that book record
     *  - DELETE the book record. The operation should fail because a user checked the book out
     *  - DELETE the checkout record (the book is returned to the library)
     *  - DELETE the book record. The server returns 204 No content 
     */
}

