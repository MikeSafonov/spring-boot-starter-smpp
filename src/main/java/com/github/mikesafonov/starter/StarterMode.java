package com.github.mikesafonov.starter;

/**
 * @author Mike Safonov
 */
public enum StarterMode {
    STANDARD,
    TEST,
    MOCK;

    public static StarterMode from(String mode) {
        if (mode == null) {
            throw new RuntimeException("Mode must not be null");
        }

        if (mode.equalsIgnoreCase(MOCK.toString())) {
            return MOCK;
        }
        if (mode.equalsIgnoreCase(TEST.toString())) {
            return TEST;
        }

        if (mode.equalsIgnoreCase(STANDARD.toString())) {
            return STANDARD;
        }

        throw new RuntimeException("Unknown mode: " + mode);
    }

}
