package com.softuarium.celsvs.apitests.utils.dtos;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
public class DtoFactory {

    private DtoFactory() {
    }
    
    public static <T extends ITestDto> ITestDto createDto(Class<T> clazz) {
        
        ITestDto dto = null;
        
        if (clazz.isAssignableFrom(CkeckoutDto.class)) {
                        
            return new CkeckoutDto(
                    randomAlphanumeric(10),                             // signature
                    randomAlphabetic(10),                               // user id
                    Instant.now().toString(),                           // from date
                    Instant.now().plus(Duration.ofDays(15)).toString(), // to date
                    0   // nr of renewals
                    );
        }
        else if (clazz.isAssignableFrom(BookDto.class)) {
            return new BookDto(
                    randomNumeric(13),              // isbn
                    randomAlphanumeric(10),         // signature
                    randomAlphabetic(20),           // title
                    Arrays.asList(randomAlphabetic(15), randomAlphabetic(15)), // authors
                    randomAlphabetic(50),           // subtitle
                    3,                              // num copies
                    true,
                    /*new Library(randomAlphabetic(15),       // library name
                            new Address(
                                    randomAlphabetic(20),   // street
                                    34,                     // num
                                    randomAlphabetic(20),   // additional info
                                    randomAlphabetic(20),   // city
                                    randomNumeric(5))),*/
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
            return new UserDto(randomAlphanumeric(8), randomAlphanumeric(10),
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
    
    public static <T extends ITestDto> ITestDto createDto(Class<T> clazz, final String id) {
        
        ITestDto dto = null;
        
        if (clazz.isAssignableFrom(CkeckoutDto.class)) {
            
            return new CkeckoutDto(
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
                    randomAlphanumeric(10),         // signature
                    randomAlphabetic(20),           // title
                    Arrays.asList(randomAlphabetic(15), randomAlphabetic(15)), // authors
                    randomAlphabetic(50),           // subtitle
                    3,                              // num copies
                    true,
                    /*new Library(randomAlphabetic(15),       // library name
                            new Address(
                                    randomAlphabetic(20),   // street
                                    34,                     // num
                                    randomAlphabetic(20),   // additional info
                                    randomAlphabetic(20),   // city
                                    randomNumeric(5))),*/
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
            return new UserDto(id, randomAlphanumeric(10),
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
    
    public static <T extends ITestDto> ITestDto createDto(Class<T> clazz, final String id, final String secondId) {
        
        ITestDto dto = null;
        
        if (clazz.isAssignableFrom(CkeckoutDto.class)) {
            
            return new CkeckoutDto(
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
                    /*new Library(randomAlphabetic(15),       // library name
                            new Address(
                                    randomAlphabetic(20),   // street
                                    34,                     // num
                                    randomAlphabetic(20),   // additional info
                                    randomAlphabetic(20),   // city
                                    randomNumeric(5))),*/
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

}
