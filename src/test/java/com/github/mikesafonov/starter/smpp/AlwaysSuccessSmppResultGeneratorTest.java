package com.github.mikesafonov.starter.smpp;

import com.github.mikesafonov.starter.clients.AlwaysSuccessSmppResultGenerator;
import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;
import com.github.mikesafonov.starter.smpp.dto.MessageType;
import org.junit.jupiter.api.Test;

import static com.github.mikesafonov.starter.smpp.util.Randomizer.randomString;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Mike Safonov
 */
class AlwaysSuccessSmppResultGeneratorTest {
    private AlwaysSuccessSmppResultGenerator generator = new AlwaysSuccessSmppResultGenerator();

    @Test
    void shouldGenerateSuccessResponse(){

        Message message = new Message(randomString(), randomString(), randomString(), randomString(), MessageType.SIMPLE);
        String smscId = randomString();
        MessageResponse messageResponse = generator.generate(smscId, message);

        assertEquals(message, messageResponse.getOriginal());
        assertTrue(messageResponse.isSended());
        assertEquals(smscId, messageResponse.getSmscId());
        assertNotNull(messageResponse.getSmscMessageID());
        assertNull(messageResponse.getMessageErrorInformation());
    }
}
