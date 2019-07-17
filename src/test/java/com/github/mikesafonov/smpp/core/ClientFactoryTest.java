package com.github.mikesafonov.smpp.core;

import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.clients.ClientFactory;
import com.github.mikesafonov.smpp.core.clients.MockSenderClient;
import com.github.mikesafonov.smpp.core.clients.SmppResultGenerator;
import com.github.mikesafonov.smpp.core.clients.TestSenderClient;
import com.github.mikesafonov.smpp.core.reciever.DefaultResponseClient;
import com.github.mikesafonov.smpp.core.reciever.ResponseClient;
import com.github.mikesafonov.smpp.core.sender.DefaultSenderClient;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import com.github.mikesafonov.smpp.core.sender.TypeOfAddressParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;

import static com.github.mikesafonov.smpp.core.util.Randomizer.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class ClientFactoryTest {

    @Test
    void shouldThrowRuntimeExceptionBecauseNameMustNotBeEmpty() throws Throwable {
        checkThrowRuntimeWithMessage(() -> ClientFactory.mockSender(null, null), "Name must not be empty!");
        checkThrowRuntimeWithMessage(() -> ClientFactory.mockSender("", null), "Name must not be empty!");
        checkThrowRuntimeWithMessage(() -> ClientFactory.defaultResponse(null, null, null), "Name must not be empty!");
        checkThrowRuntimeWithMessage(() -> ClientFactory.defaultResponse("", null, null), "Name must not be empty!");
        checkThrowRuntimeWithMessage(() -> ClientFactory.defaultSender(null, null, null, null), "Name must not be empty!");
        checkThrowRuntimeWithMessage(() -> ClientFactory.defaultSender("", null, null, null), "Name must not be empty!");
    }

    @Test
    void shouldThrowNpeBecauseInputArgumentIsNull() {
        assertThrows(NullPointerException.class, () -> ClientFactory.defaultSender(randomString(), null, null, null));
        assertThrows(NullPointerException.class, () -> ClientFactory.defaultSender(randomString(), mock(SmppProperties.Defaults.class), null, null));
        assertThrows(NullPointerException.class, () -> ClientFactory.defaultSender(randomString(), mock(SmppProperties.Defaults.class), mock(SmppProperties.SMSC.class), null));
        assertThrows(NullPointerException.class, () -> ClientFactory.defaultResponse(randomString(), null, null));
        assertThrows(NullPointerException.class, () -> ClientFactory.defaultResponse(randomString(), mock(SmppProperties.Defaults.class), null));
        assertThrows(NullPointerException.class, () -> ClientFactory.mockSender(randomString(), null));
        assertThrows(NullPointerException.class, () -> ClientFactory.testSender(null, null, null, null));
        assertThrows(NullPointerException.class, () -> ClientFactory.testSender(mock(SenderClient.class), null, null, null));
        assertThrows(NullPointerException.class, () -> ClientFactory.testSender(mock(SenderClient.class), mock(SmppProperties.Defaults.class), null, null));
        assertThrows(NullPointerException.class, () -> ClientFactory.testSender(mock(SenderClient.class), mock(SmppProperties.Defaults.class), mock(SmppResultGenerator.class), null));
    }

    @Test
    void shouldCreateMockSenderClient() {
        String name = randomString();
        SenderClient senderClient = ClientFactory.mockSender(name, mock(SmppResultGenerator.class));

        assertEquals(name, senderClient.getId());
        assertTrue(senderClient instanceof MockSenderClient);
    }

    @Test
    void shouldCreateDefaultResponseClientWithDefaultParameters() {
        String name = randomString();
        Duration defaultDuration = randomDuration();
        boolean isLoggingBytes = false;
        boolean isLoggingPdu = true;

        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        defaults.setLoggingPdu(isLoggingPdu);
        defaults.setLoggingBytes(isLoggingBytes);
        defaults.setRebindPeriod(defaultDuration);
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(new SmppProperties.Credentials());

        ResponseClient responseClient = ClientFactory.defaultResponse(name, defaults, smsc);

        assertThat(responseClient)
                .extracting("id", "rebindPeriod", "sessionConfiguration.loggingOptions.isLogPduEnabled", "sessionConfiguration.loggingOptions.isLogBytesEnabled")
                .containsExactly(name, defaultDuration.getSeconds(), isLoggingPdu, isLoggingBytes);

        assertTrue(responseClient instanceof DefaultResponseClient);
    }

    @Test
    void shouldCreateDefaultResponseClientWithCustomParameters() {
        String name = randomString();
        Duration defaultDuration = randomDuration();
        Duration customDuration = randomDuration();
        boolean isLoggingBytes = false;
        boolean isLoggingPdu = true;

        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        defaults.setLoggingPdu(isLoggingPdu);
        defaults.setLoggingBytes(isLoggingBytes);
        defaults.setRebindPeriod(defaultDuration);
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(new SmppProperties.Credentials());
        smsc.setLoggingBytes(!isLoggingBytes);
        smsc.setLoggingPdu(!isLoggingPdu);
        smsc.setRebindPeriod(customDuration);

        ResponseClient responseClient = ClientFactory.defaultResponse(name, defaults, smsc);

        assertThat(responseClient)
                .extracting("id", "rebindPeriod", "sessionConfiguration.loggingOptions.isLogPduEnabled", "sessionConfiguration.loggingOptions.isLogBytesEnabled")
                .containsExactly(name, customDuration.getSeconds(), !isLoggingPdu, !isLoggingBytes);

        assertTrue(responseClient instanceof DefaultResponseClient);
    }

    @Test
    void shouldCreateTestSenderClientWithDefaultParametersWithEmptyPhones() {
        String name = randomString();

        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        defaults.setAllowedPhones(null);

        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(new SmppProperties.Credentials());

        SenderClient senderClient = mock(SenderClient.class);
        when(senderClient.getId()).thenReturn(name);

        TestSenderClient testSenderClient = (TestSenderClient) ClientFactory.testSender(senderClient, defaults, mock(SmppResultGenerator.class), smsc);

        assertThat(testSenderClient).satisfies(client -> {
            assertThat(client.getId()).isEqualTo(name);
            assertThat(client.getAllowedPhones().isEmpty()).isTrue();
        });
    }

    @Test
    void shouldCreateTestSenderClientWithCustomParametersWithEmptyPhones() {
        String name = randomString();

        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        String phone = randomString();
        defaults.setAllowedPhones(new String[]{phone});

        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(new SmppProperties.Credentials());
        smsc.setAllowedPhones(new String[]{});

        SenderClient senderClient = mock(SenderClient.class);
        when(senderClient.getId()).thenReturn(name);

        TestSenderClient testSenderClient = (TestSenderClient) ClientFactory.testSender(senderClient, defaults, mock(SmppResultGenerator.class), smsc);

        assertThat(testSenderClient).satisfies(client -> {
            assertThat(client.getId()).isEqualTo(name);
            assertThat(client.getAllowedPhones().isEmpty()).isTrue();
        });
    }

    @Test
    void shouldCreateTestSenderClientWithDefaultParametersWithNotEmptyPhones() {
        String name = randomString();

        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        String phone = randomString();
        defaults.setAllowedPhones(new String[]{phone});

        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(new SmppProperties.Credentials());

        SenderClient senderClient = mock(SenderClient.class);
        when(senderClient.getId()).thenReturn(name);

        TestSenderClient testSenderClient = (TestSenderClient) ClientFactory.testSender(senderClient, defaults, mock(SmppResultGenerator.class), smsc);

        assertThat(testSenderClient).satisfies(client -> {
            assertThat(client.getId()).isEqualTo(name);
            assertThat(client.getAllowedPhones()).containsOnly(phone);
        });
    }

    @Test
    void shouldCreateTestSenderClientWithCustomParametersWithNotEmptyPhones() {
        String name = randomString();

        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        String phone = randomString();
        String customPhone = randomString();
        defaults.setAllowedPhones(new String[]{phone});

        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(new SmppProperties.Credentials());
        smsc.setAllowedPhones(new String[]{customPhone});

        SenderClient senderClient = mock(SenderClient.class);
        when(senderClient.getId()).thenReturn(name);

        TestSenderClient testSenderClient = (TestSenderClient) ClientFactory.testSender(senderClient, defaults, mock(SmppResultGenerator.class), smsc);

        assertThat(testSenderClient).satisfies(client -> {
            assertThat(client.getId()).isEqualTo(name);
            assertThat(client.getAllowedPhones()).containsOnly(customPhone);
        });
    }


    @Test
    void shouldCreateDefaultSenderClientWithDefaultParameters() {
        String name = randomString();
        boolean isLoggingBytes = false;
        boolean isLoggingPdu = true;
        boolean ucs2Only = true;
        int windowSize = randomInt();
        long requestTimeout = randomLong();
        int maxTry = randomInt();

        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        defaults.setLoggingBytes(isLoggingBytes);
        defaults.setLoggingPdu(isLoggingPdu);
        defaults.setUcs2Only(ucs2Only);
        defaults.setWindowSize(windowSize);
        defaults.setRequestTimeout(Duration.ofMillis(requestTimeout));

        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(new SmppProperties.Credentials());
        smsc.setMaxTry(maxTry);


        DefaultSenderClient testSenderClient = (DefaultSenderClient) ClientFactory.defaultSender(name, defaults, smsc, mock(TypeOfAddressParser.class));

        assertThat(testSenderClient).extracting("id", "ucs2Only", "timeoutMillis", "maxTryCount",
                "sessionConfig.loggingOptions.isLogPduEnabled", "sessionConfig.loggingOptions.isLogBytesEnabled", "sessionConfig.windowSize")
                .containsExactly(name, ucs2Only, requestTimeout, maxTry, isLoggingPdu, isLoggingBytes, windowSize);
    }

    @Test
    void shouldCreateDefaultSenderClientWithCustomParameters() {
        String name = randomString();

        boolean defaultIsLoggingBytes = false;
        boolean defaultIsLoggingPdu = true;
        boolean defaultUcs2Only = true;
        int defaultWindowSize = randomInt();
        long defaultRequestTimeout = randomLong();


        boolean isLoggingBytes = !defaultIsLoggingBytes;
        boolean isLoggingPdu = !defaultIsLoggingPdu;
        boolean ucs2Only = !defaultUcs2Only;
        int windowSize = randomInt();
        long requestTimeout = randomLong();
        int maxTry = randomInt();

        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        defaults.setLoggingBytes(defaultIsLoggingBytes);
        defaults.setLoggingPdu(defaultIsLoggingPdu);
        defaults.setUcs2Only(defaultUcs2Only);
        defaults.setWindowSize(defaultWindowSize);
        defaults.setRequestTimeout(Duration.ofMillis(defaultRequestTimeout));

        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(new SmppProperties.Credentials());
        smsc.setMaxTry(maxTry);
        smsc.setLoggingBytes(isLoggingBytes);
        smsc.setLoggingPdu(isLoggingPdu);
        smsc.setUcs2Only(ucs2Only);
        smsc.setWindowSize(windowSize);
        smsc.setRequestTimeout(Duration.ofMillis(requestTimeout));


        DefaultSenderClient testSenderClient = (DefaultSenderClient) ClientFactory.defaultSender(name, defaults, smsc, mock(TypeOfAddressParser.class));

        assertThat(testSenderClient).extracting("id", "ucs2Only", "timeoutMillis", "maxTryCount",
                "sessionConfig.loggingOptions.isLogPduEnabled", "sessionConfig.loggingOptions.isLogBytesEnabled", "sessionConfig.windowSize")
                .containsExactly(name, ucs2Only, requestTimeout, maxTry, isLoggingPdu, isLoggingBytes, windowSize);
    }


    private void checkThrowRuntimeWithMessage(Executable executable, String expectedMessage) throws Throwable {
        try {
            executable.execute();
            fail("Exception expected but not throwed");
        } catch (RuntimeException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
