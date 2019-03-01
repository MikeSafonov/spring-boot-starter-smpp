package com.github.mikesafonov.starter;

import com.github.mikesafonov.starter.smpp.sender.AddressBuilder;
import com.github.mikesafonov.starter.smpp.sender.DefaultTypeOfAddressParser;
import com.github.mikesafonov.starter.smpp.sender.MessageBuilder;
import com.github.mikesafonov.starter.smpp.sender.TypeOfAddressParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mike Safonov
 */
@Configuration
public class SmppAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TypeOfAddressParser.class)
    public TypeOfAddressParser defaultTypeOfAddressParser() {
        return new DefaultTypeOfAddressParser();
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
