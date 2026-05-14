package com.rapido.booking.exception;

public class InvalidTripStateTransitionException extends RuntimeException {

    public InvalidTripStateTransitionException(String message) {
        super(message);
    }
}
