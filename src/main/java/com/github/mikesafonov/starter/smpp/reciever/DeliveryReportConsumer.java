package com.github.mikesafonov.starter.smpp.reciever;


import com.github.mikesafonov.starter.smpp.dto.DeliveryReport;

import java.util.function.Consumer;

/**
 * This class dedicated to handle {@link DeliveryReport} on client side. Starter`s client may build custom logic on receiving
 * delivery report by implementing this interface
 *
 * @author Mike Safonov
 */
public interface DeliveryReportConsumer extends Consumer<DeliveryReport> {
}
