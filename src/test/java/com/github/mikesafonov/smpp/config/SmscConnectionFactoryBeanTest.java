package com.github.mikesafonov.smpp.config;


import com.cloudhopper.smpp.SmppSessionHandler;
import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.connection.ConnectionManagerFactory;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;
import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;
import com.github.mikesafonov.smpp.core.reciever.StandardResponseClient;
import com.github.mikesafonov.smpp.core.sender.MockSenderClient;
import com.github.mikesafonov.smpp.core.sender.StandardSenderClient;
import com.github.mikesafonov.smpp.core.sender.TestSenderClient;
import com.github.mikesafonov.smpp.core.sender.TypeOfAddressParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.github.mikesafonov.smpp.util.Randomizer.randomString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 * @author Mikhail Epatko
 */
class SmscConnectionFactoryBeanTest {

    private SmppProperties smppProperties;
    private SmppResultGenerator smppResultGenerator;
    private List<DeliveryReportConsumer> deliveryReportConsumers;
    private TypeOfAddressParser typeOfAddressParser;
    private ClientFactory clientFactory;
    private ConnectionManagerFactory connectionManagerFactory;
    private SmscConnectionFactoryBean smscConnectionFactoryBean;

    @BeforeEach
    void setUp() {
        smppProperties = mock(SmppProperties.class);
        smppResultGenerator = mock(SmppResultGenerator.class);
        deliveryReportConsumers = Arrays.asList(mock(DeliveryReportConsumer.class));
        typeOfAddressParser = mock(TypeOfAddressParser.class);
        clientFactory = mock(ClientFactory.class);
        connectionManagerFactory = mock(ConnectionManagerFactory.class);
        smscConnectionFactoryBean = new SmscConnectionFactoryBean(smppProperties, smppResultGenerator,
            deliveryReportConsumers, typeOfAddressParser, clientFactory, connectionManagerFactory);
    }

    @Nested
    class IsSingleton {

        @Test
        void shouldBeSingletonList() {
            assertTrue(smscConnectionFactoryBean.isSingleton());
            assertEquals(SmscConnectionsHolder.class, smscConnectionFactoryBean.getObjectType());
        }
    }


    @Nested
    class EmptyConnections {

        @Test
        void shouldReturnEmptyList() {
            when(smppProperties.getConnections()).thenReturn(new HashMap<>());

            SmscConnectionsHolder connections = smscConnectionFactoryBean.getObject();

            assertTrue(connections.getConnections().isEmpty());
        }
    }

    @Nested
    class MockConnection {

        @Test
        void shouldReturnMockConnection() {
            Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
            String connectionName = randomString();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setConnectionMode(ConnectionMode.MOCK);
            connectionMap.put(connectionName, smsc);

            MockSenderClient mockSenderClient = new MockSenderClient(smppResultGenerator, connectionName);

            when(smppProperties.getConnections()).thenReturn(connectionMap);
            when(smppProperties.getDefaults()).thenReturn(new SmppProperties.Defaults());
            when(clientFactory.mockSender(connectionName, smppResultGenerator)).thenReturn(
                mockSenderClient
            );

            SmscConnectionsHolder holder = smscConnectionFactoryBean.getObject();
            List<SmscConnection> connections = holder.getConnections();

            assertThat(connections).satisfies(smscConnections -> {
                assertThat(smscConnections.size()).isEqualTo(1);
                assertThat(smscConnections.get(0)).satisfies(connection -> {
                    assertThat(connection.getName()).isEqualTo(connectionName);
                    assertThat(connection.getResponseClient()).isEmpty();
                    assertThat(connection.getSenderClient()).isEqualTo(mockSenderClient);
                });
            });
        }
    }

    @Nested
    class StandardConnection {

        private SmppSessionHandler handler;

        @BeforeEach
        void setUp() {
            handler = mock(SmppSessionHandler.class);
            smscConnectionFactoryBean.setSessionHandler(handler);
        }

        @Test
        void shouldReturnStandardConnection() {
            Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
            String connectionName = randomString();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setConnectionMode(ConnectionMode.STANDARD);
            connectionMap.put(connectionName, smsc);
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            StandardSenderClient standardSenderClient = mock(StandardSenderClient.class);
            StandardResponseClient standardResponseClient = mock(StandardResponseClient.class);

            ConnectionManager transmitter = mock(ConnectionManager.class);
            ConnectionManager receiver = mock(ConnectionManager.class);

            when(connectionManagerFactory.transmitter(connectionName, defaults, smsc))
                .thenReturn(transmitter);
            when(connectionManagerFactory.receiver(connectionName, defaults, smsc, handler))
                .thenReturn(receiver);
            when(clientFactory.standardSender(connectionName, defaults, smsc, typeOfAddressParser, transmitter))
                .thenReturn(standardSenderClient);
            when(clientFactory.standardResponse(connectionName, receiver)).thenReturn(
                standardResponseClient
            );
            when(smppProperties.getConnections()).thenReturn(connectionMap);
            when(smppProperties.getDefaults()).thenReturn(defaults);
            when(smppProperties.isSetupRightAway()).thenReturn(true);

            SmscConnectionsHolder holder = smscConnectionFactoryBean.getObject();
            List<SmscConnection> connections = holder.getConnections();

            verify(standardSenderClient, times(1)).setup();
            verify(standardResponseClient, times(1)).setup();

            assertThat(connections).satisfies(smscConnections -> {
                assertThat(smscConnections.size()).isEqualTo(1);
                assertThat(smscConnections.get(0)).satisfies(connection -> {
                    assertThat(connection.getName()).isEqualTo(connectionName);
                    assertThat(connection.getResponseClient().get()).isEqualTo(standardResponseClient);
                    assertThat(connection.getSenderClient()).isInstanceOf(StandardSenderClient.class);
                    assertThat(connection.getSenderClient()).isEqualTo(standardSenderClient);
                });
            });
        }

        @Test
        void shouldReturnStandardConnectionWithoutResponseClient() {
            Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
            String connectionName = randomString();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setConnectionMode(ConnectionMode.STANDARD);
            connectionMap.put(connectionName, smsc);
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            StandardSenderClient standardSenderClient = mock(StandardSenderClient.class);
            ConnectionManager transmitter = mock(ConnectionManager.class);

            when(connectionManagerFactory.transmitter(connectionName, defaults, smsc))
                .thenReturn(transmitter);
            when(clientFactory.standardSender(connectionName, defaults, smsc, typeOfAddressParser, transmitter))
                .thenReturn(standardSenderClient);
            when(smppProperties.getConnections()).thenReturn(connectionMap);
            when(smppProperties.getDefaults()).thenReturn(defaults);
            when(smppProperties.isSetupRightAway()).thenReturn(true);

            smscConnectionFactoryBean = new SmscConnectionFactoryBean(smppProperties, smppResultGenerator,
                Collections.emptyList(), typeOfAddressParser, clientFactory, connectionManagerFactory);

            SmscConnectionsHolder holder = smscConnectionFactoryBean.getObject();
            List<SmscConnection> connections = holder.getConnections();

            verify(standardSenderClient, times(1)).setup();

            assertThat(connections).satisfies(smscConnections -> {
                assertThat(smscConnections.size()).isEqualTo(1);
                assertThat(smscConnections.get(0)).satisfies(connection -> {
                    assertThat(connection.getResponseClient()).isEmpty();
                });
            });
        }

        @Test
        void shouldReturnStandardConnectionNotSetuped() {
            Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
            String connectionName = randomString();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setConnectionMode(ConnectionMode.STANDARD);
            connectionMap.put(connectionName, smsc);
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            StandardSenderClient standardSenderClient = mock(StandardSenderClient.class);
            StandardResponseClient standardResponseClient = mock(StandardResponseClient.class);
            ConnectionManager transmitter = mock(ConnectionManager.class);
            ConnectionManager receiver = mock(ConnectionManager.class);

            when(connectionManagerFactory.transmitter(connectionName, defaults, smsc))
                .thenReturn(transmitter);
            when(connectionManagerFactory.receiver(connectionName, defaults, smsc, handler))
                .thenReturn(receiver);
            when(clientFactory.standardSender(connectionName, defaults, smsc, typeOfAddressParser, transmitter))
                .thenReturn(standardSenderClient);
            when(clientFactory.standardResponse(connectionName, receiver))
                .thenReturn(standardResponseClient);
            when(smppProperties.getConnections()).thenReturn(connectionMap);
            when(smppProperties.getDefaults()).thenReturn(defaults);
            when(smppProperties.isSetupRightAway()).thenReturn(false);

            SmscConnectionsHolder holder = smscConnectionFactoryBean.getObject();
            List<SmscConnection> connections = holder.getConnections();

            verify(standardSenderClient, never()).setup();
            verify(standardResponseClient, never()).setup();

            assertThat(connections).satisfies(smscConnections -> {
                assertThat(smscConnections.size()).isEqualTo(1);
                assertThat(smscConnections.get(0)).satisfies(connection -> {
                    assertThat(connection.getName()).isEqualTo(connectionName);
                    assertThat(connection.getResponseClient().get()).isEqualTo(standardResponseClient);
                    assertThat(connection.getSenderClient()).isInstanceOf(StandardSenderClient.class);
                    assertThat(connection.getSenderClient()).isEqualTo(standardSenderClient);
                });
            });
        }

        @Test
        void shouldUseSharedTransceiverConnectionManager() {
            Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
            String connectionName = randomString();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setConnectionMode(ConnectionMode.STANDARD);
            smsc.setConnectionType(ConnectionType.TRANSCEIVER);
            connectionMap.put(connectionName, smsc);
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            StandardSenderClient standardSenderClient = mock(StandardSenderClient.class);
            StandardResponseClient standardResponseClient = mock(StandardResponseClient.class);
            ConnectionManager transceiver = mock(ConnectionManager.class);

            when(connectionManagerFactory.transceiver(connectionName, defaults, smsc, handler))
                .thenReturn(transceiver);
            when(clientFactory.standardSender(connectionName, defaults, smsc, typeOfAddressParser, transceiver))
                .thenReturn(standardSenderClient);
            when(clientFactory.standardResponse(connectionName, transceiver))
                .thenReturn(standardResponseClient);
            when(smppProperties.getConnections()).thenReturn(connectionMap);
            when(smppProperties.getDefaults()).thenReturn(defaults);
            when(smppProperties.isSetupRightAway()).thenReturn(true);

            SmscConnectionsHolder holder = smscConnectionFactoryBean.getObject();
            List<SmscConnection> connections = holder.getConnections();

            assertThat(connections).satisfies(smscConnections -> {
                assertThat(smscConnections.size()).isEqualTo(1);
                assertThat(smscConnections.get(0)).satisfies(connection -> {
                    assertThat(connection.getName()).isEqualTo(connectionName);
                    assertThat(connection.getResponseClient().get()).isEqualTo(standardResponseClient);
                    assertThat(connection.getSenderClient()).isInstanceOf(StandardSenderClient.class);
                    assertThat(connection.getSenderClient()).isEqualTo(standardSenderClient);
                });
            });
        }
    }


    @Nested
    class TestStandardConnection {

        private SmppSessionHandler handler;

        @BeforeEach
        void setUp() {
            handler = mock(SmppSessionHandler.class);
            smscConnectionFactoryBean.setSessionHandler(handler);
        }

        @Test
        void shouldReturnStandardConnection() {
            Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
            String connectionName = randomString();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setConnectionMode(ConnectionMode.TEST);
            connectionMap.put(connectionName, smsc);
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            TestSenderClient testSenderClient = mock(TestSenderClient.class);
            StandardResponseClient standardResponseClient = mock(StandardResponseClient.class);

            ConnectionManager transmitter = mock(ConnectionManager.class);
            ConnectionManager receiver = mock(ConnectionManager.class);

            when(connectionManagerFactory.transmitter(connectionName, defaults, smsc))
                .thenReturn(transmitter);
            when(connectionManagerFactory.receiver(connectionName, defaults, smsc, handler))
                .thenReturn(receiver);
            when(clientFactory.testSender(connectionName, defaults, smsc, typeOfAddressParser, transmitter, smppResultGenerator))
                .thenReturn(testSenderClient);
            when(clientFactory.standardResponse(connectionName, receiver)).thenReturn(
                standardResponseClient
            );
            when(smppProperties.getConnections()).thenReturn(connectionMap);
            when(smppProperties.getDefaults()).thenReturn(defaults);
            when(smppProperties.isSetupRightAway()).thenReturn(true);

            SmscConnectionsHolder holder = smscConnectionFactoryBean.getObject();
            List<SmscConnection> connections = holder.getConnections();

            verify(testSenderClient, times(1)).setup();
            verify(standardResponseClient, times(1)).setup();

            assertThat(connections).satisfies(smscConnections -> {
                assertThat(smscConnections.size()).isEqualTo(1);
                assertThat(smscConnections.get(0)).satisfies(connection -> {
                    assertThat(connection.getName()).isEqualTo(connectionName);
                    assertThat(connection.getResponseClient().get()).isEqualTo(standardResponseClient);
                    assertThat(connection.getSenderClient()).isInstanceOf(TestSenderClient.class);
                    assertThat(connection.getSenderClient()).isEqualTo(testSenderClient);
                });
            });
        }

        @Test
        void shouldReturnStandardConnectionWithoutResponseClient() {
            Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
            String connectionName = randomString();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setConnectionMode(ConnectionMode.STANDARD);
            connectionMap.put(connectionName, smsc);
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            StandardSenderClient standardSenderClient = mock(StandardSenderClient.class);
            ConnectionManager transmitter = mock(ConnectionManager.class);

            when(connectionManagerFactory.transmitter(connectionName, defaults, smsc))
                .thenReturn(transmitter);
            when(clientFactory.standardSender(connectionName, defaults, smsc, typeOfAddressParser, transmitter))
                .thenReturn(standardSenderClient);
            when(smppProperties.getConnections()).thenReturn(connectionMap);
            when(smppProperties.getDefaults()).thenReturn(defaults);
            when(smppProperties.isSetupRightAway()).thenReturn(true);

            smscConnectionFactoryBean = new SmscConnectionFactoryBean(smppProperties, smppResultGenerator,
                Collections.emptyList(), typeOfAddressParser, clientFactory, connectionManagerFactory);

            SmscConnectionsHolder holder = smscConnectionFactoryBean.getObject();
            List<SmscConnection> connections = holder.getConnections();

            verify(standardSenderClient, times(1)).setup();

            assertThat(connections).satisfies(smscConnections -> {
                assertThat(smscConnections.size()).isEqualTo(1);
                assertThat(smscConnections.get(0)).satisfies(connection -> {
                    assertThat(connection.getResponseClient()).isEmpty();
                });
            });
        }

        @Test
        void shouldReturnStandardConnectionNotSetuped() {
            Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
            String connectionName = randomString();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setConnectionMode(ConnectionMode.STANDARD);
            connectionMap.put(connectionName, smsc);
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            StandardSenderClient standardSenderClient = mock(StandardSenderClient.class);
            StandardResponseClient standardResponseClient = mock(StandardResponseClient.class);
            ConnectionManager transmitter = mock(ConnectionManager.class);
            ConnectionManager receiver = mock(ConnectionManager.class);

            when(connectionManagerFactory.transmitter(connectionName, defaults, smsc))
                .thenReturn(transmitter);
            when(connectionManagerFactory.receiver(connectionName, defaults, smsc, handler))
                .thenReturn(receiver);
            when(clientFactory.standardSender(connectionName, defaults, smsc, typeOfAddressParser, transmitter))
                .thenReturn(standardSenderClient);
            when(clientFactory.standardResponse(connectionName, receiver))
                .thenReturn(standardResponseClient);
            when(smppProperties.getConnections()).thenReturn(connectionMap);
            when(smppProperties.getDefaults()).thenReturn(defaults);
            when(smppProperties.isSetupRightAway()).thenReturn(false);

            SmscConnectionsHolder holder = smscConnectionFactoryBean.getObject();
            List<SmscConnection> connections = holder.getConnections();

            verify(standardSenderClient, never()).setup();
            verify(standardResponseClient, never()).setup();

            assertThat(connections).satisfies(smscConnections -> {
                assertThat(smscConnections.size()).isEqualTo(1);
                assertThat(smscConnections.get(0)).satisfies(connection -> {
                    assertThat(connection.getName()).isEqualTo(connectionName);
                    assertThat(connection.getResponseClient().get()).isEqualTo(standardResponseClient);
                    assertThat(connection.getSenderClient()).isInstanceOf(StandardSenderClient.class);
                    assertThat(connection.getSenderClient()).isEqualTo(standardSenderClient);
                });
            });
        }

        @Test
        void shouldUseSharedTransceiverConnectionManager() {
            Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
            String connectionName = randomString();
            SmppProperties.SMSC smsc = new SmppProperties.SMSC();
            smsc.setConnectionMode(ConnectionMode.STANDARD);
            smsc.setConnectionType(ConnectionType.TRANSCEIVER);
            connectionMap.put(connectionName, smsc);
            SmppProperties.Defaults defaults = new SmppProperties.Defaults();
            StandardSenderClient standardSenderClient = mock(StandardSenderClient.class);
            StandardResponseClient standardResponseClient = mock(StandardResponseClient.class);
            ConnectionManager transceiver = mock(ConnectionManager.class);

            when(connectionManagerFactory.transceiver(connectionName, defaults, smsc, handler))
                .thenReturn(transceiver);
            when(clientFactory.standardSender(connectionName, defaults, smsc, typeOfAddressParser, transceiver))
                .thenReturn(standardSenderClient);
            when(clientFactory.standardResponse(connectionName, transceiver))
                .thenReturn(standardResponseClient);
            when(smppProperties.getConnections()).thenReturn(connectionMap);
            when(smppProperties.getDefaults()).thenReturn(defaults);
            when(smppProperties.isSetupRightAway()).thenReturn(true);

            SmscConnectionsHolder holder = smscConnectionFactoryBean.getObject();
            List<SmscConnection> connections = holder.getConnections();

            assertThat(connections).satisfies(smscConnections -> {
                assertThat(smscConnections.size()).isEqualTo(1);
                assertThat(smscConnections.get(0)).satisfies(connection -> {
                    assertThat(connection.getName()).isEqualTo(connectionName);
                    assertThat(connection.getResponseClient().get()).isEqualTo(standardResponseClient);
                    assertThat(connection.getSenderClient()).isInstanceOf(StandardSenderClient.class);
                    assertThat(connection.getSenderClient()).isEqualTo(standardSenderClient);
                });
            });
        }
    }
}
