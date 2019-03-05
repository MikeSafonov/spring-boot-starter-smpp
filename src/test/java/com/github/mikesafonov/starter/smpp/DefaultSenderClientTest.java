package com.github.mikesafonov.starter.smpp;

import com.github.mikesafonov.starter.SmppProperties;
import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageErrorInformation;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;
import com.github.mikesafonov.starter.smpp.dto.MessageType;
import com.github.mikesafonov.starter.smpp.sender.DefaultSenderClient;
import com.github.mikesafonov.starter.smpp.sender.DefaultTypeOfAddressParser;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import com.github.mikesafonov.starter.smpp.sender.TransmitterConfiguration;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.starter.smpp.util.Randomizer.*;
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
                () -> DefaultSenderClient.of(null, randomInt(), randomBoolean(), randomInt(), defaultTypeOfAddressParser));
        assertThrows(NullPointerException.class,
                () -> DefaultSenderClient.of(transmitterConfiguration, randomInt(), randomBoolean(), randomInt(), null));
    }

    @Test
    void shouldContainExpectedId() {
        DefaultTypeOfAddressParser defaultTypeOfAddressParser = new DefaultTypeOfAddressParser();
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        SenderClient senderClient = DefaultSenderClient.of(transmitterConfiguration, randomInt(), randomBoolean(), randomInt(), defaultTypeOfAddressParser);
        assertEquals(transmitterConfiguration.getName(), senderClient.getId());
    }

    @Test
    void shouldReturnErrorBecauseMessageIsEmpty(){
        DefaultTypeOfAddressParser defaultTypeOfAddressParser = new DefaultTypeOfAddressParser();
        TransmitterConfiguration transmitterConfiguration = randomTransmitterConfiguration();

        SenderClient senderClient = DefaultSenderClient.of(transmitterConfiguration, randomInt(), randomBoolean(), randomInt(), defaultTypeOfAddressParser);
        Message originalMessage = new Message(null, randomString(), randomString(), randomString(), MessageType.SIMPLE);
        MessageErrorInformation messageErrorInformation = new MessageErrorInformation(0, "Empty message text");

        MessageResponse messageResponse = senderClient.send(originalMessage);

        assertEquals(originalMessage, messageResponse.getOriginal());
        assertEquals(senderClient.getId(), messageResponse.getSmscId());
        assertNull(messageResponse.getSmscMessageID());
        assertFalse(messageResponse.isSended());
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
