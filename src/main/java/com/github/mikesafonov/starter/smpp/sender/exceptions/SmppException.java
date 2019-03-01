package com.github.mikesafonov.starter.smpp.sender.exceptions;

/**
 * @author Mike Safonov
 */
public class SmppException extends RuntimeException {

    protected int errorCode;
    protected String errorMessage;

    public SmppException(int errorCode, String errorMessage){

        super(errorMessage);

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
