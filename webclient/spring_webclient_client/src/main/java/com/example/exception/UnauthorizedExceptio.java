package com.example.exception;

public class UnauthorizedExceptio extends RuntimeException{
    public UnauthorizedExceptio(String notAtuhorised) {
        super(notAtuhorised);
    }
}
