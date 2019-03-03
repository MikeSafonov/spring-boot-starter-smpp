package com.github.mikesafonov.starter;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Safonov
 */
@Data
@ConfigurationProperties(prefix = "spring.smpp")
public class SmppProperties {

    private Map<String, SMSC> connections = new HashMap<>();

    @Data
    public static class SMSC {
        /**
         * SMSC host
         */
        @NotBlank
        private String host;
        /**
         * SMSC port
         */
        private int port;
        /**
         * SMSC username
         */
        @NotBlank
        private String username;
        /**
         * SMSC password
         */
        @NotBlank
        private String password;
        /**
         * using ucs2 only
         */
        private boolean ucs2Only = false;
        /**
         * Number of attempts to reconnect if smpp session closed
         */
        private int maxTry = 5;
        /**
         * Mode of client
         */
        private StarterMode starterMode = StarterMode.STANDARD;
        /**
         * Smpp connection window size
         */
        private int windowSize = 90;
        /**
         * Is logging smpp pdu
         */
        private boolean loggingPdu = false;
        /**
         * Is logging smpp bytes
         */
        private boolean loggingBytes = false;
        /**
         * Rebind period
         */
        private Duration rebindPeriod = Duration.ofSeconds(90);

        /**
         * Request timeout
         */
        private Duration requestTimeout = Duration.ofSeconds(5);
        /**
         * Array of phones to send. Using only if {@link #starterMode} is {@link StarterMode#TEST}
         */
        private String[] allowedPhones;
    }
}
