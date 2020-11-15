package com.softuarium.celsvs.apitests;

import com.softuarium.celsvs.apitests.utils.RestApiHttpStatusCodes;
import com.softuarium.celsvs.apitests.utils.dtos.Address;
import com.softuarium.celsvs.apitests.utils.dtos.BookAdditionalInfo;
import com.softuarium.celsvs.apitests.utils.dtos.BookDto;
import com.softuarium.celsvs.apitests.utils.dtos.ContactInfo;
import com.softuarium.celsvs.apitests.utils.dtos.Publisher;
import com.softuarium.celsvs.apitests.utils.dtos.Synopsis;
import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createDto;
import static com.softuarium.celsvs.apitests.utils.dtos.DtoFactory.createManyDtos;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.post;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.put;
import static com.softuarium.celsvs.apitests.utils.BasicRestOperations.delete;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeClass;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Test(groups = { "functional", "api" })
public class RestApiBookResourceTests extends RestApiBaseTester {
        
    private String booksUri;
    
    @Parameters({ "celsvsBaseUri", "booksUri" })
    @BeforeClass
    public void beforeClass(final String celsvsUri, final String booksUriFragment) {
        
        this.booksUri = celsvsUri+"/"+booksUriFragment;
        
    }
    
    @Parameters({ "booksCollectionName", "publishersCollectionName" })
    @AfterClass
    public void afterClass(final String booksCollection, final String publishersCollectionName) {
        
        List<String> collectionNames = Arrays.asList(booksCollection, publishersCollectionName);
        super.cleanupDbCollection(collectionNames);
        
    }
    
    
    // GET
    
    @Test(description="Given an existing book record, when retrieved, then 200 OK and correct json is received")
    public void test_restApiBooksGet_01() {
        final String isbn = randomNumeric(13);
        final BookDto br = (BookDto) createDto(BookDto.class, isbn, randomAlphanumeric(10));
        
        testGetExistingEntity(this.booksUri+"/"+isbn, br);
    }
    
    @Test(description="Given a non-existing book record, when retrieved, then 404 not found is received")
    public void test_restApiBooksGet_02() {
        String isbnRandom = randomNumeric(13);
    
        this.testGetNonExistingEntity(this.booksUri+"/"+isbnRandom);
    }
    
    @Test(description="Given an existing book record, when retrieved using signature, then 200 OK and correct json is received")
    public void test_restApiBooksGet_03() {
        final String isbn = randomNumeric(13);
        final String sign = randomAlphanumeric(10);
        final BookDto br = (BookDto) createDto(BookDto.class, isbn, sign);
        
        this.testGetExistingEntity2ndKey(this.booksUri, isbn, sign, br);
    }
    
    @Test(description="Given several existing book records, when all retrieved, then 200 OK")
    public void test_restApiBooksGet_04() {
    
        this.testGetAllResources(this.booksUri);
    }
    
    @Test(groups = {"unstable"},
          description="Given several existing book records, when pages retrieved, then 200 OK and pages are OK")
    public void test_restApiBooksGet_05() {
        
        final int totalRecords = 10;
        final int numPages = 5;
        final int sizePage = 2;
        final String uriResource = this.booksUri.concat(String.format("?page=%d&size=%d&sortBy=isbn&sortOrder=asc", numPages, sizePage));
    
        List<BookDto> list = createManyDtos(BookDto.class, totalRecords);
        list.forEach(br -> {
            Response resp = RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br)
                                .post(this.booksUri+"/"+br.getIsbn());
            assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
            }
            );
        
        Response response = RestAssured.given().accept(ContentType.JSON).get(uriResource);
        
        assertThat(response.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        assertThat(response.getBody().jsonPath().getList(".", BookDto.class).size(), equalTo(sizePage));       
        
        // cleanup
        list.forEach(br -> {
            Response resp = RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON)
                    .delete(this.booksUri+"/"+br.getIsbn());
            assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT));
            }
            );
    }
    
    
    // POST
    
    @Test(description="Given a non-existing book record, when created, then 201 Created is received")
    public void test_restApiBooksPost_01() {
        
        final String isbn = randomNumeric(13);
        final BookDto br = (BookDto) createDto(BookDto.class, isbn, randomAlphanumeric(10));
        
        this.testPostNewResourceOk(this.booksUri+"/"+isbn, br, BookDto.class);
    }
    
    @Test(description="Given an existing book record, when a creation operation has the same isbn, then 409 Conflict is received")
    public void test_restApiBooksPost_02() {
        
        final String isbn = randomNumeric(13);
        final BookDto br = (BookDto) createDto(BookDto.class, isbn, randomAlphanumeric(10));
        
        this.testPostExistingResourceNok(this.booksUri+"/"+isbn, br);
    }
    
    
    //PUT
    
    @Test(description="Given a non-existing book record, when updated, then 201 Created is received")
    public void test_restApiBooksPut_01() {
        final String isbn = randomNumeric(13);
        final BookDto br = (BookDto) createDto(BookDto.class,isbn, randomAlphanumeric(10));
        
        this.testPutNewResourceOk(this.booksUri+"/"+isbn, br, BookDto.class);
    }
     
    @Test(description="Given an existing book record, when a updated, then 200 OK is received")
    public void test_restApiBooksPut_02() {
        final String isbn = randomNumeric(13);
        final BookDto br = (BookDto) createDto(BookDto.class,isbn, randomAlphanumeric(10));
        final String uriResource = this.booksUri+"/"+isbn;
        
        // post a book
        post(uriResource, br, BookDto.class);
        
        // put to update
        br.setTitle("Much ado about nothing");
        put(uriResource, br, BookDto.class);
        
        // cleanup (plain post, put methods in parent class do not delete test entity
        
        delete(uriResource, RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
     
    @Test(description="Given an existing book record, when update operation has the signature of another existing book record, then 409 Conflict is received")
    public void test_restApiBooksPut_03() {
        final String isbn = randomNumeric(13);
        final String sign = randomAlphanumeric(10);
        final BookDto br = (BookDto) createDto(BookDto.class, isbn, sign);
        final String uriResource = this.booksUri+"/"+isbn;
        
        // post a book and
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br)
            .post(uriResource);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // put to update: new book record with a clashing signature:
        final String isbn2 = randomNumeric(13);
        final BookDto br2 = (BookDto) createDto(BookDto.class, isbn2, sign);
        final String uriResource2 = this.booksUri+"/"+isbn2;
        RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON).body(br2)
            .put(uriResource2);
        // Check 
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.CLIENT_ERR_CONFLICT));
        
        // cleanup
        RestAssured.given().accept(ContentType.JSON).contentType(ContentType.JSON)
                   .delete(uriResource)
                   .then().statusCode(RestApiHttpStatusCodes.SUCCESS_NO_CONTENT);
    }
    
     
    // DELETE
    
    @Test(description="Given an existing book record, when a delete operation with isbn, then 204 No Content is received with no Body")
    public void test_restApiBooksDelete_01() {
        final String isbn = randomNumeric(13);
        final BookDto br = (BookDto) createDto(BookDto.class, isbn, randomAlphanumeric(10));
        
        this.testDeleteExistingEntityOk(this.booksUri+"/"+isbn, br);
    }
    
    @Test(description="Given an existing book record, when a delete operation with signature, then 204 No Content is received with no Body")
    public void test_restApiBooksDelete_02() {
        final String isbn = randomNumeric(13);
        final String signature = randomAlphanumeric(10);
        final BookDto br = (BookDto) createDto(BookDto.class, isbn, signature);
        
        this.testDeleteExistingResource2ndKeyOk(this.booksUri, isbn, signature, br);
        
    }
    
    @Test(description="Given a non-existing book record, when a delete operation with isbn, then 204 No Content is received with no Body")
    public void test_restApiBooksDelete_03() {
        
        this.testDeleteNonExistingResourceOk(this.booksUri+"/"+randomNumeric(13));
        
    }
    
    
    // HATEOAS
    
    @Test(groups = {"hypermedia"},
          description="Given an existing resource, when retrieved, then the expected hypermedia links are received and lead to the rigth resources")
    public void test_hypermedia_01() {
        fail("Not yet implemented");
    }
    
    @Test(groups = {"hypermedia"},
          description="Given a non-existing resource, when created, then the expected hypermedia links are received and lead to the rigth resources")
    public void test_hypermedia_02() {
        fail("Not yet implemented");
    }
    
    @Test(groups = {"hypermedia"},
          description="Given a a set of resources, when all retrieved, then the expected hypermedia links are received allowing page navigation with 'next'")
    public void test_hypermedia_03() {
        fail("Not yet implemented");
    }
    
    @Test(groups = {"hypermedia"},
          description="Given a a set of resources, when retrieved paginated, then the expected hypermedia links are received allowing page navigation with 'next'")
    public void test_hypermedia_04() {
        fail("Not yet implemented");
    }
    
    
    
    
}
