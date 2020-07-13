package com.github.mikesafonov.smpp.config;

import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.connection.ConnectionManagerFactory;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;
import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;
import com.github.mikesafonov.smpp.core.reciever.ResponseClient;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import com.github.mikesafonov.smpp.core.sender.TypeOfAddressParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class SmscConnectionFactoryBean implements FactoryBean<SmscConnectionsHolder> {

    private final SmppProperties smppProperties;
    private final SmppResultGenerator smppResultGenerator;
    private final List<DeliveryReportConsumer> deliveryReportConsumers;
    private final TypeOfAddressParser typeOfAddressParser;
    private final ClientFactory clientFactory;
    private final ConnectionManagerFactory connectionManagerFactory;

    @Override
    public SmscConnectionsHolder getObject() {
        SmppProperties.Defaults defaults = smppProperties.getDefaults();
        List<SmscConnection> connections = smppProperties.getConnections().entrySet().stream()
                .map(smsc -> getSmscConnection(defaults, smsc.getKey(), smsc.getValue()))
                .collect(toList());
        return new SmscConnectionsHolder(connections);
    }

    @Override
    public Class<?> getObjectType() {
        return SmscConnectionsHolder.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private SmscConnection getSmscConnection(SmppProperties.Defaults defaults, String name, SmppProperties.SMSC smsc) {
        ConnectionMode connectionMode = getOrDefault(smsc.getConnectionMode(), defaults.getConnectionMode());
        switch (connectionMode) {
            case MOCK: {
                return getMockSmscConnection(name);
            }
            case TEST: {
                return getTestSmscConnection(defaults, name, smsc);
            }
            case STANDARD: {
                return getStandardSmscConnection(defaults, name, smsc);
            }
            default: {
                throw new RuntimeException("Unknown connection mode " + connectionMode);
            }
        }
    }

    private SmscConnection getMockSmscConnection(String name) {
        return new SmscConnection(name, clientFactory.mockSender(name, smppResultGenerator));
    }

    private SmscConnection getTestSmscConnection(SmppProperties.Defaults defaults,
                                                 String name, SmppProperties.SMSC smsc) {
        SenderClient testSenderClient;
        ResponseClient responseClient = null;
        ConnectionType type = getOrDefault(smsc.getConnectionType(), defaults.getConnectionType());
        if (type == ConnectionType.TRANSCEIVER) {
            ConnectionManager transceiver = connectionManagerFactory.transceiver(name, defaults,
                    smsc, deliveryReportConsumers);
            SenderClient standardSender = clientFactory.standardSender(name, defaults, smsc,
                    typeOfAddressParser, transceiver);
            testSenderClient = clientFactory.testSender(standardSender, defaults,
                    smsc, smppResultGenerator);
            if (isResponseClientRequired()) {
                responseClient = clientFactory.standardResponse(name, transceiver);
            }
        } else {
            ConnectionManager transmitter = connectionManagerFactory.transmitter(name, defaults, smsc);
            SenderClient standardSender = clientFactory.standardSender(name, defaults, smsc,
                    typeOfAddressParser, transmitter);
            testSenderClient = clientFactory.testSender(standardSender, defaults,
                    smsc, smppResultGenerator);
            if (isResponseClientRequired()) {
                ConnectionManager receiver = connectionManagerFactory.receiver(name, defaults,
                        smsc, deliveryReportConsumers);
                responseClient = clientFactory.standardResponse(name, receiver);
            }
        }
        setupClients(testSenderClient, responseClient);
        return new SmscConnection(name, responseClient, testSenderClient);
    }

    private SmscConnection getStandardSmscConnection(SmppProperties.Defaults defaults,
                                                     String name, SmppProperties.SMSC smsc) {
        SenderClient senderClient;
        ResponseClient responseClient = null;
        ConnectionType type = getOrDefault(smsc.getConnectionType(), defaults.getConnectionType());
        if (type == ConnectionType.TRANSCEIVER) {
            ConnectionManager connectionManager = connectionManagerFactory.transceiver(name, defaults,
                    smsc, deliveryReportConsumers);
            senderClient = clientFactory.standardSender(name, defaults, smsc,
                    typeOfAddressParser, connectionManager);
            if (isResponseClientRequired()) {
                responseClient = clientFactory.standardResponse(name, connectionManager);
            }
        } else {
            ConnectionManager transmitter = connectionManagerFactory.transmitter(name, defaults, smsc);
            senderClient = clientFactory.standardSender(name, defaults, smsc, typeOfAddressParser, transmitter);
            if (isResponseClientRequired()) {
                ConnectionManager receiver = connectionManagerFactory.receiver(name, defaults,
                        smsc, deliveryReportConsumers);
                responseClient = clientFactory.standardResponse(name, receiver);
            }
        }
        setupClients(senderClient, responseClient);
        return new SmscConnection(name, responseClient, senderClient);
    }

    private void setupClients(SenderClient senderClient, ResponseClient responseClient) {
        if (smppProperties.isSetupRightAway()) {
            senderClient.setup();
            if (responseClient != null) {
                responseClient.setup();
            }
        }
    }

    private boolean isResponseClientRequired() {
        return !deliveryReportConsumers.isEmpty();
    }

    private static <T> T getOrDefault(T value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
