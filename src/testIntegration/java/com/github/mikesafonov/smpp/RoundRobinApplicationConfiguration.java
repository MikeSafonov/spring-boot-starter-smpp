package com.github.mikesafonov.smpp;

import com.github.mikesafonov.smpp.api.SenderManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RoundRobinApplicationConfiguration {
    @Bean
    public RoundRobinApplicationService roundRobinApplicationService(SenderManager senderManager) {
        return new RoundRobinApplicationService(senderManager);
    }
}
