package com.github.mikesafonov.smpp.core.utils;

import lombok.experimental.UtilityClass;
import org.joda.time.DateTime;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Mike Safonov
 */
@UtilityClass
public class JodaJavaConverter {

    @Nullable
    public static ZonedDateTime convert(@Nullable DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        Instant instant = Instant.ofEpochMilli(dateTime.toInstant().getMillis());
        ZoneId zoneId = ZoneId.of(dateTime.getZone().getID());
        return instant.atZone(zoneId);
    }
}
