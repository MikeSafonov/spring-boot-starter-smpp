package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.clients.AlwaysSuccessSmppResultGenerator;
import com.github.mikesafonov.starter.clients.SmppResultGenerator;
import com.github.mikesafonov.starter.smpp.sender.*;
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
    public SmscConnectionFactoryBean senderClientFactoryBean(SmppProperties smppProperties){
        return new SmscConnectionFactoryBean(smppProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SmppResultGenerator.class)
    public SmppResultGenerator alwaysSuccessSmppResultGenerator() {
        return new AlwaysSuccessSmppResultGenerator();
    }

    @Bean
    public AddressBuilder addressBuilder(TypeOfAddressParser typeOfAddressParser) {
        return new AddressBuilder(typeOfAddressParser);
    }

    @Bean
    public MessageBuilder messageBuilder(AddressBuilder addressBuilder) {
        return new MessageBuilder(addressBuilder);
    }
}
