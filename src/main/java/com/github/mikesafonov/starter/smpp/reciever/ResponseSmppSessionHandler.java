package com.github.mikesafonov.starter.smpp.reciever;

import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.util.DeliveryReceipt;
import com.cloudhopper.smpp.util.DeliveryReceiptException;
import com.github.mikesafonov.starter.smpp.dto.DeliveryReport;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTimeZone;

import javax.validation.constraints.NotNull;

/**
 * @author Mike Safonov
 */
@Slf4j
public class ResponseSmppSessionHandler extends DefaultSmppSessionHandler {

    private final ResponseClient client;
    private final DeliveryReportConsumer deliveryReportConsumer;

    public ResponseSmppSessionHandler(@NotNull ResponseClient client, @NotNull DeliveryReportConsumer deliveryReportConsumer) {
        this.client = client;
        this.deliveryReportConsumer = deliveryReportConsumer;
    }

    @Override
    public PduResponse firePduRequestReceived(PduRequest pduRequest) {

        if (pduRequest != null) {
            if (isDelivery(pduRequest)) {
                client.setInProcess(true);
                PduResponse response = processReport(pduRequest);
                client.setInProcess(false);
                return response;
            }
            log.debug(pduRequest.toString());
        } else {
            log.debug(null);
        }
        return super.firePduRequestReceived(pduRequest);
    }

    private boolean isDelivery(PduRequest pduRequest) {
        return pduRequest.isRequest() && pduRequest.getClass() == DeliverSm.class;
    }

    private PduResponse processReport(PduRequest pduRequest) {
        DeliverSm dlr = (DeliverSm) pduRequest;
        try {
            deliveryReportConsumer.accept(toReport(dlr));
        } catch (DeliveryReceiptException e) {
            log.error(e.getMessage(), e);
        }
        return dlr.createResponse();
    }

    private DeliveryReport toReport(DeliverSm deliverSm) throws DeliveryReceiptException {
        byte[] shortMessage = deliverSm.getShortMessage();
        String sms = new String(shortMessage);
        DeliveryReceipt deliveryReceipt = DeliveryReceipt.parseShortMessage(sms, DateTimeZone.UTC);
        return DeliveryReport.of(deliveryReceipt);
    }

    /**
     * When channel closed unexpectedly then trying to reconnect using {@link DefaultResponseClient#reconnect()}.
     */
    @Override
    public void fireChannelUnexpectedlyClosed() {
        client.reconnect();
    }

}
