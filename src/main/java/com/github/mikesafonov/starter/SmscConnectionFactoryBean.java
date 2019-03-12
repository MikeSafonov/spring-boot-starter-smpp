package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.clients.ClientFactory;
import com.github.mikesafonov.starter.clients.SmppResultGenerator;
import com.github.mikesafonov.starter.smpp.reciever.DeliveryReportConsumer;
import com.github.mikesafonov.starter.smpp.reciever.ResponseClient;
import com.github.mikesafonov.starter.smpp.reciever.ResponseSmppSessionHandler;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import com.github.mikesafonov.starter.smpp.sender.TypeOfAddressParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class SmscConnectionFactoryBean implements FactoryBean<List<SmscConnection>> {

    private final SmppProperties smppProperties;
    private final SmppResultGenerator smppResultGenerator;
    private final DeliveryReportConsumer deliveryReportConsumer;
    private final TypeOfAddressParser typeOfAddressParser;


    @Override
    public List<SmscConnection> getObject() {

        List<SmscConnection> smscConnections = new ArrayList<>();
        SmppProperties.Defaults defaults = smppProperties.getDefaults();
        smppProperties.getConnections().forEach((name, smsc) -> {
            ConnectionMode connectionMode = smsc.getConnectionMode();
            smscConnections.add(getSmscConnection(defaults, name, smsc, connectionMode));
        });

        return smscConnections;
    }

    private SmscConnection getSmscConnection(SmppProperties.Defaults defaults, String name, SmppProperties.SMSC smsc, ConnectionMode connectionMode) {
        switch (connectionMode) {
            case MOCK: {
                return new SmscConnection(name, ClientFactory.mockSender(name, smppResultGenerator));
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

    private SmscConnection getStandardSmscConnection(SmppProperties.Defaults defaults, String name, SmppProperties.SMSC smsc) {
        SenderClient senderClient = ClientFactory.defaultSender(name, defaults, smsc, typeOfAddressParser);
        ResponseClient responseClient = ClientFactory.defaultResponse(name, defaults, smsc);
        setupClients(senderClient, responseClient);
        return new SmscConnection(name, responseClient, senderClient);
    }

    private SmscConnection getTestSmscConnection(SmppProperties.Defaults defaults, String name, SmppProperties.SMSC smsc) {
        SenderClient senderClient = ClientFactory.defaultSender(name, defaults, smsc, typeOfAddressParser);
        SenderClient testSenderClient = ClientFactory.testSender(senderClient, defaults, smppResultGenerator, smsc);
        ResponseClient responseClient = ClientFactory.defaultResponse(name, defaults, smsc);
        setupClients(senderClient, responseClient);
        return new SmscConnection(name, responseClient, testSenderClient);
    }

    private void setupClients(SenderClient senderClient, ResponseClient responseClient) {
        senderClient.setup();
        ResponseSmppSessionHandler responseSmppSessionHandler = new ResponseSmppSessionHandler(responseClient, deliveryReportConsumer);
        responseClient.setup(responseSmppSessionHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return List.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
