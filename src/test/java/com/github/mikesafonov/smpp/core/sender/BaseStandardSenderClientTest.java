package com.github.mikesafonov.smpp.core.sender;

import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import org.junit.jupiter.api.BeforeEach;

import static com.github.mikesafonov.smpp.util.Randomizer.randomBoolean;
import static com.github.mikesafonov.smpp.util.Randomizer.randomInt;
import static org.mockito.Mockito.mock;

/**
 * @author Mike Safonov
 */
abstract class BaseStandardSenderClientTest {
    protected StandardSenderClient senderClient;
    protected MessageBuilder messageBuilder;
    protected ConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        connectionManager = mock(ConnectionManager.class);
        messageBuilder = mock(MessageBuilder.class);
        senderClient = new StandardSenderClient(connectionManager, randomBoolean(), randomInt(), messageBuilder);
    }
}
