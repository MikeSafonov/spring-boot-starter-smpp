package com.github.mikesafonov.smpp.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@ConfigurationProperties(prefix = "smpp")
public class SmppProperties {

    private Defaults defaults = new Defaults();
    private Map<String, SMSC> connections = new HashMap<>();
    private boolean setupRightAway = true;

    @Data
    public static class Defaults {
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
        private ConnectionMode connectionMode = ConnectionMode.STANDARD;
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
         * Array of phones to send. Using only if {@link #connectionMode} is {@link ConnectionMode#TEST}
         */
        private String[] allowedPhones = new String[0];

        /**
         * Type of smpp connection
         */
        private ConnectionType connectionType = ConnectionType.TRANSMITTER_RECEIVER;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Credentials {
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
    }

    @Data
    public static class SMSC {
        /**
         * SMSC connection credentials
         */
        @NotNull
        private Credentials credentials;
        /**
         * using ucs2 only
         */
        private Boolean ucs2Only;
        /**
         * Number of attempts to reconnect if smpp session closed
         */
        private Integer maxTry;
        /**
         * Mode of client
         */
        private ConnectionMode connectionMode;
        /**
         * Smpp connection window size
         */
        private Integer windowSize;
        /**
         * Is logging smpp pdu
         */
        private Boolean loggingPdu;
        /**
         * Is logging smpp bytes
         */
        private Boolean loggingBytes;
        /**
         * Rebind period
         */
        private Duration rebindPeriod;

        /**
         * Request timeout
         */
        private Duration requestTimeout;
        /**
         * Array of phones to send. Using only if {@link #connectionMode} is {@link ConnectionMode#TEST}
         */
        private String[] allowedPhones;
        /**
         * Type of smpp connection
         */
        private ConnectionType connectionType;
        /**
         * The systemType parameter is used to categorize the type of ESME that is binding to the SMSC.
         * Examples include “VMS” (voice mail system) and “OTA” (over-the-air activation system).
         * Specification of the systemType is optional - some SMSC’s may not require ESME’s to provide
         * this detail. In this case, the ESME can set the systevType to NULL.
         */
        private String systemType;
    }
}
