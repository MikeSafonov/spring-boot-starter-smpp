package com.github.mikesafonov.smpp.core.sender;

import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.CancelMessageResponse;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageResponse;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link SenderClient} which not perform any connection via smpp and only generate {@link MessageResponse}/{@link CancelMessageResponse}
 * by using {@link SmppResultGenerator}
 *
 * @author Mike Safonov
 */
@EqualsAndHashCode
public class MockSenderClient implements SenderClient {

    private final SmppResultGenerator smppResultGenerator;
    private final String id;

    public MockSenderClient(@NotNull SmppResultGenerator smppResultGenerator, @NotNull String id) {
        this.smppResultGenerator = requireNonNull(smppResultGenerator);
        this.id = requireNonNull(id);
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setup() {
        // should be empty
    }

    @Override
    public @NotNull MessageResponse send(@NotNull Message message) {
        return smppResultGenerator.generate(id, message);
    }

    @Override
    public @NotNull CancelMessageResponse cancel(@NotNull CancelMessage cancelMessage) {
        return smppResultGenerator.generate(id, cancelMessage);
    }

}
