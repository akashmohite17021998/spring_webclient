package com.example.exception;

public class MyServiceException extends RuntimeException{
    public MyServiceException(String s) {
        super(s);
    }
}
