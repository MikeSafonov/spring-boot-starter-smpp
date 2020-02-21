package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSession;

/**
 * Manager class for smpp connection.
 */
public interface ConnectionManager {
    /**
     * Get current or open new smpp connection session
     *
     * @return smpp session
     */
    SmppSession getSession();

    /**
     * Closes current smpp connection session
     */
    void closeSession();

    /**
     * @return smpp connection session configuration
     */
    BaseSmppSessionConfiguration getConfiguration();

    /**
     * Closes current smpp connection, destroy smpp client
     */
    void destroy();
}
