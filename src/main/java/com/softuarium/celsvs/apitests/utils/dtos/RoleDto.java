package com.softuarium.celsvs.apitests.utils.dtos;

import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RoleDto implements ITestDto {
    
    private final String name;
    
    private final String description;
    
    public List<String> privilegesList;
}
