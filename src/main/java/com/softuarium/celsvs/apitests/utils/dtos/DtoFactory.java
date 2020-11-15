package com.softuarium.celsvs.apitests.utils.dtos;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DtoFactory {

    private DtoFactory() {
    }
    
    public static <T extends ITestDto> ITestDto createDto(Class<T> clazz) {
        
        ITestDto dto = null;
        
        if (clazz.isAssignableFrom(CheckoutDto.class)) {
            return createDto(CheckoutDto.class,
                        randomAlphanumeric(10), // signature
                        randomAlphabetic(10));  // userId
        }
        else if (clazz.isAssignableFrom(BookDto.class)) {                    
            return createDto(BookDto.class,
                        randomNumeric(13),          // isbn
                        randomAlphanumeric(10));    // signature
        }
        else if(clazz.isAssignableFrom(UserDto.class)) {
            return createDto(UserDto.class,
                    randomAlphabetic(8),        // first name
                    randomAlphabetic(6));    // last name
        }
        return dto; // null
    }
    
    public static <T extends ITestDto> ITestDto createDto(Class<T> clazz, final String id) {
        
ITestDto dto = null;
        
        if (clazz.isAssignableFrom(CheckoutDto.class)) {
            return createDto(CheckoutDto.class, id, // signature
                        randomAlphabetic(10));      // userId
        }
        else if (clazz.isAssignableFrom(BookDto.class)) {                    
            return createDto(BookDto.class, id,     // isbn
                        randomAlphanumeric(10));    // signature
        }
        else if(clazz.isAssignableFrom(UserDto.class)) {
            return createDto(UserDto.class, id,    // first name
                    randomAlphabetic(6));           // last name
        }
        return dto; // null
    }
    
    public static <T extends ITestDto> ITestDto createDto(Class<T> clazz, final String id, final String secondId) {
        
        ITestDto dto = null;
        
        if (clazz.isAssignableFrom(CheckoutDto.class)) {
            
            return new CheckoutDto(
                    id,
                    randomAlphabetic(10),
                    Instant.now().toString(),                           // from date
                    Instant.now().plus(Duration.ofDays(15)).toString(),
                    3
                    );
        }
        else if (clazz.isAssignableFrom(BookDto.class)) {
            return new BookDto(
                    id,                             // isbn
                    secondId,                       // signature
                    randomAlphabetic(20),           // title
                    Arrays.asList(randomAlphabetic(15), randomAlphabetic(15)), // authors
                    randomAlphabetic(50),           // subtitle
                    3,                              // num copies
                    true,
                    new BookAdditionalInfo(
                            new Publisher(
                                    randomAlphabetic(10),   // publisher's name
                                    randomAlphabetic(10),   // publisher's group
                                    new ContactInfo(
                                            new Address(
                                                    randomAlphabetic(20),   // street
                                                    57,                     // num
                                                    randomAlphabetic(20),   // additional info
                                                    randomAlphabetic(20),   // city
                                                    randomNumeric(5)),      // zip code
                                            randomAlphabetic(20),
                                            Arrays.asList(randomNumeric(12), randomNumeric(12))),
                                    randomAlphabetic(20)),  // translator
                                    randomAlphabetic(15),   // collection
                                    new Synopsis(
                                            478,
                                            randomAlphabetic(20),   // genre
                                            randomAlphabetic(20),   // subgenre
                                            randomAlphabetic(400)), // synopsis
                                    randomAlphabetic(20),   // original title
                                    randomAlphabetic(15)    // translator
                            )
                    );
        }
        else if(clazz.isAssignableFrom(UserDto.class)) {
            return new UserDto(id, secondId,
                    randomAlphabetic(8),        // first name
                    randomAlphabetic(6),        // last name
                    new ContactInfo(
                            new Address(
                                    randomAlphabetic(20),   // street
                                    57,                     // num
                                    randomAlphabetic(20),   // additional info
                                    randomAlphabetic(20),   // city
                                    randomNumeric(5)),      // zip code
                            randomAlphabetic(20),
                            Arrays.asList(randomNumeric(12), randomNumeric(12))),
                    randomAlphanumeric(10));
        }
        return dto;
    }
    
    public static <T extends ITestDto> List<T> createManyDtos (Class<T> clazz, final int qty){
        
        List<T> list = new ArrayList<T>();
        for (int i=0; i<qty; i++) {
            list.add((T) createDto(clazz));
        }
        return list;
    }

}
