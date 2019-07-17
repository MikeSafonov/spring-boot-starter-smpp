package com.github.mikesafonov.smpp.core;

import com.github.mikesafonov.smpp.core.utils.JodaJavaConverter;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Mike Safonov
 */
class JodaJavaConverterTest {

    @Test
    void shouldConvert(){

        DateTime dateTime = new DateTime(2001, 2, 2, 2, 2, 2, 2);

        ZonedDateTime localDateTime = JodaJavaConverter.convert(dateTime);

        assertEquals(dateTime.getYear(), localDateTime.getYear());
        assertEquals(dateTime.getMonthOfYear(), localDateTime.getMonthValue());
        assertEquals(dateTime.getDayOfMonth(), localDateTime.getDayOfMonth());
        assertEquals(dateTime.getHourOfDay(), localDateTime.getHour());
        assertEquals(dateTime.getMinuteOfHour(), localDateTime.getMinute());
        assertEquals(dateTime.getSecondOfMinute(), localDateTime.getSecond());
        assertEquals(dateTime.getMillisOfSecond(), localDateTime.getNano() / 1000000);
    }
}
