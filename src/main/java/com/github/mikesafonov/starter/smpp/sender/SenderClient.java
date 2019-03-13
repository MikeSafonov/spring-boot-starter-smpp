package com.github.mikesafonov.starter.smpp.sender;

import com.github.mikesafonov.starter.smpp.dto.CancelMessage;
import com.github.mikesafonov.starter.smpp.dto.CancelMessageResponse;
import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;
import com.github.mikesafonov.starter.smpp.sender.exceptions.SenderClientBindException;

import javax.validation.constraints.NotNull;

/**
 * This interface represents smpp protocol transmitter client
 *
 * @author Mike Safonov
 */
public interface SenderClient {

    /**
     * @return Identifier of response client
     */
    @NotNull String getId();

    /**
     * Connect to SMSC via SMPP protocol
     *
     * @throws SenderClientBindException if connection fails
     */
    void setup();

    /**
     * Send message via smpp protocol
     *
     * @param message sms
     * @return message response
     */
    @NotNull MessageResponse send(@NotNull Message message);

    /**
     * Cancel sms message
     *
     * @param cancelMessage message to cancel
     * @return cancel response
     */
    @NotNull CancelMessageResponse cancel(@NotNull CancelMessage cancelMessage);
}
