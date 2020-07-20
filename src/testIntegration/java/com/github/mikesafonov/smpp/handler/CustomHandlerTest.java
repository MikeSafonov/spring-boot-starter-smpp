package com.github.mikesafonov.smpp.handler;

import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.Pdu;
import com.cloudhopper.smpp.util.DeliveryReceipt;
import com.github.mikesafonov.smpp.api.SenderManager;
import com.github.mikesafonov.smpp.config.SmppAutoConfiguration;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageResponse;
import com.github.mikesafonov.smpp.server.MockSmppServerHolder;
import lombok.SneakyThrows;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Mike Safonov
 */
@ActiveProfiles("handler")
@SpringBootTest(classes = {CustomHandlerConfiguration.class, SmppAutoConfiguration.class})
@TestPropertySource(
    locations = "classpath:application-handler.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomHandlerTest {

    @Autowired
    private SmppSessionListenerImpl smppSessionListener;

    @Autowired
    private SenderManager senderManager;

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
    @SneakyThrows
    void shouldReceiveDeliveryReport() {
        MessageResponse response = senderManager.getByName("one").send(message);

        await().atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> assertFalse(smppSessionListener.getPduList().isEmpty()));

        for (Pdu pdu : smppSessionListener.getPduList()) {
            if (pdu.isRequest() && pdu.getClass() == DeliverSm.class) {
                DeliveryReceipt deliveryReceipt = parseDeliveryReceipt((DeliverSm) pdu);
                assertEquals(response.getSmscMessageID(), deliveryReceipt.getMessageId());
            }
        }
    }

    @SneakyThrows
    private DeliveryReceipt parseDeliveryReceipt(DeliverSm deliverSm) {
        byte[] shortMessage = deliverSm.getShortMessage();
        String sms = new String(shortMessage);
        return DeliveryReceipt.parseShortMessage(sms, DateTimeZone.UTC);
    }

}
