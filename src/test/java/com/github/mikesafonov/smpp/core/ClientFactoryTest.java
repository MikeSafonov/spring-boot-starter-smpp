package com.github.mikesafonov.smpp.core;

import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.exceptions.ClientNameSmppException;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;
import com.github.mikesafonov.smpp.core.reciever.ResponseClient;
import com.github.mikesafonov.smpp.core.reciever.StandardResponseClient;
import com.github.mikesafonov.smpp.core.sender.*;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;

import static com.github.mikesafonov.smpp.util.Randomizer.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class ClientFactoryTest {

    private ClientFactory clientFactory = new ClientFactory();

    @Nested
    class EmptyName {
        @Test
        void shouldThrowRuntimeExceptionBecauseNameMustNotBeEmpty() throws Throwable {
            checkThrowClientNameSmppWithMessage(() -> clientFactory.mockSender(null, null), "Name must not be empty!");
            checkThrowClientNameSmppWithMessage(() -> clientFactory.mockSender("", null), "Name must not be empty!");
            checkThrowClientNameSmppWithMessage(() -> clientFactory.standardResponse(null, null), "Name must not be empty!");
            checkThrowClientNameSmppWithMessage(() -> clientFactory.standardSender(null, null, null, null, null), "Name must not be empty!");
            checkThrowClientNameSmppWithMessage(() -> clientFactory.standardSender("", null, null, null, null), "Name must not be empty!");
        }
    }

    @Nested
    class NPE {
        @Test
        void shouldThrowNpeBecauseInputArgumentIsNull() {
            assertThrows(NullPointerException.class, () -> clientFactory.standardSender(randomString(), null, null, null, null));
            assertThrows(NullPointerException.class, () -> clientFactory.standardSender(randomString(), mock(SmppProperties.Defaults.class), null, null, null));
            assertThrows(NullPointerException.class, () -> clientFactory.standardSender(randomString(), mock(SmppProperties.Defaults.class), mock(SmppProperties.SMSC.class), null, null));
            assertThrows(NullPointerException.class, () -> clientFactory.standardSender(randomString(), mock(SmppProperties.Defaults.class), mock(SmppProperties.SMSC.class), mock(TypeOfAddressParser.class), null));
            assertThrows(NullPointerException.class, () -> clientFactory.standardResponse(randomString(), null));
            assertThrows(NullPointerException.class, () -> clientFactory.mockSender(randomString(), null));
            assertThrows(NullPointerException.class, () -> clientFactory.testSender(null, null, null, null));
            assertThrows(NullPointerException.class, () -> clientFactory.testSender(mock(SenderClient.class), null, null, null));
            assertThrows(NullPointerException.class, () -> clientFactory.testSender(mock(SenderClient.class), mock(SmppProperties.Defaults.class), null, null));
            assertThrows(NullPointerException.class, () -> clientFactory.testSender(mock(SenderClient.class), mock(SmppProperties.Defaults.class), mock(SmppProperties.SMSC.class), null));
        }
    }

    @Nested
    class MockSender {
        @Test
        void shouldCreateMockSenderClient() {
            String name = randomString();
            SenderClient senderClient = clientFactory.mockSender(name, mock(SmppResultGenerator.class));

            assertEquals(name, senderClient.getId());
            assertTrue(senderClient instanceof MockSenderClient);
        }
    }

    @Nested
    class StandardResponse {
        @Test
        void shouldCreateDefaultResponseClientWithDefaultParameters() {
            String name = randomString();
            ConnectionManager connectionManager = mock(ConnectionManager.class);

            ResponseClient responseClient = clientFactory.standardResponse(name, connectionManager);

            assertThat(responseClient)
                .extracting("connectionManager")
                .isEqualTo(connectionManager);

            assertTrue(responseClient instanceof StandardResponseClient);
        }
    }

    @Nested
    class TestSender {
        @Test
        void shouldCreateTestSenderClientWithDefaultParametersWithEmptyPhones() {
            String name = randomString();

            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            defaults.setAllowedPhones(null);

            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setCredentials(new SmppProperties.Credentials());

            SenderClient senderClient = mock(SenderClient.class);
            when(senderClient.getId()).thenReturn(name);

            TestSenderClient testSenderClient = (TestSenderClient) clientFactory
                .testSender(senderClient, defaults, smsc, mock(SmppResultGenerator.class));

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

            TestSenderClient testSenderClient = (TestSenderClient) clientFactory
                .testSender(senderClient, defaults, smsc, mock(SmppResultGenerator.class));

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

            TestSenderClient testSenderClient = (TestSenderClient) clientFactory
                .testSender(senderClient, defaults, smsc, mock(SmppResultGenerator.class));

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

            TestSenderClient testSenderClient = (TestSenderClient) clientFactory
                .testSender(senderClient, defaults, smsc, mock(SmppResultGenerator.class));

            assertThat(testSenderClient).satisfies(client -> {
                assertThat(client.getId()).isEqualTo(name);
                assertThat(client.getAllowedPhones()).containsOnly(customPhone);
            });
        }
    }

    @Nested
    class StandardSender {
        @Test
        void shouldCreateDefaultSenderClientWithDefaultParameters() {
            String name = randomString();
            boolean ucs2Only = randomBoolean();
            long requestTimeout = randomLong();

            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            defaults.setUcs2Only(ucs2Only);
            defaults.setRequestTimeout(Duration.ofMillis(requestTimeout));

            SmppProperties.SMSC smsc = new SmppProperties.SMSC();

            ConnectionManager connectionManager = mock(ConnectionManager.class);

            StandardSenderClient client = (StandardSenderClient) clientFactory
                .standardSender(name, defaults, smsc, mock(TypeOfAddressParser.class), connectionManager);

            assertThat(client).extracting("ucs2Only", "timeoutMillis", "connectionManager")
                .containsExactly(ucs2Only, defaults.getRequestTimeout().toMillis(), connectionManager);
        }
    }


    private void checkThrowClientNameSmppWithMessage(Executable executable, String expectedMessage) throws Throwable {
        try {
            executable.execute();
            fail("Exception expected but not throwed");
        } catch (ClientNameSmppException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
