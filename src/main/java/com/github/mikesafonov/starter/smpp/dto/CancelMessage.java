package com.github.mikesafonov.starter.smpp.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;

/**
 * Message to cancel
 *
 * @author Mike Safonov
 */
@Value
public class CancelMessage {
    /**
     * Id of smsc message.
     */
    @NotBlank
    private String messageId;
    /**
     * Source name (alpha name)
     */
    @NotBlank
    private String source;
    /**
     * Destination phone number(msisdn)
     */
    @NotBlank
    private String msisdn;
}
