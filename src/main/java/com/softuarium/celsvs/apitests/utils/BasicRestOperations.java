package com.softuarium.celsvs.apitests.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.softuarium.celsvs.apitests.utils.dtos.ITestDto;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class BasicRestOperations {

    private BasicRestOperations() {
    }

    public static void get(final String uri, final int expectedStatusCode) {
        
        RestAssured.given().accept("application/*+json").get(uri).then().statusCode(expectedStatusCode);
                
    }
    
    public static <D extends ITestDto> Response post (final String uri, D dto, final int expectedStatusCode) {
        
        // Post the resource
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(dto)
            .post(uri);
        
        // Check http response status code
        assertThat(resp.getStatusCode(), equalTo(expectedStatusCode));
        
        return resp;
    }
    
    public static <D extends ITestDto> Response put (final String uri, D dto, final int expectedStatusCode) {
        
        // Put a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(dto)
            .put(uri);
        
        // Check http response status code
        assertThat(resp.getStatusCode(), equalTo(expectedStatusCode));
        
        return resp;
    }
    
    public static void delete (final String uri, final int expectedStatusCode) {
        
        RestAssured.given().accept(ContentType.JSON).delete(uri).then().statusCode(expectedStatusCode); 
    }
    
    public static <D extends ITestDto> Response post (final String uri, D dto, Class<D> clazzEntity) {
        
        // Post a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(dto)
            .post(uri);
        
        // Check 201 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_CREATED));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(dto));
        
        return resp;
    }
    
    public static <D extends ITestDto> void put (final String uri, D dto, Class<D> clazzEntity) {
        
        // Put a book
        Response resp = RestAssured
            .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(dto)
            .put(uri);
        
        // Check 200 - Created
        assertThat(resp.getStatusCode(), equalTo(RestApiHttpStatusCodes.SUCCESS_OK));
        
        // Retrieve resource and check is the same
        resp = RestAssured.given().accept("application/hal+json").get(uri);
        assertThat(resp.getBody().as(clazzEntity), equalTo(dto));
        
    }
    
}

