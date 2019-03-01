package com.github.mikesafonov.starter.smpp.reciever;


import com.github.mikesafonov.starter.smpp.dto.DeliveryReport;

import java.util.function.Consumer;

/**
 * This class dedicated to handle {@link DeliveryReport} on client side.
 *
 * @author Mike Safonov
 */
public interface DeliveryReportConsumer extends Consumer<DeliveryReport> {
}
