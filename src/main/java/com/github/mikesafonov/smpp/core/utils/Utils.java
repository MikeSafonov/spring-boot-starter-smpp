package com.github.mikesafonov.smpp.core.utils;

import com.github.mikesafonov.smpp.core.exceptions.ClientNameSmppException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {
    /**
     * Check {@code name} not null and not blank
     *
     * @param name client name
     * @throws ClientNameSmppException if name null or blank
     */
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ClientNameSmppException("Name must not be empty!");
        }
    }

    public static <T> T getOrDefault(T obj, T defaultObj) {
        if (obj == null) {
            return defaultObj;
        }
        return obj;
    }
}
