package com.github.mikesafonov.smpp.core.sender;

import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.dto.*;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.smpp.util.Randomizer.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Mike Safonov
 */
class DefaultSenderClientTest {

    @Test
    void shouldThrowNPE() {
        DefaultTypeOfAddressParser defaultTypeOfAddressParser = new DefaultTypeOfAddressParser();
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        assertThrows(NullPointerException.class,
                () -> new DefaultSenderClient(null, randomInt(), randomBoolean(), randomInt(), defaultTypeOfAddressParser));
        assertThrows(NullPointerException.class,
                () -> new DefaultSenderClient(transmitterConfiguration, randomInt(), randomBoolean(), randomInt(), null));
    }

    @Test
    void shouldContainExpectedId() {
        DefaultTypeOfAddressParser defaultTypeOfAddressParser = new DefaultTypeOfAddressParser();
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        SenderClient senderClient = new DefaultSenderClient(transmitterConfiguration, randomInt(), randomBoolean(), randomInt(), defaultTypeOfAddressParser);
        assertEquals(transmitterConfiguration.getName(), senderClient.getId());
    }

    @Test
    void shouldReturnErrorBecauseMessageIsEmpty(){
        DefaultTypeOfAddressParser defaultTypeOfAddressParser = new DefaultTypeOfAddressParser();
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        SenderClient senderClient = new DefaultSenderClient(transmitterConfiguration, randomInt(), randomBoolean(), randomInt(), defaultTypeOfAddressParser);
        Message originalMessage = new Message(null, randomString(), randomString(), randomString(), MessageType.SIMPLE);
        MessageErrorInformation messageErrorInformation = new MessageErrorInformation(0, "Empty message text");

        MessageResponse messageResponse = senderClient.send(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscId());
        assertNull(messageResponse.getSmscMessageID());
        assertFalse(messageResponse.isSent());
        assertEquals(messageErrorInformation, messageResponse.getMessageErrorInformation());
    }

    @Test
    void shouldReturnErrorBecauseMessageIdIsEmpty(){
        DefaultTypeOfAddressParser defaultTypeOfAddressParser = new DefaultTypeOfAddressParser();
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        SenderClient senderClient = new DefaultSenderClient(transmitterConfiguration, randomInt(), randomBoolean(), randomInt(), defaultTypeOfAddressParser);
        CancelMessage originalMessage = new CancelMessage(null, randomString(), randomString());
        MessageErrorInformation messageErrorInformation = new MessageErrorInformation(0, "Empty message id");

        CancelMessageResponse messageResponse = senderClient.cancel(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscId());
        assertFalse(messageResponse.isSuccess());
        assertEquals(messageErrorInformation, messageResponse.getMessageErrorInformation());
    }


    private TransmitterConfiguration randomTransmitterConfiguration(){
        SmppProperties.Credentials credentials = new SmppProperties.Credentials();
        credentials.setHost(randomIp());
        credentials.setPort(randomPort());
        credentials.setUsername(randomString());
        credentials.setPassword(randomString());

        return new TransmitterConfiguration(randomString(), credentials, randomBoolean(), randomBoolean(), randomInt());
    }
}
