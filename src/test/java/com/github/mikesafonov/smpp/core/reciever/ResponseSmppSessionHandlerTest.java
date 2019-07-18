package com.github.mikesafonov.smpp.core.reciever;

import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.cloudhopper.smpp.util.DeliveryReceipt;
import com.cloudhopper.smpp.util.DeliveryReceiptException;
import com.github.mikesafonov.smpp.core.dto.DeliveryReport;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class ResponseSmppSessionHandlerTest {
    private ResponseClient responseClient;
    private DeliveryReportConsumer deliveryReportConsumer;
    private ResponseSmppSessionHandler responseSmppSessionHandler;

    @BeforeEach
    void setUp() {
        responseClient = mock(ResponseClient.class);
        deliveryReportConsumer = mock(DeliveryReportConsumer.class);
        responseSmppSessionHandler = new ResponseSmppSessionHandler(responseClient, deliveryReportConsumer);
    }

    @Test
    void shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> new ResponseSmppSessionHandler(null, mock(DeliveryReportConsumer.class)));
        assertThrows(NullPointerException.class, () -> new ResponseSmppSessionHandler(mock(ResponseClient.class), null));
    }

    @Test
    void shouldDoNothingBecauseRequestIsNull() {
        responseSmppSessionHandler.firePduRequestReceived(null);

        verifyZeroInteractions(responseClient, deliveryReportConsumer);
    }

    @Test
    void shouldDoNothingBecauseRequestIsNotDeliveryReceipt() {
        EnquireLink enquireLink = new EnquireLink();
        responseSmppSessionHandler.firePduRequestReceived(enquireLink);

        verifyZeroInteractions(responseClient, deliveryReportConsumer);
    }

    @Test
    void shouldHandleRequest() throws SmppInvalidArgumentException, DeliveryReceiptException {
        String dlSms = "id:261BD3E2 sub:001 dlvrd:001 submit date:190305131326 done date:190305131326 stat:DELIVRD err:0 Text:report";
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setShortMessage(dlSms.getBytes());
        DeliveryReport deliveryReport = DeliveryReport.of(DeliveryReceipt.parseShortMessage(dlSms, DateTimeZone.UTC));
        PduResponse pduResponse = responseSmppSessionHandler.firePduRequestReceived(deliverSm);

        verify(responseClient, times(1)).setInProcess(true);
        verify(responseClient, times(1)).setInProcess(false);
        verify(deliveryReportConsumer, times(1)).accept(deliveryReport);

        assertThat(pduResponse.toString()).isEqualTo(deliverSm.createResponse().toString());
    }


}
