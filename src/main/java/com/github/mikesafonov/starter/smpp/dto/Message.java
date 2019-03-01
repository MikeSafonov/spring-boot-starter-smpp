package com.github.mikesafonov.starter.smpp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * Incoming message
 *
 * @author Mike Safonov
 */
@Data
@AllArgsConstructor
public class Message {

    /**
     * Message text
     */
    @NotBlank
    private String text;
    /**
     * Destination phone number(msisdn)
     */
    @NotBlank
    private String msisdn;
    /**
     * Source name (alpha name)
     */
    @NotBlank
    private String source;
    /**
     * Client specific id. May be null
     */
    private String messageId;

    /**
     * Message type
     */
    @NotNull
    private MessageType messageType;

    public boolean isSilent() {
        return messageType == MessageType.DATAGRAM;
    }

    public boolean isDatagram() {
        return messageType == MessageType.DATAGRAM;
    }

}
