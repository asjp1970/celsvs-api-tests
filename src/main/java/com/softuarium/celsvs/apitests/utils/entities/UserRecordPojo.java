package com.softuarium.celsvs.apitests.utils.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRecordPojo {

    private String userId;
    
    private String secondaryId;
   
    private String firstName;
    
    private String lastName;
    
    private ContactInfo contactInformation;    
    
    private String password;
    

}
