package com.github.mikesafonov.starter.smpp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Response of message
 *
 * @author Mike Safonov
 */
@Data
@AllArgsConstructor
public class MessageResponse {
    /**
     * Id of smsc. May be null if {@link #sended} false or if {@link #original} is datagram message
     */
    private String smscMessageID;

    /**
     * Is message sent successfully
     */
    private boolean sended;
    /**
     * Original message
     */
    @NotNull
    private Message original;

    /**
     * Error code and message. May be null if response success
     */
    private MessageErrorInformation messageErrorInformation;

    public static MessageResponse success(@NotNull Message original, String smscMessageID) {
        return new MessageResponse(smscMessageID, true, original, null);
    }

    public static MessageResponse error(@NotNull Message original, @NotNull MessageErrorInformation messageErrorInformation) {
        return new MessageResponse(null, false, original, messageErrorInformation);
    }
}
