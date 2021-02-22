package com.softuarium.celsvs.apitests.utils.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutDto implements ITestDto {
    
    private String signature;
    
    private String userId;
        
    private String fromDate;
    
    private String expiringDate;
    
    private int renewals = 0;

}
