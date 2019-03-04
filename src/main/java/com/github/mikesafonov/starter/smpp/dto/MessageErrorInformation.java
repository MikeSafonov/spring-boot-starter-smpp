package com.github.mikesafonov.starter.smpp.dto;

import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
public class MessageErrorInformation {
    private int errorCode;
    private String errorMessage;
}
