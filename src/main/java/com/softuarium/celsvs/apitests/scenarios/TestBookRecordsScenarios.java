package com.softuarium.celsvs.apitests.scenarios;

import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createDto;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.TestParent;
import com.softuarium.celsvs.apitests.utils.dtos.BookDto;
import com.softuarium.celsvs.apitests.utils.dtos.CkeckoutDto;
import com.softuarium.celsvs.apitests.utils.dtos.UserDto;

@Test(groups = { "functional", "scenarios" })
public class TestBookRecordsScenarios extends TestParent {
    
    private String checkoutsUri;
    private String checkoutsDocUri;
    private String booksUri;
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
    public void test_checkoutScenario_01() {
        
        final String isbn = randomNumeric(13);
        final String signature = randomAlphanumeric(10);
        final String userId = randomAlphanumeric(8);
        CkeckoutDto checkoutDto = (CkeckoutDto) createDto(CkeckoutDto.class, signature);
        checkoutDto.setUserId(userId);
       
    }
    
    /**
     * Scenario 1
     * Given inexistent book and publisher records with a publisher that already exists in the database, then 201 Created
     * and the publisher's name in the book record is 
     */
}


