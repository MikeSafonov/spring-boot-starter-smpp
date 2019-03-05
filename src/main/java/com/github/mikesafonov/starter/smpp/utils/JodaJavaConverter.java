package com.github.mikesafonov.starter.smpp.utils;

import lombok.experimental.UtilityClass;
import org.joda.time.DateTime;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Mike Safonov
 */
@UtilityClass
public class JodaJavaConverter {

    public static ZonedDateTime convert(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        Instant instant = Instant.ofEpochMilli(dateTime.toInstant().getMillis());
        ZoneId zoneId = ZoneId.of(dateTime.getZone().getID());
        return instant.atZone(zoneId);
    }
}
