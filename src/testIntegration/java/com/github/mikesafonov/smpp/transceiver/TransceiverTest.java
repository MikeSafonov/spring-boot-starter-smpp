package com.github.mikesafonov.smpp.transceiver;

import com.github.mikesafonov.smpp.api.SenderManager;
import com.github.mikesafonov.smpp.assertj.SmppAssertions;
import com.github.mikesafonov.smpp.config.SmppAutoConfiguration;
import com.github.mikesafonov.smpp.core.dto.DeliveryReport;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageResponse;
import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;
import com.github.mikesafonov.smpp.server.MockSmppServer;
import com.github.mikesafonov.smpp.server.MockSmppServerHolder;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * @author Mike Safonov
 */
@ActiveProfiles("transceiver")
@SpringBootTest(classes = {TransceiverConfiguration.class, SmppAutoConfiguration.class})
@TestPropertySource(
    locations = "classpath:application-transceiver.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransceiverTest {
    @Autowired
    private SenderManager senderManager;

    @Autowired
    private DeliveryReportConsumer deliveryReportConsumer;

    @Autowired
    private MockSmppServerHolder smppServerHolder;
    private Message message;

    @BeforeEach
    void clearAll() {
        smppServerHolder.clearAll();
        message = Message.simple("my message")
            .from("3322")
            .to("2233")
            .build();
    }

    @AfterAll
    void stopAll() {
        smppServerHolder.stopAll();
    }

    @Test
    void shouldOpenOneConnection() {
        MockSmppServer smppServer = smppServerHolder.getByName("one").get();
        assertThat(smppServer).extracting("handler.sessions.size").isEqualTo(1);
    }

    @Test
    void shouldSendMessage() {
        senderManager.getByName("one").send(message);
        SmppAssertions.assertThat(smppServerHolder).serverByName("one").hasSingleMessage()
            .hasDest("2233")
            .hasSource("3322")
            .hasText("my message")
            .hasDeliveryReport();
    }

    @Test
    void shouldReceiveDeliveryReport() {
        MessageResponse response = senderManager.getByName("one").send(message);
        ArgumentCaptor<DeliveryReport> captor = ArgumentCaptor.forClass(DeliveryReport.class);

        await().atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> verify(deliveryReportConsumer).accept(captor.capture()));

        DeliveryReport report = captor.getValue();

        assertEquals(response.getSmscMessageID(), report.getMessageId());
        assertEquals("one", report.getResponseClientId());
    }
}
