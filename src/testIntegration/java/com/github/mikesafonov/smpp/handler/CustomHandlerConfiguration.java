package com.github.mikesafonov.smpp.handler;

import com.cloudhopper.smpp.SmppSessionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Mike Safonov
 */
@Configuration
public class CustomHandlerConfiguration {

    @Bean
    public SmppSessionListener listener() {
        return new SmppSessionListenerImpl();
    }

}
