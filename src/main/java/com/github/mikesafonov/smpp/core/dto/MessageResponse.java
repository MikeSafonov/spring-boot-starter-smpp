package com.github.mikesafonov.smpp.core.dto;

import lombok.Value;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Response of message
 *
 * @author Mike Safonov
 */
@Value
public class MessageResponse {
    /**
     * Original message
     */
    @NotNull
    private Message original;
    /**
     * Id of smsc connection
     */
    @NotBlank
    private String smscId;

    /**
     * Id of smsc message. May be null if {@link #sent} false or if {@link #original} is datagram message
     */
    @Nullable
    private String smscMessageID;

    /**
     * Is message sent successfully
     */
    private boolean sent;
    /**
     * Error code and message. May be null if response success
     */
    @Nullable
    private MessageErrorInformation messageErrorInformation;


    public static MessageResponse success(@NotNull Message original, @NotBlank String smscId, String smscMessageID) {
        return new MessageResponse(original, smscId, smscMessageID, true, null);
    }

    public static MessageResponse error(@NotNull Message original, @NotBlank String smscId, @NotNull MessageErrorInformation messageErrorInformation) {
        return new MessageResponse(original, smscId, null, false, messageErrorInformation);
    }
}
