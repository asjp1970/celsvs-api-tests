package com.softuarium.celsvs.apitests.utils.mongodb;

import lombok.Data;

@Data
public class BookAdditionalInfo {
       
    private final Publisher publisher;
        
    private final String collection;
        
    private final Synopsis synopsis;
        
    private final String originalTile;
        
    private final String translator;
        
       
}
