package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.CancelMessageResponse;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageResponse;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link SenderClient}, build on top of {@link DefaultSmppClient} and
 * {@link SmppSession}
 *
 * @author Mike Safonov
 * @author Mikhail Epatko
 */
@Slf4j
public class TestSenderClient extends StandardSenderClient {

    private final Set<String> allowedPhones;
    private final SmppResultGenerator smppResultGenerator;

    public TestSenderClient(@NotNull ConnectionManager connectionManager,
                            boolean ucs2Only, long timeoutMillis,
                            @NotNull MessageBuilder messageBuilder,
                            @NotNull Set<String> allowedPhones,
                            @NotNull SmppResultGenerator smppResultGenerator) {
        super(connectionManager, ucs2Only, timeoutMillis, messageBuilder);
        this.allowedPhones = requireNonNull(allowedPhones);
        this.smppResultGenerator = requireNonNull(smppResultGenerator);
    }

    /**
     * Sending message via smpp protocol or generate result
     *
     * @param message incoming message
     * @return message response
     */
    @NotNull
    @Override
    public MessageResponse send(Message message) {
        if (isAllowed(message.getMsisdn())) {
            return super.send(message);
        }
        return smppResultGenerator.generate(getId(), message);
    }

    /**
     * Cancel smsc message via smpp protocol or generate result
     *
     * @param cancelMessage message to cancel
     * @return cancel response
     */
    @Override
    public @NotNull CancelMessageResponse cancel(@NotNull CancelMessage cancelMessage) {
        if (isAllowed(cancelMessage.getMsisdn())) {
            return super.cancel(cancelMessage);
        }
        return smppResultGenerator.generate(getId(), cancelMessage);
    }

    private boolean isAllowed(String phone) {
        return allowedPhones.contains(phone);
    }

    public Set<String> getAllowedPhones() {
        return allowedPhones;
    }
}
