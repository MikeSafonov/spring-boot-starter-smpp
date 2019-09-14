package com.github.mikesafonov.smpp.core.exceptions;

public class SmppMessageBuildingException extends SmppException {
    public SmppMessageBuildingException() {
        super(101, "Invalid param");
    }
}
