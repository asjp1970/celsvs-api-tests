package com.softuarium.celsvs.apitests.utils.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto implements ITestDto {

    private String userId;
    
    private String secondaryId;
   
    private String firstName;
    
    private String lastName;
    
    private ContactInfo contactInformation;    
    
    private String password;
    

}
