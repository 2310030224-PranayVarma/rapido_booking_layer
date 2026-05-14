package com.rapido.booking.exception;

public class NoAvailableDriverException extends RuntimeException {

    public NoAvailableDriverException(String message) {
        super(message);
    }
}
