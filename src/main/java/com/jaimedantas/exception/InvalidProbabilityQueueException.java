package com.jaimedantas.exception;

import io.micronaut.http.HttpStatus;

public class InvalidProbabilityQueueException extends Exception{

    private static final long serialVersionUID = -7704201287461973374L;

    private final String message;

    public InvalidProbabilityQueueException(){
        this.message = "Error when calculating the probability of queue";
    }

    public InvalidProbabilityQueueException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
