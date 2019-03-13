package com.github.mikesafonov.starter.smpp;

import com.github.mikesafonov.starter.ConnectionMode;
import com.github.mikesafonov.starter.SmppProperties;
import com.github.mikesafonov.starter.SmscConnection;
import com.github.mikesafonov.starter.SmscConnectionFactoryBean;
import com.github.mikesafonov.starter.clients.MockSenderClient;
import com.github.mikesafonov.starter.clients.SmppResultGenerator;
import com.github.mikesafonov.starter.smpp.reciever.DeliveryReportConsumer;
import com.github.mikesafonov.starter.smpp.sender.TypeOfAddressParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.mikesafonov.starter.smpp.util.Randomizer.randomString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class SmscConnectionFactoryBeanTest {
    private SmppProperties smppProperties;
    private SmppResultGenerator smppResultGenerator;
    private DeliveryReportConsumer deliveryReportConsumer;
    private TypeOfAddressParser typeOfAddressParser;
    private SmscConnectionFactoryBean smscConnectionFactoryBean;

    @BeforeEach
    void setUp() {
        smppProperties = mock(SmppProperties.class);
        smppResultGenerator = mock(SmppResultGenerator.class);
        deliveryReportConsumer = mock(DeliveryReportConsumer.class);
        typeOfAddressParser = mock(TypeOfAddressParser.class);
        smscConnectionFactoryBean = new SmscConnectionFactoryBean(smppProperties, smppResultGenerator, deliveryReportConsumer, typeOfAddressParser);
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

        when(smppProperties.getConnections()).thenReturn(connectionMap);

        List<SmscConnection> connections = smscConnectionFactoryBean.getObject();

        assertThat(connections).satisfies(smscConnections -> {
            assertThat(smscConnections.size()).isEqualTo(1);
            assertThat(smscConnections.get(0)).satisfies(connection -> {
                assertThat(connection.getName()).isEqualTo(connectionName);
                assertThat(connection.getResponseClient()).isNull();
                assertThat(connection.getSenderClient()).isInstanceOf(MockSenderClient.class);
                assertThat(connection.getSenderClient().getId()).isEqualTo(connectionName);
            });
        });
    }
}
