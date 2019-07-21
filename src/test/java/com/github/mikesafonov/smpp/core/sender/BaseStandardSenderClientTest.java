package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.smpp.impl.DefaultSmppClient;
import org.junit.jupiter.api.BeforeEach;

import static com.github.mikesafonov.smpp.util.Randomizer.*;
import static org.mockito.Mockito.mock;

/**
 * @author Mike Safonov
 */
abstract class BaseStandardSenderClientTest {
    protected StandardSenderClient senderClient;
    protected TransmitterConfiguration transmitterConfiguration;
    protected DefaultSmppClient smppClient;
    protected MessageBuilder messageBuilder;

    @BeforeEach
    void setUp() {
        transmitterConfiguration = randomTransmitterConfiguration();
        smppClient = mock(DefaultSmppClient.class);
        messageBuilder = mock(MessageBuilder.class);
        senderClient = new StandardSenderClient(transmitterConfiguration, smppClient, randomInt(), randomBoolean(), randomInt(), messageBuilder);
    }
}
