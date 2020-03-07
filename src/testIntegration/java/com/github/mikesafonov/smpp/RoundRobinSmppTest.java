package com.github.mikesafonov.smpp;

import com.github.mikesafonov.smpp.config.SmppAutoConfiguration;
import com.github.mikesafonov.smpp.server.MockSmppServerHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static com.github.mikesafonov.smpp.assertj.SmppAssertions.assertThat;


@SpringBootTest(classes = {RoundRobinApplicationConfiguration.class, SmppAutoConfiguration.class})
@TestPropertySource(
        locations = "classpath:application.properties")
public class RoundRobinSmppTest {
    @Autowired
    private RoundRobinApplicationService roundRobinApplicationService;

    @Autowired
    private MockSmppServerHolder smppServerHolder;

    @Test
    void shouldSendTwoMessages() {
        roundRobinApplicationService.sendMessage("one", "two", "one message");
        roundRobinApplicationService.sendMessage("two", "one", "two message");
        roundRobinApplicationService.sendMessage("one", "three", "three message");

        assertThat(smppServerHolder).serverByName("one").messages()
                .hasSize(2)
                .containsDest("two")
                .containsDest("three")
                .containsSource("one")
                .containsText("one message")
                .containsText("three message");

        assertThat(smppServerHolder).serverByName("two").hasSingleMessage()
                .hasDeliveryReport()
                .hasDest("one")
                .hasSource("two")
                .hasText("two message");
    }
}
