package com.github.mikesafonov.smpp.core;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.github.mikesafonov.smpp.core.utils.CountWithEncoding;
import com.github.mikesafonov.smpp.core.utils.MessageUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Mike Safonov
 */
class MessageUtilTest {


    @Test
    void encodingTest() {

        testLatinRegular();
        testLatinMultipart();

        testLatinOnlyUcs2();

        testLatinAndCyrillicSymbol();
        testCyrillicRegular();
        testCyrillicMultipart();

    }

    private void testLatinRegular() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MessageUtil.GSM_7_REGULAR_MESSAGE_LENGTH; i++) {
            builder.append('W');
        }

        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString());
        assertEquals(1, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_GSM, countWithEncoding.getCharset());
    }

    private void testLatinMultipart() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 2 * MessageUtil.GSM_7_MULTIPART_MESSAGE_LENGTH + 1; i++) {
            builder.append('W');
        }

        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString());
        assertEquals(3, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_GSM, countWithEncoding.getCharset());
    }

    private void testLatinOnlyUcs2() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MessageUtil.UCS_2_REGULAR_MESSAGE_LENGTH + 1; i++) {
            builder.append('W');
        }

        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString(), true);
        assertEquals(2, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_UCS_2, countWithEncoding.getCharset());
    }

    private void testLatinAndCyrillicSymbol() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MessageUtil.UCS_2_REGULAR_MESSAGE_LENGTH - 1; i++) {
            builder.append('W');
        }

        builder.append('А');

        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString());
        assertEquals(1, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_UCS_2, countWithEncoding.getCharset());

    }

    private void testCyrillicRegular() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MessageUtil.UCS_2_REGULAR_MESSAGE_LENGTH; i++) {
            builder.append('А');
        }


        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString());
        assertEquals(1, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_UCS_2, countWithEncoding.getCharset());
    }


    private void testCyrillicMultipart() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 2 * MessageUtil.UCS_2_MULTIPART_MESSAGE_LENGTH + 1; i++) {
            builder.append('А');
        }


        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString());
        assertEquals(3, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_UCS_2, countWithEncoding.getCharset());
    }


}
