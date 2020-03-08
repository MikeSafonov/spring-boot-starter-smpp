package com.github.mikesafonov.smpp.core.connection;

import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.exceptions.ClientNameSmppException;
import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static com.github.mikesafonov.smpp.util.Randomizer.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * @author Mike Safonov
 */
class ConnectionManagerFactoryTest {

    private ConnectionManagerFactory factory = new ConnectionManagerFactory();

    @Nested
    class Transmitter {
        @Test
        void shouldThrowRuntimeExceptionBecauseNameMustNotBeEmpty() throws Throwable {
            checkThrowClientNameSmppWithMessage(() -> factory.transmitter(null, null, null),
                    "Name must not be empty!");
            checkThrowClientNameSmppWithMessage(() -> factory.transmitter("", null, null),
                    "Name must not be empty!");
        }

        @Test
        void shouldThrowNPEBecauseParameterIsNull() {
            assertThrows(NullPointerException.class,
                    () -> factory.transmitter(randomString(), null, null));
            assertThrows(NullPointerException.class,
                    () -> factory.transmitter(randomString(), mock(SmppProperties.Defaults.class), null));
        }

        @Test
        void shouldReturnConnectionManagerWithDefaultProperties(){
            String name = randomString();
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setCredentials(randomCredentials());

            ConnectionManager manager = factory.transmitter(name, defaults, smsc);

            assertThat(manager).isInstanceOf(TransmitterConnectionManager.class)
                    .extracting("sessionHandler", "maxTryCount",
                            "configuration.loggingOptions.isLogPduEnabled",
                            "configuration.loggingOptions.isLogBytesEnabled",
                            "configuration.windowSize",
                            "configuration.port",
                            "configuration.host",
                            "configuration.name",
                            "configuration.password",
                            "configuration.systemId")
                    .containsExactly(null, defaults.getMaxTry(),
                            defaults.isLoggingPdu(), defaults.isLoggingBytes(),
                            defaults.getWindowSize(),
                            smsc.getCredentials().getPort(),
                            smsc.getCredentials().getHost(),
                            name,
                            smsc.getCredentials().getPassword(),
                            smsc.getCredentials().getUsername());
            assertThat(manager.getConfiguration()).isInstanceOf(TransmitterConfiguration.class);
        }

        @Test
        void shouldReturnConnectionManagerWithCustomProperties(){
            String name = randomString();
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setLoggingBytes(true);
            smsc.setLoggingPdu(true);
            smsc.setMaxTry(randomInt());
            smsc.setCredentials(randomCredentials());
            smsc.setWindowSize(randomInt());

            ConnectionManager manager = factory.transmitter(name, defaults, smsc);

            assertThat(manager).isInstanceOf(TransmitterConnectionManager.class)
                    .extracting("sessionHandler", "maxTryCount",
                            "configuration.loggingOptions.isLogPduEnabled",
                            "configuration.loggingOptions.isLogBytesEnabled",
                            "configuration.windowSize",
                            "configuration.port",
                            "configuration.host",
                            "configuration.name",
                            "configuration.password",
                            "configuration.systemId")
                    .containsExactly(null, smsc.getMaxTry(),
                            smsc.getLoggingPdu(), smsc.getLoggingBytes(),
                            smsc.getWindowSize(),
                            smsc.getCredentials().getPort(),
                            smsc.getCredentials().getHost(),
                            name,
                            smsc.getCredentials().getPassword(),
                            smsc.getCredentials().getUsername());
            assertThat(manager.getConfiguration()).isInstanceOf(TransmitterConfiguration.class);
        }
    }

    @Nested
    class Receiver {
        @Test
        void shouldThrowRuntimeExceptionBecauseNameMustNotBeEmpty() throws Throwable {
            checkThrowClientNameSmppWithMessage(() -> factory.receiver(null, null, null, null),
                    "Name must not be empty!");
            checkThrowClientNameSmppWithMessage(() -> factory.receiver("", null, null, null),
                    "Name must not be empty!");
        }

        @Test
        void shouldThrowNPEBecauseParameterIsNull() {
            assertThrows(NullPointerException.class,
                    () -> factory.receiver(randomString(), null, null, null));
            assertThrows(NullPointerException.class,
                    () -> factory.receiver(randomString(), mock(SmppProperties.Defaults.class), null, null));
            assertThrows(NullPointerException.class,
                    () -> factory.receiver(randomString(), mock(SmppProperties.Defaults.class),
                            mock(SmppProperties.SMSC.class), null));
        }

        @Test
        void shouldReturnConnectionManagerWithDefaultProperties(){
            String name = randomString();
            DeliveryReportConsumer deliveryReportConsumer = mock(DeliveryReportConsumer.class);
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setCredentials(randomCredentials());

            ConnectionManager manager = factory.receiver(name, defaults, smsc, deliveryReportConsumer);

            assertThat(manager).isInstanceOf(ReceiverConnectionManager.class)
                    .extracting("sessionHandler.clientId",
                            "sessionHandler.deliveryReportConsumer",
                            "rebindPeriod",
                            "configuration.loggingOptions.isLogPduEnabled",
                            "configuration.loggingOptions.isLogBytesEnabled",
                            "configuration.port",
                            "configuration.host",
                            "configuration.name",
                            "configuration.password",
                            "configuration.systemId")
                    .containsExactly(name, deliveryReportConsumer,
                            defaults.getRebindPeriod().getSeconds(),
                            defaults.isLoggingPdu(), defaults.isLoggingBytes(),
                            smsc.getCredentials().getPort(),
                            smsc.getCredentials().getHost(),
                            name,
                            smsc.getCredentials().getPassword(),
                            smsc.getCredentials().getUsername());
            assertThat(manager.getConfiguration()).isInstanceOf(ReceiverConfiguration.class);
        }

        @Test
        void shouldReturnConnectionManagerWithCustomProperties(){
            String name = randomString();
            DeliveryReportConsumer deliveryReportConsumer = mock(DeliveryReportConsumer.class);
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setLoggingBytes(true);
            smsc.setLoggingPdu(true);
            smsc.setMaxTry(randomInt());
            smsc.setCredentials(randomCredentials());
            smsc.setRebindPeriod(randomDuration());

            ConnectionManager manager = factory.receiver(name, defaults, smsc, deliveryReportConsumer);

            assertThat(manager).isInstanceOf(ReceiverConnectionManager.class)
                    .extracting("sessionHandler.clientId",
                            "sessionHandler.deliveryReportConsumer",
                            "rebindPeriod",
                            "configuration.loggingOptions.isLogPduEnabled",
                            "configuration.loggingOptions.isLogBytesEnabled",
                            "configuration.port",
                            "configuration.host",
                            "configuration.name",
                            "configuration.password",
                            "configuration.systemId")
                    .containsExactly(name, deliveryReportConsumer,
                            smsc.getRebindPeriod().getSeconds(),
                            smsc.getLoggingPdu(), smsc.getLoggingBytes(),
                            smsc.getCredentials().getPort(),
                            smsc.getCredentials().getHost(),
                            name,
                            smsc.getCredentials().getPassword(),
                            smsc.getCredentials().getUsername());
            assertThat(manager.getConfiguration()).isInstanceOf(ReceiverConfiguration.class);
        }
    }

    @Nested
    class Transceiver {
        @Test
        void shouldThrowRuntimeExceptionBecauseNameMustNotBeEmpty() throws Throwable {
            checkThrowClientNameSmppWithMessage(() -> factory.transceiver(null, null, null, null),
                    "Name must not be empty!");
            checkThrowClientNameSmppWithMessage(() -> factory.transceiver("", null, null, null),
                    "Name must not be empty!");
        }

        @Test
        void shouldThrowNPEBecauseParameterIsNull() {
            assertThrows(NullPointerException.class,
                    () -> factory.transceiver(randomString(), null, null, null));
            assertThrows(NullPointerException.class,
                    () -> factory.transceiver(randomString(), mock(SmppProperties.Defaults.class), null, null));
            assertThrows(NullPointerException.class,
                    () -> factory.transceiver(randomString(), mock(SmppProperties.Defaults.class),
                            mock(SmppProperties.SMSC.class), null));
        }
    }

    @Test
    void shouldReturnConnectionManagerWithDefaultProperties(){
        String name = randomString();
        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setCredentials(randomCredentials());
        DeliveryReportConsumer deliveryReportConsumer = mock(DeliveryReportConsumer.class);

        ConnectionManager manager = factory.transceiver(name, defaults, smsc, deliveryReportConsumer);

        assertThat(manager).isInstanceOf(TransceiverConnectionManager.class)
            .extracting("sessionHandler.clientId",
                "sessionHandler.deliveryReportConsumer", "maxTryCount",
                "configuration.loggingOptions.isLogPduEnabled",
                "configuration.loggingOptions.isLogBytesEnabled",
                "configuration.windowSize",
                "configuration.port",
                "configuration.host",
                "configuration.name",
                "configuration.password",
                "configuration.systemId")
            .containsExactly(name, deliveryReportConsumer, defaults.getMaxTry(),
                defaults.isLoggingPdu(), defaults.isLoggingBytes(),
                defaults.getWindowSize(),
                smsc.getCredentials().getPort(),
                smsc.getCredentials().getHost(),
                name,
                smsc.getCredentials().getPassword(),
                smsc.getCredentials().getUsername());
        assertThat(manager.getConfiguration()).isInstanceOf(TransceiverConfiguration.class);
    }

    @Test
    void shouldReturnConnectionManagerWithCustomProperties(){
        String name = randomString();
        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setLoggingBytes(true);
        smsc.setLoggingPdu(true);
        smsc.setMaxTry(randomInt());
        smsc.setCredentials(randomCredentials());
        smsc.setWindowSize(randomInt());
        DeliveryReportConsumer deliveryReportConsumer = mock(DeliveryReportConsumer.class);

        ConnectionManager manager = factory.transceiver(name, defaults, smsc, deliveryReportConsumer);

        assertThat(manager).isInstanceOf(TransceiverConnectionManager.class)
            .extracting("sessionHandler.clientId",
                "sessionHandler.deliveryReportConsumer", "maxTryCount",
                "configuration.loggingOptions.isLogPduEnabled",
                "configuration.loggingOptions.isLogBytesEnabled",
                "configuration.windowSize",
                "configuration.port",
                "configuration.host",
                "configuration.name",
                "configuration.password",
                "configuration.systemId")
            .containsExactly(name, deliveryReportConsumer, smsc.getMaxTry(),
                smsc.getLoggingPdu(), smsc.getLoggingBytes(),
                smsc.getWindowSize(),
                smsc.getCredentials().getPort(),
                smsc.getCredentials().getHost(),
                name,
                smsc.getCredentials().getPassword(),
                smsc.getCredentials().getUsername());
        assertThat(manager.getConfiguration()).isInstanceOf(TransceiverConfiguration.class);
    }


//    @Test
//    void shouldCreateMockSenderClient() {
//        String name = randomString();
//        SenderClient senderClient = clientFactory.mockSender(name, mock(SmppResultGenerator.class));
//
//        assertEquals(name, senderClient.getId());
//        assertTrue(senderClient instanceof MockSenderClient);
//    }
//
//    @Test
//    void shouldCreateDefaultResponseClientWithDefaultParameters() {
//        String name = randomString();
//        Duration defaultDuration = randomDuration();
//        boolean isLoggingBytes = false;
//        boolean isLoggingPdu = true;
//
//        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
//        defaults.setLoggingPdu(isLoggingPdu);
//        defaults.setLoggingBytes(isLoggingBytes);
//        defaults.setRebindPeriod(defaultDuration);
//        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
//        smsc.setCredentials(new SmppProperties.Credentials());
//        DeliveryReportConsumer deliveryReportConsumer = mock(DeliveryReportConsumer.class);
//
//        ResponseClient responseClient = clientFactory.standardResponse(name, defaults, smsc, deliveryReportConsumer);
//
//        assertThat(responseClient)
//                .extracting("id", "connectionManager.rebindPeriod",
//                        "connectionManager.configuration.loggingOptions.isLogPduEnabled",
//                        "connectionManager.configuration.loggingOptions.isLogBytesEnabled")
//                .containsExactly(name, defaultDuration.getSeconds(), isLoggingPdu, isLoggingBytes);
//
//        assertTrue(responseClient instanceof StandardResponseClient);
//    }
//
//    @Test
//    void shouldCreateDefaultResponseClientWithCustomParameters() {
//        String name = randomString();
//        Duration defaultDuration = randomDuration();
//        Duration customDuration = randomDuration();
//        boolean isLoggingBytes = false;
//        boolean isLoggingPdu = true;
//
//        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
//        defaults.setLoggingPdu(isLoggingPdu);
//        defaults.setLoggingBytes(isLoggingBytes);
//        defaults.setRebindPeriod(defaultDuration);
//        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
//        smsc.setCredentials(new SmppProperties.Credentials());
//        smsc.setLoggingBytes(!isLoggingBytes);
//        smsc.setLoggingPdu(!isLoggingPdu);
//        smsc.setRebindPeriod(customDuration);
//
//        DeliveryReportConsumer deliveryReportConsumer = mock(DeliveryReportConsumer.class);
//
//        ResponseClient responseClient = clientFactory.standardResponse(name, defaults, smsc, deliveryReportConsumer);
//
//        assertThat(responseClient)
//                .extracting("id", "connectionManager.rebindPeriod",
//                        "connectionManager.configuration.loggingOptions.isLogPduEnabled",
//                        "connectionManager.configuration.loggingOptions.isLogBytesEnabled")
//                .containsExactly(name, customDuration.getSeconds(), !isLoggingPdu, !isLoggingBytes);
//
//        assertTrue(responseClient instanceof StandardResponseClient);
//    }
//
//    @Test
//    void shouldCreateTestSenderClientWithDefaultParametersWithEmptyPhones() {
//        String name = randomString();
//
//        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
//        defaults.setAllowedPhones(null);
//
//        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
//        smsc.setCredentials(new SmppProperties.Credentials());
//
//        SenderClient senderClient = mock(SenderClient.class);
//        when(senderClient.getId()).thenReturn(name);
//
//        TestSenderClient testSenderClient = (TestSenderClient) clientFactory.testSender(senderClient, defaults, smsc, mock(SmppResultGenerator.class));
//
//        assertThat(testSenderClient).satisfies(client -> {
//            assertThat(client.getId()).isEqualTo(name);
//            assertThat(client.getAllowedPhones().isEmpty()).isTrue();
//        });
//    }
//
//    @Test
//    void shouldCreateTestSenderClientWithCustomParametersWithEmptyPhones() {
//        String name = randomString();
//
//        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
//        String phone = randomString();
//        defaults.setAllowedPhones(new String[]{phone});
//
//        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
//        smsc.setCredentials(new SmppProperties.Credentials());
//        smsc.setAllowedPhones(new String[]{});
//
//        SenderClient senderClient = mock(SenderClient.class);
//        when(senderClient.getId()).thenReturn(name);
//
//        TestSenderClient testSenderClient = (TestSenderClient) clientFactory.testSender(senderClient, defaults, smsc, mock(SmppResultGenerator.class));
//
//        assertThat(testSenderClient).satisfies(client -> {
//            assertThat(client.getId()).isEqualTo(name);
//            assertThat(client.getAllowedPhones().isEmpty()).isTrue();
//        });
//    }
//
//    @Test
//    void shouldCreateTestSenderClientWithDefaultParametersWithNotEmptyPhones() {
//        String name = randomString();
//
//        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
//        String phone = randomString();
//        defaults.setAllowedPhones(new String[]{phone});
//
//        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
//        smsc.setCredentials(new SmppProperties.Credentials());
//
//        SenderClient senderClient = mock(SenderClient.class);
//        when(senderClient.getId()).thenReturn(name);
//
//        TestSenderClient testSenderClient = (TestSenderClient) clientFactory.testSender(senderClient, defaults, smsc, mock(SmppResultGenerator.class));
//
//        assertThat(testSenderClient).satisfies(client -> {
//            assertThat(client.getId()).isEqualTo(name);
//            assertThat(client.getAllowedPhones()).containsOnly(phone);
//        });
//    }
//
//    @Test
//    void shouldCreateTestSenderClientWithCustomParametersWithNotEmptyPhones() {
//        String name = randomString();
//
//        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
//        String phone = randomString();
//        String customPhone = randomString();
//        defaults.setAllowedPhones(new String[]{phone});
//
//        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
//        smsc.setCredentials(new SmppProperties.Credentials());
//        smsc.setAllowedPhones(new String[]{customPhone});
//
//        SenderClient senderClient = mock(SenderClient.class);
//        when(senderClient.getId()).thenReturn(name);
//
//        TestSenderClient testSenderClient = (TestSenderClient) clientFactory.testSender(senderClient, defaults, smsc, mock(SmppResultGenerator.class));
//
//        assertThat(testSenderClient).satisfies(client -> {
//            assertThat(client.getId()).isEqualTo(name);
//            assertThat(client.getAllowedPhones()).containsOnly(customPhone);
//        });
//    }
//
//
//    @Test
//    void shouldCreateDefaultSenderClientWithDefaultParameters() {
//        String name = randomString();
//        boolean isLoggingBytes = false;
//        boolean isLoggingPdu = true;
//        boolean ucs2Only = true;
//        int windowSize = randomInt();
//        long requestTimeout = randomLong();
//        int maxTry = randomInt();
//
//        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
//        defaults.setLoggingBytes(isLoggingBytes);
//        defaults.setLoggingPdu(isLoggingPdu);
//        defaults.setUcs2Only(ucs2Only);
//        defaults.setWindowSize(windowSize);
//        defaults.setRequestTimeout(Duration.ofMillis(requestTimeout));
//
//        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
//        smsc.setCredentials(new SmppProperties.Credentials());
//        smsc.setMaxTry(maxTry);
//
//
//        StandardSenderClient testSenderClient = (StandardSenderClient) clientFactory.standardSender(name, defaults, smsc, mock(TypeOfAddressParser.class));
//
//        assertThat(testSenderClient).extracting("id", "ucs2Only", "timeoutMillis", "connectionManager.maxTryCount",
//                "connectionManager.configuration.loggingOptions.isLogPduEnabled",
//                "connectionManager.configuration.loggingOptions.isLogBytesEnabled",
//                "connectionManager.configuration.windowSize")
//                .containsExactly(name, ucs2Only, requestTimeout, maxTry, isLoggingPdu, isLoggingBytes, windowSize);
//    }
//
//    @Test
//    void shouldCreateDefaultSenderClientWithCustomParameters() {
//        String name = randomString();
//
//        boolean defaultIsLoggingBytes = false;
//        boolean defaultIsLoggingPdu = true;
//        boolean defaultUcs2Only = true;
//        int defaultWindowSize = randomInt();
//        long defaultRequestTimeout = randomLong();
//
//
//        boolean isLoggingBytes = !defaultIsLoggingBytes;
//        boolean isLoggingPdu = !defaultIsLoggingPdu;
//        boolean ucs2Only = !defaultUcs2Only;
//        int windowSize = randomInt();
//        long requestTimeout = randomLong();
//        int maxTry = randomInt();
//
//        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
//        defaults.setLoggingBytes(defaultIsLoggingBytes);
//        defaults.setLoggingPdu(defaultIsLoggingPdu);
//        defaults.setUcs2Only(defaultUcs2Only);
//        defaults.setWindowSize(defaultWindowSize);
//        defaults.setRequestTimeout(Duration.ofMillis(defaultRequestTimeout));
//
//        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
//        smsc.setCredentials(new SmppProperties.Credentials());
//        smsc.setMaxTry(maxTry);
//        smsc.setLoggingBytes(isLoggingBytes);
//        smsc.setLoggingPdu(isLoggingPdu);
//        smsc.setUcs2Only(ucs2Only);
//        smsc.setWindowSize(windowSize);
//        smsc.setRequestTimeout(Duration.ofMillis(requestTimeout));
//
//        StandardSenderClient testSenderClient = (StandardSenderClient) clientFactory.standardSender(name, defaults, smsc, mock(TypeOfAddressParser.class));
//
//        assertThat(testSenderClient).extracting("id", "ucs2Only", "timeoutMillis", "connectionManager.maxTryCount",
//                "connectionManager.configuration.loggingOptions.isLogPduEnabled",
//                "connectionManager.configuration.loggingOptions.isLogBytesEnabled",
//                "connectionManager.configuration.windowSize")
//                .containsExactly(name, ucs2Only, requestTimeout, maxTry, isLoggingPdu, isLoggingBytes, windowSize);
//    }


    private void checkThrowClientNameSmppWithMessage(Executable executable, String expectedMessage) throws Throwable {
        try {
            executable.execute();
            fail("Exception expected but not throwed");
        } catch (ClientNameSmppException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
