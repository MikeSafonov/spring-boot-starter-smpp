package com.github.mikesafonov.smpp.transceiver;

import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

/**
 * @author Mike Safonov
 */
@Configuration
public class TransceiverConfiguration {

    @Bean
    public DeliveryReportConsumer deliveryReportConsumer() {
        return mock(DeliveryReportConsumer.class);
    }

}
