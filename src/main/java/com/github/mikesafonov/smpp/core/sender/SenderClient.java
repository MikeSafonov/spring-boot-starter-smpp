package com.github.mikesafonov.smpp.core.sender;

import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.CancelMessageResponse;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageResponse;
import com.github.mikesafonov.smpp.core.exceptions.SenderClientBindException;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * This interface represents smpp protocol transmitter client
 *
 * @author Mike Safonov
 * @author Mikhail Epatko
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

    /**
     * Get Connection Manager
     *
     * @return {@link ConnectionManager}
     */
    @NotNull Optional<ConnectionManager> getConnectionManager();
}
