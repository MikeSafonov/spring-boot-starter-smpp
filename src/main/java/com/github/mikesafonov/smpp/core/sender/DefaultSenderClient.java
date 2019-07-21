package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.*;
import com.cloudhopper.smpp.type.*;
import com.github.mikesafonov.smpp.core.dto.*;
import com.github.mikesafonov.smpp.core.sender.exceptions.IllegalAddressException;
import com.github.mikesafonov.smpp.core.sender.exceptions.SenderClientBindException;
import com.github.mikesafonov.smpp.core.sender.exceptions.SmppException;
import com.github.mikesafonov.smpp.core.sender.exceptions.SmppSessionException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link SenderClient}, build on top of {@link DefaultSmppClient} and
 * {@link SmppSession}
 *
 * @author Mike Safonov
 */
@Slf4j
public class DefaultSenderClient implements SenderClient {

    private static final int INVALID_PARAM = 101;
    private static final int INVALID_SENDING_ERROR = 102;
    private static final String SENDER_SUCCESS_BINDED_MESSAGE = "SENDER SUCCESSFUL BINDED";

    /**
     * SMPP client.
     */
    private final DefaultSmppClient client;
    /**
     * SMPP sender configuration
     */
    private final TransmitterConfiguration sessionConfig;
    /**
     * Number of attempts to reconnect if smpp session closed
     */
    private final int maxTryCount;
    /**
     * Request timeout in millis
     */
    private final long timeoutMillis;
    /**
     * Message builder
     */
    private final MessageBuilder messageBuilder;
    private final boolean ucs2Only;
    private boolean inited = false;
    /**
     * SMPP session.
     */
    private SmppSession session;


    public DefaultSenderClient(@NotNull TransmitterConfiguration configuration, @NotNull DefaultSmppClient client, int maxTryCount,
                                  boolean ucs2Only, long timeoutMillis, @NotNull MessageBuilder messageBuilder) {
        this.sessionConfig = requireNonNull(configuration);
        this.messageBuilder = requireNonNull(messageBuilder);
        this.client = requireNonNull(client);
        this.maxTryCount = maxTryCount;
        this.ucs2Only = ucs2Only;
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public @NotNull String getId() {
        return sessionConfig.getName();
    }

    /**
     * Setup connection to SMSC.
     *
     * @throws SenderClientBindException if connection fails
     * @see #checkSession()
     */
    public void setup(){
        if (!inited) {
            try {
                checkSession();
            } catch (SmppSessionException e) {
                log.error(e.getErrorMessage(), e);
                throw new SenderClientBindException(format("Unable to bind with configuration: %s ", sessionConfig.configInformation()));
            }
            inited = true;
        }
    }

    /**
     * Sending message via smpp protocol
     *
     * @param message incoming message
     * @return message response
     */
    @NotNull
    @Override
    public MessageResponse send(@NotNull Message message) {

        requireNonNull(message);
        if (isNullOrEmpty(message.getText())) {
            return MessageResponse.error(message, getId(), new MessageErrorInformation(0, "Empty message text"));
        }

        try {
            SubmitSm submitSm = messageBuilder.createSubmitSm(message, ucs2Only);
            SubmitSmResp send = send(submitSm);
            return analyzeResponse(message, send);
        } catch (IllegalAddressException e) {
            log.error(e.getMessage(), e);
            return MessageResponse.error(message, getId(), new MessageErrorInformation(INVALID_PARAM, e.getMessage()));
        } catch (SmppException e) {
            return MessageResponse.error(message, getId(), new MessageErrorInformation(e.getErrorCode(),
                    e.getErrorMessage()));
        }
    }

    /**
     * Cancel smsc message
     *
     * @param cancelMessage message to cancel
     * @return cancel response
     */
    @Override
    public @NotNull CancelMessageResponse cancel(@NotNull CancelMessage cancelMessage) {

        requireNonNull(cancelMessage);
        if (isNullOrEmpty(cancelMessage.getMessageId())) {
            return CancelMessageResponse.error(cancelMessage, getId(), new MessageErrorInformation(0, "Empty message id"));
        }

        try {
            CancelSm cancelSm = messageBuilder.createCancelSm(cancelMessage);
            WindowFuture<Integer, PduRequest, PduResponse> futureResponse = session.sendRequestPdu(cancelSm, timeoutMillis, true);
            if (futureResponse.await() && futureResponse.isDone() && futureResponse.isSuccess()) {
                return createCancelMessageResponse(cancelMessage, futureResponse);
            }
            return CancelMessageResponse.error(cancelMessage, getId(), new MessageErrorInformation(INVALID_PARAM, "Unable to get response"));
        } catch (RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException | InterruptedException e) {
            log.error(e.getMessage(), e);
            return CancelMessageResponse.error(cancelMessage, getId(), new MessageErrorInformation(INVALID_PARAM, e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CancelMessageResponse.error(cancelMessage, getId(), new MessageErrorInformation(INVALID_SENDING_ERROR, "Unexpected exception"));
        }
    }

    @NotNull
    private CancelMessageResponse createCancelMessageResponse(@NotNull CancelMessage cancelMessage, WindowFuture<Integer, PduRequest, PduResponse> futureResponse) {
        CancelSmResp cancelSmResp = (CancelSmResp) futureResponse.getResponse();
        if (cancelSmResp.getCommandStatus() == SmppConstants.STATUS_OK) {
            return CancelMessageResponse.success(cancelMessage, getId());
        } else {
            return CancelMessageResponse.error(cancelMessage, getId(), new MessageErrorInformation(INVALID_PARAM,
                    cancelSmResp.getResultMessage()));
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }


    /**
     * Sending {@link SubmitSm} command via smpp. First of all checking session state, if session is
     * connected then perform SubmitSm request.
     *
     * @param sm request
     * @return request response
     * @throws SmppException if some exception occurs
     * @see #checkSession()
     */
    private SubmitSmResp send(SubmitSm sm){

        checkSession();

        try {
            return session.submit(sm, timeoutMillis);
        } catch (SmppInvalidArgumentException ex) {
            log.error(ex.getMessage(), ex);
            throw new SmppException(INVALID_PARAM, "Invalid param");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new SmppException(INVALID_SENDING_ERROR, "Cant send message");
        }
    }

    private MessageResponse analyzeResponse(Message message, SubmitSmResp submitSmResp) {
        if (submitSmResp.getCommandStatus() == SmppConstants.STATUS_OK)
            return MessageResponse.success(message, getId(), submitSmResp.getMessageId());
        else
            return MessageResponse.error(message, getId(), new MessageErrorInformation(INVALID_PARAM,
                    submitSmResp.getResultMessage()));
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
        while (!connectionAlive && tryCount <= maxTryCount) {
            if (session == null) {
                connectionAlive = bind();
            } else {
                logSessionConnection(connectionAlive, tryCount);
                connectionAlive = checkBoundState();
            }
            tryCount++;
        }

        if (!connectionAlive) {
            throw new SmppSessionException();
        }
    }

    /**
     * Check is {@link #session} in bound state. <br>
     * <p>
     * If <b>true</b>, then sending ping command. If ping fails then trying to reconnect.<br>
     * If <b>false</b>, check is smpp session in binding state then method return false, otherwise try to reconnect session.
     * <p>
     * Метод для проверки состояния сессии Bound
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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
            // ignore
        }
    }


    /**
     * Debug session connection results
     *
     * @param connectionResult    connection result
     * @param connectionTryNumber numbers of try
     */
    private void logSessionConnection(boolean connectionResult, int connectionTryNumber) {
        log.debug("RESULT = " + connectionResult + " count = " + connectionTryNumber);
        log.debug("BOUND = " + session.isBound());
        log.debug(session.getStateName());
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
     * Binding new smpp session
     *
     * @return true - if binding was successfully, false - otherwise
     * @see DefaultSmppClient#bind(SmppSessionConfiguration)
     */
    private boolean bind() {
        try {
            session = client.bind(sessionConfig);
            log.debug(SENDER_SUCCESS_BINDED_MESSAGE);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * Close and destroy smpp session
     */
    private void closeSession() {
        if (session != null) {
            session.close();
            session.destroy();
            session = null;
        }
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
