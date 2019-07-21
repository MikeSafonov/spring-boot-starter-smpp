package com.github.mikesafonov.smpp.core.dto;

import lombok.Value;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Response of cancel message
 *
 * @author Mike Safonov
 */
@Value
public class CancelMessageResponse {
    /**
     * Original cancel message
     */
    @NotNull
    private CancelMessage original;
    /**
     * Id of smsc connection
     */
    @NotBlank
    private String smscConnectionId;
    /**
     * Is cancel success
     */
    private boolean success;
    /**
     * Error information
     */
    @Nullable
    private MessageErrorInformation messageErrorInformation;


    public static CancelMessageResponse success(@NotNull CancelMessage original, @NotBlank String smscConnectionId) {
        return new CancelMessageResponse(original, smscConnectionId, true, null);
    }

    public static CancelMessageResponse error(@NotNull CancelMessage original, @NotBlank String smscConnectionId, @NotNull MessageErrorInformation messageErrorInformation) {
        return new CancelMessageResponse(original, smscConnectionId, false, messageErrorInformation);
    }

}
