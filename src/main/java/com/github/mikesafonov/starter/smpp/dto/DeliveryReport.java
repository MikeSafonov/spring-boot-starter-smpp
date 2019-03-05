package com.github.mikesafonov.starter.smpp.dto;

import com.cloudhopper.smpp.util.DeliveryReceipt;
import lombok.Data;

import java.time.ZonedDateTime;

import static com.github.mikesafonov.starter.smpp.utils.JodaJavaConverter.convert;

/**
 * @author Mike Safonov
 */
@Data
public class DeliveryReport {

    private String messageId;
    private ZonedDateTime deliveryDate;
    private int deliveryCount;
    private int submitCount;
    private int error;
    private int state;

    public static DeliveryReport of(final DeliveryReceipt deliveryReceipt){
        DeliveryReport deliveryReport = new DeliveryReport();
        deliveryReport.setDeliveryCount(deliveryReceipt.getDeliveredCount());
        deliveryReport.setMessageId(deliveryReceipt.getMessageId());
        deliveryReport.setError(deliveryReceipt.getErrorCode());
        deliveryReport.setState(deliveryReceipt.getState());
        deliveryReport.setSubmitCount(deliveryReceipt.getSubmitCount());
        deliveryReport.setDeliveryDate(convert(deliveryReceipt.getDoneDate()));
        return deliveryReport;
    }
}
