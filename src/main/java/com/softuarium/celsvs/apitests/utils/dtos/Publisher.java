package com.softuarium.celsvs.apitests.utils.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Publisher {
    
    private String name;
    
    private String group;
    
    private ContactInfo contact;
    
    private String web;
}
