package com.github.mikesafonov.smpp.core.sender.exceptions;

public class SmppMessageBuildingException extends SmppException {
    public SmppMessageBuildingException() {
        super(101, "Invalid param");
    }
}
