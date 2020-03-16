package com.github.mikesafonov.smpp.roundrobin;

import com.github.mikesafonov.smpp.config.SmppAutoConfiguration;
import com.github.mikesafonov.smpp.server.MockSmppServerHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.github.mikesafonov.smpp.assertj.SmppAssertions.assertThat;


@ActiveProfiles("robin")
@SpringBootTest(classes = {RoundRobinApplicationConfiguration.class, SmppAutoConfiguration.class})
@TestPropertySource(
    locations = "classpath:application-robin.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoundRobinSmppTest {
    @Autowired
    private RoundRobinApplicationService roundRobinApplicationService;

    @Autowired
    private MockSmppServerHolder smppServerHolder;

    @BeforeEach
    void clearAll() {
        smppServerHolder.clearAll();
    }

    @AfterAll
    void stopAll() {
        smppServerHolder.stopAll();
    }

    @Test
    void shouldOpenTwoConnection(){
        org.assertj.core.api.Assertions.assertThat(smppServerHolder.getByName("one").get())
            .extracting("handler.sessions.size").isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(smppServerHolder.getByName("two").get())
            .extracting("handler.sessions.size").isEqualTo(2);
    }

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
