package com.github.mikesafonov.starter.smpp;

import com.github.mikesafonov.starter.clients.AlwaysSuccessSmppResultGenerator;
import com.github.mikesafonov.starter.clients.SmppResultGenerator;
import com.github.mikesafonov.starter.clients.TestSenderClient;
import com.github.mikesafonov.starter.smpp.dto.CancelMessage;
import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageType;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mikesafonov.starter.smpp.util.Randomizer.randomString;
import static java.util.Collections.emptyList;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class TestSenderClientTest {

    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> new TestSenderClient(null, emptyList(), new AlwaysSuccessSmppResultGenerator()));
        assertThrows(NullPointerException.class, () -> new TestSenderClient(mock(SenderClient.class), emptyList(), null));
    }

    @Test
    void shouldContainExpectedId() {
        SenderClient senderClient = mock(SenderClient.class);
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        TestSenderClient testSenderClient = new TestSenderClient(senderClient, emptyList(), smppResultGenerator);

        String id = randomString();
        when(senderClient.getId()).thenReturn(id);

        assertEquals(id, testSenderClient.getId());
    }

    @Test
    void shouldGenerateResponse() {
        SenderClient senderClient = mock(SenderClient.class);
        List<String> allowedPhones = asList(randomString());
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        TestSenderClient testSenderClient = new TestSenderClient(senderClient, allowedPhones, smppResultGenerator);

        String id = randomString();
        when(senderClient.getId()).thenReturn(id);

        Message message = new Message(randomString(), randomString(), randomString(), randomString(), MessageType.SIMPLE);
        testSenderClient.send(message);

        verify(smppResultGenerator, times(1)).generate(id, message);
        verify(senderClient, times(0)).send(message);
    }

    @Test
    void shouldCallSenderClient() {
        SenderClient senderClient = mock(SenderClient.class);
        String destinationPhone = randomString();
        List<String> allowedPhones = asList(destinationPhone);
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        TestSenderClient testSenderClient = new TestSenderClient(senderClient, allowedPhones, smppResultGenerator);

        String id = randomString();
        when(senderClient.getId()).thenReturn(id);

        Message message = new Message(randomString(), destinationPhone, randomString(), randomString(), MessageType.SIMPLE);
        testSenderClient.send(message);

        verify(smppResultGenerator, times(0)).generate(id, message);
        verify(senderClient, times(1)).send(message);
    }

    @Test
    void shouldGenerateResponseForCancel() {
        SenderClient senderClient = mock(SenderClient.class);
        List<String> allowedPhones = asList(randomString());
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        TestSenderClient testSenderClient = new TestSenderClient(senderClient, allowedPhones, smppResultGenerator);

        String id = randomString();
        when(senderClient.getId()).thenReturn(id);

        CancelMessage message = new CancelMessage(randomString(),  randomString(), randomString());
        testSenderClient.cancel(message);

        verify(smppResultGenerator, times(1)).generate(id, message);
        verify(senderClient, times(0)).cancel(message);
    }

    @Test
    void shouldCallSenderClientForCancel() {
        SenderClient senderClient = mock(SenderClient.class);
        String destinationPhone = randomString();
        List<String> allowedPhones = asList(destinationPhone);
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        TestSenderClient testSenderClient = new TestSenderClient(senderClient, allowedPhones, smppResultGenerator);

        String id = randomString();
        when(senderClient.getId()).thenReturn(id);

        CancelMessage message = new CancelMessage(randomString(),  randomString(), destinationPhone);
        testSenderClient.cancel(message);

        verify(smppResultGenerator, times(0)).generate(id, message);
        verify(senderClient, times(1)).cancel(message);
    }
}
