package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.tlv.Tlv;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.utils.CountWithEncoding;
import com.github.mikesafonov.smpp.core.utils.MessageUtil;
import lombok.SneakyThrows;

/**
 * Encoder for silent message
 *
 * @author Mike Safonov
 */
public class SilentSubmitSmEncoder implements SubmitSmEncoder {
    public static final byte SILENT_CODING = (byte) 0xC0;

    @Override
    @SneakyThrows
    public void encode(Message message, SubmitSm submitSm, boolean ucs2Only) {
        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(message.getText(), ucs2Only);
        submitSm.setDataCoding(SILENT_CODING);
        byte[] messageByte = CharsetUtil.encode(message.getText(), countWithEncoding.getCharset());
        if (countWithEncoding.getCount() > 1) {
            submitSm.setShortMessage(new byte[0]);
            submitSm.addOptionalParameter(new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, messageByte));
        } else {
            submitSm.setShortMessage(messageByte);
        }
    }
}
