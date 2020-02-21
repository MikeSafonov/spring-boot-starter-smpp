package com.github.mikesafonov.smpp.core.connection;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.github.mikesafonov.smpp.core.exceptions.SmppSessionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSenderConnectionManager implements ConnectionManager {
    protected final DefaultSmppClient client;
    protected final BaseSmppSessionConfiguration configuration;
    /**
     * Number of attempts to reconnect if smpp session closed
     */
    protected final int maxTryCount;
    protected SmppSession session;


    @Override
    public SmppSession getSession() {
        checkSession();
        return session;
    }

    @Override
    public void closeSession() {
        if (session != null) {
            session.close();
            session.destroy();
            session = null;
        }
    }

    @Override
    public BaseSmppSessionConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void destroy() {
        closeSession();
        client.destroy();
    }

    /**
     * Checking smpp session state. If session is null - creating new session using method {@link #bind()}.
     * Otherwise checking bound session state by method {@link #checkBoundState()}.
     *
     * @throws SmppSessionException if session not connected
     */
    private void checkSession() {
        boolean connectionAlive = false;
        int tryCount = 0;
        while (!connectionAlive && tryCount < maxTryCount) {
            if (session == null) {
                connectionAlive = bind();
            } else {
                log.debug("Session state is " + session.getStateName() + " bound: " + session.isBound());
                connectionAlive = checkBoundState();
            }
            tryCount++;
        }

        if (!connectionAlive) {
            throw new SmppSessionException();
        }
    }

    /**
     * Binding new smpp session
     *
     * @return true - if binding was successfully, false - otherwise
     * @see DefaultSmppClient#bind(SmppSessionConfiguration)
     */
    protected abstract boolean bind();

    /**
     * Check is {@link #session} in bound state. <br>
     * <p>
     * If <b>true</b>, then sending ping command. If ping fails then trying to reconnect.<br>
     * If <b>false</b>, check is smpp session in binding state then method return false, otherwise try to reconnect session.
     * <p>
     *
     * @return true if session in bound state and ping/reconnection success.
     * @see #pingOrReconnect()
     * @see #isBindingOrReconnect()
     */
    private boolean checkBoundState() {
        if (session.isBound()) {
            return pingOrReconnect();
        } else {
            return isBindingOrReconnect();
        }
    }

    /**
     * Check is smpp session in `binding` state. Reconnect session if session not in `binding` state
     *
     * @return is {@link #session} in binding state - return false, otherwise returns result of {@link #reconnect()} method
     */
    private boolean isBindingOrReconnect() {
        if (session.isBinding()) {
            sleep(50);
            return false;
        } else {
            return reconnect();
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
            // ignore
        }
    }

    /**
     * Send ping command. If ping return false - then try to reconnect.
     *
     * @return if ping or reconnection was successfully
     * @see #pingConnection()
     * @see #reconnect()
     */
    private boolean pingOrReconnect() {
        return pingConnection() || reconnect();
    }

    /**
     * Sending test request {@link EnquireLink}.
     *
     * @return if request was successfully
     */
    private boolean pingConnection() {
        try {
            session.enquireLink(new EnquireLink(), 1000);
            return true;
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * Closing existing smpp session and create new connection.
     *
     * @return true if reconnection success, false - otherwise
     * @see #closeSession()
     * @see #bind()
     */
    private boolean reconnect() {
        closeSession();
        return bind();
    }
}
