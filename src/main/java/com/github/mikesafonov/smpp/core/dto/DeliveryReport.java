package com.github.mikesafonov.smpp.core.dto;

import com.cloudhopper.smpp.util.DeliveryReceipt;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static com.github.mikesafonov.smpp.core.utils.JodaJavaConverter.convert;

/**
 * @author Mike Safonov
 */
@Data
public class DeliveryReport {

    private String messageId;
    private ZonedDateTime deliveryDate;
    private ZonedDateTime submitDate;
    private int deliveredCount;
    private int submitCount;
    private int errorCode;
    private int state;
    private String responseClientId;
    private String text;

    public static DeliveryReport of(@NotNull final DeliveryReceipt deliveryReceipt, @NotNull String responseClientId){
        DeliveryReport deliveryReport = new DeliveryReport();
        deliveryReport.setDeliveredCount(deliveryReceipt.getDeliveredCount());
        deliveryReport.setMessageId(deliveryReceipt.getMessageId());
        deliveryReport.setErrorCode(deliveryReceipt.getErrorCode());
        deliveryReport.setState(deliveryReceipt.getState());
        deliveryReport.setSubmitCount(deliveryReceipt.getSubmitCount());
        deliveryReport.setDeliveryDate(convert(deliveryReceipt.getDoneDate()));
        deliveryReport.setSubmitDate(convert(deliveryReceipt.getSubmitDate()));
        deliveryReport.setResponseClientId(responseClientId);
        deliveryReport.setText(deliveryReceipt.getText());
        return deliveryReport;
    }
}
