package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.tlv.Tlv;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.utils.CountWithEncoding;
import com.github.mikesafonov.smpp.core.utils.MessageUtil;
import lombok.SneakyThrows;

import javax.validation.constraints.NotNull;

/**
 * Encoder for simple/datagram messages
 *
 * @author Mike Safonov
 */
public class SimpleSubmitSmEncoder implements SubmitSmEncoder {

    @Override
    @SneakyThrows
    public void encode(Message message, SubmitSm submitSm, boolean ucs2Only) {
        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(message.getText(), ucs2Only);
        byte coding = findCoding(countWithEncoding.getCharset());
        submitSm.setDataCoding(coding);
        byte[] messageByte = CharsetUtil.encode(message.getText(), countWithEncoding.getCharset());
        if (countWithEncoding.getCount() > 1) {
            submitSm.setShortMessage(new byte[0]);
            submitSm.addOptionalParameter(new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, messageByte));
        } else {
            submitSm.setShortMessage(messageByte);
        }
    }

    private byte findCoding(@NotNull Charset charset) {
        return (charset == CharsetUtil.CHARSET_GSM) ? SmppConstants.DATA_CODING_DEFAULT :
                SmppConstants.DATA_CODING_UCS2;
    }
}
