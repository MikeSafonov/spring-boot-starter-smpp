package com.github.mikesafonov.smpp.roundrobin;

import com.github.mikesafonov.smpp.api.SenderManager;
import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RoundRobinApplicationConfiguration {
    @Bean
    public RoundRobinApplicationService roundRobinApplicationService(SenderManager senderManager) {
        return new RoundRobinApplicationService(senderManager);
    }

    @Bean
    public DeliveryReportConsumer deliveryReportConsumer(){
        return deliveryReport -> {};
    }
}
