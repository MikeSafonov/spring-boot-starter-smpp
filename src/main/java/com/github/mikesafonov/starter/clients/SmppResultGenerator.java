package com.github.mikesafonov.starter.clients;


import com.github.mikesafonov.starter.smpp.dto.CancelMessage;
import com.github.mikesafonov.starter.smpp.dto.CancelMessageResponse;
import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Implementations of this interface is used by mock {@link MockSenderClient} or test {@link TestSenderClient}
 * clients to generate request answer {@link MessageResponse} and {@link CancelMessageResponse}
 *
 * @author Mike Safonov
 */
public interface SmppResultGenerator {

    @NotNull MessageResponse generate(@NotBlank String smscId, @NotNull Message message);

    @NotNull CancelMessageResponse generate(@NotNull String smscId, @NotNull CancelMessage cancelMessage);
}
