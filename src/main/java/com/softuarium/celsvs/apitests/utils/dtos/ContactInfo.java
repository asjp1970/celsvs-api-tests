package com.softuarium.celsvs.apitests.utils.dtos;

import java.util.List;

import lombok.Data;

@Data
public class ContactInfo {

    private final Address address;
    
    private final String email;
    
    private final List<String> phoneNrs;

}
