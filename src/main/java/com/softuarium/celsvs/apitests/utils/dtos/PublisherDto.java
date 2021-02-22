package com.softuarium.celsvs.apitests.utils.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublisherDto implements ITestDto {
    
    private String name;
    
    private String group;
    
    private ContactInfo contact;
    
    private String web;
  
}
