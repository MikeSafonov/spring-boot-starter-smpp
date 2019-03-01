package com.github.mikesafonov.starter.smpp.sender;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.github.mikesafonov.starter.smpp.config.TransmitterConfiguration;
import com.github.mikesafonov.starter.smpp.dto.Message;
import com.github.mikesafonov.starter.smpp.dto.MessageErrorInformation;
import com.github.mikesafonov.starter.smpp.dto.MessageResponse;
import com.github.mikesafonov.starter.smpp.sender.exceptions.IllegalAddressException;
import com.github.mikesafonov.starter.smpp.sender.exceptions.SenderClientBindException;
import com.github.mikesafonov.starter.smpp.sender.exceptions.SmppException;
import com.github.mikesafonov.starter.smpp.sender.exceptions.SmppSessionException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

import java.util.UUID;

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
    private final long timeoutMillis;
    private final MessageBuilder messageBuilder;
    private final boolean ucs2Only;
    /**
     * SMPP session.
     */
    private SmppSession session;
    private final String id;


    protected DefaultSenderClient(@NotNull TransmitterConfiguration configuration, int maxTryCount,
                                  boolean ucs2Only, long timeoutMillis, @NotNull MessageBuilder messageBuilder, @NotNull String id) {
        client = new DefaultSmppClient();
        sessionConfig = requireNonNull(configuration);
        this.maxTryCount = maxTryCount;
        this.ucs2Only = ucs2Only;
        this.messageBuilder = requireNonNull(messageBuilder);
        this.timeoutMillis = timeoutMillis;
        this.id = requireNonNull(id);
    }

    public static SenderClient of(@NotNull TransmitterConfiguration configuration, int maxTryCount, boolean ucs2Only, long timeoutMillis, @NotNull MessageBuilder messageBuilder) {
        return of(configuration, maxTryCount, ucs2Only, timeoutMillis, messageBuilder, UUID.randomUUID().toString());
    }

    public static SenderClient of(@NotNull TransmitterConfiguration configuration, int maxTryCount, boolean ucs2Only, long timeoutMillis, @NotNull MessageBuilder messageBuilder, @NotNull String id) {
        return new DefaultSenderClient(configuration, maxTryCount, ucs2Only, timeoutMillis, messageBuilder, id);
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    /**
     * Setup connection to SMSC.
     *
     * @throws SenderClientBindException if connection fails
     * @see #checkSession()
     */
    public void setup() throws SenderClientBindException {
        try {
            checkSession();
        } catch (SmppSessionException e) {
            log.error(e.getErrorMessage(), e);
            throw new SenderClientBindException(format("Unable to bind with configuration: %s ", sessionConfig.configInformation()));
        }
    }

    /**
     * Отправка сообщения с отчетом о доставке
     *
     * @param message сообщение
     * @return результат сообщения
     */
    @NotNull
    @Override
    public MessageResponse send(@NotNull Message message) {

        if (message.getText() == null || message.getText().isEmpty()) {
            return MessageResponse.error(message, new MessageErrorInformation(0, "Empty message text"));
        }

        try {
            SubmitSm submitSm = messageBuilder.createSubmitSm(message, message.isSilent(), ucs2Only);
            SubmitSmResp send = send(submitSm);
            return analyzeResponse(message, send);
        } catch (SmppInvalidArgumentException e) {
            log.error(e.getMessage(), e);
            return MessageResponse.error(message, new MessageErrorInformation(INVALID_PARAM, "Invalid param"));
        } catch (IllegalAddressException e) {
            log.error(e.getMessage(), e);
            return MessageResponse.error(message, new MessageErrorInformation(INVALID_PARAM, e.getMessage()));
        } catch (SmppException e) {
            return MessageResponse.error(message, new MessageErrorInformation(e.getErrorCode(),
                    e.getErrorMessage()));
        }
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
    private SubmitSmResp send(SubmitSm sm) throws SmppException {

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
            return MessageResponse.success(message, submitSmResp.getMessageId());
        else
            return MessageResponse.error(message, new MessageErrorInformation(INVALID_PARAM,
                    submitSmResp.getResultMessage()));
    }


    /**
     * Checking smpp session state. If session is null - creating new session using method {@link #bind()}.
     * Otherwise checking bound session state by method {@link #checkBoundState()}.
     *
     * @throws SmppSessionException if session not connected
     */
    private void checkSession() throws SmppSessionException {
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
        if (!session.isBound()) {
            return isBindingOrReconnect();
        } else {
            return pingOrReconnect();
        }
    }

    /**
     * Check is smpp session in `binding` state. If no in `binding` state - calling
     * reconnect.
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
            log.info(ex.getMessage(), ex);
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
