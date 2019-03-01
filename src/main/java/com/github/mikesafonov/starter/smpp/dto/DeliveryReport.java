package com.github.mikesafonov.starter.smpp.dto;

import com.cloudhopper.smpp.util.DeliveryReceipt;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

/**
 * @author Mike Safonov
 */
@Data
@ToString
public class DeliveryReport {

    private String messageId;
    private LocalDate deliveryDate;
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
        deliveryReport.setDeliveryDate(jodaToJava8(deliveryReceipt.getDoneDate().toLocalDate()));
        return deliveryReport;
    }

    private static LocalDate jodaToJava8(org.joda.time.LocalDate joda) {
        return LocalDate.of(joda.getYear(), joda.getMonthOfYear(), joda.getDayOfMonth());
    }



}
