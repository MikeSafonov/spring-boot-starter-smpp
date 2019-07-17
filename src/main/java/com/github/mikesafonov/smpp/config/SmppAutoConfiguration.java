package com.github.mikesafonov.smpp.config;

import com.github.mikesafonov.smpp.api.RoundRobinIndexDetectionStrategy;
import com.github.mikesafonov.smpp.api.SenderManager;
import com.github.mikesafonov.smpp.api.StrategySenderManager;
import com.github.mikesafonov.smpp.core.clients.AlwaysSuccessSmppResultGenerator;
import com.github.mikesafonov.smpp.core.clients.NullDeliveryReportConsumer;
import com.github.mikesafonov.smpp.core.clients.SmppResultGenerator;
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
    @ConditionalOnMissingBean(DeliveryReportConsumer.class)
    public DeliveryReportConsumer nullDeliveryReportConsumer() {
        return new NullDeliveryReportConsumer();
    }

    @Bean
    public SmscConnectionFactoryBean senderClientFactoryBean(SmppProperties smppProperties, SmppResultGenerator smppResultGenerator,
                                                             TypeOfAddressParser typeOfAddressParser,
                                                             DeliveryReportConsumer deliveryReportConsumer) {
        return new SmscConnectionFactoryBean(smppProperties, smppResultGenerator, deliveryReportConsumer, typeOfAddressParser);
    }

    @Bean
    @ConditionalOnMissingBean(SmppResultGenerator.class)
    public SmppResultGenerator alwaysSuccessSmppResultGenerator() {
        return new AlwaysSuccessSmppResultGenerator();
    }

    @Bean
    @ConditionalOnMissingBean(SenderManager.class)
    public SenderManager roundRobinSenderManager(List<SmscConnection> smscConnections) {
        return new StrategySenderManager(smscConnections, new RoundRobinIndexDetectionStrategy());
    }
}
