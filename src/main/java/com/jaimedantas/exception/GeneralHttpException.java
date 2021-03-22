package com.jaimedantas.exception;

import io.micronaut.http.HttpStatus;

public class GeneralHttpException extends Exception{

    private static final long serialVersionUID = -7704201287461973374L;

    private final String message;
    private final HttpStatus httpStatus;

    public GeneralHttpException(){
        this.message = "Internal error";
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public GeneralHttpException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
