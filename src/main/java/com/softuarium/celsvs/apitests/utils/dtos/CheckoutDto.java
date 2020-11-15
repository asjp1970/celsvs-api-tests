package com.softuarium.celsvs.apitests.utils.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutDto implements ITestDto {
    
    private String signature;
    
    private String userId;
        
    private String fromDate;
    
    private String expiringDate;
    
    private int nrRenewals = 0;

}
