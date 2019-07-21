package com.github.mikesafonov.smpp.core.dto;

import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
public class MessageErrorInformation {
    private final int errorCode;
    private final String errorMessage;
}
