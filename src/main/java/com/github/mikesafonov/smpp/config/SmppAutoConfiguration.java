package com.github.mikesafonov.smpp.config;

import com.github.mikesafonov.smpp.api.RoundRobinIndexDetectionStrategy;
import com.github.mikesafonov.smpp.api.SenderManager;
import com.github.mikesafonov.smpp.api.StrategySenderManager;
import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.connection.ConnectionManagerFactory;
import com.github.mikesafonov.smpp.core.generators.AlwaysSuccessSmppResultGenerator;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;
import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;
import com.github.mikesafonov.smpp.core.sender.DefaultTypeOfAddressParser;
import com.github.mikesafonov.smpp.core.sender.TypeOfAddressParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * @author Mike Safonov
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(SmppProperties.class)
public class SmppAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TypeOfAddressParser.class)
    public TypeOfAddressParser defaultTypeOfAddressParser() {
        return new DefaultTypeOfAddressParser();
    }

    @Bean
    public ClientFactory clientFactory (){
        return new ClientFactory();
    }

    @Bean
    public ConnectionManagerFactory connectionManagerFactory(){return new ConnectionManagerFactory();}

    @Bean
    public SmscConnectionFactoryBean senderClientFactoryBean(SmppProperties smppProperties,
                                                             SmppResultGenerator smppResultGenerator,
                                                             TypeOfAddressParser typeOfAddressParser,
                                                             List<DeliveryReportConsumer> deliveryReportConsumers,
                                                             ClientFactory clientFactory,
                                                             ConnectionManagerFactory connectionManagerFactory) {
        return new SmscConnectionFactoryBean(smppProperties, smppResultGenerator, deliveryReportConsumers,
                typeOfAddressParser, clientFactory, connectionManagerFactory);
    }

    @Bean
    @ConditionalOnMissingBean(SmppResultGenerator.class)
    public SmppResultGenerator alwaysSuccessSmppResultGenerator() {
        return new AlwaysSuccessSmppResultGenerator();
    }

    @Bean
    @ConditionalOnMissingBean(SenderManager.class)
    public SenderManager roundRobinSenderManager(SmscConnectionsHolder smscConnections) {
        return new StrategySenderManager(smscConnections.getConnections(), new RoundRobinIndexDetectionStrategy());
    }
}
