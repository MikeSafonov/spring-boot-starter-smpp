package com.github.mikesafonov.smpp.core.dto;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.util.DeliveryReceipt;
import com.cloudhopper.smpp.util.DeliveryReceiptException;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.github.mikesafonov.smpp.util.Randomizer.randomString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeliveryReportTest {
    @Test
    void shouldParseDeliveryReport() throws DeliveryReceiptException {
        String responseClientId = randomString();
        String receipt0 = "id:0123456789 sub:002 dlvrd:001 submit date:1005232039 done date:1005242339 stat:DELIVRD err:012 text:This is a sample mes";
        DeliveryReceipt dlr = DeliveryReceipt.parseShortMessage(receipt0, DateTimeZone.UTC);

        DeliveryReport deliveryReport = DeliveryReport.of(dlr, responseClientId);
        assertEquals(responseClientId, deliveryReport.getResponseClientId());
        assertEquals("0123456789", deliveryReport.getMessageId());
        assertEquals("This is a sample mes", deliveryReport.getText());
        assertEquals(SmppConstants.STATE_DELIVERED, deliveryReport.getState());
        assertEquals(12, deliveryReport.getErrorCode());
        assertEquals(2, deliveryReport.getSubmitCount());
        assertEquals(1, deliveryReport.getDeliveredCount());
        assertEquals(ZonedDateTime.of(LocalDateTime.of(2010, 5, 23, 20, 39, 0), ZoneId.of(DateTimeZone.UTC.getID())),
                deliveryReport.getSubmitDate()
                );
        assertEquals(ZonedDateTime.of(LocalDateTime.of(2010, 5, 24, 23, 39, 0), ZoneId.of(DateTimeZone.UTC.getID())),
                deliveryReport.getDeliveryDate()
        );
    }
}
