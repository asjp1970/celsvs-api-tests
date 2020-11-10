package com.softuarium.celsvs.apitests.utils.mongodb;

import java.util.List;

import lombok.Data;

@Data
public class BookRecordPojo {
    
    private final String isbn;
    
    private final String signature;
    
    private final String title;
    
    private final List<String> authors;
    
    private final String subtitle;
    
    private final int numOfCopies;
    
    private final boolean available;
    
    private final Library location;
        
    private final BookAdditionalInfo detailedInfo;
}
