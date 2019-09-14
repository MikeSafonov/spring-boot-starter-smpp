package com.github.mikesafonov.smpp.core.exceptions;



/**
 * @author Mike Safonov
 */
public class SmppSessionException extends SmppException {

    private static final int SESSION_ERROR = 100;
    private static final String SESSION_ERROR_MESSAGE = "Unable to bind session";


    public SmppSessionException(){

        super(SESSION_ERROR, SESSION_ERROR_MESSAGE);

    }

}
