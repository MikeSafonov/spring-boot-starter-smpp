package com.github.mikesafonov.starter.smpp.sender.exceptions;

/**
 * @author MikeSafonov
 */
public class IllegalAddressException extends RuntimeException {

    public IllegalAddressException(String message) {
        super(message);
    }
}
