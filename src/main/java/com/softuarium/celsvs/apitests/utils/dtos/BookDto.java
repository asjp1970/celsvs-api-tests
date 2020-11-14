package com.softuarium.celsvs.apitests.utils.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookDto implements ITestDto {
    
    private String isbn;
    
    private String signature;
    
    private String title;
    
    private List<String> authors;
    
    private String subtitle;
    
    private int numOfCopies;
    
    private boolean available;
            
    private BookAdditionalInfo detailedInfo;
}
