package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.clients.MockSenderClient;
import com.github.mikesafonov.starter.clients.SmppResultGenerator;
import com.github.mikesafonov.starter.clients.TestSenderClient;
import com.github.mikesafonov.starter.smpp.config.SmppConfigurationProperties;
import com.github.mikesafonov.starter.smpp.config.TransmitterConfiguration;
import com.github.mikesafonov.starter.smpp.reciever.DefaultResponseClient;
import com.github.mikesafonov.starter.smpp.sender.DefaultSenderClient;
import com.github.mikesafonov.starter.smpp.sender.MessageBuilder;
import com.github.mikesafonov.starter.smpp.sender.SenderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class SmscConnectionFactoryBean implements FactoryBean<List<SmscConnection>> {

    private final SmppProperties smppProperties;
    private final SmppResultGenerator smppResultGenerator;
    private final MessageBuilder messageBuilder;


    @Override
    public List<SmscConnection> getObject() throws Exception {

        List<SmscConnection> smscConnections = new ArrayList<>();
        smppProperties.getConnections().forEach((name, smsc) -> {
            StarterMode starterMode = smsc.getStarterMode();

            switch (starterMode) {
                case MOCK: {
                    MockSenderClient senderClient = new MockSenderClient(smppResultGenerator, name);
                    smscConnections.add(new SmscConnection(name, null, senderClient));
                    break;
                }
                case TEST: {
                    TransmitterConfiguration transmitterConfiguration = new TransmitterConfiguration(
                            smsc.getHost(), smsc.getPort(), smsc.getUsername(), smsc.getPassword(),
                            smsc.getWindowSize(), smsc.isLoggingBytes(), smsc.isLoggingPdu()
                    );
                    SenderClient senderClient = DefaultSenderClient.of(transmitterConfiguration, smsc.getMaxTry(),
                            smsc.isUcs2Only(), smsc.getRebindPeriod().getSeconds(), messageBuilder, name);
                    TestSenderClient testSenderClient = new TestSenderClient(senderClient, Arrays.asList(smsc.getAllowedPhones()), smppResultGenerator);


                    DefaultResponseClient responseClient = DefaultResponseClient.of();

                    smscConnections.add(new SmscConnection(name, responseClient, testSenderClient));
                }
                case STANDARD: {

                }
                default: {
                }
            }


            if (starterMode == StarterMode.MOCK) {
                MockSenderClient senderClient = new MockSenderClient(smppResultGenerator, name);
                smscConnections.add(new SmscConnection(name, null, senderClient));
            }

        });


        //TODO: implements sender client registry.
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return List.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
