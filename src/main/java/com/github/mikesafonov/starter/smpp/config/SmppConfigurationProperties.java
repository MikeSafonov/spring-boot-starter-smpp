package com.github.mikesafonov.starter.smpp.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Properties of SMPP connection
 *
 * @author Mike Safonov
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmppConfigurationProperties {

    @NotBlank
    private String host;
    private int port;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private int windowSize;
    private boolean ucs2Only = false;
    private boolean loggingPdu = false;
    private boolean loggingBytes = false;


    public SmppConfigurationProperties(String host, int port, String username, String password, int windowSize, boolean ucs2Only) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.windowSize = windowSize;
        this.ucs2Only = ucs2Only;
    }
}
