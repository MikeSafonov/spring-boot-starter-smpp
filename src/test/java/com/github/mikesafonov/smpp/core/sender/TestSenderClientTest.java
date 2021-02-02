package com.github.mikesafonov.smpp.core.sender;

import com.github.mikesafonov.smpp.core.connection.BaseSmppSessionConfiguration;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.dto.CancelMessage;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageType;
import com.github.mikesafonov.smpp.core.generators.AlwaysSuccessSmppResultGenerator;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.github.mikesafonov.smpp.util.Randomizer.*;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 * @author Mikhail Epatko
 */
class TestSenderClientTest {

    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> new TestSenderClient(null, randomBoolean(), randomLong(),
            mock(MessageBuilder.class), emptySet(), new AlwaysSuccessSmppResultGenerator()));
        assertThrows(NullPointerException.class, () -> new TestSenderClient(mock(ConnectionManager.class), randomBoolean(),
            randomLong(), null, emptySet(), new AlwaysSuccessSmppResultGenerator()));
        assertThrows(NullPointerException.class, () -> new TestSenderClient(mock(ConnectionManager.class), randomBoolean(),
            randomLong(), mock(MessageBuilder.class), null, new AlwaysSuccessSmppResultGenerator()));
        assertThrows(NullPointerException.class, () -> new TestSenderClient(mock(ConnectionManager.class), randomBoolean(),
            randomLong(), mock(MessageBuilder.class), emptySet(), null));
    }

    @Test
    void shouldGenerateResponse() {
        ConnectionManager connectionManager = mock(ConnectionManager.class);
        MessageBuilder messageBuilder = mock(MessageBuilder.class);
        BaseSmppSessionConfiguration config = mock(BaseSmppSessionConfiguration.class);
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        Set<String> allowedPhones = new HashSet<>();
        allowedPhones.add(randomString());
        String id = randomString();

        when(connectionManager.getConfiguration()).thenReturn(config);
        when(config.getName()).thenReturn(id);

        TestSenderClient testSenderClient = spy(new TestSenderClient(connectionManager, randomBoolean(), randomLong(),
            messageBuilder, allowedPhones, smppResultGenerator));

        Message message = new Message(randomString(), randomString(), randomString(), randomString(), MessageType.SIMPLE);
        testSenderClient.send(message);

        verify(smppResultGenerator, times(1)).generate(id, message);
        verify(connectionManager, never()).getSession();
    }

    @Test
    void shouldCallSenderClient() {
        ConnectionManager connectionManager = mock(ConnectionManager.class);
        BaseSmppSessionConfiguration config = mock(BaseSmppSessionConfiguration.class);
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        String msisdn = randomString();
        Set<String> allowedPhones = new HashSet<>();
        allowedPhones.add(msisdn);
        String id = randomString();


        when(connectionManager.getConfiguration()).thenReturn(config);
        when(config.getName()).thenReturn(id);

        TestSenderClient testSenderClient = spy(new TestSenderClient(connectionManager, randomBoolean(), randomLong(),
            mock(MessageBuilder.class), allowedPhones, smppResultGenerator));

        Message message = new Message(randomString(), msisdn, randomString(), randomString(), MessageType.SIMPLE);
        testSenderClient.send(message);

        verify(smppResultGenerator, never()).generate(id, message);
        verify(connectionManager, times(1)).getSession();
    }

    @Test
    void shouldGenerateResponseForCancel() {
        ConnectionManager connectionManager = mock(ConnectionManager.class);
        BaseSmppSessionConfiguration config = mock(BaseSmppSessionConfiguration.class);
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        Set<String> allowedPhones = new HashSet<>();
        allowedPhones.add(randomString());
        String id = randomString();

        when(connectionManager.getConfiguration()).thenReturn(config);
        when(config.getName()).thenReturn(id);

        TestSenderClient testSenderClient = spy(new TestSenderClient(connectionManager, randomBoolean(), randomLong(),
            mock(MessageBuilder.class), allowedPhones, smppResultGenerator));

        CancelMessage message = new CancelMessage(randomString(), randomString(), randomString());
        testSenderClient.cancel(message);

        verify(smppResultGenerator, times(1)).generate(id, message);
        verify(connectionManager, never()).getSession();
    }

    @Test
    void shouldCallSenderClientForCancel() {
        ConnectionManager connectionManager = mock(ConnectionManager.class);
        BaseSmppSessionConfiguration config = mock(BaseSmppSessionConfiguration.class);
        SmppResultGenerator smppResultGenerator = mock(SmppResultGenerator.class);
        String msisdn = randomString();
        Set<String> allowedPhones = new HashSet<>();
        allowedPhones.add(msisdn);
        String id = randomString();

        when(connectionManager.getConfiguration()).thenReturn(config);
        when(config.getName()).thenReturn(id);

        TestSenderClient testSenderClient = spy(new TestSenderClient(connectionManager, randomBoolean(), randomLong(),
            mock(MessageBuilder.class), allowedPhones, smppResultGenerator));

        CancelMessage message = new CancelMessage(randomString(), randomString(), msisdn);
        testSenderClient.cancel(message);

        verify(smppResultGenerator, never()).generate(id, message);
        verify(connectionManager, times(1)).getSession();
    }
}
