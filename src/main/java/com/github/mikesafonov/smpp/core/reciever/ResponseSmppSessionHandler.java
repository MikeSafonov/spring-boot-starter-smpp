package com.github.mikesafonov.smpp.core.reciever;

import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.util.DeliveryReceipt;
import com.cloudhopper.smpp.util.DeliveryReceiptException;
import com.github.mikesafonov.smpp.core.dto.DeliveryReport;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTimeZone;

import javax.validation.constraints.NotNull;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Handler for listening PDU events (delivery reports, etc)
 *
 * @author Mike Safonov
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ResponseSmppSessionHandler extends DefaultSmppSessionHandler {

    private final String clientId;
    private final List<DeliveryReportConsumer> deliveryReportConsumers;

    public ResponseSmppSessionHandler(String clientId, @NotNull List<DeliveryReportConsumer> deliveryReportConsumers) {
        this.clientId = requireNonNull(clientId);
        this.deliveryReportConsumers = requireNonNull(deliveryReportConsumers);
    }

    @Override
    public PduResponse firePduRequestReceived(PduRequest pduRequest) {

        if (pduRequest != null) {
            if (isDelivery(pduRequest)) {
                return processReport(pduRequest);
            }
            log.debug(pduRequest.toString());
        }
        return super.firePduRequestReceived(pduRequest);
    }

    private boolean isDelivery(PduRequest pduRequest) {
        return pduRequest.isRequest() && pduRequest.getClass() == DeliverSm.class;
    }

    private PduResponse processReport(PduRequest pduRequest) {
        DeliverSm dlr = (DeliverSm) pduRequest;
        try {
            DeliveryReport report = toReport(dlr);
            for (DeliveryReportConsumer deliveryReportConsumer : deliveryReportConsumers) {
                deliveryReportConsumer.accept(report);
            }
        } catch (DeliveryReceiptException e) {
            log.error(e.getMessage(), e);
        }
        return dlr.createResponse();
    }

    private DeliveryReport toReport(DeliverSm deliverSm) throws DeliveryReceiptException {
        byte[] shortMessage = deliverSm.getShortMessage();
        String sms = new String(shortMessage);
        DeliveryReceipt deliveryReceipt = DeliveryReceipt.parseShortMessage(sms, DateTimeZone.UTC);
        return DeliveryReport.of(deliveryReceipt, clientId);
    }
}
