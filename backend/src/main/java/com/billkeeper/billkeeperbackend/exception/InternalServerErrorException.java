package com.billkeeper.billkeeperbackend.exception;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) { super(message); }
}