package com.github.mikesafonov.smpp.core.clients;

import com.github.mikesafonov.smpp.core.dto.DeliveryReport;
import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;

/**
 * Implementation of {@link DeliveryReportConsumer} which simple ignore any delivery reports. This is default implementation
 * if any other implementations not presents in application context.
 *
 * @author Mike Safonov
 */
public class NullDeliveryReportConsumer implements DeliveryReportConsumer {
    @Override
    public void accept(DeliveryReport deliveryReport) {
        // ignore delivery report
    }
}
