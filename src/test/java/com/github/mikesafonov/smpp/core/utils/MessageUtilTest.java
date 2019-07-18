package com.github.mikesafonov.smpp.core.utils;

import com.cloudhopper.commons.charset.CharsetUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Mike Safonov
 */
class MessageUtilTest {


    private static Stream<String> emptyTextProvider() {
        return Stream.of("", null);
    }

    @Test
    void latinRegular() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MessageUtil.GSM_7_REGULAR_MESSAGE_LENGTH; i++) {
            builder.append('W');
        }

        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString());
        assertEquals(1, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_GSM, countWithEncoding.getCharset());
    }

    @Test
    void latinMultipart() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 2 * MessageUtil.GSM_7_MULTIPART_MESSAGE_LENGTH; i++) {
            builder.append('W');
        }

        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString());
        assertEquals(2, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_GSM, countWithEncoding.getCharset());
    }

    @Test
    void testLatinOnlyUcs2() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MessageUtil.UCS_2_REGULAR_MESSAGE_LENGTH + 1; i++) {
            builder.append('W');
        }

        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString(), true);
        assertEquals(2, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_UCS_2, countWithEncoding.getCharset());
    }

    @Test
    void latinAndCyrillicSymbol() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MessageUtil.UCS_2_REGULAR_MESSAGE_LENGTH - 1; i++) {
            builder.append('W');
        }

        builder.append('А');

        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString());
        assertEquals(1, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_UCS_2, countWithEncoding.getCharset());

    }

    @Test
    void cyrillicRegular() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MessageUtil.UCS_2_REGULAR_MESSAGE_LENGTH; i++) {
            builder.append('А');
        }


        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString());
        assertEquals(1, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_UCS_2, countWithEncoding.getCharset());
    }

    @Test
    void cyrillicMultipart() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 2 * MessageUtil.UCS_2_MULTIPART_MESSAGE_LENGTH + 1; i++) {
            builder.append('А');
        }


        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(builder.toString());
        assertEquals(3, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_UCS_2, countWithEncoding.getCharset());
    }

    @ParameterizedTest
    @MethodSource("emptyTextProvider")
    void shouldReturnEmpty(String value) {
        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(value);

        assertEquals(0, countWithEncoding.getCount());
        assertEquals(CharsetUtil.CHARSET_UCS_2, countWithEncoding.getCharset());
    }

}
