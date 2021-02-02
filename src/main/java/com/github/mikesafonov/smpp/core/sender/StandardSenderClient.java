package com.github.mikesafonov.smpp.core.sender;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.*;
import com.cloudhopper.smpp.type.*;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.dto.*;
import com.github.mikesafonov.smpp.core.exceptions.IllegalAddressException;
import com.github.mikesafonov.smpp.core.exceptions.SenderClientBindException;
import com.github.mikesafonov.smpp.core.exceptions.SmppException;
import com.github.mikesafonov.smpp.core.exceptions.SmppSessionException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link SenderClient}, build on top of {@link DefaultSmppClient} and
 * {@link SmppSession}
 *
 * @author Mike Safonov
 * @author Mikhail Epatko
 */
@Slf4j
public class StandardSenderClient implements SenderClient {

    private static final int INVALID_PARAM = 101;
    private static final int INVALID_SENDING_ERROR = 102;

    /**
     * Request timeout in millis
     */
    private final long timeoutMillis;
    /**
     * Message builder
     */
    private final MessageBuilder messageBuilder;
    private final boolean ucs2Only;
    private final ConnectionManager connectionManager;
    private boolean inited = false;

    public StandardSenderClient(@NotNull ConnectionManager connectionManager,
                                boolean ucs2Only, long timeoutMillis,
                                @NotNull MessageBuilder messageBuilder) {
        this.messageBuilder = requireNonNull(messageBuilder);
        this.connectionManager = requireNonNull(connectionManager);
        this.ucs2Only = ucs2Only;
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public @NotNull String getId() {
        return connectionManager.getConfiguration().getName();
    }

    /**
     * Setup connection to SMSC
     *
     * @throws SenderClientBindException if connection fails
     * @see ConnectionManager#getSession()
     */
    public void setup(){
        if (!inited) {
            try {
                connectionManager.getSession();
            } catch (SmppSessionException e) {
                log.error(e.getErrorMessage(), e);
                throw new SenderClientBindException(format("Unable to bind with configuration: %s ",
                        connectionManager.getConfiguration().configInformation()));
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
            return MessageResponse.error(message, getId(), new MessageErrorInformation(0,
                    "Empty message text"));
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
            return CancelMessageResponse.error(cancelMessage, getId(),
                    new MessageErrorInformation(0, "Empty message id"));
        }

        try {
            CancelSm cancelSm = messageBuilder.createCancelSm(cancelMessage);
            WindowFuture<Integer, PduRequest, PduResponse> futureResponse =
                    connectionManager.getSession().sendRequestPdu(cancelSm, timeoutMillis, true);
            if (futureResponse.await() && futureResponse.isDone() && futureResponse.isSuccess()) {
                return createCancelMessageResponse(cancelMessage, futureResponse);
            }
            return CancelMessageResponse.error(cancelMessage, getId(),
                    new MessageErrorInformation(INVALID_PARAM, "Unable to get response"));
        } catch (RecoverablePduException | UnrecoverablePduException
                | SmppTimeoutException | SmppChannelException | InterruptedException e) {
            log.error(e.getMessage(), e);
            return CancelMessageResponse.error(cancelMessage, getId(),
                    new MessageErrorInformation(INVALID_PARAM, e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CancelMessageResponse.error(cancelMessage, getId(),
                    new MessageErrorInformation(INVALID_SENDING_ERROR, "Unexpected exception"));
        }
    }

    @Override
    public Optional<ConnectionManager> getConnectionManager() {
        return Optional.of(connectionManager);
    }

    @NotNull
    private CancelMessageResponse createCancelMessageResponse(@NotNull CancelMessage cancelMessage,
                                                              WindowFuture<Integer, PduRequest, PduResponse> futureResponse) {
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
     */
    private SubmitSmResp send(SubmitSm sm){
        try {
            return connectionManager.getSession().submit(sm, timeoutMillis);
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
}
