package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSessionHandler;
import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.exceptions.ClientNameSmppException;
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
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setCredentials(randomCredentials());
            SmppSessionHandler handler = mock(SmppSessionHandler.class);

            ConnectionManager manager = factory.receiver(name, defaults, smsc, handler);

            assertThat(manager).isInstanceOf(ReceiverConnectionManager.class)
                    .extracting("sessionHandler",
                            "rebindPeriod",
                            "configuration.loggingOptions.isLogPduEnabled",
                            "configuration.loggingOptions.isLogBytesEnabled",
                            "configuration.port",
                            "configuration.host",
                            "configuration.name",
                            "configuration.password",
                            "configuration.systemId")
                    .containsExactly(handler,
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
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setLoggingBytes(true);
            smsc.setLoggingPdu(true);
            smsc.setMaxTry(randomInt());
            smsc.setCredentials(randomCredentials());
            smsc.setRebindPeriod(randomDuration());

            SmppSessionHandler handler = mock(SmppSessionHandler.class);

            ConnectionManager manager = factory.receiver(name, defaults, smsc, handler);

            assertThat(manager).isInstanceOf(ReceiverConnectionManager.class)
                    .extracting("sessionHandler",
                            "rebindPeriod",
                            "configuration.loggingOptions.isLogPduEnabled",
                            "configuration.loggingOptions.isLogBytesEnabled",
                            "configuration.port",
                            "configuration.host",
                            "configuration.name",
                            "configuration.password",
                            "configuration.systemId")
                    .containsExactly(handler,
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
        SmppSessionHandler handler = mock(SmppSessionHandler.class);

        ConnectionManager manager = factory.transceiver(name, defaults, smsc, handler);

        assertThat(manager).isInstanceOf(TransceiverConnectionManager.class)
            .extracting("sessionHandler", "maxTryCount",
                "configuration.loggingOptions.isLogPduEnabled",
                "configuration.loggingOptions.isLogBytesEnabled",
                "configuration.windowSize",
                "configuration.port",
                "configuration.host",
                "configuration.name",
                "configuration.password",
                "configuration.systemId")
            .containsExactly(handler, defaults.getMaxTry(),
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
        SmppSessionHandler handler = mock(SmppSessionHandler.class);

        ConnectionManager manager = factory.transceiver(name, defaults, smsc, handler);

        assertThat(manager).isInstanceOf(TransceiverConnectionManager.class)
            .extracting("sessionHandler", "maxTryCount",
                "configuration.loggingOptions.isLogPduEnabled",
                "configuration.loggingOptions.isLogBytesEnabled",
                "configuration.windowSize",
                "configuration.port",
                "configuration.host",
                "configuration.name",
                "configuration.password",
                "configuration.systemId")
            .containsExactly(handler, smsc.getMaxTry(),
                smsc.getLoggingPdu(), smsc.getLoggingBytes(),
                smsc.getWindowSize(),
                smsc.getCredentials().getPort(),
                smsc.getCredentials().getHost(),
                name,
                smsc.getCredentials().getPassword(),
                smsc.getCredentials().getUsername());
        assertThat(manager.getConfiguration()).isInstanceOf(TransceiverConfiguration.class);
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
