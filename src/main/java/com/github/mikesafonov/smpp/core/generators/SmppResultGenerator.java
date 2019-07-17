package com.github.mikesafonov.smpp.core.generators;


import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.CancelMessageResponse;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageResponse;
import com.github.mikesafonov.smpp.core.sender.MockSenderClient;
import com.github.mikesafonov.smpp.core.sender.TestSenderClient;

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
