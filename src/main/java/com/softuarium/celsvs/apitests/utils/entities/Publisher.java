package com.softuarium.celsvs.apitests.utils.entities;

import lombok.Data;

@Data
public class Publisher {
    
    private final String name;
    
    private final String group;
    
    private final ContactInfo contact;
    
    private final String web;
}
