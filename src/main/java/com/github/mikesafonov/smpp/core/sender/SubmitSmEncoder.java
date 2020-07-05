package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.smpp.pdu.SubmitSm;
import com.github.mikesafonov.smpp.core.dto.Message;

import javax.validation.constraints.NotNull;

/**
 * This interface represents class for detecting message data coding and encoding message
 * text to necessary coding.
 *
 * @author Mike Safonov
 */
public interface SubmitSmEncoder {

    void encode(@NotNull Message message, @NotNull SubmitSm submitSm, boolean ucs2Only);
}
