package com.softuarium.celsvs.apitests.utils;

public class RestApiHttpStatusCodes {
    
    // 2XX - success
    
    public static int SUCCESS_OK = 200;
    public static int SUCCESS_CREATED = 201;
    public static int SUCCESS_NO_CONTENT = 204;
    
    
    // 4XX - client error
    public static int CLIENT_ERR_BAD_REQ = 400;
    public static int CLIENT_ERR_FORBIDDEN = 403;
    public static int CLIENT_ERR_NOT_FOUND = 404;
    public static int CLIENT_ERR_NOT_ACCEPTABLE = 406;
    public static int CLIENT_ERR_CONFLICT = 409;

    private RestApiHttpStatusCodes() {
    }

}
