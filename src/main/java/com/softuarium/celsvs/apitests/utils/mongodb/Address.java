package com.softuarium.celsvs.apitests.utils.mongodb;

import lombok.Data;

@Data
public class Address {
    private final String street;
    private final int nr;
    private final String additionalInfo;
    private final String city;
    private final String zipCode;
}
