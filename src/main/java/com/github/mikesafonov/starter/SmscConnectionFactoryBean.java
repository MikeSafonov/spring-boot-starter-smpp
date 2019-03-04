package com.github.mikesafonov.starter;

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
    public List<SmscConnection> getObject() throws Exception {

        List<SmscConnection> smscConnections = new ArrayList<>();
        smppProperties.getConnections().forEach((name, smsc) -> {
            StarterMode starterMode = smsc.getStarterMode();

            switch (starterMode) {
                case MOCK: {
                    smscConnections.add(new SmscConnection(name, ClientFactory.mockSender(name, smppResultGenerator)));
                    break;
                }
                case TEST: {
                    SenderClient senderClient = ClientFactory.defaultSender(name, smsc, typeOfAddressParser);
                    SenderClient testSenderClient = ClientFactory.testSender(senderClient, smppResultGenerator, smsc);
                    ResponseClient responseClient = ClientFactory.defaultResponse(name, smsc);
                    setupClients(senderClient, responseClient);
                    smscConnections.add(new SmscConnection(name, responseClient, testSenderClient));
                }
                case STANDARD: {
                    SenderClient senderClient = ClientFactory.defaultSender(name, smsc, typeOfAddressParser);
                    ResponseClient responseClient = ClientFactory.defaultResponse(name, smsc);
                    setupClients(senderClient, responseClient);
                    smscConnections.add(new SmscConnection(name, responseClient, senderClient));
                }
            }
        });

        return smscConnections;
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
