package com.softuarium.celsvs.apitests.utils.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookAdditionalInfo {
       
    private Publisher publisher;
        
    private final String collection;
        
    private final Synopsis synopsis;
        
    private final String originalTile;
        
    private final String translator;
        
       
}
