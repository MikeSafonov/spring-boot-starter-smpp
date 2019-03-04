package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.clients.AlwaysSuccessSmppResultGenerator;
import com.github.mikesafonov.starter.clients.NullDeliveryReportConsumer;
import com.github.mikesafonov.starter.clients.SmppResultGenerator;
import com.github.mikesafonov.starter.smpp.reciever.DeliveryReportConsumer;
import com.github.mikesafonov.starter.smpp.sender.DefaultTypeOfAddressParser;
import com.github.mikesafonov.starter.smpp.sender.TypeOfAddressParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
}
