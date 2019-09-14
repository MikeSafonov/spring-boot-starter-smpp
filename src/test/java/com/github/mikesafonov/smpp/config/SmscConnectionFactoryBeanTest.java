package com.github.mikesafonov.smpp.config;


import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;
import com.github.mikesafonov.smpp.core.reciever.DefaultResponseClient;
import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;
import com.github.mikesafonov.smpp.core.reciever.ResponseSmppSessionHandler;
import com.github.mikesafonov.smpp.core.sender.DefaultSenderClient;
import com.github.mikesafonov.smpp.core.sender.MockSenderClient;
import com.github.mikesafonov.smpp.core.sender.TestSenderClient;
import com.github.mikesafonov.smpp.core.sender.TypeOfAddressParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.mikesafonov.smpp.util.Randomizer.randomString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class SmscConnectionFactoryBeanTest {
    private SmppProperties smppProperties;
    private SmppResultGenerator smppResultGenerator;
    private DeliveryReportConsumer deliveryReportConsumer;
    private TypeOfAddressParser typeOfAddressParser;
    private ClientFactory clientFactory;
    private SmscConnectionFactoryBean smscConnectionFactoryBean;

    @BeforeEach
    void setUp() {
        smppProperties = mock(SmppProperties.class);
        smppResultGenerator = mock(SmppResultGenerator.class);
        deliveryReportConsumer = mock(DeliveryReportConsumer.class);
        typeOfAddressParser = mock(TypeOfAddressParser.class);
        clientFactory = mock(ClientFactory.class);
        smscConnectionFactoryBean = new SmscConnectionFactoryBean(smppProperties, smppResultGenerator, deliveryReportConsumer, typeOfAddressParser, clientFactory);
    }

    @Test
    void shouldBeSingletonList() {
        assertTrue(smscConnectionFactoryBean.isSingleton());
        assertEquals(List.class, smscConnectionFactoryBean.getObjectType());
    }


    @Test
    void shouldReturnEmptyList() {
        when(smppProperties.getConnections()).thenReturn(new HashMap<>());

        List<SmscConnection> connections = smscConnectionFactoryBean.getObject();

        assertTrue(connections.isEmpty());
    }


    @Test
    void shouldReturnMockConnection() {
        Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
        String connectionName = randomString();
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setConnectionMode(ConnectionMode.MOCK);
        connectionMap.put(connectionName, smsc);

        MockSenderClient mockSenderClient = new MockSenderClient(smppResultGenerator, connectionName);

        when(smppProperties.getConnections()).thenReturn(connectionMap);
        when(clientFactory.mockSender(connectionName, smppResultGenerator)).thenReturn(
                mockSenderClient
        );

        List<SmscConnection> connections = smscConnectionFactoryBean.getObject();

        assertThat(connections).satisfies(smscConnections -> {
            assertThat(smscConnections.size()).isEqualTo(1);
            assertThat(smscConnections.get(0)).satisfies(connection -> {
                assertThat(connection.getName()).isEqualTo(connectionName);
                assertThat(connection.getResponseClient()).isEmpty();
                assertThat(connection.getSenderClient()).isEqualTo(mockSenderClient);
            });
        });
    }

    @Test
    void shouldReturnStandardConnection() {
        Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
        String connectionName = randomString();
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setConnectionMode(ConnectionMode.STANDARD);
        connectionMap.put(connectionName, smsc);
        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        DefaultSenderClient defaultSenderClient = mock(DefaultSenderClient.class);
        DefaultResponseClient defaultResponseClient = mock(DefaultResponseClient.class);

        when(clientFactory.standardSender(connectionName, defaults, smsc, typeOfAddressParser)).thenReturn(
                defaultSenderClient
        );
        when(clientFactory.standardResponse(connectionName, defaults, smsc)).thenReturn(
                defaultResponseClient
        );
        when(smppProperties.getConnections()).thenReturn(connectionMap);
        when(smppProperties.getDefaults()).thenReturn(defaults);

        List<SmscConnection> connections = smscConnectionFactoryBean.getObject();

        verify(defaultSenderClient, times(1)).setup();
        verify(defaultResponseClient, times(1)).setup(any(ResponseSmppSessionHandler.class));

        assertThat(connections).satisfies(smscConnections -> {
            assertThat(smscConnections.size()).isEqualTo(1);
            assertThat(smscConnections.get(0)).satisfies(connection -> {
                assertThat(connection.getName()).isEqualTo(connectionName);
                assertThat(connection.getResponseClient().get()).isEqualTo(defaultResponseClient);
                assertThat(connection.getSenderClient()).isInstanceOf(DefaultSenderClient.class);
                assertThat(connection.getSenderClient()).isEqualTo(defaultSenderClient);
            });
        });
    }

    @Test
    void shouldReturnTestConnection() {
        Map<String, SmppProperties.SMSC> connectionMap = new HashMap<>();
        String connectionName = randomString();
        SmppProperties.SMSC smsc = new SmppProperties.SMSC();
        smsc.setConnectionMode(ConnectionMode.TEST);
        connectionMap.put(connectionName, smsc);
        SmppProperties.Defaults defaults = new SmppProperties.Defaults();
        DefaultSenderClient defaultSenderClient = mock(DefaultSenderClient.class);
        DefaultResponseClient defaultResponseClient = mock(DefaultResponseClient.class);
        TestSenderClient testSenderClient = new TestSenderClient(defaultSenderClient, Collections.emptyList(), smppResultGenerator);

        when(clientFactory.standardSender(connectionName, defaults, smsc, typeOfAddressParser)).thenReturn(
                defaultSenderClient
        );
        when(clientFactory.standardResponse(connectionName, defaults, smsc)).thenReturn(
                defaultResponseClient
        );
        when(clientFactory.testSender(defaultSenderClient, defaults, smsc, smppResultGenerator)).thenReturn(
                testSenderClient
        );
        when(smppProperties.getConnections()).thenReturn(connectionMap);
        when(smppProperties.getDefaults()).thenReturn(defaults);

        List<SmscConnection> connections = smscConnectionFactoryBean.getObject();

        verify(defaultSenderClient, times(1)).setup();
        verify(defaultResponseClient, times(1)).setup(any(ResponseSmppSessionHandler.class));

        assertThat(connections).satisfies(smscConnections -> {
            assertThat(smscConnections.size()).isEqualTo(1);
            assertThat(smscConnections.get(0)).satisfies(connection -> {
                assertThat(connection.getName()).isEqualTo(connectionName);
                assertThat(connection.getResponseClient().get()).isEqualTo(defaultResponseClient);
                assertThat(connection.getSenderClient()).isInstanceOf(TestSenderClient.class);
                assertThat(connection.getSenderClient()).isEqualTo(testSenderClient);
            });
        });
    }
}
