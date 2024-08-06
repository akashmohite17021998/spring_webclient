package com.example.exception;

public class MyCustomExceptionServerIssue extends RuntimeException{
    public MyCustomExceptionServerIssue(String s) {
        super(s);
    }
}
