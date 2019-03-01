package com.github.mikesafonov.starter.smpp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author MikeSafonov
 */
@Data
@AllArgsConstructor
public class MessageErrorInformation {
    private int errorCode;
    private String errorMessage;
}
